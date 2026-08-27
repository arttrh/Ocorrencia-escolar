package br.com.project_sena.integration;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import br.com.project_sena.adapter.out.persistence.entity.AlunoEntity;
import br.com.project_sena.adapter.out.persistence.entity.CategoriaOcorrenciaEntity;
import br.com.project_sena.adapter.out.persistence.entity.TipoOcorrenciaEntity;
import br.com.project_sena.adapter.out.persistence.entity.TurmaEntity;
import br.com.project_sena.adapter.out.persistence.entity.UsuarioEntity;
import br.com.project_sena.adapter.out.persistence.entity.VinculoEntity;
import br.com.project_sena.adapter.out.persistence.jpa.AlunoJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.CategoriaOcorrenciaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.OcorrenciaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.TipoOcorrenciaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.TurmaJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.UsuarioJpaRepository;
import br.com.project_sena.adapter.out.persistence.jpa.VinculoJpaRepository;
import br.com.project_sena.adapter.out.ratelimit.LoginRateLimiterAdapter;
import br.com.project_sena.application.core.domain.enums.AlunoEnum;
import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.SemestreEnum;
import br.com.project_sena.application.core.domain.enums.TurmaEnum;
import br.com.project_sena.application.core.domain.enums.TurmaTurnoEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Base dos testes de integracao: sobe o contexto completo (Spring MVC, seguranca,
 * JPA, use cases) e prepara uma massa de dados minima antes de cada cenario.
 *
 * <p>Cada teste comeca de um banco limpo, entao a ordem de execucao nao importa.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class TesteDeIntegracao {

    protected static final String SENHA_PADRAO = "senha123";

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected PasswordEncoder passwordEncoder;
    @Autowired protected LoginRateLimiterAdapter rateLimiter;

    @Autowired protected UsuarioJpaRepository usuarioRepository;
    @Autowired protected AlunoJpaRepository alunoRepository;
    @Autowired protected TurmaJpaRepository turmaRepository;
    @Autowired protected VinculoJpaRepository vinculoRepository;
    @Autowired protected OcorrenciaJpaRepository ocorrenciaRepository;
    @Autowired protected CategoriaOcorrenciaJpaRepository categoriaRepository;
    @Autowired protected TipoOcorrenciaJpaRepository tipoRepository;

    /**
     * Tokens ja emitidos neste cenario.
     *
     * <p>Sem o cache, um teste que chama {@code autorizacao()} varias vezes dispara
     * varios POST /login e esbarra no proprio rate limit — o limitador funcionando
     * como esperado, mas atrapalhando o teste.</p>
     */
    private final java.util.Map<PerfilEnum, String> tokensDoCenario =
            new java.util.EnumMap<>(PerfilEnum.class);

    protected TurmaEntity turma;
    protected AlunoEntity aluno;
    protected CategoriaOcorrenciaEntity categoria;
    protected TipoOcorrenciaEntity tipo;

    @BeforeEach
    void prepararMassaDeDados() {
        // O limitador guarda estado em memoria entre testes do mesmo contexto.
        rateLimiter.limpar();
        tokensDoCenario.clear();

        ocorrenciaRepository.deleteAll();
        vinculoRepository.deleteAll();
        tipoRepository.deleteAll();
        categoriaRepository.deleteAll();
        alunoRepository.deleteAll();
        turmaRepository.deleteAll();
        usuarioRepository.deleteAll();

        for (PerfilEnum perfil : PerfilEnum.values()) {
            usuarioRepository.save(new UsuarioEntity(
                    null,
                    "Usuario " + perfil.name(),
                    loginDe(perfil),
                    passwordEncoder.encode(SENHA_PADRAO),
                    perfil,
                    UsuarioEnum.ATIVO));
        }

        turma = turmaRepository.save(new TurmaEntity(
                null, "DS-01", TurmaTurnoEnum.MANHA, LocalDate.now().getYear(),
                SemestreEnum.PRIMEIRO, TurmaEnum.ATIVA));
        aluno = alunoRepository.save(new AlunoEntity(
                null, "Ana Souza", LocalDate.of(2008, 3, 27), null, AlunoEnum.ATIVO));
        vinculoRepository.save(new VinculoEntity(null, aluno, turma));

        categoria = categoriaRepository.save(
                new CategoriaOcorrenciaEntity(null, "DISCIPLINAR"));
        tipo = tipoRepository.save(
                new TipoOcorrenciaEntity(null, "INDISCIPLINA EM SALA", categoria));
    }

    protected static String loginDe(PerfilEnum perfil) {
        return perfil.name().toLowerCase() + "@escola.com";
    }

    /** Autentica pelo endpoint real e devolve o cabecalho Authorization pronto. */
    protected String autorizacao(PerfilEnum perfil) throws Exception {
        return "Bearer " + token(perfil);
    }

    protected String token(PerfilEnum perfil) throws Exception {
        String cacheado = tokensDoCenario.get(perfil);
        if (cacheado != null) {
            return cacheado;
        }
        String corpo = """
                {"login":"%s","password":"%s"}""".formatted(loginDe(perfil), SENHA_PADRAO);

        String resposta = mockMvc.perform(post("/login")
                        .contentType("application/json")
                        .content(corpo))
                .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(resposta);
        if (!json.has("tokenJWT")) {
            throw new IllegalStateException("Login falhou para " + perfil + ": " + resposta);
        }
        String token = json.get("tokenJWT").asText();
        tokensDoCenario.put(perfil, token);
        return token;
    }

    protected String json(Object valor) throws Exception {
        return objectMapper.writeValueAsString(valor);
    }
}

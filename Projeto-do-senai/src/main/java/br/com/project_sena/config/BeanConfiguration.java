package br.com.project_sena.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.project_sena.application.core.usecase.AlunoService;
import br.com.project_sena.application.core.usecase.AutenticacaoService;
import br.com.project_sena.application.core.usecase.OcorrenciaService;
import br.com.project_sena.application.core.usecase.TurmaService;
import br.com.project_sena.application.core.usecase.UsuarioService;
import br.com.project_sena.application.core.usecase.validacoes.ocorrencia.ValidadorOcorrencia;
import br.com.project_sena.application.core.usecase.validacoes.ocorrencia.ValidarAlunoAtivo;
import br.com.project_sena.application.core.usecase.validacoes.ocorrencia.ValidarAlunoPertenceATurma;
import br.com.project_sena.application.core.usecase.validacoes.ocorrencia.ValidarTurmaAtiva;
import br.com.project_sena.application.core.usecase.validacoes.vinculo.ValidadorVinculo;
import br.com.project_sena.application.core.usecase.validacoes.vinculo.ValidarAlunoAtivoParaVinculo;
import br.com.project_sena.application.core.usecase.validacoes.vinculo.ValidarAlunoSemOutraTurma;
import br.com.project_sena.application.core.usecase.validacoes.vinculo.ValidarTurmaComVaga;
import br.com.project_sena.application.port.in.AlunoUseCase;
import br.com.project_sena.application.port.in.AutenticarUsuarioUseCase;
import br.com.project_sena.application.port.in.OcorrenciaUseCase;
import br.com.project_sena.application.port.in.TurmaUseCase;
import br.com.project_sena.application.port.in.UsuarioUseCase;
import br.com.project_sena.application.port.out.AlunoRepository;
import br.com.project_sena.application.port.out.CategoriaOcorrenciaRepository;
import br.com.project_sena.application.port.out.CriptografiaPort;
import br.com.project_sena.application.port.out.EventoOcorrenciaPort;
import br.com.project_sena.application.port.out.OcorrenciaRepository;
import br.com.project_sena.application.port.out.RateLimiterPort;
import br.com.project_sena.application.port.out.TipoOcorrenciaRepository;
import br.com.project_sena.application.port.out.TokenPort;
import br.com.project_sena.application.port.out.TransacaoPort;
import br.com.project_sena.application.port.out.TurmaRepository;
import br.com.project_sena.application.port.out.UsuarioRepository;
import br.com.project_sena.application.port.out.VinculoRepository;

/**
 * Montagem do nucleo.
 *
 * <p>Os use cases e os validadores sao POJOs sem anotacao de framework — e' aqui, no
 * anel externo, que eles viram beans. Essa e' a inversao que a arquitetura hexagonal
 * pede: o Spring conhece a aplicacao, a aplicacao nao conhece o Spring.</p>
 *
 * <p>Antes, {@code @Service} e {@code @Transactional} estavam espalhados pelas classes de
 * {@code application.core}, e a ordem dos validadores dependia de qual bean o Spring
 * decidisse injetar. Declarados aqui, a composicao fica explicita e legivel.</p>
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public AutenticarUsuarioUseCase autenticarUsuarioUseCase(UsuarioRepository usuarioRepository,
                                                             CriptografiaPort criptografia,
                                                             TokenPort tokenPort,
                                                             RateLimiterPort rateLimiter) {
        return new AutenticacaoService(usuarioRepository, criptografia, tokenPort, rateLimiter);
    }

    @Bean
    public UsuarioUseCase usuarioUseCase(UsuarioRepository usuarioRepository,
                                         CriptografiaPort criptografia,
                                         TransacaoPort transacao) {
        return new UsuarioService(usuarioRepository, criptografia, transacao);
    }

    @Bean
    public AlunoUseCase alunoUseCase(AlunoRepository alunoRepository, TransacaoPort transacao) {
        return new AlunoService(alunoRepository, transacao);
    }

    /** Ordem intencional: existencia e situacao antes da regra mais cara (contar vagas). */
    @Bean
    public List<ValidadorVinculo> validadoresDeVinculo(VinculoRepository vinculoRepository) {
        return List.of(
                new ValidarAlunoAtivoParaVinculo(),
                new ValidarAlunoSemOutraTurma(vinculoRepository),
                new ValidarTurmaComVaga(vinculoRepository));
    }

    @Bean
    public TurmaUseCase turmaUseCase(TurmaRepository turmaRepository,
                                     AlunoRepository alunoRepository,
                                     VinculoRepository vinculoRepository,
                                     List<ValidadorVinculo> validadoresDeVinculo,
                                     TransacaoPort transacao) {
        return new TurmaService(
                turmaRepository, alunoRepository, vinculoRepository, validadoresDeVinculo, transacao);
    }

    /** Regras que toda ocorrencia precisa satisfazer, na ordem em que sao aplicadas. */
    @Bean
    public List<ValidadorOcorrencia> validadoresDeOcorrencia(VinculoRepository vinculoRepository) {
        return List.of(
                new ValidarAlunoAtivo(),
                new ValidarTurmaAtiva(),
                new ValidarAlunoPertenceATurma(vinculoRepository));
    }

    @Bean
    public OcorrenciaUseCase ocorrenciaUseCase(OcorrenciaRepository ocorrenciaRepository,
                                               TurmaRepository turmaRepository,
                                               AlunoRepository alunoRepository,
                                               CategoriaOcorrenciaRepository categoriaRepository,
                                               TipoOcorrenciaRepository tipoRepository,
                                               List<ValidadorOcorrencia> validadoresDeOcorrencia,
                                               EventoOcorrenciaPort eventos,
                                               TransacaoPort transacao) {
        return new OcorrenciaService(
                ocorrenciaRepository, turmaRepository, alunoRepository,
                categoriaRepository, tipoRepository, validadoresDeOcorrencia, eventos, transacao);
    }
}

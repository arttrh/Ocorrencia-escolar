package br.com.project_sena.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.exception.CredenciaisInvalidasException;
import br.com.project_sena.application.core.domain.exception.LimiteDeTentativasException;
import br.com.project_sena.application.core.domain.exception.UsuarioInativoException;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.port.in.AutenticarUsuarioUseCase;
import br.com.project_sena.application.port.in.command.CredenciaisCommand;
import br.com.project_sena.application.core.usecase.AutenticacaoService;
import br.com.project_sena.fakes.FakesEmMemoria;

@DisplayName("AutenticacaoService (caso de uso)")
class AutenticacaoServiceTest {

    private FakesEmMemoria.UsuarioRepositorioFake usuarios;
    private Usuario admin;

    @BeforeEach
    void preparar() {
        usuarios = new FakesEmMemoria.UsuarioRepositorioFake();
        Usuario novo = Usuario.novo("Admin", "admin@escola.com", null, PerfilEnum.ADMIN);
        novo.definirSenhaCodificada("cod:senha123");
        admin = usuarios.save(novo);
    }

    private AutenticacaoService comLimite(int tentativas) {
        return new AutenticacaoService(
                usuarios,
                FakesEmMemoria.criptografiaFalsa(),
                FakesEmMemoria.tokenFalso(),
                new FakesEmMemoria.RateLimiterContado(tentativas));
    }

    private AutenticacaoService semLimite() {
        return new AutenticacaoService(
                usuarios,
                FakesEmMemoria.criptografiaFalsa(),
                FakesEmMemoria.tokenFalso(),
                FakesEmMemoria.rateLimiterPermissivo());
    }

    @Test
    @DisplayName("credenciais corretas geram token com id, login e perfil")
    void autenticaComSucesso() {
        AutenticarUsuarioUseCase.Resultado resultado = semLimite().autenticar(
                new CredenciaisCommand("admin@escola.com", "senha123", "127.0.0.1"));

        assertNotNull(resultado.token());
        assertEquals(admin.getId(), resultado.usuarioId());
        assertEquals("admin@escola.com", resultado.login());
        assertEquals(PerfilEnum.ADMIN, resultado.perfil());
    }

    @Test
    @DisplayName("login e' tratado sem diferenciar maiusculas nem espacos")
    void loginNormalizado() {
        assertNotNull(semLimite().autenticar(
                new CredenciaisCommand("  ADMIN@Escola.com ", "senha123", "127.0.0.1")).token());
    }

    @Test
    @DisplayName("senha errada e usuario inexistente devolvem a mesma mensagem")
    void naoRevelaSeAContaExiste() {
        AutenticacaoService service = semLimite();

        CredenciaisInvalidasException senhaErrada = assertThrows(
                CredenciaisInvalidasException.class,
                () -> service.autenticar(
                        new CredenciaisCommand("admin@escola.com", "errada", "127.0.0.1")));
        CredenciaisInvalidasException usuarioInexistente = assertThrows(
                CredenciaisInvalidasException.class,
                () -> service.autenticar(
                        new CredenciaisCommand("ninguem@escola.com", "senha123", "127.0.0.1")));

        // Mensagens identicas evitam enumeracao de contas validas.
        assertEquals(senhaErrada.getMessage(), usuarioInexistente.getMessage());
    }

    @Test
    @DisplayName("usuario inativo nao recebe token")
    void usuarioInativoNaoEntra() {
        admin.inativar();
        usuarios.save(admin);

        assertThrows(UsuarioInativoException.class, () -> semLimite().autenticar(
                new CredenciaisCommand("admin@escola.com", "senha123", "127.0.0.1")));
    }

    @Test
    @DisplayName("bloqueia apos exceder o numero de tentativas")
    void bloqueiaAposLimite() {
        AutenticacaoService service = comLimite(3);
        CredenciaisCommand erradas =
                new CredenciaisCommand("admin@escola.com", "errada", "10.0.0.1");

        for (int i = 0; i < 3; i++) {
            assertThrows(CredenciaisInvalidasException.class, () -> service.autenticar(erradas));
        }

        LimiteDeTentativasException bloqueio = assertThrows(LimiteDeTentativasException.class,
                () -> service.autenticar(erradas));
        assertTrue(bloqueio.getSegundosParaLiberar() > 0);
    }

    @Test
    @DisplayName("o limite e' por origem e login, entao outro IP nao herda o bloqueio")
    void limitePorOrigemELogin() {
        AutenticacaoService service = comLimite(2);
        CredenciaisCommand deUmIp =
                new CredenciaisCommand("admin@escola.com", "errada", "10.0.0.1");

        assertThrows(CredenciaisInvalidasException.class, () -> service.autenticar(deUmIp));
        assertThrows(CredenciaisInvalidasException.class, () -> service.autenticar(deUmIp));
        assertThrows(LimiteDeTentativasException.class, () -> service.autenticar(deUmIp));

        // Outro IP continua com as proprias tentativas: o limite global anterior
        // deixaria o sistema inteiro sem login depois de cinco erros de uma pessoa.
        assertNotNull(service.autenticar(
                new CredenciaisCommand("admin@escola.com", "senha123", "10.0.0.2")).token());
    }

    @Test
    @DisplayName("o limite tambem separa logins diferentes no mesmo IP")
    void limiteSeparaLogins() {
        AutenticacaoService service = comLimite(1);
        String ip = "10.0.0.9";

        assertThrows(CredenciaisInvalidasException.class, () -> service.autenticar(
                new CredenciaisCommand("outro@escola.com", "errada", ip)));

        assertNotNull(service.autenticar(
                new CredenciaisCommand("admin@escola.com", "senha123", ip)).token());
    }
}

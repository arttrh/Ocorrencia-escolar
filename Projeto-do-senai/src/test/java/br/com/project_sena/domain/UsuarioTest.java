package br.com.project_sena.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;
import br.com.project_sena.application.core.domain.exception.SenhaException;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.domain.vo.PoliticaSenha;

@DisplayName("Usuario e politica de senha (dominio)")
class UsuarioTest {

    @Test
    @DisplayName("normaliza o login para minusculas")
    void normalizaLogin() {
        Usuario usuario = Usuario.novo("Ana", "  Ana.Souza@Escola.COM ", "hash", PerfilEnum.ADMIN);

        assertEquals("ana.souza@escola.com", usuario.getLogin());
    }

    @Test
    @DisplayName("nasce ativo")
    void nasceAtivo() {
        assertTrue(Usuario.novo("Ana", "ana@escola.com", "hash", PerfilEnum.ADMIN).isAtivo());
    }

    @Test
    @DisplayName("inativar e reativar alternam o estado")
    void inativaEReativa() {
        Usuario usuario = Usuario.novo("Ana", "ana@escola.com", "hash", PerfilEnum.ADMIN);

        usuario.inativar();
        assertFalse(usuario.isAtivo());

        usuario.reativar();
        assertTrue(usuario.isAtivo());
    }

    @Test
    @DisplayName("reativar um usuario ativo e' erro de negocio")
    void naoReativaAtivo() {
        Usuario usuario = Usuario.novo("Ana", "ana@escola.com", "hash", PerfilEnum.ADMIN);

        assertThrows(RegraDeNegocioException.class, usuario::reativar);
    }

    @Test
    @DisplayName("exige nome e login")
    void exigeNomeELogin() {
        assertThrows(RegraDeNegocioException.class,
                () -> Usuario.novo("  ", "ana@escola.com", "hash", PerfilEnum.ADMIN));
        assertThrows(RegraDeNegocioException.class,
                () -> Usuario.novo("Ana", "", "hash", PerfilEnum.ADMIN));
    }

    @Test
    @DisplayName("atualizacao parcial preserva o que nao foi enviado")
    void atualizacaoParcial() {
        Usuario usuario = new Usuario(
                1L, "Ana", "ana@escola.com", "hash", PerfilEnum.PROFESSOR, UsuarioEnum.ATIVO);

        usuario.atualizarDados("Ana Souza", null, null);

        assertEquals("Ana Souza", usuario.getName());
        assertEquals("ana@escola.com", usuario.getLogin());
        assertEquals(PerfilEnum.PROFESSOR, usuario.getPerfil());
    }

    @ParameterizedTest(name = "senha \"{0}\" e' recusada")
    @ValueSource(strings = {"", "   ", "12345"})
    @DisplayName("politica recusa senha curta ou vazia")
    void recusaSenhaCurta(String senha) {
        assertThrows(SenhaException.class, () -> PoliticaSenha.validar(senha));
    }

    @Test
    @DisplayName("politica recusa senha acima do limite do BCrypt")
    void recusaSenhaAcimaDoLimiteDoBcrypt() {
        // Acima de 72 bytes o BCrypt trunca em silencio: aceitar seria dar ao usuario
        // uma sensacao falsa de senha forte.
        String longa = "a".repeat(PoliticaSenha.TAMANHO_MAXIMO_BYTES + 1);

        assertThrows(SenhaException.class, () -> PoliticaSenha.validar(longa));
    }

    @Test
    @DisplayName("politica aceita senha valida")
    void aceitaSenhaValida() {
        assertEquals("senha123", PoliticaSenha.validar("senha123"));
    }
}

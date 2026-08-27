package br.com.project_sena.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.exception.EmailDuplicadoException;
import br.com.project_sena.application.core.domain.exception.SenhaException;
import br.com.project_sena.application.core.domain.exception.UsuarioNotFoundException;
import br.com.project_sena.application.core.domain.model.Usuario;
import br.com.project_sena.application.core.domain.vo.PaginaRequest;
import br.com.project_sena.application.core.usecase.UsuarioService;
import br.com.project_sena.application.port.in.command.AlterarSenhaCommand;
import br.com.project_sena.application.port.in.command.AtualizarUsuarioCommand;
import br.com.project_sena.application.port.in.command.CadastrarUsuarioCommand;
import br.com.project_sena.fakes.FakesEmMemoria;

@DisplayName("UsuarioService (caso de uso)")
class UsuarioServiceTest {

    private FakesEmMemoria.UsuarioRepositorioFake usuarios;
    private UsuarioService service;

    @BeforeEach
    void preparar() {
        usuarios = new FakesEmMemoria.UsuarioRepositorioFake();
        service = new UsuarioService(
                usuarios, FakesEmMemoria.criptografiaFalsa(), FakesEmMemoria.transacaoDireta());
    }

    private Usuario cadastrarAna() {
        return service.cadastrar(new CadastrarUsuarioCommand(
                "Ana", "ana@escola.com", "senha123", PerfilEnum.COORDENADOR));
    }

    @Test
    @DisplayName("a senha e' gravada codificada, nunca em texto puro")
    void gravaSenhaCodificada() {
        Usuario salvo = cadastrarAna();

        assertFalse("senha123".equals(salvo.getPassword()));
        assertEquals("cod:senha123", salvo.getPassword());
    }

    @Test
    @DisplayName("recusa login ja cadastrado")
    void recusaLoginDuplicado() {
        cadastrarAna();

        assertThrows(EmailDuplicadoException.class, () -> service.cadastrar(
                new CadastrarUsuarioCommand("Outra Ana", "ana@escola.com",
                        "senha456", PerfilEnum.PROFESSOR)));
    }

    @Test
    @DisplayName("recusa senha que nao cumpre a politica")
    void recusaSenhaFraca() {
        assertThrows(SenhaException.class, () -> service.cadastrar(
                new CadastrarUsuarioCommand("Ana", "ana@escola.com", "123", PerfilEnum.ADMIN)));
    }

    @Test
    @DisplayName("atualizar recusa login que ja pertence a outro usuario")
    void recusaLoginDeOutro() {
        cadastrarAna();
        Usuario bruno = service.cadastrar(new CadastrarUsuarioCommand(
                "Bruno", "bruno@escola.com", "senha123", PerfilEnum.PROFESSOR));

        assertThrows(EmailDuplicadoException.class, () -> service.atualizar(
                new AtualizarUsuarioCommand(bruno.getId(), null, "ana@escola.com", null)));
    }

    @Test
    @DisplayName("atualizar aceita manter o proprio login")
    void aceitaManterProprioLogin() {
        Usuario ana = cadastrarAna();

        Usuario atualizada = service.atualizar(new AtualizarUsuarioCommand(
                ana.getId(), "Ana Souza", "ana@escola.com", null));

        assertEquals("Ana Souza", atualizada.getName());
        assertEquals("ana@escola.com", atualizada.getLogin());
    }

    @Test
    @DisplayName("trocar a senha exige a senha atual correta")
    void exigeSenhaAtual() {
        Usuario ana = cadastrarAna();

        assertThrows(SenhaException.class, () -> service.alterarSenha(
                new AlterarSenhaCommand(ana.getId(), "errada", "novaSenha1")));

        service.alterarSenha(new AlterarSenhaCommand(ana.getId(), "senha123", "novaSenha1"));
        assertEquals("cod:novaSenha1", service.buscar(ana.getId()).getPassword());
    }

    @Test
    @DisplayName("a nova senha precisa ser diferente da atual")
    void novaSenhaDeveSerDiferente() {
        Usuario ana = cadastrarAna();

        assertThrows(SenhaException.class, () -> service.alterarSenha(
                new AlterarSenhaCommand(ana.getId(), "senha123", "senha123")));
    }

    @Test
    @DisplayName("inativar tira o usuario da listagem de ativos")
    void inativaEListagemReflete() {
        Usuario ana = cadastrarAna();

        service.inativar(ana.getId());

        assertEquals(0, service.listar(UsuarioEnum.ATIVO, PaginaRequest.padrao()).totalElementos());
        assertEquals(1, service.listar(UsuarioEnum.INATIVO, PaginaRequest.padrao()).totalElementos());
    }

    @Test
    @DisplayName("reativar devolve o usuario a listagem de ativos")
    void reativa() {
        Usuario ana = cadastrarAna();
        service.inativar(ana.getId());

        assertTrue(service.reativar(ana.getId()).isAtivo());
    }

    @Test
    @DisplayName("buscar id inexistente devolve erro de recurso nao encontrado")
    void buscaInexistente() {
        assertThrows(UsuarioNotFoundException.class, () -> service.buscar(404L));
    }
}

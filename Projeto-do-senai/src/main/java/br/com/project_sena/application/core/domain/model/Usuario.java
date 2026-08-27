package br.com.project_sena.application.core.domain.model;

import java.util.Objects;

import br.com.project_sena.application.core.domain.enums.PerfilEnum;
import br.com.project_sena.application.core.domain.enums.UsuarioEnum;
import br.com.project_sena.application.core.domain.exception.RegraDeNegocioException;

/**
 * Usuario do sistema.
 *
 * <p>POJO puro: nao implementa {@code UserDetails} nem conhece Spring Security. Quem
 * adapta este modelo para o principal do Spring e' {@code adapter.out.security.UsuarioPrincipal}.</p>
 */
public class Usuario {

    private Long id;
    private String name;
    private String login;
    private String password;
    private PerfilEnum perfil;
    private UsuarioEnum status;

    public Usuario(Long id, String name, String login, String password, PerfilEnum perfil, UsuarioEnum status) {
        this.id = id;
        this.name = exigirTexto(name, "Nome do usuario e' obrigatorio");
        this.login = normalizarLogin(login);
        this.password = password;
        this.perfil = Objects.requireNonNull(perfil, "Perfil do usuario e' obrigatorio");
        this.status = status == null ? UsuarioEnum.ATIVO : status;
    }

    /** Construtor de cadastro: id e status sao definidos pelo sistema. */
    public static Usuario novo(String name, String login, String password, PerfilEnum perfil) {
        return new Usuario(null, name, login, password, perfil, UsuarioEnum.ATIVO);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public PerfilEnum getPerfil() {
        return perfil;
    }

    public UsuarioEnum getStatus() {
        return status;
    }

    public boolean isAtivo() {
        return status.isAtivo();
    }

    /** Atualizacao parcial: campos nulos ou em branco preservam o valor atual. */
    public void atualizarDados(String name, String login, PerfilEnum perfil) {
        if (name != null && !name.isBlank()) {
            this.name = name.trim();
        }
        if (login != null && !login.isBlank()) {
            this.login = normalizarLogin(login);
        }
        if (perfil != null) {
            this.perfil = perfil;
        }
    }

    public void alterarPerfil(PerfilEnum perfil) {
        this.perfil = Objects.requireNonNull(perfil, "Perfil e' obrigatorio");
    }

    /** Recebe a senha ja codificada pelo adaptador de criptografia. */
    public void definirSenhaCodificada(String senhaCodificada) {
        this.password = exigirTexto(senhaCodificada, "Senha e' obrigatoria");
    }

    public void inativar() {
        this.status = UsuarioEnum.INATIVO;
    }

    public void reativar() {
        if (isAtivo()) {
            throw new RegraDeNegocioException("Usuario ja esta ativo");
        }
        this.status = UsuarioEnum.ATIVO;
    }

    private static String normalizarLogin(String login) {
        return exigirTexto(login, "Login do usuario e' obrigatorio").trim().toLowerCase();
    }

    private static String exigirTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException(mensagem);
        }
        return valor.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario outro)) {
            return false;
        }
        return id != null && Objects.equals(id, outro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

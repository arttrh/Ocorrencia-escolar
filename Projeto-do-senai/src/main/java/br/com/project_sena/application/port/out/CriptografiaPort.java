package br.com.project_sena.application.port.out;

/**
 * Hash e verificacao de senhas.
 *
 * <p>Substitui o uso direto de {@code PasswordEncoder} do Spring Security dentro dos
 * use cases, que acoplava o nucleo ao framework.</p>
 */
public interface CriptografiaPort {

    String codificar(String senhaEmTextoPuro);

    boolean confere(String senhaEmTextoPuro, String senhaCodificada);
}

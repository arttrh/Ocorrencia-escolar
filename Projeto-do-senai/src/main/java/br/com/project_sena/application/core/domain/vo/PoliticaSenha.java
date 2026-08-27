package br.com.project_sena.application.core.domain.vo;

import br.com.project_sena.application.core.domain.exception.SenhaException;

/**
 * Regras da senha em texto puro, aplicadas antes de codificar.
 *
 * <p>O limite maximo nao e' capricho: o BCrypt ignora silenciosamente tudo alem de 72
 * bytes, entao aceitar senhas maiores daria ao usuario uma falsa sensacao de forca.</p>
 */
public final class PoliticaSenha {

    public static final int TAMANHO_MINIMO = 6;
    public static final int TAMANHO_MAXIMO_BYTES = 72;

    private PoliticaSenha() {
    }

    public static String validar(String senha) {
        if (senha == null || senha.isBlank()) {
            throw new SenhaException("Senha e' obrigatoria");
        }
        if (senha.length() < TAMANHO_MINIMO) {
            throw new SenhaException("Senha deve ter ao menos " + TAMANHO_MINIMO + " caracteres");
        }
        if (senha.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > TAMANHO_MAXIMO_BYTES) {
            throw new SenhaException(
                    "Senha deve ter no maximo " + TAMANHO_MAXIMO_BYTES + " bytes");
        }
        return senha;
    }
}

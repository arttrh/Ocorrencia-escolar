package br.com.project_sena.application.port.in.command;

/**
 * @param login  identificador do usuario (e-mail de acesso)
 * @param senha  senha em texto puro, descartada logo apos a verificacao
 * @param origem identificador de quem tenta autenticar (IP), usado no rate limit
 */
public record CredenciaisCommand(String login, String senha, String origem) {
}

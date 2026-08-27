package br.com.project_sena.adapter.in.web.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Corpo padrao de erro.
 *
 * <p>Antes cada handler devolvia um tipo diferente — ora {@code String} pura, ora um
 * {@code Stream} de objetos, que o Jackson serializa como lista mas so' pode ser lido uma
 * vez. Um unico formato torna o tratamento de erro previsivel no front.</p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(LocalDateTime timestamp,
                           int status,
                           String error,
                           String message,
                           String path,
                           List<CampoInvalido> fields) {

    public record CampoInvalido(String field, String message) {
    }

    public static ErroResponse de(int status, String error, String message, String path) {
        return new ErroResponse(LocalDateTime.now(), status, error, message, path, null);
    }

    public static ErroResponse comCampos(int status, String error, String message,
                                         String path, List<CampoInvalido> fields) {
        return new ErroResponse(LocalDateTime.now(), status, error, message, path, fields);
    }
}

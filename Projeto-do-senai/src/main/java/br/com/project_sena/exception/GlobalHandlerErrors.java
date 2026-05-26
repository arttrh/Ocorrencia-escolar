package br.com.project_sena.exception;

import br.com.project_sena.exception.type.UsuarioNotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Classe Global de tratar Erros
@Configuration
@RestControllerAdvice
public class GlobalHandlerErrors {

    @ExceptionHandler(UsuarioNotFoundException.class)
    public String handleUsuarioNotFoundException(UsuarioNotFoundException ex) {
        return ex.getMessage();
    }


}

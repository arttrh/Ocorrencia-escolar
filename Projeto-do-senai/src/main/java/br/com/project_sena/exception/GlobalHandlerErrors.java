package br.com.project_sena.exception;

import br.com.project_sena.exception.type.UsuarioNotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Classe Global de tratar Erros
@Configuration
public class GlobalHandlerErrors {

    @ExceptionHandler(UsuarioNotFoundException.class)
    public String handleUsuarioNotFoundException(UsuarioNotFoundException ex) {
        return ex.getMessage();
    }


}

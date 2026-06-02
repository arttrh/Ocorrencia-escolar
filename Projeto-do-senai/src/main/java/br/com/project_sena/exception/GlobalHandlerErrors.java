package br.com.project_sena.exception;

import br.com.project_sena.exception.type.EmailException;
import br.com.project_sena.exception.type.SenhaException;
import br.com.project_sena.exception.type.TokenInvalidoException;
import br.com.project_sena.exception.type.UsuarioNotFoundException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Classe Global de tratar Erros
@Configuration
@RestControllerAdvice
public class GlobalHandlerErrors {

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<String> handleUsuarioNotFoundException(UsuarioNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SenhaException.class)
    public ResponseEntity<String> handlerSenha(SenhaException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<String> handlerEmail(EmailException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(TokenInvalidoException.class)
    public ResponseEntity<String> handlerTokenInvalido(TokenInvalidoException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

}

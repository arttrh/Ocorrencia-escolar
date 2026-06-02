package br.com.project_sena.adapter.in.controller;

import br.com.project_sena.adapter.in.controller.request.EmailDTO;
import br.com.project_sena.application.core.service.AutenticacaoService;
import br.com.project_sena.config.security.service.TokenDTO;
import br.com.project_sena.config.security.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final AutenticacaoService service;

    public LoginController(AutenticacaoService service, TokenService token) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TokenDTO> logar(@RequestBody EmailDTO dto) throws LoginException {
        TokenDTO usuario = service.logar(dto);
        return ResponseEntity.ok(usuario);
    }
}
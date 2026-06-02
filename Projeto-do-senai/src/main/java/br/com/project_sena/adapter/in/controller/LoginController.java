package br.com.project_sena.adapter.in.controller;

import br.com.project_sena.adapter.in.controller.request.EmailDTO;
import br.com.project_sena.application.core.service.AutenticacaoService;
import br.com.project_sena.config.security.rateLimit.RateLimitService;
import br.com.project_sena.config.security.service.TokenDTO;
import br.com.project_sena.config.security.service.TokenService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.LoginException;

@Slf4j
@RestController
@RequestMapping("/login")
public class LoginController {

    private final RateLimitService serviceRate;
    private final AutenticacaoService service;

    public LoginController(AutenticacaoService service, TokenService token, RateLimitService serviceRate) {
        this.service = service;
        this.serviceRate = serviceRate;
    }

    @PostMapping
    public ResponseEntity<TokenDTO> logar(
            @RequestBody EmailDTO dto,
            HttpServletRequest request)
            throws LoginException {
        String ip = request.getRemoteAddr(); //Endereco de ip para quem esta acessando
        log.info("ip: {}", ip);
        Bucket bucket = serviceRate.getBucket(ip);

        TokenDTO usuario = service.logar(dto);
        return ResponseEntity.ok(usuario);
    }
}
package br.com.project_sena.adapter.in.controller;

import br.com.project_sena.adapter.in.controller.request.email.EmailDTO;
import br.com.project_sena.application.core.service.AutenticacaoService;
import br.com.project_sena.config.security.rateLimit.RateLimitService;
import br.com.project_sena.config.security.service.TokenDTO;
import br.com.project_sena.config.security.service.TokenService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @CrossOrigin(origins = "http://127.0.0.1:5500")
    public ResponseEntity<TokenDTO> logar(
            @RequestBody @Valid EmailDTO dto,
            HttpServletRequest request)
            throws LoginException {
        String ip = request.getRemoteAddr(); //Endereco de ip para quem esta acessando
        log.info("ip: {}", ip);
        Bucket bucket = serviceRate.getBucket(ip);

        TokenDTO usuario = service.logar(dto);
        return ResponseEntity.ok(usuario);
    }
}
package br.com.project_sena.adapter.in.controller;

import br.com.project_sena.adapter.in.controller.request.LoginDTO;
import br.com.project_sena.application.core.service.AutenticacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final AutenticacaoService service;

    public LoginController(AutenticacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity logar(@RequestBody LoginDTO dto) {
        service.logar(dto);
        return ResponseEntity.ok().build();
    }
}

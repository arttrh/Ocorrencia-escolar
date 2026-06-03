package br.com.project_sena.adapter.in.controller;

import br.com.project_sena.adapter.in.controller.request.ClassRegisterDTO;
import br.com.project_sena.adapter.in.controller.response.ClassDetailsDTO;
import br.com.project_sena.adapter.in.web.mapper.TurmaMapperDTO;
import br.com.project_sena.application.core.domain.model.Turma;
import br.com.project_sena.application.core.usecase.TurmaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    private final TurmaService service;
    private final TurmaMapperDTO mapper;

    public TurmaController(TurmaService service, TurmaMapperDTO mapper){
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ClassDetailsDTO> cadastrar(@RequestBody @Valid ClassRegisterDTO dto){
        Turma domain = mapper.toDomain(dto);
        Turma salvo = service.cadastrar(domain, dto.studentId()); //Corrigir
        ClassDetailsDTO response = mapper.toDetails(salvo);
        return ResponseEntity.ok(response);
    }
}

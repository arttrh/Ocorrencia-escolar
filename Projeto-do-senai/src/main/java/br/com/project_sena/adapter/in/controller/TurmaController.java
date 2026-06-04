package br.com.project_sena.adapter.in.controller;

import br.com.project_sena.adapter.in.web.mapper.TurmaMapperDTO;
import br.com.project_sena.application.core.usecase.TurmaService;
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


}

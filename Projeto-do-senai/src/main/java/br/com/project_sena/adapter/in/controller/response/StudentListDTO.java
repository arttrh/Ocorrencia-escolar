package br.com.project_sena.adapter.in.controller.response;

import br.com.project_sena.application.core.domain.model.Aluno;

public record StudentListDTO(
        Long id,
        String photo,
        String name
) {
    public StudentListDTO(Aluno aluno){
        this(
                aluno.getId(),
                aluno.getPhoto(),
                aluno.getName()
        );
    }
}

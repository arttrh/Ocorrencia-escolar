package br.com.project_sena.application.core.domain.model;

import br.com.project_sena.application.core.domain.enums.AlunoEnum;

import java.time.LocalDate;

public class Aluno {
    private Long id;
    private String photo;
    private String name;
    private LocalDate dateBirth;

    //Enum
    private AlunoEnum alunoEnum;

    public Aluno() {
    }

    public Aluno(Long id, String photo, String name, LocalDate dateBirth, AlunoEnum alunoEnum) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.dateBirth = dateBirth;
        this.alunoEnum = alunoEnum;
    }

    public Long getId() {
        return id;
    }

    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDateBirth() {
        return dateBirth;
    }

    public AlunoEnum getAlunoEnum() {
        return alunoEnum;
    }
}

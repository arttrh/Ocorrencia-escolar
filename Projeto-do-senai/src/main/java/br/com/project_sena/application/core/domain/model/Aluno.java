package br.com.project_sena.application.core.domain.model;

import java.time.LocalDate;

public class Aluno {
    private Long id;
    private String photo;
    private String name;
    private LocalDate dateBirth;

    public Aluno() {
    }

    public Aluno(Long id, String photo, String name, LocalDate dateBirth) {
        this.id = id;
        this.photo = photo;
        this.name = name;
        this.dateBirth = dateBirth;
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
}

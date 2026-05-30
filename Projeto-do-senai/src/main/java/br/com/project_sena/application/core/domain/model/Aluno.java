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

    public void atualizarAluno(Aluno aluno){
        if(aluno.getPhoto() != null && !aluno.getPhoto().isBlank()){
            this.photo) = aluno.getPhoto();
        }
        if(aluno.getName() != null && !aluno.getName().isBlank()){
            this.name = aluno.getName();
        }
        if(aluno.getDateBirth() != null && !aluno.getDateBirth().isBlank()){
            this.dateBirth = aluno.getDateBirth();
        }
    }

    public void excluir(){
        this.AlunoEnum = AlunoEnum.INVATIVO;
    }

    public void reativar(){
        this.AlunoEnum = AlunoEnum.ATIVO;
    }
}

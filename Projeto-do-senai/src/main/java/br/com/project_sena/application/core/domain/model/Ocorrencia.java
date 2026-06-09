package br.com.project_sena.application.core.domain.model;

import br.com.project_sena.application.core.domain.enums.OcorrenciaEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class Ocorrencia {
    private Long id;
    private Turma turma;
    private Aluno student;
    private CategoriaOcorrencia category;
    private TipoOcorrencia occurrenceType;

    private LocalDate dataOcorrencia;
    private LocalTime time;
    private String descricaoDaOcorrencia;

    //Enums
    private OcorrenciaEnum ocorrenciaEnum;

    public Ocorrencia() {
    }

    public Ocorrencia(Long id,
                      Turma turma,
                      Aluno student,
                      CategoriaOcorrencia category,
                      TipoOcorrencia occurrenceType,
                      LocalDate dataOcorrencia,
                      LocalTime time,
                      String descricaoDaOcorrencia,
                      OcorrenciaEnum ocorrenciaEnum) {
        this.id = id;
        this.turma = turma;
        this.student = student;
        this.category = category;
        this.occurrenceType = occurrenceType;
        this.dataOcorrencia = dataOcorrencia;
        this.time = time;
        this.descricaoDaOcorrencia = descricaoDaOcorrencia;
        this.ocorrenciaEnum = ocorrenciaEnum;
    }

    public Long getId() {
        return id;
    }

    public Turma getTurma() {
        return turma;
    }

    public Aluno getStudent() {
        return student;
    }

    public CategoriaOcorrencia getCategory() {
        return category;
    }

    public TipoOcorrencia getOccurrenceType() {
        return occurrenceType;
    }

    public LocalDate getDataOcorrencia() {
        return dataOcorrencia;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getDescricaoDaOcorrencia() {
        return descricaoDaOcorrencia;
    }

    public OcorrenciaEnum getOcorrenciaEnum() {
        return ocorrenciaEnum;
    }

    public void setStudent(Aluno student) {
        this.student = student;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }
}

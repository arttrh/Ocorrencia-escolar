package br.com.project_sena.application.core.domain.model;

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

    public Ocorrencia() {
    }

    public Ocorrencia(Long id,
                      Turma turma,
                      Aluno student,
                      CategoriaOcorrencia category,
                      TipoOcorrencia occurrenceType,
                      LocalDate dataOcorrencia,
                      LocalTime time,
                      String descricaoDaOcorrencia) {
        this.id = id;
        this.turma = turma;
        this.student = student;
        this.category = category;
        this.occurrenceType = occurrenceType;
        this.dataOcorrencia = dataOcorrencia;
        this.time = time;
        this.descricaoDaOcorrencia = descricaoDaOcorrencia;
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
}

package br.com.project_sena.application.core.domain.model;

import br.com.project_sena.application.core.domain.enums.TurmaEnum;

public class Turma {

    private Long id;
    private String className;
    private String shift;
    private Integer classYear;

    //Enums
    private TurmaEnum turmaEnum;

    public Turma(Long id, String className, String shift, Integer classYear, TurmaEnum turmaEnum) {
        this.id = id;
        this.className = className;
        this.shift = shift;
        this.classYear = classYear;
        this.turmaEnum = turmaEnum;
    }

    public Turma() {
    }

    public Long getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public String getShift() {
        return shift;
    }

    public Integer getClassYear() {
        return classYear;
    }

    public TurmaEnum getTurmaEnum() {
        return turmaEnum;
    }
}

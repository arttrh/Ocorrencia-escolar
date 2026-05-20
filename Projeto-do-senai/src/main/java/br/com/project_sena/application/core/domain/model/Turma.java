package br.com.project_sena.application.core.domain.model;

public class Turma {

    private Long id;
    private String class_name;
    private String shift;
    private Integer classYear;

    public Turma(Long id, String class_name, String shift, Integer classYear) {
        this.id = id;
        this.class_name = class_name;
        this.shift = shift;
        this.classYear = classYear;
    }

    public Turma() {
    }

    public Long getId() {
        return id;
    }

    public String getClass_name() {
        return class_name;
    }

    public String getShift() {
        return shift;
    }

    public Integer getClassYear() {
        return classYear;
    }
}

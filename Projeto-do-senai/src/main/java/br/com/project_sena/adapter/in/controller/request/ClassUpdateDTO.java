package br.com.project_sena.adapter.in.controller.request;

public class ClassUpdateDTO {

    private String class_name;
    private String shift;
    private Integer classYear;

    public ClassUpdateDTO() {
    }

    public ClassUpdateDTO(String class_name, String shift, Integer classYear) {
        this.class_name = class_name;
        this.shift = shift;
        this.classYear = classYear;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public Integer getClassYear() {
        return classYear;
    }

    public void setClassYear(Integer classYear) {
        this.classYear = classYear;
    }
}

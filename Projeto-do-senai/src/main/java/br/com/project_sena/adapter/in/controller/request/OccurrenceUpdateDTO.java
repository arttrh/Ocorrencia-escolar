package br.com.project_sena.adapter.in.controller.request;

import java.time.LocalDate;
import java.time.LocalTime;

public class OccurrenceUpdateDTO {

    private Long studentId;
    private Long classId;
    private Long categoryId;
    private Long occurenceTypeId;
    private LocalDate date;
    private LocalTime time;
    private String descriptionOccurrence;

    public OccurrenceUpdateDTO() {
    }

    public OccurrenceUpdateDTO(Long studentId, Long classId, Long occurenceTypeId, Long categoryId, LocalTime time, LocalDate date, String descriptionOccurrence) {
        this.studentId = studentId;
        this.classId = classId;
        this.occurenceTypeId = occurenceTypeId;
        this.categoryId = categoryId;
        this.time = time;
        this.date = date;
        this.descriptionOccurrence = descriptionOccurrence;
    }

    public String getDescriptionOccurrence() {
        return descriptionOccurrence;
    }

    public void setDescriptionOccurrence(String descriptionOccurrence) {
        this.descriptionOccurrence = descriptionOccurrence;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getOccurenceTypeId() {
        return occurenceTypeId;
    }

    public void setOccurenceTypeId(Long occurenceTypeId) {
        this.occurenceTypeId = occurenceTypeId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
}

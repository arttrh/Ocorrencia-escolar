package br.com.project_sena.adapter.in.controller.request.turma;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VincularStudentAndClassDTO(@NotNull @Positive Long studentId, Long classId) {}

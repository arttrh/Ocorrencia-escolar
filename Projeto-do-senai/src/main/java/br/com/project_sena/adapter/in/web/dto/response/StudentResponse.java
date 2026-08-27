package br.com.project_sena.adapter.in.web.dto.response;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.project_sena.adapter.in.web.dto.request.Formatos;

public record StudentResponse(Long id,
                              String name,
                              @JsonFormat(pattern = Formatos.DATA) LocalDate birthDate,
                              String imageUrl,
                              boolean active) {
}

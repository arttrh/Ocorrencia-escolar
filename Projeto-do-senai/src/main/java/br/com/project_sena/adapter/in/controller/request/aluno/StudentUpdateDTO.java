package br.com.project_sena.adapter.in.controller.request.aluno;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record
    StudentUpdateDTO(@NotBlank @Size(max = 220000) @Pattern(
                         regexp = "^(?:/static/images/[a-zA-Z0-9._-]+|https://[^<>\\\"\\s]+|data:image/"
                                  + "(?:png|jpeg);base64,[A-Za-z0-9+/=]+)$") String photo,
        @NotBlank @Size(max = 100) String name,
        @NotNull @JsonFormat(pattern = "dd/MM/yyyy") @jakarta.validation.constraints.PastOrPresent LocalDate dateBirth) {}

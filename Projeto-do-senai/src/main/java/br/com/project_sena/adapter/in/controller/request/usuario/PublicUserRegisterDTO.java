package br.com.project_sena.adapter.in.controller.request.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicUserRegisterDTO(
    @NotBlank String name,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 72) String password) {}

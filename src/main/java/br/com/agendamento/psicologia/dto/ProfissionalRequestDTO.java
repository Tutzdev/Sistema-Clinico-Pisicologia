package br.com.agendamento.psicologia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfissionalRequestDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(
                max = 120,
                message = "O nome deve possuir no máximo 120 caracteres."
        )
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail informado é inválido.")
        @Size(
                max = 160,
                message = "O e-mail deve possuir no máximo 160 caracteres."
        )
        String email,

        @NotBlank(message = "O código da agenda é obrigatório.")
        @Size(
                min = 3,
                max = 80,
                message = "O código deve possuir entre 3 e 80 caracteres."
        )
        @Pattern(
                regexp = "^[a-zA-Z0-9-]+$",
                message = "O código deve conter apenas letras, números e hífen."
        )
        String codigoAgenda
) {
}
package br.com.agendamento.psicologia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PacienteRequestDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(
                max = 120,
                message = "O nome deve possuir no máximo 120 caracteres."
        )
        String nome,

        @NotBlank(message = "O telefone é obrigatório.")
        @Size(
                min = 10,
                max = 20,
                message = "O telefone deve possuir entre 10 e 20 caracteres."
        )
        String telefone,

        @Email(message = "O e-mail informado é inválido.")
        @Size(
                max = 160,
                message = "O e-mail deve possuir no máximo 160 caracteres."
        )
        String email
) {
}
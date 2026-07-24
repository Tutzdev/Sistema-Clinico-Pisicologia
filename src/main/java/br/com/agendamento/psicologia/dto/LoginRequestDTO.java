package br.com.agendamento.psicologia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Locale;

public record LoginRequestDTO(
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail informado é inválido.")
        @Size(
                max = 160,
                message = "O e-mail deve possuir no máximo 160 caracteres."
        )
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(
                min = 8,
                max = 72,
                message = "A senha deve possuir entre 8 e 72 caracteres."
        )
        String senha
) {

    public String emailNormalizado() {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

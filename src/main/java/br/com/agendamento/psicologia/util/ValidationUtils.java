package br.com.agendamento.psicologia.util;

import java.util.Locale;

public final class ValidationUtils {

    private ValidationUtils() {
        throw new IllegalStateException(
                "Esta classe utilitária não deve ser instanciada."
        );
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static String normalizeEmail(String email) {
        if (isBlank(email)) {
            return email;
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    public static String normalizeText(String value) {
        if (value == null) {
            return null;
        }

        return value.trim();
    }
}

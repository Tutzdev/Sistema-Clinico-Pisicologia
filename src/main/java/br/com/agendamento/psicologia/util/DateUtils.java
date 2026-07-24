package br.com.agendamento.psicologia.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private static final DateTimeFormatter FORMATO_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private DateUtils() {
    }

    public static String formatarDataHora(LocalDateTime dataHora) {
        return dataHora.format(FORMATO_DATA_HORA);
    }
}
package org.example.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss.SSS");

    public static String dateTimeFormatter(LocalDateTime data){
        return formatter.format(data);
    }
}

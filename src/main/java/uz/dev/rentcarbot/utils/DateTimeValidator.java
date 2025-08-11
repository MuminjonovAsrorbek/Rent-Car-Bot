package uz.dev.rentcarbot.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Created by: asrorbek
 * DateTime: 8/10/25 20:59
 **/

public class DateTimeValidator {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

    public static Optional<LocalDateTime> validDateTime(String input) {
        try {
            LocalDateTime parse = LocalDateTime.parse(input, FORMATTER);

            return Optional.of(parse);
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

}

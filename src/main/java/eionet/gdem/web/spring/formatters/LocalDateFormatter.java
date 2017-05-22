package eionet.gdem.web.spring.formatters;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 *
 *
 */
public class LocalDateFormatter implements Formatter<LocalDate> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate parse(String s, Locale locale) throws ParseException {
        return LocalDate.parse(s, formatter);
    }

    @Override
    public String print(LocalDate localDate, Locale locale) {
        return formatter.format(localDate);
    }
}

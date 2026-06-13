package com.unicesar.proyectogym.views.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JTextField;

public class DateField extends JTextField {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DateField() {
        super(12);
        setToolTipText("Formato: aaaa-mm-dd (ej. 2025-06-08)");
    }

    public void setDate(LocalDate date) {
        setText(date == null ? "" : date.format(FMT));
    }

    public LocalDate getDate() {
        String txt = getText() == null ? "" : getText().trim();
        if (txt.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(txt, FMT);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(
                    "Fecha inválida: \"" + txt + "\". Use el formato aaaa-mm-dd.");
        }
    }
}

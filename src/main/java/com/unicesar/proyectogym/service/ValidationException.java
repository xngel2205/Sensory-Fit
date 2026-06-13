package com.unicesar.proyectogym.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super(String.join("\n", errors));
        this.errors = new ArrayList<>(errors);
    }

    public ValidationException(String error) {
        this(Collections.singletonList(error));
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        for (String e : errors) {
            sb.append("• ").append(e).append('\n');
        }
        return sb.toString().trim();
    }
}

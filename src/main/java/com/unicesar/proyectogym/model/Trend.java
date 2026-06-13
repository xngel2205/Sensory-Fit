package com.unicesar.proyectogym.model;


public enum Trend {

    IMPROVEMENT("Tendencia de mejora"),
    DETERIORATION("Tendencia de deterioro"),
    STABLE("Tendencia estable"),
    NO_DATA("Datos insuficientes");

    private final String label;

    Trend(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}

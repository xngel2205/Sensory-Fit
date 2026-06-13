package com.unicesar.proyectogym.model;


public enum ImcClassification {

    UNDER_WEIGHT("Bajo peso", Double.NEGATIVE_INFINITY, 18.5),
    NORMAL_WEIGHT("Peso normal", 18.5, 25.0),
    OVERWEIGHT("Sobrepeso", 25.0, 30.0),
    OBESITY_I("Obesidad grado I", 30.0, 35.0),
    OBESITY_II("Obesidad grado II", 35.0, 40.0),
    OBESITY_III("Obesidad grado III", 40.0, Double.POSITIVE_INFINITY);

    private final String label;
    private final double min;
    private final double max;

    ImcClassification(String label, double min, double max) {
        this.label = label;
        this.min = min;
        this.max = max;
    }

    public String getLabel() {
        return label;
    }

    
    public static ImcClassification classify(double imc) {
        for (ImcClassification c : values()) {
            if (imc >= c.min && imc < c.max) {
                return c;
            }
        }
        return NORMAL_WEIGHT;
    }

    @Override
    public String toString() {
        return label;
    }
}

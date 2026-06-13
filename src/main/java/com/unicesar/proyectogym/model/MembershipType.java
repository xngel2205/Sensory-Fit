
package com.unicesar.proyectogym.model;


public enum MembershipType {

    DAILY("Diaria", 1),
    WEEKLY("Semanal", 7),
    BI_WEEKLY("Quincenal", 15),
    MONTHLY("Mensual", 30),
    QUARTERLY("Trimestral", 90),
    SEMI_ANNUALLY("Semestral", 180),
    ANNUALLY("Anual", 365);

    private final String label;
    private final int durationDays;

    MembershipType(String label, int durationDays) {
        this.label = label;
        this.durationDays = durationDays;
    }

    public String getLabel() {
        return label;
    }

    public int getDurationDays() {
        return durationDays;
    }

    @Override
    public String toString() {
        return label;
    }
}

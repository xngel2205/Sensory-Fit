
package com.unicesar.proyectogym.model;

public enum MembershipStatus {

    ACTIVE("Activa"),
    EXPIRED("Vencida"),
    SUSPENDED("Suspendida");

    private final String label;

    MembershipStatus(String label) {
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

package com.unicesar.proyectogym.service;

import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.model.MembershipStatus;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class MembershipService {

    public static final int DAYS_NOTICE_EXPIRATION = 5;

    public MembershipReport evaluate(Member member) {
        LocalDate today = LocalDate.now();
        LocalDate expiration = member.getDateExpiration();

        long daysRemaining = (expiration == null)
                ? 0
                : ChronoUnit.DAYS.between(today, expiration);
        if (member.getState() == MembershipStatus.SUSPENDED) {
            return new MembershipReport(member, MembershipStatus.SUSPENDED, daysRemaining,
                    "Membresía Suspendida. Contacte a administración.");
        }
        if (expiration == null || expiration.isBefore(today)) {
            return new MembershipReport(member, MembershipStatus.EXPIRED, daysRemaining,
                    "Membresía Vencida. Debe renovar el pago.");
        }
        if (daysRemaining < DAYS_NOTICE_EXPIRATION) {
            String detalle = (daysRemaining <= 0)
                    ? "Vence hoy."
                    : "Vence en " + daysRemaining + " día(s).";
            return new MembershipReport(member, MembershipStatus.ACTIVE, daysRemaining,
                    "Membresía próxima a vencer. " + detalle + " Puede ingresar.");
        }
        return new MembershipReport(member, MembershipStatus.ACTIVE, daysRemaining,
                "Membresía Activa. Puede ingresar.");
    }
}

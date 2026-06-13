package com.unicesar.proyectogym.model;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;
    private String identification;
    private String typeIdentification;
    private String names;
    private String surnames;
    private LocalDate dateBirth;
    private String phone;
    private String correo;
    private String address;
    private LocalDate dateRegistration;
    private MembershipType membershipType;
    private LocalDate dateExpiration;
    private MembershipStatus state;

    private byte[] fingerprintTemplate;
    private String fingerprintFormat;


    public String getFullName() {
        String n = names == null ? "" : names.trim();
        String a = surnames == null ? "" : surnames.trim();
        return (n + " " + a).trim();
    }

    public boolean hastFootprint() {
        return fingerprintTemplate != null && fingerprintTemplate.length > 0;
    }
}

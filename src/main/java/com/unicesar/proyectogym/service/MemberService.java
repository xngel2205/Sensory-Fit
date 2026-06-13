package com.unicesar.proyectogym.service;

import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.model.MembershipStatus;
import com.unicesar.proyectogym.model.MembershipType;
import com.unicesar.proyectogym.interfaces.MemberDao;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;


public class MemberService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?\\d{7,15}$");

    private static final Pattern ID_PATTERN =
            Pattern.compile("^\\d{5,20}$");

    private static final int NAME_MIN = 2;
    private static final int NAME_MAX = 50;
    private static final int ADDRESS_MIN = 5;
    private static final int ADDRESS_MAX = 120;
    private static final int AGE_MIN = 12;
    private static final int AGE_MAX = 100;

    private final MemberDao memberDao;

    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public Member register(Member member) {
        validate(member, true);

        if (member.getState() == null) {
            member.setState(computeStatus(member.getDateExpiration()));
        }
        memberDao.save(member);
        return member;
    }

   
    public Member update(Member member) {
        validate(member, false);
        memberDao.save(member);
        return member;
    }

   
    public void attachFingerprint(String identification, byte[] template, String format) {
        Member member = memberDao.findById(identification)
                .orElseThrow(() -> new ValidationException(
                        "No existe un usuario con identificación " + identification));
        member.setFingerprintTemplate(template);
        member.setFingerprintFormat(format);
        memberDao.save(member);
    }

    public Optional<Member> findById(String identification) {
        return memberDao.findById(identification);
    }

    public List<Member> findAll() {
        return memberDao.findAll();
    }

    public List<Member> findAllWithFingerprint() {
        List<Member> result = new ArrayList<>();
        for (Member m : memberDao.findAll()) {
            if (m.hastFootprint()) {
                result.add(m);
            }
        }
        return result;
    }

    public boolean deleteById(String identification) {
        if (identification == null || identification.trim().isEmpty()) {
            return false;
        }
        return memberDao.deleteById(identification.trim());
    }



    public LocalDate computeExpiration(LocalDate inscripcion, MembershipType type) {
        if (inscripcion == null || type == null) {
            return null;
        }
        return inscripcion.plusDays(type.getDurationDays());
    }

    public MembershipStatus computeStatus(LocalDate expiration) {
        if (expiration == null) {
            return MembershipStatus.EXPIRED;
        }
        return expiration.isBefore(LocalDate.now())
                ? MembershipStatus.EXPIRED
                : MembershipStatus.ACTIVE;
    }


    public void validate(Member m, boolean isNew) {
        List<String> errors = new ArrayList<>();

        if (m == null) {
            throw new ValidationException("No se recibió información del usuario.");
        }


        if (isBlank(m.getIdentification())) {
            errors.add("La identificación es obligatoria.");
        } else if (!ID_PATTERN.matcher(m.getIdentification().trim()).matches()) {
            errors.add("La identificación debe contener solo dígitos (entre 5 y 20).");
        } else if (isNew && memberDao.existsById(m.getIdentification().trim())) {
            errors.add("Ya existe un usuario registrado con la identificación "
                    + m.getIdentification().trim() + ".");
        }

        if (isBlank(m.getTypeIdentification())) {
            errors.add("Debe seleccionar el tipo de identificación.");
        }

        validateLength(errors, "Los nombres", m.getNames(), NAME_MIN, NAME_MAX, true);
        validateLength(errors, "Los apellidos", m.getSurnames(), NAME_MIN, NAME_MAX, true);

        if (m.getDateBirth() == null) {
            errors.add("La fecha de nacimiento es obligatoria.");
        } else {
            LocalDate today = LocalDate.now();
            if (m.getDateBirth().isAfter(today)) {
                errors.add("La fecha de nacimiento no puede ser futura.");
            } else {
                int age = java.time.Period.between(m.getDateBirth(), today).getYears();
                if (age < AGE_MIN) {
                    errors.add("El usuario debe tener al menos " + AGE_MIN + " años.");
                } else if (age > AGE_MAX) {
                    errors.add("La fecha de nacimiento no es válida (edad mayor a "
                            + AGE_MAX + " años).");
                }
            }
        }

        if (isBlank(m.getPhone())) {
            errors.add("El teléfono es obligatorio.");
        } else if (!PHONE_PATTERN.matcher(m.getPhone().trim()).matches()) {
            errors.add("El teléfono debe contener entre 7 y 15 dígitos "
                    + "(se admite el prefijo '+').");
        }

        if (isBlank(m.getCorreo())) {
            errors.add("El correo electrónico es obligatorio.");
        } else if (!EMAIL_PATTERN.matcher(m.getCorreo().trim()).matches()) {
            errors.add("El correo electrónico no tiene un formato válido.");
        }

        validateLength(errors, "La dirección", m.getAddress(), ADDRESS_MIN, ADDRESS_MAX, true);

        if (m.getDateRegistration() == null) {
            errors.add("La fecha de inscripción es obligatoria.");
        }
        if (m.getMembershipType() == null) {
            errors.add("Debe seleccionar el tipo de membresía.");
        }
        if (m.getDateExpiration() == null) {
            errors.add("La fecha de vencimiento es obligatoria.");
        }
        if (m.getDateRegistration() != null && m.getDateExpiration() != null
                && m.getDateExpiration().isBefore(m.getDateRegistration())) {
            errors.add("La fecha de vencimiento no puede ser anterior "
                    + "a la fecha de inscripción.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }


    private void validateLength(List<String> errors, String field, String worth,
                                int min, int max, boolean obligatory) {
        if (isBlank(worth)) {
            if (obligatory) {
                errors.add(field + " " + "es obligatorio(a).");
            }
            return;
        }
        int len = worth.trim().length();
        if (len < min) {
            errors.add(field + " debe tener al menos " + min + " caracteres.");
        } else if (len > max) {
            errors.add(field + " no puede superar los " + max + " caracteres.");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}

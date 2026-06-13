package com.unicesar.proyectogym.service.biometric;

import com.unicesar.proyectogym.model.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IdentificationResult {

    private final Member member;
    private final boolean matched;
    public static IdentificationResult noMatch() {
        return new IdentificationResult(null, false);
    }
    public static IdentificationResult match(Member member) {
        return new IdentificationResult(member, true);
    }
}

package com.unicesar.proyectogym.service;

import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.model.MembershipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MembershipReport {

    private final Member member;
    private final MembershipStatus effectiveState;
    private final long daysRemaining;
    private final String message;
    public boolean allowsEntry() {
        return effectiveState == MembershipStatus.ACTIVE;
    }
}

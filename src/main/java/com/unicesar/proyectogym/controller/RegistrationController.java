package com.unicesar.proyectogym.controller;

import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.model.MembershipType;
import com.unicesar.proyectogym.service.MemberService;
import java.time.LocalDate;


public class RegistrationController {

    private final MemberService memberService;

    public RegistrationController(MemberService memberService) {
        this.memberService = memberService;
    }

    public Member register(Member member) {
        return memberService.register(member);
    }

    
    public Member update(Member member) {
        return memberService.update(member);
    }

   
    public void saveFingerprint(String identificacion, byte[] template, String format) {
        memberService.attachFingerprint(identificacion, template, format);
    }

   
    public LocalDate suggestExpiration(LocalDate registration, MembershipType type) {
        return memberService.computeExpiration(registration, type);
    }
}

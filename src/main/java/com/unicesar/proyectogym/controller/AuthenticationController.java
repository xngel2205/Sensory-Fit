
package com.unicesar.proyectogym.controller;

import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.service.MemberService;
import com.unicesar.proyectogym.service.MembershipReport;
import com.unicesar.proyectogym.service.MembershipService;
import com.unicesar.proyectogym.service.biometric.BiometricException;
import com.unicesar.proyectogym.service.biometric.FingerprintCapture;
import com.unicesar.proyectogym.interfaces.FingerprintService;
import com.unicesar.proyectogym.service.biometric.IdentificationResult;


public class AuthenticationController {

    private final MemberService memberService;
    private final MembershipService membershipService;
    private final FingerprintService fingerprintService;

    public AuthenticationController(MemberService memberService,
                                    MembershipService membershipService,
                                    FingerprintService fingerprintService) {
        this.memberService = memberService;
        this.membershipService = membershipService;
        this.fingerprintService = fingerprintService;
    }

    public IdentificationResult captureAndIdentify(int timeoutMs) throws BiometricException {
        return fingerprintService.captureAndIdentify(
                timeoutMs,
                memberService.findAllWithFingerprint());
    }

    public FingerprintCapture capture(int timeoutMs) throws BiometricException {
        return fingerprintService.capture(timeoutMs);
    }

    @Deprecated
    public IdentificationResult identify(FingerprintCapture capture) throws BiometricException {
        return fingerprintService.identify(
                capture.getTemplate(),
                capture.getFormat(),
                memberService.findAllWithFingerprint());
    }

    
    public MembershipReport evaluate(Member member) {
        return membershipService.evaluate(member);
    }
}

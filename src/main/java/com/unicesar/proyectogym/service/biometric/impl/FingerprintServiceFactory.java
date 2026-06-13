package com.unicesar.proyectogym.service.biometric.impl;

import com.unicesar.proyectogym.interfaces.FingerprintService;

public final class FingerprintServiceFactory {

    private FingerprintServiceFactory() {
    }

    public static FingerprintService create() {
        return new DigitalPersonaFingerprintService();
    }
}

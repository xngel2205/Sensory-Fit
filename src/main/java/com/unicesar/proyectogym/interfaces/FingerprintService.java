package com.unicesar.proyectogym.interfaces;

import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.service.biometric.BiometricException;
import com.unicesar.proyectogym.service.biometric.FingerprintCapture;
import com.unicesar.proyectogym.service.biometric.IdentificationResult;
import java.util.List;

public interface FingerprintService extends AutoCloseable {

    void initialize() throws BiometricException;

    boolean isReady();

    String getReaderDescription();

    FingerprintCapture capture(int timeoutMs) throws BiometricException;

    boolean matches(byte[] templateA, String formatA,
                    byte[] templateB, String formatB) throws BiometricException;

    IdentificationResult identify(byte[] probe, String probeFormat,
                                  List<Member> candidates) throws BiometricException;
    IdentificationResult captureAndIdentify(int timeoutMs, List<Member> candidates)
            throws BiometricException;

    @Override
    void close();
}

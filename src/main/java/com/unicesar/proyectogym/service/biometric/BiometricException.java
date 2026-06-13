
package com.unicesar.proyectogym.service.biometric;

public class BiometricException extends Exception {

    private static final long serialVersionUID = 1L;

    public BiometricException(String message) {
        super(message);
    }

    public BiometricException(String message, Throwable cause) {
        super(message, cause);
    }
}

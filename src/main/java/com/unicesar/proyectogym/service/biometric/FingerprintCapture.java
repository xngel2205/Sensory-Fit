package com.unicesar.proyectogym.service.biometric;

import java.awt.image.BufferedImage;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FingerprintCapture {
    private final byte[] template;
    private final String format;
    private final int quality;
    private final BufferedImage image;
}

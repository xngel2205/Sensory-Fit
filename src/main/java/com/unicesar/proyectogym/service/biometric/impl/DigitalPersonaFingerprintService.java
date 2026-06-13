package com.unicesar.proyectogym.service.biometric.impl;

import com.unicesar.proyectogym.interfaces.FingerprintService;
import com.unicesar.proyectogym.service.biometric.BiometricException;
import com.unicesar.proyectogym.service.biometric.FingerprintCapture;
import com.unicesar.proyectogym.service.biometric.IdentificationResult;
import com.unicesar.proyectogym.model.Member;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPDataListener;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.processing.DPFPSampleConversion;
import com.digitalpersona.onetouch.processing.DPFPTemplateStatus;
import com.digitalpersona.onetouch.readers.DPFPReaderDescription;
import com.digitalpersona.onetouch.readers.DPFPReadersCollection;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DigitalPersonaFingerprintService implements FingerprintService {

    private static final Logger LOG =
            Logger.getLogger(DigitalPersonaFingerprintService.class.getName());

    private static final String FORMAT_NAME = "DPFP_ONETOUCH";
    private static final int FAR_REQUESTED = DPFPVerification.HIGH_SECURITY_FAR;
    private DPFPCapture capture;
    private String readerDescription = "Sin lector";
    private DPFPEnrollment activeEnrollment;

    @Override
    public void initialize() throws BiometricException {
        try {
            DPFPReadersCollection readers =
                    DPFPGlobal.getReadersFactory().getReaders();

            if (readers == null || readers.isEmpty()) {
                throw new BiometricException(
                        "No se detectó ningún lector de huellas. "
                        + "Conecte el U.are.U 4500 y verifique los drivers.");
            }

            DPFPReaderDescription desc = readers.get(0);
            String serial = desc.getSerialNumber();
            readerDescription = desc.getProductName()
                    + (serial != null && !serial.isBlank() ? " (" + serial + ")" : "");
            capture = DPFPGlobal.getCaptureFactory().createCapture();
            LOG.log(Level.INFO, "Lector biométrico inicializado: {0}", readerDescription);

        } catch (BiometricException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BiometricException(
                    "Error al inicializar el lector de huellas: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean isReady() {
        return capture != null;
    }

    @Override
    public String getReaderDescription() {
        return readerDescription;
    }

    @Override
    public void close() {
        try {
            if (capture != null && capture.isStarted()) {
                capture.stopCapture();
            }
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Error al cerrar el lector.", ex);
        } finally {
            capture = null;
            activeEnrollment = null;
        }
    }

    @Override
    public FingerprintCapture capture(int timeoutMs) throws BiometricException {
        if (!isReady()) {
            throw new BiometricException("El lector no está inicializado.");
        }

        if (activeEnrollment == null) {
            activeEnrollment = DPFPGlobal.getEnrollmentFactory().createEnrollment();
        }

        DPFPSample sample = waitForSample(timeoutMs);

        try {
            DPFPFeatureExtraction extractor =
                    DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
            DPFPFeatureSet features = extractor.createFeatureSet(
                    sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

            activeEnrollment.addFeatures(features);
            int needed = activeEnrollment.getFeaturesNeeded();

            if (activeEnrollment.getTemplateStatus() == DPFPTemplateStatus.TEMPLATE_STATUS_READY) {
                DPFPTemplate template = activeEnrollment.getTemplate();
                byte[] templateBytes = template.serialize();
                BufferedImage preview = toBufferedImage(sample);
                activeEnrollment = null;
                return new FingerprintCapture(templateBytes, FORMAT_NAME, 100, preview);
            } else if (activeEnrollment.getTemplateStatus() == DPFPTemplateStatus.TEMPLATE_STATUS_FAILED) {
                activeEnrollment = null;
                throw new BiometricException("La creación de la plantilla falló. Inténtelo de nuevo.");
            } else {
                throw new BiometricException(
                        "Huella leída (" + (4 - needed) + "/4). Vuelva a colocar el dedo.");
            }

        } catch (DPFPImageQualityException ex) {
            throw new BiometricException(
                    "Calidad de huella insuficiente. Por favor, vuelva a intentarlo.", ex);
        } catch (BiometricException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BiometricException(
                    "Error al procesar la imagen de la huella: " + ex.getMessage(), ex);
        }
    }

    @Override
    public IdentificationResult captureAndIdentify(int timeoutMs, List<Member> candidates)
            throws BiometricException {
        if (!isReady()) {
            throw new BiometricException("El lector no está inicializado.");
        }
        if (candidates == null || candidates.isEmpty()) {
            return IdentificationResult.noMatch();
        }

        DPFPSample sample = waitForSample(timeoutMs);

        try {
            DPFPFeatureExtraction extractor =
                    DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
            DPFPFeatureSet features = extractor.createFeatureSet(
                    sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

            return identifyWithFeatures(features, candidates);

        } catch (DPFPImageQualityException ex) {
            throw new BiometricException(
                    "Calidad de huella insuficiente. Por favor, vuelva a intentarlo.", ex);
        } catch (BiometricException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BiometricException(
                    "Error durante la captura/identificación: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean matches(byte[] templateA, String formatA,
                           byte[] templateB, String formatB) throws BiometricException {
        LOG.log(Level.WARNING, "matches() no soportado en el One Touch SDK sin muestra viva.");
        return false;
    }

    @Override
    public IdentificationResult identify(byte[] probe, String probeFormat,
                                         List<Member> candidates) throws BiometricException {
        LOG.log(Level.WARNING,
                "identify(byte[]) llamado; use captureAndIdentify() para autenticación.");
        return IdentificationResult.noMatch();
    }

    private DPFPSample waitForSample(int timeoutMs) throws BiometricException {
        SynchronousQueue<Object> queue = new SynchronousQueue<>();

        LOG.info("Iniciando waitForSample... Esperando huella.");

        DPFPDataListener listener = (DPFPDataEvent ev) -> {
            LOG.info("¡Muestra de huella adquirida!");
            try {
                queue.offer(ev.getSample(), 1, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        };

        com.digitalpersona.onetouch.capture.event.DPFPSensorListener sensorListener = 
            new com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter() {
                @Override
                public void fingerTouched(com.digitalpersona.onetouch.capture.event.DPFPSensorEvent ev) {
                    LOG.info("Sensor: Dedo colocado en el lector.");
                }
                @Override
                public void fingerGone(com.digitalpersona.onetouch.capture.event.DPFPSensorEvent ev) {
                    LOG.info("Sensor: Dedo retirado del lector.");
                }
            };

        com.digitalpersona.onetouch.capture.event.DPFPImageQualityListener qualityListener = 
            new com.digitalpersona.onetouch.capture.event.DPFPImageQualityAdapter() {
                @Override
                public void onImageQuality(com.digitalpersona.onetouch.capture.event.DPFPImageQualityEvent ev) {
                    LOG.info("Calidad de imagen detectada: " + ev.getFeedback());
                }
            };

        capture.addDataListener(listener);
        capture.addSensorListener(sensorListener);
        capture.addImageQualityListener(qualityListener);

        try {
            LOG.info("Llamando a capture.startCapture()...");
            capture.startCapture();
            Object received = queue.poll(timeoutMs, TimeUnit.MILLISECONDS);
            LOG.info("Llamando a capture.stopCapture()...");
            capture.stopCapture();
            
            capture.removeDataListener(listener);
            capture.removeSensorListener(sensorListener);
            capture.removeImageQualityListener(qualityListener);

            if (received == null) {
                throw new BiometricException(
                        "No se capturó ninguna huella (tiempo agotado).");
            }
            if (received instanceof BiometricException) {
                throw (BiometricException) received;
            }
            return (DPFPSample) received;

        } catch (BiometricException ex) {
            safeStopAll(listener, sensorListener, qualityListener);
            throw ex;
        } catch (InterruptedException ex) {
            safeStopAll(listener, sensorListener, qualityListener);
            Thread.currentThread().interrupt();
            throw new BiometricException("La captura fue interrumpida.", ex);
        } catch (Exception ex) {
            safeStopAll(listener, sensorListener, qualityListener);
            throw new BiometricException(
                    "Error durante la captura: " + ex.getMessage(), ex);
        }
    }

    private void safeStopAll(
            DPFPDataListener listener,
            com.digitalpersona.onetouch.capture.event.DPFPSensorListener sensorListener,
            com.digitalpersona.onetouch.capture.event.DPFPImageQualityListener qualityListener) {
        try {
            if (capture != null && capture.isStarted()) {
                capture.stopCapture();
            }
            if (capture != null) {
                if (listener != null) capture.removeDataListener(listener);
                if (sensorListener != null) capture.removeSensorListener(sensorListener);
                if (qualityListener != null) capture.removeImageQualityListener(qualityListener);
            }
        } catch (Exception ignored) {
        }
    }

    private IdentificationResult identifyWithFeatures(DPFPFeatureSet features,
                                                       List<Member> candidates)
            throws BiometricException {
        try {
            DPFPVerification verifier =
                    DPFPGlobal.getVerificationFactory().createVerification();
            verifier.setFARRequested(FAR_REQUESTED);

            for (Member m : candidates) {
                if (!m.hastFootprint()) {
                    continue;
                }
                try {
                    DPFPTemplate tmpl = DPFPGlobal.getTemplateFactory()
                            .createTemplate(m.getFingerprintTemplate());
                    DPFPVerificationResult result = verifier.verify(features, tmpl);
                    if (result.isVerified()) {
                        LOG.log(Level.INFO, "Huella identificada: {0}", m.getFullName());
                        return IdentificationResult.match(m);
                    }
                } catch (Exception ex) {
                    LOG.log(Level.WARNING,
                            "Error comparando con miembro " + m.getIdentification(), ex);
                }
            }
            return IdentificationResult.noMatch();

        } catch (Exception ex) {
            throw new BiometricException(
                    "Error durante la identificación: " + ex.getMessage(), ex);
        }
    }

    private BufferedImage toBufferedImage(DPFPSample sample) {
        try {
            DPFPSampleConversion conv = DPFPGlobal.getSampleConversionFactory();
            Image img = conv.createImage(sample);
            if (img instanceof BufferedImage) {
                return (BufferedImage) img;
            }
            BufferedImage buf = new BufferedImage(
                    img.getWidth(null), img.getHeight(null),
                    BufferedImage.TYPE_BYTE_GRAY);
            buf.getGraphics().drawImage(img, 0, 0, null);
            return buf;
        } catch (Exception ex) {
            LOG.log(Level.WARNING, "No se pudo generar la vista previa.", ex);
            return null;
        }
    }
}

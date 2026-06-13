package com.unicesar.proyectogym.views;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.RegistrationController;
import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.service.biometric.BiometricException;
import com.unicesar.proyectogym.service.biometric.FingerprintCapture;
import com.unicesar.proyectogym.interfaces.FingerprintService;
import com.unicesar.proyectogym.service.biometric.impl.FingerprintServiceFactory;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class CaptureFootprintFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final int CAPTURE_TIMEOUT_MS = 15000;

    private final Member member;
    private final RegistrationController controller;
    private final FingerprintService fingerprintService;
    private FingerprintCapture lastCapture;
    private final JLabel lblPreview = new JLabel();
    private final JLabel lblEstado = new JLabel(" ", SwingConstants.CENTER);
    private final JButton btnCapturar = new JButton("Capturar huella");
    private final JButton btnGuardar = new JButton("Guardar huella");
    private final JButton btnCancelar = new JButton("Cancelar");

    public CaptureFootprintFrame(Member member) {
        this.member = member;
        this.controller = AppContext.get().newRegistrationController();
        this.fingerprintService = FingerprintServiceFactory.create();
        initUI();
        initReader();
    }

    private void initUI() {
        setTitle("Captura de Huella - " + member.getFullName());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel encabezado = new JLabel(
                "<html><b>Usuario:</b> " + member.getFullName()
                + " &nbsp;&nbsp; <b>ID:</b> " + member.getIdentification() + "</html>",
                SwingConstants.CENTER);
        encabezado.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(encabezado, BorderLayout.NORTH);

        lblPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(320, 360));
        lblPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblPreview.setText("Coloque el dedo en el lector");
        add(lblPreview, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(5, 5));
        lblEstado.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        sur.add(lblEstado, BorderLayout.NORTH);

        JPanel botones = new JPanel();
        btnGuardar.setEnabled(false);
        botones.add(btnCancelar);
        botones.add(btnCapturar);
        botones.add(btnGuardar);
        sur.add(botones, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        btnCapturar.addActionListener(e -> doCapture());
        btnGuardar.addActionListener(e -> doSave());
        btnCancelar.addActionListener(e -> confirmClose());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmClose();
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void initReader() {
        btnCapturar.setEnabled(false);
        setEstado("Inicializando lector...", Color.DARK_GRAY);
        new SwingWorker<Void, Void>() {
            private Exception error;

            @Override
            protected Void doInBackground() {
                try {
                    fingerprintService.initialize();
                } catch (BiometricException ex) {
                    error = ex;
                }
                return null;
            }

            @Override
            protected void done() {
                if (error != null) {
                    setEstado("No se pudo inicializar el lector.", Color.RED);
                    JOptionPane.showMessageDialog(CaptureFootprintFrame.this,
                            error.getMessage(),
                            "Error del lector", JOptionPane.ERROR_MESSAGE);
                } else {
                    setEstado("Lector listo: "
                            + fingerprintService.getReaderDescription(), new Color(0, 128, 0));
                    btnCapturar.setEnabled(true);
                }
            }
        }.execute();
    }

    private void doCapture() {
        btnCapturar.setEnabled(false);
        btnGuardar.setEnabled(false);
        setEstado("Coloque el dedo en el lector...", Color.BLUE);
        lblPreview.setIcon(null);
        lblPreview.setText("Capturando...");

        new SwingWorker<FingerprintCapture, Void>() {
            private Exception error;

            @Override
            protected FingerprintCapture doInBackground() {
                try {
                    return fingerprintService.capture(CAPTURE_TIMEOUT_MS);
                } catch (BiometricException ex) {
                    error = ex;
                    return null;
                }
            }

            @Override
            protected void done() {
                btnCapturar.setEnabled(true);
                if (error != null || getResultSafe() == null) {
                    String msg = (error != null) ? error.getMessage()
                            : "No se obtuvo ninguna captura.";
                    setEstado(msg + " Puede reintentar.", Color.RED);
                    lblPreview.setText("Sin captura");
                    return;
                }
                lastCapture = getResultSafe();
                mostrarPreview(lastCapture);
                setEstado("Huella capturada (calidad: " + lastCapture.getQuality()
                        + "%). Revise y guarde, o reintente.", new Color(0, 128, 0));
                btnGuardar.setEnabled(true);
            }

            private FingerprintCapture getResultSafe() {
                try {
                    return get();
                } catch (Exception e) {
                    return null;
                }
            }
        }.execute();
    }

    private void mostrarPreview(FingerprintCapture cap) {
        if (cap.getImage() != null) {
            Image scaled = cap.getImage().getScaledInstance(
                    lblPreview.getWidth() > 0 ? lblPreview.getWidth() : 300,
                    -1, Image.SCALE_SMOOTH);
            lblPreview.setText(null);
            lblPreview.setIcon(new ImageIcon(scaled));
        } else {
            lblPreview.setIcon(null);
            lblPreview.setText("Huella capturada (sin vista previa)");
        }
    }

    private void doSave() {
        if (lastCapture == null) {
            return;
        }
        try {
            controller.saveFingerprint(member.getIdentification(),
                    lastCapture.getTemplate(), lastCapture.getFormat());
            JOptionPane.showMessageDialog(this,
                    "Huella asociada y guardada correctamente para "
                    + member.getFullName() + ".",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            closeAndRelease();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar la huella:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmClose() {
        if (lastCapture != null && btnGuardar.isEnabled()) {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Hay una huella capturada sin guardar. ¿Desea salir de todos modos?",
                    "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opt != JOptionPane.YES_OPTION) {
                return;
            }
        }
        closeAndRelease();
    }

    private void closeAndRelease() {
        try {
            fingerprintService.close();
        } catch (Exception ignored) {
        }
        dispose();
    }

    private void setEstado(String texto, Color color) {
        lblEstado.setText(texto);
        lblEstado.setForeground(color);
    }
}

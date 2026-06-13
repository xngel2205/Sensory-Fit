package com.unicesar.proyectogym.views.panels;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.AttendanceController;
import com.unicesar.proyectogym.controller.AuthenticationController;
import com.unicesar.proyectogym.controller.ProgresoController;
import com.unicesar.proyectogym.model.PhysicalEvaluation;
import com.unicesar.proyectogym.model.HistoryProgress;
import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.model.MembershipStatus;
import com.unicesar.proyectogym.model.PhysicalGoal;
import com.unicesar.proyectogym.service.MembershipReport;
import com.unicesar.proyectogym.service.biometric.BiometricException;
import com.unicesar.proyectogym.service.biometric.FingerprintCapture;
import com.unicesar.proyectogym.interfaces.FingerprintService;
import com.unicesar.proyectogym.service.biometric.impl.FingerprintServiceFactory;
import com.unicesar.proyectogym.service.biometric.IdentificationResult;
import com.unicesar.proyectogym.views.MainDashboard;
import com.unicesar.proyectogym.views.util.ModernComponents;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class IdentityValidationPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int CAPTURE_TIMEOUT_MS = 15000;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final AuthenticationController controller;
    private final ProgresoController progresoController;
    private final AttendanceController asistenciaController;
    private final FingerprintService fingerprintService;

    private final JButton btnIdentificar = new JButton("Identificar huella");
    private final JLabel lblEstado = new JLabel(" ", SwingConstants.CENTER);

    private final JLabel valNombre = new JLabel("-");
    private final JLabel valDocumento = new JLabel("-");
    private final JLabel valTipoMembresia = new JLabel("-");
    private final JLabel valInscripcion = new JLabel("-");
    private final JLabel valVencimiento = new JLabel("-");
    private final JLabel valEstadoMembresia = new JLabel("-");

    private final JLabel lblMensaje = new JLabel(" ", SwingConstants.CENTER);

    private final JLabel valUltimaEval = new JLabel("-");
    private final JLabel valPesoActual = new JLabel("-");
    private final JLabel valImcActual = new JLabel("-");
    private final JLabel valClasificacion = new JLabel("-");
    private final JLabel valMetaActiva = new JLabel("-");
    private final JLabel valAvanceMeta = new JLabel("-");
    private final JLabel valIngreso = new JLabel("-");

    public IdentityValidationPanel() {
        this.fingerprintService = FingerprintServiceFactory.create();
        this.controller = AppContext.get().newAuthenticationController(fingerprintService);
        this.progresoController = AppContext.get().newProgresoController();
        this.asistenciaController = AppContext.get().newAsistenciaController();
        initUI();
        initReader();
    }

    private void initUI() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("CONTROL DE ACCESO BIOMÉTRICO");
        ModernComponents.styleLabel(titulo, Font.BOLD, 24f, ModernComponents.CYAN_NEON);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(titulo, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        centerPanel.setOpaque(false);

        JPanel datosCard = new JPanel(new GridLayout(6, 2, 10, 10));
        ModernComponents.stylePanel(datosCard, true);
        datosCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(35, 50, 75)),
                        "Información de Membresía",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        ModernComponents.CYAN_NEON
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        addInfoRow(datosCard, "Nombre completo:", valNombre);
        addInfoRow(datosCard, "Documento:", valDocumento);
        addInfoRow(datosCard, "Tipo de membresía:", valTipoMembresia);
        addInfoRow(datosCard, "Fecha inscripción:", valInscripcion);
        addInfoRow(datosCard, "Fecha vencimiento:", valVencimiento);
        addInfoRow(datosCard, "Estado membresía:", valEstadoMembresia);

        JPanel progresoCard = new JPanel(new GridLayout(7, 2, 10, 10));
        ModernComponents.stylePanel(progresoCard, true);
        progresoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(35, 50, 75)),
                        "Progreso Físico y Acceso",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        ModernComponents.BLUE_NEON
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        addInfoRow(progresoCard, "Última evaluación:", valUltimaEval);
        addInfoRow(progresoCard, "Peso actual:", valPesoActual);
        addInfoRow(progresoCard, "IMC actual:", valImcActual);
        addInfoRow(progresoCard, "Clasificación:", valClasificacion);
        addInfoRow(progresoCard, "Meta activa:", valMetaActiva);
        addInfoRow(progresoCard, "Avance meta:", valAvanceMeta);
        addInfoRow(progresoCard, "Ingreso registrado:", valIngreso);

        centerPanel.add(datosCard);
        centerPanel.add(progresoCard);
        add(centerPanel, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(10, 10));
        sur.setOpaque(false);

        lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMensaje.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        lblMensaje.setOpaque(true);
        lblMensaje.setBackground(new Color(25, 35, 55));
        lblMensaje.setForeground(ModernComponents.TEXT_WHITE);
        sur.add(lblMensaje, BorderLayout.NORTH);

        ModernComponents.styleLabel(lblEstado, Font.ITALIC, 13f, ModernComponents.TEXT_GRAY);
        sur.add(lblEstado, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        botones.setOpaque(false);

        JButton btnCerrar = new JButton("Cerrar");
        ModernComponents.styleButton(btnCerrar, false);
        ModernComponents.styleButton(btnIdentificar, true);

        btnCerrar.setPreferredSize(new Dimension(140, 40));
        btnIdentificar.setPreferredSize(new Dimension(180, 40));

        botones.add(btnCerrar);
        botones.add(btnIdentificar);
        sur.add(botones, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        btnIdentificar.addActionListener(e -> doIdentify());
        btnCerrar.addActionListener(e -> closePanel());

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {}
            @Override
            public void ancestorRemoved(AncestorEvent event) {
                closeAndRelease();
            }
            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });
    }

    private void addInfoRow(JPanel panel, String labelText, JLabel valueLabel) {
        JLabel label = new JLabel(labelText);
        ModernComponents.styleLabel(label, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        panel.add(label);

        ModernComponents.styleLabel(valueLabel, Font.PLAIN, 13f, ModernComponents.TEXT_WHITE);
        panel.add(valueLabel);
    }

    private void initReader() {
        btnIdentificar.setEnabled(false);
        setState("Inicializando lector...", Color.LIGHT_GRAY);
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
                    setState("No se pudo inicializar el lector.", new Color(255, 100, 100));
                    JOptionPane.showMessageDialog(IdentityValidationPanel.this,
                            error.getMessage(), "Error del lector", JOptionPane.ERROR_MESSAGE);
                } else {
                    setState("Lector listo: " + fingerprintService.getReaderDescription(), ModernComponents.CYAN_NEON);
                    btnIdentificar.setEnabled(true);
                }
            }
        }.execute();
    }

    private void doIdentify() {
        btnIdentificar.setEnabled(false);
        cleanData();
        setState("Coloque el dedo en el lector...", ModernComponents.BLUE_NEON);
        lblMensaje.setText("Procesando...");
        lblMensaje.setBackground(new Color(25, 35, 55));
        lblMensaje.setForeground(ModernComponents.TEXT_WHITE);

        new SwingWorker<IdentificationResult, Void>() {
            private Exception error;

            @Override
            protected IdentificationResult doInBackground() {
                try {
                    return controller.captureAndIdentify(CAPTURE_TIMEOUT_MS);
                } catch (BiometricException ex) {
                    error = ex;
                    return null;
                }
            }


            @Override
            protected void done() {
                btnIdentificar.setEnabled(true);
                if (error != null) {
                    setState(error.getMessage() + " Puede reintentar.", new Color(255, 100, 100));
                    lblMensaje.setText("Error de captura");
                    lblMensaje.setBackground(new Color(120, 25, 25));
                    return;
                }
                IdentificationResult result = getResultSafe();
                if (result == null || !result.isMatched()) {
                    setState("Huella no reconocida.", new Color(255, 100, 100));
                    showMessage("Usuario no identificado", new Color(120, 25, 25));
                    return;
                }
                mostrarUsuario(result.getMember());
            }

            private IdentificationResult getResultSafe() {
                try {
                    return get();
                } catch (Exception e) {
                    return null;
                }
            }
        }.execute();
    }

    private void mostrarUsuario(Member m) {
        MembershipReport report = controller.evaluate(m);

        valNombre.setText(m.getFullName());
        valDocumento.setText((m.getTypeIdentification() == null ? "" : m.getTypeIdentification() + " ") + m.getIdentification());
        valTipoMembresia.setText(m.getMembershipType() == null ? "-" : m.getMembershipType().getLabel());
        valInscripcion.setText(m.getDateRegistration() == null ? "-" : m.getDateRegistration().format(FMT));
        valVencimiento.setText(m.getDateExpiration() == null ? "-" : m.getDateExpiration().format(FMT));
        valEstadoMembresia.setText(report.getEffectiveState().getLabel());

        setState("Usuario identificado correctamente.", ModernComponents.CYAN_NEON);

        Color color;
        if (report.getEffectiveState() == MembershipStatus.ACTIVE) {
            color = (report.getDaysRemaining() < com.unicesar.proyectogym.service.MembershipService.DAYS_NOTICE_EXPIRATION)
                    ? new Color(180, 105, 0)
                    : new Color(0, 120, 70);
        } else {
            color = new Color(120, 25, 25);
        }
        showMessage(report.getMessage(), color);

        HistoryProgress h = progresoController.getHistory(m.getIdentification());
        PhysicalEvaluation ultima = (h == null) ? null : h.getLast();
        if (ultima != null) {
            valUltimaEval.setText(ultima.getDate().format(FMT));
            valPesoActual.setText(ultima.getWeight() + " kg");
            valImcActual.setText(String.valueOf(ultima.getImc()));
            valClasificacion.setText(ultima.getClassification() == null ? "-" : ultima.getClassification().getLabel());
        } else {
            valUltimaEval.setText("Sin evaluaciones");
            valPesoActual.setText("-");
            valImcActual.setText("-");
            valClasificacion.setText("-");
        }
        if (h != null && h.getActiveGoal() != null) {
            PhysicalGoal meta = h.getActiveGoal();
            valMetaActiva.setText("Peso " + meta.getTargetWeight() + " kg, grasa " + meta.getTargetFat() + " %");
            valAvanceMeta.setText("Peso " + h.getAdvanceWeightPorc() + " % | Grasa " + h.getAdvanceFatPorc() + " %");
        } else {
            valMetaActiva.setText("Sin meta activa");
            valAvanceMeta.setText("-");
        }

        if (report.allowsEntry()) {
            asistenciaController.registerIncome(m);
            valIngreso.setText("Sí — " + LocalTime.now().withNano(0));
            valIngreso.setForeground(ModernComponents.CYAN_NEON);
        } else {
            valIngreso.setText("No (membresía no activa)");
            valIngreso.setForeground(new Color(255, 100, 100));
        }
    }

    private void showMessage(String text, Color fondo) {
        lblMensaje.setText(text);
        lblMensaje.setBackground(fondo);
        lblMensaje.setForeground(Color.WHITE);
    }

    private void cleanData() {
        valNombre.setText("-");
        valDocumento.setText("-");
        valTipoMembresia.setText("-");
        valInscripcion.setText("-");
        valVencimiento.setText("-");
        valEstadoMembresia.setText("-");
        valUltimaEval.setText("-");
        valPesoActual.setText("-");
        valImcActual.setText("-");
        valClasificacion.setText("-");
        valMetaActiva.setText("-");
        valAvanceMeta.setText("-");
        valIngreso.setText("-");
        valIngreso.setForeground(ModernComponents.TEXT_WHITE);
    }

    private void setState(String text, Color color) {
        lblEstado.setText(text);
        lblEstado.setForeground(color);
    }

    private void closeAndRelease() {
        try {
            fingerprintService.close();
        } catch (Exception ignored) {
        }
    }

    private void closePanel() {
        closeAndRelease();
        MainDashboard dash = (MainDashboard) SwingUtilities.getWindowAncestor(this);
        if (dash != null) {
            dash.showPanel(new WelcomePanel());
        }
    }
}

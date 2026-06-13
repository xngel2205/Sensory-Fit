package com.unicesar.proyectogym.views;

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
import com.unicesar.proyectogym.interfaces.FingerprintService;
import com.unicesar.proyectogym.service.biometric.impl.FingerprintServiceFactory;
import com.unicesar.proyectogym.service.biometric.IdentificationResult;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class IdentityValidationFrame extends JFrame {

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

    public IdentityValidationFrame() {
        this.fingerprintService = FingerprintServiceFactory.create();
        this.controller = AppContext.get().newAuthenticationController(fingerprintService);
        this.progresoController = AppContext.get().newProgresoController();
        this.asistenciaController = AppContext.get().newAsistenciaController();
        initUI();
        initReader();
    }

    private void initUI() {
        setTitle("Validación de Identidad");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("Control de Acceso Biométrico", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 12, 0, 12));
        add(titulo, BorderLayout.NORTH);
        JPanel datos = new JPanel(new GridLayout(6, 2, 8, 8));
        datos.setBorder(BorderFactory.createTitledBorder("Información del usuario"));
        datos.add(new JLabel("Nombre completo:"));     datos.add(valNombre);
        datos.add(new JLabel("Documento:"));            datos.add(valDocumento);
        datos.add(new JLabel("Tipo de membresía:"));    datos.add(valTipoMembresia);
        datos.add(new JLabel("Fecha de inscripción:")); datos.add(valInscripcion);
        datos.add(new JLabel("Fecha de vencimiento:")); datos.add(valVencimiento);
        datos.add(new JLabel("Estado actual:"));        datos.add(valEstadoMembresia);
        JPanel progreso = new JPanel(new GridLayout(0, 2, 8, 6));
        progreso.setBorder(BorderFactory.createTitledBorder("Progreso físico y asistencia"));
        progreso.add(new JLabel("Última evaluación:")); progreso.add(valUltimaEval);
        progreso.add(new JLabel("Peso actual:"));       progreso.add(valPesoActual);
        progreso.add(new JLabel("IMC actual:"));        progreso.add(valImcActual);
        progreso.add(new JLabel("Clasificación:"));     progreso.add(valClasificacion);
        progreso.add(new JLabel("Meta activa:"));       progreso.add(valMetaActiva);
        progreso.add(new JLabel("Avance meta:"));       progreso.add(valAvanceMeta);
        progreso.add(new JLabel("Ingreso registrado:")); progreso.add(valIngreso);
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(datos);
        center.add(progreso);
        add(center, BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout(6, 6));
        lblMensaje.setFont(lblMensaje.getFont().deriveFont(Font.BOLD, 16f));
        lblMensaje.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        lblMensaje.setOpaque(true);
        lblMensaje.setBackground(Color.LIGHT_GRAY);
        sur.add(lblMensaje, BorderLayout.NORTH);

        lblEstado.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        sur.add(lblEstado, BorderLayout.CENTER);

        JPanel botones = new JPanel();
        JButton btnCerrar = new JButton("Cerrar");
        botones.add(btnCerrar);
        botones.add(btnIdentificar);
        sur.add(botones, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        btnIdentificar.addActionListener(e -> doIdentify());
        btnCerrar.addActionListener(e -> closeAndRelease());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAndRelease();
            }
        });

        setSize(480, 620);
        setLocationRelativeTo(null);
    }

    private void initReader() {
        btnIdentificar.setEnabled(false);
        setState("Inicializando lector...", Color.DARK_GRAY);
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
                    setState("No se pudo inicializar el lector.", Color.RED);
                    JOptionPane.showMessageDialog(IdentityValidationFrame.this,
                            error.getMessage(), "Error del lector",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    setState("Lector listo: "
                            + fingerprintService.getReaderDescription(), new Color(0, 128, 0));
                    btnIdentificar.setEnabled(true);
                }
            }
        }.execute();
    }

    private void doIdentify() {
        btnIdentificar.setEnabled(false);
        cleanData();
        setState("Coloque el dedo en el lector...", Color.BLUE);
        lblMensaje.setText("Procesando...");
        lblMensaje.setBackground(Color.LIGHT_GRAY);

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
                    setState(error.getMessage() + " Puede reintentar.", Color.RED);
                    lblMensaje.setText("Error de captura");
                    return;
                }
                IdentificationResult result = getResultSafe();
                if (result == null || !result.isMatched()) {
                    setState("Huella no reconocida.", Color.RED);
                    showMessage("Usuario no identificado", Color.RED);
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
        valDocumento.setText((m.getTypeIdentification() == null ? "" : m.getTypeIdentification() + " ")
                + m.getIdentification());
        valTipoMembresia.setText(m.getMembershipType() == null ? "-" : m.getMembershipType().getLabel());
        valInscripcion.setText(m.getDateRegistration() == null ? "-" : m.getDateRegistration().format(FMT));
        valVencimiento.setText(m.getDateExpiration() == null ? "-" : m.getDateExpiration().format(FMT));
        valEstadoMembresia.setText(report.getEffectiveState().getLabel());

        setState("Usuario identificado correctamente.", new Color(0, 128, 0));

        Color color;
        if (report.getEffectiveState() == MembershipStatus.ACTIVE) {
            color = (report.getDaysRemaining() < com.unicesar.proyectogym.service.MembershipService.DAYS_NOTICE_EXPIRATION)
                    ? new Color(230, 145, 0)
                    : new Color(0, 150, 0);
        } else {
            color = new Color(200, 0, 0);
        }
        showMessage(report.getMessage(), color);

        HistoryProgress h = progresoController.getHistory(m.getIdentification());
        PhysicalEvaluation ultima = (h == null) ? null : h.getLast();
        if (ultima != null) {
            valUltimaEval.setText(ultima.getDate().format(FMT));
            valPesoActual.setText(ultima.getWeight() + " kg");
            valImcActual.setText(String.valueOf(ultima.getImc()));
            valClasificacion.setText(ultima.getClassification() == null
                    ? "-" : ultima.getClassification().getLabel());
        } else {
            valUltimaEval.setText("Sin evaluaciones");
            valPesoActual.setText("-");
            valImcActual.setText("-");
            valClasificacion.setText("-");
        }
        if (h != null && h.getActiveGoal() != null) {
            PhysicalGoal meta = h.getActiveGoal();
            valMetaActiva.setText("Peso " + meta.getTargetWeight()
                    + " kg, grasa " + meta.getTargetFat() + " %");
            valAvanceMeta.setText("Peso " + h.getAdvanceWeightPorc()
                    + " % | Grasa " + h.getAdvanceFatPorc() + " %");
        } else {
            valMetaActiva.setText("Sin meta activa");
            valAvanceMeta.setText("-");
        }

        if (report.allowsEntry()) {
            asistenciaController.registerIncome(m);
            valIngreso.setText("Sí — " + LocalTime.now().withNano(0));
            valIngreso.setForeground(new Color(0, 130, 0));
        } else {
            valIngreso.setText("No (membresía no activa)");
            valIngreso.setForeground(Color.RED);
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
        valIngreso.setForeground(java.awt.Color.BLACK);
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
        dispose();
    }
}

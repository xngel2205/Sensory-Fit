package com.unicesar.proyectogym.views.panels;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.RegistrationController;
import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.model.MembershipStatus;
import com.unicesar.proyectogym.model.MembershipType;
import com.unicesar.proyectogym.service.ValidationException;
import com.unicesar.proyectogym.views.CaptureFootprintFrame;
import com.unicesar.proyectogym.views.MainDashboard;
import com.unicesar.proyectogym.views.util.DateField;
import com.unicesar.proyectogym.views.util.ModernComponents;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;

public class RegisterUserPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final RegistrationController controller;
    private final Member editing;
    private Runnable onSaved;

    private final JComboBox<String> cmbTipoId = new JComboBox<>(new String[]{"Cédula Ciudadanía", "Pasaporte", "TI", "Cédula Extranjería"});
    private final JTextField txtIdentificacion = new JTextField(20);
    private final JTextField txtNombres = new JTextField(20);
    private final JTextField txtApellidos = new JTextField(20);
    private final DateField txtNacimiento = new DateField();
    private final JTextField txtTelefono = new JTextField(20);
    private final JTextField txtCorreo = new JTextField(20);
    private final JTextField txtDireccion = new JTextField(20);
    private final DateField txtInscripcion = new DateField();
    private final JComboBox<MembershipType> cmbMembresia = new JComboBox<>(MembershipType.values());
    private final DateField txtVencimiento = new DateField();
    private final JComboBox<MembershipStatus> cmbEstado = new JComboBox<>(MembershipStatus.values());

    public RegisterUserPanel() {
        this(null);
    }

    public RegisterUserPanel(Member toEdit) {
        this.controller = AppContext.get().newRegistrationController();
        this.editing = toEdit;
        initUI();
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    private void initUI() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        ModernComponents.stylePanel(card, true);
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 12, 6, 12);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        JLabel titulo = new JLabel(editing == null ? "REGISTRO DE USUARIO" : "EDITAR USUARIO");
        ModernComponents.styleLabel(titulo, Font.BOLD, 22f, ModernComponents.CYAN_NEON);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0; c.gridy = row++; c.gridwidth = 2;
        card.add(titulo, c);
        c.gridwidth = 1;

        addRow(card, c, row++, "Tipo de identificación:", cmbTipoId);
        addRow(card, c, row++, "Identificación:", txtIdentificacion);
        addRow(card, c, row++, "Nombres:", txtNombres);
        addRow(card, c, row++, "Apellidos:", txtApellidos);
        addRow(card, c, row++, "Fecha de nacimiento (aaaa-mm-dd):", txtNacimiento);
        addRow(card, c, row++, "Teléfono:", txtTelefono);
        addRow(card, c, row++, "Correo electrónico:", txtCorreo);
        addRow(card, c, row++, "Dirección:", txtDireccion);
        addRow(card, c, row++, "Fecha de inscripción:", txtInscripcion);
        addRow(card, c, row++, "Tipo de membresía:", cmbMembresia);
        addRow(card, c, row++, "Fecha de vencimiento:", txtVencimiento);
        addRow(card, c, row++, "Estado de membresía:", cmbEstado);

        cmbMembresia.addActionListener(e -> recalculateExpiration());
        txtInscripcion.addActionListener(e -> recalculateExpiration());

        if (editing == null) {
            txtInscripcion.setDate(LocalDate.now());
            cmbEstado.setSelectedItem(MembershipStatus.ACTIVE);
            recalculateExpiration();
        } else {
            prefillForm(editing);
            txtIdentificacion.setEditable(false);
            cmbTipoId.setEnabled(false);
        }

        JButton btnGuardar = new JButton(editing == null ? "Guardar y capturar huella" : "Guardar cambios");
        JButton btnCancelar = new JButton("Cancelar");
        ModernComponents.styleButton(btnGuardar, true);
        ModernComponents.styleButton(btnCancelar, false);

        btnGuardar.addActionListener(e -> onGuardar());
        btnCancelar.addActionListener(e -> goBack());

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        buttons.add(btnCancelar);
        buttons.add(btnGuardar);

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        card.add(buttons, c);

        JScrollPane scroll = new JScrollPane(card);
        ModernComponents.styleScrollPane(scroll);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        GridBagConstraints outerGbc = new GridBagConstraints();
        outerGbc.gridx = 0;
        outerGbc.gridy = 0;
        outerGbc.weightx = 1.0;
        outerGbc.weighty = 1.0;
        outerGbc.fill = GridBagConstraints.BOTH;
        outerGbc.insets = new Insets(15, 15, 15, 15);
        add(scroll, outerGbc);
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String labelText, Component field) {
        JLabel label = new JLabel(labelText);
        ModernComponents.styleLabel(label, Font.BOLD, 13f, ModernComponents.TEXT_GRAY);
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        panel.add(label, c);

        c.gridx = 1; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        if (field instanceof JTextField) {
            ModernComponents.styleTextField((JTextField) field);
        } else if (field instanceof JComboBox) {
            ModernComponents.styleComboBox((JComboBox<?>) field);
        }
        panel.add(field, c);
    }

    private void recalculateExpiration() {
        try {
            LocalDate inscripcion = txtInscripcion.getDate();
            MembershipType tipo = (MembershipType) cmbMembresia.getSelectedItem();
            LocalDate venc = controller.suggestExpiration(inscripcion, tipo);
            if (venc != null) {
                txtVencimiento.setDate(venc);
            }
        } catch (IllegalArgumentException ex) {
        }
    }

    private void onGuardar() {
        Member member;
        try {
            member = buildMemberFromForm();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (editing == null) {
                controller.register(member);
            } else {
                controller.update(member);
            }
        } catch (ValidationException ex) {
            showErrors(ex);
            return;
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el usuario:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (onSaved != null) {
            onSaved.run();
        }

        if (editing != null) {
            JOptionPane.showMessageDialog(this, "Cambios guardados correctamente.", "Edición exitosa", JOptionPane.INFORMATION_MESSAGE);
            goBack();
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this,
                "Usuario registrado correctamente.\n¿Desea capturar la huella ahora?",
                "Registro exitoso", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        goBack();

        if (opt == JOptionPane.YES_OPTION) {
            CaptureFootprintFrame capture = new CaptureFootprintFrame(member);
            capture.setVisible(true);
        }
    }

    private void prefillForm(Member m) {
        if (m.getTypeIdentification() != null) {
            cmbTipoId.setSelectedItem(m.getTypeIdentification());
        }
        txtIdentificacion.setText(m.getIdentification());
        txtNombres.setText(m.getNames());
        txtApellidos.setText(m.getSurnames());
        txtNacimiento.setDate(m.getDateBirth());
        txtTelefono.setText(m.getPhone());
        txtCorreo.setText(m.getCorreo());
        txtDireccion.setText(m.getAddress());
        txtInscripcion.setDate(m.getDateRegistration());
        if (m.getMembershipType() != null) {
            cmbMembresia.setSelectedItem(m.getMembershipType());
        }
        if (m.getState() != null) {
            cmbEstado.setSelectedItem(m.getState());
        }
        txtVencimiento.setDate(m.getDateExpiration());
    }

    private Member buildMemberFromForm() {
        Member m = Member.builder()
                .typeIdentification((String) cmbTipoId.getSelectedItem())
                .identification(txtIdentificacion.getText().trim())
                .names(txtNombres.getText().trim())
                .surnames(txtApellidos.getText().trim())
                .dateBirth(txtNacimiento.getDate())
                .phone(txtTelefono.getText().trim())
                .correo(txtCorreo.getText().trim())
                .address(txtDireccion.getText().trim())
                .dateRegistration(txtInscripcion.getDate())
                .membershipType((MembershipType) cmbMembresia.getSelectedItem())
                .dateExpiration(txtVencimiento.getDate())
                .state((MembershipStatus) cmbEstado.getSelectedItem())
                .build();

        if (editing != null) {
            m.setFingerprintTemplate(editing.getFingerprintTemplate());
            m.setFingerprintFormat(editing.getFingerprintFormat());
        }
        return m;
    }

    private void showErrors(ValidationException ex) {
        JTextArea area = new JTextArea(ex.getFormattedMessage());
        area.setEditable(false);
        area.setOpaque(false);
        ModernComponents.styleTextArea(area);
        JOptionPane.showMessageDialog(this, area, "Por favor corrija los siguientes datos", JOptionPane.WARNING_MESSAGE);
    }

    private void goBack() {
        MainDashboard dash = (MainDashboard) SwingUtilities.getWindowAncestor(this);
        if (dash != null) {
            dash.showPanel(new UserListPanel());
        }
    }
}

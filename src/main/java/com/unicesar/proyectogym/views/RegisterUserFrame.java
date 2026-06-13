package com.unicesar.proyectogym.views;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.RegistrationController;
import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.model.MembershipStatus;
import com.unicesar.proyectogym.model.MembershipType;
import com.unicesar.proyectogym.service.ValidationException;
import com.unicesar.proyectogym.views.util.DateField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class RegisterUserFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private final RegistrationController controller;
    private final Member editing;
    private Runnable onSaved;
    private final JComboBox<String> cmbTipoId =
            new JComboBox<>(new String[]{"Cédula Ciudadanía", "Pasaporte", "TI", "Cédula Extranjería"});
    private final JTextField txtIdentificacion = new JTextField(20);
    private final JTextField txtNombres = new JTextField(20);
    private final JTextField txtApellidos = new JTextField(20);
    private final DateField txtNacimiento = new DateField();
    private final JTextField txtTelefono = new JTextField(20);
    private final JTextField txtCorreo = new JTextField(20);
    private final JTextField txtDireccion = new JTextField(20);
    private final DateField txtInscripcion = new DateField();
    private final JComboBox<MembershipType> cmbMembresia =
            new JComboBox<>(MembershipType.values());
    private final DateField txtVencimiento = new DateField();
    private final JComboBox<MembershipStatus> cmbEstado =
            new JComboBox<>(MembershipStatus.values());

    public RegisterUserFrame() {
        this(null);
    }
    public RegisterUserFrame(Member toEdit) {
        this.controller = AppContext.get().newRegistrationController();
        this.editing = toEdit;
        initUI();
    }

    public void setOnSaved(Runnable onSaved) {
        this.onSaved = onSaved;
    }

    private void initUI() {
        setTitle(editing == null ? "Registro de Usuarios" : "Editar Usuario");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        JLabel titulo = new JLabel(editing == null ? "Registro de Usuarios" : "Editar Usuario");
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 20f));
        c.gridx = 0; c.gridy = row++; c.gridwidth = 2;
        form.add(titulo, c);
        c.gridwidth = 1;

        addRow(form, c, row++, "Tipo de identificación:", cmbTipoId);
        addRow(form, c, row++, "Identificación:", txtIdentificacion);
        addRow(form, c, row++, "Nombres:", txtNombres);
        addRow(form, c, row++, "Apellidos:", txtApellidos);
        addRow(form, c, row++, "Fecha de nacimiento:", txtNacimiento);
        addRow(form, c, row++, "Teléfono:", txtTelefono);
        addRow(form, c, row++, "Correo electrónico:", txtCorreo);
        addRow(form, c, row++, "Dirección:", txtDireccion);
        addRow(form, c, row++, "Fecha de inscripción:", txtInscripcion);
        addRow(form, c, row++, "Tipo de membresía:", cmbMembresia);
        addRow(form, c, row++, "Fecha de vencimiento:", txtVencimiento);
        addRow(form, c, row++, "Estado de membresía:", cmbEstado);

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
        JButton btnGuardar = new JButton(
                editing == null ? "Guardar y capturar huella" : "Guardar cambios");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.addActionListener(e -> onGuardar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel buttons = new JPanel();
        buttons.add(btnCancelar);
        buttons.add(btnGuardar);

        c.gridx = 0; c.gridy = row; c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        form.add(buttons, c);

        setContentPane(new JScrollPane(form));
        pack();
        setLocationRelativeTo(null);
    }
    private void addRow(JPanel panel, GridBagConstraints c, int row,
                        String label, java.awt.Component field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
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
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this,
                    "No se pudo guardar el usuario:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (onSaved != null) {
            onSaved.run();
        }

        if (editing != null) {
            JOptionPane.showMessageDialog(this,
                    "Cambios guardados correctamente.",
                    "Edición exitosa", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this,
                "Usuario registrado correctamente.\n¿Desea capturar la huella ahora?",
                "Registro exitoso", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        dispose();
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
        JOptionPane.showMessageDialog(this, area,
                "Por favor corrija los siguientes datos",
                JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterUserFrame().setVisible(true));
    }
}

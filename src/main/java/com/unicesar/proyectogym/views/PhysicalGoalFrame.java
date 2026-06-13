package com.unicesar.proyectogym.views;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.controller.ProgresoController;
import com.unicesar.proyectogym.model.HistoryProgress;
import com.unicesar.proyectogym.model.PhysicalGoal;
import com.unicesar.proyectogym.service.ValidationException;
import com.unicesar.proyectogym.views.util.DateField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class PhysicalGoalFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final ProgresoController controller;

    private final JTextField txtDocumento = new JTextField(14);
    private final JLabel lblNombre = new JLabel("—");
    private final JTextField txtPeso = new JTextField(8);
    private final JTextField txtGrasa = new JTextField(8);
    private final DateField txtFecha = new DateField();
    private final JTextArea txtObs = new JTextArea(3, 18);
    private final JLabel lblAvance = new JLabel(" ");

    public PhysicalGoalFrame() {
        this.controller = AppContext.get().newProgresoController();
        initUI();
    }

    public PhysicalGoalFrame(String documento) {
        this();
        txtDocumento.setText(documento);
        buscar();
    }

    private void initUI() {
        setTitle("Metas Físicas");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 6, 5, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        JLabel titulo = new JLabel("Metas Físicas");
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 20f));
        c.gridx = 0; c.gridy = row++; c.gridwidth = 3;
        form.add(titulo, c);
        c.gridwidth = 1;
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Documento:"), c);
        c.gridx = 1; form.add(txtDocumento, c);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscar());
        c.gridx = 2; form.add(btnBuscar, c);
        row++;
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Usuario:"), c);
        c.gridx = 1; c.gridwidth = 2; form.add(lblNombre, c);
        c.gridwidth = 1; row++;
        addRow(form, c, row++, "Peso objetivo (kg):", txtPeso);
        addRow(form, c, row++, "Grasa objetivo (%):", txtGrasa);
        addRow(form, c, row++, "Fecha estimada:", txtFecha);
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Observaciones:"), c);
        c.gridx = 1; c.gridwidth = 2;
        txtObs.setLineWrap(true);
        txtObs.setWrapStyleWord(true);
        form.add(new JScrollPane(txtObs), c);
        c.gridwidth = 1; row++;

        c.gridx = 0; c.gridy = row; c.gridwidth = 3;
        lblAvance.setFont(lblAvance.getFont().deriveFont(java.awt.Font.BOLD));
        form.add(lblAvance, c);
        c.gridwidth = 1; row++;

        JButton btnGuardar = new JButton("Guardar meta");
        JButton btnAvance = new JButton("Ver avance");
        JButton btnCerrar = new JButton("Cerrar");
        btnGuardar.addActionListener(e -> guardar());
        btnAvance.addActionListener(e -> verAvance());
        btnCerrar.addActionListener(e -> dispose());

        JPanel botones = new JPanel();
        botones.add(btnCerrar);
        botones.add(btnAvance);
        botones.add(btnGuardar);
        c.gridx = 0; c.gridy = row; c.gridwidth = 3;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        form.add(botones, c);

        setContentPane(new JScrollPane(form));
        pack();
        setLocationRelativeTo(null);
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String label,
                        java.awt.Component field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, c);
    }

    private String doc() {
        return txtDocumento.getText().trim();
    }

    private void buscar() {
        if (doc().isEmpty()) {
            return;
        }
        var m = controller.searchUser(doc());
        if (m.isPresent()) {
            lblNombre.setText(m.get().getFullName());
            lblNombre.setForeground(new java.awt.Color(0, 130, 0));
            Optional<PhysicalGoal> meta = controller.activeGoal(doc());
            if (meta.isPresent()) {
                PhysicalGoal mt = meta.get();
                txtPeso.setText(String.valueOf(mt.getTargetWeight()));
                txtGrasa.setText(String.valueOf(mt.getTargetFat()));
                txtFecha.setDate(mt.getEstimatedDate());
                txtObs.setText(mt.getObservations());
            }
            verAvance();
        } else {
            lblNombre.setText("No existe un usuario con ese documento");
            lblNombre.setForeground(java.awt.Color.RED);
        }
    }

    private void guardar() {
        try {
            PhysicalGoal meta = PhysicalGoal.builder()
                    .documentUser(doc())
                    .targetWeight(parse(txtPeso.getText(), "Peso objetivo"))
                    .targetFat(parse(txtGrasa.getText(), "Grasa objetivo"))
                    .estimatedDate(txtFecha.getDate())
                    .observations(txtObs.getText().trim())
                    .build();
            if (controller.searchUser(doc()).isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No existe un usuario con ese documento.",
                        "Usuario no encontrado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            controller.registerGoal(meta);
            JOptionPane.showMessageDialog(this, "Meta guardada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            verAvance();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Dato inválido", JOptionPane.WARNING_MESSAGE);
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getFormattedMessage(),
                    "Corrija los siguientes datos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void verAvance() {
        if (doc().isEmpty()) {
            return;
        }
        HistoryProgress h = controller.getHistory(doc());
        if (h == null || h.getActiveGoal() == null) {
            lblAvance.setText("Sin meta activa.");
            return;
        }
        lblAvance.setText("Avance → Peso: " + h.getAdvanceWeightPorc()
                + " %   |   Grasa: " + h.getAdvanceFatPorc() + " %");
    }

    private double parse(String s, String campo) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo \"" + campo + "\" es obligatorio.");
        }
        try {
            return Double.parseDouble(s.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo \"" + campo + "\" debe ser numérico.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PhysicalGoalFrame().setVisible(true));
    }
}

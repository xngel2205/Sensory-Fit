package com.unicesar.proyectogym.views;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.model.MembershipStatus;
import com.unicesar.proyectogym.service.MemberService;
import com.unicesar.proyectogym.service.MembershipService;
import com.unicesar.proyectogym.views.util.MemberTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

public class UserListFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private final MemberService memberService;
    private final MembershipService membershipService;
    private final MemberTableModel tableModel;
    private final JTable tabla;
    private final JLabel lblTotal = new JLabel(" ");

    public UserListFrame() {
        this.memberService = AppContext.get().getMemberService();
        this.membershipService = AppContext.get().getMembershipService();
        this.tableModel = new MemberTableModel(membershipService);
        this.tabla = new JTable(tableModel);
        initUI();
        loadData();
    }

    private void initUI() {
        setTitle("Usuarios Registrados");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JLabel titulo = new JLabel("Usuarios Registrados", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(titulo, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(24);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowSorter(new TableRowSorter<>(tableModel));
        tabla.getColumnModel().getColumn(5).setCellRenderer(new StateRenderer());

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() != -1) {
                    editSelected();
                }
            }
        });

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnNuevo = new JButton("Nuevo usuario");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnCerrar = new JButton("Cerrar");

        btnNuevo.addActionListener(e -> newUser());
        btnEditar.addActionListener(e -> editSelected());
        btnEliminar.addActionListener(e -> deleteSelected());
        btnRefrescar.addActionListener(e -> loadData());
        btnCerrar.addActionListener(e -> dispose());

        JPanel actions = new JPanel();
        actions.add(lblTotal);
        actions.add(btnNuevo);
        actions.add(btnEditar);
        actions.add(btnEliminar);
        actions.add(btnRefrescar);
        actions.add(btnCerrar);

        JLabel ayuda = new JLabel("Doble clic sobre un usuario para editarlo.");
        ayuda.setBorder(BorderFactory.createEmptyBorder(0, 12, 4, 0));

        JPanel sur = new JPanel(new BorderLayout());
        sur.add(ayuda, BorderLayout.WEST);
        sur.add(actions, BorderLayout.EAST);
        add(sur, BorderLayout.SOUTH);

        setSize(820, 460);
        setLocationRelativeTo(null);
     }

    private void loadData() {
        tableModel.setData(memberService.findAll());
        lblTotal.setText("Total: " + tableModel.getRowCount() + "   ");
    }

    private void newUser() {
        RegisterUserFrame frame = new RegisterUserFrame();
        frame.setOnSaved(this::loadData);
        frame.setVisible(true);
    }

    private void editSelected() {
        int viewRow = tabla.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un usuario de la tabla.",
                    "Edición", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = tabla.convertRowIndexToModel(viewRow);
        Member seleccionado = tableModel.getMemberAt(modelRow);

        RegisterUserFrame frame = new RegisterUserFrame(seleccionado);
        frame.setOnSaved(this::loadData);
        frame.setVisible(true);
    }

    private void deleteSelected() {
        int viewRow = tabla.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un usuario de la tabla para eliminar.",
                    "Eliminar usuario", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = tabla.convertRowIndexToModel(viewRow);
        Member seleccionado = tableModel.getMemberAt(modelRow);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar al usuario " + seleccionado.getNames() + " " + seleccionado.getSurnames() + "?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = memberService.deleteById(seleccionado.getIdentification());
            if (deleted) {
                JOptionPane.showMessageDialog(this,
                        "Usuario eliminado correctamente.",
                        "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se pudo eliminar el usuario.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private class StateRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component comp = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);

            int modelRow = table.convertRowIndexToModel(row);
            Member m = tableModel.getMemberAt(modelRow);
            var report = membershipService.evaluate(m);

            Color fg;
            if (report.getEffectiveState() == MembershipStatus.ACTIVE) {
                fg = (report.getDaysRemaining() < MembershipService.DAYS_NOTICE_EXPIRATION)
                        ? new Color(200, 120, 0)
                        : new Color(0, 140, 0);
            } else {
                fg = new Color(200, 0, 0);
            }
            comp.setForeground(isSelected ? Color.WHITE : fg);
            return comp;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserListFrame().setVisible(true));
    }
}

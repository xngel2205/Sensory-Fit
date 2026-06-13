package com.unicesar.proyectogym.views.panels;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.model.MembershipStatus;
import com.unicesar.proyectogym.service.MemberService;
import com.unicesar.proyectogym.service.MembershipService;
import com.unicesar.proyectogym.views.MainDashboard;
import com.unicesar.proyectogym.views.util.MemberTableModel;
import com.unicesar.proyectogym.views.util.ModernComponents;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

public class UserListPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final MemberService memberService;
    private final MembershipService membershipService;
    private final MemberTableModel tableModel;
    private final JTable tabla;
    private final JLabel lblTotal = new JLabel(" ");

    public UserListPanel() {
        this.memberService = AppContext.get().getMemberService();
        this.membershipService = AppContext.get().getMembershipService();
        this.tableModel = new MemberTableModel(membershipService);
        this.tabla = new JTable(tableModel);
        initUI();
        loadData();
    }

    private void initUI() {
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("USUARIOS REGISTRADOS");
        ModernComponents.styleLabel(titulo, Font.BOLD, 24f, ModernComponents.CYAN_NEON);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        add(titulo, BorderLayout.NORTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(28);
        tabla.setAutoCreateRowSorter(true);
        tabla.setRowSorter(new TableRowSorter<>(tableModel));
        tabla.getColumnModel().getColumn(5).setCellRenderer(new StateRenderer());

        ModernComponents.styleTable(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        ModernComponents.styleScrollPane(scroll);
        add(scroll, BorderLayout.CENTER);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() != -1) {
                    editSelected();
                }
            }
        });

        JButton btnNuevo = new JButton("Nuevo usuario");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnCerrar = new JButton("Cerrar");

        ModernComponents.styleButton(btnNuevo, true);
        ModernComponents.styleButton(btnEditar, false);
        ModernComponents.styleButton(btnEliminar, false);
        ModernComponents.styleButton(btnRefrescar, false);
        ModernComponents.styleButton(btnCerrar, false);

        btnNuevo.addActionListener(e -> newUser());
        btnEditar.addActionListener(e -> editSelected());
        btnEliminar.addActionListener(e -> deleteSelected());
        btnRefrescar.addActionListener(e -> loadData());
        btnCerrar.addActionListener(e -> closePanel());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        ModernComponents.styleLabel(lblTotal, Font.BOLD, 14f, ModernComponents.TEXT_WHITE);
        actions.add(lblTotal);
        actions.add(btnNuevo);
        actions.add(btnEditar);
        actions.add(btnEliminar);
        actions.add(btnRefrescar);
        actions.add(btnCerrar);

        JLabel ayuda = new JLabel("Doble clic sobre un usuario para editarlo.");
        ModernComponents.styleLabel(ayuda, Font.ITALIC, 13f, ModernComponents.TEXT_GRAY);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        sur.add(ayuda, BorderLayout.WEST);
        sur.add(actions, BorderLayout.EAST);
        add(sur, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setData(memberService.findAll());
        lblTotal.setText("Total: " + tableModel.getRowCount() + "   ");
    }

    private void newUser() {
        MainDashboard dash = (MainDashboard) SwingUtilities.getWindowAncestor(this);
        if (dash != null) {
            RegisterUserPanel panel = new RegisterUserPanel();
            panel.setOnSaved(this::loadData);
            dash.showPanel(panel);
        }
    }

    private void editSelected() {
        int viewRow = tabla.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Edición", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = tabla.convertRowIndexToModel(viewRow);
        Member seleccionado = tableModel.getMemberAt(modelRow);

        MainDashboard dash = (MainDashboard) SwingUtilities.getWindowAncestor(this);
        if (dash != null) {
            RegisterUserPanel panel = new RegisterUserPanel(seleccionado);
            panel.setOnSaved(this::loadData);
            dash.showPanel(panel);
        }
    }

    private void deleteSelected() {
        int viewRow = tabla.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla para eliminar.", "Eliminar usuario", JOptionPane.INFORMATION_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void closePanel() {
        MainDashboard dash = (MainDashboard) SwingUtilities.getWindowAncestor(this);
        if (dash != null) {
            dash.showPanel(new WelcomePanel());
        }
    }


    private class StateRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(CENTER);

            int modelRow = table.convertRowIndexToModel(row);
            Member m = tableModel.getMemberAt(modelRow);
            var report = membershipService.evaluate(m);

            Color fg;
            if (report.getEffectiveState() == MembershipStatus.ACTIVE) {
                fg = (report.getDaysRemaining() < MembershipService.DAYS_NOTICE_EXPIRATION)
                        ? new Color(230, 145, 0)
                        : new Color(0, 200, 0);
            } else {
                fg = new Color(255, 80, 80);
            }
            comp.setForeground(isSelected ? Color.WHITE : fg);
            return comp;
        }
    }
}

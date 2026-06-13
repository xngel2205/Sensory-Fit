package com.unicesar.proyectogym.views.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public final class ModernComponents {

    public static final Color BG_DARK_BLUE = new Color(7, 13, 25);
    public static final Color BG_CARD = new Color(15, 25, 45);
    public static final Color CYAN_NEON = new Color(0, 255, 170);
    public static final Color BLUE_NEON = new Color(0, 180, 255);
    public static final Color TEXT_WHITE = Color.WHITE;
    public static final Color TEXT_GRAY = new Color(170, 185, 205);

    private ModernComponents() {}

    public static void styleButton(JButton button, boolean primary) {
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(primary ? CYAN_NEON : TEXT_GRAY, 2));
        if (primary) {
            button.setBackground(CYAN_NEON);
            button.setForeground(Color.BLACK);
        } else {
            button.setBackground(new Color(25, 35, 55));
            button.setForeground(TEXT_WHITE);
        }
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleSidebarButton(JButton button) {
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBackground(new Color(15, 25, 45));
        button.setForeground(TEXT_WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(25, 35, 55)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(25, 35, 55));
                button.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, CYAN_NEON));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(15, 25, 45));
                button.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, new Color(25, 35, 55)));
            }
        });
    }

    public static void styleLabel(JLabel label, int style, float size, Color color) {
        label.setFont(new Font("Segoe UI", style, (int) size));
        label.setForeground(color);
    }

    public static void styleTextField(JTextField textField) {
        textField.setBackground(new Color(15, 25, 45));
        textField.setForeground(TEXT_WHITE);
        textField.setCaretColor(CYAN_NEON);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(35, 50, 75), 1),
                BorderFactory.createEmptyBorder(5, 7, 5, 7)
        ));
    }

    public static void styleTextArea(JTextArea textArea) {
        textArea.setBackground(new Color(15, 25, 45));
        textArea.setForeground(TEXT_WHITE);
        textArea.setCaretColor(CYAN_NEON);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setBackground(new Color(15, 25, 45));
        comboBox.setForeground(TEXT_WHITE);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(35, 50, 75), 1));
    }

    public static void styleTable(JTable table) {
        table.setBackground(new Color(15, 25, 45));
        table.setForeground(TEXT_WHITE);
        table.setGridColor(new Color(35, 50, 75));
        table.setSelectionBackground(new Color(30, 75, 110));
        table.setSelectionForeground(TEXT_WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(25, 40, 65));
        header.setForeground(CYAN_NEON);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBorder(BorderFactory.createLineBorder(new Color(35, 50, 75), 1));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(15, 25, 45) : new Color(20, 32, 55));
                }
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);
        table.setDefaultRenderer(Number.class, cellRenderer);
    }

    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.getViewport().setBackground(BG_DARK_BLUE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(35, 50, 75), 1));
    }

    public static void stylePanel(JPanel panel, boolean card) {
        panel.setOpaque(true);
        panel.setBackground(card ? BG_CARD : BG_DARK_BLUE);
        if (card) {
            panel.setBorder(BorderFactory.createLineBorder(new Color(35, 50, 75), 1));
        }
    }
}

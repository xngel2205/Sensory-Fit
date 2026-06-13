package com.unicesar.proyectogym.views.util;

import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.service.MembershipService;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;


public class MemberTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String[] COLUMNS = {
        "Identificación", "Nombres", "Apellidos", "Tipo membresía",
        "Vencimiento", "Estado", "Huella"
    };

    private final transient MembershipService membershipService;
    private transient List<Member> data = new ArrayList<>();

    public MemberTableModel(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    public void setData(List<Member> members) {
        this.data = (members == null) ? new ArrayList<>() : new ArrayList<>(members);
        fireTableDataChanged();
    }

    public Member getMemberAt(int modelRow) {
        return data.get(modelRow);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Member m = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return m.getIdentification();
            case 1: return m.getNames();
            case 2: return m.getSurnames();
            case 3: return m.getMembershipType() == null ? "" : m.getMembershipType().getLabel();
            case 4: return m.getDateExpiration() == null ? "" : m.getDateExpiration().format(FMT);
            case 5: return membershipService.evaluate(m).getEffectiveState().getLabel();
            case 6: return m.hastFootprint() ? "Sí" : "No";
            default: return "";
        }
    }
}

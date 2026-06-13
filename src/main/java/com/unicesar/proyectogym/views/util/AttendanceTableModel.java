package com.unicesar.proyectogym.views.util;

import com.unicesar.proyectogym.model.Attendance;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;


public class AttendanceTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final String[] COLS = {"Documento", "Nombre", "Fecha", "Hora"};

    private transient List<Attendance> data = new ArrayList<>();

    public void setData(List<Attendance> attendances) {
        this.data = (attendances == null) ? new ArrayList<>() : new ArrayList<>(attendances);
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return COLS.length;
    }

    @Override
    public String getColumnName(int c) {
        return COLS[c];
    }

    @Override
    public Object getValueAt(int r, int c) {
        Attendance a = data.get(r);
        switch (c) {
            case 0: return a.getDocumentUser();
            case 1: return a.getNameUser();
            case 2: return a.getDateTime() == null ? "" : a.getDateTime().format(FECHA);
            case 3: return a.getDateTime() == null ? "" : a.getDateTime().format(HORA);
            default: return "";
        }
    }
}

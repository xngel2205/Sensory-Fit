package com.unicesar.proyectogym.views.util;

import com.unicesar.proyectogym.model.PhysicalEvaluation;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;


public class EvaluacionTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String[] COLS = {
        "Fecha", "Peso (kg)", "IMC", "Clasificación", "Grasa %", "Masa (kg)", "Observaciones"
    };

    private transient List<PhysicalEvaluation> data = new ArrayList<>();

    public void setData(List<PhysicalEvaluation> evals) {
        this.data = (evals == null) ? new ArrayList<>() : new ArrayList<>(evals);
        fireTableDataChanged();
    }

    public PhysicalEvaluation getAt(int row) {
        return data.get(row);
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
        PhysicalEvaluation e = data.get(r);
        switch (c) {
            case 0: return e.getDate() == null ? "" : e.getDate().format(FMT);
            case 1: return e.getWeight();
            case 2: return e.getImc();
            case 3: return e.getClassification() == null ? "" : e.getClassification().getLabel();
            case 4: return e.getFatPercentage();
            case 5: return e.getMuscleMass();
            case 6: return e.getObservations() == null ? "" : e.getObservations();
            default: return "";
        }
    }
}

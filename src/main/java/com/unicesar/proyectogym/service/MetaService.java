package com.unicesar.proyectogym.service;

import com.unicesar.proyectogym.model.PhysicalEvaluation;
import com.unicesar.proyectogym.model.HistoryProgress;
import com.unicesar.proyectogym.model.PhysicalGoal;
import com.unicesar.proyectogym.interfaces.MetaDao;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MetaService {

    private final MetaDao metaDao;

    public MetaService(MetaDao metaDao) {
        this.metaDao = metaDao;
    }

    public PhysicalGoal register(PhysicalGoal goal) {
        validate(goal);
        List<PhysicalGoal> all = metaDao.findAll();
        for (PhysicalGoal m : all) {
            if (m.getDocumentUser() != null
                    && m.getDocumentUser().equals(goal.getDocumentUser())) {
                m.setActive(false);
            }
        }
        goal.setActive(true);
        if (goal.getDateRegister() == null) {
            goal.setDateRegister(LocalDate.now());
        }
        all.add(goal);
        metaDao.saveAll(all);
        return goal;
    }

    public Optional<PhysicalGoal> findActive(String document) {
        return metaDao.findActiveByDocument(document);
    }

    public List<PhysicalGoal> findByDocument(String document) {
        return metaDao.findByDocument(document);
    }

    public void calculateProgress(HistoryProgress h) {
        PhysicalGoal goal = h.getActiveGoal();
        if (goal == null || !h.hasEvaluations()) {
            h.setAdvanceWeightPorc(0);
            h.setAdvanceFatPorc(0);
            return;
        }
        PhysicalEvaluation initial = h.getFirst();
        PhysicalEvaluation current = h.getLast();

        h.setAdvanceWeightPorc(round(progressPercentage(initial.getWeight(), current.getWeight(), goal.getTargetWeight())));
        h.setAdvanceFatPorc(round(progressPercentage(initial.getFatPercentage(), current.getFatPercentage(),
                        goal.getTargetFat())));
    }
    public double progressPercentage(double initial, double current, double target) {
        double required = target - initial;
        if (Math.abs(required) < 1e-6) {
            return Math.abs(current - target) < 1e-6 ? 100 : 0;
        }
        double Advanced = current - initial;
        double porc = (Advanced / required) * 100.0;
        if (porc < 0) {
            return 0;
        }
        return Math.min(porc, 100);
    }

    public void validate(PhysicalGoal meta) {
        List<String> errors = new ArrayList<>();
        if (meta == null) {
            throw new ValidationException("No se recibió información de la meta.");
        }
        if (meta.getDocumentUser() == null || meta.getDocumentUser().isBlank()) {
            errors.add("El documento del usuario es obligatorio.");
        }
        if (meta.getTargetWeight() <= 0) {
            errors.add("El peso objetivo debe ser mayor a 0.");
        }
        if (meta.getTargetFat() < 0 || meta.getTargetFat() > 100) {
            errors.add("El porcentaje de grasa objetivo debe estar entre 0 y 100.");
        }
        if (meta.getEstimatedDate() == null) {
            errors.add("La fecha estimada es obligatoria.");
        } else if (meta.getEstimatedDate().isBefore(LocalDate.now())) {
            errors.add("La fecha estimada no puede ser anterior a hoy.");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}

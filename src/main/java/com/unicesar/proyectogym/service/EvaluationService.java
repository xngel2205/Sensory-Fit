package com.unicesar.proyectogym.service;

import com.unicesar.proyectogym.model.PhysicalEvaluation;
import com.unicesar.proyectogym.model.HistoryProgress;
import com.unicesar.proyectogym.model.ImcClassification;
import com.unicesar.proyectogym.model.Trend;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.unicesar.proyectogym.interfaces.EvaluationDao;

public class EvaluationService {

    private final EvaluationDao evaluationDao;

    public EvaluationService(EvaluationDao evaluacionDao) {
        this.evaluationDao = evaluacionDao;
    }

    public double calculateImc(double weightKg, double heightCm) {
        if (heightCm <= 0) {
            return 0;
        }
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    public PhysicalEvaluation register(PhysicalEvaluation e) {
        validar(e, true);
        double imc = calculateImc(e.getWeight(), e.getHeight());
        e.setImc(round(imc));
        e.setClassification(ImcClassification.classify(imc));
        evaluationDao.save(e);
        return e;
    }


    public List<PhysicalEvaluation> findByDocument(String document) {
        return evaluationDao.findByDocument(document);
    }

    public List<PhysicalEvaluation> findAll() {
        return evaluationDao.findAll();
    }

    public PhysicalEvaluation findLast(String document) {
        List<PhysicalEvaluation> lista = evaluationDao.findByDocument(document);
        return lista.isEmpty() ? null : lista.get(lista.size() - 1);
    }

    public List<PhysicalEvaluation> findByRang(String document, LocalDate from, LocalDate until) {
        List<PhysicalEvaluation> result = new ArrayList<>();
        for (PhysicalEvaluation e : evaluationDao.findByDocument(document)) {
            if ((from == null || !e.getDate().isBefore(from))
                    && (until == null || !e.getDate().isAfter(until))) {
                result.add(e);
            }
        }
        return result;
    }

    public void calculateIndicators(HistoryProgress h) {
        if (!h.hasEvaluations()) {
            h.setTrend(Trend.NO_DATA);
            return;
        }
        PhysicalEvaluation first = h.getFirst();
        PhysicalEvaluation last = h.getLast();

        h.setDifferenceWeight(round(last.getWeight() - first.getWeight()));
        h.setVariationImc(round(last.getImc() - first.getImc()));
        h.setDifferenceFat(round(last.getFatPercentage() - first.getFatPercentage()));
        h.setDifferenceMuscleMass(round(last.getMuscleMass() - first.getMuscleMass()));
        h.setTrend(calculateTrend(h));
    }

    private Trend calculateTrend(HistoryProgress h) {
        if (h.getEvaluations().size() < 2) {
            return Trend.NO_DATA;
        }
        int score = 0;

        double difFat = h.getDifferenceFat();
        if (difFat < -0.5) {
            score++;
        } else if (difFat > 0.5) {
            score--;
        }

        if (h.getActiveGoal() != null && h.getActiveGoal().getTargetWeight() > 0) {
            double obj = h.getActiveGoal().getTargetWeight();
            double distInitial = Math.abs(h.getFirst().getWeight() - obj);
            double disCurrent = Math.abs(h.getLast().getWeight() - obj);
            if (disCurrent < distInitial - 0.1) {
                score++;
            } else if (disCurrent > distInitial + 0.1) {
                score--;
            }
        }

        if (score > 0) {
            return Trend.IMPROVEMENT;
        }
        if (score < 0) {
            return Trend.DETERIORATION;
        }
        return Trend.STABLE;
    }


    public void validar(PhysicalEvaluation e, boolean isNew) {
        List<String> errors = new ArrayList<>();
        if (e == null) {
            throw new ValidationException("No se recibió información de la evaluación.");
        }
        if (e.getDocumentUser() == null || e.getDocumentUser().isBlank()) {
            errors.add("El documento del usuario es obligatorio.");
        }
        if (e.getDate() == null) {
            errors.add("La fecha de evaluación es obligatoria.");
        } else if (e.getDate().isAfter(LocalDate.now())) {
            errors.add("La fecha de evaluación no puede ser futura.");
        }
        if (e.getWeight() <= 0) {
            errors.add("El peso debe ser mayor a 0.");
        }
        if (e.getHeight() <= 0) {
            errors.add("La altura debe ser mayor a 0.");
        }
        if (e.getFatPercentage() < 0 || e.getFatPercentage() > 100) {
            errors.add("El porcentaje de grasa debe estar entre 0 y 100.");
        }
        if (e.getMuscleMass() < 0) {
            errors.add("La masa muscular no puede ser negativa.");
        }
        if (e.getWaistCircumference() < 0 || e.getHipCircumference() < 0) {
            errors.add("Las circunferencias no pueden ser negativas.");
        }

        if (isNew && e.getDocumentUser() != null && e.getDate() != null
                && evaluationDao.existsByDocumentAndDate(e.getDocumentUser(), e.getDate())) {
            errors.add("Ya existe una evaluación para ese usuario en la fecha "
                    + e.getDate() + ".");
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}

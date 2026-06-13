package com.unicesar.proyectogym.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;


@Data
public class HistoryProgress {

    private final Member member;

    private final List<PhysicalEvaluation> evaluations;

    private final PhysicalGoal activeGoal;


    private double differenceWeight;
    private double variationImc; 
    private double differenceFat;
    private double differenceMuscleMass;
    private Trend trend = Trend.NO_DATA;
    private double advanceWeightPorc;
    private double advanceFatPorc;

    public HistoryProgress(Member member, List<PhysicalEvaluation> evaluations,
                             PhysicalGoal activeGoal) {
        this.member = member;
        this.evaluations = (evaluations == null) ? new ArrayList<>() : evaluations;
        this.activeGoal = activeGoal;
    }

    public PhysicalEvaluation getFirst() {
        return evaluations.isEmpty() ? null : evaluations.get(0);
    }

    public PhysicalEvaluation getLast() {
        return evaluations.isEmpty() ? null : evaluations.get(evaluations.size() - 1);
    }

    public boolean hasEvaluations() {
        return !evaluations.isEmpty();
    }
}

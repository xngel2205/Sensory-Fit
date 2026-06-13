package com.unicesar.proyectogym.controller;

import com.unicesar.proyectogym.model.PhysicalEvaluation;
import com.unicesar.proyectogym.model.HistoryProgress;
import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.model.PhysicalGoal;
import com.unicesar.proyectogym.service.EvaluationService;
import com.unicesar.proyectogym.service.MemberService;
import com.unicesar.proyectogym.service.MetaService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public class ProgresoController {

    private final EvaluationService evaluationService;
    private final MetaService goalService;
    private final MemberService memberService;

    public ProgresoController(EvaluationService evaluacionService,
                              MetaService metaService,
                              MemberService memberService) {
        this.evaluationService = evaluacionService;
        this.goalService = metaService;
        this.memberService = memberService;
    }


    public Optional<Member> searchUser(String document) {
        return memberService.findById(document);
    }



    public PhysicalEvaluation registerEvaluation(PhysicalEvaluation e) {
        return evaluationService.register(e);
    }

    public double calculateImc(double weight, double height) {
        return evaluationService.calculateImc(weight, height);
    }

    public List<PhysicalEvaluation> evaluationsOf(String document) {
        return evaluationService.findByDocument(document);
    }

    public List<PhysicalEvaluation> rangeEvaluations(String document,
                                                      LocalDate from, LocalDate until) {
        return evaluationService.findByRang(document, from, until);
    }

    public PhysicalEvaluation lastEvaluation(String document) {
        return evaluationService.findLast(document);
    }


    public PhysicalGoal registerGoal(PhysicalGoal goal) {
        return goalService.register(goal);
    }

    public Optional<PhysicalGoal> activeGoal(String document) {
        return goalService.findActive(document);
    }

   
    public HistoryProgress getHistory(String document) {
        Optional<Member> member = memberService.findById(document);
        if (member.isEmpty()) {
            return null;
        }
        List<PhysicalEvaluation> evals = evaluationService.findByDocument(document);
        PhysicalGoal goal = goalService.findActive(document).orElse(null);

        HistoryProgress record = new HistoryProgress(member.get(), evals, goal);
        evaluationService.calculateIndicators(record);
        goalService.calculateProgress(record);
        return record;
    }
}

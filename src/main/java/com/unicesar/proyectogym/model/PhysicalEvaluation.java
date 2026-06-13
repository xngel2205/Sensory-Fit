package com.unicesar.proyectogym.model;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhysicalEvaluation implements Serializable {

    private static final long serialVersionUID = 1L;
    private String documentUser;
    private LocalDate date;
    private double weight;
    private double height;
    private double imc;
    private ImcClassification classification;
    private double fatPercentage;
    private double muscleMass;
    private double waistCircumference;
    private double hipCircumference;
    private String observations;
}

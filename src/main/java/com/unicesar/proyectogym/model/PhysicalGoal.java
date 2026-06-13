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
public class PhysicalGoal implements Serializable {

    private static final long serialVersionUID = 1L;
    private String documentUser;
    private LocalDate dateRegister;
    private double targetWeight;
    private double targetFat;
    private LocalDate estimatedDate;
    private String observations;
    private boolean active;
}

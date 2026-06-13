package com.unicesar.proyectogym.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
   
    private int typeId;
    private String numIde;
    private String firstName;
    private String secondName;
    private String firstLastname;
    private String secondLastname;
    private Date dateBorn;
    
}


package com.unicesar.proyectogym.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class historicResult {
    
    private User user;
    private Date dateResult;
    private float heigth;
    private float weigth;
    private float imc;
    
}

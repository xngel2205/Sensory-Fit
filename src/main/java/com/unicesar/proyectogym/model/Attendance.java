package com.unicesar.proyectogym.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Attendance implements Serializable {

    private static final long serialVersionUID = 1L;

 
    private String documentUser;
 
    private String nameUser;

    private LocalDateTime dateTime;
    
    
}

package com.unicesar.proyectogym.interfaces;

import com.unicesar.proyectogym.model.PhysicalEvaluation;
import java.time.LocalDate;
import java.util.List;


public interface EvaluationDao {

    void save(PhysicalEvaluation evaluacion);

    List<PhysicalEvaluation> findByDocument(String document);

    List<PhysicalEvaluation> findAll();

    boolean existsByDocumentAndDate(String document, LocalDate date);

    boolean delete(String document, LocalDate date);
}

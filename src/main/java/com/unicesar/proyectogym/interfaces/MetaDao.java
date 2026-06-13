package com.unicesar.proyectogym.interfaces;

import com.unicesar.proyectogym.model.PhysicalGoal;
import java.util.List;
import java.util.Optional;

public interface MetaDao {

    void save(PhysicalGoal goal);

    void saveAll(List<PhysicalGoal> goals);

    List<PhysicalGoal> findByDocument(String document);

    Optional<PhysicalGoal> findActiveByDocument(String document);

    List<PhysicalGoal> findAll();
}

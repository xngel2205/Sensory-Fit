package com.unicesar.proyectogym.interfaces;

import com.unicesar.proyectogym.model.Member;
import java.util.List;
import java.util.Optional;


public interface MemberDao {

   
    void save(Member member);

    Optional<Member> findById(String identification);

    List<Member> findAll();

    boolean existsById(String identification);

    boolean deleteById(String identification);
}

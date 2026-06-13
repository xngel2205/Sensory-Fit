package com.unicesar.proyectogym.interfaces;

import com.unicesar.proyectogym.model.Attendance;
import java.util.List;


public interface AttendanceDao {


    void save(Attendance attendance);


    List<Attendance> findAll();


    List<Attendance> findByDocument(String document);
}

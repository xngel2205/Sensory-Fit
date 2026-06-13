
package com.unicesar.proyectogym.controller;

import com.unicesar.proyectogym.model.Attendance;
import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.service.AttendanceService;
import java.util.List;


public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    public Attendance registerIncome(Member member) {
        return attendanceService.registerIncome(member);
    }

    public List<Attendance> listAll() {
        return attendanceService.findAll();
    }

    public List<Attendance> listDocuments(String document) {
        return attendanceService.findByDocument(document);
    }
}

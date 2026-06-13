package com.unicesar.proyectogym.service;

import com.unicesar.proyectogym.model.Attendance;
import com.unicesar.proyectogym.model.Member;
import java.time.LocalDateTime;
import java.util.List;
import com.unicesar.proyectogym.interfaces.AttendanceDao;

public class AttendanceService {

    private final AttendanceDao attendanceDao;

    public AttendanceService(AttendanceDao asistenciaDao) {
        this.attendanceDao = asistenciaDao;
    }

    public Attendance registerIncome(Member member) {
        Attendance a = Attendance.builder()
                .documentUser(member.getIdentification())
                .nameUser(member.getFullName())
                .dateTime(LocalDateTime.now())
                .build();
        attendanceDao.save(a);
        return a;
    }

    public List<Attendance> findAll() {
        return attendanceDao.findAll();
    }

    public List<Attendance> findByDocument(String document) {
        return attendanceDao.findByDocument(document);
    }
}

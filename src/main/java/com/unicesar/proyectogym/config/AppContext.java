
package com.unicesar.proyectogym.config;

import com.unicesar.proyectogym.controller.AttendanceController;
import com.unicesar.proyectogym.controller.AuthenticationController;
import com.unicesar.proyectogym.controller.LoginController;
import com.unicesar.proyectogym.controller.ProgresoController;
import com.unicesar.proyectogym.controller.RegistrationController;
import com.unicesar.proyectogym.persistence.file.FileAsistenciaDao;
import com.unicesar.proyectogym.persistence.file.FileEvaluacionDao;
import com.unicesar.proyectogym.persistence.file.FileMemberDao;
import com.unicesar.proyectogym.persistence.file.FileMetaDao;
import com.unicesar.proyectogym.interfaces.MemberDao;
import com.unicesar.proyectogym.interfaces.MetaDao;
import com.unicesar.proyectogym.service.AttendanceService;
import com.unicesar.proyectogym.service.EvaluationService;
import com.unicesar.proyectogym.service.LoginService;
import com.unicesar.proyectogym.service.MemberService;
import com.unicesar.proyectogym.service.MembershipService;
import com.unicesar.proyectogym.service.MetaService;
import com.unicesar.proyectogym.interfaces.AttendanceDao;
import com.unicesar.proyectogym.interfaces.EvaluationDao;


public final class AppContext {

    private static AppContext instance;

    private final MemberDao memberDao;
    private final EvaluationDao evaluacionDao;
    private final MetaDao metaDao;
    private final AttendanceDao asistenciaDao;

    private final MemberService memberService;
    private final MembershipService membershipService;
    private final LoginService loginService;
    private final EvaluationService evaluacionService;
    private final MetaService metaService;
    private final AttendanceService asistenciaService;

    private AppContext() {
        this.memberDao = new FileMemberDao();
        this.evaluacionDao = new FileEvaluacionDao();
        this.metaDao = new FileMetaDao();
        this.asistenciaDao = new FileAsistenciaDao();
        this.memberService = new MemberService(memberDao);
        this.membershipService = new MembershipService();
        this.loginService = new LoginService();
        this.evaluacionService = new EvaluationService(evaluacionDao);
        this.metaService = new MetaService(metaDao);
        this.asistenciaService = new AttendanceService(asistenciaDao);
    }

    public static synchronized AppContext get() {
        if (instance == null) {
            instance = new AppContext();
        }
        return instance;
    }

    public MemberService getMemberService() {
        return memberService;
    }

    public MembershipService getMembershipService() {
        return membershipService;
    }

    public EvaluationService getEvaluacionService() {
        return evaluacionService;
    }

    public MetaService getMetaService() {
        return metaService;
    }

    public AttendanceService getAsistenciaService() {
        return asistenciaService;
    }

    public RegistrationController newRegistrationController() {
        return new RegistrationController(memberService);
    }

    public LoginController newLoginController() {
        return new LoginController(loginService);
    }

    public ProgresoController newProgresoController() {
        return new ProgresoController(evaluacionService, metaService, memberService);
    }

    public AttendanceController newAsistenciaController() {
        return new AttendanceController(asistenciaService);
    }


    public AuthenticationController newAuthenticationController(
            com.unicesar.proyectogym.interfaces.FingerprintService fp) {
        return new AuthenticationController(memberService, membershipService, fp);
    }
}

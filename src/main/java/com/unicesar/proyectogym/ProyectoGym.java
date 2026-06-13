package com.unicesar.proyectogym;

import com.unicesar.proyectogym.config.AppContext;
import com.unicesar.proyectogym.views.LoginFrame;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProyectoGym {

    private static final Logger LOG = Logger.getLogger(ProyectoGym.class.getName());

    public static void main(String[] args) {
        AppContext.get();
        java.awt.EventQueue.invokeLater(() -> {
            try {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            } catch (Exception ex) {
                LOG.log(Level.WARNING, "Error al iniciar LoginFrame", ex);
            }
        });
    }
}

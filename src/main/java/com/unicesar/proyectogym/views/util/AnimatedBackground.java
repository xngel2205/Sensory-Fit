package com.unicesar.proyectogym.views.util;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class AnimatedBackground extends JPanel {

    private int[] x = new int[20];
    private int[] y = new int[20];
    private Random random = new Random();

    public AnimatedBackground() {
        setOpaque(true);
        for(int i = 0; i < 20; i++) {
            x[i] = random.nextInt(1200);
            y[i] = random.nextInt(700);
        }
        Timer timer = new Timer(40, e -> {
            for(int i = 0; i < y.length; i++) {
                y[i] += 2;
                if(y[i] > getHeight()) {
                    y[i] = -50;
                    x[i] = random.nextInt(Math.max(getWidth(), 1));
                }
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(7, 13, 25));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        for(int i = 0; i < x.length; i++) {
            if(i % 2 == 0) {
                g2.setColor(new Color(0, 255, 170, 80));
            } else {
                g2.setColor(new Color(0, 180, 255, 80));
            }
            g2.drawString("🏋", x[i], y[i]);
        }
    }
}

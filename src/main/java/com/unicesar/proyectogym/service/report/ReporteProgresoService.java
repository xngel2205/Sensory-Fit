package com.unicesar.proyectogym.service.report;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.unicesar.proyectogym.model.PhysicalEvaluation;
import com.unicesar.proyectogym.model.HistoryProgress;
import com.unicesar.proyectogym.model.Member;
import com.unicesar.proyectogym.model.PhysicalGoal;
import com.unicesar.proyectogym.views.util.ProgresoChartFactory;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class ReporteProgresoService {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final Font F_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
    private static final Font F_SECTION = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, new Color(0, 90, 160));
    private static final Font F_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font F_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);

    public void generar(HistoryProgress h, File out) {
        Document doc = new Document(PageSize.A4, 36, 36, 40, 40);
        try (FileOutputStream fos = new FileOutputStream(out)) {
            PdfWriter.getInstance(doc, fos);
            doc.open();

            doc.add(titulo("Reporte de Progreso Físico"));
            doc.add(new Paragraph("Generado: " + LocalDateTime.now().format(DATE_TIME), F_NORMAL));
            doc.add(space());

            addPersonalData(doc, h.getMember());
            addCurrentState(doc, h);
            addIndicators(doc, h);
            addMeta(doc, h);
            addHistory(doc, h.getEvaluations());
            addGraphics(doc, h.getEvaluations());

            doc.close();
        } catch (Exception ex) {
            if (doc.isOpen()) {
                doc.close();
            }
            throw new ReporteException("No se pudo generar el reporte PDF: " + ex.getMessage(), ex);
        }
    }

    private void addPersonalData(Document doc, Member m) throws Exception {
        doc.add(seccion("Datos Personales"));
        PdfPTable t = dataTable();
        row(t, "Nombre", m.getFullName());
        row(t, "Documento", (m.getTypeIdentification() == null ? "" : m.getTypeIdentification() + " ")
                + m.getIdentification());
        row(t, "Correo", nvl(m.getCorreo()));
        row(t, "Teléfono", nvl(m.getPhone()));
        row(t, "Tipo de membresía", m.getMembershipType() == null ? "-" : m.getMembershipType().getLabel());
        row(t, "Estado de membresía", m.getState() == null ? "-" : m.getState().getLabel());
        doc.add(t);
        doc.add(space());
    }

    private void addCurrentState(Document doc, HistoryProgress h) throws Exception {
        doc.add(seccion("Estado Actual"));
        PhysicalEvaluation u = h.getLast();
        if (u == null) {
            doc.add(new Paragraph("El usuario no tiene evaluaciones registradas.", F_NORMAL));
            doc.add(space());
            return;
        }
        PdfPTable t = dataTable();
        row(t, "Fecha última evaluación", u.getDate().format(DATE));
        row(t, "Peso", u.getWeight() + " kg");
        row(t, "Altura", u.getHeight() + " cm");
        row(t, "IMC", String.valueOf(u.getImc()));
        row(t, "Clasificación", u.getClassification() == null ? "-" : u.getClassification().getLabel());
        row(t, "Grasa corporal", u.getFatPercentage() + " %");
        row(t, "Masa muscular", u.getMuscleMass() + " kg");
        row(t, "Cintura / Cadera", u.getWaistCircumference() + " / " + u.getHipCircumference() + " cm");
        doc.add(t);
        doc.add(space());
    }

    private void addIndicators(Document doc, HistoryProgress h) throws Exception {
        doc.add(seccion("Indicadores de Evolución"));
        if (!h.hasEvaluations()) {
            doc.add(new Paragraph("Sin datos suficientes.", F_NORMAL));
            doc.add(space());
            return;
        }
        PdfPTable t = dataTable();
        row(t, "Diferencia de peso", sign(h.getDifferenceWeight()) + " kg (respecto a la 1ª evaluación)");
        row(t, "Variación de IMC", sign(h.getVariationImc()));
        row(t, "Diferencia de grasa", sign(h.getDifferenceFat()) + " %");
        row(t, "Diferencia masa muscular", sign(h.getDifferenceMuscleMass()) + " kg");
        row(t, "Tendencia", h.getTrend().getLabel());
        doc.add(t);
        doc.add(space());
    }

    private void addMeta(Document doc, HistoryProgress h) throws Exception {
        doc.add(seccion("Cumplimiento de Metas"));
        PhysicalGoal meta = h.getActiveGoal();
        if (meta == null) {
            doc.add(new Paragraph("El usuario no tiene una meta activa.", F_NORMAL));
            doc.add(space());
            return;
        }
        PdfPTable t = dataTable();
        row(t, "Peso objetivo", meta.getTargetWeight() + " kg");
        row(t, "Grasa objetivo", meta.getTargetFat() + " %");
        row(t, "Fecha estimada", meta.getEstimatedDate() == null ? "-" : meta.getEstimatedDate().format(DATE));
        row(t, "Avance hacia peso", h.getAdvanceWeightPorc() + " %");
        row(t, "Avance hacia grasa", h.getAdvanceFatPorc() + " %");
        row(t, "Observaciones", nvl(meta.getObservations()));
        doc.add(t);
        doc.add(space());
    }

    private void addHistory(Document doc, List<PhysicalEvaluation> evals) throws Exception {
        doc.add(seccion("Historial de Evaluaciones"));
        if (evals.isEmpty()) {
            doc.add(new Paragraph("Sin evaluaciones.", F_NORMAL));
            doc.add(space());
            return;
        }
        PdfPTable t = new PdfPTable(new float[]{2.2f, 1.5f, 1.2f, 2.5f, 1.6f, 3f});
        t.setWidthPercentage(100);
        header(t, "Fecha", "Peso", "IMC", "Clasificación", "Grasa %", "Observaciones");
        for (PhysicalEvaluation e : evals) {
            cell(t, e.getDate().format(DATE));
            cell(t, String.valueOf(e.getWeight()));
            cell(t, String.valueOf(e.getImc()));
            cell(t, e.getClassification() == null ? "-" : e.getClassification().getLabel());
            cell(t, String.valueOf(e.getFatPercentage()));
            cell(t, nvl(e.getObservations()));
        }
        doc.add(t);
        doc.add(space());
    }

    private void addGraphics(Document doc, List<PhysicalEvaluation> evals) throws Exception {
        if (evals.isEmpty()) {
            return;
        }
        doc.add(seccion("Gráficas de Evolución"));
        addGraphic(doc, ProgresoChartFactory.peso(evals).createBufferedImage(500, 260));
        addGraphic(doc, ProgresoChartFactory.imc(evals).createBufferedImage(500, 260));
        addGraphic(doc, ProgresoChartFactory.grasa(evals).createBufferedImage(500, 260));
        addGraphic(doc, ProgresoChartFactory.masaMuscular(evals).createBufferedImage(500, 260));
    }

    private void addGraphic(Document doc, java.awt.Image awtImage) throws Exception {
        com.lowagie.text.Image img = com.lowagie.text.Image.getInstance(awtImage, null);
        img.scaleToFit(500, 260);
        img.setAlignment(Element.ALIGN_CENTER);
        doc.add(img);
        doc.add(space());
    }

    private Paragraph titulo(String s) {
        Paragraph p = new Paragraph(s, F_TITLE);
        p.setAlignment(Element.ALIGN_CENTER);
        return p;
    }

    private Paragraph seccion(String s) {
        Paragraph p = new Paragraph(s, F_SECTION);
        p.setSpacingBefore(6);
        p.setSpacingAfter(4);
        return p;
    }

    private Paragraph space() {
        return new Paragraph(" ", F_NORMAL);
    }

    private PdfPTable dataTable() {
        PdfPTable t = new PdfPTable(new float[]{1.2f, 2.8f});
        t.setWidthPercentage(100);
        return t;
    }

    private void row(PdfPTable t, String campo, String valor) {
        PdfPCell c1 = new PdfPCell(new Phrase(campo, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        c1.setBackgroundColor(new Color(235, 240, 247));
        t.addCell(c1);
        t.addCell(new PdfPCell(new Phrase(valor, F_NORMAL)));
    }

    private void header(PdfPTable t, String... titulos) {
        for (String s : titulos) {
            PdfPCell c = new PdfPCell(new Phrase(s, F_HEADER));
            c.setBackgroundColor(new Color(0, 90, 160));
            t.addCell(c);
        }
    }

    private void cell(PdfPTable t, String s) {
        t.addCell(new PdfPCell(new Phrase(s, F_NORMAL)));
    }

    private String sign(double v) {
        return (v > 0 ? "+" : "") + v;
    }

    private String nvl(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}

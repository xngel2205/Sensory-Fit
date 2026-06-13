package com.unicesar.proyectogym.views.util;

import com.unicesar.proyectogym.model.PhysicalEvaluation;
import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.ToDoubleFunction;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.category.DefaultCategoryDataset;

public final class ProgresoChartFactory {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ProgresoChartFactory() {
    }

    public static JFreeChart lineChart(String titulo, String ejeY,
                                       List<PhysicalEvaluation> evals,
                                       ToDoubleFunction<PhysicalEvaluation> extractor) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (PhysicalEvaluation e : evals) {
            dataset.addValue(extractor.applyAsDouble(e), ejeY, e.getDate().format(FMT));
        }
        JFreeChart chart = ChartFactory.createLineChart(
                titulo, "Fecha", ejeY, dataset,
                PlotOrientation.VERTICAL, false, true, false);

        chart.setBackgroundPaint(new Color(15, 25, 45));
        if (chart.getTitle() != null) {
            chart.getTitle().setPaint(new Color(0, 255, 170));
        }

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(7, 13, 25));
        plot.setDomainGridlinePaint(new Color(35, 50, 75));
        plot.setRangeGridlinePaint(new Color(35, 50, 75));

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelPaint(new Color(170, 185, 205));
        domainAxis.setTickLabelPaint(Color.WHITE);

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelPaint(new Color(170, 185, 205));
        rangeAxis.setTickLabelPaint(Color.WHITE);

        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0, 255, 170));
        renderer.setSeriesStroke(0, new java.awt.BasicStroke(3.0f));

        return chart;
    }

    public static JFreeChart peso(List<PhysicalEvaluation> evals) {
        return lineChart("Evolución del Peso", "Peso (kg)", evals, PhysicalEvaluation::getWeight);
    }

    public static JFreeChart imc(List<PhysicalEvaluation> evals) {
        return lineChart("Evolución del IMC", "IMC", evals, PhysicalEvaluation::getImc);
    }

    public static JFreeChart grasa(List<PhysicalEvaluation> evals) {
        return lineChart("Evolución de Grasa Corporal", "Grasa (%)", evals,
                PhysicalEvaluation::getFatPercentage);
    }

    public static JFreeChart masaMuscular(List<PhysicalEvaluation> evals) {
        return lineChart("Evolución de Masa Muscular", "Masa muscular (kg)", evals,
                PhysicalEvaluation::getMuscleMass);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
//package org.jfree.chart.demo;
/**
 *
 * @author STORM
 */
import javax.swing.JFrame;
 import org.jfree.chart.ChartFactory;
 import org.jfree.chart.ChartPanel;
 import org.jfree.chart.JFreeChart;
 import org.jfree.chart.plot.PlotOrientation;
 import org.jfree.data.xy.XYSeries;
 import org.jfree.data.xy.XYSeriesCollection;
 import org.jfree.ui.RefineryUtilities;



public class diagram extends JFrame {
    //public AFclass af_= new AFclass();
    public diagram(final double[][] a) {
        
        super("autofocus diagram");
        final XYSeries series = new XYSeries("autofocus");
        for (int i = 0; i <= a.length-2; i++){
            series.add(a[i][0], a[i][1]);
        }
        
        final XYSeriesCollection data = new XYSeriesCollection(series);
        final JFreeChart chart = ChartFactory.createXYLineChart(
            "autofocus calibration data",
            "objective position", 
            "identified z position", 
            data,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }

    public static void main(final String[] args) {
        double[][] b = null;
        final diagram demo = new diagram(b);
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

}

}
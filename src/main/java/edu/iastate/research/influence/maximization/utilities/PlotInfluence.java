package edu.iastate.research.influence.maximization.utilities;

import edu.iastate.research.influence.maximization.models.IMTreeSeedSet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * Created by madhavanrp on 6/11/17.
 */
public class PlotInfluence extends JFrame {
    public PlotInfluence(String title, List<IMTreeSeedSet> seedSets) {
        super(title);

        // Create dataset
        CategoryDataset dataset = createDataset(seedSets);

        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Influence By Seed Sets",
                "Seed Set #", "Nodes Influenced", dataset);


        // Create Panel
        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }
    private CategoryDataset createDataset(List<IMTreeSeedSet> seedSetList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int i=0;
        for (IMTreeSeedSet seedSet:
             seedSetList) {

            int targetsActivated = seedSet.getTargetsActivated();
            int nonTargetsActivated = seedSet.getNonTargetsActivated();
            dataset.addValue(targetsActivated, "Targets", String.valueOf(i));
            dataset.addValue(nonTargetsActivated, "Non Targets", String.valueOf(i));
            i++;
        }

        return dataset;
    }
}

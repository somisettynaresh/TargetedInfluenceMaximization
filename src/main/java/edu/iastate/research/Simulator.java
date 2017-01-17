package edu.iastate.research;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.utilities.FileDataReader;
import edu.iastate.research.influence.maximization.algorithms.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Naresh on 10/28/2016.
 */
public class Simulator {
    final static Logger logger = Logger.getLogger(Simulator.class);
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Graph File Name");
        String filename = sc.next();
        System.out.println("Enter the propagation probability");
        float probability = Float.parseFloat(sc.next());
        System.out.println("Enter percentage of A's to be in Graph");
        int percent = sc.nextInt();
        System.out.println("Enter budget of seed set");
        int budget = sc.nextInt();
        System.out.println("Enter non target threshold");
        int nonTargetThreshold = sc.nextInt();
        setupLogger(filename + "_" + probability +"_" + percent + "_" + budget+ "_"+ nonTargetThreshold + "_"+ System.currentTimeMillis()+ ".log");
        wikiGraphDifferentComobination(filename, probability, percent, budget, nonTargetThreshold);
    }

    private static void setupLogger(String logFileName) {
        Properties props = new Properties();
        try {
            InputStream configStream = new FileInputStream((Simulator.class.getClassLoader().getResource("log4j.properties")).getPath());
            props.load(configStream);
            configStream.close();
        } catch (IOException e) {
            System.out.println("log4j configuration file not found");
        }
        props.setProperty("log4j.appender.file.File", "logs/"+logFileName);
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);
    }

    private static void wikiGraphDifferentComobination(String filename, float probability, int percent, int budget, int nonTargetThreshold) {

        FileDataReader wikiVoteDataReader = new FileDataReader(filename, probability);

        Set<String> targetLabels = new HashSet<>();
        targetLabels.add("A");
        Set<String> nonTargetLabels = new HashSet<>();
        nonTargetLabels.add("B");

        DirectedGraph graphWith90PerA = generateGraph(wikiVoteDataReader, ((float) percent) / 100);
        logger.info("***************** Simulating with" + percent + "% A graph ****************");
        printGraphStats(graphWith90PerA, targetLabels, nonTargetLabels);

/*
        Greedy greedy = new GreedyWithMultiThreading();
        Set<Integer> seedSet = greedy.findSeedSet(graphWith90PerA,budget,targetLabels,10000);
        logger.info("Influence spread : " + greedy.influenceSpread(graphWith90PerA,seedSet,targetLabels, 10000));
*/

        EstimateNonTargets edag = new EstimateNonTargetsUsingRandomDAG();
        edag.estimate(graphWith90PerA,nonTargetLabels,10000);
    }

    private static void printGraphStats(DirectedGraph graph, Set<String> targetLabels, Set<String> nonTargetLabels) {
        logger.info("Number of vertices : " + graph.getVertices().size());
        logger.info("Number of Edges : " + graph.getNoOfEdges());
        int targetLabelledVertices = 0;
        int nonTargetVertices = 0;
        for (Vertex vertex : graph.getVertices()) {
            String label = (String) vertex.getProperties().get("label");
            if (targetLabels.contains(label)) {
                targetLabelledVertices++;
            } else if (nonTargetLabels.contains(label)) {
                nonTargetVertices++;
            }
        }
        logger.info("Number of target labelled vertices : " + targetLabelledVertices);
        logger.info("Number of non target labelled vertices : " + nonTargetVertices);

    }

    public static void printNonTargetThresholdMap(Map<Integer, Set<Vertex>> map) {
        for (Integer integer : map.keySet()) {
            System.out.println( "No of B's :" + integer);
            for (Vertex vertex : map.get(integer)) {
                System.out.print(vertex.getId() + ",");
            }
            System.out.println("");
        }
    }

    private static Map<Vertex, Integer> transform(Map<Integer, Set<Vertex>> map) {
        Map<Vertex, Integer> transformed = new HashMap<>();
        for (Integer noOfB : map.keySet()) {
            for (Vertex vertex : map.get(noOfB)) {
                transformed.put(vertex,noOfB);
            }
        }
        return transformed;
    }

    private static DirectedGraph generateGraph(FileDataReader wikiVoteDataReader, float aPercentage) {
        DirectedGraph graph = wikiVoteDataReader.createGraphFromData();
        for (Vertex vertex : graph.getVertices()) {
            vertex.setLabel(new Random().nextFloat() > aPercentage ? "B" : "A");
        }
        return graph;
    }



  }

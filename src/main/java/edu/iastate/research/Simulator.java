package edu.iastate.research;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.utilities.FileDataReader;
import edu.iastate.research.graph.utilities.ReadLabelsFromFile;
import edu.iastate.research.influence.maximization.algorithms.IMTInstanceByStrategy;
import edu.iastate.research.influence.maximization.algorithms.IMWithTargetLabels;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.models.IMTStrategy;
import edu.iastate.research.influence.maximization.models.IMTreeSeedSet;
import edu.iastate.research.influence.maximization.utilities.DisplaySeedSets;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;



/**
 * Created by Naresh on 10/28/2016.
 */
public class Simulator {
    final static Logger logger = Logger.getLogger(Simulator.class);

    public static void main(String[] args) {
        if(args!=null && args.length>0) {
            runWithParameters(args);
        } else {
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
            System.out.println("Enter the NonTargetsEstimate filename");
            String nonTargetsEstimateFilename = sc.next();
            System.out.println("Enter the Influence Maximization Strategy (1-6)");
            int strategy = sc.nextInt();
            setupLogger(filename + "_" + probability + "_" + percent + "_" + budget + "_" + nonTargetThreshold + "_" + "_" + strategy + "_" + System.currentTimeMillis() + ".log");
            wikiGraphDifferentComobination(filename, probability, percent, budget, nonTargetThreshold, nonTargetsEstimateFilename, strategy);
        }
    }

    private static void runWithParameters(String[] args) {
        String filename = args[0];
        float probability = Float.parseFloat(args[1]);
        int percent = Integer.valueOf(args[2]);
        int budget = Integer.valueOf(args[3]);
        int nonTargetThreshold = Integer.valueOf(args[4]);
        String nonTargetsEstimateFilename = "";
        int strategy = Integer.valueOf(args[6]);
        setupLogger(filename + "_" + probability + "_" + percent + "_" + budget + "_" + nonTargetThreshold + "_" + "_" + strategy + "_" + System.currentTimeMillis() + ".log");
        wikiGraphDifferentComobination(filename, probability, percent, budget, nonTargetThreshold, nonTargetsEstimateFilename, strategy);
    }

    private static void setupLogger(String logFileName) {
        Properties props = new Properties();
        try {
            InputStream configStream = Simulator.class.getClassLoader().getResourceAsStream("log4j.properties");
            props.load(configStream);
            configStream.close();
        } catch (IOException e) {
            System.out.println("log4j configuration file not found");
        }
        props.setProperty("log4j.appender.file.File", "logs/" + logFileName);
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);
    }

    private static void wikiGraphDifferentComobination(String filename, float probability, int percent, int budget, int nonTargetThreshold, String nonTargetsEstimateFilename, int strategy) {

        String experimentName = filename.split(".txt")[0] + "_" + probability + "_" + percent + "%A_" + budget + "_" + nonTargetThreshold + "_" + nonTargetsEstimateFilename.split(".data")[0].replace('\\', '_') + "_" + strategy;
        FileDataReader wikiVoteDataReader = new FileDataReader(filename, probability);

        Set<String> targetLabels = new HashSet<>();
        targetLabels.add("A");
        Set<String> nonTargetLabels = new HashSet<>();
        nonTargetLabels.add("B");

        DirectedGraph graph = generateGraph(wikiVoteDataReader, ((float) percent) / 100, filename);
        logger.info("***************** Simulating with" + percent + "% A graph ****************");
        printGraphStats(graph, targetLabels, nonTargetLabels);

/*
        Greedy greedy = new GreedyWithMultiThreading();
        Set<Integer> seedSet = greedy.findSeedSet(graph,budget,targetLabels,10000);
        logger.info("Influence spread : " + greedy.influenceSpread(graph,seedSet,targetLabels, 10000));


        EstimateNonTargets edag = new EstimateNonTargetsUsingRandomDAG();
        edag.estimate(graph, nonTargetLabels, 20000);
*/

        IMWithTargetLabels im = IMTInstanceByStrategy.getInstance(IMTStrategy.byValue(strategy));
        List<IMTreeSeedSet> candidateSeedSets = im.findCandidateSeedSets(graph, budget, nonTargetThreshold, targetLabels, nonTargetLabels, 20000, nonTargetsEstimateFilename, experimentName);
        DisplaySeedSets.printOutput(candidateSeedSets);
//        Set<Integer> seedSet = im.findSeedSet(graph, budget, nonTargetThreshold, targetLabels, nonTargetLabels, 10000, nonTargetsEstimateFilename, experimentName);
        DisplaySeedSets.plotGraph(candidateSeedSets);



    }

    private static void printTargetsActivatedForSeedSet(DirectedGraph graph, Set<Integer> seedSet, Set<String> targetLabels) {
        for (Integer integer : seedSet) {
            logger.info("Seed : " + integer);
        }
        Set<Integer> activatedSet = IndependentCascadeModel.performDiffusion(graph, seedSet, 20000, new HashSet<>());
        int targetsCount = 0;
        int nonTargetsCount = 0;
        for (Integer v : activatedSet) {
            if (graph.find(v).hasLabel(targetLabels)) {
                targetsCount++;
            } else {
                nonTargetsCount++;
            }
        }
        logger.info("Targets Activated : " + targetsCount);
        logger.info("Non Targets Activated : " + nonTargetsCount);
    }

    private static void printGraphStats(DirectedGraph graph, Set<String> targetLabels, Set<String> nonTargetLabels) {
        logger.info("Number of vertices : " + graph.getVertices().size());
        logger.info("Number of Edges : " + graph.getNoOfEdges());
        int targetLabelledVertices = 0;
        int nonTargetVertices = 0;
        for (Vertex vertex : graph.getVertices()) {
            String label = vertex.getProperties().get("label");
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
            System.out.println("No of B's :" + integer);
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
                transformed.put(vertex, noOfB);
            }
        }
        return transformed;
    }

    private static DirectedGraph generateGraph(FileDataReader wikiVoteDataReader, float aPercentage, String graphFileName) {
        DirectedGraph graph = wikiVoteDataReader.createGraphFromData();
        return new ReadLabelsFromFile().read(graph, graphFileName + "_" + aPercentage + "_labels.txt");
    }


}

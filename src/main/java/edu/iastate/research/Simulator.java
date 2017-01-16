package edu.iastate.research;

import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by Naresh on 10/28/2016.
 */
public class Simulator {
    static Logger logger = Logger.getLogger("influenceMax");

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Graph File Name");
        String filename = sc.next();
        System.out.println("Enter percentage of A's to be in Graph");
        int percent = sc.nextInt();
        System.out.println("Enter budget of seed set");
        int budget = sc.nextInt();
        System.out.println("Enter non target threshold");
        int nonTargetThreshold = sc.nextInt();
        System.out.println("Enter Log file name");
        setupLogger(filename + "_" + percent + "_" + budget+ "_"+ nonTargetThreshold + ".log");
        wikiGraphDifferentComobination(filename, percent, budget, nonTargetThreshold);
    }

    private static void setupLogger(String logFileName) {

        FileHandler fh;
        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler(logFileName);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            // the following statement is used to log any messages

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void wikiGraphDifferentComobination(String filename, int percent, int budget, int nonTargetThreshold) {

        FileDataReader wikiVoteDataReader = new FileDataReader(filename);

        Set<String> targetLabels = new HashSet<>();
        targetLabels.add("A");
        Set<String> nonTargetLabels = new HashSet<>();
        nonTargetLabels.add("B");

        DirectedGraph graphWith90PerA = generateGraph(wikiVoteDataReader, ((float) percent) / 100);
        logger.info("***************** Simulating with" + percent + "% A graph ****************");
        printGraphStats(graphWith90PerA, targetLabels, nonTargetLabels);
        simulateICTargetMaximization(graphWith90PerA, budget, nonTargetThreshold, targetLabels, nonTargetLabels);
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

    private static void simulateICTargetMaximization(DirectedGraph graph, int budget, int nonTragetThreshold, Set<String> targetLabels, Set<String> nonTargetLabels) {
        logger.info("************** With DD  **********************");
        ICTargetMaximizationPruningDD icTargetMaximizationPruningDD = new ICTargetMaximizationPruningDD();
        Set<Integer> seedSetWithDD = icTargetMaximizationPruningDD.findSeedSet(graph, budget, nonTragetThreshold, targetLabels, nonTargetLabels);
        perfromDiffusionWithResultSeedset(graph, targetLabels, nonTargetLabels, seedSetWithDD);
        Map<Integer, Set<Vertex>> map = icTargetMaximizationPruningDD.getNonTargetVertexMap();

        String treeFile = icTargetMaximizationPruningDD.getTreeFileName();
        IMTree treeFromFile = IMTreeParser.read(treeFile);
        getResultsFromTree(treeFromFile,graph, budget, targetLabels, nonTargetLabels);

        logger.info("************** With DD  Optimized **********************");
        OptimizedGreedy icTargetMaximizationPruningDDOptimized = new OptimizedGreedy();
        Set<Integer> seedSetWithDDOptimized = icTargetMaximizationPruningDDOptimized.findSeedSet(graph, budget, nonTragetThreshold, targetLabels, nonTargetLabels);
        perfromDiffusionWithResultSeedset(graph, targetLabels, nonTargetLabels, seedSetWithDDOptimized);
        Map<Integer, Set<Vertex>> map1 = icTargetMaximizationPruningDDOptimized.getNonTargetVertexMap();

        System.out.println("Printing the diff");

        printDiff(map, map1);

        String treeFileDDO = icTargetMaximizationPruningDDOptimized.getTreeFileName();
        IMTree treeFromFileDDO = IMTreeParser.read(treeFileDDO);
        getResultsFromTree(treeFromFileDDO,graph, budget, targetLabels, nonTargetLabels);

        /*logger.info("******************* With Greedy **************");
        ICTargetMaximizationPruning icTargetMaximizationPruningGreedy = new ICTargetMaximizationPruning();
        Set<Integer> seedSetWithGreedy = icTargetMaximizationPruningGreedy.findSeedSet(graph, budget, nonTragetThreshold, targetLabels, nonTargetLabels);
        perfromDiffusionWithResultSeedset(graph, targetLabels, nonTargetLabels, seedSetWithGreedy);

        String treeFileGreedy = icTargetMaximizationPruningGreedy.getTreeFileName();
        IMTree treeFromFileGreedy = IMTreeParser.read(treeFileGreedy);
        getResultsFromTree(treeFromFileGreedy,graph, budget, targetLabels, nonTargetLabels);*/

    }

    private static void printDiff(Map<Integer, Set<Vertex>> map, Map<Integer, Set<Vertex>> map1) {
        System.out.println("printing DD Map");
        printNonTargetThresholdMap(map);
        System.out.println("printing DDO Map");
        printNonTargetThresholdMap(map1);

        Map<Vertex, Integer> transformedMap = transform(map);
        Map<Vertex, Integer> transformedMap1 = transform(map1);
        for (Vertex vertex : transformedMap.keySet()) {
            if((!transformedMap1.containsKey(vertex)) || (transformedMap1.containsKey(vertex) && transformedMap.get(vertex) > transformedMap1.get(vertex))) {
                System.out.println("DD is more for " + vertex.getId() + " , " + transformedMap.get(vertex) + " : " + (transformedMap1.containsKey(vertex)? transformedMap1.get(vertex): -1));
            }
        }

        for (Vertex vertex : transformedMap1.keySet()) {
            if(!transformedMap.containsKey(vertex)) {
                System.out.println("DD is not present for " + vertex.getId() + " , " + transformedMap1.get(vertex) + " : ");
            }
        }
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

    private static void getResultsFromTree(IMTree treeFromFileDDO, DirectedGraph graph, int budget, Set<String> targetLabels, Set<String> nonTargetLabels) {
        for(int b=1; b<=budget; b=b+5){
            System.out.println("For Budget : " + b);
            Set<Integer> seedSet = ICTargetMaximizationPruning.findSeedSetFromTree(treeFromFileDDO,b);
            perfromDiffusionWithResultSeedset(graph,targetLabels,nonTargetLabels,seedSet);
        }
    }

    private static DirectedGraph generateGraph(FileDataReader wikiVoteDataReader, float aPercentage) {
        DirectedGraph graph = wikiVoteDataReader.createGraphFromData();
        for (Vertex vertex : graph.getVertices()) {
            vertex.setLabel(new Random().nextFloat() > aPercentage ? "B" : "A");
        }
        return graph;
    }

    private static void wikiGraphTest() {
        FileDataReader wikiVoteDataReader = new UpdatedFileDataReader("updated_ca-GrQc2.txt");
        DirectedGraph graph = wikiVoteDataReader.createGraphFromData();
        for (Vertex vertex : graph.getVertices()) {
            vertex.setLabel(new Random().nextFloat() > 0.9 ? "B" : "A");
        }
        Set<String> targetLabels = new HashSet<>();
        targetLabels.add("A");
        Set<String> nonTargetLabels = new HashSet<>();
        nonTargetLabels.add("B");
        printResults(graph, targetLabels, nonTargetLabels, 10, 10);
        IndependentCascadeModel icmodel = new IndependentCascadeModel();

    }

    private static void simpleGraphTest() {
        DirectedGraph graph = generateDirectedGraph();
        Set<String> targetLabels = new HashSet<>();
        targetLabels.add("A");
        Set<String> nonTargetLabels = new HashSet<>();
        nonTargetLabels.add("B");
        printResults(graph, targetLabels, nonTargetLabels, 2, 4);
    }

    private static void printResults(DirectedGraph graph, Set<String> targetLabels, Set<String> nonTargetLabels, int budget, int nonTargetThreshold) {
        Set<Integer> seedSet = new ICTargetMaximizationPruning().findSeedSet(graph, budget, nonTargetThreshold, targetLabels, nonTargetLabels);
        perfromDiffusionWithResultSeedset(graph, targetLabels, nonTargetLabels, seedSet);
    }

    public static void perfromDiffusionWithResultSeedset(DirectedGraph graph, Set<String> targetLabels, Set<String> nonTargetLabels, Set<Integer> seedSet) {
        IndependentCascadeModel icmodel = new IndependentCascadeModel();
        Integer[] activatedTargetsSim = new Integer[50];
        Integer[] activatedNonTargetsSim = new Integer[50];
        int avgActivatedTargets = 0;
        int avgActivatedNonTargets = 0;
        for (int i = 0; i < 50; i++) {
            Set<Vertex> activatedNodes = icmodel.activeNodesAfterDiffusion(graph, seedSet);
            int targetActivated = 0;
            int nonTargetActivated = 0;
            for (Vertex activatedNode : activatedNodes) {
                if (ICTargetMaximization.isFollwerHasLabel(activatedNode, targetLabels)) {
                    targetActivated++;
                } else if (ICTargetMaximization.isFollwerHasLabel(activatedNode, nonTargetLabels)) {
                    nonTargetActivated++;
                }
            }
            activatedTargetsSim[i] = targetActivated;
            avgActivatedTargets += targetActivated;
            activatedNonTargetsSim[i] = nonTargetActivated;
            avgActivatedNonTargets += nonTargetActivated;
        }

        logger.info("Active target nodes in ICDD " + avgActivatedTargets / 50);
        logger.info("Active non target nodes in ICDD " + avgActivatedNonTargets / 50);
    }

    private static DirectedGraph generateDirectedGraph() {
        DirectedGraph graph = new DirectedGraph();
        HashMap<String, String> properties = new HashMap<>();
        for (int i = 1; i < 12; i++) {
            Vertex iVertex = new Vertex(i);
            properties.put("label", "A");
            iVertex.setProperties(properties);
            graph.addVertex(iVertex);
        }
        int[] labelB = new int[]{7, 9, 11};
        for (int i : labelB) {
            Vertex ithVertex = graph.find(i);
            HashMap<String, String> labelProperties = new HashMap<>();
            labelProperties.put("label", "B");
            ithVertex.setProperties(labelProperties);
            graph.addVertex(ithVertex);
        }
        for (int j = 2; j < 7; j++) {
            graph.addEdge(1, j, 1);
            graph.addEdge(j, 1, 0.1f);
        }
        graph.addEdge(1, 7, 0.1f);
        graph.addEdge(7, 1, 0.1f);
        graph.addEdge(7, 8, 1);
        graph.addEdge(8, 7, 0.1f);
        graph.addEdge(7, 10, 1);
        graph.addEdge(10, 7, 0.1f);
        graph.addEdge(8, 9, 1);
        graph.addEdge(9, 8, 0.1f);
        graph.addEdge(10, 11, 1);
        graph.addEdge(11, 10, 0.1f);
        return graph;
    }
}

package edu.iastate.research.influence.maximization.utilities;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.utilities.FileDataReader;
import edu.iastate.research.graph.utilities.ReadLabelsFromFile;
import edu.iastate.research.influence.maximization.algorithms.MaxTargetInfluentialNode;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.models.IMTree;
import edu.iastate.research.influence.maximization.models.IMTreeNode;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import static edu.iastate.research.influence.maximization.algorithms.MaxTargetInfluentialNode.countTargets;
import static edu.iastate.research.influence.maximization.utilities.SeedSetFromIMTree.findSeedSetInPath;
import static edu.iastate.research.influence.maximization.utilities.SeedSetFromIMTree.getTreeNodesAtDepth;

/**
 * Created by Naresh on 1/28/2017.
 */
public class ResultsFromTree {
    final static Logger logger = Logger.getLogger(ResultsFromTree.class);

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
        System.out.println("Enter the IMTree filename");
        String imTreeFilename = sc.next();
        printResults(filename, probability, percent, budget, nonTargetThreshold, imTreeFilename);

    }

    public static void seedSet(DirectedGraph graph, IMTree imTree, Set<String> targetLabels, int budget, int noOfSimulations) {
        Queue<IMTreeNode> leafNodesAtDepth = getTreeNodesAtDepth(imTree.getRoot(), budget);
        double maxInfluenceSpread = Integer.MIN_VALUE;
        double maxNontargetsActivated = Integer.MIN_VALUE;
        Set<Integer> maxInfluencedSeedSet = new HashSet<>();
        for (IMTreeNode leaf : leafNodesAtDepth) {
            Set<Integer> seedSetInPath = findSeedSetInPath(leaf);
            Set<Integer> activatedSet = IndependentCascadeModel.performDiffusion(graph, seedSetInPath, noOfSimulations, new HashSet<>());
            double influenceSpread = countTargets(activatedSet, graph, targetLabels);
            StringBuilder sb = new StringBuilder("Seed Set: ");
            for (Integer seed : seedSetInPath) {
                sb.append(seed + ",");
            }
            logger.debug(sb.toString());
            logger.debug("Targets Activated " + influenceSpread);
            if (influenceSpread > maxInfluenceSpread) {
                maxInfluenceSpread = influenceSpread;
                maxInfluencedSeedSet = seedSetInPath;
                maxNontargetsActivated = activatedSet.size() - influenceSpread;
            }
        }
        logger.info("Non Targets Activated : " + maxNontargetsActivated);
        StringBuilder sb = new StringBuilder("Seed Set: ");
        for (Integer seed : maxInfluencedSeedSet) {
            sb.append(seed + ",");
        }
        logger.info(sb.toString());
        logger.info("Targets Activated : " + maxInfluenceSpread);


    }

    private static void printResults(String filename, float probability, int percent, int budget, int nonTargetThreshold, String imTreeFilename) {
        FileDataReader wikiVoteDataReader = new FileDataReader(filename, probability);

        Set<String> targetLabels = new HashSet<>();
        targetLabels.add("A");
        Set<String> nonTargetLabels = new HashSet<>();
        nonTargetLabels.add("B");

        DirectedGraph graphWith90PerA = generateGraph(wikiVoteDataReader, ((float) percent) / 100, filename);
        logger.info("***************** Simulating with" + percent + "% A graph ****************");
        printGraphStats(graphWith90PerA, targetLabels, nonTargetLabels);

        IMTree imTree = ReadIMTreeFromFile.read(imTreeFilename);
        for (int i = 41; i <= budget; ) {
 //           findResults(targetLabels, graphWith90PerA, imTree, i);
            seedSet(graphWith90PerA, imTree, targetLabels, i, 10000);
            i = i + 5;
        }

    }

    private static void findResults(Set<String> targetLabels, DirectedGraph graphWith90PerA, IMTree imTree, int i) {
        Set<Integer> seedSet = new SeedSetFromIMTree().findSeedSetFromPath(imTree, i);
        StringBuilder sb = new StringBuilder("Seed Set: ");
        for (Integer seed : seedSet) {
            sb.append(seed + ",");
        }
        logger.info(sb.toString());
        Set<Integer> activatedSet = IndependentCascadeModel.performDiffusion(graphWith90PerA, seedSet, 10000, new HashSet<>());
        int targetsCount = 0;
        int nonTargetsCount = 0;
        for (Integer v : activatedSet) {
            if (graphWith90PerA.find(v).hasLabel(targetLabels)) {
                targetsCount++;
            } else {
                nonTargetsCount++;
            }
        }
        logger.info("Targets Activated : " + targetsCount);
        // logger.info("Non Targets Activated : " + nonTargetsCount);
    }

    private static DirectedGraph generateGraph(FileDataReader wikiVoteDataReader, float aPercentage, String graphFileName) {
        DirectedGraph graph = wikiVoteDataReader.createGraphFromData();
        return new ReadLabelsFromFile().read(graph, graphFileName + "_" + aPercentage + "_labels.txt");
    }

}

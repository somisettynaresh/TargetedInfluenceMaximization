package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.influence.maximization.models.CELFNode;
import edu.iastate.research.influence.maximization.models.CELFNodeComparator;
import edu.iastate.research.influence.maximization.models.CELFNodeWithNonTarget;
import edu.iastate.research.influence.maximization.models.CELFNodeWithNonTargetComparator;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;

import static edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel.performDiffusion;

/**
 * Created by Naresh on 2/27/2017
 */
public class NaiveGreedy {
    final static Logger logger = Logger.getLogger(NaiveGreedy.class);
    private static Set<Integer> prevActivatedSet = new HashSet();
    private static int prevMG = 0;


    public Set<Integer> findSeedSet(DirectedGraph graph, int budget, int nonTargetThreshold, Set<String> targetLabels, Set<String> nonTargetLabels, int noOfSimulations) {
        Set<Integer> seedSet = new LinkedHashSet<>();
        int totalNoTargetsCount = 0;
        PriorityQueue<CELFNodeWithNonTarget> queue = new PriorityQueue<>(new CELFNodeWithNonTargetComparator());
        for (Vertex v : graph.getVertices()) {
            seedSet.add(v.getId());
            Set<Integer> activatedSet = performDiffusion(graph, seedSet, noOfSimulations, new HashSet<>());
            int activeTargetCount = countTargets(activatedSet, graph, targetLabels);
            int activeNonTargetsCount = countTargets(activatedSet, graph, nonTargetLabels);
            if (activeNonTargetsCount < nonTargetThreshold) {
                queue.add(new CELFNodeWithNonTarget(v.getId(), activeTargetCount, 0, activeNonTargetsCount));
            }
            seedSet.remove(v.getId());
        }
        while (seedSet.size() < budget) {
            CELFNodeWithNonTarget top = queue.peek();
            if (top.getEstimatedActivateNontargets() + totalNoTargetsCount > nonTargetThreshold) {
                queue.remove();
            } else {
                if (top.getFlag() == seedSet.size()) {
                    logger.info("Added node " + top.getNode() + " to seed set with activated target nodes: "
                            + top.getMarginalGain() + ", non-targets :" + top.getEstimatedActivateNontargets());
                    seedSet.add(top.getNode());
                    totalNoTargetsCount += top.getEstimatedActivateNontargets();
                    queue.remove();
                    prevActivatedSet = performDiffusion(graph, seedSet, noOfSimulations, new HashSet<>());
                    logger.info("Total Targets activated " + countTargets(prevActivatedSet,graph, targetLabels));
                    logger.info("Total Non-targets activated " + totalNoTargetsCount);
                    logger.info("Queue Size" + queue.size());
                } else {
                    seedSet.add(top.getNode());
                    Set<Integer> activatedSet = performDiffusion(graph, seedSet, noOfSimulations, new HashSet<>());
                    int marginalGainTargetNodes = countTargets(activatedSet, graph, targetLabels) - countTargets(prevActivatedSet, graph, targetLabels);
                    int marginalGainNonTargetNodes = Math.max(0, countTargets(activatedSet, graph, nonTargetLabels) - countTargets(prevActivatedSet, graph, nonTargetLabels));
                    logger.info("Updating Queue for node : " + top.getNode() +
                            " with gain, target : " + marginalGainTargetNodes + " non-targets : " + marginalGainNonTargetNodes);
                    seedSet.remove(top.getNode());
                    queue.remove(top);
                    top.setFlag(seedSet.size());
                    top.setMarginalGain(marginalGainTargetNodes);
                    //  top.setEstimatedActivateNontargets(marginalGainNonTargetNodes);
                    queue.add(top);
                }
            }
        }
        return seedSet;

    }

    public int countTargets(Set<Integer> activatedSet, DirectedGraph graph, Set<String> targetLabels) {
        int targetsCount = 0;
        for (Integer v : activatedSet) {
            if (graph.find(v).hasLabel(targetLabels)) {
                targetsCount++;
            }
        }
        return targetsCount;
    }

}

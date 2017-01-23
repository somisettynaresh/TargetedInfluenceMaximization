package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Naresh on 1/17/2017.
 */
public class IMTRandomDAGEstimatorAndGreedyInfluential extends IMWithTargetLabelsWithPruning {
    @Override
    public Map<Integer, Integer> estimateNonTargetsByNode(DirectedGraph graph, Set<String> nonTargetLabels, int noOfSimulations) {
        return new EstimateNonTargetsUsingRandomDAG().estimate(graph, nonTargetLabels, noOfSimulations);
    }

    @Override
    public NodeWithInfluence findMaxInfluentialNode(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        return new MaxTargetInfluentialNodeUsingGreedy().find(graph, nodes, seedSet, targetLabels, noOfSimulations);
    }
}

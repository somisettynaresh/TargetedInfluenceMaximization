package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;

import java.util.Map;
import java.util.Set;

import static edu.iastate.research.influence.maximization.algorithms.MaxTargetInfluentialNodeWithRandomDAG.getInstance;

/**
 * Created by Naresh on 1/23/2017.
 */
public class IMTRandomDAGEstimatorAndRandomDAG extends IMWithTargetLabelsWithPruning {
    @Override
    public Map<Integer, Integer> estimateNonTargetsByNode(DirectedGraph graph, Set<String> nonTargetLabels, int noOfSimulations) {
        return new EstimateNonTargetsUsingRandomDAG().estimate(graph, nonTargetLabels, noOfSimulations);
    }

    @Override
    public NodeWithInfluence findMaxInfluentialNode(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        return getInstance(graph, targetLabels, noOfSimulations).find(graph, nodes, seedSet, targetLabels, noOfSimulations);
    }
}

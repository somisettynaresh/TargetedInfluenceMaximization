package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;

import java.util.Map;
import java.util.Set;

/**
 * Created by Naresh on 1/16/2017.
 */
public abstract class EstimateNonTargets {
    public abstract Map<Integer, Integer> estimate(DirectedGraph graph, Set<String> nonTargetLabels, int noOfSimulations);

    public int countNonTargets(Set<Integer> activatedSet, DirectedGraph graph, Set<String> nonTargetLabels) {
        int nonTargetsCount = 0;
        for (Integer v : activatedSet) {
            if(graph.find(v).hasLabel(nonTargetLabels)) {
                nonTargetsCount++;
            }
        }
        return nonTargetsCount;
    }
}

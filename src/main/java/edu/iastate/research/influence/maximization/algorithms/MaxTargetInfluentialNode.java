package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;

import java.util.Set;

/**
 * Created by Naresh on 1/17/2017.
 */
public abstract class MaxTargetInfluentialNode {

    public abstract NodeWithInfluence find(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations);

    public int countNonTargets(Set<Integer> activatedSet, DirectedGraph graph, Set<String> targetLabels) {
        int targetsCount = 0;
        for (Integer v : activatedSet) {
            if(graph.find(v).hasLabel(targetLabels)) {
                targetsCount++;
            }
        }
        return targetsCount;
    }
}

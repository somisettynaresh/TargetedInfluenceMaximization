package edu.iastate.research.influence.maximization.diffusion;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.utilities.MapUtil;

import java.util.*;

import static edu.iastate.research.graph.utilities.MapUtil.shrinkMapBySize;
import static edu.iastate.research.graph.utilities.MapUtil.sortByValue;

/**
 * Created by Naresh on 2/23/2016.
 */
public class IndependentCascadeModel {

    public static Set<Integer> performDiffusion(DirectedGraph graph, Set<Integer> seedSet, int noOfSimulations, Set<Integer> alreadyActivated) {
        Map<Integer, Integer> activatedNodeFrequencyMap = new HashMap<>();
        int totalActiveSetSize = 0;
        for (int i = 0; i < noOfSimulations; i++) {
            Set<Integer> activeSet = singleDiffusion(graph, seedSet, alreadyActivated);
            totalActiveSetSize += activeSet.size();
            for (Integer vertex : activeSet) {
                int currentFrequency = 0;
                if (activatedNodeFrequencyMap.containsKey(vertex)) {
                    currentFrequency = activatedNodeFrequencyMap.get(vertex);
                }
                currentFrequency++;
                activatedNodeFrequencyMap.put(vertex, currentFrequency);
            }
        }
        int avgActiveSetSize = (int) Math.ceil(((float) totalActiveSetSize) / noOfSimulations);
        return shrinkMapBySize(sortByValue(activatedNodeFrequencyMap), avgActiveSetSize).keySet();
    }

    private static Set<Integer> singleDiffusion(DirectedGraph graph, Set<Integer> seedSet, Set<Integer> alreadyActivated) {
        Set<Integer> active = new HashSet<>(alreadyActivated);
        Stack<Vertex> target = new Stack<>(); //will store unprocessed nodes during intermediate time
        for (Integer sId : seedSet) {
            Vertex s = graph.find(sId);
            if (!active.contains(s)) {
                target.push(s);
            }
            while (target.size() > 0) {
                Vertex node = target.pop();
                if (node != null) {
                    active.add(node.getId());
                    for (Vertex follower : node.getOutBoundNeighbours()) {
                        float randnum = new Random().nextFloat();
                        if (randnum <= node.getPropagationProbability(follower)) {
                            if (!active.contains(follower.getId())) {
                                target.push(follower);
                            }
                        }
                    }
                }
            }
        }
        return active;
    }
}

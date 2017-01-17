package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.utilities.MapUtil;
import edu.iastate.research.graph.utilities.WriteObject;
import org.apache.log4j.Logger;

import java.util.*;

import static edu.iastate.research.graph.utilities.MapUtil.shrinkMapBySize;
import static edu.iastate.research.graph.utilities.MapUtil.sortByValue;

/**
 * Created by Naresh on 1/16/2017.
 */
public class EstimateNonTargetsUsingRandomDAG extends EstimateNonTargets {
    final static Logger logger = Logger.getLogger(EstimateNonTargetsUsingRandomDAG.class);

    @Override
    public Map<Integer, Integer> estimate(DirectedGraph graph, Set<String> nonTargetLabels, int noOfSimulations) {
        List<Map<Integer, Integer>> nonTargetsActivatedMapDAGList = new ArrayList<>();
        for (int i = 0; i < noOfSimulations; i++) {
            DirectedGraph dag = createDAG(graph);
            logger.debug("Created DAG : " + i);
            Map<Integer, Integer> nonTargetMapInDAG = findNonTargetsInDAG(dag, nonTargetLabels);
            nonTargetsActivatedMapDAGList.add(nonTargetMapInDAG);
        }
        Map<Integer, Integer> estimatedNonTargetMap = normalizeResults(nonTargetsActivatedMapDAGList, noOfSimulations);

        String filename = UUID.randomUUID().toString() + "-non-targets-map.data";
        logger.debug("Writing Estimated Non Targets Map to file " + filename);
        WriteObject.writeToFile(estimatedNonTargetMap, filename);

        return estimatedNonTargetMap;
    }

    private Map<Integer, Integer> normalizeResults(List<Map<Integer, Integer>> nonTargetsActivatedMapDAGList, int noOfSimulations) {
        Map<Integer, Integer> estimatedActiveNonTargetMap = new HashMap<>();
        for (Map<Integer, Integer> nonTargetsMapInDAG : nonTargetsActivatedMapDAGList) {
            for (Integer vertex : nonTargetsMapInDAG.keySet()) {
                int aggregateNonTargetsActivated = 0;
                if (estimatedActiveNonTargetMap.containsKey(vertex)) {
                    aggregateNonTargetsActivated = estimatedActiveNonTargetMap.get(vertex);
                }
                aggregateNonTargetsActivated += nonTargetsMapInDAG.get(vertex);
                estimatedActiveNonTargetMap.put(vertex, aggregateNonTargetsActivated);
            }
        }
        for (Integer vertex : estimatedActiveNonTargetMap.keySet()) {
            int avgNontargetsActivated = Math.round ((float) estimatedActiveNonTargetMap.get(vertex) / noOfSimulations);
            estimatedActiveNonTargetMap.put(vertex, avgNontargetsActivated);
        }
        return estimatedActiveNonTargetMap;
    }

    private Map<Integer, Integer> findNonTargetsInDAG(DirectedGraph dag, Set<String> nonTargetLabels) {
        Map<Integer, Set<Integer>> reachableCache = new HashMap<>();
        Map<Integer, Integer> nonTargetsActivated = new HashMap<>();
        for (Vertex v : dag.getVertices()) {
            Set<Integer> reachableSet = new HashSet<>();
            Queue<Integer> bfsQueue = new LinkedList<>();
            bfsQueue.add(v.getId());
            reachableSet.add(v.getId());
            while (!bfsQueue.isEmpty()) {
                int node = bfsQueue.remove();
                if (reachableCache.containsKey(node)) {
                        reachableSet.addAll(reachableCache.get(node));
                    } else {
                        for (Vertex vOut : dag.find(node).getOutBoundNeighbours()) {
                            if(!reachableSet.contains(vOut.getId())) {
                                bfsQueue.add(vOut.getId());
                                reachableSet.add(vOut.getId());
                            }
                        }
                    }
            }
            reachableCache.put(v.getId(), reachableSet);
            nonTargetsActivated.put(v.getId(), countNonTargets(reachableSet, dag, nonTargetLabels));
        }
        return nonTargetsActivated;

    }


    private DirectedGraph createDAG(DirectedGraph graph) {
        DirectedGraph clonedGraph = graph.copyVertices();
        for (Vertex v : graph.getVertices()) {
            for (Vertex vOut : v.getOutBoundNeighbours()) {
                if (!(new Random().nextFloat() < (1 - v.getPropagationProbability(vOut)))) {
                    clonedGraph.addEdge(v.getId(), vOut.getId(), v.getPropagationProbability(vOut));
                }
            }
        }
        return clonedGraph;
    }
}

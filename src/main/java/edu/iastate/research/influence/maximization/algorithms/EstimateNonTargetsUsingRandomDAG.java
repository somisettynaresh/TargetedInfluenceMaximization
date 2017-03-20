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
        Map<Integer, Integer> aggregatedNonTargetsMap = new HashMap<>();
        for (int i = 0; i < noOfSimulations; i++) {
            DirectedGraph dag = createDAG(graph);
            logger.info("Created DAG : " + i);

            findNonTargetsInDAG(dag, nonTargetLabels, aggregatedNonTargetsMap);
        }
        Map<Integer, Integer> estimatedNonTargetMap = avgResults(aggregatedNonTargetsMap, noOfSimulations);

        String filename = UUID.randomUUID().toString() + "-non-targets-map.data";
        logger.info("Writing Estimated Non Targets Map to file " + filename);
        WriteObject.writeToFile(estimatedNonTargetMap, filename);

        return estimatedNonTargetMap;
    }

    private Map<Integer, Integer> avgResults(Map<Integer, Integer> aggregatedNonTargetsMap, int noOfSimulations) {
        Map<Integer, Integer> normalizedNonTargetMap = new HashMap<>();
        for (Integer node : aggregatedNonTargetsMap.keySet()) {
            normalizedNonTargetMap.put(node, Math.round(((float) aggregatedNonTargetsMap.get(node)) / noOfSimulations));
        }
        return normalizedNonTargetMap;
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
            int avgNontargetsActivated = Math.round((float) estimatedActiveNonTargetMap.get(vertex) / noOfSimulations);
            estimatedActiveNonTargetMap.put(vertex, avgNontargetsActivated);
        }
        return estimatedActiveNonTargetMap;
    }

    private void findNonTargetsInDAG(DirectedGraph dag, Set<String> nonTargetLabels, Map<Integer, Integer> aggregatedNonTargetsMap) {
        Map<Integer, Set<Integer>> reachableCache = new HashMap<>();
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
                        if (!reachableSet.contains(vOut.getId())) {
                            bfsQueue.add(vOut.getId());
                            reachableSet.add(vOut.getId());
                        }
                    }
                }
            }
            reachableCache.put(v.getId(), reachableSet);
            int prevNonTargetCount = 0;
            if (aggregatedNonTargetsMap.containsKey(v.getId())) {
                prevNonTargetCount = aggregatedNonTargetsMap.get(v.getId());
            }
            aggregatedNonTargetsMap.put(v.getId(), prevNonTargetCount + countNonTargets(reachableSet, dag, nonTargetLabels));
        }
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

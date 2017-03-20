package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;
import org.apache.log4j.Logger;

import java.util.*;

import static edu.iastate.research.graph.utilities.MapUtil.shrinkMapBySize;
import static edu.iastate.research.graph.utilities.MapUtil.sortByValue;

/**
 * Created by Naresh on 1/23/2017.
 */
public class MaxTargetInfluentialNodeWithRandomDAG extends MaxTargetInfluentialNode {
    final static Logger logger = Logger.getLogger(MaxTargetInfluentialNodeWithRandomDAG.class);

    private static MaxTargetInfluentialNodeWithRandomDAG instance = null;
    private Map<Integer, Set<Integer>> reachableTargetNodesMap = new HashMap<>();

    private MaxTargetInfluentialNodeWithRandomDAG() {

    }

    private MaxTargetInfluentialNodeWithRandomDAG(DirectedGraph graph, Set<String> targetLabels, int noOfSimulations) {
        Map<Integer, Map<Integer, Integer>> aggregateReachableSetInDag = new HashMap<>();
        Map<Integer, Integer> aggregateSizeOfReachableTargets = new HashMap<>();
        for (int i = 0; i < noOfSimulations; i++) {
            DirectedGraph dag = createDAG(graph);
            logger.info("Created DAG - " + i);
            processDAG(dag, targetLabels, aggregateReachableSetInDag, aggregateSizeOfReachableTargets);
            logger.debug("Processed DAG - " + i);
        }
        reachableTargetNodesMap = normalizeResults(aggregateReachableSetInDag, aggregateSizeOfReachableTargets, noOfSimulations);
        logger.debug("Reachable Target Nodes Map is Created");
    }

    private Map<Integer, Set<Integer>> normalizeResults(Map<Integer, Map<Integer, Integer>> aggregateReachableSetInDag, Map<Integer, Integer> aggregateSizeOfReachableTargets, int noOfSimulations) {
        Map<Integer, Set<Integer>> result = new HashMap<>();
        for (Integer node : aggregateReachableSetInDag.keySet()) {
            Map<Integer, Integer> aggregateReachableNodeFrequencyMap = aggregateReachableSetInDag.get(node);
            int avgReachableTargetsSize = Math.round((float) aggregateSizeOfReachableTargets.get(node) / noOfSimulations);
            Set<Integer> avgReachableSet = shrinkMapBySize(sortByValue(aggregateReachableNodeFrequencyMap), avgReachableTargetsSize).keySet();
            result.put(node, avgReachableSet);
        }
        return result;
    }

    private void processDAG(DirectedGraph dag, Set<String> targetLabels, Map<Integer, Map<Integer, Integer>> aggregateReachableSetInDag, Map<Integer, Integer> aggregateSizeOfReachableTargets) {
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
            Map<Integer, Integer> aggregateReachableNodesForNode = new HashMap<>();
            int reachableNodesCount = 0;
            for (Integer reachableNode : reachableSet) {
                if (dag.find(reachableNode).hasLabel(targetLabels)) {
                    reachableNodesCount++;
                    if (aggregateReachableSetInDag.containsKey(v.getId())) {
                        aggregateReachableNodesForNode = aggregateReachableSetInDag.get(v.getId());
                    }
                    int currentFrequencyOFReachableNode = 0;
                    if (aggregateReachableNodesForNode.containsKey(reachableNode)) {
                        currentFrequencyOFReachableNode = aggregateReachableNodesForNode.get(reachableNode);
                    }
                    currentFrequencyOFReachableNode++;
                    aggregateReachableNodesForNode.put(reachableNode, currentFrequencyOFReachableNode);
                }
            }
                aggregateReachableSetInDag.put(v.getId(), aggregateReachableNodesForNode);
                int aggregateReachableSetSize = 0;
                if (aggregateSizeOfReachableTargets.containsKey(v.getId())) {
                    aggregateReachableSetSize = aggregateSizeOfReachableTargets.get(v.getId());
                }
                aggregateReachableSetSize += reachableNodesCount;
                aggregateSizeOfReachableTargets.put(v.getId(), aggregateReachableSetSize);
        }

    }

    public static MaxTargetInfluentialNodeWithRandomDAG getInstance(DirectedGraph graph, Set<String> targetLabels, int noOfSimulations) {
        if (instance == null) {
            instance = new MaxTargetInfluentialNodeWithRandomDAG(graph, targetLabels, noOfSimulations);
        }
        return instance;
    }

    @Override
    public List<NodeWithInfluence> find(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        int maxMarginalInfluenceSpread = Integer.MIN_VALUE;
        int maxInfluentialNode = Integer.MIN_VALUE;
        Set<Integer> reachableTargetNodesWithSeedSet = new HashSet<>();
        for (Integer seed : seedSet) {

            reachableTargetNodesWithSeedSet.addAll(reachableTargetNodesMap.get(seed));
        }
        for (Integer node : nodes) {
            if (!seedSet.contains(node)) {
                int marginalInfluenceSpread = calculateMarginalInfluence(reachableTargetNodesWithSeedSet, reachableTargetNodesMap.get(node));
                if (marginalInfluenceSpread > maxMarginalInfluenceSpread) {
                    maxMarginalInfluenceSpread = marginalInfluenceSpread;
                    maxInfluentialNode = node;
                }

            }
        }
        logger.debug("Maximum influential Node is " + maxInfluentialNode + " and influence is " + maxMarginalInfluenceSpread);
        List<NodeWithInfluence> nodeWithMaxInfluence = new ArrayList<>();
        nodeWithMaxInfluence.add(new NodeWithInfluence(maxInfluentialNode, maxMarginalInfluenceSpread));
        return nodeWithMaxInfluence;
    }

    private int calculateMarginalInfluence(Set<Integer> reachableTargetNodesWithSeedSet, Set<Integer> reachableTargetsFromNode) {
        int marginalInfluenceSpread = 0;
        for (Integer reachableTarget : reachableTargetsFromNode) {
            if(!reachableTargetNodesWithSeedSet.contains(reachableTarget)) {
                marginalInfluenceSpread++;
            }
        }
        return marginalInfluenceSpread;
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

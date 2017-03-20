package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.utilities.WriteObject;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

import static edu.iastate.research.graph.utilities.MapUtil.shrinkMapBySize;
import static edu.iastate.research.graph.utilities.MapUtil.sortByValue;

/**
 * Created by Naresh on 1/23/2017.
 */
public class MaxTargetInfluentialNodeWithRandomDAG2 extends MaxTargetInfluentialNode {
    final static Logger logger = Logger.getLogger(MaxTargetInfluentialNodeWithRandomDAG2.class);

    private static MaxTargetInfluentialNodeWithRandomDAG2 instance = null;
    private List<String> dagsReachableSetMap = new ArrayList<>();
    private Map<Integer, Set<Integer>> reachableTargetNodesMap = new HashMap<>();
    private static final int BATCH_SIZE = 500;

    private MaxTargetInfluentialNodeWithRandomDAG2() {

    }

    private MaxTargetInfluentialNodeWithRandomDAG2(DirectedGraph graph, Set<String> targetLabels, int noOfSimulations) {
        Map<Integer, Map<Integer, Integer>> aggregateReachableSetInDag = new HashMap<>();
        Map<Integer, Integer> aggregateSizeOfReachableTargets = new HashMap<>();
        String baseFileName = "tmp";
        String dagReachbleMapFileName = "";
        List<Map<Integer, Set<Integer>>> reachableSetDagList = new ArrayList<>();
        for (int i = 0; i < noOfSimulations; i++) {
            DirectedGraph dag = createDAG(graph);
            logger.debug("Created DAG - " + i);
            dagReachbleMapFileName = baseFileName + "\\" + i + "-dag.data";
            Map<Integer, Set<Integer>> reachableSetMapForDAG = processDAG(dag);
            if (reachableSetDagList.size() == BATCH_SIZE) {
                WriteObject.writeToFile(reachableSetDagList, dagReachbleMapFileName);
                dagsReachableSetMap.add(dagReachbleMapFileName);
                reachableSetDagList = new ArrayList<>();
            }
            reachableSetDagList.add(reachableSetMapForDAG);
            logger.debug("Processed DAG - " + i);
        }
        WriteObject.writeToFile(reachableSetDagList, dagReachbleMapFileName);
        dagsReachableSetMap.add(dagReachbleMapFileName);
        //reachableTargetNodesMap = normalizeResults(aggregateReachableSetInDag, aggregateSizeOfReachableTargets, noOfSimulations);
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

    private Map<Integer, Set<Integer>> processDAG(DirectedGraph dag) {
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
        }
        return filterItSelf(reachableCache);
    }

    private Map<Integer, Set<Integer>> filterItSelf(Map<Integer, Set<Integer>> reachableCache) {
        Map<Integer, Set<Integer>> reachableMap = new HashMap<>();
        for (Integer node : reachableCache.keySet()) {
            Set<Integer> reachableNodes = reachableCache.get(node);
            reachableNodes.remove(node);
            if (reachableNodes.size() > 0) {
                reachableMap.put(node, reachableNodes);
            }
        }
        return reachableMap;
    }

    public static MaxTargetInfluentialNodeWithRandomDAG2 getInstance(DirectedGraph graph, Set<String> targetLabels, int noOfSimulations) {
        if (instance == null) {
            instance = new MaxTargetInfluentialNodeWithRandomDAG2(graph, targetLabels, noOfSimulations);
        }
        return instance;
    }

    private List<Map<Integer, Set<Integer>>> reachableMapFromFile(String filename) {
        List<Map<Integer, Set<Integer>>> reachableMap = new ArrayList<>();
        InputStream fin = null;
        try {
            fin = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fin);
            reachableMap = (List<Map<Integer, Set<Integer>>>) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reachableMap;
    }

    @Override
    public List<NodeWithInfluence> find(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        List<NodeWithInfluence> nodesWithMaxInfluence = new ArrayList<>();
        Map<Integer, Double> nodesInfluenceMap = new HashMap<>();
        for (String dagFileName : dagsReachableSetMap) {
            List<Map<Integer, Set<Integer>>> dagReachableMapList = reachableMapFromFile(dagFileName);
            for (Map<Integer, Set<Integer>> dagReachableMap : dagReachableMapList) {

                Set<Integer> nodesReachableFromSeedSet = new HashSet<>();
                for (Integer seed : seedSet) {
                    if (dagReachableMap.containsKey(seed)) {
                        nodesReachableFromSeedSet.addAll(dagReachableMap.get(seed));
                    }
                }
                for (Integer node : nodes) {
                    double marginalInfluence = 0;
                    if (nodesInfluenceMap.containsKey(node)) {
                        marginalInfluence = nodesInfluenceMap.get(node);
                    }
                    marginalInfluence = marginalInfluence + findMarginalInfluence(dagReachableMap, node, nodesReachableFromSeedSet, targetLabels, graph);
                    nodesInfluenceMap.put(node, marginalInfluence);
                }
            }
        }
        nodesWithMaxInfluence = findMaxInfluentialNodes(nodesInfluenceMap, noOfSimulations);
        return nodesWithMaxInfluence;
    }

    private List<NodeWithInfluence> findMaxInfluentialNodes(Map<Integer, Double> nodesInfluenceMap, int noOfSimulations) {
        List<NodeWithInfluence> maxInfluentialNodes = new ArrayList<>();
        double maxMarginalInfluence = Integer.MIN_VALUE;
        for (Integer node : nodesInfluenceMap.keySet()) {
            if (maxMarginalInfluence < nodesInfluenceMap.get(node)) {
                maxMarginalInfluence = nodesInfluenceMap.get(node);
            }
        }

        for (Integer node : nodesInfluenceMap.keySet()) {
            if (maxMarginalInfluence == nodesInfluenceMap.get(node)) {
                maxInfluentialNodes.add(new NodeWithInfluence(node, maxMarginalInfluence / noOfSimulations));
            }
        }
        return maxInfluentialNodes;
    }

    private double findMarginalInfluence(Map<Integer, Set<Integer>> dagReachableMap, Integer node, Set<Integer> nodesReachableFromSeedSet, Set<String> targetLabels, DirectedGraph graph) {
        Set<Integer> nodesReachable = new HashSet<>();
        if (dagReachableMap.containsKey(node)) {
            nodesReachable = dagReachableMap.get(node);
        }
        nodesReachable.removeAll(nodesReachableFromSeedSet);
        double marginalInfluence = 0;
        for (Integer reachableNode : nodesReachable) {
            if (graph.find(reachableNode).hasLabel(targetLabels)) {
                marginalInfluence = marginalInfluence + 1;
            }

        }
        return marginalInfluence;
    }


    private int calculateMarginalInfluence(Set<Integer> reachableTargetNodesWithSeedSet, Set<Integer> reachableTargetsFromNode) {
        int marginalInfluenceSpread = 0;
        for (Integer reachableTarget : reachableTargetsFromNode) {
            if (!reachableTargetNodesWithSeedSet.contains(reachableTarget)) {
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

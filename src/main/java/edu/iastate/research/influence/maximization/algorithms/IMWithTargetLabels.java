package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.utilities.WriteObject;
import edu.iastate.research.influence.maximization.models.IMTree;
import edu.iastate.research.influence.maximization.models.IMTreeNode;
import edu.iastate.research.influence.maximization.models.IMTreeSeedSet;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;
import edu.iastate.research.influence.maximization.utilities.ReadNonTargetsEstimationFromFile;
import edu.iastate.research.influence.maximization.utilities.SeedSetFromIMTree;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Naresh on 1/17/2017.
 */
public abstract class IMWithTargetLabels {

    static IMTreeNode maxLeaf;

    final static Logger logger = Logger.getLogger(IMWithTargetLabels.class);

    /**
     * Getter for property 'maxInfluenceTree'.
     *
     * @return Value for property 'maxInfluenceTree'.
     */
    public IMTree getMaxInfluenceTree() {
        return maxInfluenceTree;
    }

    public IMTree maxInfluenceTree;

    public Map<Integer, Set<Integer>> nonTargetsEstimateMap;

    public abstract Map<Integer, Integer> estimateNonTargetsByNode(DirectedGraph graph, Set<String> nonTargetLabels, int noOfSimulations);

    public Map<Integer, Set<Integer>> estimateNonTargets(DirectedGraph graph, Set<String> nonTargetLabels, int noOfSimulations) {
        Map<Integer, Set<Integer>> nonTargetsEstimatedMapByCount = new HashMap<>();
        Map<Integer, Integer> nonTargetEstimatesByNode = estimateNonTargetsByNode(graph, nonTargetLabels, noOfSimulations);
        for (Integer v : nonTargetEstimatesByNode.keySet()) {
            Set<Integer> nodesWithNonTargetCount = new HashSet<>();
            Integer nonTargetCount = nonTargetEstimatesByNode.get(v);
            if (nonTargetsEstimatedMapByCount.containsKey(nonTargetCount)) {
                nodesWithNonTargetCount = nonTargetsEstimatedMapByCount.get(nonTargetCount);
            }
            nodesWithNonTargetCount.add(v);
            nonTargetsEstimatedMapByCount.put(nonTargetCount, nodesWithNonTargetCount);
        }
        return nonTargetsEstimatedMapByCount;
    }

    public Map<Integer, Set<Integer>> estimateNonTargetsFromFile(String filename) {
        return new ReadNonTargetsEstimationFromFile().read(filename);
    }


    public abstract List<NodeWithInfluence> findMaxInfluentialNode(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations);

    public void constructIMTree(DirectedGraph graph, int budget, int nonTargetThreshold, Set<String> targetLabels, Set<String> nonTargetLabels, int noOfSimulations, String nonTargetsEstimateFilename, String experimentName) {
        int depth = 0;
        maxInfluenceTree = new IMTree();
        IMTreeNode root = maxInfluenceTree.getRoot();
        maxLeaf = root;
        Queue<IMTreeNode> firstQueue = new LinkedList<>();
        Queue<IMTreeNode> secondQueue = new LinkedList<>();
        firstQueue.add(root);
        if (nonTargetsEstimateFilename != "") {
            nonTargetsEstimateMap = estimateNonTargetsFromFile(nonTargetsEstimateFilename);
        } else {
            nonTargetsEstimateMap = estimateNonTargets(graph, nonTargetLabels, noOfSimulations);
        }
        init(graph, targetLabels, noOfSimulations);
        while (!((firstQueue.isEmpty() && secondQueue.isEmpty()) || depth >= budget)) {

            if (!firstQueue.isEmpty()) {
                depth++;
            }
            logger.info("Start Process the tree for level :" + depth);
            processTreeLevel(graph, nonTargetThreshold, targetLabels, nonTargetLabels, secondQueue, firstQueue, nonTargetsEstimateMap, noOfSimulations);
            logger.info("End Process the tree for level :" + depth);
            if (depth >= budget) {
                break;
            } else {
                if (!secondQueue.isEmpty()) {
                    depth++;
                }
                logger.info("Start Process the tree for level :" + depth);
                processTreeLevel(graph, nonTargetThreshold, targetLabels, nonTargetLabels, firstQueue, secondQueue, nonTargetsEstimateMap, noOfSimulations);
                logger.info("End Process the tree for level :" + depth);
            }
        }
        logger.info("IMTTree is created");
        String treeFile = "tree-" + experimentName + new Random().nextInt() + ".data";
        WriteObject.writeToFile(maxInfluenceTree, treeFile);
        logger.info("Influence Maximization Tree :" + treeFile);
    }

    public Set<Integer> findSeedSet(DirectedGraph graph, int budget, int nonTargetThreshold, Set<String> targetLabels, Set<String> nonTargetLabels, int noOfSimulations, String nonTargetsEstimateFilename, String experimentName) {
        constructIMTree(graph, budget, nonTargetThreshold, targetLabels, nonTargetLabels, noOfSimulations, nonTargetsEstimateFilename, experimentName);
        return new SeedSetFromIMTree().findSeedSetFromPath(maxInfluenceTree, budget);
    }

    public List<IMTreeSeedSet> findCandidateSeedSets(DirectedGraph graph, int budget, int nonTargetThreshold, Set<String> targetLabels, Set<String> nonTargetLabels, int noOfSimulations, String nonTargetsEstimateFilename, String experimentName) {
        constructIMTree(graph, budget, nonTargetThreshold, targetLabels, nonTargetLabels, noOfSimulations, nonTargetsEstimateFilename, experimentName);
        return IMTreeSeedSelector.findSeedSets(graph, maxInfluenceTree, budget, nonTargetThreshold);
    }

    public void init(DirectedGraph graph, Set<String> targetLabels, int noOfSimulations) {

    }

    void processTreeLevel(DirectedGraph graph, int nonTargetThreshold, Set<String> targetLabels, Set<String> nonTargetLabels, Queue<IMTreeNode> firstQueue, Queue<IMTreeNode> secondQueue, Map<Integer, Set<Integer>> nonTargetsEstimateMap, int noOfSimulations) {
        IMTreeNode current;
        logger.info("Number of nodes at level : " + secondQueue.size());
        while (!secondQueue.isEmpty()) {
            current = secondQueue.remove();
            logger.debug("Processing TreeNode " + current.getNode());
            Set<Integer> seedSetInPath = findSeedSetInPath(current);
            int currentNonThresholdCount = countNonTargetsActivatedInPath(current);
            logger.debug("Total NonActive nodes till this tree node " + currentNonThresholdCount);
            for (int i = 0; i <= nonTargetThreshold - currentNonThresholdCount; i++) {
                if (nonTargetsEstimateMap.containsKey(i)) {
                    logger.debug("Finding best child node for non target count " + i);
                    for (NodeWithInfluence maxInfluentialNode : findMaxInfluentialNode(graph, nonTargetsEstimateMap.get(i), seedSetInPath, targetLabels, noOfSimulations)) {
                        IMTreeNode childNode = new IMTreeNode(maxInfluentialNode, i, current);
                        logger.debug("Adding child node " + childNode.getNode() + " with Target influence Spread " + childNode.getActiveTargets() + " non Targets : " + childNode.getActiveNonTargets());
                        current.addChild(childNode);
                        firstQueue.add(childNode);
                    }
                }
            }
        }
    }

    static Set<Integer> findSeedSetInPath(IMTreeNode current) {
        Set<Integer> nodesInPath = new HashSet<>();
        while (current.getParent() != null) {
            if (current.getNode() != -1) {
                nodesInPath.add(current.getNode());
            }
            current = current.getParent();
        }
        return nodesInPath;
    }

    static Integer countNonTargetsActivatedInPath(IMTreeNode current) {
        int nonTargetsActivated = 0;
        while (current.getParent() != null) {
            if (current.getNode() != -1) {
                nonTargetsActivated += current.getActiveNonTargets();
            }
            current = current.getParent();
        }
        return nonTargetsActivated;
    }

    static Double countTargetsActivatedInPath(IMTreeNode current) {
        double targetsActivated = 0;
        while (current.getParent() != null) {
            if (current.getNode() != -1) {
                targetsActivated += current.getActiveTargets();
            }
            current = current.getParent();
        }
        return targetsActivated;
    }


}
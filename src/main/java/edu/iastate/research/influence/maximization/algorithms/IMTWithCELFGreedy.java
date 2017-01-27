package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.influence.maximization.models.*;

import java.util.*;
import java.util.function.Predicate;

import static edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel.performDiffusion;

/**
 * Created by Naresh on 1/23/2017.
 */
public class IMTWithCELFGreedy extends IMWithTargetLabels {
    @Override
    public Map<Integer, Integer> estimateNonTargetsByNode(DirectedGraph graph, Set<String> nonTargetLabels, int noOfSimulations) {
        return null;
    }

    @Override
    public NodeWithInfluence findMaxInfluentialNode(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        return null;
    }

    @Override
    public void init(DirectedGraph graph, Set<String> targetLabels, int noOfSimulations) {
        Set<Integer> seedSet = new HashSet<>();
        PriorityQueue<CELFNodeWithNonTarget> queue = new PriorityQueue<>(new CELFNodeWithNonTargetComparator());
        for (Vertex v : graph.getVertices()) {
            seedSet.add(v.getId());
            Set<Integer> activatedSet = performDiffusion(graph, seedSet, noOfSimulations, new HashSet<>());
            int activeTargetCount = countTargets(activatedSet, graph, targetLabels);
            queue.add(new CELFNodeWithNonTarget(v.getId(), activeTargetCount, 0, findNonTargetEstimate(v.getId())));
            seedSet.remove(v.getId());
        }
        maxInfluenceTree.getRoot().setQueue(queue);
    }

    private int findNonTargetEstimate(int node) {
        for (Integer nonTargetActiveCount : nonTargetsEstimateMap.keySet()) {
            if (nonTargetsEstimateMap.get(nonTargetActiveCount).contains(node)) {
                return nonTargetActiveCount;
            }
        }
        return Integer.MAX_VALUE;
    }

    public int countTargets(Set<Integer> activatedSet, DirectedGraph graph, Set<String> targetLabels) {
        int targetsCount = 0;
        for (Integer v : activatedSet) {
            if (graph.find(v).hasLabel(targetLabels)) {
                targetsCount++;
            }
        }
        return targetsCount;
    }

    @Override
    void processTreeLevel(DirectedGraph graph, int nonTargetThreshold, Set<String> targetLabels, Set<String> nonTargetLabels, Queue<IMTreeNode> firstQueue, Queue<IMTreeNode> secondQueue, Map<Integer, Set<Integer>> nonTargetsEstimateMap, int noOfSimulations) {
        IMTreeNode current;
        logger.info("Number of nodes at level : " + secondQueue.size());
        Map<Integer, IMTreeNode> maxTreeChildNodeByNotTargetCount = new HashMap<>();
        while (!secondQueue.isEmpty()) {
            current = secondQueue.remove();
            logger.debug("Processing TreeNode " + current.getNode());
            Set<Integer> seedSetInPath = findSeedSetInPath(current);
            int currentNonThresholdCount = countNonTargetsActivatedInPath(current);
            int currentTargetsActivated = countTargetsActivatedInPath(current);
            Set<Integer> prevActivatedSet = performDiffusion(graph, seedSetInPath, noOfSimulations, new HashSet<>());
            //logger.info("Total NonActive nodes till this tree node "+ currentNonThresholdCount);
            Predicate<CELFNodeWithNonTarget> celfNodePredicate = p -> prevActivatedSet.contains(p.getNode());
            PriorityQueue<CELFNodeWithNonTarget> queue = current.getQueue();
            queue.removeIf(celfNodePredicate);
            for (int i = 0; i <= nonTargetThreshold - currentNonThresholdCount; i++) {
                IMTreeNode childNode = findMaxChildNode(graph, targetLabels, seedSetInPath, current, i, prevActivatedSet, noOfSimulations);
                if (childNode != null) {
                    if (maxTreeChildNodeByNotTargetCount.containsKey(currentNonThresholdCount + i)) {
                        int currentMax = maxTreeChildNodeByNotTargetCount.get(currentNonThresholdCount + i).getActiveTargets() +
                                countTargetsActivatedInPath(maxTreeChildNodeByNotTargetCount.get(currentNonThresholdCount + i).getParent());
                        if (currentTargetsActivated + childNode.getActiveTargets() > currentMax) {
                            maxTreeChildNodeByNotTargetCount.put(currentNonThresholdCount + i, childNode);
                        }
                    } else {
                        maxTreeChildNodeByNotTargetCount.put(currentNonThresholdCount + i, childNode);
                    }
                }
            }
        }
        for (IMTreeNode childNode : maxTreeChildNodeByNotTargetCount.values()) {
            IMTreeNode parent = childNode.getParent();
            logger.debug("Adding child node " + childNode.getNode() + " with Target influence Spread " + childNode.getActiveTargets() + " non Targets : " + childNode.getActiveNonTargets());
            parent.addChild(childNode);
            firstQueue.add(childNode);
        }
    }

    private IMTreeNode findMaxChildNode(DirectedGraph graph, Set<String> targetLabels, Set<Integer> seedSet, IMTreeNode current, int nontTargetsCount, final Set<Integer> prevActivatedSet, int noOfSimulations) {
        boolean isMaxInfluentialChildFound = false;
        PriorityQueue<CELFNodeWithNonTarget> currentQueue = clone(current.getQueue());
        PriorityQueue<CELFNodeWithNonTarget> queue = filterQueueByNonTargetsCount(currentQueue, nontTargetsCount);
        Set<Integer> currentlyActivated = new HashSet<>();
        IMTreeNode maxInfluentialChildNode = null;
        if (queue.isEmpty()) {
            return null;
        }
        while (!isMaxInfluentialChildFound && !queue.isEmpty()) {
            CELFNodeWithNonTarget top = queue.peek();
            if (top.getFlag() == seedSet.size() && top.getEstimatedActivateNontargets() == nontTargetsCount) {
                logger.info("Added node " + top.getNode() + " to seed set");
                queue.remove();
                maxInfluentialChildNode = new IMTreeNode(top.getNode(), (int) Math.round(top.getMarginalGain()), nontTargetsCount, current);
                isMaxInfluentialChildFound = true;

            } else {
                seedSet.add(top.getNode());
                logger.info("Updating Queue with node after performing diffusion : " + top.getNode());
                currentlyActivated = performDiffusion(graph, seedSet, noOfSimulations, prevActivatedSet);
                currentlyActivated.removeAll(prevActivatedSet);
                int marginalGain = countTargets(currentlyActivated, graph, targetLabels);
                seedSet.remove(top.getNode());
                queue.remove(top);
                top.setFlag(seedSet.size());
                top.setMarginalGain(marginalGain);
                queue.add(top);
            }
        }
        queue.addAll(currentQueue);
        maxInfluentialChildNode.setQueue(queue);
        return maxInfluentialChildNode;
    }

    private PriorityQueue<CELFNodeWithNonTarget> clone(PriorityQueue<CELFNodeWithNonTarget> queue) {
        PriorityQueue<CELFNodeWithNonTarget> clonedQueue = new PriorityQueue<>(new CELFNodeWithNonTargetComparator());
        for (CELFNodeWithNonTarget celfNodeWithNonTarget : queue) {
            clonedQueue.add(celfNodeWithNonTarget);
        }
        return clonedQueue;
    }

    private PriorityQueue<CELFNodeWithNonTarget> filterQueueByNonTargetsCount(PriorityQueue<CELFNodeWithNonTarget> currentQueue, int nontTargetsCount) {
        PriorityQueue<CELFNodeWithNonTarget> filteredQueue = new PriorityQueue<>(new CELFNodeWithNonTargetComparator());
        Iterator<CELFNodeWithNonTarget> iterator = currentQueue.iterator();
        while (iterator.hasNext()) {
            CELFNodeWithNonTarget celfNodeWithNonTarget = iterator.next();
            if (celfNodeWithNonTarget.getEstimatedActivateNontargets() == nontTargetsCount) {
                filteredQueue.add(celfNodeWithNonTarget);
                iterator.remove();
            }
        }
        return filteredQueue;
    }

}

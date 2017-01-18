package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

import static edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel.performDiffusion;

/**
 * Created by Naresh on 1/17/2017.
 */
public class MaxTargetInfluentialNodeUsingGreedy extends MaxTargetInfluentialNode {

    final static Logger logger = Logger.getLogger(MaxTargetInfluentialNodeUsingGreedy.class);

    @Override
    public NodeWithInfluence find(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        int maxMarginalInfluenceSpread = Integer.MIN_VALUE;
        int maxInfluentialNode = Integer.MIN_VALUE;
        Set<Integer> alreadyActivatedNodes = performDiffusion(graph, seedSet, noOfSimulations, new HashSet<>());
        int alreadyInfluencedSpread = countNonTargets(alreadyActivatedNodes, graph, targetLabels);
        for (Integer node : nodes) {
            if (!seedSet.contains(node)) {
                seedSet.add(node);
                Set<Integer> currentlyActivatedNodes = performDiffusion(graph, seedSet, noOfSimulations, alreadyActivatedNodes);
                int marginalInfluenceSpread = countNonTargets(currentlyActivatedNodes, graph, targetLabels) - alreadyInfluencedSpread;
                logger.debug("Performed Diffusion for node " + node + " and influence is : " + marginalInfluenceSpread);
                if (marginalInfluenceSpread > maxMarginalInfluenceSpread) {
                    maxMarginalInfluenceSpread = marginalInfluenceSpread;
                    maxInfluentialNode = node;
                }
                seedSet.remove(node);
            }
        }
        logger.debug("Maximum influential Node is " + maxInfluentialNode + " and influence is " + maxMarginalInfluenceSpread);
        return new NodeWithInfluence(maxInfluentialNode, maxMarginalInfluenceSpread);
    }


}

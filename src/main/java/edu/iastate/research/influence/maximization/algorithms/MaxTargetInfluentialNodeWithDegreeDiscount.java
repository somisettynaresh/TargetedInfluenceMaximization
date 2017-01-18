package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.heuristics.DegreeDiscount;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;
import org.apache.log4j.Logger;

import java.util.Set;

/**
 * Created by Naresh on 1/17/2017.
 */
public class MaxTargetInfluentialNodeWithDegreeDiscount extends MaxTargetInfluentialNode {
    final static Logger logger = Logger.getLogger(MaxTargetInfluentialNodeWithDegreeDiscount.class);

    @Override
    public NodeWithInfluence find(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        double maxInfluenceSpread = Integer.MIN_VALUE;
        int maxInfluentialNode = Integer.MIN_VALUE;
        for (Integer node : nodes) {
            if (!seedSet.contains(node)) {
                double influenceSpread = DegreeDiscount.degreeHeuresticForNodeWithLabels(graph, seedSet, node, targetLabels);
                logger.debug("Degree Discount heuristic for node " + node + " is : " + influenceSpread);

                if (influenceSpread > maxInfluenceSpread) {
                    maxInfluenceSpread = influenceSpread;
                    maxInfluentialNode = node;
                }
            }
        }
        logger.debug("Maximum influential Node is " + maxInfluentialNode + " and influence is " + maxInfluenceSpread);
        return new NodeWithInfluence(maxInfluentialNode, maxInfluenceSpread);
    }
}

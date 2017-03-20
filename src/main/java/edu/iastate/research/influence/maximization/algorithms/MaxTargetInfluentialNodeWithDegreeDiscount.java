package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.heuristics.DegreeDiscount;
import edu.iastate.research.influence.maximization.models.NodeWithInfluence;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Naresh on 1/17/2017.
 */
public class MaxTargetInfluentialNodeWithDegreeDiscount extends MaxTargetInfluentialNode {
    final static Logger logger = Logger.getLogger(MaxTargetInfluentialNodeWithDegreeDiscount.class);

    @Override
    public List<NodeWithInfluence> find(DirectedGraph graph, Set<Integer> nodes, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulations) {
        double maxInfluenceSpread = Integer.MIN_VALUE;
        int maxInfluentialNode = Integer.MIN_VALUE;
        TreeMap<Double, Set<Integer>> marginalInfluenceMap = new TreeMap<>();
        List<NodeWithInfluence> nodesWithMaxInfluence = new ArrayList<>();
        for (Integer node : nodes) {
            if (!seedSet.contains(node)) {
                double influenceSpread = DegreeDiscount.degreeHeuresticForNodeWithLabels(graph, seedSet, node, targetLabels);
                logger.debug("Degree Discount heuristic for node " + node + " is : " + influenceSpread);
                Set<Integer> nodesWithSameSpread = new HashSet<>();
                if (marginalInfluenceMap.containsKey(influenceSpread)) {
                    nodesWithSameSpread = marginalInfluenceMap.get(influenceSpread);
                }
                nodesWithSameSpread.add(node);
                marginalInfluenceMap.put(influenceSpread, nodesWithSameSpread);
                if (influenceSpread > maxInfluenceSpread) {
                    maxInfluenceSpread = influenceSpread;
                    maxInfluentialNode = node;
                }
            }
        }
        if(marginalInfluenceMap.containsKey(maxInfluenceSpread)) {
            for (Integer node : marginalInfluenceMap.get(maxInfluenceSpread)) {
                nodesWithMaxInfluence.add(new NodeWithInfluence(node, maxInfluenceSpread));
            }
        }
       // logger.info("MaxMarginal Influence nodes size :" + marginalInfluenceMap.descendingMap().firstEntry().getValue().size());
        logger.debug("Maximum influential Node is " + maxInfluentialNode + " and influence is " + maxInfluenceSpread);
        return nodesWithMaxInfluence;
    }
}

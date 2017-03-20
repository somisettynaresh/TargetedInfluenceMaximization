package edu.iastate.research.influence.maximization.utilities;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.algorithms.MaxTargetInfluentialNode;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.models.IMTree;
import edu.iastate.research.influence.maximization.models.IMTreeNode;
import org.apache.log4j.Logger;

import java.util.*;

import static edu.iastate.research.influence.maximization.algorithms.MaxTargetInfluentialNode.countTargets;
import static edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel.performDiffusion;
import static edu.iastate.research.influence.maximization.utilities.SeedSetFromIMTree.countActiveTargetsInPath;
import static edu.iastate.research.influence.maximization.utilities.SeedSetFromIMTree.findSeedSetInPath;

/**
 * Created by Naresh on 1/30/2017.
 */
public class UpdateInflunceForDDTree {
    final static Logger logger = Logger.getLogger(UpdateInflunceForDDTree.class);

    public static IMTree updateWithInfluenceSpread(IMTree imTree, DirectedGraph graph, int noOfSimulations, Set<String> targetLabels) {
        Queue<IMTreeNode> queue = new LinkedList<>();
        Map<IMTreeNode,Set<Integer>> cache = new HashMap<>();
        cache.put(imTree.getRoot(), new HashSet<>());
        int processingNode = 0;
        for (IMTreeNode imTreeNode : imTree.getRoot().getChildren()) {
            queue.add(imTreeNode);
        }
        while (!queue.isEmpty()) {
            IMTreeNode childNode = queue.poll();
            Set<Integer> seedSetInpath = findSeedSetInPath(childNode);
            Set<Integer> activatedSet = performDiffusion(graph, seedSetInpath, noOfSimulations, cache.get(childNode.getParent()));
            cache.put(childNode, activatedSet);
            double marginalInfluence = countTargets(activatedSet, graph, targetLabels) - countActiveTargetsInPath(childNode.getParent());
            childNode.setActiveTargets(marginalInfluence);
            for (IMTreeNode child : childNode.getChildren()) {
                queue.add(child);
            }
            processingNode++;
            logger.info("Updated Influence Spread for node :" + processingNode);
        }
        return imTree;
    }
}

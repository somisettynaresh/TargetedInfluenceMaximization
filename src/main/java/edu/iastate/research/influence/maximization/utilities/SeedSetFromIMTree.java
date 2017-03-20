package edu.iastate.research.influence.maximization.utilities;

import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.models.IMTree;
import edu.iastate.research.influence.maximization.models.IMTreeNode;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Naresh on 1/17/2017.
 */
public class SeedSetFromIMTree {

    static double maxSum = Integer.MIN_VALUE;
    static IMTreeNode maxLeaf = null;
    static int currentSum = 0;
    final static Logger logger = Logger.getLogger(SeedSetFromIMTree.class);

    public void maxSum(IMTreeNode root, double sum, int budget, int depth) {
        if (root != null && depth <= budget) {
            sum = sum + root.getActiveTargets();
            if (sum > maxSum) {
                maxLeaf = root;
                maxSum = sum;
            }
            for (IMTreeNode childNode : root.getChildren()) {
                maxSum(childNode, sum, budget, depth + 1);
            }
        }
    }

    public static Set<Integer> findSeedSetInPath(IMTreeNode current) {
        Set<Integer> nodesInPath = new LinkedHashSet<>();
        while (current.getParent() != null) {
            if (current.getNode() != -1) {
                nodesInPath.add(current.getNode());
            }
            current = current.getParent();
        }
        return nodesInPath;
    }

    public static double countActiveTargetsInPath(IMTreeNode current) {
        double count = 0;
        while (current.getParent() != null) {
            if (current.getNode() != -1) {
                count = count + current.getActiveTargets();
            }
            current = current.getParent();
        }
        return count;
    }

    public static Queue<IMTreeNode> getTreeNodesAtDepth(IMTreeNode root, int depth) {
        int currentLevel = -1;
        Queue<IMTreeNode> firstQueue = new LinkedList<>();
        Queue<IMTreeNode> secondQueue = new LinkedList<>();
        firstQueue.add(root);
        while (!((firstQueue.isEmpty() && secondQueue.isEmpty()) || currentLevel > depth)) {

            if (!firstQueue.isEmpty()) {
                currentLevel++;
            }
            if (currentLevel == depth) {
                return firstQueue;
            }
            while (!firstQueue.isEmpty()) {
                IMTreeNode current = firstQueue.poll();
                for (IMTreeNode child : current.getChildren()) {
                    secondQueue.add(child);
                }
            }

            if (!secondQueue.isEmpty()) {
                currentLevel++;
            }
            if (currentLevel == depth) {
                return secondQueue;
            }
            while (!secondQueue.isEmpty()) {
                IMTreeNode current = secondQueue.poll();
                for (IMTreeNode child : current.getChildren()) {
                    firstQueue.add(child);
                }
            }
    }
    return new LinkedList<>();
}

    public Set<Integer> findSeedSetFromPath(IMTree maxInfluenceTree, int budget) {
        maxSum = Integer.MIN_VALUE;
        maxLeaf = null;
        maxSum(maxInfluenceTree.getRoot(), 0, budget, 0);
        logger.info("Total target Nodes Activated :" + maxSum);
        return findSeedSetInPath(maxLeaf);
    }


}

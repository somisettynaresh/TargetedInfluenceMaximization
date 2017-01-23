package edu.iastate.research.influence.maximization.utilities;

import edu.iastate.research.influence.maximization.models.IMTree;
import edu.iastate.research.influence.maximization.models.IMTreeNode;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Naresh on 1/17/2017.
 */
public class SeedSetFromIMTree {

    static int maxSum = Integer.MIN_VALUE;
    static IMTreeNode maxLeaf = null;
    static int currentSum = 0;

    public void maxSum(IMTreeNode root, int sum, int budget, int depth) {
        if (root != null || depth > budget) {
            sum = sum + root.getActiveTargets();
            if (sum > maxSum && root.getChildren().size() == 0 || depth == budget) {
                maxLeaf = root;
                maxSum = sum;
            }
            for (IMTreeNode childNode : root.getChildren()) {
                maxSum(childNode, sum, budget, depth + 1);
            }
        }
    }

    Set<Integer> findSeedSetInPath(IMTreeNode current) {
        Set<Integer> nodesInPath = new HashSet<>();
        while (current.getParent() != null) {
            if (current.getNode() != -1) {
                nodesInPath.add(current.getNode());
            }
            current = current.getParent();
        }
        return nodesInPath;
    }

    public Set<Integer> findSeedSetFromPath(IMTree maxInfluenceTree, int budget) {
        maxSum(maxInfluenceTree.getRoot(), 0, budget, 0);
        return findSeedSetInPath(maxLeaf);
    }

}

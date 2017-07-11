package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import edu.iastate.research.influence.maximization.models.IMTree;
import edu.iastate.research.influence.maximization.models.IMTreeNode;
import edu.iastate.research.influence.maximization.models.IMTreeSeedSet;
import edu.iastate.research.influence.maximization.utilities.SeedSetFromIMTree;

import java.util.*;

import static edu.iastate.research.influence.maximization.utilities.SeedSetFromIMTree.getTreeNodesAtDepth;

/**
 * Created by madhavanrp on 6/7/17.
 */
public class IMTreeSeedSelector {
    public static List<IMTreeSeedSet> findSeedSets(DirectedGraph graph, IMTree tree, int budget, int threshold, Set<String> targetLabels, Set<String> nonTargetLabels, int noOfSimulations) {

        SeedSetFromIMTree seedSetFromIMTree = new SeedSetFromIMTree();
        Queue<IMTreeNode> leafNodes = seedSetFromIMTree.getTreeNodesAtDepth(tree.getRoot(), budget);

        List<IMTreeSeedSet> seedSets = new ArrayList<>();
        for(IMTreeNode leaf:leafNodes) {
            IMTreeSeedSet imSeedSet = new IMTreeSeedSet();
            Set<Integer> seedSet = seedSetFromIMTree.findSeedSetInPath(leaf);
            Set<Integer> activatedSet = IndependentCascadeModel.performDiffusion(graph, seedSet, noOfSimulations, new HashSet<>());
            int targetsActivated = countTargets(activatedSet, graph, targetLabels);
            int nonTargetsActivated = countTargets(activatedSet, graph, nonTargetLabels);
            imSeedSet.setTargetsActivated(targetsActivated);
            imSeedSet.setNonTargetsActivated(nonTargetsActivated);
            imSeedSet.setSeeds(seedSet);
            seedSets.add(imSeedSet);
        }
        return seedSets;
    }

    public static int countTargets(Set<Integer> activatedSet, DirectedGraph graph, Set<String> targetLabels) {
        int targetsCount = 0;
        for (Integer v : activatedSet) {
            if (graph.find(v).hasLabel(targetLabels)) {
                targetsCount++;
            }
        }
        return targetsCount;
    }
}

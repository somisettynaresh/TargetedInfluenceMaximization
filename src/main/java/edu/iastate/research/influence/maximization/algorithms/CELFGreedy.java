package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.influence.maximization.models.CELFNode;
import edu.iastate.research.influence.maximization.models.CELFNodeComparator;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Predicate;

import static edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel.performDiffusion;

/**
 * Created by Naresh on 1/23/2017.
 */
public class CELFGreedy {
    final static Logger logger = Logger.getLogger(Greedy.class);
    private static Set<Integer> prevActivatedSet = new HashSet();
    private static int prevMG = 0;

    public Set<Integer> findSeedSet(DirectedGraph graph, int budget, Set<String> targetLabels, int noOfSimulations) {
        Set<Integer> seedSet = new HashSet<>();
        PriorityQueue<CELFNode> queue = new PriorityQueue<>(new CELFNodeComparator());
        for (Vertex v : graph.getVertices()) {
            seedSet.add(v.getId());
            Set<Integer> activatedSet = performDiffusion(graph, seedSet, noOfSimulations, new HashSet<>());
            int activeTargetCount = countTargets(activatedSet, graph, targetLabels);
            queue.add(new CELFNode(v.getId(), activeTargetCount, 0));
            seedSet.remove(v.getId());
        }
        while (seedSet.size() < budget) {
            CELFNode top = queue.peek();
            //Set<Integer> alreadyActivated = performDiffusion(graph, seedSet, noOfSimulations, new HashSet<>());
            if (top.getFlag() == seedSet.size()) {
                logger.info("Added node " + top.getNode() + " to seed set");
                seedSet.add(top.getNode());
                queue.remove();
                prevActivatedSet = performDiffusion(graph, seedSet, noOfSimulations, new HashSet<>());
                logger.info("Queue Size before pruning " + queue.size());
                Predicate<CELFNode> celfNodePredicate = p -> prevActivatedSet.contains(p.getNode());
                queue.removeIf(celfNodePredicate);
                logger.info("Queue Size after pruning " + queue.size());

            } else {
                seedSet.add(top.getNode());
                int marginalGain = countTargets(performDiffusion(graph, seedSet, noOfSimulations, prevActivatedSet), graph, targetLabels) - prevMG;
                seedSet.remove(top.getNode());
                queue.remove(top);
                top.setFlag(seedSet.size());
                top.setMarginalGain(marginalGain);
                queue.add(top);
            }
        }
        return seedSet;
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

}

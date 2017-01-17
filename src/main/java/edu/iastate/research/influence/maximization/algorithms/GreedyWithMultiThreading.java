package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.influence.maximization.callables.GreedyCallable;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Naresh on 1/15/2017.
 */
public class GreedyWithMultiThreading extends Greedy {

    final static Logger logger = Logger.getLogger(GreedyWithMultiThreading.class);

    @Override
    public Integer findMaxInfluentialNode(Set<Integer> seedSet, DirectedGraph graph, Set<String> targetLabels, int noOfSimulations, Set<Integer> active) {
        int maxInfluentialNode = Integer.MIN_VALUE;
        double maxMarginalInfluenceSpread = Integer.MIN_VALUE;
        Set<Integer> maxActivatedSet = new HashSet();
        Set<Integer> alreadyActivatedSet = IndependentCascadeModel.performDiffusion(graph, seedSet, noOfSimulations, active);
        double currentSpread = calculateInfluenceSpread(graph, targetLabels, alreadyActivatedSet);
        Map<Integer, Set<Integer>> futureResults = new HashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (Vertex vertex : graph.getVertices()) {
            if (!seedSet.contains(vertex.getId())) {
                executor.execute(new GreedyCallable(graph, targetLabels, seedSet, vertex.getId(), noOfSimulations, alreadyActivatedSet, futureResults));
                logger.debug("Performing diffusion for vertex " + vertex.getId() + " influence spread : ");

            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        for (Integer v : futureResults.keySet()) {
            Set<Integer> currentActivatedSet = futureResults.get(v);
            double influenceSpread = calculateInfluenceSpread(graph, targetLabels, currentActivatedSet) - currentSpread;
            logger.debug("Performing diffusion for vertex " + v + " influence spread : " + influenceSpread);
            if (influenceSpread > maxMarginalInfluenceSpread) {
                maxMarginalInfluenceSpread = influenceSpread;
                maxInfluentialNode = v;
                maxActivatedSet = currentActivatedSet;
            }
        }
        active = maxActivatedSet;
        logger.info("Max Influential Node " + maxInfluentialNode);
        return maxInfluentialNode;
    }
}

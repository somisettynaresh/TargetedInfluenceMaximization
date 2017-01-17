package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Naresh on 1/15/2017.
 */
public class Greedy {
    final static Logger logger = Logger.getLogger(Greedy.class);

    public Set<Integer> findSeedSet(DirectedGraph graph, int budget, Set<String> targetLabels, int noOfSimulations) {
        Set<Integer> seedSet = new HashSet<>();
        Set<Integer> active = new HashSet<>();
        for (int i = 0; i < budget; i++) {
            logger.info("Finding" + i +  " seed vertex");
            Integer maxInfluentialNode = findMaxInfluentialNode(seedSet, graph, targetLabels, noOfSimulations, active);
            seedSet.add(maxInfluentialNode);
        }
        return seedSet;
    }

    public Integer findMaxInfluentialNode(Set<Integer> seedSet, DirectedGraph graph, Set<String> targetLabels, int noOfSimulations, Set<Integer> active) {
        int maxInfluentialNode = Integer.MIN_VALUE;
        double maxMarginalInfluenceSpread = Integer.MIN_VALUE;
        Set<Integer> maxActivatedSet = new HashSet<>();
        Set<Integer> activatedSet = IndependentCascadeModel.performDiffusion(graph,seedSet,noOfSimulations,active);
        double currentSpread = calculateInfluenceSpread(graph,targetLabels,activatedSet);
        for (Vertex vertex : graph.getVertices()) {
            if(!seedSet.contains(vertex.getId())){
                seedSet.add(vertex.getId());
                Set<Integer> currentActivatedSet = IndependentCascadeModel.performDiffusion(graph,seedSet,noOfSimulations,activatedSet);
                double marginalInfluenceSpread = calculateInfluenceSpread(graph,targetLabels,currentActivatedSet) - currentSpread;
                logger.debug("Performing diffusion for vertex " + vertex.getId() + " influence spread : " + marginalInfluenceSpread);
                if (maxMarginalInfluenceSpread < marginalInfluenceSpread) {
                    maxInfluentialNode = vertex.getId();
                    maxMarginalInfluenceSpread = marginalInfluenceSpread;
                    maxActivatedSet = currentActivatedSet;
                }
                seedSet.remove(vertex.getId());
            }
        }
        active = maxActivatedSet;
        logger.info("Max Influential Node " + maxInfluentialNode);
        return maxInfluentialNode;
    }

    public double influenceSpread(DirectedGraph graph, Set<Integer> seedSet, Set<String> targetLabels, int noOfSimulation) {
        Set<Integer> activeSet = IndependentCascadeModel.performDiffusion(graph, seedSet, noOfSimulation, new HashSet<>());
        return calculateInfluenceSpread(graph, targetLabels, activeSet);
    }

   public double calculateInfluenceSpread(DirectedGraph graph, Set<String> targetLabels, Set<Integer> activeSet) {
        double spread = 0;
        for (Integer vertex : activeSet) {
            if (graph.find(vertex).hasLabel(targetLabels)) {
                spread++;
            }
        }
        return spread;
    }
}

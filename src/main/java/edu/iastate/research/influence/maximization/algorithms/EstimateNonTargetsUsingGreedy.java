package edu.iastate.research.influence.maximization.algorithms;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;
import edu.iastate.research.graph.utilities.WriteObject;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Naresh on 1/16/2017.
 */
public class EstimateNonTargetsUsingGreedy extends EstimateNonTargets {
    final static Logger logger = Logger.getLogger(EstimateNonTargetsUsingGreedy.class);

    @Override
    public Map<Integer, Integer> estimate(DirectedGraph graph, Set<String> nonTargetLabels, int noOfSimulations) {
        Set<Integer> seedSet = new HashSet<>();
        Map<Integer, Integer> nonTargetsEstimateMap = new HashMap<>();
        for (Vertex vertex : graph.getVertices()) {
            seedSet.add(vertex.getId());
            Set<Integer> currentActivatedSet = IndependentCascadeModel.performDiffusion(graph, seedSet, noOfSimulations, new HashSet<>());
            int nonTargetsCount = countNonTargets(currentActivatedSet,graph,nonTargetLabels);
            logger.debug("Estimated Activated Non Targets for Vertex " + vertex.getId() + " : " + nonTargetsCount);
            nonTargetsEstimateMap.put(vertex.getId(), nonTargetsCount);
            seedSet.remove(vertex.getId());
        }
        String filename = UUID.randomUUID().toString() + "-non-targets-map.data";
        logger.debug("Writing Estimated Non Targets Map to file " + filename);
        WriteObject.writeToFile(nonTargetsEstimateMap,filename);
        return nonTargetsEstimateMap;
    }

}
package edu.iastate.research.influence.maximization.callables;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.influence.maximization.diffusion.IndependentCascadeModel;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Naresh on 1/15/2017.
 */
public class GreedyCallable implements Runnable {
    private DirectedGraph graph;
    private Set<String> targetLabels;
    private Set<Integer> seedSet;
    private int vertex;
    private int noOfSimulations;
    private Set<Integer> active;
    private Map<Integer, Set<Integer>> results;
    final static Logger logger = Logger.getLogger(GreedyCallable.class);

    public GreedyCallable(DirectedGraph graph, Set<String> targetLabels, Set<Integer> seedSet, int vertex, int noOfSimulations, Set<Integer> active, Map<Integer, Set<Integer>> results) {
        this.graph = graph;
        this.targetLabels = targetLabels;
        this.seedSet = seedSet;
        this.vertex = vertex;
        this.noOfSimulations = noOfSimulations;
        this.active = active;
        this.results = results;
    }


    @Override
    public void run() {
        Set<Integer> clonedSeedSet = cloneSeedSet();
        clonedSeedSet.add(vertex);
        Set<Integer> currentActive = IndependentCascadeModel.performDiffusion(graph,clonedSeedSet,noOfSimulations, active);
        clonedSeedSet.remove(vertex);
        logger.debug("Finished processing " + vertex);
        results.put(vertex,currentActive);
    }

    public Set<Integer> cloneSeedSet() {
        Set<Integer> clonedSeedSet = new HashSet<>();
        for (Integer seed : seedSet) {
            clonedSeedSet.add(seed);
        }
        return clonedSeedSet;
    }

}

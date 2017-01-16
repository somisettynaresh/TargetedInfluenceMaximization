package edu.iastate.research.influence.maximization.diffusion;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;

import java.util.*;

/**
 * Created by Naresh on 2/23/2016.
 */
public class IndependentCascadeModel {

    public Map<Integer, Set<Vertex>> singleDiffusion(DirectedGraph graph, Set<Integer> seeds) {
        Set<Vertex> active = new HashSet(); //will store the active nodes
        Stack<Vertex> target = new Stack<>(); //will store unprocessed nodes during intermediate time
        Map<Integer, Set<Vertex>> result = new HashMap<>();

        for (Integer sId : seeds) {
            Vertex s = graph.find(sId);
            target.push(s);
            performDiffusion(active, target);
            result.put(result.size() + 1, active);
        }
        return result;
    }

    private void performDiffusion(Set<Vertex> active, Stack<Vertex> target) {
        while (target.size() > 0) {
            Vertex node = target.pop();
            active.add(node);

            if (node != null) {
                for (Vertex follower : node.getOutBoundNeighbours()) {
                    float randnum = new Random().nextFloat();
                    if (randnum <= node.getPropagationProbability(follower)) {
                        if (!active.contains(follower)) {
                            target.push(follower);
                        }
                    }
                }
            }
        }
    }

    public Set<Vertex> activeNodesAfterDiffusion(DirectedGraph graph, Set<Integer> seeds) {
        Set<Vertex> active = new HashSet(); //will store the active nodes
        Stack<Vertex> target = new Stack<>(); //will store unprocessed nodes during intermediate time

        // System.out.println("Seed Set for the current diffusion");
        for (Integer sId : seeds) {
            Vertex s = graph.find(sId);
            target.push(s);
            performDiffusion(active, target);
        }
        return active;
    }

    public double profitMarginalSpread(DirectedGraph graph, Set seeds) {
        Map<Integer, Set<Vertex>>[] results = new Map[2];
        double[] avg = new double[seeds.size()];
        for (int i = 0; i < 2; i++) {
            results[i] = singleDiffusion(graph, seeds);
            for (int j = 1; j <= seeds.size(); j++) {
                avg[j - 1] += calculateMarginalProfit(results[i].get(j));
            }
        }
        for (int i = 0; i < seeds.size(); i++) {
            avg[i] = avg[i] / 2;
        }
        return avg[seeds.size() - 1];
    }

    private double calculateMarginalProfit(Set<Vertex> vertices) {
        double profit = 0;
        for (Vertex vertex : vertices) {
            int labelId =  Integer.parseInt(vertex.getProperties().get("label"));
            profit = profit + getProfitByLabel(labelId);
        }
        return profit;
    }

    private double getProfitByLabel(int labelId) {
        switch (labelId) {
            case 0:
                return 2.0;
            case 1:
                return -1.0;
            default:
                return -1.0;
        }
    }

    public int simulateICModel(DirectedGraph graph, Set seeds, int ROUNDS) {
        if (seeds.isEmpty()) {
            return 0;
        }
        Map<Integer, Set<Vertex>>[] results = new Map[2];
        float[] avg = new float[seeds.size()];
        for (int i = 0; i < ROUNDS; i++) {
            results[i] = singleDiffusion(graph, seeds);
            for (int j = 1; j <= seeds.size(); j++) {
                avg[j - 1] += results[i].get(j).size();
            }
        }
        for (int i = 0; i < seeds.size(); i++) {
            avg[i] = avg[i] / ROUNDS;
        }
        return (int) avg[seeds.size() - 1];
    }

}

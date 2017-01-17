package edu.iastate.research.influence.maximization.heuristics;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;

import java.util.Set;

/**
 * Created by Naresh on 10/25/2016.
 */
public class DegreeDiscount {

    public static double degreeHeuresticForNodeWithLabels(DirectedGraph graph, Set<Integer> seeds, int node, Set<String> labels) {
        Vertex vertex = graph.find(node);
        Set<Vertex> inboundNeighbours = vertex.getInBoundNeighbours();
        Set<Vertex> outboundNeighbours = vertex.getOutBoundNeighbours();
        double probabilityNotInfluenced = 1.0;
        double influenceSpread = 1;
        for (Vertex inboundNeighbour : inboundNeighbours) {
            if(seeds.contains(inboundNeighbour.getId())) {
                probabilityNotInfluenced = probabilityNotInfluenced * (1- inboundNeighbour.getPropagationProbability(vertex));
            }
        }
        for (Vertex outboundNeighbour : outboundNeighbours) {
            if (!seeds.contains(outboundNeighbour.getId()) && outboundNeighbour.hasLabel(labels)) {
                influenceSpread = influenceSpread + vertex.getPropagationProbability(outboundNeighbour);
            }
        }
        return probabilityNotInfluenced * influenceSpread;
    }
}

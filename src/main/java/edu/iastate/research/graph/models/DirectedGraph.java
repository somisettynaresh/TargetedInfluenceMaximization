package edu.iastate.research.graph.models;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Naresh on 2/23/2016.
 */
public class DirectedGraph implements Serializable {

    private Map<Integer, Vertex> vertexMap = new HashMap();

    /**
     * Getter for property 'vertices'.
     *
     * @return Value for property 'vertices'.
     */
    public Set<Vertex> getVertices() {
        return vertices;
    }

    Set<Vertex> vertices;
    int noOfEdges;

    public DirectedGraph() {
        vertices = new HashSet<>();
        this.noOfEdges = 0;
    }

    /**
     * Getter for property 'noOfEdges'.
     *
     * @return Value for property 'noOfEdges'.
     */
    public int getNoOfEdges() {
        return noOfEdges;
    }

    public int getNumberOfVertices() {
        return this.vertices.size();
    }

    public void addVertex(Vertex v) {
        this.vertices.add(v);
        vertexMap.put(v.getId(), v);
    }

    public void addVertex(int value) {
        this.vertices.add(new Vertex(value));
    }

    public Vertex find(int id) {
        return vertexMap.get(id);
    }

    public void addEdge(int from, int to, float propagationProbability) {
        Vertex fromVertex = find(from);
        Vertex toVertex = find(to);
        if (fromVertex == null) {
            fromVertex = new Vertex(from);
            this.vertices.add(fromVertex);
            vertexMap.put(fromVertex.getId(), fromVertex);
        }
        if (toVertex == null) {
            toVertex = new Vertex(to);
            this.vertices.add(toVertex);
            vertexMap.put(toVertex.getId(), toVertex);
        }
        fromVertex.addOutBoundNeighbour(toVertex, propagationProbability);
        toVertex.addInBoundNeighbour(fromVertex);
        noOfEdges++;
    }

    public void print() {
        System.out.print("Vertices of the graph : ");
        StringBuilder vertexString = new StringBuilder("");
        StringBuilder edgeString = new StringBuilder("");
        for (Vertex vertex : vertices) {
            vertexString.append(vertex.getId() + ",");
            edgeString.append("Edges for the vertex " + vertex.getId() + " are : ");
            for (Vertex neighbour : vertex.getOutBoundNeighbours()) {
                edgeString.append(neighbour.getId() + ",");
            }
            edgeString.deleteCharAt(edgeString.length() - 1);
            edgeString.append("\n");
        }

        System.out.println(vertexString.deleteCharAt(vertexString.length() - 1));
        System.out.println(edgeString);
    }

    public DirectedGraph copyVertices() {
        DirectedGraph graph = new DirectedGraph();
        for (Vertex vertex : vertices) {
            Vertex clonedVertex =new Vertex(vertex.getId());
            clonedVertex.setProperties(vertex.getProperties());
            graph.addVertex(clonedVertex);
        }
        return graph;
    }

    public void removeEdge(Vertex from, Vertex to) {
        from.removeOutBoundNeighbour(to);
        to.removeInBoundNeighbour(from);
    }
}

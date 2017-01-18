package edu.iastate.research.graph.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Naresh on 2/23/2016.
 */
public class Vertex implements Serializable {

    private int id;
    private int indDegree;
    private int outDegree;
    private Set<Vertex> inBoundNeighbours;
    private Set<Vertex> outBoundNeighbours;
    private Map<Integer, Float> propagationProbabilities;
    private Map<String, String> properties;

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Vertex(int id) {
        this.id = id;
        this.propagationProbabilities = new HashMap<>();
        this.inBoundNeighbours = new HashSet<>();
        this.outBoundNeighbours = new HashSet<>();
        this.indDegree = 0;
        this.outDegree = 0;
    }

    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public int getId() {
        return id;
    }

    /**
     * Setter for property 'id'.
     *
     * @param id Value to set for property 'id'.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Getter for property 'indDegree'.
     *
     * @return Value for property 'indDegree'.
     */
    public int getIndDegree() {
        return indDegree;
    }

    /**
     * Setter for property 'indDegree'.
     *
     * @param indDegree Value to set for property 'indDegree'.
     */
    public void setIndDegree(int indDegree) {
        this.indDegree = indDegree;
    }

    /**
     * Getter for property 'outDegree'.
     *
     * @return Value for property 'outDegree'.
     */
    public int getOutDegree() {
        return outDegree;
    }

    /**
     * Setter for property 'outDegree'.
     *
     * @param outDegree Value to set for property 'outDegree'.
     */
    public void setOutDegree(int outDegree) {
        this.outDegree = outDegree;
    }

    /**
     * Getter for property 'inBoundNeighbours'.
     *
     * @return Value for property 'inBoundNeighbours'.
     */
    public Set<Vertex> getInBoundNeighbours() {
        return inBoundNeighbours;
    }

    /**
     * Setter for property 'inBoundNeighbours'.
     *
     * @param inBoundNeighbours Value to set for property 'inBoundNeighbours'.
     */
    public void setInBoundNeighbours(Set<Vertex> inBoundNeighbours) {
        this.inBoundNeighbours = inBoundNeighbours;
    }

    /**
     * Getter for property 'outBoundNeighbours'.
     *
     * @return Value for property 'outBoundNeighbours'.
     */
    public Set<Vertex> getOutBoundNeighbours() {
        return outBoundNeighbours;
    }

    /**
     * Setter for property 'outBoundNeighbours'.
     *
     * @param outBoundNeighbours Value to set for property 'outBoundNeighbours'.
     */
    public void setOutBoundNeighbours(Set<Vertex> outBoundNeighbours) {
        this.outBoundNeighbours = outBoundNeighbours;
    }

    /**
     * Getter for property 'propagationProbabilities'.
     *
     * @return Value for property 'propagationProbabilities'.
     */
    public Map<Integer, Float> getPropagationProbabilities() {
        return propagationProbabilities;
    }

    /**
     * Setter for property 'propagationProbabilities'.
     *
     * @param propagationProbabilities Value to set for property 'propagationProbabilities'.
     */
    public void setPropagationProbabilities(Map<Integer, Float> propagationProbabilities) {
        this.propagationProbabilities = propagationProbabilities;
    }

    public float getPropagationProbability(Vertex neighbour) {
        return this.propagationProbabilities.get(neighbour.id);
    }

    public void addInBoundNeighbour(Vertex v) {
        this.inBoundNeighbours.add(v);
        indDegree++;
    }

    public void addOutBoundNeighbour(Vertex v, float propagationProbability) {
        this.outBoundNeighbours.add(v);
        this.propagationProbabilities.put(v.id, propagationProbability);
        this.outDegree++;
    }

    public void setLabel(String label) {
        if (this.getProperties() != null) {
            this.properties.put("label", label);
        } else {
            this.properties = new HashMap<>();
            this.properties.put("label", label);
        }
    }

    public String getLabel() {
        return this.properties.get("label");
    }

    public void removeOutBoundNeighbour(Vertex toVertex) {
        this.outBoundNeighbours.remove(toVertex);
        outDegree--;
    }

    public void removeInBoundNeighbour(Vertex fromVertex) {
        this.inBoundNeighbours.remove(fromVertex);
        indDegree--;
    }

    public boolean hasLabel(Set<String> targetLabels) {
        for (String targetLabel : targetLabels) {
            if (this.getProperties().get("label").equals(targetLabel)) {
                return true;
            }
        }
        return false;
    }
}

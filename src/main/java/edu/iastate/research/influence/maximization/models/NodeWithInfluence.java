package edu.iastate.research.influence.maximization.models;

/**
 * Created by Naresh on 1/17/2017.
 */
public class NodeWithInfluence {
    public int node;
    public double influence;

    public NodeWithInfluence(int node, double influence) {
        this.node = node;
        this.influence = influence;
    }

    /**
     * Getter for property 'node'.
     *
     * @return Value for property 'node'.
     */
    public int getNode() {
        return node;
    }

    /**
     * Setter for property 'node'.
     *
     * @param node Value to set for property 'node'.
     */
    public void setNode(int node) {
        this.node = node;
    }

    /**
     * Getter for property 'influence'.
     *
     * @return Value for property 'influence'.
     */
    public double getInfluence() {
        return influence;
    }

    /**
     * Setter for property 'influence'.
     *
     * @param influence Value to set for property 'influence'.
     */
    public void setInfluence(double influence) {
        this.influence = influence;
    }
}

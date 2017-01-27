package edu.iastate.research.influence.maximization.models;

import java.io.Serializable;

/**
 * Created by Naresh on 1/23/2017.
 */
public class CELFNode implements Serializable{
    private int node;
    private double marginalGain;
    private int flag = 0;

    public CELFNode(int node, double marginalGain, int flag) {
        this.node = node;
        this.marginalGain = marginalGain;
        this.flag = flag;
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
     * Getter for property 'marginalGain'.
     *
     * @return Value for property 'marginalGain'.
     */
    public double getMarginalGain() {
        return marginalGain;
    }

    /**
     * Setter for property 'marginalGain'.
     *
     * @param marginalGain Value to set for property 'marginalGain'.
     */
    public void setMarginalGain(double marginalGain) {
        this.marginalGain = marginalGain;
    }

    /**
     * Getter for property 'flag'.
     *
     * @return Value for property 'flag'.
     */
    public int getFlag() {
        return flag;
    }

    /**
     * Setter for property 'flag'.
     *
     * @param flag Value to set for property 'flag'.
     */
    public void setFlag(int flag) {
        this.flag = flag;
    }
}

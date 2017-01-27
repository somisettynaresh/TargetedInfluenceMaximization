package edu.iastate.research.influence.maximization.models;

import java.io.Serializable;
import java.util.PriorityQueue;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Naresh on 10/26/2016.
 */
public class IMTreeNode implements Serializable{
    private int node;
    private int activeTargets;
    private int activeNonTargets;
    private IMTreeNode parent;

    /**
     * Getter for property 'queue'.
     *
     * @return Value for property 'queue'.
     */
    public PriorityQueue<CELFNodeWithNonTarget> getQueue() {
        return queue;
    }

    /**
     * Setter for property 'queue'.
     *
     * @param queue Value to set for property 'queue'.
     */
    public void setQueue(PriorityQueue<CELFNodeWithNonTarget> queue) {
        this.queue = queue;
    }

    private PriorityQueue<CELFNodeWithNonTarget> queue;
    private SortedSet<IMTreeNode> children = new TreeSet<>(new IMTNodeComparator());

    /**
     * Getter for property 'children'.
     *
     * @return Value for property 'children'.
     */
    public SortedSet<IMTreeNode> getChildren() {
        return children;
    }

    public IMTreeNode(int node, int activeTargets, int activeNonTargets, SortedSet<IMTreeNode> children) {
        this.node = node;
        this.activeTargets = activeTargets;
        this.activeNonTargets = activeNonTargets;
        this.children = children;
    }

    public IMTreeNode(int node, int activeTargets, int activeNonTargets, IMTreeNode parent) {
        this.node = node;
        this.activeTargets = activeTargets;
        this.activeNonTargets = activeNonTargets;
        this.parent = parent;
    }

    public IMTreeNode(int node, int activeTargets, int activeNonTargets, SortedSet<IMTreeNode> children, IMTreeNode parent) {
        this.node = node;
        this.activeTargets = activeTargets;
        this.activeNonTargets = activeNonTargets;
        this.children = children;
        this.parent = parent;
    }


    public IMTreeNode(NodeWithInfluence maxInfluentialNode, int nonTargetsActivated, IMTreeNode parent) {
        this.node = maxInfluentialNode.getNode();
        this.activeNonTargets = nonTargetsActivated;
        this.activeTargets = ((Double)maxInfluentialNode.getInfluence()).intValue();
        this.parent = parent;
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
     * Getter for property 'activeTargets'.
     *
     * @return Value for property 'activeTargets'.
     */
    public int getActiveTargets() {
        return activeTargets;
    }

    /**
     * Setter for property 'activeTargets'.
     *
     * @param activeTargets Value to set for property 'activeTargets'.
     */
    public void setActiveTargets(int activeTargets) {
        this.activeTargets = activeTargets;
    }

    /**
     * Getter for property 'activeNonTargets'.
     *
     * @return Value for property 'activeNonTargets'.
     */
    public int getActiveNonTargets() {
        return activeNonTargets;
    }

    /**
     * Setter for property 'activeNonTargets'.
     *
     * @param activeNonTargets Value to set for property 'activeNonTargets'.
     */
    public void setActiveNonTargets(int activeNonTargets) {
        this.activeNonTargets = activeNonTargets;
    }

    public void addChild(IMTreeNode child) {
        this.children.add(child);
    }


    public IMTreeNode getParent() {
        return parent;
    }

    public void setParent(IMTreeNode parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return node + "";
    }
}

package edu.iastate.research.influence.maximization.models;

import java.io.Serializable;

/**
 * Created by Naresh on 1/23/2017.
 */
public class CELFNodeWithNonTarget extends CELFNode implements Serializable{
    /**
     * Getter for property 'estimatedActivateNontargets'.
     *
     * @return Value for property 'estimatedActivateNontargets'.
     */
    public int getEstimatedActivateNontargets() {
        return estimatedActivateNontargets;
    }

    /**
     * Setter for property 'estimatedActivateNontargets'.
     *
     * @param estimatedActivateNontargets Value to set for property 'estimatedActivateNontargets'.
     */
    public void setEstimatedActivateNontargets(int estimatedActivateNontargets) {
        this.estimatedActivateNontargets = estimatedActivateNontargets;
    }

    private int estimatedActivateNontargets =0;

    public CELFNodeWithNonTarget(int node, double marginalGain, int flag) {
        super(node, marginalGain, flag);
    }

    public CELFNodeWithNonTarget(int node, double marginalGain, int flag, int estimatedActivateNontargets) {
        super(node, marginalGain,flag);
        this.estimatedActivateNontargets = estimatedActivateNontargets;
    }
}

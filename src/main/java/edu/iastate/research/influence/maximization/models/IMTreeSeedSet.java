package edu.iastate.research.influence.maximization.models;

import java.util.List;
import java.util.Set;

/**
 * Created by madhavanrp on 6/9/17.
 */
public class IMTreeSeedSet {

    private Set<Integer> seeds;
    private int targetsActivated;
    private int nonTargetsActivated;


    public int getTargetsActivated() {
        return targetsActivated;
    }

    public void setTargetsActivated(int targetsActivated) {
        this.targetsActivated = targetsActivated;
    }

    public int getNonTargetsActivated() {
        return nonTargetsActivated;
    }

    public void setNonTargetsActivated(int nonTargetsActivated) {
        this.nonTargetsActivated = nonTargetsActivated;
    }


    public Set<Integer> getSeeds() {
        return seeds;
    }

    public void setSeeds(Set<Integer> seeds) {
        this.seeds = seeds;
    }

}

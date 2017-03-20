package edu.iastate.research.influence.maximization.models;

import java.io.Serializable;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Created by Naresh on 1/23/2017.
 */
public class CELFNodeWithNonTargetComparator implements Comparator<CELFNodeWithNonTarget>, Serializable {

    @Override
    public int compare(CELFNodeWithNonTarget o1, CELFNodeWithNonTarget o2) {
        return Double.compare(o2.getMarginalGain(), o1.getMarginalGain());
    }

    public static void main(String[] args) {
        PriorityQueue<CELFNodeWithNonTarget> queue = new PriorityQueue<>(50, new CELFNodeWithNonTargetComparator());
        for (int i = 0; i < 50; i++) {
            Random r = new Random();
            double randomValue = 50 * r.nextDouble();
            queue.add(new CELFNodeWithNonTarget(i, randomValue, 0));
        }
        while (!queue.isEmpty()) {
            System.out.println(queue.remove().getMarginalGain());
        }
    }
}
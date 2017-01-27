package edu.iastate.research.influence.maximization.models;

import java.io.Serializable;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by Naresh on 1/23/2017.
 */
public class CELFNodeComparator implements Comparator<CELFNode>, Serializable {

    @Override
    public int compare(CELFNode o1, CELFNode o2) {
        return Double.compare(o2.getMarginalGain(), o1.getMarginalGain());
    }

    public static void main(String[] args) {
        PriorityQueue<CELFNode> queue = new PriorityQueue<>(new CELFNodeComparator());

        queue.add(new CELFNode(1,10.0,0));
        queue.add(new CELFNode(2,20.0,0));
        queue.add(new CELFNode(6,20.05,0));
        CELFNode n1 = new CELFNode(3,5,0);
        queue.add(n1);
        queue.add(new CELFNode(5,40,0));
        System.out.println(queue.remove().getNode());
        //queue.add(new CELFNode(4,20,0));
        //n1.setMarginalGain(25);
        System.out.println(queue.remove().getNode());
        System.out.println(queue.remove().getNode());
        System.out.println(queue.remove().getNode());
        System.out.println(queue.remove().getNode());
    }
}
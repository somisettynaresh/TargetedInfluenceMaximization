package edu.iastate.research.influence.maximization.models;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Naresh on 10/26/2016.
 */
public class IMTNodeComparator  implements Comparator<IMTreeNode>, Serializable{
    @Override
    public int compare(IMTreeNode node1, IMTreeNode node2) {
        if(node1.getActiveNonTargets() > node2.getActiveNonTargets()) {
            return 1;
        }
         else {
            return -1;
        }
    }
}

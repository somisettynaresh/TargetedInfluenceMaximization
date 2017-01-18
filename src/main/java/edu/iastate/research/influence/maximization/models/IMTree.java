package edu.iastate.research.influence.maximization.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

/**
 * Created by Naresh on 10/26/2016.
 */
public class IMTree implements Serializable{

    private IMTreeNode root;

    public IMTree() {
        this.root = new IMTreeNode(-1, 0, 0, new TreeSet<>(new IMTNodeComparator()),null);
    }

    public IMTreeNode getRoot() {
        return root;
    }

    public void setRoot(IMTreeNode root) {
        this.root = root;
    }

    public void printTreeByLevels() {
        Queue<IMTreeNode> fq = new LinkedList<>();
        Queue<IMTreeNode> sq = new LinkedList<>();
        int level =0;
        fq.add(root);
        while(!fq.isEmpty() || !sq.isEmpty()) {
            level = processLevel(fq, sq, level);
            level = processLevel(sq, fq, level);
        }
    }

    private int processLevel(Queue<IMTreeNode> fq, Queue<IMTreeNode> sq, int level) {
        while(!fq.isEmpty()) {
            IMTreeNode current = fq.remove();
                System.out.print(current + " , ");
                for (IMTreeNode child : current.getChildren()) {
                    sq.add(child);
                }
        }
        System.out.println();
        level++;
        System.out.println("Printing level " + level);
        return level;
    }



}

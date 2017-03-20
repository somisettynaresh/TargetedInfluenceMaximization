package edu.iastate.research.influence.maximization.utilities;

import edu.iastate.research.influence.maximization.models.IMTree;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by Naresh on 1/27/2017.
 */
public class ReadIMTreeFromFile {

    public static IMTree read(String filename) {
        InputStream fin = null;
        IMTree imTree = null;
        try {
            fin = ReadIMTreeFromFile.class.getClassLoader().getResourceAsStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fin);
            imTree = (IMTree) ois.readObject();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imTree;
    }

    public static void main(String[] args) {
        //IMTree ddTree = ReadIMTreeFromFile.read("treeca-GrQc_0.01_80%A_51_10_results_ca-GrQc-80-01-greedy_2.data");
        IMTree celfTree = ReadIMTreeFromFile.read("tree-ca-GrQc_0.05_80%A_20_10_results_ca-GrQc-80-05-greedy_4-454977633.data");

        SeedSetFromIMTree seedSetFromIMTree = new SeedSetFromIMTree();
        for (int i = 1; i <= 51; i++) {
            seedSetFromIMTree.findSeedSetFromPath(celfTree, i);
        }
    }
}

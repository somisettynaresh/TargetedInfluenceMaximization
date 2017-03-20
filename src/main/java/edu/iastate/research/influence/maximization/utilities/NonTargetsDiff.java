package edu.iastate.research.influence.maximization.utilities;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Map;

/**
 * Created by Naresh on 1/16/2017.
 */
public class NonTargetsDiff {
    final static Logger logger = Logger.getLogger(NonTargetsDiff.class);

    public void printDiff(String filename1, String filename2) {
        InputStream fin = null;
        try {
            fin = NonTargetsDiff.class.getClassLoader().getResourceAsStream(filename1);
            ObjectInputStream ois = new ObjectInputStream(fin);
            Map<Integer, Integer> nonTargetMap1 = (Map<Integer, Integer>) ois.readObject();

            fin = NonTargetsDiff.class.getClassLoader().getResourceAsStream(filename2);
            ois = new ObjectInputStream(fin);
            Map<Integer, Integer> nonTargetMap2 = (Map<Integer, Integer>) ois.readObject();
            int[] diffCounter = new int[20];
            for (Integer v : nonTargetMap1.keySet()) {
                int nonTargetCount1 = nonTargetMap1.get(v);
                int nonTargetCount2 = nonTargetMap2.get(v);
                int diff = Math.abs(nonTargetCount1 - nonTargetCount2);
                diffCounter[diff] = diffCounter[diff] + 1;
            }
            for (int i=0; i<20;i++) {
                logger.info("Difference with " + i + " is " + diffCounter[i]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        NonTargetsDiff diff = new NonTargetsDiff();
        diff.printDiff("results\\Wiki-Vote-80-02-Greedy.data", "results\\Wiki-Vote-80-02-DAG.data");
    }
}

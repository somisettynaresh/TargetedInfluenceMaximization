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

            for (Integer v : nonTargetMap1.keySet()) {
                int nonTargetCount1 = nonTargetMap1.get(v);
                int nonTargetCount2 = nonTargetMap2.get(v);
                if (nonTargetCount1 > nonTargetCount2) {
                     // logger.debug("NonTargets count for 1 > 2 " + nonTargetCount1 + " : " + nonTargetCount2);
                } else if (nonTargetCount2 > nonTargetCount1) {
                  //  if (nonTargetCount2 > 2)
                       logger.info("NonTargets count for 1 < 2 " + nonTargetCount1 + " : " + nonTargetCount2);
                } else {
                    // logger.debug("NonTargets count for 1 = 2 " + nonTargetCount1 + " : " + nonTargetCount2);
                }
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
        diff.printDiff("results\\ca-GrQc-80-05-greedy.data", "results\\ca-GrQc-80-05-DAG.data");
    }
}

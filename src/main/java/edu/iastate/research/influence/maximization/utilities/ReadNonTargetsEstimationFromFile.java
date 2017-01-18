package edu.iastate.research.influence.maximization.utilities;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Naresh on 1/16/2017.
 */
public class ReadNonTargetsEstimationFromFile {
    final static Logger logger = Logger.getLogger(ReadNonTargetsEstimationFromFile.class);

    public Map<Integer, Set<Integer>> read(String filename) {
        InputStream fin = null;
        try {
            fin = ReadNonTargetsEstimationFromFile.class.getClassLoader().getResourceAsStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fin);
            Map<Integer, Integer> nonTargetMap = (Map<Integer, Integer>) ois.readObject();
            Map<Integer, Set<Integer>> mapByNonTargetsCount = new HashMap<>();
            for (Integer v : nonTargetMap.keySet()) {
                Set<Integer> nodesWithNonTargetCount = new HashSet<>();
                Integer nonTargetCount = nonTargetMap.get(v);
                if (mapByNonTargetsCount.containsKey(nonTargetCount)) {
                    nodesWithNonTargetCount = mapByNonTargetsCount.get(nonTargetCount);
                }
                nodesWithNonTargetCount.add(v);
                mapByNonTargetsCount.put(nonTargetCount, nodesWithNonTargetCount);
            }
            for (Integer nonTargetCount : mapByNonTargetsCount.keySet()) {
                StringBuilder sb = new StringBuilder();
                for (Integer v : mapByNonTargetsCount.get(nonTargetCount)) {
                    sb.append(v + " , ");
                }
                logger.debug("Vertices with Non Target Count " + nonTargetCount + " :" + sb.toString());
            }
            return mapByNonTargetsCount;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        ReadNonTargetsEstimationFromFile reader = new ReadNonTargetsEstimationFromFile();
        reader.read("af7a21c2-4f63-4042-9d1d-c6562641300f-non-targets-map.data");
    }
}

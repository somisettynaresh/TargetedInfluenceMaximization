package edu.iastate.research.graph.utilities;

import java.util.*;

/**
 * Created by Naresh on 1/15/2017.
 */
public class MapUtil {
    public static Map<Integer, Integer> sortByValue(Map<Integer, Integer> map) {
        List<Map.Entry<Integer, Integer>> list =
                new LinkedList<>(map.entrySet());
        Collections.sort(list, (o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));
        Map<Integer, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static Map<Integer, Integer> shrinkMapBySize(Map<Integer, Integer> map, int size) {
        Map<Integer, Integer> result = new HashMap<>();
        int count = 0;
        for (Integer key : map.keySet()) {
            if (count >= size) {
                break;
            }
            result.put(key, map.get(key));
            count++;
        }
        return result;
    }
}

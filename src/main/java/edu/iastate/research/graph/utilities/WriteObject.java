package edu.iastate.research.graph.utilities;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Naresh on 12/10/2016.
 */
public class WriteObject {
    public static void writeToFile(Object obj, String fileName) {
        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(fileName, true);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(obj);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

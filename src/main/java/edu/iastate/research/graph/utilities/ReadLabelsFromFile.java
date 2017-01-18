package edu.iastate.research.graph.utilities;

import edu.iastate.research.graph.models.DirectedGraph;

import java.io.*;

/**
 * Created by Naresh on 1/17/2017.
 */
public class ReadLabelsFromFile {

    public DirectedGraph read(DirectedGraph graph, String filename){
        BufferedReader bufferedReader = null;
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream("data/" +filename);
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                String[] inputLine = sCurrentLine.split("\t", 2);
                int node = Integer.parseInt(inputLine[0]);
                String label = inputLine[1];
                graph.find(node).setLabel(label);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return graph;
    }
}

package edu.iastate.research.graph.utilities;

import edu.iastate.research.graph.models.DirectedGraph;

import java.io.*;
import java.net.URL;

/**
 * Created by Naresh on 3/1/2016.
 */
public class FileDataReader implements DataReader {

    String filename;
    private float probability;

    public FileDataReader(String filename, float probability) {
        this.filename = filename;
        this.probability = probability;
    }

    @Override
    public DirectedGraph createGraphFromData() {
        DirectedGraph graph = new DirectedGraph();
        BufferedReader bufferedReader = null;
        try {
            URL url = getClass().getClassLoader().getResource("data/" +this.filename);
            bufferedReader = new BufferedReader(new FileReader(url.getPath()));
            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                String[] inputLine = sCurrentLine.split("\t", 2);
                int nodeFrom = Integer.parseInt(inputLine[0]);
                int nodeTo = Integer.parseInt(inputLine[1]);
                graph.addEdge(nodeFrom, nodeTo, this.probability);
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
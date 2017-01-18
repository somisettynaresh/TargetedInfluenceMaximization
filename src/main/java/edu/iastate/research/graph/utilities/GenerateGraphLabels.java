package edu.iastate.research.graph.utilities;

import edu.iastate.research.graph.models.DirectedGraph;
import edu.iastate.research.graph.models.Vertex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by Naresh on 1/17/2017.
 */
public class GenerateGraphLabels {

    public void generateLabels(String filename, float aPercentage) {
        FileDataReader wikiVoteDataReader = new FileDataReader(filename, 0.05f);
        DirectedGraph graph = wikiVoteDataReader.createGraphFromData();
        File output = new File(filename + "_" + aPercentage + "_labels.txt");
        try {
            PrintWriter writer = new PrintWriter(output);
            for (Vertex vertex : graph.getVertices()) {
                vertex.setLabel(new Random().nextFloat() > aPercentage ? "B" : "A");
                writer.write(vertex.getId() + "\t" + vertex.getLabel() +"\n");
            }
            writer.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GenerateGraphLabels generateGraphLabels = new GenerateGraphLabels();
        generateGraphLabels.generateLabels("CA-HepPh.txt",0.80f);
    }

}

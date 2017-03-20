package edu.iastate.research.graph.utilities;

import java.io.*;

/**
 * Created by Naresh on 2/14/2017.
 */
public class UndirectedToDirectedGraphConverter {
    public void convert(String filename) {
        BufferedReader bufferedReader = null;
        File output = new File("updated_" +filename);

        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream("data/" +  filename);
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            PrintWriter writer = new PrintWriter(output);
            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                String[] inputLine = sCurrentLine.split("\t", 2);
                String outputLine = inputLine[1] + "\t" + inputLine[0];
                writer.write(sCurrentLine + "\n");
                writer.write(outputLine + "\n");
            }
            writer.flush();
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        UndirectedToDirectedGraphConverter converter = new UndirectedToDirectedGraphConverter();
        converter.convert("DBLP.txt");
    }
}

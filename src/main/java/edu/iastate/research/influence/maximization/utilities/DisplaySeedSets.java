package edu.iastate.research.influence.maximization.utilities;

import dnl.utils.text.table.TextTable;
import edu.iastate.research.influence.maximization.models.IMTreeSeedSet;

import java.util.List;

/**
 * Created by madhavanrp on 6/10/17.
 */
public class DisplaySeedSets {

    public static void printOutput(List<IMTreeSeedSet> seedSetList) {
        String[] columnNames = {
                "Seed Set",
                "Targets Activated",
                "Non Targets Activated"
        };
        Object[][] data = new Object[seedSetList.size()][3];
        for (int i = 0; i < seedSetList.size(); i++) {
            IMTreeSeedSet seedSet = seedSetList.get(i);
            Object[] row = new Object[]{seedSet.getSeeds(), seedSet.getTargetsActivated(), seedSet.getNonTargetsActivated()};
            data[i] = row;

        };

        TextTable textTable = new TextTable(columnNames, data);
        textTable.setAddRowNumbering(true);
        textTable.printTable();

    }
}

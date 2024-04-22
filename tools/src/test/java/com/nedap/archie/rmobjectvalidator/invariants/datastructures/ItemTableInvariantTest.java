package com.nedap.archie.rmobjectvalidator.invariants.datastructures;

import com.google.common.collect.Lists;
import org.openehr.rm.datastructures.Cluster;
import org.openehr.rm.datastructures.Element;
import org.openehr.rm.datastructures.ItemTable;
import org.openehr.rm.datavalues.DvText;
import com.nedap.archie.rmobjectvalidator.invariants.InvariantTestUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemTableInvariantTest {
    @Test
    public void valid() {
        List<Cluster> rows = new ArrayList<>();

        InvariantTestUtil.assertValid(new ItemTable("id3", new DvText("table"), rows));

        Element elementOne = new Element("id5", new DvText("textElementOne"), new DvText("valueElementOne"));
        Element elementTwo = new Element("id6", new DvText("textElementTwo"), new DvText("valueElementTwo"));

        Cluster clusterOne = new Cluster("cluster", new DvText("Text1"), Arrays.asList(elementOne, elementTwo));

        Element elementThree = new Element("id7", new DvText("textElementThree"), new DvText("valueElementThree"));
        Element elementFour = new Element("id8", new DvText("textElementFour"), new DvText("valueElementFour"));

        Cluster clusterTwo = new Cluster("cluster", new DvText("Text1"), Arrays.asList(elementThree, elementFour));
        rows.add(clusterOne);
        rows.add(clusterTwo);

        InvariantTestUtil.assertValid(createValid());

    }

    @Test
    public void invalidClusterInRow() {
        List<Cluster> rows = new ArrayList<>();
        Element elementOne = new Element("id5", new DvText("textElementOne"), new DvText("valueElementOne"));
        Element elementTwo = new Element("id6", new DvText("textElementTwo"), new DvText("valueElementTwo"));

        Element elementThree = new Element("id7", new DvText("textElementThree"), new DvText("valueElementThree"));
        Element elementFour = new Element("id8", new DvText("textElementFour"), new DvText("valueElementFour"));

        Cluster clusterTwo = new Cluster("cluster", new DvText("Text1"), Arrays.asList(elementThree, elementFour));

        Cluster clusterOne = new Cluster("cluster", new DvText("Text1"), Arrays.asList(elementOne, elementTwo, clusterTwo));
        rows.add(clusterOne);
        ItemTable table = new ItemTable("id3", new DvText("table"), rows);
        InvariantTestUtil.assertInvariantInvalid(table, "Valid_structure", "/");
    }

    @Test
    public void sizeWrong() {
        ItemTable table = createValid();
        table.getRows().get(0).getItems().remove(0);

        InvariantTestUtil.assertInvariantInvalid(table, "Valid_number_of_rows", "/");
    }

    private ItemTable createValid() {
        List<Cluster> rows = new ArrayList<>();
        Element elementOne = new Element("id5", new DvText("textElementOne"), new DvText("valueElementOne"));
        Element elementTwo = new Element("id6", new DvText("textElementTwo"), new DvText("valueElementTwo"));

        Cluster clusterOne = new Cluster("cluster", new DvText("Text1"), Lists.newArrayList(elementOne, elementTwo));

        Element elementThree = new Element("id7", new DvText("textElementThree"), new DvText("valueElementThree"));
        Element elementFour = new Element("id8", new DvText("textElementFour"), new DvText("valueElementFour"));

        Cluster clusterTwo = new Cluster("cluster", new DvText("Text1"), Lists.newArrayList(elementThree, elementFour));
        rows.add(clusterOne);
        rows.add(clusterTwo);
        return new ItemTable("id3", new DvText("table"), rows);

    }

}

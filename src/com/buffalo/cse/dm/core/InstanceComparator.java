package com.buffalo.cse.dm.core;

import java.util.Comparator;

public class InstanceComparator implements Comparator<Instance> {

    private int sortIndex;

    public InstanceComparator(int index) {
        sortIndex = index;
    }

    @Override
    public int compare(Instance o1, Instance o2) {
        double val1 = o1.getAttribute(sortIndex).getAttributeValue();
        double val2 = o2.getAttribute(sortIndex).getAttributeValue();
        if (val1 < val2) {
            return -1;
        } else if (val1 > val2) {
            return 1;
        } else {
            return 0;
        }
    }

}

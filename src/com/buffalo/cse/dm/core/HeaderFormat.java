package com.buffalo.cse.dm.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeaderFormat {
    private List<AttributeType> headerType;
    private List<Map<String, Integer>> nominalValues;

    public HeaderFormat() {
        headerType = new ArrayList<AttributeType>();
        nominalValues = new ArrayList<Map<String, Integer>>();
    }

    public void add(AttributeType type, String[] values) {
        headerType.add(type);
        if (type == AttributeType.NUMERIC) {
            nominalValues.add(null);
        } else if (type == AttributeType.NOMINAL) {
            Map<String, Integer> nomValues = new HashMap<String, Integer>();
            for (int i = 0; i < values.length; i++) {
                nomValues.put(values[i].trim().toLowerCase(), i);
            }
            nominalValues.add(nomValues);
        }
    }

    public AttributeType getType(int i) {
        return headerType.get(i);
    }

    public Map<String, Integer> getNominalValues(int i) {
        return nominalValues.get(i);
    }

}

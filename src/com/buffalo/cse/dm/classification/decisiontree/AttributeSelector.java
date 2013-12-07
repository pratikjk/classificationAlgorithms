package com.buffalo.cse.dm.classification.decisiontree;

import java.util.List;

import com.buffalo.cse.dm.core.Instances;

public abstract class AttributeSelector {

    /*
     * 
     * Method to return the index of the attribute that gives the best split.
     * The attribute to split will be decided by the respective algorithm
     */
    public abstract SplitModel bestAttribute(Instances data,
            List<Integer> attributes);

}

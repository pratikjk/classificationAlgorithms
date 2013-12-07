package com.buffalo.cse.dm.classification.decisiontree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.buffalo.cse.dm.classification.decisiontree.TreeNode.Leaf;
import com.buffalo.cse.dm.classification.decisiontree.TreeNode.Binary;
import com.buffalo.cse.dm.core.AttributeType;
import com.buffalo.cse.dm.core.Instance;
import com.buffalo.cse.dm.core.InstanceComparator;
import com.buffalo.cse.dm.core.Instances;

public class ID3 extends DecisionTree {

    @Override
    public TreeNode buildModel(Instances trainData) {
        List<Integer> attributes = new ArrayList<Integer>();
        for (int i = 0; i < trainData.getInstance(0).getNumOfAttributes(); i++) {
            attributes.add(i);
        }
        root = buildTree(trainData, attributes);
        return root;
    }

    private TreeNode buildTree(Instances data, List<Integer> nonTargetAttributes) {
        TreeNode node = null;
        if (data.getDataSetSize() == 0) {
            node = new Leaf();
            ((Leaf) node).setClassLabel(-1);
            return node;
        } else if (data.areAllSameClass()) {
            node = new Leaf();
            ((Leaf) node).setClassLabel(data.getInstance(0).getClassValue());
            return node;
        } else if (nonTargetAttributes.isEmpty()) {
            return maxClassLabel(data);
        } else {
            // find the best attribute to split upon
            AttributeSelector as = new EntropyBasedAttributeSelection();
            SplitModel sm = as.bestAttribute(data, nonTargetAttributes);

            // sort the data on the best attribute
            InstanceComparator ic = new InstanceComparator(
                    sm.getAttributeIndex());
            Collections.sort(data.getDataSet(), ic);

            double splitPoint = (data.getInstance(sm.getSplitCriteriaIndex())
                    .getAttribute(sm.getAttributeIndex()).getAttributeValue() + data
                    .getInstance(sm.getSplitCriteriaIndex() - 1)
                    .getAttribute(sm.getAttributeIndex()).getAttributeValue()) / 2;
            sm.setSplitCriteria(splitPoint);
            Instances leftData = null;
            Instances rightData = null;
            if (data.getHeader().get(sm.getAttributeIndex()) == AttributeType.NUMERIC) {
                leftData = data.getInstancesSubset(0,
                        sm.getSplitCriteriaIndex());
                rightData = data.getInstancesSubset(sm.getSplitCriteriaIndex());
            } else {
                double val = data.getInstance(0)
                        .getAttribute(sm.getAttributeIndex())
                        .getAttributeValue();
                int breakIndex = data.getDataSetSize() - 1;
                for (int i = 1; i < data.getDataSetSize(); i++) {
                    double curr = data.getInstance(i)
                            .getAttribute(sm.getAttributeIndex())
                            .getAttributeValue();
                    if (curr != val) {
                        breakIndex = i;
                        break;
                    }
                }
                leftData = data.getInstancesSubset(0, breakIndex);
                rightData = data.getInstancesSubset(breakIndex);
                sm.setSplitCriteria(1.0);
            }

            // make the old data ready for garbage collection
            data = null;
            node = new Binary();
            ((Binary) node).setSplitModel(sm);
            // nonTargetAttributes.remove(nonTargetAttributes.indexOf(sm.getAttributeIndex()));
            ((Binary) node).setLChild(buildTree(leftData, nonTargetAttributes));
            ((Binary) node)
                    .setRChild(buildTree(rightData, nonTargetAttributes));

        }
        return node;
    }

    private TreeNode maxClassLabel(Instances trainData) {
        Leaf leafNode = new Leaf();
        leafNode.setClassLabel(trainData.maxClassLabel());
        return leafNode;
    }

    @Override
    public void classify(Instance testData) {
        TreeNode temp = root;
        while (true) {
            if (!(temp instanceof Leaf)) {
                SplitModel node = ((Binary) temp).getSplitModel();
                if (testData.getAttribute(node.getAttributeIndex())
                        .getAttributeValue() < node.getSplitCriteria()) {
                    // go left
                    temp = ((Binary) temp).getLChild();
                } else {
                    // go right
                    temp = ((Binary) temp).getRChild();
                }
            } else {
                testData.setPredictedClass(((Leaf) temp).getClassLabel());
                return;
            }
        }

    }

    @Override
    public void printTree() {

    }

}

package com.buffalo.cse.dm.classification.decisiontree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.buffalo.cse.dm.classification.decisiontree.TreeNode.Binary;
import com.buffalo.cse.dm.classification.decisiontree.TreeNode.Leaf;
import com.buffalo.cse.dm.core.AttributeType;
import com.buffalo.cse.dm.core.Instance;
import com.buffalo.cse.dm.core.InstanceComparator;
import com.buffalo.cse.dm.core.Instances;

public class ID3 extends DecisionTree {

    private int mSelectAtributes;
    private int mTotalAttributes;

    public ID3() {
        // TODO Auto-generated constructor stub
    }

    public ID3(int total, int m) {
        mSelectAtributes = m;
        mTotalAttributes = total;
    }

    @Override
    public TreeNode buildModel(Instances trainData) {
        List<Integer> attributes = new ArrayList<Integer>();
        for (int i = 0; i < trainData.getInstance(0).getNumOfAttributes(); i++) {
            attributes.add(i);
        }
        root = buildTree(trainData, attributes);
        return root;
    }

    public TreeNode buildRFModel(Instances trainData) {
        List<Integer> totalAttributes = new ArrayList<Integer>();
        for (int i = 0; i < mTotalAttributes; i++) {
            totalAttributes.add(i);
        }
        List<Integer> toSelectAttributes = getAttributesToUse();
        root = buildRandomForestTree(trainData, totalAttributes,
                toSelectAttributes);
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

    public TreeNode buildRandomForestTree(Instances data,
            List<Integer> nonTargetAttributes,
            List<Integer> randomSelectedAttributes) {
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
            SplitModel sm = as.bestAttribute(data, randomSelectedAttributes);

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
            randomSelectedAttributes = getAttributesToUse();
            ((Binary) node).setLChild(buildRandomForestTree(leftData,
                    nonTargetAttributes, randomSelectedAttributes));
            ((Binary) node).setRChild(buildRandomForestTree(rightData,
                    nonTargetAttributes, randomSelectedAttributes));

        }
        return node;
    }

    private List<Integer> getAttributesToUse() {
        boolean[] whichVarsToInclude = new boolean[mTotalAttributes];

        for (int i = 0; i < mTotalAttributes; i++)
            whichVarsToInclude[i] = false;

        while (true) {
            int a = (int) Math.floor(Math.random() * mTotalAttributes);
            whichVarsToInclude[a] = true;
            int N = 0;
            for (int i = 0; i < mTotalAttributes; i++)
                if (whichVarsToInclude[i])
                    N++;
            if (N == mSelectAtributes)
                break;
        }

        List<Integer> shortRecord = new ArrayList<Integer>(mSelectAtributes);

        for (int i = 0; i < mTotalAttributes; i++)
            if (whichVarsToInclude[i])
                shortRecord.add(i);
        return shortRecord;
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
        printTree(root, 0);
        System.out.println();
    }

    /*
     * Printing the tree in preorder fashion
     */
    private void printTree(TreeNode root, int level) {
        StringBuffer indent = new StringBuffer();
        if (level > 0) {
            for (int i = 0; i < (level - 1) * 4; i++)
                indent.append(" ");
            for (int i = 0; i < 4; i++)
                indent.append("-");
        }
        // System.out.println(indent.toString());
        indent.append(root.toString());
        System.out.println(indent);
        if (root instanceof TreeNode.Binary) {
            printTree(((TreeNode.Binary) root).getLChild(), level + 1);
            printTree(((TreeNode.Binary) root).getRChild(), level + 1);
        } else if (root instanceof TreeNode.Leaf) {
            return;
        }

    }
}

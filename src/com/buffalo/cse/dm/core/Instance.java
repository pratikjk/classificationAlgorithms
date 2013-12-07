package com.buffalo.cse.dm.core;

import java.util.ArrayList;
import java.util.List;

public class Instance {

    private int numOfAttributes;
    private int classValue;
    private List<Attribute> attributes;
    private int sortIndex;
    private int predictedClass = -1;

    public Instance() {
        attributes = new ArrayList<Attribute>();
    }

    public Instance(int num) {
        attributes = new ArrayList<Attribute>(num);
        numOfAttributes = num;
    }

    public int getNumOfAttributes() {
        return numOfAttributes;
    }

    public void setNumOfAttributes(int numOfAttributes) {
        this.numOfAttributes = numOfAttributes;
    }

    public int getClassValue() {
        return classValue;
    }

    public void setClassValue(int classValue) {
        this.classValue = classValue;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public void setAttribute(int attrIndex, Attribute value) {
        attributes.set(attrIndex, value);
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public int getPredictedClass() {
        return predictedClass;
    }

    public void setPredictedClass(int predictedClass) {
        this.predictedClass = predictedClass;
    }

    public boolean isCorrectClassified() {
        return classValue == predictedClass;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public Instance clone() {
        Instance newInstance = new Instance(this.numOfAttributes);
        newInstance.setClassValue(this.getClassValue());
        newInstance.getAttributes().addAll(attributes);
        return newInstance;
    }

    public Attribute getAttribute(int index) {
        return attributes.get(index);
    }

    @Override
    public String toString() {
        String value = "";
        for (int i = 0; i < getNumOfAttributes(); i++) {
            value += attributes.get(i).toString() + " ";
        }
        return value + classValue;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

    }

}

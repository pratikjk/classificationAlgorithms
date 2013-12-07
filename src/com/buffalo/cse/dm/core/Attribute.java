package com.buffalo.cse.dm.core;

public class Attribute {

    private String attributeName;
    private AttributeType attributeType;
    private double attributeValue;
    private double normalizedAttributeValue;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(AttributeType attributeType) {
        this.attributeType = attributeType;
    }

    public double getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(double attributeValue) {
        this.attributeValue = attributeValue;
    }

    public double getNormalizedAttributeValue() {
        return normalizedAttributeValue;
    }

    public void setNormalizedAttributeValue(double normalizedAttributeValue) {
        this.normalizedAttributeValue = normalizedAttributeValue;
    }

    @Override
    public String toString() {
        return attributeValue + "";
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

    }

}

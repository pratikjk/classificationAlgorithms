package com.buffalo.cse.dm.classification.decisiontree;

public class SplitModel {

	private int attributeIndex;
	private double splitCriteria;
	private int splitCriteriaIndex;

	public SplitModel() {

	}

	public SplitModel(int attInd, double splitCriteria) {
		this.attributeIndex=attInd;
		this.splitCriteria=splitCriteria;
	}
	
	public SplitModel(int attInd, double splitCriteria, int splitCriteriaIndex) {
		this(attInd,splitCriteria);
		this.splitCriteriaIndex=splitCriteriaIndex;
	}
	
	public int getAttributeIndex() {
		return attributeIndex;
	}

	public void setAttributeIndex(int attributeIndex) {
		this.attributeIndex = attributeIndex;
	}

	public double getSplitCriteria() {
		return splitCriteria;
	}

	public void setSplitCriteria(double splitCriteria) {
		this.splitCriteria = splitCriteria;
	}

	public int getSplitCriteriaIndex() {
		return splitCriteriaIndex;
	}

	public void setSplitCriteriaIndex(int splitCriteriaIndex) {
		this.splitCriteriaIndex = splitCriteriaIndex;
	}

	@Override
	public String toString() {
		String s = "Attribute Index:"+attributeIndex+" | SplitCriteria:"+splitCriteria+" | SplitIndex:"+splitCriteriaIndex;
		return s;
	}

}

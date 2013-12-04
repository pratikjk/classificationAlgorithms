package com.buffalo.cse.dm.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.buffalo.cse.dm.classification.decisiontree.TreeNode;
import com.buffalo.cse.dm.preprocessing.Normalizer;

public class Instances {
	private List<Instance> dataSet;
	private boolean isNormalized;
	private List<AttributeType> header;
	private Instances normalizedDataSet;
	private Instances testForCv;
	
	public Instances(){
		dataSet=new ArrayList<Instance>();
	}
	public Instances(Instances data){
		this.dataSet=data.dataSet;
		this.isNormalized=data.isNormalized;
		this.header=data.header;
	}
	
	public int getDataSetSize() {
		return this.dataSet.size();
	}

	public List<Instance> getDataSet() {
		return dataSet;
	}

	public void setDataSet(List<Instance> dataSet) {
		this.dataSet = dataSet;
	}

	public List<AttributeType> getHeader() {
		return header;
	}

	public void setHeader(List<AttributeType> header) {
		this.header = header;
	}

	public boolean isNormalized(){
		return isNormalized;
	}
	
	public Instances getNormalizedDataSet() {
		if(isNormalized)
			return normalizedDataSet;
		else
			return null;
	}


	public void addInstance(Instance instance){
		this.dataSet.add(instance);
	}
	
	public void removeInstance(int index){
		this.dataSet.remove(index);
	}
	
	public Instances normalize(Normalizer norm){
		normalizedDataSet=norm.normalize(this);
		isNormalized=true;
		return normalizedDataSet;
	}
	
	public Instances clone(){
		Instances deepCopy = new Instances();
		for(int i=0;i<this.dataSet.size();i++){
			deepCopy.addInstance(this.getInstance(i).clone());
		}
		
		return deepCopy;
		
	}

	public int maxClassLabel() {
		Map<Integer, Integer> classCount = new HashMap<Integer,Integer>();
		for(int i=0;i<this.getDataSetSize();i++){
			int currClassValue = this.getInstance(i).getClassValue();
			if(classCount.containsKey(currClassValue)){
				int count=classCount.get(currClassValue);
				classCount.put(currClassValue, count+1);
			}else{
				classCount.put(currClassValue, 1);
			}
		}
		int maxClassLabel=-1;
		int maxClassCount=Integer.MIN_VALUE;
		for(Integer classLabel:classCount.keySet()){
			if(classCount.get(classLabel)>maxClassCount){
				maxClassCount=classCount.get(classLabel);
				maxClassLabel=classLabel;
			}
		}
		return maxClassLabel;
	}
	
	public Instance getInstance(int i) {
		return dataSet.get(i);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public boolean areAllSameClass() {
		for(int i=1;i<getDataSetSize();i++){
			if(getInstance(i).getClassValue() != getInstance(i-1).getClassValue()){
				return false;
			}
		}
		return true;
	}
	
	public void sortOnIndex(int index){
		
	}
	
	@Override
	public String toString() {
		String s="";
		for(int i=0;i<getDataSetSize();i++){
			s+=getInstance(i).toString()+"\n";
		}
		return s;
	}

	public Instances getInstancesSubset(int start, int endExclusive) {
		Instances subData = new Instances();
		subData.setHeader(this.getHeader());
		subData.isNormalized=isNormalized();
		List<Instance> d = new ArrayList<Instance>();
		for(int i=start;i<endExclusive;i++){
			d.add(this.getInstance(i));
		}
		subData.setDataSet(d);
		return subData;
	}

	public Instances getInstancesSubset(int start) {
		Instances subData = new Instances();
		subData.setHeader(this.getHeader());
		subData.isNormalized=isNormalized();
		List<Instance> d = new ArrayList<Instance>();
		for(int i=start;i<this.getDataSetSize();i++){
			d.add(this.getInstance(i));
		}
		subData.setDataSet(d);
		return subData;
	}
	
	public Instances add(Instances toAdd){
		for(int i=0;i<toAdd.getDataSetSize();i++){
			this.addInstance(toAdd.getInstance(i));
		}
		return this;
	}
	
	public Instances getTrainingForCrossValidation(int numOfFolds, int foldToValidate){
		Instances train=new Instances();
		train.setHeader(getHeader());
		int instancePerFold = this.getDataSetSize()/numOfFolds;
		int remainder = this.getDataSetSize()%numOfFolds;
		train.add(getInstancesSubset(0,((foldToValidate-1)*instancePerFold)));
		//System.out.print(0 +" to "+((foldToValidate-1)*instancePerFold));
		setTestForCrossValidation(getInstancesSubset(((foldToValidate-1)*instancePerFold), foldToValidate*instancePerFold));
		train.add(getInstancesSubset(foldToValidate*instancePerFold,(numOfFolds*instancePerFold)));
		//System.out.print("\t"+foldToValidate*instancePerFold +" to "+numOfFolds*instancePerFold);
		train.add(getInstancesSubset(numOfFolds*instancePerFold, (numOfFolds*instancePerFold)+remainder));
		//System.out.println("\t"+numOfFolds*instancePerFold +" to "+((numOfFolds*instancePerFold)+remainder));
		return train;
	}
	
	public Instances getTestForCrossValidation(){
		return testForCv;
	}
	
	public void setTestForCrossValidation(Instances test){
		this.testForCv=test;
	}
}

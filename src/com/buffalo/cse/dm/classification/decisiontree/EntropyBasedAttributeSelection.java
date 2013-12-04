package com.buffalo.cse.dm.classification.decisiontree;

import java.util.Collections;
import java.util.List;

import com.buffalo.cse.dm.core.InstanceComparator;
import com.buffalo.cse.dm.core.Instances;

public class EntropyBasedAttributeSelection extends AttributeSelector {

	@Override
	public SplitModel bestAttribute(Instances data, List<Integer> attributes) {

		int index = -1;
		double maxGain = Double.MIN_VALUE;
		double splitPoint=0;
		int splitPointIndex=-1;
		for (int i = 0; i < attributes.size(); i++) {
			double[] splitPointAndGain = findSplitPointAndMaxGain(data, attributes.get(i));
			if (splitPointAndGain[1] > maxGain) {
				splitPoint=splitPointAndGain[0];
				maxGain=splitPointAndGain[1];
				index = attributes.get(i);
				splitPointIndex=(int)splitPointAndGain[2];
			}
		}
		return new SplitModel(index, splitPoint,splitPointIndex);
	}

	private double[] findSplitPointAndMaxGain(Instances data, Integer attributeIndex) {
		// sort the data on the attribute index, if the attribute values are
		// continuous
		InstanceComparator ic = new InstanceComparator(attributeIndex);
		Collections.sort(data.getDataSet(), ic);

		// get the class(ification) distribution
		// assuming 2-class problem
		int[] count = getClassificationDistribution(data, 0,
				data.getDataSetSize());
		
		// find adjacent examples that differ in their target classification
		double maxGain = Double.MIN_VALUE;
		double splitPoint = 0;
		double splitPointIndex=-1;
		int classVal = data.getInstance(0).getClassValue();

		for (int i = 1; i < data.getDataSetSize(); i++) {
			if (data.getInstance(i).getClassValue() != classVal) {
				classVal = data.getInstance(i).getClassValue();
				double temp = calcluateGainForSplit(data, count, i);
				if (maxGain < temp) {
					maxGain = temp;
					splitPoint = data.getInstance(i)
							.getAttribute(attributeIndex).getAttributeValue();
					splitPointIndex=i;
				}
			}
		}
		return new double[]{splitPoint,maxGain,splitPointIndex};
	}

	// reference:http://www.cs.berkeley.edu/~russell/classes/cs194/f11/lectures/CS194%20Fall%202011%20Lecture%2008.pdf
	private double calcluateGainForSplit(Instances data, int[] parentCount ,int splitIndex) {
		int prevCount[] = getClassificationDistribution(data, 0, splitIndex);//{3,0}
		int afterCount[] = getClassificationDistribution(data, splitIndex,
				data.getDataSetSize());//{0,2}
		double origPos=(float)parentCount[1]/data.getDataSetSize();// 0.4
		double left=(float)(splitIndex)/data.getDataSetSize(); // 0.6
		double leftPos=(float)prevCount[1]/(splitIndex); // 0
		double rightPos=(float)afterCount[1]/(data.getDataSetSize()-splitIndex); // 1

		return gain(origPos, left, leftPos, rightPos);
	}

	private int[] getClassificationDistribution(Instances data, int startIndex,
			int endIndex) {
		// get the class(ification) distribution
		int[] count = new int[2];
		for (int i = startIndex; i < endIndex; i++) {
			count[data.getInstance(i).getClassValue()]++;
		}
		return count;
	}

	private double entropy(double val) {
		double result;
		
		if(val==0)
			result = -((1 - val) * Math.log(1 - val)/Math.log(2));
		else if(val==1)
			result = -(val * Math.log(val)/Math.log(2));
		else
			result = -(val * Math.log(val)/Math.log(2) + (1 - val) * Math.log(1 - val)/Math.log(2));
		
		return result;
	}

	public double gain(double origPos, double left, double leftPos,
			double rightPos) {
		double result = entropy(origPos)
				- (left * entropy(leftPos) + (1 - left) * entropy(rightPos));
		return result;
	}
	
	public static void main(String[] args) {
		EntropyBasedAttributeSelection gias = new EntropyBasedAttributeSelection();
		double origPos=(2.0/5);
		double leftPos=(0.0/3.0);
		double left=(3.0/5);
		double rightPos=(2.0/2);
		System.out.println(gias.gain(origPos, left, leftPos, rightPos));
		System.out.println(gias.entropy(0.4));
		
	}

}

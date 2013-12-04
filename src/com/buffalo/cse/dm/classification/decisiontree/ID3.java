package com.buffalo.cse.dm.classification.decisiontree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.buffalo.cse.dm.classification.decisiontree.TreeNode.Leaf;
import com.buffalo.cse.dm.classification.decisiontree.TreeNode.Binary;
import com.buffalo.cse.dm.core.Instance;
import com.buffalo.cse.dm.core.InstanceComparator;
import com.buffalo.cse.dm.core.Instances;

public class ID3 extends DecisionTreeClassifier {


	@Override
	public TreeNode buildModel(Instances trainData) {
		List<Integer> attributes = new ArrayList<Integer>();
		for(int i=0;i<trainData.getInstance(0).getNumOfAttributes();i++){
			attributes.add(i);
		}		
		root=buildTree(trainData, attributes);
		return root;
	}
	
	private TreeNode buildTree(Instances data, List<Integer> nonTargetAttributes){
		TreeNode node = null;
		if(data.getDataSetSize()==0){
			node= new Leaf();
			((Leaf)node).setClassLabel(-1);
			return node;
		}else if(data.areAllSameClass()){
			node = new Leaf();
			((Leaf)node).setClassLabel(data.getInstance(0).getClassValue());
			return node;
		}else if(nonTargetAttributes.isEmpty()){
			return maxClassLabel(data);
		}else{
			// find the best attribute to split upon
			EntropyBasedAttributeSelection ebas = new EntropyBasedAttributeSelection();
			SplitModel sm = ebas.bestAttribute(data, nonTargetAttributes);
			
			// sort the data on the best attribute
			InstanceComparator ic = new InstanceComparator(sm.getAttributeIndex());
			Collections.sort(data.getDataSet(), ic);
			
			Instances leftData = data.getInstancesSubset(0,sm.getSplitCriteriaIndex());
			Instances rightData = data.getInstancesSubset(sm.getSplitCriteriaIndex());
			data=null;
			node=new Binary();
			((Binary)node).setSplitModel(sm);
			nonTargetAttributes.remove(nonTargetAttributes.indexOf(sm.getAttributeIndex()));
			((Binary)node).setLChild(buildTree(leftData,nonTargetAttributes));
			((Binary)node).setRChild(buildTree(rightData,nonTargetAttributes));
			
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
		TreeNode temp=root;
		while(true){
			if(!(temp instanceof Leaf)){
				SplitModel node = ((Binary)temp).getSplitModel();
				if(testData.getAttribute(node.getAttributeIndex()).getAttributeValue() < node.getSplitCriteria()){
					// go left
					temp=((Binary)temp).getLChild();
				}else{
					// go right
					temp=((Binary)temp).getRChild();
				}
			}else{
				testData.setPredictedClass(((Leaf)temp).getClassLabel());
				return;
			}
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}


}

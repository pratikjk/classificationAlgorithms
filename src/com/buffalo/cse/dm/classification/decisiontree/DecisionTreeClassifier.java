package com.buffalo.cse.dm.classification.decisiontree;

import com.buffalo.cse.dm.classification.Classifier;
import com.buffalo.cse.dm.core.Instance;
import com.buffalo.cse.dm.core.Instances;

public abstract class DecisionTreeClassifier implements Classifier {
	
	protected TreeNode root;
	

	public abstract TreeNode buildModel(Instances trainData);

	public abstract void classify(Instance testData);
	
	public TreeNode getRoot(){
		return root;
	}
	
	public void setRoot(TreeNode root){
		this.root=root;
	}
	

}

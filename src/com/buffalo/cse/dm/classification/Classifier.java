package com.buffalo.cse.dm.classification;

import com.buffalo.cse.dm.classification.decisiontree.TreeNode;
import com.buffalo.cse.dm.core.Instance;
import com.buffalo.cse.dm.core.Instances;

public interface Classifier {

	public TreeNode buildModel(Instances trainData);
	
	public void classify(Instance testData);

}

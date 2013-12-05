package com.buffalo.cse.dm.main;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.buffalo.cse.dm.classification.analysis.ConfusionMatrix;
import com.buffalo.cse.dm.classification.decisiontree.AttributeSelector;
import com.buffalo.cse.dm.classification.decisiontree.DecisionTree;
import com.buffalo.cse.dm.classification.decisiontree.EntropyBasedAttributeSelection;
import com.buffalo.cse.dm.classification.decisiontree.ID3;
import com.buffalo.cse.dm.classification.decisiontree.TreeNode;
import com.buffalo.cse.dm.core.Instance;
import com.buffalo.cse.dm.core.Instances;
import com.buffalo.cse.dm.io.DataFileReader;
import com.buffalo.cse.dm.preprocessing.MinMaxNormalizer;

public class MainDriver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataFileReader ip = new DataFileReader("dataset2");
		try {
			Instances data=ip.loadDataFromFile();
			//testCV(data);
			testCLassifyWithCV(data);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void testSplitIndex(Instances data, List<Integer> attributes){
		AttributeSelector as = new EntropyBasedAttributeSelection();
		System.out.println(as.bestAttribute(data, attributes).toString());
		System.out.println();
	}
	
	public static void testNormalization(Instances data){
		MinMaxNormalizer minMaxNorm = new MinMaxNormalizer();
		Instances normalized=data.normalize(minMaxNorm);
		System.out.println(normalized.getDataSetSize());
	}
	
	public static void testAreAllSameClasses(Instances data){
		System.out.println(data.areAllSameClass());
	}
	
	public static void testCLassify(Instances data){
		Random index = new Random();
		DecisionTree dtc = new ID3();
		TreeNode tree = dtc.buildModel(data);
		Instance test = data.getInstance(index.nextInt(data.getDataSetSize()));
		dtc.classify(test);
		System.out.println(test);
	}
	
	public static void testCV(Instances data){
		for(int i=1;i<=10;i++){
			Instances cv=data.getTrainingForCrossValidation(10, i);
			//System.out.println(cv.getInstance(0).getClassValue() + " to "+cv.getInstance(cv.getDataSetSize()-1).getClassValue());
		}
	}
	
	public static void testCLassifyWithCV(Instances data){
		DecisionTree dtc = new ID3();
		ConfusionMatrix averageConfusionMatrix = new ConfusionMatrix();
		for(int i=1;i<=10;i++){
			Instances train=data.getTrainingForCrossValidation(10, i);
			dtc.buildModel(train);
			Instances test = data.getTestForCrossValidation();
			ConfusionMatrix cm = new ConfusionMatrix();
			for(int j=0;j<test.getDataSetSize();j++){
				Instance t = test.getInstance(j);
				dtc.classify(t);
				//System.out.println(t.getPredictedClass()+" "+t.isCorrectClassified());
				if(t.isCorrectClassified()){
					// true
					if(t.getClassValue()==1){
						// positive
						cm.incrementTruePositive();
					}else{
						// negative
						cm.incrementTrueNegative();
					}
				}else{
					// false
					if(t.getClassValue()==1){
						// positive
						cm.incrementFalsePositive();
					}else{
						// negative
						cm.incrementFalseNegative();
					}
				}
			}
			System.out.format("******************** Fold %2d ********************\n",i);
			System.out.format("\t Accuracy %f \n",cm.getAccuracy());
			System.out.format("\t Precision %f \n",cm.getPrecision());
			System.out.format("\t Recall %f \n",cm.getRecall());
			System.out.format("\t F-Measure %f \n",cm.getFmeasure());
			System.out.println();
			//System.out.format("************************************************\n",i);
		}
		
	}

}

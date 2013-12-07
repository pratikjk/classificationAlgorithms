package com.buffalo.cse.dm.main;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.buffalo.cse.dm.classification.analysis.ConfusionMatrix;
import com.buffalo.cse.dm.classification.decisiontree.AttributeSelector;
import com.buffalo.cse.dm.classification.decisiontree.DecisionTree;
import com.buffalo.cse.dm.classification.decisiontree.EntropyBasedAttributeSelection;
import com.buffalo.cse.dm.classification.decisiontree.ID3;
import com.buffalo.cse.dm.classification.decisiontree.RandomForest;
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
            Instances data = ip.loadDataFromFile("\t");
            // testCV(data);
            // testCLassifyWithCV(data);
            testRandomFoest(data);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testSplitIndex(Instances data, List<Integer> attributes) {
        AttributeSelector as = new EntropyBasedAttributeSelection();
        System.out.println(as.bestAttribute(data, attributes).toString());
        System.out.println();
    }

    public static void testNormalization(Instances data) {
        MinMaxNormalizer minMaxNorm = new MinMaxNormalizer();
        Instances normalized = data.normalize(minMaxNorm);
        System.out.println(normalized.getDataSetSize());
    }

    public static void testAreAllSameClasses(Instances data) {
        System.out.println(data.areAllSameClass());
    }

    public static void testCLassify(Instances data) {
        Random index = new Random();
        DecisionTree dtc = new ID3();
        TreeNode tree = dtc.buildModel(data);
        Instance test = data.getInstance(index.nextInt(data.getDataSetSize()));
        dtc.classify(test);
        System.out.println(test);
    }

    public static void testCV(Instances data) {
        for (int i = 1; i <= 10; i++) {
            Instances cv = data.getTrainingForCrossValidation(10, i);
            // System.out.println(cv.getInstance(0).getClassValue() +
            // " to "+cv.getInstance(cv.getDataSetSize()-1).getClassValue());
        }
    }

    public static void testCLassifyWithCV(Instances data) {
        DecisionTree dtc = new ID3();
        ConfusionMatrix averageConfusionMatrix = new ConfusionMatrix();
        int crossValidation = 10;
        for (int i = 1; i <= crossValidation; i++) {
            Instances train = data.getTrainingForCrossValidation(
                    crossValidation, i);
            dtc.buildModel(train);
            dtc.printTree();
            Instances test = data.getTestForCrossValidation();
            ConfusionMatrix cm = new ConfusionMatrix();
            for (int j = 0; j < test.getDataSetSize(); j++) {
                Instance t = test.getInstance(j);
                dtc.classify(t);
                // System.out.println(t.getPredictedClass()+" "+t.isCorrectClassified());
                if (t.isCorrectClassified()) {
                    // true
                    if (t.getClassValue() == 1) {
                        // positive
                        cm.incrementTruePositive();
                    } else {
                        // negative
                        cm.incrementTrueNegative();
                    }
                } else {
                    // false
                    if (t.getClassValue() == 1) {
                        // positive
                        cm.incrementFalsePositive();
                    } else {
                        // negative
                        cm.incrementFalseNegative();
                    }
                }
            }
            averageConfusionMatrix.addToTruePositive(cm.getTruePositive());
            averageConfusionMatrix.addToFalsePositive(cm.getFalsePositive());
            averageConfusionMatrix.addToTrueNegative(cm.getTrueNegative());
            averageConfusionMatrix.addToFalseNegative(cm.getFalseNegative());

            /*
             * System.out.format(
             * "******************** Fold %2d ********************\n",i);
             * System.out.format("\t Accuracy %f \n",cm.getAccuracy());
             * System.out.format("\t Precision %f \n",cm.getPrecision());
             * System.out.format("\t Recall %f \n",cm.getRecall());
             * System.out.format("\t F-Measure %f \n",cm.getFmeasure());
             * System.out.println();
             */
            // System.out.format("************************************************\n",i);
        }
        averageConfusionMatrix.allDivideBy(crossValidation);
        System.out.format("\t Average Accuracy %f \n",
                averageConfusionMatrix.getAccuracy());
        System.out.format("\t Average Precision %f \n",
                averageConfusionMatrix.getPrecision());
        System.out.format("\t Average Recall %f \n",
                averageConfusionMatrix.getRecall());
        System.out.format("\t Average F-Measure %f \n",
                averageConfusionMatrix.getFmeasure());
    }

    public static void testRandomFoest(Instances data) {
        Instances train = data.getTrainingForCrossValidation(10, 1);
        Instances tests = data.getTestForCrossValidation();
        RandomForest rf = new RandomForest(5, train);
        rf.startForestBuild();
        ConfusionMatrix cm = new ConfusionMatrix();
        for (Instance t : tests.getDataSet()) {
            rf.classify(t);
            // System.out.println(test.isCorrectClassified());
            if (t.isCorrectClassified()) {
                // true
                if (t.getClassValue() == 1) {
                    // positive
                    cm.incrementTruePositive();
                } else {
                    // negative
                    cm.incrementTrueNegative();
                }
            } else {
                // false
                if (t.getClassValue() == 1) {
                    // positive
                    cm.incrementFalsePositive();
                } else {
                    // negative
                    cm.incrementFalseNegative();
                }
            }
        }
        // System.out.format("******************** Fold %2d ********************\n",i);
        System.out.format("\t Accuracy %f \n", cm.getAccuracy());
        System.out.format("\t Precision %f \n", cm.getPrecision());
        System.out.format("\t Recall %f \n", cm.getRecall());
        System.out.format("\t F-Measure %f \n", cm.getFmeasure());
        System.out.println();
    }

}

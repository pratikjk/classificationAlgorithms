package com.buffalo.cse.dm.classification.decisiontree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.buffalo.cse.dm.classification.analysis.ConfusionMatrix;
import com.buffalo.cse.dm.core.Instance;
import com.buffalo.cse.dm.core.Instances;
import com.buffalo.cse.dm.io.DataFileReader;
import com.buffalo.cse.dm.io.TestFileReader;

public class RandomForest {

    private static final int NUM_THREADS = Runtime.getRuntime()
            .availableProcessors();
    private int totalAttributes;
    private int numOfAttributesPerTree;
    private List<DecisionTree> forest;
    public int numOfTrees;
    private ExecutorService treePool;
    Instances trainData;

    public RandomForest(int numOfTrees, Instances data) {
        this.numOfTrees = numOfTrees;
        forest = new ArrayList<DecisionTree>(numOfTrees);
        trainData = data;
        totalAttributes = trainData.getInstance(0).getNumOfAttributes();
        numOfAttributesPerTree = ((int) Math.round(Math.log(totalAttributes)
                / Math.log(2) + 1));
        // numOfAttributesPerTree = (int) Math.sqrt(totalAttributes);
    }

    private Instances getBootStrapSample(Instances data) {
        int N = data.getDataSetSize();
        Instances bootStrappedSample = new Instances();
        bootStrappedSample.setHeader(data.getHeader());
        for (int i = 0; i < N; i++) {
            Random r = new Random();
            int num = r.nextInt(N);
            bootStrappedSample.addInstance(data.getInstance(num));
        }
        return bootStrappedSample;
    }

    public void startForestBuild() {
        treePool = Executors.newFixedThreadPool(NUM_THREADS);
        for (int t = 0; t < numOfTrees; t++) {
            treePool.execute(new RunnableID3(getBootStrapSample(trainData),
                    this, t + 1));
        }
        treePool.shutdown();
        try {
            treePool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
            System.out.println("interrupted exception in Random Forests");
        }
    }

    public void classify(Instance testData) {
        int[] predictions = new int[2];
        for (int i = 0; i < numOfTrees; i++) {
            forest.get(i).classify(testData);
            predictions[testData.getPredictedClass()]++;
        }
        int classz = predictions[0] > predictions[1] ? 0 : 1;
        testData.setClassValue(classz);
    }

    public class RunnableID3 implements Runnable {

        private RandomForest parentForest;
        private int treeNum;

        public RunnableID3(Instances data, RandomForest parentForest,
                int treeNum) {
            super();
            this.parentForest = parentForest;
            this.treeNum = treeNum;
        }

        @Override
        public void run() {
            DecisionTree tree = new ID3(totalAttributes, numOfAttributesPerTree);
            forest.add(tree);
            tree.buildModel(trainData);
        }

    }

    public int getNumOfAttributesPerTree() {
        return numOfAttributesPerTree;
    }

    public void setNumOfAttributesPerTree(int numOfAttributesPerTree) {
        this.numOfAttributesPerTree = numOfAttributesPerTree;
    }

    public List<DecisionTree> getForest() {
        return forest;
    }

    public void setForest(List<DecisionTree> forest) {
        this.forest = forest;
    }

    public int getNumOfTrees() {
        return numOfTrees;
    }

    public void setNumOfTrees(int numOfTrees) {
        this.numOfTrees = numOfTrees;
    }

    public static int getNumThreads() {
        return NUM_THREADS;
    }

    public static void main(String[] args) {
        String fileName = "dataset3";
        String delimiter = "\t";
        DataFileReader ip = new DataFileReader(fileName, delimiter);

        try {
            Instances train = ip.loadDataFromFile();
            // testdataset4(train);
            Instances test = new TestFileReader(fileName, delimiter)
                    .loadDataFromFile(train.getHeader());
            testdataset3(train, test);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testdataset3(Instances train, Instances tests) {
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
        System.out
                .format("************* Random Forest withour CV *************\n");
        System.out.format("\t Accuracy %f \n", cm.getAccuracy());
        System.out.format("\t Precision %f \n", cm.getPrecision());
        System.out.format("\t Recall %f \n", cm.getRecall());
        System.out.format("\t F-Measure %f \n", cm.getFmeasure());
        System.out.format("************************************************\n");
    }

}

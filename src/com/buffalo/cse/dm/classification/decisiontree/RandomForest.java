package com.buffalo.cse.dm.classification.decisiontree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.buffalo.cse.dm.core.Instance;
import com.buffalo.cse.dm.core.Instances;

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
        trainData = getBootStrapSample(data);
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
            treePool.execute(new RunnableID3(trainData, this, t + 1));
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
            predictions[testData.getClassValue()]++;
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

}

package com.buffalo.cse.dm.classification.bayes;

import java.io.IOException;
import java.util.List;

import com.buffalo.cse.dm.classification.analysis.ConfusionMatrix;
import com.buffalo.cse.dm.core.AttributeType;
import com.buffalo.cse.dm.core.Instance;
import com.buffalo.cse.dm.core.Instances;
import com.buffalo.cse.dm.io.DataFileReader;
import com.buffalo.cse.dm.io.TestFileReader;

public class NaiveBayes {
    public Instances data;
    public double[] classPriorProbability;
    public List<Instance> dataSet;
    public double[][] attributeDistributionMean, attributeDistributionVariance;
    public double[] classPosteriorProbability;

    public NaiveBayes(Instances data) {
        this.data = data;
        classPriorProbability = new double[2];
        attributeDistributionMean = new double[2][data.getHeader().size() - 1];
        attributeDistributionVariance = new double[2][data.getHeader().size() - 1];
        dataSet = data.getDataSet();
        classPosteriorProbability = new double[2];
        getClassPriorProbability();
        getAttributeDistribution();
    }

    public void getClassPriorProbability() {

        Instance dataSample = new Instance();

        for (int i = 0; i < classPriorProbability.length; i++) {
            classPriorProbability[i] = 0;
        }

        for (int i = 0; i < dataSet.size(); i++) {
            dataSample = dataSet.get(i);

            if (dataSample.getClassValue() == 0) {
                classPriorProbability[0]++;
            } else {
                classPriorProbability[1]++;
            }
        }

        for (int i = 0; i < classPriorProbability.length; i++) {
            classPriorProbability[i] /= dataSet.size();
        }

    }

    public void getAttributeDistribution() {
        double[] classCount = new double[2];

        // Initialize Mean and Variance
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < data.getHeader().size() - 1; j++) {
                attributeDistributionMean[i][j] = 0;
                attributeDistributionVariance[i][j] = 0;
            }
        }

        classCount[0] = 0;
        classCount[1] = 0;
        // Class Count
        for (int j = 0; j < dataSet.size(); j++) {
            if (dataSet.get(j).getClassValue() == 0) {
                classCount[0]++;
            } else {
                classCount[1]++;
            }
        }

        // Mean Calculation
        for (int i = 0; i < data.getHeader().size() - 1; i++) {
            for (int j = 0; j < dataSet.size(); j++) {
                if (dataSet.get(j).getClassValue() == 0) {
                    attributeDistributionMean[0][i] += dataSet.get(j)
                            .getAttribute(i).getAttributeValue();
                } else {
                    attributeDistributionMean[1][i] += dataSet.get(j)
                            .getAttribute(i).getAttributeValue();
                }
            }
            attributeDistributionMean[0][i] /= classCount[0];
            attributeDistributionMean[1][i] /= classCount[1];
        }

        // Variance Calculation
        for (int i = 0; i < data.getHeader().size() - 1; i++) {
            for (int j = 0; j < dataSet.size(); j++) {
                if (dataSet.get(j).getClassValue() == 0) {
                    attributeDistributionVariance[0][i] += Math.pow(dataSet
                            .get(j).getAttribute(i).getAttributeValue()
                            - attributeDistributionMean[0][i], 2);

                } else {
                    attributeDistributionVariance[1][i] += Math.pow(dataSet
                            .get(j).getAttribute(i).getAttributeValue()
                            - attributeDistributionMean[1][i], 2);
                }
            }
            attributeDistributionVariance[0][i] /= classCount[0];
            attributeDistributionVariance[1][i] /= classCount[1];
        }
    }

    public double getDescriptorPosteriorProbability(Instance instance,
            int classLabel) {

        double descriptorPosteriorProbability = 1;

        for (int i = 0; i < instance.getNumOfAttributes(); i++) {
            if (instance.getAttribute(i).getAttributeType() == AttributeType.NOMINAL) {
                descriptorPosteriorProbability *= attributeDistributionMean[classLabel][i];
            } else {
                descriptorPosteriorProbability *= Math.exp(-Math.pow(instance
                        .getAttribute(i).getAttributeValue()
                        - attributeDistributionMean[classLabel][i], 2)
                        / (2 * attributeDistributionVariance[classLabel][i]));
                descriptorPosteriorProbability /= Math.sqrt(2 * Math.PI
                        * attributeDistributionVariance[classLabel][i]);
            }
        }

        return descriptorPosteriorProbability;
    }

    public void getClassPosteriorProbability(Instances test) {
        List<Instance> testSet = test.getDataSet();
        for (int j = 0; j < testSet.size(); j++) {
            classPosteriorProbability[0] = 1;
            classPosteriorProbability[1] = 1;

            classPosteriorProbability[0] = classPriorProbability[0]
                    * getDescriptorPosteriorProbability(testSet.get(j), 0);
            classPosteriorProbability[1] = classPriorProbability[1]
                    * getDescriptorPosteriorProbability(testSet.get(j), 1);

            System.out.println("For X:" + test + " p(H=0|X)="
                    + classPosteriorProbability[0] + " p(H=1|X)="
                    + classPosteriorProbability[1]);
            if (classPosteriorProbability[0] > classPosteriorProbability[1]) {
                test.getInstance(j).setPredictedClass(0);
            } else {
                test.getInstance(j).setPredictedClass(1);
            }
        }
    }

    public static void main(String[] args) {
        String fileName = "dataset3";
        String delimiter = "\t";
        DataFileReader ip = new DataFileReader(fileName, delimiter);
        // testNoCV(ip);
        Instances train;
        try {
            train = ip.loadDataFromFile();
            Instances test = new TestFileReader(fileName, delimiter)
                    .loadDataFromFile(train.getHeader());
            testdataset3(train, test);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void testWithCV(DataFileReader ip) {
        try {
            Instances data = ip.loadDataFromFile();
            ConfusionMatrix averageConfusionMatrix = new ConfusionMatrix();
            int crossValidation = 10;
            NaiveBayes nbClassifier;
            for (int i = 1; i <= crossValidation; i++) {
                Instances train = data.getTrainingForCrossValidation(
                        crossValidation, i);
                Instances test = data.getTestForCrossValidation();
                nbClassifier = new NaiveBayes(train);
                nbClassifier.getClassPosteriorProbability(test);
                ConfusionMatrix cm = new ConfusionMatrix();
                for (int j = 0; j < test.getDataSetSize(); j++) {
                    Instance t = test.getInstance(j);
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
                averageConfusionMatrix
                        .addToFalsePositive(cm.getFalsePositive());
                averageConfusionMatrix.addToTrueNegative(cm.getTrueNegative());
                averageConfusionMatrix
                        .addToFalseNegative(cm.getFalseNegative());
            }
            System.out.format("\t  Accuracy %f \n",
                    averageConfusionMatrix.getAccuracy());
            System.out.format("\t  Precision %f \n",
                    averageConfusionMatrix.getPrecision());
            System.out.format("\t  Recall %f \n",
                    averageConfusionMatrix.getRecall());
            System.out.format("\t  F-Measure %f \n",
                    averageConfusionMatrix.getFmeasure());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void testNoCV(DataFileReader ip) {
        try {
            Instances data = ip.loadDataFromFile();
            NaiveBayes nbClassifier;
            int size = data.getDataSetSize();
            // Collections.shuffle(data.getDataSet());
            Instances train = data.getInstancesSubset(0,
                    data.getDataSetSize() - 1);
            // sunny cool high weak -1
            Instances test = data.getInstancesSubset(data.getDataSetSize() - 1);
            nbClassifier = new NaiveBayes(train);
            nbClassifier.getClassPosteriorProbability(test);
            /*
             * ConfusionMatrix cm = new ConfusionMatrix(); for (int j = 0; j <
             * test.getDataSetSize(); j++) { Instance t = test.getInstance(j);
             * if (t.isCorrectClassified()) { // true if (t.getClassValue() ==
             * 1) { // positive cm.incrementTruePositive(); } else { // negative
             * cm.incrementTrueNegative(); } } else { // false if
             * (t.getClassValue() == 1) { // positive
             * cm.incrementFalsePositive(); } else { // negative
             * cm.incrementFalseNegative(); } } }
             * System.out.format("\t  Accuracy %f \n", cm.getAccuracy());
             * System.out.format("\t  Precision %f \n", cm.getPrecision());
             * System.out.format("\t  Recall %f \n", cm.getRecall());
             * System.out.format("\t  F-Measure %f \n", cm.getFmeasure());
             */

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void testdataset3(Instances train, Instances test) {
        NaiveBayes classifier = new NaiveBayes(train);
        classifier.getClassPosteriorProbability(test);
        ConfusionMatrix cm = new ConfusionMatrix();
        for (int j = 0; j < test.getDataSetSize(); j++) {
            Instance t = test.getInstance(j);
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
        System.out.format("\t Accuracy %f \n", cm.getAccuracy());
        System.out.format("\t Precision %f \n", cm.getPrecision());
        System.out.format("\t Recall %f \n", cm.getRecall());
        System.out.format("\t F-Measure %f \n", cm.getFmeasure());
    }
}

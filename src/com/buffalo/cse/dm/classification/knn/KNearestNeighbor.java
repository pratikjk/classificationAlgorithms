package com.buffalo.cse.dm.classification.knn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.buffalo.cse.dm.classification.analysis.ConfusionMatrix;
import com.buffalo.cse.dm.core.Instance;
import com.buffalo.cse.dm.core.Instances;
import com.buffalo.cse.dm.distance.DistanceAlgorithm;
import com.buffalo.cse.dm.distance.EucledianDistance;
import com.buffalo.cse.dm.io.DataFileReader;
import com.buffalo.cse.dm.io.TestFileReader;
import com.buffalo.cse.dm.preprocessing.MinMaxNormalizer;

public class KNearestNeighbor {
    public Instances data;
    public List<DistanceInstanceMap> distanceQueue;
    public int k = 9;
    public MinMaxNormalizer minMaxNorm;

    public KNearestNeighbor(Instances train) {
        this.data = train;
    }

    public void kNNClassify(Instances test) {
        Instance testSample = null;
        DistanceAlgorithm d = new EucledianDistance();
        Map<Double, List<Instance>> hp;
        int[] count = new int[2];
        for (int i = 0; i < test.getDataSetSize(); i++) {
            count[0] = 0;
            count[1] = 0;
            distanceQueue = new ArrayList<DistanceInstanceMap>();
            testSample = test.getInstance(i);

            for (int j = 0; j < data.getDataSetSize(); j++) {
                Double distance = new Double(d.distance(testSample,
                        data.getInstance(j)));
                distanceQueue.add(new DistanceInstanceMap(distance, data
                        .getInstance(j)));
            }
            Collections.sort(distanceQueue);
            for (int j = 0; j < 1; j++) {
                DistanceInstanceMap dimap = distanceQueue.get(j);
                Instance inst = dimap.getInstance();
                if (inst.getClassValue() == 0) {
                    count[0]++;
                } else {
                    count[1]++;
                }
            }
            if (count[0] > count[1]) {
                testSample.setPredictedClass(0);
            } else {
                testSample.setPredictedClass(1);
            }
        }

    }

    public static void main(String[] args) {
        DataFileReader ip = new DataFileReader("dataset3", "\t");
        testD3(ip);
    }

    public static void testCV(DataFileReader ip) {
        try {
            Instances dataSet = ip.loadDataFromFile();
            MinMaxNormalizer minMaxNorm = new MinMaxNormalizer();
            ConfusionMatrix averageConfusionMatrix = new ConfusionMatrix();
            int crossValidation = 10;
            for (int i = 1; i <= crossValidation; i++) {
                Instances train = dataSet.getTrainingForCrossValidation(
                        crossValidation, i);
                Instances test = dataSet.getTestForCrossValidation();
                test = test.normalize(minMaxNorm);
                train = train.normalize(minMaxNorm);
                KNearestNeighbor kNNClassifier = new KNearestNeighbor(train);
                kNNClassifier.kNNClassify(test);

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
            System.out.format("\t Average Accuracy %f \n",
                    averageConfusionMatrix.getAccuracy());
            System.out.format("\t Average Precision %f \n",
                    averageConfusionMatrix.getPrecision());
            System.out.format("\t Average Recall %f \n",
                    averageConfusionMatrix.getRecall());
            System.out.format("\t Average F-Measure %f \n",
                    averageConfusionMatrix.getFmeasure());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void testD3(DataFileReader ip) {
        String stemFileName = "dataset3";
        String delimiter = "\t";
        try {
            Instances train = ip.loadDataFromFile();
            MinMaxNormalizer minMaxNorm = new MinMaxNormalizer();
            Instances test = new TestFileReader(stemFileName, delimiter)
                    .loadDataFromFile(train.getHeader());
            test = test.normalize(minMaxNorm);
            train = train.normalize(minMaxNorm);
            KNearestNeighbor kNNClassifier = new KNearestNeighbor(train);
            kNNClassifier.kNNClassify(test);

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
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}

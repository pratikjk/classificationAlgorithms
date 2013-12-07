package com.buffalo.cse.dm.preprocessing;

import com.buffalo.cse.dm.core.Instances;

public class MinMaxNormalizer implements Normalizer {

    private int newMin;
    private int newMax;

    public MinMaxNormalizer() {
        newMin = 0;
        newMax = 1;
    }

    public MinMaxNormalizer(int newMin, int newMax) {
        this.newMin = newMin;
        this.newMax = newMax;
    }

    @Override
    public Instances normalize(Instances dataset) {
        int numOfAttributes = dataset.getInstance(0).getNumOfAttributes();
        Instances copy = dataset.clone();
        for (int i = 0; i < numOfAttributes; i++) {
            normalizeByColumn(copy, i);
        }
        return copy;
    }

    private void normalizeByColumn(Instances dataset, int colIndex) {
        double max = Double.MIN_VALUE;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < dataset.getDataSetSize(); i++) {
            double currValue = dataset.getInstance(i).getAttribute(colIndex)
                    .getAttributeValue();
            if (max < currValue) {
                max = currValue;
            }
            if (min > currValue) {
                min = currValue;
            }
        }

        for (int i = 0; i < dataset.getDataSetSize(); i++) {
            double currValue = dataset.getInstance(i).getAttribute(colIndex)
                    .getAttributeValue();
            double newValue = minMaxNormalization(currValue, min, max);
            dataset.getInstance(i).getAttribute(colIndex)
                    .setAttributeValue(newValue);
        }

    }

    private double minMaxNormalization(double currValue, double min, double max) {
        return (((currValue - min) / (max - min)) * (newMax - newMin)) + newMin;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        MinMaxNormalizer mn = new MinMaxNormalizer(0, 1);
        System.out.println(mn.minMaxNormalization(50000, 25000, 55000));

    }

}

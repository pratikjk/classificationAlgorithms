package com.buffalo.cse.dm.classification.analysis;

public class ConfusionMatrix {

    /*
     * Predicted class Yes No Actual | Yes a b class | No c d
     * 
     * a: TP (true positive) b: FN (false negative) c: FP (false positive)d: TN
     * (true negative)
     */

    private int truePositive;
    private int falsePositive;
    private int falseNegative;
    private int trueNegative;

    public ConfusionMatrix() {
        truePositive = 0;
        falsePositive = 0;
        falseNegative = 0;
        trueNegative = 0;
    }

    public void incrementTruePositive() {
        truePositive++;
    }

    public void addToTruePositive(int tp) {
        truePositive += tp;
    }

    public void incrementFalsePositive() {
        falsePositive++;
    }

    public void addToFalsePositive(int fp) {
        falsePositive += fp;
    }

    public void incrementFalseNegative() {
        falseNegative++;
    }

    public void addToFalseNegative(int fn) {
        falseNegative += fn;
    }

    public void incrementTrueNegative() {
        trueNegative++;
    }

    public void addToTrueNegative(int tn) {
        trueNegative += tn;
    }

    public int getTruePositive() {
        return truePositive;
    }

    public void setTruePositive(int truePositive) {
        this.truePositive = truePositive;
    }

    public int getFalsePositive() {
        return falsePositive;
    }

    public void setFalsePositive(int falsePositive) {
        this.falsePositive = falsePositive;
    }

    public int getFalseNegative() {
        return falseNegative;
    }

    public void setFalseNegative(int falseNegative) {
        this.falseNegative = falseNegative;
    }

    public int getTrueNegative() {
        return trueNegative;
    }

    public void setTrueNegative(int trueNegative) {
        this.trueNegative = trueNegative;
    }

    public double getAccuracy() {
        return (float) (trueNegative + truePositive)
                / (trueNegative + truePositive + falseNegative + falsePositive);
    }

    public double getRecall() {
        if (truePositive != 0) {
            return (float) (truePositive) / (truePositive + falseNegative);
        } else {
            return 0;
        }
    }

    public double getPrecision() {
        if (truePositive != 0) {
            return (float) (truePositive) / (truePositive + falsePositive);
        } else {
            return 0;
        }
    }

    public double getFmeasure() {
        if (truePositive != 0) {
            return (float) (2 * truePositive)
                    / (2 * truePositive + falseNegative + falsePositive);
        } else {
            return 0;
        }
    }

    public void allDivideBy(int crossValidation) {
        truePositive /= crossValidation;
        falsePositive /= crossValidation;
        falseNegative /= crossValidation;
        trueNegative /= crossValidation;
    }

}

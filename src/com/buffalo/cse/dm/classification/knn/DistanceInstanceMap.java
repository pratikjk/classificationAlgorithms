package com.buffalo.cse.dm.classification.knn;

import com.buffalo.cse.dm.core.Instance;

public class DistanceInstanceMap implements Comparable<DistanceInstanceMap> {

    private Double distance;
    private Instance instance;

    public DistanceInstanceMap(Double distance, Instance instance) {
        this.distance = distance;
        this.instance = instance;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    @Override
    public int compareTo(DistanceInstanceMap o) {
        return this.distance.compareTo(o.distance);
    }

}

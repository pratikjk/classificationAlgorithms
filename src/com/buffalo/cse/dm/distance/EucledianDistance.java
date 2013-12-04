package com.buffalo.cse.dm.distance;

import com.buffalo.cse.dm.core.Instance;


public class EucledianDistance implements DistanceAlgorithm {

	@Override
	public double distance(Instance v1, Instance v2) {	
		double result=0;
		if(v1.getNumOfAttributes()!=v2.getNumOfAttributes()){
			throw new IllegalArgumentException("Instances are not of same size");
		}else{
			
			for(int i=0;i<v1.getNumOfAttributes();i++){
				result+=Math.pow(v1.getAttribute(i).getAttributeValue()-v2.getAttribute(i).getAttributeValue(),2);
			}
		
		}
		return Math.sqrt(result);
	}
	
	public static void main(String[] args) {
		
	}
}

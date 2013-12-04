package com.buffalo.cse.dm.preprocessing;

import com.buffalo.cse.dm.core.Instances;

public interface Normalizer {

	/*
	 * returns a normalized copy of the 'dataset'
	 * @param dataset the dataset to be normalized
	 * @return copy of normalized dataset
	 * */
	public Instances normalize(Instances dataset);


}

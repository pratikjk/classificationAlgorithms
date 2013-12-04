package com.buffalo.cse.dm.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.buffalo.cse.dm.core.Attribute;
import com.buffalo.cse.dm.core.AttributeType;
import com.buffalo.cse.dm.core.Instance;
import com.buffalo.cse.dm.core.Instances;


/**
 * 
 * @author pratikkathalkar
 * Create list of Input Vector in a nDimensional space, from a input file.
 */
public class DataFileReader {
	private BufferedReader dataReader;
	private BufferedReader formatReader;
	private BufferedReader testReader;
	private String stemFileName;
	
	
	public DataFileReader(String stemFileName){
		assert(stemFileName !=null);
		this.stemFileName=stemFileName;
	}
	
	public Instances loadDataFromFile() throws IOException{
		String line;
		dataReader = new BufferedReader(new FileReader(stemFileName+".txt"));
		Instances dataSet = new Instances();
		dataSet.setHeader(loadDataFormatFromFile());
		while((line=dataReader.readLine())!=null){
			String[] tokens = line.split("\t");
			Instance vector = new Instance(tokens.length-1);
			for(int i=0;i<tokens.length-1;i++){
				Attribute atr = new Attribute();
				atr.setAttributeType(dataSet.getHeader().get(i));
				if(dataSet.getHeader().get(i)==AttributeType.NUMERIC){
					atr.setAttributeValue(Double.parseDouble(tokens[i]));
				}else if(dataSet.getHeader().get(i)==AttributeType.NOMINAL){
					// hardcoding for string format "Present"/"Absent"
					if(tokens[i].equalsIgnoreCase("present"))
						atr.setAttributeValue(1);
					else if(tokens[i].equalsIgnoreCase("absent")){
						atr.setAttributeValue(0);
					}else{
						throw new RuntimeException("Nominal Attribute other than Hardcoded 'Present' and 'Absent'");
					}
				}
				vector.addAttribute(atr);
			}
			vector.setClassValue(Integer.parseInt(tokens[tokens.length-1]));
			dataSet.addInstance(vector);
		}
		dataReader.close();
		return dataSet;
	}
	
	private List<AttributeType> loadDataFormatFromFile() throws IOException{
		String line;
		formatReader = new BufferedReader(new FileReader(stemFileName+".format"));
		List<AttributeType> header = new ArrayList<AttributeType>();
		while((line=formatReader.readLine())!=null){
			line=line.trim();
			if(line.equals("nominal")){
				header.add(AttributeType.NOMINAL);
			}else if(line.equals("numeric")){
				header.add(AttributeType.NUMERIC);
			}
		}
		
		formatReader.close();
		return header;
	}
	
	public Instances loadTestFromFile() throws IOException{
		String line;
		Instances dataSet = new Instances();
		testReader = new BufferedReader(new FileReader(stemFileName+".test"));
		dataSet.setHeader(loadDataFormatFromFile());
		while((line=testReader.readLine())!=null){
			String[] tokens = line.split("\t");
			Instance vector = new Instance(tokens.length);
			for(int i=0;i<tokens.length;i++){
				Attribute atr = new Attribute();
				atr.setAttributeType(dataSet.getHeader().get(i));
				if(dataSet.getHeader().get(i)==AttributeType.NUMERIC){
					atr.setAttributeValue(Double.parseDouble(tokens[i]));
				}else if(dataSet.getHeader().get(i)==AttributeType.NOMINAL){
					// hardcoding for string format "Present"/"Absent"
					if(tokens[i].equalsIgnoreCase("present"))
						atr.setAttributeValue(1);
					else if(tokens[i].equalsIgnoreCase("absent")){
						atr.setAttributeValue(0);
					}else{
						throw new RuntimeException("Nominal Attribute other than Hardcoded 'Present' and 'Absent'");
					}
				}
				vector.addAttribute(atr);
			}
			//vector.setClassValue(Integer.parseInt(tokens[tokens.length-1]));
			dataSet.addInstance(vector);
		}
		testReader.close();
		return dataSet;
		
	}
	public static void main(String[] args) {
		DataFileReader ip = new DataFileReader("dataset2");
		try {
			Instances data=ip.loadDataFromFile();
			System.out.println(data.getDataSetSize());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}


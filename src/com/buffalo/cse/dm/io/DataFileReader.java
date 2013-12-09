package com.buffalo.cse.dm.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import com.buffalo.cse.dm.core.Attribute;
import com.buffalo.cse.dm.core.AttributeType;
import com.buffalo.cse.dm.core.HeaderFormat;
import com.buffalo.cse.dm.core.Instance;
import com.buffalo.cse.dm.core.Instances;

/**
 * 
 * @author pratikkathalkar Create list of Input Vector in a nDimensional space,
 *         from a input file.
 */
public class DataFileReader {
    private BufferedReader dataReader;
    private BufferedReader formatReader;
    private BufferedReader testReader;
    private String stemFileName;
    private String delimiter;

    public DataFileReader(String stemFileName, String delimiter) {
        assert (stemFileName != null);
        this.stemFileName = stemFileName;
        this.delimiter = delimiter;
    }

    public Instances loadDataFromFile() throws IOException {
        String line;
        dataReader = new BufferedReader(new FileReader(stemFileName + ".txt"));
        Instances dataSet = new Instances();
        HeaderFormat header = loadDataFormatFromFile();
        dataSet.setHeader(header);
        while ((line = dataReader.readLine()) != null) {
            String[] tokens = line.split(delimiter);
            Instance vector = new Instance(tokens.length - 1);
            for (int i = 0; i < tokens.length - 1; i++) {
                Attribute atr = new Attribute();
                atr.setAttributeType(header.getType(i));
                if (atr.getAttributeType() == AttributeType.NUMERIC) {
                    atr.setAttributeValue(Double.parseDouble(tokens[i]));
                } else if (atr.getAttributeType() == AttributeType.NOMINAL) {
                    // hardcoding for string format "Present"/"Absent"
                    // if (tokens[i].equalsIgnoreCase("present"))
                    // atr.setAttributeValue(1);
                    // else if (tokens[i].equalsIgnoreCase("absent")) {
                    // atr.setAttributeValue(0);
                    Map<String, Integer> values = header.getNominalValues(i);
                    atr.setAttributeValue(values.get(tokens[i].toLowerCase()));
                    /*
                     * } else { throw new RuntimeException(
                     * "Nominal Attribute other than Hardcoded 'Present' and 'Absent'"
                     * ); }
                     */
                }
                vector.addAttribute(atr);
            }
            vector.setClassValue(Integer.parseInt(tokens[tokens.length - 1]));
            dataSet.addInstance(vector);
        }
        dataReader.close();
        return dataSet;
    }

    private HeaderFormat loadDataFormatFromFile() throws IOException {
        String line;
        formatReader = new BufferedReader(new FileReader(stemFileName
                + ".format"));
        // List<AttributeType> header = new ArrayList<AttributeType>();
        HeaderFormat hFormat = new HeaderFormat();
        while ((line = formatReader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("nominal")) {
                String[] values = getNominalValues(line);
                hFormat.add(AttributeType.NOMINAL, values);
            } else if (line.startsWith("numeric")) {
                hFormat.add(AttributeType.NUMERIC, null);
            }
        }

        formatReader.close();
        return hFormat;
    }

    // will get a line of format nominal\t{val1,val2}
    private String[] getNominalValues(String line) {
        String[] tokens = line.split(delimiter);
        String[] values = tokens[1].substring(1, tokens[1].length() - 1).split(
                ",");
        return values;
    }

    public static void main(String[] args) {
        DataFileReader ip = new DataFileReader("dataset2", "\t");
        try {
            Instances data = ip.loadDataFromFile();
            System.out.println(data.getDataSetSize());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

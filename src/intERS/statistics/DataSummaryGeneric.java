package intERS.statistics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class DataSummaryGeneric implements DataSummaryInterface{
	
	private static final int									CYCLE							= 0;
	
	private static final int									TYPE							= 1;
	
	private static final int									FIELDS_DISREGARD	= 3;
	
	// cycle, type, object
	private Map<Integer, Map<String, Value>>	avgData;
	
	private Map<Integer, Map<String, Value>>	sumData;
	
	private String														header;
	
	private int																numFields;
	
	
	public DataSummaryGeneric(){
		this.avgData = new TreeMap<Integer, Map<String, Value>>();
		this.sumData = new TreeMap<Integer, Map<String, Value>>();
		
		this.header = null;
		this.numFields = 0;
	}
	
	
	@Override
	public boolean add(String filename, String fieldSeparator){
		boolean result = false;
		
		Path file = Paths.get(filename);
		BufferedReader reader;
		try{
			reader = Files.newBufferedReader(file, Charset.defaultCharset());
			
			// Header
			String line = reader.readLine();
			String[] tokens = line.split(fieldSeparator);
			
			if(this.numFields == 0){
				this.numFields = tokens.length - FIELDS_DISREGARD;
			}
			
			if(this.header == null){
				this.header = "cycle" + fieldSeparator + "type";
				for(int i = 0; i < this.numFields; i++){
					this.header += fieldSeparator + tokens[i + FIELDS_DISREGARD];
				}
			}
			
			Map<Integer, Map<String, Value>> values = new TreeMap<Integer, Map<String, Value>>();
			Map<String, Value> valueType;
			Value value;
			
			int cycle;
			String type;
			while((line = reader.readLine()) != null){
				tokens = line.split(fieldSeparator);
				
				cycle = Integer.parseInt(tokens[CYCLE]);
				type = tokens[TYPE];
				
				if(values.containsKey(cycle)){
					valueType = values.get(cycle);
				}else{
					valueType = new TreeMap<String, Value>();
				}
				
				if(valueType.containsKey(type)){
					value = valueType.get(type);
				}else{
					value = new Value(this.numFields);
				}
				
				for(int i = 0; i < this.numFields; i++){
					value.setValue(
							i,
							value.getValue(i)
									+ Double.parseDouble(tokens[i + FIELDS_DISREGARD]));
				}
				value.setNumber(value.getNumber() + 1);
				
				valueType.put(type, value);
				values.put(cycle, valueType);
			}
			
			Map<String, Value> avgValueType;
			Map<String, Value> sumValueType;
			
			Value avgValue;
			Value sumValue;
			for(Integer c : values.keySet()){
				
				if(this.avgData.containsKey(c)){
					avgValueType = this.avgData.get(c);
				}else{
					avgValueType = new TreeMap<String, Value>();
				}
				
				if(this.sumData.containsKey(c)){
					sumValueType = this.sumData.get(c);
				}else{
					sumValueType = new TreeMap<String, Value>();
				}
				
				valueType = values.get(c);
				for(String t : valueType.keySet()){
					value = valueType.get(t);
					
					if(avgValueType.containsKey(t)){
						avgValue = avgValueType.get(t);
					}else{
						avgValue = new Value(this.numFields);
					}
					
					if(sumValueType.containsKey(t)){
						sumValue = sumValueType.get(t);
					}else{
						sumValue = new Value(this.numFields);
					}
					
					for(int i = 0; i < this.numFields; i++){
						sumValue.setValue(i, sumValue.getValue(i) + value.getValue(i));
						
						avgValue.setValue(i, avgValue.getValue(i)
								+ (value.getValue(i) / (double) value.getNumber()));
					}
					sumValue.setNumber(sumValue.getNumber() + 1);
					avgValue.setNumber(avgValue.getNumber() + 1);
					
					avgValueType.put(t, avgValue);
					this.avgData.put(c, avgValueType);
					
					sumValueType.put(t, sumValue);
					this.sumData.put(c, sumValueType);
				}
			}
			
			result = true;
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	@Override
	public boolean writeAvg(String filename, String fieldSeparator){
		boolean result = false;
		
		Path file = Paths.get(filename);
		BufferedWriter writer;
		try{
			writer = Files.newBufferedWriter(file, Charset.defaultCharset());
			
			writer.write(this.header);
			writer.newLine();
			
			Map<String, Value> valuesType;
			Value value;
			String line;
			for(Integer cycle : this.avgData.keySet()){
				valuesType = this.avgData.get(cycle);
				
				for(String type : valuesType.keySet()){
					value = valuesType.get(type);
					
					line = cycle + fieldSeparator + type;
					for(int i = 0; i < this.numFields; i++){
						line += fieldSeparator
								+ String.format("%f",
										(value.getValue(i) / (double) value.getNumber()));
					}
					
					writer.write(line);
					writer.newLine();
				}
			}
			
			writer.close();
			result = true;
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	@Override
	public boolean writeSum(String filename, String fieldSeparator){
		boolean result = false;
		
		Path file = Paths.get(filename);
		BufferedWriter writer;
		try{
			writer = Files.newBufferedWriter(file, Charset.defaultCharset());
			
			writer.write(this.header);
			writer.newLine();
			
			Map<String, Value> valuesType;
			Value value;
			String line;
			for(Integer cycle : this.sumData.keySet()){
				valuesType = this.sumData.get(cycle);
				
				for(String type : valuesType.keySet()){
					value = valuesType.get(type);
					
					line = cycle + fieldSeparator + type;
					for(int i = 0; i < this.numFields; i++){
						line += fieldSeparator
								+ String.format("%f",
										(value.getValue(i) / (double) value.getNumber()));
					}
					
					writer.write(line);
					writer.newLine();
				}
			}
			
			writer.close();
			result = true;
		}catch(IOException e){
			e.printStackTrace();
		}
		
		return result;
	}
}


class Value{
	
	private Double[]	values;
	
	private int				number;
	
	
	public Value(int numValues){
		
		this.values = new Double[numValues];
		for(int i = 0; i < numValues; i++){
			this.values[i] = 0.0;
		}
		
		this.number = 0;
	}
	
	
	public Double[] getValues(){
		return this.values;
	}
	
	
	public void setValues(Double[] values){
		for(int i = 0; i < values.length; i++){
			this.values[i] = values[i].doubleValue();
		}
	}
	
	
	public double getValue(int index){
		return this.values[index].doubleValue();
	}
	
	
	public void setValue(int index, double value){
		this.values[index] = value;
	}
	
	
	public int getNumber(){
		return this.number;
	}
	
	
	public void setNumber(int number){
		this.number = number;
	}
}
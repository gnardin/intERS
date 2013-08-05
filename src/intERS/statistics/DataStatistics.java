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

public class DataStatistics {

	private static final int CYCLE = 0;
	private static final int TYPE = 1;

	private static final int FIELDS_DISREGARD = 3;

	// cycle, type, object
	private Map<Integer, Map<String, Double[]>> avgData;

	private Map<Integer, Map<String, Double[]>> sumData;

	private String fieldSeparator;

	private String header;

	private int numFields;

	private int numSims;

	public DataStatistics(int numSims, String fieldSeparator) {
		this.avgData = new TreeMap<Integer, Map<String, Double[]>>();
		this.sumData = new TreeMap<Integer, Map<String, Double[]>>();

		this.fieldSeparator = fieldSeparator;
		this.header = null;
		this.numFields = 0;
		this.numSims = numSims;
	}

	public void add(String filename) {
		Path file = Paths.get(filename);
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(file, Charset.defaultCharset());

			// Header
			String line = reader.readLine();
			String[] tokens = line.split(this.fieldSeparator);

			if (this.numFields == 0) {
				this.numFields = tokens.length - FIELDS_DISREGARD;
			}

			if (this.header == null) {
				this.header = "cycle" + this.fieldSeparator + "type";
				for (int i = 0; i < this.numFields; i++) {
					this.header += this.fieldSeparator
							+ tokens[i + FIELDS_DISREGARD];
				}
			}

			Map<Integer, Map<String, Double[]>> values = new TreeMap<Integer, Map<String, Double[]>>();
			Map<Integer, Map<String, Integer>> numbers = new TreeMap<Integer, Map<String, Integer>>();

			Map<String, Double[]> valueType;
			Map<String, Integer> numberType;

			Double[] value;
			int number;

			int cycle;
			String type;
			while ((line = reader.readLine()) != null) {
				tokens = line.split(this.fieldSeparator);

				cycle = Integer.parseInt(tokens[CYCLE]);
				type = tokens[TYPE];

				if (values.containsKey(cycle)) {
					valueType = values.get(cycle);
					numberType = numbers.get(cycle);
				} else {
					valueType = new TreeMap<String, Double[]>();
					numberType = new TreeMap<String, Integer>();
				}

				if (valueType.containsKey(type)) {
					value = valueType.get(type);
					number = numberType.get(type);
				} else {
					value = new Double[this.numFields];
					for (int i = 0; i < this.numFields; i++) {
						value[i] = 0.0;
					}
					number = 0;
				}

				for (int i = 0; i < this.numFields; i++) {
					value[i] += Double
							.parseDouble(tokens[i + FIELDS_DISREGARD]);
				}

				valueType.put(type, value);
				values.put(cycle, valueType);

				numberType.put(type, number + 1);
				numbers.put(cycle, numberType);
			}

			// Map<Integer, Map<String, Double[]>> avgValues = new
			// TreeMap<Integer, Map<String, Double[]>>();
			// Map<Integer, Map<String, Double[]>> sumValues = new
			// TreeMap<Integer, Map<String, Double[]>>();

			Map<String, Double[]> avgValueType;
			Map<String, Double[]> sumValueType;

			Double[] avgValue;
			Double[] sumValue;
			for (Integer c : values.keySet()) {

				if (this.avgData.containsKey(c)) {
					avgValueType = this.avgData.get(c);
				} else {
					avgValueType = new TreeMap<String, Double[]>();
				}

				if (this.sumData.containsKey(c)) {
					sumValueType = this.sumData.get(c);
				} else {
					sumValueType = new TreeMap<String, Double[]>();
				}

				numberType = numbers.get(c);
				valueType = values.get(c);
				for (String t : valueType.keySet()) {
					number = numberType.get(t);
					value = valueType.get(t);

					if (avgValueType.containsKey(t)) {
						avgValue = avgValueType.get(t);
					} else {
						avgValue = new Double[this.numFields];
						for (int i = 0; i < this.numFields; i++) {
							avgValue[i] = 0.0;
						}
					}

					if (sumValueType.containsKey(t)) {
						sumValue = sumValueType.get(t);
					} else {
						sumValue = new Double[this.numFields];
						for (int i = 0; i < this.numFields; i++) {
							sumValue[i] = 0.0;
						}
					}

					for (int i = 0; i < this.numFields; i++) {
						sumValue[i] += value[i];
						avgValue[i] += (value[i] / (double) number);
					}

					avgValueType.put(t, avgValue);
					this.avgData.put(c, avgValueType);

					sumValueType.put(t, sumValue);
					this.sumData.put(c, sumValueType);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeAvg(String filename) {
		Path file = Paths.get(filename);
		BufferedWriter writer;
		try {
			writer = Files.newBufferedWriter(file, Charset.defaultCharset());

			writer.write(this.header);
			writer.newLine();

			Map<String, Double[]> valuesType;
			Double[] value;
			String line;
			for (Integer cycle : this.avgData.keySet()) {
				valuesType = this.avgData.get(cycle);

				for (String type : valuesType.keySet()) {
					value = valuesType.get(type);

					line = cycle + this.fieldSeparator + type;
					for (int i = 0; i < this.numFields; i++) {
						line += this.fieldSeparator
								+ String.format("%f",
										(value[i] / (double) this.numSims));
					}

					writer.write(line);
					writer.newLine();
				}
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeSum(String filename) {
		Path file = Paths.get(filename);
		BufferedWriter writer;
		try {
			writer = Files.newBufferedWriter(file, Charset.defaultCharset());

			writer.write(this.header);
			writer.newLine();

			Map<String, Double[]> valuesType;
			Double[] value;
			String line;
			for (Integer cycle : this.sumData.keySet()) {
				valuesType = this.sumData.get(cycle);

				for (String type : valuesType.keySet()) {
					value = valuesType.get(type);

					line = cycle + this.fieldSeparator + type;
					for (int i = 0; i < this.numFields; i++) {
						line += this.fieldSeparator
								+ String.format("%f",
										(value[i] / (double) this.numSims));
					}

					writer.write(line);
					writer.newLine();
				}
			}

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
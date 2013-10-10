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

public class DataSummaryExtorter implements DataSummaryInterface {

	private enum FIELDS_INPUT {
		CYCLE("cycle"), TYPE("type"), ID("id"), WEALTH("wealth"), NUM_TARGETS(
				"numTargets"), NUM_EXTORTION("numExtortion"), TOTAL_EXTORTION(
				"totalExtortion"), NUM_HELP_REQUESTED("numHelpRequested"), NUM_HELP_PROVIDED(
				"numHelpProvided"), NUM_RETALIATION("numRetaliation"), NUM_RECEIVED_RETALIATION(
				"numReceivedRetaliation"), NUM_COUNTERATTACK("numCounterattack"), NUM_RECEIVED_COUNTERATTACK(
				"numReceivedCounterattack"), TOTAL_LOST_FIGHT("totalLostFight"), NUM_EXTORTION_RECEIVED(
				"numExtortionReceived"), TOTAL_EXTORTION_RECEIVED(
				"totalExtortionReceived"), NUM_PUNISHMENT("numPunishment"), TOTAL_LOST_PUNISHMENT(
				"totalLostPunishment");

		private final String header;

		FIELDS_INPUT(String header) {
			this.header = header;
		}

		private String getHeader() {
			return this.header;
		}
	};

	private static final int FIELDS_DISREGARD = 3;

	// cycle, type, object
	private Map<Integer, Map<String, Double[]>> avgData;

	private Map<Integer, Map<String, Double[]>> sumData;

	private String fieldSeparator;

	private String header;

	private int numFields;

	private int numSims;

	public DataSummaryExtorter(Integer numSims, String fieldSeparator) {
		this.avgData = new TreeMap<Integer, Map<String, Double[]>>();
		this.sumData = new TreeMap<Integer, Map<String, Double[]>>();

		this.fieldSeparator = fieldSeparator;

		this.header = "";
		for (FIELDS_INPUT field : FIELDS_INPUT.values()) {
			this.header = this.header + field.getHeader() + this.fieldSeparator;
		}
		this.header = this.header.substring(0, this.header.length() - 1);

		this.numFields = FIELDS_INPUT.values().length - FIELDS_DISREGARD;
		this.numSims = numSims;
	}

	@Override
	public boolean add(String filename) {
		boolean result = false;

		Path file = Paths.get(filename);
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(file, Charset.defaultCharset());

			// Header
			String line = reader.readLine();
			String[] tokens = line.split(this.fieldSeparator);

			if (tokens.length != FIELDS_INPUT.values().length) {

				Map<Integer, Map<String, Double[]>> values = new TreeMap<Integer, Map<String, Double[]>>();
				Map<Integer, Map<String, Integer[]>> numbers = new TreeMap<Integer, Map<String, Integer[]>>();

				Map<String, Double[]> valueType;
				Map<String, Integer[]> numberType;

				Double[] value;
				Integer[] number;

				int cycle;
				String type;
				while ((line = reader.readLine()) != null) {
					tokens = line.split(this.fieldSeparator);

					cycle = Integer.parseInt(tokens[FIELDS_INPUT.CYCLE.ordinal()]);
					type = tokens[FIELDS_INPUT.TYPE.ordinal()];

					if (values.containsKey(cycle)) {
						valueType = values.get(cycle);
						numberType = numbers.get(cycle);
					} else {
						valueType = new TreeMap<String, Double[]>();
						numberType = new TreeMap<String, Integer[]>();
					}

					if (valueType.containsKey(type)) {
						value = valueType.get(type);
						number = numberType.get(type);
					} else {
						value = new Double[this.numFields];
						number = new Integer[this.numFields];
						for (int i = 0; i < this.numFields; i++) {
							value[i] = 0.0;
							number[i] = 0;
						}
					}

					int id;
					for (FIELDS_INPUT field : FIELDS_INPUT.values()) {
						id = field.ordinal();

						if (field.equals(FIELDS_INPUT.NUM_HELP_REQUESTED)) {

							if (value[id] > 0) {
								value[id] += Double.parseDouble(tokens[id]);
								number[id] += 1;
							}

						} else if (field.equals(FIELDS_INPUT.NUM_HELP_PROVIDED)) {

						} else {
							value[id] += Double.parseDouble(tokens[id]);
							number[id] += 1;
						}
					}

					valueType.put(type, value);
					values.put(cycle, valueType);

					numberType.put(type, number);
					numbers.put(cycle, numberType);
				}

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
							avgValue[i] += (value[i] / (double) number[i]);
						}

						avgValueType.put(t, avgValue);
						this.avgData.put(c, avgValueType);

						sumValueType.put(t, sumValue);
						this.sumData.put(c, sumValueType);
					}
				}

				result = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public boolean writeAvg(String filename) {
		boolean result = false;

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

			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public boolean writeSum(String filename) {
		boolean result = false;

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
			result = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
}
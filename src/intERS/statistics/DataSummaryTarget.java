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

public class DataSummaryTarget implements DataSummaryInterface {

	private enum FIELDS_INPUT {
		CYCLE("cycle"), TYPE("type"), ID("id"), WEALTH("wealth"), INCOME(
				"income"), NUM_EXTORTION("numExtortion"), TOTAL_EXTORTION(
				"totalExtortion"), NUM_PROTECTION_REQUESTED(
				"numProtectionRequested"), NUM_PROTECTION_RECEIVED(
				"numProtectionReceived"), NUM_PAID("numPaid"), TOTAL_PAID(
				"totalPaid"), NUM_NOT_PAID("numNotPaid"), TOTAL_NOT_PAID(
				"totalNotPaid"), NUM_PUNISHMENT("numPunishment"), TOTAL_PUNISHMENT(
				"totalPunishment");

		private final String header;

		FIELDS_INPUT(String header) {
			this.header = header;
		}

		private String getHeader() {
			return this.header;
		}
	};

	private enum FIELDS_OUTPUT {
		CYCLE("cycle"), TYPE("type"), WEALTH("wealth"), INCOME("income"), NUM_EXTORTION(
				"numExtortion"), TOTAL_EXTORTION("totalExtortion"), NUM_PROTECTION_REQUESTED(
				"numProtectionRequested"), NUM_PROTECTION_RECEIVED(
				"numProtectionReceived"), NUM_PAID("numPaid"), TOTAL_PAID(
				"totalPaid"), NUM_NOT_PAID("numNotPaid"), TOTAL_NOT_PAID(
				"totalNotPaid"), NUM_PUNISHMENT("numPunishment"), TOTAL_PUNISHMENT(
				"totalPunishment"), SUCCESSFUL_PROTECTION(
				"successfulProtection"), REQUESTED_PROTECTION(
				"requestedProtection");

		private final String header;

		FIELDS_OUTPUT(String header) {
			this.header = header;
		}

		private String getHeader() {
			return this.header;
		}
	};

	private static final int FIELDS_DISREGARD = 3;
	private static final int EXTRA_FIELDS = 2;

	// cycle, type, object
	private Map<Integer, Map<String, Double[]>> avgData;

	private Map<Integer, Map<String, Double[]>> sumData;

	private String fieldSeparator;

	private String headerInput;

	private String headerOutput;

	private int numFields;

	private int numSims;

	public DataSummaryTarget(Integer numSims, String fieldSeparator) {
		this.avgData = new TreeMap<Integer, Map<String, Double[]>>();
		this.sumData = new TreeMap<Integer, Map<String, Double[]>>();

		this.fieldSeparator = fieldSeparator;

		// HEADER Input
		this.headerInput = "";
		for (FIELDS_INPUT field : FIELDS_INPUT.values()) {
			this.headerInput = this.headerInput + field.getHeader()
					+ this.fieldSeparator;
		}
		this.headerInput = this.headerInput.substring(0,
				this.headerInput.length() - 1);

		// HEADER Output
		this.headerOutput = "";
		for (FIELDS_OUTPUT field : FIELDS_OUTPUT.values()) {
			this.headerOutput = this.headerOutput + field.getHeader()
					+ this.fieldSeparator;
		}
		this.headerOutput = this.headerOutput.substring(0,
				this.headerOutput.length() - 1);

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

			if (tokens.length == FIELDS_INPUT.values().length) {

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

					cycle = Integer.parseInt(tokens[FIELDS_INPUT.CYCLE
							.ordinal()]);
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
						value = new Double[this.numFields + EXTRA_FIELDS];
						number = new Integer[this.numFields + EXTRA_FIELDS];
						for (int i = 0; i < Math.max(value.length,
								number.length); i++) {
							value[i] = 0.0;
							number[i] = 0;
						}
					}

					for (int i = 0; i < this.numFields; i++) {
						value[i] += Double.parseDouble(tokens[i
								+ FIELDS_DISREGARD]);
						number[i] += 1;
					}

					// Extra fields
					int extra_index = this.numFields;
					// PROTECTION SUCCESSFULL
					if (Double
							.parseDouble(tokens[FIELDS_INPUT.NUM_PROTECTION_REQUESTED
									.ordinal()]) > 0.0) {
						if ((Double
								.parseDouble(tokens[FIELDS_INPUT.NUM_PROTECTION_RECEIVED
										.ordinal()]) > 0.0)
								&& (Double
										.parseDouble(tokens[FIELDS_INPUT.NUM_PUNISHMENT
												.ordinal()]) == 0.0)) {
							value[extra_index] += 1.0;
						}

						number[extra_index] += 1;

						value[extra_index + 1] += 1;
						number[extra_index + 1] += 1;
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
							avgValue = new Double[this.numFields + EXTRA_FIELDS];
							for (int i = 0; i < avgValue.length; i++) {
								avgValue[i] = 0.0;
							}
						}

						if (sumValueType.containsKey(t)) {
							sumValue = sumValueType.get(t);
						} else {
							sumValue = new Double[this.numFields + EXTRA_FIELDS];
							for (int i = 0; i < sumValue.length; i++) {
								sumValue[i] = 0.0;
							}
						}

						for (int i = 0; i < (this.numFields + EXTRA_FIELDS); i++) {
							sumValue[i] += value[i];

							if (number[i] != 0) {
								avgValue[i] += (value[i] / (double) number[i]);
							} else {
								avgValue[i] = 0.0;
							}
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

			writer.write(this.headerOutput);
			writer.newLine();

			Map<String, Double[]> valuesType;
			Double[] value;
			String line;
			for (Integer cycle : this.avgData.keySet()) {
				valuesType = this.avgData.get(cycle);

				for (String type : valuesType.keySet()) {
					value = valuesType.get(type);

					line = cycle + this.fieldSeparator + type;
					for (int i = 0; i < (this.numFields + EXTRA_FIELDS); i++) {
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

			writer.write(this.headerOutput);
			writer.newLine();

			Map<String, Double[]> valuesType;
			Double[] value;
			String line;
			for (Integer cycle : this.sumData.keySet()) {
				valuesType = this.sumData.get(cycle);

				for (String type : valuesType.keySet()) {
					value = valuesType.get(type);

					line = cycle + this.fieldSeparator + type;
					for (int i = 0; i < (this.numFields + EXTRA_FIELDS); i++) {
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
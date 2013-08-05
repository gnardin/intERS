package intERS.statistics;

import java.io.File;

public class DataAggregation {

	private int numSims;
	private String baseDirectory;
	private String outputFieldSeparator;

	public DataAggregation(int numSims, String baseDirectory,
			String outputFieldSeparator) {
		this.numSims = numSims;
		this.baseDirectory = baseDirectory;
		this.outputFieldSeparator = outputFieldSeparator;
	}

	/**
	 * Aggregate data
	 * 
	 * @param rawFile
	 *            Raw file to process
	 * @param avgFilename
	 *            Output filename containing the average values
	 * @param sumFilename
	 *            Output filename containing the sum values
	 */
	public void aggregate(String rawFile, String avgFilename, String sumFilename) {

		DataStatistics summary = new DataStatistics(this.numSims,
				this.outputFieldSeparator);
		String filename;
		for (int sim = 0; sim < this.numSims; sim++) {
			filename = this.baseDirectory + File.separator + (sim + 1)
					+ File.separator + rawFile;

			summary.add(filename);
		}

		summary.writeAvg(this.baseDirectory + File.separator + avgFilename);
		summary.writeSum(this.baseDirectory + File.separator + sumFilename);
	}
}
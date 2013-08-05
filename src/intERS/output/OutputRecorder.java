package intERS.output;

import intERS.conf.simulation.OutputConf;
import intERS.objects.ExtorterObject;
import intERS.objects.ObjectAbstract;
import intERS.objects.ObserverObject;
import intERS.objects.TargetObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class OutputRecorder {

	private static volatile OutputRecorder instance = null;

	private BufferedWriter fExtorter;

	private BufferedWriter fObserver;

	private BufferedWriter fTarget;

	private boolean firstExtorter;

	private boolean firstObserver;

	private boolean firstTarget;

	private OutputConf output;

	private Map<Integer, List<ObjectAbstract>> records;

	/**
	 * Constructor
	 * 
	 * @param none
	 * @return none
	 */
	private OutputRecorder() {
		this.records = new TreeMap<Integer, List<ObjectAbstract>>();
	}

	public void setOutput(OutputConf output) {
		this.output = output;

		this.firstExtorter = true;
		this.firstObserver = true;
		this.firstTarget = true;

		File directory = new File(this.output.getDirectory());
		directory.mkdirs();

		try {
			this.fExtorter = new BufferedWriter(new FileWriter(new File(
					this.output.getDirectory() + File.separator
							+ this.output.getFileExtorter()),
					this.output.getFileAppend()));

			this.fObserver = new BufferedWriter(new FileWriter(new File(
					this.output.getDirectory() + File.separator
							+ this.output.getFileObserver()),
					this.output.getFileAppend()));

			this.fTarget = new BufferedWriter(new FileWriter(new File(
					this.output.getDirectory() + File.separator
							+ this.output.getFileTarget()),
					this.output.getFileAppend()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get an OutputRecorder instance
	 * 
	 * @param none
	 * @return OutputRecorder instance
	 */
	public static OutputRecorder getInstance() {
		if (instance == null) {
			synchronized (OutputRecorder.class) {
				if (instance == null) {
					instance = new OutputRecorder();
				}
			}
		}
		return instance;
	}

	/**
	 * Add a record to record
	 * 
	 * @param record
	 *            Record output
	 * @return none
	 */
	public synchronized void addRecord(ObjectAbstract record) {
		List<ObjectAbstract> outputs = this.records.get(record.getCycle());

		if (outputs == null) {
			outputs = new ArrayList<ObjectAbstract>();
		}

		outputs.add(record);
		this.records.put(record.getCycle(), outputs);
	}

	/**
	 * Write data to disk
	 * 
	 * @param none
	 * @return none
	 */
	public synchronized void write() throws IOException {

		List<ObjectAbstract> outputs;
		for (Integer cycle : this.records.keySet()) {
			outputs = this.records.get(cycle);

			for (ObjectAbstract record : outputs) {

				if (record instanceof ExtorterObject) {
					if (this.firstExtorter) {
						this.fExtorter.write("cycle"
								+ this.output.getFieldSeparator()
								+ record.getHeader(this.output
										.getFieldSeparator()));
						this.fExtorter.newLine();
						this.firstExtorter = false;
					}

					this.fExtorter.write(cycle
							+ this.output.getFieldSeparator()
							+ record.getLine(this.output.getFieldSeparator()));
					this.fExtorter.newLine();

				} else if (record instanceof ObserverObject) {
					if (this.firstObserver) {
						this.fObserver.write("cycle"
								+ this.output.getFieldSeparator()
								+ record.getHeader(this.output
										.getFieldSeparator()));
						this.fObserver.newLine();
						this.firstObserver = false;
					}

					this.fObserver.write(cycle
							+ this.output.getFieldSeparator()
							+ record.getLine(this.output.getFieldSeparator()));
					this.fObserver.newLine();

				} else if (record instanceof TargetObject) {
					if (this.firstTarget) {
						this.fTarget.write("cycle"
								+ this.output.getFieldSeparator()
								+ record.getHeader(this.output
										.getFieldSeparator()));
						this.fTarget.newLine();
						this.firstTarget = false;
					}

					this.fTarget.write(cycle + this.output.getFieldSeparator()
							+ record.getLine(this.output.getFieldSeparator()));
					this.fTarget.newLine();
				}
			}
		}

		this.fExtorter.flush();
		this.fObserver.flush();
		this.fTarget.flush();

		this.records.clear();
	}

	/**
	 * Close files
	 * 
	 * @param none
	 * @return none
	 */
	public void close() throws IOException {
		this.write();
		this.fExtorter.close();
		this.fObserver.close();
		this.fTarget.close();
	}
}
package intERS.main;

import intERS.conf.simulation.SimulationConf;
import intERS.statistics.DataAggregation;
import intERS.utils.XML;

public class IntERS {

	public static void main(String[] args) throws Exception {

		// Validate the Arguments and Simulation XML file
		if (args.length < 2) {
			System.out.println("Invalid Arguments");
			System.exit(1);
		} else if (!XML.isValid(args[0], args[1])) {
			System.out.println("Invalid XML");
			System.exit(1);
		}
		SimulationConf simulation = SimulationConf.simulationParser(args[0]);

		IntERSRunner runner = new IntERSRunner();

		int randomSeed;
		// Runs the simulation
		for (int sim = 0; sim < simulation.getNumberRuns(); sim++) {

			randomSeed = simulation.getSeed(sim);

			runner.load((sim + 1), randomSeed, simulation.getRSDirectory(),
					simulation.getXMLFilename(), simulation.getXSDFilename(),
					simulation.getOutput().getDirectory(), simulation
							.getOutput().getFileExtorter(), simulation
							.getOutput().getFileObserver(), simulation
							.getOutput().getFileTarget(), simulation
							.getOutput().getFileAppend(), simulation
							.getOutput().getFieldSeparator(), simulation
							.getOutput().getWriteEvery());

			runner.runInitialize();

			while (true) {
				if (runner.getModelActionCount() == 0) {
					break;
				}
				runner.step();
			}

			runner.stop();
			runner.cleanUpRun();
		}
		runner.cleanUpBatch();

		// Data aggregation
		DataAggregation summary = new DataAggregation(
				simulation.getNumberRuns(), simulation.getOutput()
						.getDirectory(), simulation.getOutput()
						.getFieldSeparator());

		summary.aggregate(simulation.getOutput().getClassExtorterStat(),
				simulation.getOutput().getFileExtorter(), simulation
						.getOutput().getFilePrefixAvg()
						+ simulation.getOutput().getFileExtorter(), simulation
						.getOutput().getFilePrefixSum()
						+ simulation.getOutput().getFileExtorter());

		summary.aggregate(simulation.getOutput().getClassObserverStat(),
				simulation.getOutput().getFileObserver(), simulation
						.getOutput().getFilePrefixAvg()
						+ simulation.getOutput().getFileObserver(), simulation
						.getOutput().getFilePrefixSum()
						+ simulation.getOutput().getFileObserver());

		summary.aggregate(simulation.getOutput().getClassTargetStat(),
				simulation.getOutput().getFileTarget(), simulation.getOutput()
						.getFilePrefixAvg()
						+ simulation.getOutput().getFileTarget(), simulation
						.getOutput().getFilePrefixSum()
						+ simulation.getOutput().getFileTarget());
	}
}
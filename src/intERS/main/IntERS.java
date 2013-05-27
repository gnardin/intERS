package intERS.main;

import intERS.conf.simulation.SimulationConf;
import intERS.utils.XML;

import java.io.File;

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
		String outputDirectory;
		// Number of Runs
		for (int sim = 0; sim < simulation.getNumberRuns(); sim++) {

			randomSeed = simulation.getSeed(sim);
			outputDirectory = simulation.getOutput().getDirectory()
					+ File.separator + (sim + 1);

			runner.load(randomSeed, simulation.getRSDirectory(), simulation
					.getXMLFilename(), simulation.getXSDFilename(),
					outputDirectory, simulation.getOutput().getFileExtorter(),
					simulation.getOutput().getFileObserver(), simulation
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
	}
}
package intERS.batch;

import intERS.main.IntERS;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BatchHeterogeneous {

	public static void main(String[] args) {

		String confDir = "/data/workspace/gloders/intERS/conf/";
		String outDir = "./output/withProtection";
		String simulationXML = "simulation.xml";
		String simulationXSD = "simulation.xsd";
		String scenarioXML = "scenario.xml";
		String batchSimulationXML = "batchSimulation.xml";
		String batchScenarioXML = "batchScenario.xml";

		String enlargement[] = { "10" };
		String tolerance[] = { "40" };
		String extortion[][] = { { "10", "20" } };
		String punishment[][] = { { "40", "80" } };
		// String enlargement[] = { "10", "40", "80" };
		// String tolerance[] = { "10", "40", "80" };
		// String extortion[][] = { { "10", "20" }, { "20", "40" },
		// { "30", "60" }, { "40", "80" }, { "50", "100" } };
		// String punishment[][] = { { "20", "40" }, { "30", "60" },
		// { "40", "80" }, { "50", "100" } };

		Path readFile;
		Path writeFile;
		BufferedReader reader;
		BufferedWriter writer;
		String line;
		int numLine;
		for (int to = 0; to < tolerance.length; to++) {
			for (int en = 0; en < enlargement.length; en++) {
				for (int pu = 0; pu < punishment.length; pu++) {
					for (int ex = 0; ex < extortion.length; ex++) {
						try {
							// SIMULATION XML
							readFile = Paths.get(confDir + simulationXML);
							writeFile = Paths.get(confDir + batchSimulationXML);

							reader = Files.newBufferedReader(readFile,
									Charset.defaultCharset());
							writer = Files.newBufferedWriter(writeFile,
									Charset.defaultCharset());

							numLine = 1;
							while ((line = reader.readLine()) != null) {
								if (numLine == 209) {
									line = "<xmlFilename>./conf/"
											+ batchScenarioXML
											+ "</xmlFilename>";
								}

								if (numLine == 212) {
									line = "<directory>" + outDir + "/En"
											+ enlargement[en] + "_To"
											+ tolerance[to] + "/Ex"
											+ extortion[ex][0] + "-"
											+ extortion[ex][1] + "_Pu"
											+ punishment[pu][0] + "-"
											+ punishment[pu][1] + "_En"
											+ enlargement[en] + "_To"
											+ tolerance[to]
											+ "/LL-LH-HL-HH</directory>";
								}

								writer.write(line);
								writer.newLine();

								numLine++;
							}
							reader.close();
							writer.close();

							// SCENARIO XML
							readFile = Paths.get(confDir + scenarioXML);
							writeFile = Paths.get(confDir + batchScenarioXML);

							reader = Files.newBufferedReader(readFile,
									Charset.defaultCharset());
							writer = Files.newBufferedWriter(writeFile,
									Charset.defaultCharset());

							numLine = 1;
							while ((line = reader.readLine()) != null) {

								if ((numLine == 37) || (numLine == 56)
										|| (numLine == 75) || (numLine == 94)) {
									line = "<enlargementProbability>"
											+ enlargement[en]
											+ "</enlargementProbability>";
								}

								if ((numLine == 40) || (numLine == 59)
										|| (numLine == 78) || (numLine == 97)) {
									line = "<tolerance>" + tolerance[to]
											+ "</tolerance>";
								}

								if ((numLine == 48) || (numLine == 67)) {
									line = "<extortion>" + extortion[ex][0]
											+ "</extortion>";
								}

								if ((numLine == 86) || (numLine == 105)) {
									line = "<extortion>" + extortion[ex][1]
											+ "</extortion>";
								}

								if ((numLine == 51) || (numLine == 89)) {
									line = "<punishment>" + punishment[pu][0]
											+ "</punishment>";
								}

								if ((numLine == 70) || (numLine == 108)) {
									line = "<punishment>" + punishment[pu][1]
											+ "</punishment>";
								}

								writer.write(line);
								writer.newLine();

								numLine++;
							}
							reader.close();
							writer.close();

							String[] arguments = new String[] {
									confDir + batchSimulationXML,
									confDir + simulationXSD };

							IntERS.main(arguments);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
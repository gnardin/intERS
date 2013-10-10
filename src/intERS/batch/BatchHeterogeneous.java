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

		String simulationXML = "/data/workspace/gloders/intERS/conf/simulation.xml";
		String simulationXSD = "/data/workspace/gloders/intERS/conf/simulation.xsd";
		String scenarioXML = "/data/workspace/gloders/intERS/conf/scenario.xml";
		String batchSimulationXML = "/data/workspace/gloders/intERS/conf/batchSimulation.xml";
		String batchScenarioXML = "/data/workspace/gloders/intERS/conf/batchScenario.xml";

		String tolerance[] = { "80" };
		String enlargement[] = { "10", "40" };
		String punishment[][] = { { "20", "40" }, { "30", "60" },
				{ "40", "80" }, { "50", "100" } };
		String extortion[][] = { { "10", "20" }, { "20", "40" },
				{ "30", "60" }, { "40", "80" }, { "50", "100" } };

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
							readFile = Paths.get(simulationXML);
							writeFile = Paths.get(batchSimulationXML);

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
									line = "<directory>./output/En"
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
							readFile = Paths.get(scenarioXML);
							writeFile = Paths.get(batchScenarioXML);

							reader = Files.newBufferedReader(readFile,
									Charset.defaultCharset());
							writer = Files.newBufferedWriter(writeFile,
									Charset.defaultCharset());

							numLine = 1;
							while ((line = reader.readLine()) != null) {

								if ((numLine == 32) || (numLine == 47)
										|| (numLine == 62) || (numLine == 77)) {
									line = "<enlargementProbability>"
											+ enlargement[en]
											+ "</enlargementProbability>";
								}

								if ((numLine == 35) || (numLine == 50)
										|| (numLine == 65) || (numLine == 80)) {
									line = "<tolerance>" + tolerance[to]
											+ "</tolerance>";
								}

								if ((numLine == 40) || (numLine == 55)) {
									line = "<extortion>" + extortion[ex][0]
											+ "</extortion>";
								}

								if ((numLine == 70) || (numLine == 85)) {
									line = "<extortion>" + extortion[ex][1]
											+ "</extortion>";
								}

								if ((numLine == 43) || (numLine == 73)) {
									line = "<punishment>" + punishment[pu][0]
											+ "</punishment>";
								}

								if ((numLine == 58) || (numLine == 88)) {
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
									batchSimulationXML, simulationXSD };

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
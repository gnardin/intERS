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
		String outDir = "./output/newCombinatorial/protection/Pu40-80";
		String simulationXML = "simulation.xml";
		String simulationXSD = "simulation.xsd";
		String scenarioXML = "scenario.xml";
		String batchSimulationXML = "batchSimulation.xml";
		String batchScenarioXML = "batchScenario.xml";

		String enlargement[] = { "10" };
		String tolerance[] = { "40" };
		String extortion[][] = { { "10", "20" }, { "10", "30" },
				{ "10", "40" }, { "10", "50" }, { "10", "60" }, { "10", "70" },
				{ "10", "80" }, { "10", "90" }, { "10", "100" },
				{ "20", "30" }, { "20", "40" }, { "20", "50" }, { "20", "60" },
				{ "20", "70" }, { "20", "80" }, { "20", "90" },
				{ "20", "100" }, { "30", "40" }, { "30", "50" },
				{ "30", "60" }, { "30", "70" }, { "30", "80" }, { "30", "90" },
				{ "30", "100" }, { "40", "50" }, { "40", "60" },
				{ "40", "70" }, { "40", "80" }, { "40", "90" },
				{ "40", "100" }, { "50", "60" }, { "50", "70" },
				{ "50", "80" }, { "50", "90" }, { "50", "100" },
				{ "60", "70" }, { "60", "80" }, { "60", "90" },
				{ "60", "100" }, { "70", "80" }, { "70", "90" },
				{ "70", "100" }, { "80", "90" }, { "80", "100" },
				{ "90", "100" } };

		// String extortion[][] = { { "80", "100" }, { "90", "100" } };

		String punishment[][] = { { "40", "80" } };

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
									// line = "<directory>" + outDir + "/En"
									// + enlargement[en] + "_To"
									// + tolerance[to] + "/Ex"
									// + extortion[ex][0] + "-"
									// + extortion[ex][1] + "_Pu"
									// + punishment[pu][0] + "-"
									// + punishment[pu][1] + "_En"
									// + enlargement[en] + "_To"
									// + tolerance[to]
									// + "/LL-LH-HL-HH</directory>";
									line = "<directory>" + outDir + "/Ex"
											+ extortion[ex][0] + "-"
											+ extortion[ex][1] + "_Pu"
											+ punishment[pu][0] + "-"
											+ punishment[pu][1] + "_En"
											+ enlargement[en] + "_To"
											+ tolerance[to] + "</directory>";
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

								if ((numLine == 38) || (numLine == 58)
										|| (numLine == 78) || (numLine == 98)) {
									line = "<enlargementProbability>"
											+ enlargement[en]
											+ "</enlargementProbability>";
								}

								if ((numLine == 41) || (numLine == 61)
										|| (numLine == 81) || (numLine == 101)) {
									line = "<tolerance>" + tolerance[to]
											+ "</tolerance>";
								}

								if ((numLine == 49) || (numLine == 69)) {
									line = "<extortion>" + extortion[ex][0]
											+ "</extortion>";
								}

								if ((numLine == 89) || (numLine == 109)) {
									line = "<extortion>" + extortion[ex][1]
											+ "</extortion>";
								}

								if ((numLine == 52) || (numLine == 92)) {
									line = "<punishment>" + punishment[pu][0]
											+ "</punishment>";
								}

								if ((numLine == 72) || (numLine == 112)) {
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
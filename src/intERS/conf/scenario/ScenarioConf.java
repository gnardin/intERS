package intERS.conf.scenario;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class ScenarioConf {

	private final static String SCENARIO = "scenario";
	private final static String STOP_AT = "stopAt";
	// Prison
	private final static String PRISON_PROBABILITY = "probability";
	private final static String PRISON_ROUNDS = "rounds";
	// Target
	private final static String TARGET = "target";
	private final static String TARGET_TYPE = "targetType";
	private final static String TARGET_NUMBER = "numberOfTargets";
	private final static String TARGET_EXTORTERS = "extorterPerTarget";
	private final static String TARGET_MINIMUM_INCOME = "minimumIncome";
	private final static String TARGET_MAXIMUM_INCOME = "maximumIncome";
	private final static String TARGET_MINIMUM_EXTORTION = "minimumExtortion";
	private final static String TARGET_MAXIMUM_EXTORTION = "maximumExtortion";
	private final static String TARGET_MEMORY_LENGTH = "memoryLength";
	// Extorter
	private final static String TARGET_PER_EXTORTERS = "targetPerExtorter";
	private final static String EXTORTER = "extorter";
	private final static String EXTORTER_TYPE = "extorterType";
	private final static String EXTORTER_ENLARGEMENT_PROBABILITY = "enlargementProbability";
	private final static String EXTORTER_NUMBER = "numberOfExtorters";
	private final static String EXTORTER_INITIAL_WEALTH = "initialWealth";
	private final static String EXTORTER_TOLERANCE = "tolerance";
	private final static String EXTORTER_RETALIATION = "retaliation";
	private final static String EXTORTER_COUNTERATTACK = "counterattack";
	private final static String EXTORTER_COST_FIGHT = "costOfFight";
	private final static String EXTORTER_EXTORTION_TYPE = "extortionType";
	private final static String EXTORTER_EXTORTION = "extortion";
	private final static String EXTORTER_PUNISHMENT_TYPE = "punishmentType";
	private final static String EXTORTER_PUNISHMENT_COST = "punishmentCost";
	private final static String EXTORTER_PUNISHMENT = "punishment";
	private final static String EXTORTER_PUNISHMENT_MINIMUM_ESCALATION = "escalationMinimun";
	private final static String EXTORTER_PUNISHMENT_MAXIMUM_ESCALATION = "escalationMaximum";
	private final static String EXTORTER_PUNISHMENT_FORMULA_ESCALATION = "escalationFormula";

	// Scenario
	private String stopAt;

	// Prison information
	private double prisonProbability;
	private int prisonRounds;

	// Target information
	private List<TargetConf> targets;

	// Extorter information
	private List<ExtorterConf> extorters;
	private String targetPerExtorter;

	public ScenarioConf() {
		this.extorters = new ArrayList<ExtorterConf>();
		this.targets = new ArrayList<TargetConf>();
	}

	public static ScenarioConf scenarioParser(String scenarioFilename) {
		ScenarioConf scenario = null;
		ExtorterConf extorter = null;
		TargetConf target = null;

		try {
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();

			// Setup a new eventReader
			InputStream in = new FileInputStream(scenarioFilename);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

			// Read the XML document
			XMLEvent event;
			StartElement startElement;
			EndElement endElement;
			while (eventReader.hasNext()) {
				event = eventReader.nextEvent();

				if (event.isStartElement()) {
					startElement = event.asStartElement();

					// Create a Scenario
					if (startElement.getName().getLocalPart() == (SCENARIO)) {
						scenario = new ScenarioConf();
						continue;

						// Set stopAt attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(STOP_AT)) {
						event = eventReader.nextEvent();
						scenario.setStopAt(event.asCharacters().getData());
						continue;

						// Set prisonProbability attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(PRISON_PROBABILITY)) {
						event = eventReader.nextEvent();
						scenario.setPrisonProbability(new Double(event
								.asCharacters().getData()));
						continue;

						// Set prisonRounds attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(PRISON_ROUNDS)) {
						event = eventReader.nextEvent();
						scenario.setPrisonRounds(new Integer(event
								.asCharacters().getData()));
						continue;

						// Create a Target
					} else if (startElement.getName().getLocalPart() == (TARGET)) {
						target = new TargetConf();
						continue;

						// Set targetType attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(TARGET_TYPE)) {
						event = eventReader.nextEvent();
						target.setType(event.asCharacters().getData());
						continue;

						// Set numberTargets attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(TARGET_NUMBER)) {
						event = eventReader.nextEvent();
						target.setNumberTargets(new Integer(event
								.asCharacters().getData()));
						continue;

						// Set extorterPerTarget attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(TARGET_EXTORTERS)) {
						event = eventReader.nextEvent();
						target.setExtorterPerTarget(new Integer(event
								.asCharacters().getData()));
						continue;

						// Set MinIncome attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(TARGET_MINIMUM_INCOME)) {
						event = eventReader.nextEvent();
						target.setMinIncome(new Double(event.asCharacters()
								.getData()));
						continue;

						// Set MaxIncome attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(TARGET_MAXIMUM_INCOME)) {
						event = eventReader.nextEvent();
						target.setMaxIncome(new Double(event.asCharacters()
								.getData()));
						continue;

						// Set MinExtortion attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(TARGET_MINIMUM_EXTORTION)) {
						event = eventReader.nextEvent();
						target.setMinExtortion(new Double(event.asCharacters()
								.getData()));
						continue;

						// Set MaxExtortion attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(TARGET_MAXIMUM_EXTORTION)) {
						event = eventReader.nextEvent();
						target.setMaxExtortion(new Double(event.asCharacters()
								.getData()));
						continue;

						// Set MemLength attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(TARGET_MEMORY_LENGTH)) {
						event = eventReader.nextEvent();
						target.setMemLength(new Integer(event.asCharacters()
								.getData()));
						continue;

						// Set targetPerExtorter attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(TARGET_PER_EXTORTERS)) {
						event = eventReader.nextEvent();
						scenario.setTargetPerExtorter(event.asCharacters()
								.getData());
						continue;

						// Create a Extorter
					} else if (startElement.getName().getLocalPart() == (EXTORTER)) {
						extorter = new ExtorterConf();

						// Set extorterType attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_TYPE)) {
						event = eventReader.nextEvent();
						extorter.setType(event.asCharacters().getData());
						continue;

						// Set extorterEnlargementProbability attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_ENLARGEMENT_PROBABILITY)) {
						event = eventReader.nextEvent();
						extorter.setEnlargementProbability(event.asCharacters()
								.getData());
						continue;

						// Set extorterNumber attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_NUMBER)) {
						event = eventReader.nextEvent();
						extorter.setNumberExtorters(new Integer(event
								.asCharacters().getData()));
						continue;

						// Set initWealth attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_INITIAL_WEALTH)) {
						event = eventReader.nextEvent();
						extorter.setInitialWealth(new Double(event
								.asCharacters().getData()));
						continue;

						// Set tolerance attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_TOLERANCE)) {
						event = eventReader.nextEvent();
						extorter.setTolerance(new Double(event.asCharacters()
								.getData()));
						continue;

						// Set retaliation attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_RETALIATION)) {
						event = eventReader.nextEvent();
						extorter.setRetaliation(new Double(event.asCharacters()
								.getData()));
						continue;

						// Set counterattack attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_COUNTERATTACK)) {
						event = eventReader.nextEvent();
						extorter.setCounterattack(new Double(event
								.asCharacters().getData()));
						continue;

						// Set costFight attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_COST_FIGHT)) {
						event = eventReader.nextEvent();
						extorter.setCostFight(new Double(event.asCharacters()
								.getData()));
						continue;

						// Set extortionType attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_EXTORTION_TYPE)) {
						event = eventReader.nextEvent();

						String type = event.asCharacters().getData();

						if (ExtorterConf.ExtortionType.PROPORTIONAL.name()
								.toLowerCase().equals(type)) {
							extorter.setExtortionType(ExtorterConf.ExtortionType.PROPORTIONAL);
						}
						continue;

						// Set extortion attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_EXTORTION)) {
						event = eventReader.nextEvent();
						extorter.setExtortion(new Double(event.asCharacters()
								.getData()));
						continue;

						// Set punishmentType attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_PUNISHMENT_TYPE)) {
						event = eventReader.nextEvent();

						String type = event.asCharacters().getData();

						if (ExtorterConf.PunishmentType.FIXED.name()
								.toLowerCase().equals(type)) {
							extorter.setPunishmentType(ExtorterConf.PunishmentType.FIXED);
						} else if (ExtorterConf.PunishmentType.PROPORTIONAL
								.name().toLowerCase().equals(type)) {
							extorter.setPunishmentType(ExtorterConf.PunishmentType.PROPORTIONAL);
						} else if (ExtorterConf.PunishmentType.ESCALATION
								.name().toLowerCase().equals(type)) {
							extorter.setPunishmentType(ExtorterConf.PunishmentType.ESCALATION);
						}
						continue;

						// Set punishmentCost attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_PUNISHMENT_COST)) {
						event = eventReader.nextEvent();
						extorter.setCostPunish(new Double(event.asCharacters()
								.getData()));
						continue;

						// Set punishment attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_PUNISHMENT)) {
						event = eventReader.nextEvent();
						extorter.setPunishment(new Double(event.asCharacters()
								.getData()));
						continue;

						// Set escalationMinimum attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_PUNISHMENT_MINIMUM_ESCALATION)) {
						event = eventReader.nextEvent();
						extorter.setMinEscalation(new Double(event
								.asCharacters().getData()));
						continue;

						// Set escalationMaximum attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_PUNISHMENT_MAXIMUM_ESCALATION)) {
						event = eventReader.nextEvent();
						extorter.setMaxEscalation(new Double(event
								.asCharacters().getData()));
						continue;

						// Set escalationFormula attribute
					} else if (event.asStartElement().getName().getLocalPart()
							.equals(EXTORTER_PUNISHMENT_FORMULA_ESCALATION)) {
						event = eventReader.nextEvent();
						extorter.setFormulaEscalation(event.asCharacters()
								.getData());
						continue;
					}
				}

				// If we reach the end of an item element we add it to the list
				if (event.isEndElement()) {
					endElement = event.asEndElement();
					if (endElement.getName().getLocalPart() == (EXTORTER)) {
						scenario.addExtorter(extorter);
					} else if (endElement.getName().getLocalPart() == (TARGET)) {
						scenario.addTarget(target);
					}
				}
			}

			// Close the file
			in.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		return scenario;
	}

	public String getStopAt() {
		return this.stopAt;
	}

	public void setStopAt(String stopAt) {
		this.stopAt = stopAt;
	}

	public double getPrisonProbability() {
		return this.prisonProbability;
	}

	private void setPrisonProbability(double prisonProbability) {
		this.prisonProbability = prisonProbability;
	}

	public int getPrisonRounds() {
		return this.prisonRounds;
	}

	private void setPrisonRounds(int prisonRounds) {
		this.prisonRounds = prisonRounds;
	}

	public List<TargetConf> getTargets() {
		return this.targets;
	}

	private void addTarget(TargetConf target) {
		this.targets.add(target);
	}

	public String getTargetPerExtorter() {
		return this.targetPerExtorter;
	}

	private void setTargetPerExtorter(String targetPerExtorter) {
		this.targetPerExtorter = targetPerExtorter;
	}

	public List<ExtorterConf> getExtorters() {
		return this.extorters;
	}

	private void addExtorter(ExtorterConf extorter) {
		this.extorters.add(extorter);
	}

	@Override
	public String toString() {
		String str = new String();

		str += "PRISON\n";
		str += "Prison Probability...........: [" + this.prisonProbability
				+ "]\n";
		str += "Prison Rounds................: [" + this.prisonRounds + "]\n";

		for (TargetConf target : this.targets) {
			str += target.toString();
		}

		str += "EXTORTERS\n";
		str += "Targets per Extorter.........: [" + this.targetPerExtorter
				+ "]\n";

		for (ExtorterConf extorter : this.extorters) {
			str += extorter.toString();
		}

		return str;
	}
}
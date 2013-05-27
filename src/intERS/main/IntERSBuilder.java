package intERS.main;

import intERS.agents.extorter.ExtorterAbstract;
import intERS.agents.extorter.ExtorterAgent;
import intERS.agents.extorter.punishment.ExtorterAgentProportionalEscalation;
import intERS.agents.extorter.punishment.ExtorterAgentProportionalProportional;
import intERS.agents.observer.Observer;
import intERS.agents.target.TargetAbstract;
import intERS.agents.target.TargetAgent;
import intERS.conf.scenario.ExtorterConf;
import intERS.conf.scenario.ExtorterConf.PunishmentType;
import intERS.conf.scenario.ScenarioConf;
import intERS.conf.scenario.TargetConf;
import intERS.conf.simulation.OutputConf;
import intERS.output.OutputRecorder;
import intERS.utils.XML;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import repast.simphony.context.Context;
import repast.simphony.context.ContextEvent;
import repast.simphony.context.ContextListener;
import repast.simphony.context.DefaultContext;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;

public class IntERSBuilder extends DefaultContext<Object> implements
		ContextBuilder<Object> {

	private static final String PROJECT_NAME = "intERS";
	private static final String PARAM_RANDOM_SEED = "randomSeed";
	private static final String PARAM_SCENARIO_FILENAME = "scenarioFilename";
	private static final String PARAM_SCHEMA_FILENAME = "schemaFilename";
	// Output
	private final static String PARAM_OUTPUT_DIRECTORY = "outputDirectory";
	private final static String PARAM_OUTPUT_FILENAME_EXTORTER = "outputFileExtorter";
	private final static String PARAM_OUTPUT_FILENAME_OBSERVER = "outputFileObserver";
	private final static String PARAM_OUTPUT_FILENAME_TARGET = "outputFileTarget";
	private final static String PARAM_OUTPUT_FILE_APPEND = "outputFileAppend";
	private final static String PARAM_OUTPUT_FIELD_SEPARATOR = "outputFieldSeparator";
	private final static String PARAM_OUTPUT_WRITE_EVERY = "outputWriteEvery";

	private Map<Integer, ExtorterAbstract> extorters;
	private Map<Integer, TargetAbstract> targets;

	@Override
	public Context<Object> build(Context<Object> context) {
		context.setId(PROJECT_NAME);

		this.extorters = new Hashtable<Integer, ExtorterAbstract>();
		this.targets = new Hashtable<Integer, TargetAbstract>();

		// Get the parameter values
		Parameters params = RunEnvironment.getInstance().getParameters();
		Integer randomSeed = (Integer) params.getValue(PARAM_RANDOM_SEED);
		RandomHelper.setSeed(randomSeed);

		String scenarioFilename = (String) params
				.getValue(PARAM_SCENARIO_FILENAME);
		String schemaFilename = (String) params.getValue(PARAM_SCHEMA_FILENAME);

		OutputConf outputConf = new OutputConf();
		outputConf.setDirectory((String) params
				.getValue(PARAM_OUTPUT_DIRECTORY));
		outputConf.setFileExtorter((String) params
				.getValue(PARAM_OUTPUT_FILENAME_EXTORTER));
		outputConf.setFileObserver((String) params
				.getValue(PARAM_OUTPUT_FILENAME_OBSERVER));
		outputConf.setFileTarget((String) params
				.getValue(PARAM_OUTPUT_FILENAME_TARGET));
		outputConf.setFileAppend((Boolean) params
				.getValue(PARAM_OUTPUT_FILE_APPEND));
		outputConf.setFieldSeparator((String) params
				.getValue(PARAM_OUTPUT_FIELD_SEPARATOR));
		outputConf.setWriteEvery((Integer) params
				.getValue(PARAM_OUTPUT_WRITE_EVERY));

		// If the file does not exist, exit with an error
		if (!(new File(scenarioFilename)).exists()) {
			System.out.println("Scenario file does not exist");
			System.exit(1);
		}

		// Validate the Scenario XML file
		if (!XML.isValid(scenarioFilename, schemaFilename)) {
			System.out.println("Invalid XML");
			System.exit(1);
		}
		ScenarioConf scenario = ScenarioConf.scenarioParser(scenarioFilename);

		// Associate the ContextListener to the Context
		context.addContextListener(new ContextUpdate(this.extorters,
				this.targets));

		// Create Target agents
		List<TargetConf> targetsScenario = scenario.getTargets();

		int id = 1;
		for (TargetConf targetConf : targetsScenario) {
			for (int i = 0; i < targetConf.getNumberTargets(); i++) {
				context.add(new TargetAgent(this.extorters, this.targets, id,
						targetConf));

				id++;
			}
		}

		// Calculate the number of Targets per Extorter
		List<ExtorterConf> extortersScenario = scenario.getExtorters();

		int numExtorters = 0;
		for (ExtorterConf extorter : extortersScenario) {
			numExtorters += extorter.getNumberExtorters();
		}

		int targetsPerExtorter = 0;
		int numTargets = this.targets.size();

		Evaluator eval = new Evaluator();
		try {
			eval.putVariable("TARGETS", new Integer(numTargets).toString());
			eval.putVariable("EXTORTERS", new Integer(numExtorters).toString());

			targetsPerExtorter = new Double(eval.evaluate(scenario
					.getTargetPerExtorter())).intValue();
			if (targetsPerExtorter > numTargets) {
				targetsPerExtorter = numTargets;
			}
		} catch (EvaluationException e) {
			e.printStackTrace();
		}

		// Create Extorter agents
		Set<Integer> initialTargets;
		ExtorterAbstract extorter;
		id = 1;
		for (ExtorterConf extorterConf : extortersScenario) {

			numExtorters = extorterConf.getNumberExtorters();
			for (int i = 0; i < numExtorters; i++) {

				// Create list of initial Targets per Extorter
				initialTargets = new HashSet<Integer>();
				int targetId;
				while (initialTargets.size() < targetsPerExtorter) {
					targetId = RandomHelper.nextIntFromTo(0, (numTargets - 1));
					initialTargets.add(targetId);
				}

				if (extorterConf.getPunishmentType().equals(
						PunishmentType.PROPORTIONAL)) {

					extorter = new ExtorterAgentProportionalProportional(
							this.extorters, this.targets, initialTargets, id,
							extorterConf);
					this.extorters.put(id, extorter);
					context.add(extorter);

				} else if (extorterConf.getPunishmentType().equals(
						PunishmentType.ESCALATION)) {

					extorter = new ExtorterAgentProportionalEscalation(
							this.extorters, this.targets, initialTargets, id,
							extorterConf);
					this.extorters.put(id, extorter);
					context.add(extorter);
				}

				id++;
			}
		}

		// Create a Police
		context.add(new Observer(scenario, context, this.extorters,
				this.targets, 1, scenario.getPrisonProbability(), scenario
						.getPrisonRounds()));

		// Schedule the output writing
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters scheduleRep = ScheduleParameters.createRepeating(
				outputConf.getWriteEvery(), outputConf.getWriteEvery());
		ScheduleParameters scheduleEnd = ScheduleParameters.createAtEnd(99);

		OutputRecorder output = OutputRecorder.getInstance();
		output.setOutput(outputConf);
		schedule.schedule(scheduleRep, output, "write");
		schedule.schedule(scheduleEnd, output, "close");

		// Schedule the simulation stop
		// RunEnvironment.getInstance().endAt(scenario.getNumberCycles());

		return context;
	}
}

/**
 * Update dynamically the Extorters and Targets available in the simulation
 */
class ContextUpdate implements ContextListener<Object> {

	Map<Integer, ExtorterAbstract> extorters;
	Map<Integer, TargetAbstract> targets;

	public ContextUpdate(Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets) {
		this.extorters = extorters;
		this.targets = targets;
	}

	@Override
	public void eventOccured(ContextEvent<Object> event) {
		if (event.getType().equals(ContextEvent.REMOVED)) {
			Object obj = event.getTarget();
			if (obj instanceof ExtorterAbstract) {
				this.extorters.remove(((ExtorterAgent) obj).getId());
			} else if (obj instanceof TargetAbstract) {
				this.targets.remove(((TargetAgent) obj).getId());
			}
		} else if (event.getType().equals(ContextEvent.ADDED)) {
			Object obj = event.getTarget();
			if (obj instanceof ExtorterAbstract) {
				ExtorterAgent extorter = (ExtorterAgent) obj;
				this.extorters.put(extorter.getId(), extorter);
			} else if (obj instanceof TargetAbstract) {
				TargetAgent target = (TargetAgent) obj;
				this.targets.put(target.getId(), target);
			}
		}
	}
}
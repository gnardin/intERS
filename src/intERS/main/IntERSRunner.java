package intERS.main;

import java.io.File;

import repast.simphony.batch.BatchScenarioLoader;
import repast.simphony.engine.controller.Controller;
import repast.simphony.engine.controller.DefaultController;
import repast.simphony.engine.environment.AbstractRunner;
import repast.simphony.engine.environment.ControllerRegistry;
import repast.simphony.engine.environment.DefaultRunEnvironmentBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.environment.RunEnvironmentBuilder;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.Schedule;
import repast.simphony.parameter.Parameters;
import repast.simphony.parameter.ParametersCreator;
import simphony.util.messages.MessageCenter;

public class IntERSRunner extends AbstractRunner {

	private static MessageCenter msgCenter = MessageCenter
			.getMessageCenter(IntERSRunner.class);

	private RunEnvironmentBuilder runEnvironmentBuilder;
	protected Controller controller;
	protected Parameters params;
	private ISchedule schedule;

	public IntERSRunner() {
		this.runEnvironmentBuilder = new DefaultRunEnvironmentBuilder(this,
				true);
		this.controller = new DefaultController(this.runEnvironmentBuilder);
		this.controller.setScheduleRunner(this);
	}

	public void load(int simulationRun, int randomSeed, String rsDirectory,
			String xmlFilename, String xsdFilename, String outputDirectory,
			String outputFileExtorter, String outputFileObserver,
			String outputFileTarget, boolean outputFileAppend,
			String outputFieldSeparator, int outputWriteEvery) throws Exception {
		File codePath = new File(rsDirectory);
		if (codePath.exists()) {
			BatchScenarioLoader loader = new BatchScenarioLoader(codePath);
			ControllerRegistry registry = loader
					.load(this.runEnvironmentBuilder);
			this.controller.setControllerRegistry(registry);
		} else {
			msgCenter.error("RS directory not found",
					new IllegalArgumentException("Invalid RS directory "
							+ codePath.getAbsolutePath()));
			return;
		}

		this.controller.batchInitialize();

		ParametersCreator paramsCreator = new ParametersCreator();
		paramsCreator.addParameter("simulationRun", Integer.class,
				simulationRun, false);
		paramsCreator.addParameter("randomSeed", Integer.class, randomSeed,
				false);
		paramsCreator.addParameter("scenarioFilename", String.class,
				xmlFilename, false);
		paramsCreator.addParameter("schemaFilename", String.class, xsdFilename,
				false);
		paramsCreator.addParameter("outputDirectory", String.class,
				outputDirectory, false);
		paramsCreator.addParameter("outputFileExtorter", String.class,
				outputFileExtorter, false);
		paramsCreator.addParameter("outputFileObserver", String.class,
				outputFileObserver, false);
		paramsCreator.addParameter("outputFileTarget", String.class,
				outputFileTarget, false);
		paramsCreator.addParameter("outputFileAppend", Boolean.class,
				outputFileAppend, false);
		paramsCreator.addParameter("outputFieldSeparator", String.class,
				outputFieldSeparator, false);
		paramsCreator.addParameter("outputWriteEvery", Integer.class,
				outputWriteEvery, false);
		this.params = paramsCreator.createParameters();

		this.controller.runParameterSetters(this.params);
	}

	public void runInitialize() {
		this.controller.runInitialize(this.params);
		this.schedule = RunState.getInstance().getScheduleRegistry()
				.getModelSchedule();
	}

	public void cleanUpRun() {
		this.controller.runCleanup();
	}

	public void cleanUpBatch() {
		this.controller.batchCleanup();
	}

	// returns the tick count of the next scheduled item
	public double getNextScheduledTime() {
		return ((Schedule) RunEnvironment.getInstance().getCurrentSchedule())
				.peekNextAction().getNextTime();
	}

	// returns the number of model actions on the schedule
	public int getModelActionCount() {
		return this.schedule.getModelActionCount();
	}

	// returns the number of non-model actions on the schedule
	public int getActionCount() {
		return this.schedule.getActionCount();
	}

	// Step the schedule
	public void step() {
		this.schedule.execute();
	}

	// stop the schedule
	public void stop() {
		if (this.schedule != null) {
			this.schedule.executeEndActions();
		}
	}

	public void setFinishing(boolean fin) {
		this.schedule.setFinishing(fin);
	}

	public void execute(RunState toExecuteOn) {
	}
}
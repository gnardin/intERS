package intERS.agents;

import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.ScenarioConf;
import intERS.output.OutputObserver;
import intERS.output.OutputRecorder;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;

public abstract class StateAbstract {

	protected int id;

	protected Context<Object> context;

	protected Map<Integer, ExtorterAbstract> extorters;

	protected List<String> extorterTypes;

	protected Map<Integer, ExtorterAbstract> imprisoned;

	protected int imprisonedTotal;

	protected OutputRecorder outputRecorder;

	protected double prisonProbability;

	protected int prisonRounds;

	protected Map<Integer, Integer> remainedRounds;

	protected ScenarioConf scenarioConf;

	protected Map<Integer, TargetAbstract> targets;

	protected List<String> targetTypes;

	public StateAbstract(ScenarioConf scenarioConf, Context<Object> context,
			Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets, Integer id,
			Double prisonProbability, Integer prisonRounds) {
		this.id = id;
		this.scenarioConf = scenarioConf;
		this.context = context;
		this.extorters = extorters;

		this.extorterTypes = new ArrayList<String>();
		ExtorterAbstract extorter;
		for (Integer extorterId : this.extorters.keySet()) {
			extorter = this.extorters.get(extorterId);

			if (!this.extorterTypes.contains(extorter.getType())) {
				this.extorterTypes.add(extorter.getType());
			}
		}

		this.imprisoned = new Hashtable<Integer, ExtorterAbstract>();
		this.imprisonedTotal = 0;
		this.outputRecorder = OutputRecorder.getInstance();
		this.prisonProbability = prisonProbability / 100;
		this.prisonRounds = prisonRounds;
		this.remainedRounds = new Hashtable<Integer, Integer>();

		this.targets = targets;
		this.targetTypes = new ArrayList<String>();
		TargetAbstract target;
		for (Integer targetId : this.targets.keySet()) {
			target = this.targets.get(targetId);

			if (!this.targetTypes.contains(target.getType())) {
				this.targetTypes.add(target.getType());
			}
		}

		// Output
		this.outputRecorder.addRecord(this.getOutput(0));
	}

	@ScheduledMethod(start = 1, interval = 1, priority = 0)
	public void beginCycle() {
		double cycle = RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount();

		boolean stop = false;
		Evaluator eval = new Evaluator();

		try {
			eval.putVariable("CYCLE", new Double(cycle - 1).toString());
			eval.putVariable("EXTORTERS", new Integer(this.extorters.size()
					+ this.imprisonedTotal).toString());

			List<String> extorterTypesList = new ArrayList<String>();
			ExtorterAbstract extorter;
			for (Integer extorterId : this.extorters.keySet()) {
				extorter = this.extorters.get(extorterId);

				if (!extorterTypesList.contains(extorter.getType())) {
					extorterTypesList.add(extorter.getType());
				}
			}
			for (Integer extorterId : this.imprisoned.keySet()) {
				extorter = this.imprisoned.get(extorterId);

				if (!extorterTypesList.contains(extorter.getType())) {
					extorterTypesList.add(extorter.getType());
				}
			}
			eval.putVariable("EXTORTER_TYPES",
					new Integer(extorterTypesList.size()).toString());

			eval.putVariable("TARGETS",
					new Integer(this.targets.size()).toString());

			List<String> targetTypesList = new ArrayList<String>();
			TargetAbstract target;
			for (Integer targetId : this.targets.keySet()) {
				target = this.targets.get(targetId);

				if (!targetTypesList.contains(target.getType())) {
					targetTypesList.add(target.getType());
				}
			}
			eval.putVariable("TARGET_TYPES",
					new Integer(targetTypesList.size()).toString());

			stop = new Double(eval.evaluate(this.scenarioConf.getStopAt())) == 1.0;
			if (stop) {
				RunEnvironment.getInstance().getCurrentSchedule()
						.setFinishing(true);
			}
		} catch (EvaluationException e) {
			e.printStackTrace();
		}
	}

	@ScheduledMethod(start = 1.98, interval = 1)
	public abstract void decideToArrestRelease();

	@ScheduledMethod(start = 1.99, interval = 1)
	public void endCycle() {
		int cycle = (int) RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount() + 1;

		this.outputRecorder.addRecord(this.getOutput(cycle));
	}

	private OutputObserver getOutput(int cycle) {
		OutputObserver output = new OutputObserver(cycle, this.id, "0");

		for (String type : this.extorterTypes) {
			output.setNumExtortersFree(type, 0);
			output.setNumExtortersImprisoned(type, 0);
		}

		for (String type : this.targetTypes) {
			output.setNumTargetsAlive(type, 0);
			output.setNumTargetsExtorted(type, 0);
			output.setNumTargetExtorions(type, 0);
		}

		int num;
		int numExtortions;
		String type;
		TargetAbstract target;
		for (Integer targetId : this.targets.keySet()) {
			target = this.targets.get(targetId);

			type = target.getType();
			num = output.getNumTypeTargetsAlive(type);
			output.setNumTargetsAlive(type, num + 1);

			numExtortions = target.getNumExtortions();
			if (numExtortions > 0) {
				num = output.getNumTypeTargetsExtorted(type);
				output.setNumTargetsExtorted(type, num + 1);

				num = output.getNumTargetExtortions(type);
				output.setNumTargetExtorions(type, num + numExtortions);
			}
		}

		ExtorterAbstract extorter;
		for (Integer extorterId : this.extorters.keySet()) {
			extorter = this.extorters.get(extorterId);

			type = extorter.getType();
			num = output.getNumTypeExtortersFree(type);
			output.setNumExtortersFree(type, num + 1);
		}

		for (Integer extorterId : this.imprisoned.keySet()) {
			extorter = this.imprisoned.get(extorterId);

			type = extorter.getType();
			num = output.getNumTypeExtortersImprisoned(type);
			output.setNumExtortersImprisoned(type, num + 1);
		}

		return output;
	}
}

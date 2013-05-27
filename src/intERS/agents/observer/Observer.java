package intERS.agents.observer;

import intERS.agents.extorter.ExtorterAbstract;
import intERS.agents.target.TargetAbstract;
import intERS.conf.scenario.ScenarioConf;
import intERS.output.ObserverOutput;
import intERS.output.OutputRecorder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class Observer {

	private int id;

	private Context<Object> context;

	private Map<Integer, ExtorterAbstract> extorters;

	private List<String> extorterTypes;

	private Map<Integer, ExtorterAbstract> imprisoned;

	private int imprisonedTotal;

	private OutputRecorder outputRecorder;

	private double prisonProbability;

	private int prisonRounds;

	private Map<Integer, Integer> remainedRounds;

	private ScenarioConf scenarioConf;

	private Map<Integer, TargetAbstract> targets;

	private List<String> targetTypes;

	public Observer(ScenarioConf scenarioConf, Context<Object> context,
			Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets, int id,
			double prisonProbability, int prisonRounds) {
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
		this.outputRecorder.addRecord(1, this.getOutput());
	}

	@ScheduledMethod(start = 1, interval = 1, priority = 0)
	public void beginCycle() {
		double cycle = RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount();

		boolean stop = false;
		Evaluator eval = new Evaluator();

		try {
			eval.putVariable("CYCLE", new Double(cycle).toString());
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

	@ScheduledMethod(start = 1.95, interval = 1)
	public void decideToArrestRelease() {

		// Determine the Extorters to arrest
		ExtorterAbstract extorter;
		Collection<ExtorterAbstract> removeFromContext = new ArrayList<ExtorterAbstract>();
		for (Integer extorterId : this.extorters.keySet()) {
			if (RandomHelper.nextDouble() < this.prisonProbability) {
				extorter = this.extorters.get(extorterId);

				this.imprisoned.put(extorterId, extorter);
				this.remainedRounds.put(extorterId, this.prisonRounds);
				removeFromContext.add(extorter);

				this.imprisonedTotal++;
			}
		}
		this.context.removeAll(removeFromContext);

		// Determine the Extorters to release from prison
		int rounds;
		Collection<Integer> removeFromPrison = new ArrayList<Integer>();
		for (Integer extorterId : this.remainedRounds.keySet()) {
			rounds = this.remainedRounds.get(extorterId) - 1;
			if (rounds < 0) {
				// Release from prison
				removeFromPrison.add(extorterId);
				// Put it back to the simulation
				extorter = this.imprisoned.get(extorterId);
				this.context.add(extorter);

				this.imprisonedTotal--;
			} else {
				this.remainedRounds.put(extorterId, rounds);
			}
		}

		for (Integer extorterId : removeFromPrison) {
			this.imprisoned.remove(extorterId);
			this.remainedRounds.remove(extorterId);
		}
	}

	@ScheduledMethod(start = 1.95, interval = 1)
	public void endCycle() {
		int cycle = (int) RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount() + 1;

		this.outputRecorder.addRecord(cycle, this.getOutput());
	}

	private ObserverOutput getOutput() {
		ObserverOutput output = new ObserverOutput(this.id, "0");

		for (String type : this.extorterTypes) {
			output.setNumExtortersFree(type, 0);
			output.setNumExtortersImprisoned(type, 0);
		}

		for (String type : this.targetTypes) {
			output.setNumTargets(type, 0);
		}

		int num;
		String type;
		TargetAbstract target;
		for (Integer targetId : this.targets.keySet()) {
			target = this.targets.get(targetId);

			type = target.getType();
			num = output.getNumTypeTargets(type);
			output.setNumTargets(type, num + 1);
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
package intERS.impl.withProtection;

import intERS.agents.ExtorterAbstract;
import intERS.agents.StateAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.ScenarioConf;
import intERS.output.OutputObserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;

public class StateAgent extends StateAbstract {

	public StateAgent(ScenarioConf scenarioConf, Context<Object> context,
			Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets, Integer id,
			Double prisonProbability, Integer prisonRounds) {
		super(scenarioConf, context, extorters, targets, id, prisonProbability,
				prisonRounds);
	}

	@Override
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
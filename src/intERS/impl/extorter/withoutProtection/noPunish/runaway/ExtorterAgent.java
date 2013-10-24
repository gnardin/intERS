package intERS.impl.extorter.withoutProtection.noPunish.runaway;

import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.ExtorterConf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import repast.simphony.random.RandomHelper;

public class ExtorterAgent extends ExtorterAbstract {

	public ExtorterAgent(Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets, Set<Integer> initialTargets,
			int id, ExtorterConf extorterConf) {
		super(extorters, targets, initialTargets, id, extorterConf);
	}

	@Override
	public void updateTargets() {
		// Remove non existent Targets from extorted list
		List<Integer> noExist = new ArrayList<Integer>();
		for (Integer targetId : this.extorted.keySet()) {
			if (!this.targets.containsKey(targetId)) {
				noExist.add(targetId);
			}
		}
		for (Integer targetd : noExist) {
			this.extorted.remove(targetd);
		}

		// Extorter has a probability to add new Target
		if (this.enlargementProbability > 0) {
			if ((RandomHelper.nextDoubleFromTo(0, 1) < this.enlargementProbability)
					&& (this.extorted.size() < this.targets.size())) {

				Object[] targetIds = this.targets.keySet().toArray();
				int targetId;
				boolean notOK = true;
				while (notOK) {
					targetId = (Integer) targetIds[RandomHelper.nextIntFromTo(
							0, targetIds.length - 1)];
					if (!this.extorted.containsKey(targetId)) {
						this.extorted.put(targetId, 0.0);
						notOK = false;
					}
				}
			}
		}
	}

	@Override
	public void updateExtortion() {
	}

	@Override
	public void decideToProtect() {
	}

	@Override
	public void decideToCounterattackProtection() {
	}

	@Override
	public void decideToRetaliate() {
		Map<Integer, Double> targets;
		ExtorterAbstract extorter;
		double maxNumExtorted;
		double maxWealth;
		double opNumExtorted;
		double opWealth;
		double opStrength;
		double myStrength;
		for (Integer extorterId : this.extortersPaidInsteadOfMe.keySet()) {
			targets = this.extortersPaidInsteadOfMe.get(extorterId);

			// Attack Retaliation anyway
			if (RandomHelper.nextDouble() <= this.attackRetaliationPropensity) {
				this.attackRetaliation.put(extorterId, new ArrayList<Integer>(
						targets.keySet()));

				// Rational decision to attack
			} else {
				extorter = this.extorters.get(extorterId);

				opNumExtorted = extorter.getNumberExtorted();
				opWealth = extorter.getWealth();

				maxNumExtorted = Math.max(this.extorted.size(), opNumExtorted);
				maxWealth = Math.max(this.wealth, opWealth);

				opStrength = ((double) opNumExtorted / maxNumExtorted)
						+ ((double) opWealth / maxWealth);

				myStrength = ((double) this.extorted.size() / maxNumExtorted)
						+ ((double) this.wealth / maxWealth);

				// If I am stronger attack retaliation, otherwise I do not
				// attack but I continue to extort the Target
				if (opStrength < myStrength) {
					this.attackRetaliation.put(extorterId,
							new ArrayList<Integer>(targets.keySet()));

				} else {
					this.nonAttackRetaliation.put(extorterId,
							new ArrayList<Integer>(targets.keySet()));

					// TO BE REMOVED LATER
					for (Integer targetId : targets.keySet()) {
						if (this.extorted.containsKey(targetId)) {
							this.extorted.remove(targetId);

							this.output.setNumRunawayRetaliation(this.output
									.getNumRunawayRetaliation() + 1);
						}
					}
				}
			}
		}
	}

	@Override
	public void decideToCounterattackRetaliation() {
		List<Integer> targets;
		ExtorterAbstract extorter;
		double maxNumExtorted;
		double maxWealth;
		double opNumExtorted;
		double opWealth;
		double opStrength;
		double myStrength;
		for (Integer extorterId : this.retaliation.keySet()) {
			// Counterattack Retaliation anyway
			if (RandomHelper.nextDouble() <= this.counterattackRetaliationPropensity) {
				this.counterattackRetaliation.add(extorterId);

				// Rational decision to counterattack
			} else {
				extorter = this.extorters.get(extorterId);

				opNumExtorted = extorter.getNumberExtorted();
				opWealth = extorter.getWealth();

				maxNumExtorted = Math.max(this.extorted.size(), opNumExtorted);
				maxWealth = Math.max(this.wealth, opWealth);

				opStrength = ((double) opNumExtorted / maxNumExtorted)
						+ ((double) opWealth / maxWealth);

				myStrength = ((double) this.extorted.size() / maxNumExtorted)
						+ ((double) this.wealth / maxWealth);

				// If I am stronger than counterattack retaliation,
				// otherwise do not
				if (opStrength < myStrength) {
					this.counterattackRetaliation.add(extorterId);

					// Do not extort the Targets that was also extorted by
					// the winner Extorter
				} else {
					targets = this.retaliation.get(extorterId);

					for (Integer targetId : targets) {
						if (this.extorted.containsKey(targetId)) {
							this.extorted.remove(targetId);

							this.output.setNumRunawayRetaliation(this.output
									.getNumRunawayRetaliation() + 1);
						}
					}
				}
			}
		}
	}

	@Override
	public void decideToPunish() {

		List<Integer> targets;
		for (Integer extorterId : this.attackRetaliation.keySet()) {

			if (!this.counterattackedRetaliation.contains(extorterId)) {
				targets = this.attackRetaliation.get(extorterId);
				for (Integer targetId : targets) {
					if (!this.punishments.containsKey(targetId)) {
						this.punishments.put(targetId, 0.0);
					}
				}
			}

			// TO BE REMOVED LATER
			if (this.counterattackedRetaliation.contains(extorterId)) {
				targets = this.attackRetaliation.get(extorterId);

				for (Integer targetId : targets) {
					if (this.extorted.containsKey(targetId)) {
						this.extorted.remove(targetId);

						this.output.setNumRunawayRetaliation(this.output
								.getNumRunawayRetaliation() + 1);
					}
				}
			}
		}
	}
}
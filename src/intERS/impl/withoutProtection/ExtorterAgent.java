package intERS.impl.withoutProtection;

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
		// Extorter was not paid by some Target
		if (!this.extortersPaid.containsKey(this.id)) {

			Map<Integer, Double> targets;
			ExtorterAbstract extorter;
			int maxNumExtorted;
			double maxWealth;
			int opNumExtorted;
			double opWealth;
			double opStrength;
			double myStrength;
			for (Integer extorterId : this.extortersPaid.keySet()) {
				// Attack Retaliation anyway
				if (RandomHelper.nextDouble() <= this.attackRetaliationPropensity) {
					this.attackRetaliation.add(extorterId);

					// Rational decision to attack
				} else {
					extorter = this.extorters.get(extorterId);

					opNumExtorted = extorter.getNumberExtorted();
					opWealth = extorter.getWealth();

					maxNumExtorted = Math.max(this.extorted.size(),
							opNumExtorted);
					maxWealth = Math.max(this.wealth, opWealth);

					opStrength = ((double) opNumExtorted / maxNumExtorted)
							+ (opWealth / maxWealth);

					myStrength = ((double) this.extorted.size() / maxNumExtorted)
							+ (this.wealth / maxWealth);

					// If I am stronger attack retaliation, otherwise do not
					if (opStrength < myStrength) {
						this.attackRetaliation.add(extorterId);

						// Do not extort the Targets that was also extorted by
						// the winner Extorter
					} else {
						targets = this.extortersPaid.get(extorterId);

						for (Integer targetId : targets.keySet()) {
							if (this.extorted.containsKey(targetId)) {
								this.extorted.remove(targetId);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void decideToCounterattackRetaliation() {
		if (!this.retaliation.isEmpty()) {

			Map<Integer, Double> targets;
			ExtorterAbstract extorter;
			int maxNumExtorted;
			double maxWealth;
			int opNumExtorted;
			double opWealth;
			double opStrength;
			double myStrength;
			for (Integer extorterId : this.retaliation) {
				// Counterattack Retaliation anyway
				if (RandomHelper.nextDouble() <= this.counterattackRetaliationPropensity) {
					this.counterattackRetaliation.add(extorterId);

					// Rational decision to counterattack
				} else {
					extorter = this.extorters.get(extorterId);

					opNumExtorted = extorter.getNumberExtorted();
					opWealth = extorter.getWealth();

					maxNumExtorted = Math.max(this.extorted.size(),
							opNumExtorted);
					maxWealth = Math.max(this.wealth, opWealth);

					opStrength = ((double) opNumExtorted / maxNumExtorted)
							+ (opWealth / maxWealth);

					myStrength = ((double) this.extorted.size() / maxNumExtorted)
							+ (this.wealth / maxWealth);

					// If I am stronger than counterattack retaliation,
					// otherwise do not
					if (opStrength < myStrength) {
						this.counterattackRetaliation.add(extorterId);

						// Do not extort the Targets that was also extorted by
						// the winner Extorter
					} else {
						if (this.extortersPaid.containsKey(extorterId)) {
							targets = this.extortersPaid.get(extorterId);

							for (Integer targetId : targets.keySet()) {
								if (this.extorted.containsKey(targetId)) {
									this.extorted.remove(targetId);
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void decideToPunish() {

		Map<Integer, Double> targets;
		for (Integer extorterId : this.attackRetaliation) {

			// I attacked retaliation, but I was not counterattacked retaliation
			if (!this.counterattackedRetaliation.contains(extorterId)) {
				targets = this.extortersPaid.get(extorterId);
				for (Integer targetId : targets.keySet()) {
					if (RandomHelper.nextDouble() < this.tolerance) {
						this.punishments.put(targetId, 0.0);
					}
				}

				// In case of counterattack retaliation, I dismiss the Target
				// from future extortions
			} else {
				targets = this.extortersPaid.get(extorterId);
				for (Integer targetId : targets.keySet()) {
					if (this.extorted.containsKey(targetId)) {
						this.extorted.remove(targetId);
					}
				}
			}
		}
	}
}
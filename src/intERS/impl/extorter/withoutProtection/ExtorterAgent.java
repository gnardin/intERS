package intERS.impl.extorter.withoutProtection;

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
			Integer id, ExtorterConf extorterConf) {
		super(extorters, targets, initialTargets, id, extorterConf);
	}

	@Override
	public void decideProtection() {
		List<Integer> nonProtectionList;
		List<Integer> extortersList;
		for (Integer targetId : this.paymentProtection.keySet()) {
			extortersList = this.paymentProtection.get(targetId);

			for (Integer extorterId : extortersList) {
				if (this.nonAttackProtection.containsKey(extorterId)) {
					nonProtectionList = this.nonAttackProtection
							.get(extorterId);
				} else {
					nonProtectionList = new ArrayList<Integer>();
				}

				nonProtectionList.add(targetId);
				this.nonAttackProtection.put(extorterId, nonProtectionList);
			}
		}
	}

	@Override
	public void decideCounterattackProtection() {
	}

	@Override
	public void decideRetaliation() {
		Map<Integer, Double> targets;
		ExtorterAbstract extorter;
		double sumTarget;
		double sumWealth;
		double opTarget;
		double opWealth;
		double opStrength;
		double myStrength;
		for (Integer extorterId : this.extortersExtortingMyTargets.keySet()) {
			targets = this.extortersExtortingMyTargets.get(extorterId);

			// Attack Retaliation anyway
			if (RandomHelper.nextDouble() <= this.impulseRetaliationProb) {
				this.attackRetaliation.put(extorterId, new ArrayList<Integer>(
						targets.keySet()));

				// Rational decision to retaliate
			} else {
				extorter = this.extorters.get(extorterId);

				opTarget = extorter.getNumberTargets();
				sumTarget = this.extorted.size() + opTarget;

				opWealth = extorter.getWealth();
				sumWealth = this.wealth + opWealth;

				opStrength = ((double) opTarget / (double) sumTarget)
						+ ((double) opWealth / (double) sumWealth);
				myStrength = ((double) this.extorted.size() / (double) sumTarget)
						+ ((double) this.wealth / (double) sumWealth);

				// If stronger then attack retaliation, otherwise do not
				// attack but continue to extort the Target
				if (opStrength < myStrength) {
					this.attackRetaliation.put(extorterId,
							new ArrayList<Integer>(targets.keySet()));
				} else {
					this.nonAttackRetaliation.put(extorterId,
							new ArrayList<Integer>(targets.keySet()));
				}
			}
		}
	}

	@Override
	public void decideCounterattackRetaliation() {
		List<Integer> targets;
		ExtorterAbstract extorter;
		double sumTarget;
		double sumWealth;
		double opTarget;
		double opWealth;
		double opStrength;
		double myStrength;
		for (Integer extorterId : this.retaliation.keySet()) {
			// Counterattack Retaliation anyway
			if (RandomHelper.nextDouble() <= this.impulseFightRetaliationProb) {
				this.counterattackRetaliation.add(extorterId);

				// Rational decision to counterattack
			} else {
				extorter = this.extorters.get(extorterId);

				opTarget = extorter.getNumberTargets();
				sumTarget = this.extorted.size() + opTarget;

				opWealth = extorter.getWealth();
				sumWealth = this.wealth + opWealth;

				opStrength = ((double) opTarget / (double) sumTarget)
						+ ((double) opWealth / (double) sumWealth);
				myStrength = ((double) this.extorted.size() / (double) sumTarget)
						+ ((double) this.wealth / (double) sumWealth);
				// If stronger then counterattack retaliation,
				// otherwise do not
				if (opStrength < myStrength) {
					this.counterattackRetaliation.add(extorterId);
					// Do not extort the Targets that was also extorted by
					// the winner Extorter
				} else {
					targets = this.retaliation.get(extorterId);

					for (Integer targetId : targets) {
						if (this.extorted.containsKey(targetId)) {
							this.targetsToRemove.add(targetId);
						}
					}
				}
			}
		}
	}

	@Override
	public void decidePunishment() {
		List<Integer> targets;
		for (Integer extorterId : this.attackRetaliation.keySet()) {

			targets = this.attackRetaliation.get(extorterId);
			for (Integer targetId : targets) {
				if (!this.punishments.contains(targetId)) {
					this.punishments.add(targetId);
				}
			}
		}

		// Even though not retaliating the Extorter, there is a probability to
		// punish the Targets
		for (Integer extorterId : this.nonAttackRetaliation.keySet()) {
			targets = this.nonAttackRetaliation.get(extorterId);
			for (Integer targetId : targets) {
				if (!this.punishments.contains(targetId)) {
					this.punishments.add(targetId);
				}
			}
		}

		// Others
		for (Integer targetId : this.extorted.keySet()) {
			if (!this.paid.containsKey(targetId)) {
				if (!this.punishments.contains(targetId)) {
					this.punishments.add(targetId);
				}
			}
		}
	}
}
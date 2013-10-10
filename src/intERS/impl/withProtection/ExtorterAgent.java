package intERS.impl.withProtection;

import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.ExtorterConf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
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
		List<Integer> retaliateList;
		List<Integer> extortersList;
		ExtorterAbstract extorter;
		int maxNumExtorted;
		double maxWealth;
		int opNumExtorted;
		double opWealth;
		double opStrength;
		double myStrength;
		for (Integer targetId : this.protectionRequested.keySet()) {
			extortersList = this.protectionRequested.get(targetId);

			for (Integer extorterId : extortersList) {
				if (this.attackProtection.containsKey(extorterId)) {
					retaliateList = this.attackProtection.get(extorterId);
					if (retaliateList == null) {
						retaliateList = new ArrayList<Integer>();
					}
				} else {
					retaliateList = new ArrayList<Integer>();
				}

				if (!retaliateList.contains(targetId)) {
					// Attack anyway
					if (RandomHelper.nextDouble() <= this.protectionPropensity) {
						retaliateList.add(targetId);
						this.attackProtection.put(extorterId, retaliateList);
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

						// If I am stronger attack, otherwise do not
						if (opStrength < myStrength) {
							retaliateList.add(targetId);
							this.attackProtection.put(extorterId, retaliateList);
						}
					}
				}
			}
		}
	}

	@Override
	public void decideToCounterattackProtection() {
		ExtorterAbstract extorter;
		int maxNumExtorted;
		double maxWealth;
		int opNumExtorted;
		double opWealth;
		double opStrength;
		double myStrength;
		for (Integer extorterId : this.protection.keySet()) {
			// Counterattack anyway
			if (RandomHelper.nextDouble() <= this.counterattackPropensity) {
				this.counterattackProtection.add(extorterId);
				// Rational decision to counterattack
			} else {
				extorter = this.extorters.get(extorterId);

				opNumExtorted = extorter.getNumberExtorted();
				opWealth = extorter.getWealth();

				maxNumExtorted = Math.max(this.extorted.size(), opNumExtorted);
				maxWealth = Math.max(this.wealth, opWealth);

				opStrength = ((double) opNumExtorted / maxNumExtorted)
						+ (opWealth / maxWealth);

				myStrength = ((double) this.extorted.size() / maxNumExtorted)
						+ (this.wealth / maxWealth);

				// If I am stronger attack, otherwise do not
				if (opStrength < myStrength) {
					this.counterattackProtection.add(extorterId);
				}
			}
		}
	}

	@Override
	public void decideToRetaliate() {
	}

	@Override
	public void decideToCounterattackRetaliation() {
	}

	@Override
	public void decideToPunish() {

		Map<Integer, Set<Integer>> attackedBecauseTarget = new Hashtable<Integer, Set<Integer>>();
		Set<Integer> otherExtorters;
		for (Integer extorterId : this.attackProtection.keySet()) {
			for (Integer targetId : this.attackProtection.get(extorterId)) {

				if (attackedBecauseTarget.containsKey(targetId)) {
					otherExtorters = attackedBecauseTarget.get(targetId);
				} else {
					otherExtorters = new HashSet<Integer>();
				}
				otherExtorters.add(extorterId);

				attackedBecauseTarget.put(targetId, otherExtorters);
			}
		}

		Map<Integer, Set<Integer>> wasAttackedBecauseTarget = new Hashtable<Integer, Set<Integer>>();
		Set<Integer> extortersThatAttacked;
		for (Integer extorterId : this.protection.keySet()) {
			for (Integer targetId : this.protection.get(extorterId)) {

				if (wasAttackedBecauseTarget.containsKey(targetId)) {
					extortersThatAttacked = wasAttackedBecauseTarget
							.get(targetId);
				} else {
					extortersThatAttacked = new HashSet<Integer>();
				}
				extortersThatAttacked.add(extorterId);

				wasAttackedBecauseTarget.put(targetId, extortersThatAttacked);
			}
		}

		Set<Integer> attackedExtorters;
		boolean isCounterattackedBecauseTarget;
		for (Integer targetId : this.extorted.keySet()) {

			// Extorter was Not Asked For Protection and Was Not Attacked
			if ((!this.protectionRequested.containsKey(targetId))
					&& (!wasAttackedBecauseTarget.containsKey(targetId))) {
				if (!this.paid.containsKey(targetId)) {
					this.punishments.put(targetId, 0.0);

					// System.out.println("(1) " + this.id + " PUNISHING "
					// + targetId);
				}
				// Extorter was Asked for Protection
			} else if (this.protectionRequested.containsKey(targetId)) {

				// Attacked
				if (attackedBecauseTarget.containsKey(targetId)) {
					attackedExtorters = attackedBecauseTarget.get(targetId);

					// Counterattacked because of this Target Request
					// Protection?
					isCounterattackedBecauseTarget = false;
					for (Integer extorterId : attackedExtorters) {
						if (this.counterattackedProtection.contains(extorterId)) {
							isCounterattackedBecauseTarget = true;
						}
					}

					// Yes, Counterattacked because of this Target Request
					// Protection
					if (isCounterattackedBecauseTarget) {
						if (!this.paid.containsKey(targetId)) {
							// System.out.println("(2) " + this.id +
							// " PUNISHING "
							// + targetId);

							this.punishments.put(targetId, 0.0);
						}

						// Was Not Counterattacked because this Target
						// Request Protection
					} else {
						if (!this.paid.containsKey(targetId)) {
							// System.out.println("(3) " + this.id +
							// " PUNISHING "
							// + targetId);

							this.punishments.put(targetId, 0.0);
						}
					}

					// Did not Attack
				} else {
					if (!this.paid.containsKey(targetId)) {
						if (RandomHelper.nextDouble() < this.tolerance) {
							// System.out.println("(4) " + this.id +
							// " PUNISHING "
							// + targetId);

							this.punishments.put(targetId, 0.0);
						}
					}
				}
			}
		}

		boolean counterattackedAll;
		for (Integer targetId : wasAttackedBecauseTarget.keySet()) {
			attackedExtorters = wasAttackedBecauseTarget.get(targetId);

			counterattackedAll = true;
			for (Integer extorterId : attackedExtorters) {
				if (!this.counterattackProtection.contains(extorterId)) {
					counterattackedAll = false;
				}
			}

			// I counterattacked
			if (counterattackedAll) {
				if (!this.paid.containsKey(targetId)) {
					if (RandomHelper.nextDouble() < this.tolerance) {
						// System.out.println("(5) " + this.id + " PUNISHING "
						// + targetId);

						this.punishments.put(targetId, 0.0);
					}

					// I did not counterattack
				} else {
					if (this.extorted.containsKey(targetId)) {
						this.extorted.remove(targetId);
					}
				}
			}
		}
	}
}
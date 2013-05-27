package intERS.agents.extorter;

import intERS.agents.target.TargetAbstract;
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
	public void decideToRetaliate() {
		List<Integer> retaliateList;
		List<Integer> extortersList;
		ExtorterAbstract extorter;
		int maxNumExtorted;
		double maxWealth;
		int opNumExtorted;
		double opWealth;
		double opStrength;
		double myStrength;
		for (Integer targetId : this.helpRequested.keySet()) {
			extortersList = this.helpRequested.get(targetId);

			for (Integer extorterId : extortersList) {
				if (this.retaliate.containsKey(extorterId)) {
					retaliateList = this.retaliate.get(extorterId);
					if (retaliateList == null) {
						retaliateList = new ArrayList<Integer>();
					}
				} else {
					retaliateList = new ArrayList<Integer>();
				}

				if (!retaliateList.contains(targetId)) {
					// Attack anyway
					if (RandomHelper.nextDouble() <= this.retaliatePropensity) {
						retaliateList.add(targetId);
						this.retaliate.put(extorterId, retaliateList);
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
							this.retaliate.put(extorterId, retaliateList);
						}
					}
				}
			}
		}
	}

	@Override
	public void decideToCounterattack() {
		ExtorterAbstract extorter;
		int maxNumExtorted;
		double maxWealth;
		int opNumExtorted;
		double opWealth;
		double opStrength;
		double myStrength;
		for (Integer extorterId : this.retaliation.keySet()) {
			// Counterattack anyway
			if (RandomHelper.nextDouble() <= this.counterattackPropensity) {
				this.counterattack.add(extorterId);
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
					this.counterattack.add(extorterId);
				}
			}
		}
	}

	@Override
	public void decideToPunish() {

		/**
		 * System.out.println("====================== CYCLE " + this.cycle);
		 * System.out.println("MY ID " + this.id);
		 * System.out.print("   Extorted: "); for (Integer extort :
		 * this.extorted.keySet()) { System.out.print(extort + " "); }
		 * System.out.println(); System.out.print("   Requested Help: "); for
		 * (Integer help : this.helpRequested.keySet()) { System.out.print(help
		 * + " ["); for (Integer against : this.helpRequested.get(help)) {
		 * System.out.print(against + ";"); } System.out.println("]"); }
		 * System.out.println(); System.out.print("   Attacked: "); for (Integer
		 * attack : this.retaliate.keySet()) { System.out.print(attack + " "); }
		 * System.out.println(); System.out.print("   Received Attack: "); for
		 * (Integer wasAttacked : this.retaliation.keySet()) {
		 * System.out.print(wasAttacked + " ["); for (Integer because :
		 * this.retaliation.get(wasAttacked)) { System.out.print(because + ";");
		 * } System.out.println("]"); } System.out.println();
		 * System.out.print("   I Counterattack: "); for (Integer ca :
		 * this.counterattack) { System.out.print(ca + " "); }
		 * System.out.println(); System.out.print("   I was Counterattacked: ");
		 * for (Integer ca : this.counterattacked) { System.out.print(ca + " ");
		 * } System.out.println(); System.out.print("   Paid: "); for (Integer
		 * paid : this.paid.keySet()) { System.out.print(paid + " "); }
		 * System.out.println();
		 **/

		Map<Integer, Set<Integer>> attackedBecauseTarget = new Hashtable<Integer, Set<Integer>>();
		Set<Integer> otherExtorters;
		for (Integer extorterId : this.retaliate.keySet()) {
			for (Integer targetId : this.retaliate.get(extorterId)) {

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
		for (Integer extorterId : this.retaliation.keySet()) {
			for (Integer targetId : this.retaliation.get(extorterId)) {

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

			// Extorter was Not Asked For Help and Was Not Attacked
			if ((!this.helpRequested.containsKey(targetId))
					&& (!wasAttackedBecauseTarget.containsKey(targetId))) {
				if (!this.paid.containsKey(targetId)) {
					this.punishments.put(targetId, 0.0);

					// System.out.println("(1) " + this.id + " PUNISHING "
					// + targetId);
				}
				// Extorter was Asked for Help
			} else if (this.helpRequested.containsKey(targetId)) {

				// Attacked
				if (attackedBecauseTarget.containsKey(targetId)) {
					attackedExtorters = attackedBecauseTarget.get(targetId);

					// Counterattacked because of this Target Request Help?
					isCounterattackedBecauseTarget = false;
					for (Integer extorterId : attackedExtorters) {
						if (this.counterattacked.contains(extorterId)) {
							isCounterattackedBecauseTarget = true;
						}
					}

					// Yes, Counterattacked because of this Target Request Help
					if (isCounterattackedBecauseTarget) {
						if (!this.paid.containsKey(targetId)) {
							// System.out.println("(2) " + this.id +
							// " PUNISHING "
							// + targetId);

							this.punishments.put(targetId, 0.0);
						}

						// Was Not Counterattacked because of this Target
						// Request
						// Help
					} else {
						if (!this.paid.containsKey(targetId)) {
							// System.out.println("(3) " + this.id +
							// " PUNISHING "
							// + targetId);

							this.punishments.put(targetId, 0.0);
						}
					}

					// Did not Attacked
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
				if (!this.counterattack.contains(extorterId)) {
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

		/**
		 * System.out.println(); System.out.print("   Punished: "); for (Integer
		 * punish : this.punishments.keySet()) { System.out.print(punish + " ");
		 * } System.out.println();
		 **/
	}

	@Override
	public void receivePayment(int id, double extortion) {
		super.receivePayment(id, extortion);
	}
}
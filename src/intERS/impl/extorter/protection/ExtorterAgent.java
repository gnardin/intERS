package intERS.impl.extorter.protection;

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
			Integer id, ExtorterConf extorterConf) {
		super(extorters, targets, initialTargets, id, extorterConf);
	}

	@Override
	public void decideProtection() {
		List<Integer> protectionList;
		List<Integer> nonProtectionList;
		List<Integer> extortersList;
		ExtorterAbstract extorter;
		double sumTarget;
		double sumWealth;
		double opTarget;
		double opWealth;
		double opStrength;
		double myStrength;
		for (Integer targetId : this.paymentProtection.keySet()) {
			extortersList = this.paymentProtection.get(targetId);

			for (Integer extorterId : extortersList) {
				if (this.attackProtection.containsKey(extorterId)) {
					protectionList = this.attackProtection.get(extorterId);
				} else {
					protectionList = new ArrayList<Integer>();
				}

				if (!protectionList.contains(targetId)) {
					// Attack Protection anyway
					if (RandomHelper.nextDouble() <= this.impulseProtectionProb) {
						protectionList.add(targetId);
						this.attackProtection.put(extorterId, protectionList);

						// Rational decision to protect
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

						// If stronger then attack protection, otherwise do not
						if (opStrength < myStrength) {
							protectionList.add(targetId);
							this.attackProtection.put(extorterId,
									protectionList);
						} else {
							if (this.nonAttackProtection
									.containsKey(extorterId)) {
								nonProtectionList = this.nonAttackProtection
										.get(extorterId);
							} else {
								nonProtectionList = new ArrayList<Integer>();
							}

							nonProtectionList.add(targetId);
							this.nonAttackProtection.put(extorterId,
									nonProtectionList);
						}
					}
				}
			}
		}
	}

	@Override
	public void decideCounterattackProtection() {
		ExtorterAbstract extorter;
		double sumTarget;
		double sumWealth;
		double opTarget;
		double opWealth;
		double opStrength;
		double myStrength;
		for (Integer extorterId : this.protection.keySet()) {
			// Counterattack anyway
			if (RandomHelper.nextDouble() <= this.impulseFightProtectionProb) {
				this.counterattackProtection.add(extorterId);

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

				// If stronger then attack, otherwise do not
				if (opStrength < myStrength) {
					this.counterattackProtection.add(extorterId);
				}
			}
		}
	}

	@Override
	public void decideRetaliation() {
	}

	@Override
	public void decideCounterattackRetaliation() {
	}

	@Override
	public void decidePunishment() {
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

		for (Integer targetId : this.extorted.keySet()) {

			// Extorter was NOT paid
			if (!this.paid.containsKey(targetId)) {

				// Extorter was requested Protection
				// PUNISH
				if (this.paymentProtection.containsKey(targetId)) {
					if (!this.punishments.contains(targetId)) {
						this.punishments.add(targetId);
					}

					// Extorter was NOT requested Protection
				} else {

					// Extorter was attacked because the Target
					if (wasAttackedBecauseTarget.containsKey(targetId)) {

						boolean counterattackedAll = true;
						for (Integer extorterId : wasAttackedBecauseTarget
								.get(targetId)) {
							if (!this.counterattackProtection
									.contains(extorterId)) {
								counterattackedAll = false;
							}
						}

						// Extorter counterattacked all attackers
						if (counterattackedAll) {
							if (!this.punishments.contains(targetId)) {
								this.punishments.add(targetId);
							}

							// Extorter did NOT counterattack all attackers
						} else {
							if (this.extorted.containsKey(targetId)) {
								this.targetsToRemove.add(targetId);
							}
						}

						// Extorter was NOT attacked because the Target
					} else {
						if (!this.punishments.contains(targetId)) {
							this.punishments.add(targetId);
						}
					}
				}
			}
		}
	}
}
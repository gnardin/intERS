package intERS.impl.extorter.noCompetition;

import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.ExtorterConf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
		for (Integer extorterId : this.extortersExtortingMyTargets.keySet()) {
			targets = this.extortersExtortingMyTargets.get(extorterId);

			this.nonAttackRetaliation.put(extorterId, new ArrayList<Integer>(
					targets.keySet()));
		}
	}

	@Override
	public void decideCounterattackRetaliation() {
	}

	@Override
	public void decidePunishment() {
		for (Integer targetId : this.extorted.keySet()) {
			if (!this.paid.containsKey(targetId)) {
				if (!this.punishments.contains(targetId)) {
					this.punishments.add(targetId);
				}
			}
		}
	}
}
package intERS.impl.withProtection;

import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.ExtorterConf;

import java.util.Map;
import java.util.Set;

public class ExtorterAgentProportionalProportional extends
		ExtorterAgentProportional {

	private double punishmentPercentage;

	public ExtorterAgentProportionalProportional(
			Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets, Set<Integer> initialTargets,
			Integer id, ExtorterConf extorterConf) {
		super(extorters, targets, initialTargets, id, extorterConf);

		this.punishmentPercentage = extorterConf.getPunishment() / 100;
	}

	@Override
	public void decideToPunish() {
		super.decideToPunish();

		double value;
		TargetAbstract target;
		for (Integer targetId : this.punishments.keySet()) {
			target = this.targets.get(targetId);

			value = this.punishmentPercentage * target.getRealIncome();

			this.punishments.put(targetId, value);
		}
	}
}
package intERS.impl.extorter.withoutProtection.punish.noRunaway;

import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.ExtorterConf;

import java.util.Map;
import java.util.Set;

public class ExtorterAgentProportional extends ExtorterAgent {

	private double extortionPercentage;

	public ExtorterAgentProportional(Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets, Set<Integer> initialTargets,
			Integer id, ExtorterConf extorterConf) {
		super(extorters, targets, initialTargets, id, extorterConf);

		if (extorterConf.getExtortion() > 0) {
			this.extortionPercentage = extorterConf.getExtortion() / 100;
		} else {
			this.extortionPercentage = 0;
		}
	}

	@Override
	public void updateExtortion() {
		TargetAbstract target;
		double extortion;
		for (Integer targetId : this.extorted.keySet()) {
			target = this.targets.get(targetId);

			if (target != null) {
				extortion = this.extortionPercentage * target.getIncome();
				this.extorted.put(targetId, extortion);
			}
		}
	}
}
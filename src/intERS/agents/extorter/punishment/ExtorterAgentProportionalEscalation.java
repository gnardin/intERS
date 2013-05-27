package intERS.agents.extorter.punishment;

import intERS.agents.extorter.ExtorterAbstract;
import intERS.agents.extorter.extortion.ExtorterAgentProportional;
import intERS.agents.target.TargetAbstract;
import intERS.conf.scenario.ExtorterConf;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

public class ExtorterAgentProportionalEscalation extends
		ExtorterAgentProportional {

	// Balance between paid/not paid
	private Map<Integer, Integer> targetBalance;

	private double minEscalation;
	private double maxEscalation;
	private String formulaEscalation;

	public ExtorterAgentProportionalEscalation(
			Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets, Set<Integer> initialTargets,
			int id, ExtorterConf extorterConf) {
		super(extorters, targets, initialTargets, id, extorterConf);

		this.targetBalance = new HashMap<Integer, Integer>();
		this.minEscalation = extorterConf.getMinEscalation();
		this.maxEscalation = extorterConf.getMaxEscalation();
		this.formulaEscalation = extorterConf.getFormulaEscalation();
	}

	@Override
	public void decideToPunish() {
		super.decideToPunish();

		Evaluator eval;
		double value;
		Integer notPaid;
		TargetAbstract target;
		double punishmentPercentage;
		for (Integer targetId : this.punishments.keySet()) {
			if (this.targetBalance.containsKey(targetId)) {
				notPaid = this.targetBalance.get(targetId) + 1;
			} else {
				notPaid = 1;
			}
			this.targetBalance.put(targetId, notPaid);

			eval = new Evaluator();
			try {
				eval.putVariable("MIN",
						new Double(this.minEscalation).toString());
				eval.putVariable("MAX",
						new Double(this.maxEscalation).toString());
				eval.putVariable("NP", new Integer(notPaid).toString());

				punishmentPercentage = new Double(
						eval.evaluate(this.formulaEscalation));
				if (punishmentPercentage < 0) {
					punishmentPercentage = 0;
				} else if (punishmentPercentage > this.maxEscalation) {
					punishmentPercentage = this.maxEscalation;
				}

				target = this.targets.get(targetId);
				value = (punishmentPercentage / 100) * target.getRealIncome();
				this.punishments.put(targetId, value);

			} catch (EvaluationException e) {
				e.printStackTrace();
			}
		}
	}
}
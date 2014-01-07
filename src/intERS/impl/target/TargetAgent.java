package intERS.impl.target;

import intERS.agents.Demand;
import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.TargetConf;
import intERS.utils.Sort;

import java.util.HashMap;
import java.util.Map;

public class TargetAgent extends TargetAbstract {

	public TargetAgent(Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets, Integer id,
			TargetConf targetConf) {
		super(extorters, targets, id, targetConf);
	}

	@Override
	public void decidePaymentProtection() {

		// if ((this.extortions.size() > 1) && (this.id == 1862))
		// if (this.id == 407)
		// System.out.println(this.output.getCycle() + " " + this.id + " "
		// + this.extortions.size());

		// Calculate the convenience of paying each Extorter
		double convenience;
		double sumPunishment;
		Map<Integer, Double> sort = new HashMap<Integer, Double>();
		for (Integer extorterId : this.extortions.keySet()) {
			sumPunishment = 0;
			for (Integer otherExtorterId : this.extortions.keySet()) {
				if (otherExtorterId != extorterId) {
					// Punishment * Prob. Punishment
					sumPunishment += this.extortions.get(otherExtorterId)
							.getPunishment()
							* this.extortersInfo.get(otherExtorterId)
									.getPunishmentProb();
				}
			}

			// if ((this.extortions.size() > 1) && (this.id == 1862))
			// if (this.id == 407)
			// System.out.println(extorterId
			// + " "
			// + this.extortions.get(extorterId).getExtortion()
			// + " "
			// + this.extortions.get(extorterId).getPunishment()
			// + " "
			// + this.extortersInfo.get(extorterId).getExtortions()
			// .size()
			// + " "
			// + +this.extortersInfo.get(extorterId)
			// .getSuccessfulProtectionProb()
			// + " "
			// + this.extortersInfo.get(extorterId).getPunishments()
			// .size()
			// + " "
			// + +this.extortersInfo.get(extorterId)
			// .getPunishmentProb());

			// Extortion + (Sum Prob. Punishment * (1 - Successful Protection))
			convenience = this.extortions.get(extorterId).getExtortion()
					+ (sumPunishment * (1 - this.extortersInfo.get(extorterId)
							.getSuccessfulProtectionProb()));

			sort.put(extorterId, convenience);
		}

		// Sort Extorters by their convenience
		this.extortersRank = Sort.ascendentSortByValue(sort);
	}

	@Override
	public void decidePaymentNoProtection() {
		Object[] extorterIds = this.extortersRank.keySet().toArray();

		// Pay all the Extorters that protection was unsuccessful
		double auxIncomeAvailable = this.incomeAvailable;
		Demand demand;
		for (Object extorterId : extorterIds) {
			if (this.protectionNotSuccessful.contains((Integer) extorterId)) {
				demand = this.extortions.get((Integer) extorterId);

				if (auxIncomeAvailable > demand.getExtortion()) {
					this.extortersPaidNoProtection.add((Integer) extorterId);
					auxIncomeAvailable -= demand.getExtortion();
				}
			}
		}
	}
}
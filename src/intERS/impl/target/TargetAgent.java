package intERS.impl.target;

import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.TargetConf;
import intERS.utils.Sort;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class TargetAgent extends TargetAbstract {

	public TargetAgent(Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets, Integer id, TargetConf targetConf) {
		super(extorters, targets, id, targetConf);
	}

	@Override
	public void decideAskForProtection() {
		if (!this.extortions.isEmpty()) {

			// Output attributes
			int numPaid = 0;
			int numNotPaid = 0;
			double totalPaid = 0;
			double totalNotPaid = 0;

			double maxSeverity = 0;
			double maxWealth = 0;
			int maxNumPunishment = 0;
			int maxNumExtorted = 0;

			double severity;
			double wealth;
			int numPunishment;
			int numExtorted;

			// Calculate the Extorters Power
			Map<Integer, Summary> summary = new HashMap<Integer, Summary>();
			ExtorterAbstract extorter;
			Queue<Double> pList;
			for (Integer extorterId : this.extortions.keySet()) {
				extorter = this.extorters.get(extorterId);

				severity = 0;
				wealth = 0;
				numPunishment = 0;
				numExtorted = 0;

				pList = this.punishments.get(extorterId);
				if (pList == null) {
					pList = new LinkedList<Double>();
					this.punishments.put(extorterId, pList);
				}
				double sumSeverity = 0;
				for (Double p : pList) {
					sumSeverity += p;
					numPunishment++;
				}

				if (numPunishment > 0) {
					severity = sumSeverity / numPunishment;
				}
				wealth = extorter.getWealth();
				numExtorted = extorter.getNumberExtorted();

				summary.put(extorterId, new Summary(severity, numPunishment,
						wealth, numExtorted));

				if (numPunishment > maxNumPunishment) {
					maxNumPunishment = numPunishment;
				}

				if (severity > maxSeverity) {
					maxSeverity = severity;
				}

				if (wealth > maxWealth) {
					maxWealth = wealth;
				}

				if (numExtorted > maxNumExtorted) {
					maxNumExtorted = numExtorted;
				}
			}

			Map<Integer, Double> extorterPower = new HashMap<Integer, Double>();
			Summary s;
			double calc;
			for (Integer extorterId : summary.keySet()) {
				s = summary.get(extorterId);

				calc = 0;
				if (maxSeverity > 0) {
					calc += s.getSeverity() / maxSeverity;
				}

				if (maxNumPunishment > 0) {
					calc += s.getNumPunishment() / maxNumPunishment;
				}

				if (maxWealth > 0) {
					calc += s.getWealth() / maxWealth;
				}

				if (maxNumExtorted > 0) {
					calc += s.getNumExtorted() / maxNumExtorted;
				}

				extorterPower.put(extorterId, calc);
			}

			// Sort Exorters by their Powers
			Map<Integer, Double> sorted = Sort
					.descendentSortByValue(extorterPower);

			// Add Extorters paid into the Help Request List
			// Add Extorters not paid into the Help Request Against List
			double availableIncome = this.incomeCurrent
					* this.percIncomeForExtortion;
			double extortion;
			for (Integer extorterId : sorted.keySet()) {
				extortion = this.extortions.get(extorterId);

				// If Target can pay
				// AND there is no number restriction
				// OR attend the number restriction
				if ((availableIncome >= extortion)
						&& ((this.targetConf.getExtorterPerTarget() <= 0) || ((this.targetConf
								.getExtorterPerTarget() > 0) && (numPaid < this.targetConf
								.getExtorterPerTarget())))) {
					this.extortersAskProtection.add(extorterId);
					availableIncome -= extortion;

					// Output
					numPaid++;
					totalPaid += extortion;
				} else {
					this.extortersAskProtectionAgainst.add(extorterId);

					// Output
					numNotPaid++;
					totalNotPaid += extortion;
				}
			}

			// Output update
			this.output.setNumPaid(numPaid);
			this.output.setTotalPaid(totalPaid);
			this.output.setNumNotPaid(numNotPaid);
			this.output.setTotalNotPaid(totalNotPaid);
		}
	}

	@Override
	public void decideToPay() {
		this.extortersToPay = new ArrayList<Integer>(
				this.extortersAskProtection);
	}

	@Override
	public void decideToExit() {
		this.incomeCurrent -= (this.payment + this.punishment);

		if (this.incomeCurrent <= 0) {
			this.endCycle();
			this.die();
		} else {
			this.wealth += this.incomeCurrent;
		}
	}
}

/**
 * Data structure to record data about the Extorter power
 */
class Summary {

	private double severity;
	private double wealth;
	private int numPunishment;
	private int numExtorted;

	public Summary(double severity, int nPunishment, double wealth,
			int numExtorted) {
		this.severity = severity;
		this.numPunishment = nPunishment;
		this.wealth = wealth;
		this.numExtorted = numExtorted;
	}

	public double getSeverity() {
		return this.severity;
	}

	public int getNumPunishment() {
		return this.numPunishment;
	}

	public double getWealth() {
		return this.wealth;
	}

	public int getNumExtorted() {
		return this.numExtorted;
	}
}
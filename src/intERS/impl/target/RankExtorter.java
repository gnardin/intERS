package intERS.impl.target;

import java.util.HashMap;
import java.util.Map;

import intERS.agents.ExtorterAbstract;

public class RankExtorter {

	private static RankExtorter rankExtorter = null;

	private Map<Integer, Double> extorterPunishmentProb;

	private RankExtorter() {
		this.extorterPunishmentProb = new HashMap<Integer, Double>();
	}

	public static RankExtorter getInstance() {
		if (rankExtorter == null) {
			rankExtorter = new RankExtorter();
		}

		return rankExtorter;
	}

	/**
	 * Common knowledge about the Extorter's punishment probability
	 * 
	 * @param none
	 * @return none
	 */
	public void calcExtorterPunishmentProb(
			Map<Integer, ExtorterAbstract> extorters) {
		Double maxWealth = null;
		Double minWealth = null;
		Integer maxTargets = null;
		Integer minTargets = null;

		ExtorterAbstract extorter;
		for (Integer extorterId : extorters.keySet()) {
			extorter = extorters.get(extorterId);

			if ((maxWealth == null) || (maxWealth < extorter.getWealth())) {
				maxWealth = extorter.getWealth();
			}

			if ((minWealth == null) || (minWealth > extorter.getWealth())) {
				minWealth = extorter.getWealth();
			}

			if ((maxTargets == null)
					|| (maxTargets < extorter.getNumberTargets())) {
				maxTargets = extorter.getNumberTargets();
			}

			if ((minTargets == null)
					|| (minTargets > extorter.getNumberTargets())) {
				minTargets = extorter.getNumberTargets();
			}
		}

		double punishmentProb;
		double wealth;
		double targets;
		for (Integer extorterId : extorters.keySet()) {
			extorter = extorters.get(extorterId);

			if ((maxWealth - minWealth) != 0) {
				wealth = (double) extorter.getWealth()
						/ (double) (maxWealth - minWealth);
			} else {
				wealth = 1;
			}

			if ((maxTargets - minTargets) != 0) {
				targets = (double) extorter.getNumberTargets()
						/ (double) (maxTargets - minTargets);
			} else {
				targets = 1;
			}

			punishmentProb = (wealth + targets) / 2;

			extorterPunishmentProb.put(extorterId, punishmentProb);

			// int cycle = (int)
			// RunEnvironment.getInstance().getCurrentSchedule()
			// .getTickCount();
			// System.out.println(cycle + " " + extorterId + " " +
			// punishmentProb);
		}
	}

	/**
	 * Returns the calculated Extorter's punishment probability
	 * 
	 * @param extorterId
	 *            Extorter identification
	 * @return Extorter's punishment probability value
	 */
	public double getExtorterPunishmentProb(int extorterId) {
		double prob = 0;

		if (this.extorterPunishmentProb.containsKey(extorterId)) {
			prob = this.extorterPunishmentProb.get(extorterId);
		}

		return prob;
	}
}
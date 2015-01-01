package intERS.agents.entity;

import java.util.HashMap;
import java.util.Map;
import intERS.agents.ExtorterAbstract;

public class RankExtorter {
	
	private static RankExtorter		rankExtorter	= null;
	
	private Map<Integer, Double>	punishmentProb;
	
	
	/**
	 * Private constructor
	 * 
	 * @param none
	 * @return none
	 */
	private RankExtorter() {
		this.punishmentProb = new HashMap<Integer, Double>();
	}
	
	
	/**
	 * Constructor
	 * 
	 * @param none
	 * @return none
	 */
	public static RankExtorter getInstance() {
		if(rankExtorter == null) {
			rankExtorter = new RankExtorter();
		}
		
		return rankExtorter;
	}
	
	
	/**
	 * Calculate Extorter's punishment probability
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
		for(Integer extorterId : extorters.keySet()) {
			extorter = extorters.get(extorterId);
			
			if((maxWealth == null) || (maxWealth < extorter.getWealth())) {
				maxWealth = extorter.getWealth();
			}
			
			if((minWealth == null) || (minWealth > extorter.getWealth())) {
				minWealth = extorter.getWealth();
			}
			
			if((maxTargets == null) || (maxTargets < extorter.getNumberTargets())) {
				maxTargets = extorter.getNumberTargets();
			}
			
			if((minTargets == null) || (minTargets > extorter.getNumberTargets())) {
				minTargets = extorter.getNumberTargets();
			}
		}
		
		double punishmentProb;
		double wealth;
		double targets;
		for(Integer extorterId : extorters.keySet()) {
			extorter = extorters.get(extorterId);
			
			if((maxWealth - minWealth) != 0) {
				wealth = (double) (extorter.getWealth() - minWealth)
						/ (double) (maxWealth - minWealth);
			} else {
				wealth = 1;
			}
			
			if((maxTargets - minTargets) != 0) {
				targets = (double) (extorter.getNumberTargets() - minTargets)
						/ (double) (maxTargets - minTargets);
			} else {
				targets = 1;
			}
			
			punishmentProb = (wealth + targets) / 2.0;
			
			this.punishmentProb.put(extorterId, punishmentProb);
		}
	}
	
	
	/**
	 * Get the calculated Extorter's punishment probability value
	 * 
	 * @param extorterId
	 *          Extorter identification
	 * @return Extorter's punishment probability value
	 */
	public double getPunishmentProb(int extorterId) {
		double prob = 0;
		
		if(this.punishmentProb.containsKey(extorterId)) {
			prob = this.punishmentProb.get(extorterId);
		}
		
		return prob;
	}
}
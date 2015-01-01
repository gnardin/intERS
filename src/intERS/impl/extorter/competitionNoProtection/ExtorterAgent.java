package intERS.impl.extorter.competitionNoProtection;

import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.ExtorterConf;
import java.util.ArrayList;
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
	public void decidePunishment() {
		for(Integer targetId : this.extorted.keySet()) {
			if(!this.paid.containsKey(targetId)) {
				
				if(!this.punishments.contains(targetId)) {
					this.punishments.add(targetId);
				}
			}
		}
	}
	
	
	@Override
	public void decideProtection() {
		List<Integer> nonProtectionList;
		List<Integer> competitorsList;
		for(Integer targetId : this.targetExtorter.keySet()) {
			
			// Protect only if paid
			if(this.paid.containsKey(targetId)) {
				competitorsList = this.targetExtorter.get(targetId);
				
				for(Integer extorterId : competitorsList) {
					if(this.nonAttackProtection.containsKey(extorterId)) {
						nonProtectionList = this.nonAttackProtection.get(extorterId);
					} else {
						nonProtectionList = new ArrayList<Integer>();
					}
					
					nonProtectionList.add(targetId);
					this.nonAttackProtection.put(extorterId, nonProtectionList);
				}
			}
		}
	}
	
	
	@Override
	public void decideCounterattackProtection() {
	}
	
	
	@Override
	public void decideRetaliation() {
		List<Integer> targetsList;
		ExtorterAbstract extorter;
		
		double myTarget;
		double opTarget;
		double sumTarget;
		double myStrength;
		
		double myWealth;
		double opWealth;
		double sumWealth;
		double opStrength;
		for(Integer extorterId : this.extorterTarget.keySet()) {
			targetsList = this.extorterTarget.get(extorterId);
			
			// Attack Retaliation anyway
			if(RandomHelper.nextDouble() <= this.impulseRetaliationProb) {
				this.attackRetaliation.put(extorterId, targetsList);
				
				// Rational decision to retaliate
			} else {
				extorter = this.extorters.get(extorterId);
				
				myTarget = this.paid.size();
				opTarget = extorter.getNumberTargetsPaid();
				sumTarget = myTarget + opTarget;
				
				myWealth = this.wealth;
				opWealth = extorter.getWealth();
				sumWealth = myWealth + opWealth;
				
				opStrength = 0;
				myStrength = 0;
				if(sumTarget != 0) {
					opStrength = (double) opTarget / (double) sumTarget;
					myStrength = (double) myTarget / (double) sumTarget;
				}
				
				if(sumWealth != 0) {
					opStrength += ((double) opWealth / (double) sumWealth);
					myStrength += ((double) myWealth / (double) sumWealth);
				}
				
				// If stronger then attack retaliation, otherwise do not
				// attack but continue to extort the Target
				if(opStrength <= myStrength) {
					this.attackRetaliation.put(extorterId, targetsList);
				} else {
					this.nonAttackRetaliation.put(extorterId, targetsList);
				}
			}
		}
	}
	
	
	@Override
	public void decideCounterattackRetaliation() {
		ExtorterAbstract extorter;
		double myTarget;
		double opTarget;
		double sumTarget;
		double myStrength;
		
		double myWealth;
		double opWealth;
		double sumWealth;
		double opStrength;
		
		for(Integer extorterId : this.retaliation.keySet()) {
			// Counterattack Retaliation anyway
			if(RandomHelper.nextDouble() <= this.impulseFightRetaliationProb) {
				this.counterattackRetaliation.add(extorterId);
				
				// Rational decision to counterattack
			} else {
				extorter = this.extorters.get(extorterId);
				
				myTarget = this.paid.size();
				opTarget = extorter.getNumberTargetsPaid();
				sumTarget = myTarget + opTarget;
				
				myWealth = this.wealth;
				opWealth = extorter.getWealth();
				sumWealth = myWealth + opWealth;
				
				opStrength = 0;
				myStrength = 0;
				if(sumTarget != 0) {
					opStrength = (double) opTarget / (double) sumTarget;
					myStrength = (double) myTarget / (double) sumTarget;
				}
				
				if(sumWealth != 0) {
					opStrength = opStrength + ((double) opWealth / (double) sumWealth);
					myStrength = myStrength + ((double) myWealth / (double) sumWealth);
				}
				
				// If stronger or has the same strength then counterattack
				// retaliation, otherwise do nothing
				if(opStrength <= myStrength) {
					this.counterattackRetaliation.add(extorterId);
				}
			}
		}
	}
}
package intERS.impl.extorter.competitionProtection;

import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.ExtorterConf;
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
		List<Integer> targetsList;
		ExtorterAbstract extorter;
		double sumTarget;
		double sumWealth;
		double opTarget;
		double opWealth;
		double opStrength;
		double myStrength;
		boolean protect;
		for(Integer extorterId : this.extorterTarget.keySet()) {
			targetsList = this.extorterTarget.get(extorterId);
			
			// Check whether one of the targets paid me
			protect = false;
			for(Integer targetId : targetsList) {
				if(this.paid.containsKey(targetId)) {
					protect = true;
				}
			}
			
			if(protect) {
				// Attack Protection anyway
				if(RandomHelper.nextDouble() <= this.impulseProtectionProb) {
					this.attackProtection.put(extorterId, targetsList);
					
					// Rational decision to protect
				} else {
					extorter = this.extorters.get(extorterId);
					
					opTarget = extorter.getNumberTargetsPaid();
					sumTarget = this.paid.size() + opTarget;
					
					opWealth = extorter.getWealth();
					sumWealth = this.wealth + opWealth;
					
					opStrength = 0;
					myStrength = 0;
					if(sumTarget != 0) {
						opStrength = (double) opTarget / (double) sumTarget;
						myStrength = (double) this.paid.size() / (double) sumTarget;
					}
					
					if(sumWealth != 0) {
						opStrength = opStrength + ((double) opWealth / (double) sumWealth);
						myStrength = myStrength
								+ ((double) this.wealth / (double) sumWealth);
					}
					
					// If stronger then attack protection, otherwise do not
					if(opStrength <= myStrength) {
						targetsList = this.attackProtection.put(extorterId, targetsList);
					} else {
						this.nonAttackProtection.put(extorterId, targetsList);
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
		for(Integer extorterId : this.protection.keySet()) {
			// Counterattack anyway
			if(RandomHelper.nextDouble() <= this.impulseFightProtectionProb) {
				this.counterattackProtection.add(extorterId);
				
				// Rational decision to counterattack
			} else {
				extorter = this.extorters.get(extorterId);
				
				opTarget = extorter.getNumberTargetsPaid();
				sumTarget = this.paid.size() + opTarget;
				
				opWealth = extorter.getWealth();
				sumWealth = this.wealth + opWealth;
				
				opStrength = 0;
				myStrength = 0;
				if(sumTarget != 0) {
					opStrength = (double) opTarget / (double) sumTarget;
					myStrength = (double) this.paid.size() / (double) sumTarget;
				}
				
				if(sumWealth != 0) {
					opStrength = opStrength + ((double) opWealth / (double) sumWealth);
					myStrength = myStrength + ((double) this.wealth / (double) sumWealth);
				}
				
				// If stronger then attack, otherwise do not
				if(opStrength <= myStrength) {
					this.counterattackProtection.add(extorterId);
				}
			}
		}
	}
	
	
	@Override
	public void decideRetaliation() {
		List<Integer> targets;
		ExtorterAbstract extorter;
		double sumTarget;
		double sumWealth;
		double opTarget;
		double opWealth;
		double opStrength;
		double myStrength;
		for(Integer extorterId : this.extorterTarget.keySet()) {
			targets = this.extorterTarget.get(extorterId);
			
			// Attack Retaliation anyway
			if(RandomHelper.nextDouble() <= this.impulseRetaliationProb) {
				this.attackRetaliation.put(extorterId, targets);
				
				// Rational decision to retaliate
			} else {
				extorter = this.extorters.get(extorterId);
				
				opTarget = extorter.getNumberTargetsPaid();
				sumTarget = this.paid.size() + opTarget;
				
				opWealth = extorter.getWealth();
				sumWealth = this.wealth + opWealth;
				
				opStrength = 0;
				myStrength = 0;
				if(sumTarget != 0) {
					opStrength = (double) opTarget / (double) sumTarget;
					myStrength = (double) this.paid.size() / (double) sumTarget;
				}
				
				if(sumWealth != 0) {
					opStrength = opStrength + ((double) opWealth / (double) sumWealth);
					myStrength = myStrength + ((double) this.wealth / (double) sumWealth);
				}
				
				// If stronger then attack retaliation, otherwise do not
				// attack but continue to extort the Target
				if(opStrength <= myStrength) {
					this.attackRetaliation.put(extorterId, targets);
				} else {
					this.nonAttackRetaliation.put(extorterId, targets);
				}
			}
		}
	}
	
	
	@Override
	public void decideCounterattackRetaliation() {
		List<Integer> targets;
		ExtorterAbstract extorter;
		double sumTarget;
		double sumWealth;
		double opTarget;
		double opWealth;
		double opStrength;
		double myStrength;
		for(Integer extorterId : this.retaliation.keySet()) {
			// Counterattack Retaliation anyway
			if(RandomHelper.nextDouble() <= this.impulseFightRetaliationProb) {
				this.counterattackRetaliation.add(extorterId);
				
				// Rational decision to counterattack
			} else {
				extorter = this.extorters.get(extorterId);
				
				opTarget = extorter.getNumberTargetsPaid();
				sumTarget = this.paid.size() + opTarget;
				
				opWealth = extorter.getWealth();
				sumWealth = this.wealth + opWealth;
				
				opStrength = 0;
				myStrength = 0;
				if(sumTarget != 0) {
					opStrength = (double) opTarget / (double) sumTarget;
					myStrength = (double) this.paid.size() / (double) sumTarget;
				}
				
				if(sumWealth != 0) {
					opStrength = opStrength + ((double) opWealth / (double) sumWealth);
					myStrength = myStrength + ((double) this.wealth / (double) sumWealth);
				}
				
				// If stronger then counterattack retaliation,
				// otherwise do not
				if(opStrength <= myStrength) {
					this.counterattackRetaliation.add(extorterId);
					// Do not extort the Targets that was also extorted by
					// the winner Extorter
				} else {
					targets = this.retaliation.get(extorterId);
					
					for(Integer targetId : targets) {
						if(this.extorted.containsKey(targetId)) {
							this.targetsToRemove.add(targetId);
						}
					}
				}
			}
		}
	}
}
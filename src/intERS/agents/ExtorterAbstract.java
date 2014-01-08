package intERS.agents;

import intERS.agents.entity.Demand;
import intERS.conf.scenario.ExtorterConf;
import intERS.output.OutputExtorter;
import intERS.output.OutputRecorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;

public abstract class ExtorterAbstract {

	// Identification
	protected int id;

	// Type
	protected String type;

	// Cost of Fight Protection
	protected double costFightProtection;

	// Cost of Fight Retaliation
	protected double costFightRetaliation;

	// Cost of Punish
	protected double costPunish;

	// List of Extorter to counterattack because of Protection
	protected List<Integer> counterattackProtection;

	// List of Extorter from which counterattack because Protection was received
	protected List<Integer> counterattackedProtection;

	// List of Extorter to counterattack because of Retaliation
	protected List<Integer> counterattackRetaliation;

	// List of Extorter from which counterattack because Retaliation was
	// received
	protected List<Integer> counterattackedRetaliation;

	// Impulse to protect probability [0;1]
	protected double impulseProtectionProb;

	// Impulse to counterattack protection probability [0;1]
	protected double impulseFightProtectionProb;

	// Impulse to retaliation probability [0;1]
	protected double impulseRetaliationProb;

	// Impulse to counterattack retaliation probability [0;1]
	protected double impulseFightRetaliationProb;

	// Enlargement formula
	protected String enlargementFormula;

	// Enlargement probability
	protected double enlargementProb;

	// List of extorted Targets <Target Id, Demand>
	protected Map<Integer, Demand> extorted;

	// List of Extorters <Extorter Id, Extorter Object>
	protected Map<Integer, ExtorterAbstract> extorters;

	// List of Extorters paid <Extorter Id, <Target Id, Extortion paid>>
	protected Map<Integer, Map<Integer, Double>> extortersExtortingMyTargets;

	// List of protection requests <Target Id, Extorters list>
	protected Map<Integer, List<Integer>> paymentProtection;

	// Output cycle data
	protected OutputExtorter output;

	// Output recorder
	protected OutputRecorder outputRecorder;

	// Target payments <Target Id, Payment>
	protected Map<Integer, Double> paid;

	// Punishments <Target Id>
	protected List<Integer> punishments;

	// Extorters to attack for protection <Extorter Id, List Target Ids>
	protected Map<Integer, List<Integer>> attackProtection;

	// Extorters to non attack for protection <Extorter Id, List Target Ids>
	protected Map<Integer, List<Integer>> nonAttackProtection;

	// Extorters to attack for retaliation <Extorter Id, List Target Ids>
	protected Map<Integer, List<Integer>> attackRetaliation;

	// Extorters non attack for retaliation <Extorter Id, List Target Ids>
	protected Map<Integer, List<Integer>> nonAttackRetaliation;

	// List of received protection attacks <Extorter Id, List Target Ids>
	protected Map<Integer, List<Integer>> protection;

	// List of received attacks <Extorter ID, List Target Ids>
	protected Map<Integer, List<Integer>> retaliation;

	// List of available Targets <Target Id, Target Object>
	protected Map<Integer, TargetAbstract> targets;

	// List of Targets to remove from extorted list
	protected List<Integer> targetsToRemove;

	// Extortion value
	protected double extortion;

	// Punishment value
	protected double punishment;

	// Flag indicating the moment in which the extortion should be added to the
	// Extorter's wealth
	protected boolean updateAtEnd;

	// Accumulated wealth
	protected double wealth;

	// Amount won in each round
	protected double wealthWon;

	// Amount lost in each round
	protected double wealthLost;

	/**
	 * Constructor
	 * 
	 * @param extorters
	 *            List of Extorters
	 * @param targets
	 *            List of Targets
	 * @param id
	 *            Extorter identification
	 * @param extorterConf
	 *            Extorter configuration
	 * @return none
	 */
	public ExtorterAbstract(Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets, Set<Integer> initialTargets,
			Integer id, ExtorterConf extorterConf) {
		this.id = id;
		this.type = extorterConf.getType();

		this.enlargementFormula = extorterConf.getEnlargementProbability();
		this.enlargementProb = 0;

		this.costFightProtection = extorterConf.getCostFightProtection() / 100;
		this.costFightRetaliation = extorterConf.getCostFightAttack() / 100;
		this.costPunish = extorterConf.getCostPunish() / 100;

		this.impulseProtectionProb = extorterConf.getImpulseProtection() / 100;
		this.impulseFightProtectionProb = extorterConf.getImpulseFight() / 100;
		this.impulseRetaliationProb = extorterConf.getImpulseAttack() / 100;
		this.impulseFightRetaliationProb = extorterConf
				.getImpulseCounterattack() / 100;

		this.extorted = new HashMap<Integer, Demand>();
		for (Integer target : initialTargets) {
			this.extorted.put(target, new Demand(0.0, 0.0));
		}

		this.extorters = extorters;
		this.targets = targets;

		this.extortion = extorterConf.getExtortion(this.id);
		this.punishment = extorterConf.getPunishment(this.id);

		this.paymentProtection = new HashMap<Integer, List<Integer>>();
		this.paid = new HashMap<Integer, Double>();

		this.attackProtection = new HashMap<Integer, List<Integer>>();
		this.nonAttackProtection = new HashMap<Integer, List<Integer>>();
		this.protection = new HashMap<Integer, List<Integer>>();
		this.counterattackProtection = new ArrayList<Integer>();
		this.counterattackedProtection = new ArrayList<Integer>();

		this.attackRetaliation = new HashMap<Integer, List<Integer>>();
		this.nonAttackRetaliation = new HashMap<Integer, List<Integer>>();
		this.retaliation = new HashMap<Integer, List<Integer>>();
		this.counterattackRetaliation = new ArrayList<Integer>();
		this.counterattackedRetaliation = new ArrayList<Integer>();

		this.extortersExtortingMyTargets = new HashMap<Integer, Map<Integer, Double>>();
		this.punishments = new ArrayList<Integer>();

		this.targetsToRemove = new ArrayList<Integer>();

		this.updateAtEnd = extorterConf.getUpdateAtEnd();
		this.wealth = extorterConf.getInitialWealth();
		this.wealthWon = 0;
		this.wealthLost = 0;

		// Output
		this.outputRecorder = OutputRecorder.getInstance();
		this.output = new OutputExtorter(0, this.id, this.type, this.extortion,
				this.punishment);
		this.output.setNumTargets(this.extorted.size());
		this.output.setWealth(this.wealth);
		this.outputRecorder.addRecord(this.output);
	}

	/**
	 * Return identification
	 * 
	 * @param none
	 * @return Extorter identification
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Return Extorter type
	 * 
	 * @param none
	 * @return Extorter type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Return the extortion value
	 * 
	 * @return Extortion value
	 */
	public double getExtortion() {
		return this.extortion;
	}

	/**
	 * Return the punishment value
	 * 
	 * @return Punishment value
	 */
	public double getPunishment() {
		return this.punishment;
	}

	/**
	 * Return the imprecise number of extorted Targets
	 * 
	 * @param none
	 * @return Number of extorted Targets
	 */
	public int getNumberTargets() {
		return this.extorted.size();
	}

	/**
	 * Return the imprecise wealth accumulated
	 * 
	 * @param none
	 * @return Wealth
	 */
	public double getWealth() {
		return this.wealth;
	}

	/**
	 * Cycle initialization
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void beginCycle() {
		for (Integer targetId : this.extorted.keySet()) {
			this.extorted.put(targetId, new Demand(0.0, 0.0));
		}

		this.paymentProtection.clear();

		this.attackProtection.clear();
		this.nonAttackProtection.clear();
		this.protection.clear();
		this.counterattackProtection.clear();
		this.counterattackedProtection.clear();

		this.attackRetaliation.clear();
		this.nonAttackRetaliation.clear();
		this.counterattackRetaliation.clear();
		this.counterattackedRetaliation.clear();
		this.retaliation.clear();

		this.extortersExtortingMyTargets.clear();
		this.paid.clear();
		this.punishments.clear();

		this.targetsToRemove.clear();

		this.wealthWon = 0;
		this.wealthLost = 0;

		Evaluator eval = new Evaluator();
		this.enlargementProb = 0;
		try {
			eval.putVariable("TARGETS",
					new Integer(this.extorted.size()).toString());

			this.enlargementProb = new Double(
					eval.evaluate(this.enlargementFormula)) / 100.0;
		} catch (EvaluationException e) {
			e.printStackTrace();
		}

		int cycle = (int) RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount();
		this.output = new OutputExtorter(cycle, this.id, this.type,
				this.extortion, this.punishment);
	}

	/**
	 * Update the list of Targets to demand extortion from
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.05, interval = 1)
	public void updateTargets() {
		// Remove non existent Targets from extorted list
		for (Integer targetId : this.extorted.keySet()) {
			if (!this.targets.containsKey(targetId)) {
				this.targetsToRemove.add(targetId);
			}
		}
		for (Integer targetId : this.targetsToRemove) {
			this.extorted.remove(targetId);
		}
		this.targetsToRemove.clear();

		// Extorter has a probability to add new Target
		if ((this.enlargementProb > 0)
				&& (RandomHelper.nextDouble() <= this.enlargementProb)
				&& (this.extorted.size() < this.targets.size())) {

			Object[] targetIds = this.targets.keySet().toArray();
			int targetId;
			boolean finish = false;
			while (!finish) {
				targetId = (Integer) targetIds[RandomHelper.nextIntFromTo(0,
						targetIds.length - 1)];
				if (!this.extorted.containsKey(targetId)) {
					this.extorted.put(targetId, new Demand(0, 0));
					finish = true;
				}
			}
		}
	}

	/**
	 * Update the extortion amount to demand from the Targets
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.10, interval = 1)
	public void updateExtortionPunishment() {
		TargetAbstract target;
		Demand demand;
		double currentIncome;
		for (Integer targetId : this.extorted.keySet()) {
			target = this.targets.get(targetId);

			// NOTE: If necessary, include noise here
			currentIncome = target.getCurrentIncome();

			demand = new Demand(currentIncome * this.extortion, currentIncome
					* this.punishment);

			this.extorted.put(targetId, demand);
		}
	}

	/**
	 * Demand the extortion to Targets
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.15, interval = 1)
	public void demandExtortion() {
		// Output
		int numExtortionDemanded = 0;
		double totalExtortionDemanded = 0;

		TargetAbstract target;
		Demand demand;
		for (Integer targetId : this.extorted.keySet()) {
			if (this.targets.containsKey(targetId)) {
				demand = this.extorted.get(targetId);

				target = this.targets.get(targetId);
				target.receiveExtortionDemand(this.id, demand);

				// Output
				numExtortionDemanded++;
				totalExtortionDemanded += demand.getExtortion();
			}
		}

		// Output
		this.output.setNumTargets(this.extorted.size());
		this.output.setNumExtortionDemanded(numExtortionDemanded);
		this.output.setTotalExtortionDemanded(totalExtortionDemanded);
	}

	/**
	 * Receive payment for protection
	 * 
	 * @param targetId
	 *            Identification of the Target paying for protection
	 * @param extortion
	 *            Extortion value paid
	 * @param extorters
	 *            List of Extorters the Target is requesting protection from
	 * @return none
	 */
	public void receivePaymentProtection(int targetId, double extortion,
			List<Integer> extorters) {
		this.paymentProtection.put(targetId, extorters);
		this.paid.put(targetId, extortion);

		if (this.updateAtEnd) {
			this.wealthWon += extortion;
		} else {
			this.wealth += extortion;
		}

		// Output
		this.output.setNumExtortionReceived(this.output
				.getNumExtortionReceived() + 1);
		this.output.setTotalExtortionReceived(this.output
				.getTotalExtortionReceived() + extortion);
		this.output.setNumPaymentProtection(this.output
				.getNumPaymentProtection() + 1);
		if (!extorters.isEmpty()) {
			this.output.setNumProtectionRequested(this.output
					.getNumProtectionRequested() + 1);
		}
	}

	/**
	 * Decide to attack other Extorters to protect
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.30, interval = 1)
	public abstract void decideProtection();

	/**
	 * Protect by attacking other Extorters
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.35, interval = 1)
	public void attackProtection() {
		ExtorterAbstract extorter;
		List<Integer> targetList;
		for (Integer extorterId : this.attackProtection.keySet()) {
			targetList = this.attackProtection.get(extorterId);

			extorter = this.extorters.get(extorterId);
			extorter.receiveAttackProtection(this.id, targetList);
		}

		// Output
		this.output.setNumAttackProtection(this.attackProtection.size());
		this.output.setNumNonAttackProtection(this.nonAttackProtection.size());
	}

	/**
	 * Receive protect attack from other Extorters
	 * 
	 * @param extorterId
	 *            Identification of the protector Extorter
	 * @param targetList
	 *            List of Targets that required protection against this Extorter
	 * @result none
	 */
	public void receiveAttackProtection(int extorterId, List<Integer> targetList) {
		this.protection.put(extorterId, targetList);

		this.output.setNumAttackProtectionReceived(this.output
				.getNumAttackProtectionReceived() + 1);
	}

	/**
	 * Decide to counterattack because of protection
	 * 
	 * @param none
	 * @param none
	 */
	@ScheduledMethod(start = 1.40, interval = 1)
	public abstract void decideCounterattackProtection();

	/**
	 * Counterattack
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.45, interval = 1)
	public void counterattackProtection() {
		double lossWealthProtection = 0;
		ExtorterAbstract extorter;
		for (Integer extorterId : this.counterattackProtection) {
			extorter = this.extorters.get(extorterId);

			extorter.receiveCounterattackProtection(this.id);

			lossWealthProtection += this.costFightProtection
					* extorter.getWealth();
		}

		if (this.updateAtEnd) {
			this.wealthLost += lossWealthProtection;
		} else {
			this.wealth -= lossWealthProtection;
		}

		// Output
		this.output.setNumCounterattackProtection(this.counterattackProtection
				.size());
		this.output.setTotalLostFightProtection(this.output
				.getTotalLostFightProtection() + lossWealthProtection);
		this.output.setNumEscapeProtection(this.protection.size()
				- this.counterattackProtection.size());
	}

	/**
	 * Receive the counterattack because of protection
	 * 
	 * @param extorterId
	 *            Identification of the counterattack Extorter
	 * @return none
	 */
	public void receiveCounterattackProtection(int extorterId) {
		this.counterattackedProtection.add(extorterId);

		ExtorterAbstract extorter = this.extorters.get(extorterId);
		double lossWealthProtection = this.costFightProtection
				* extorter.getWealth();

		if (this.updateAtEnd) {
			this.wealthLost += lossWealthProtection;
		} else {
			this.wealth -= lossWealthProtection;
		}

		// Output
		this.output.setNumCounterattackProtectionReceived(this.output
				.getNumCounterattackProtectionReceived() + 1);
		this.output.setTotalLostFightProtection(this.output
				.getTotalLostFightProtection() + lossWealthProtection);
	}

	/**
	 * Inform whether it will try to protect
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.50, interval = 1)
	public void informProtection() {
		// Output
		int numProtectionProvided = 0;
		int numSuccessfulProtectionProvided = 0;

		List<Integer> extorters;
		Map<Integer, Boolean[]> protectedAgainst;
		// Provided and Successful protection
		Boolean[] protectionStatus;
		TargetAbstract target;
		boolean providedProtection;
		boolean successfulProtection;
		for (Integer targetId : this.paymentProtection.keySet()) {
			protectedAgainst = new HashMap<Integer, Boolean[]>();

			providedProtection = false;
			successfulProtection = true;

			extorters = this.paymentProtection.get(targetId);
			for (Integer extorterId : extorters) {
				protectionStatus = new Boolean[2];

				if (this.attackProtection.containsKey(extorterId)) {
					protectionStatus[0] = true;
					providedProtection = true;

					if (!this.counterattackedProtection.contains(extorterId)) {
						protectionStatus[1] = true;
					} else {
						protectionStatus[1] = false;
						successfulProtection = false;
					}
				} else {
					protectionStatus[0] = false;
					protectionStatus[1] = false;
					successfulProtection = false;
				}

				protectedAgainst.put(extorterId, protectionStatus);
			}

			if (!protectedAgainst.isEmpty()) {
				target = this.targets.get(targetId);
				target.receiveInformProtection(this.id, protectedAgainst);
			}

			if (!extorters.isEmpty()) {
				if (providedProtection) {
					numProtectionProvided++;
				}

				if (successfulProtection) {
					numSuccessfulProtectionProvided++;
				}
			}
		}

		// Output
		this.output.setNumProtectionProvided(numProtectionProvided);
		this.output
				.setNumSuccessfulProtectionProvided(numSuccessfulProtectionProvided);
	}

	/**
	 * Receive Target's payment before punishment
	 * 
	 * @param targetId
	 *            Target identification
	 * @param payment
	 *            <Extorter Id, Extortion Value>
	 */
	public void receivePaymentNoProtection(int targetId,
			Map<Integer, Double> payments) {

		// If Extorter was paid
		double extortion = 0;
		if ((payments.containsKey(this.id))
				&& (!this.paid.containsKey(targetId))) {
			extortion = payments.get(this.id);
		}

		if (extortion > 0) {
			if (this.updateAtEnd) {
				this.wealthWon += extortion;
			} else {
				this.wealth += extortion;
			}
			this.paid.put(targetId, extortion);

			// Output
			this.output.setNumExtortionReceived(this.output
					.getNumExtortionReceived() + 1);
			this.output.setTotalExtortionReceived(this.output
					.getTotalExtortionReceived() + extortion);

			// If Extorter was not paid
		} else {
			Map<Integer, Double> listPayments;
			for (Integer extorterId : payments.keySet()) {

				if ((payments.get(extorterId) > 0)
						&& (!extorterId.equals(this.id))) {
					if (this.extortersExtortingMyTargets
							.containsKey(extorterId)) {
						listPayments = this.extortersExtortingMyTargets
								.get(extorterId);
					} else {
						listPayments = new HashMap<Integer, Double>();
					}
					listPayments.put(targetId, payments.get(extorterId));
					this.extortersExtortingMyTargets.put(extorterId,
							listPayments);
				}
			}
		}
	}

	/**
	 * Decide to retaliate
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.65, interval = 1)
	public abstract void decideRetaliation();

	/**
	 * Retaliation
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.70, interval = 1)
	public void attackRetaliation() {
		List<Integer> targets;
		ExtorterAbstract extorter;
		for (Integer extorterId : this.attackRetaliation.keySet()) {
			extorter = this.extorters.get(extorterId);

			targets = this.attackRetaliation.get(extorterId);
			extorter.receiveAttackRetaliation(this.id, targets);
		}

		// Output
		this.output.setNumAttackRetaliation(this.attackRetaliation.size());
		this.output
				.setNumNonAttackRetaliation(this.nonAttackRetaliation.size());
	}

	/**
	 * Receive retaliation attack from other Extorters
	 * 
	 * @param extorterId
	 *            Identification of the retaliator Extorter
	 * @param targetList
	 *            List of targets I am being retaliate for
	 */
	public void receiveAttackRetaliation(int extorterId,
			List<Integer> targetList) {
		this.retaliation.put(extorterId, targetList);

		this.output.setNumAttackRetaliationReceived(this.output
				.getNumAttackRetaliationReceived() + 1);
	}

	/**
	 * Decide to counterattack retaliation
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.75, interval = 1)
	public abstract void decideCounterattackRetaliation();

	/**
	 * Counterattack retaliation
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.80, interval = 1)
	public void counterattackRetaliation() {
		double lossWealthRetaliation = 0.0;
		ExtorterAbstract extorter;
		for (Integer extorterId : this.counterattackRetaliation) {
			extorter = this.extorters.get(extorterId);

			extorter.receiveCounterattackRetaliation(this.id);

			lossWealthRetaliation += this.costFightRetaliation
					* extorter.getWealth();
		}

		if (this.updateAtEnd) {
			this.wealthLost += lossWealthRetaliation;
		} else {
			this.wealth -= lossWealthRetaliation;
		}

		// Output
		this.output
				.setNumCounterattackRetaliation(this.counterattackRetaliation
						.size());
		this.output.setTotalLostFightRetaliation(this.output
				.getTotalLostFightRetaliation() + lossWealthRetaliation);
		this.output.setNumEscapeRetaliation(this.retaliation.size()
				- this.counterattackRetaliation.size());
	}

	/**
	 * Receive counterattack retaliation from other Extorters
	 * 
	 * @param extorterId
	 *            Identification of the counterattacker Extorter
	 */
	public void receiveCounterattackRetaliation(int extorterId) {
		this.counterattackedRetaliation.add(extorterId);

		ExtorterAbstract extorter = this.extorters.get(extorterId);
		double lossWealthRetaliation = this.costFightRetaliation
				* extorter.getWealth();

		if (this.updateAtEnd) {
			this.wealthLost += lossWealthRetaliation;
		} else {
			this.wealth -= lossWealthRetaliation;
		}

		// Output
		this.output.setNumCounterattackRetaliationReceived(this.output
				.getNumCounterattackRetaliationReceived() + 1);
		this.output.setTotalLostFightRetaliation(this.output
				.getTotalLostFightRetaliation() + lossWealthRetaliation);
	}

	/**
	 * Decide to punish
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.85, interval = 1)
	public abstract void decidePunishment();

	/**
	 * Punish
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.90, interval = 1)
	public void punish() {
		TargetAbstract target;
		double costPunish;
		double totalCostPunishment = 0;
		Demand demand;
		for (Integer targetId : this.punishments) {
			target = this.targets.get(targetId);
			demand = this.extorted.get(targetId);

			costPunish = demand.getPunishment();
			target.receivePunishment(this.id, costPunish);

			totalCostPunishment += costPunish * this.costPunish;
		}

		if (this.updateAtEnd) {
			this.wealthLost += totalCostPunishment;
		} else {
			this.wealth -= totalCostPunishment;
		}

		// Output
		this.output.setNumPunishment(this.punishments.size());
		this.output.setTotalLostPunishment(totalCostPunishment);
	}

	/**
	 * Decide to exit
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.95, interval = 1)
	public void decideExit() {

		if (this.updateAtEnd) {
			this.wealth += this.wealthWon - this.wealthLost;
		}

		if (this.wealth <= 0) {

			List<Integer> targetsList;
			List<Integer> extortersList;
			Map<Integer, List<Integer>> extortersTargets = new HashMap<Integer, List<Integer>>();

			// Extorter attacked and it was counterattacked
			for (Integer extorterId : this.attackProtection.keySet()) {
				if (this.counterattackedProtection.contains(extorterId)) {
					if (extortersTargets.containsKey(extorterId)) {
						targetsList = extortersTargets.get(extorterId);
					} else {
						targetsList = new ArrayList<Integer>();
					}

					for (Integer targetId : this.paymentProtection.keySet()) {
						extortersList = this.paymentProtection.get(targetId);
						if ((extortersList.contains(extorterId))
								&& (!targetsList.contains(targetId))) {
							targetsList.add(targetId);
						}
					}

					extortersTargets.put(extorterId, targetsList);
				}
			}

			// Extorter was attacked
			for (Integer extorterId : this.protection.keySet()) {
				// if (this.counterattackProtection.contains(extorterId)) {
				if (extortersTargets.containsKey(extorterId)) {
					targetsList = extortersTargets.get(extorterId);
				} else {
					targetsList = new ArrayList<Integer>();
				}

				for (Integer targetId : this.protection.get(extorterId)) {
					if (!targetsList.contains(targetId)) {
						targetsList.add(targetId);
					}
				}
				extortersTargets.put(extorterId, targetsList);
				// }
			}

			// Extorter retaliated and it was counterretaliated
			for (Integer extorterId : this.attackRetaliation.keySet()) {
				if (this.counterattackedRetaliation.contains(extorterId)) {
					if (extortersTargets.containsKey(extorterId)) {
						targetsList = extortersTargets.get(extorterId);
					} else {
						targetsList = new ArrayList<Integer>();
					}

					for (Integer targetId : this.extortersExtortingMyTargets
							.get(extorterId).keySet()) {
						if ((this.extortersExtortingMyTargets.get(extorterId)
								.get(targetId) > 0)
								&& (!targetsList.contains(targetId))) {
							targetsList.add(targetId);
						}
					}
					extortersTargets.put(extorterId, targetsList);
				}
			}

			// Extorter was retaliated
			for (Integer extorterId : this.retaliation.keySet()) {
				// if (this.counterattackRetaliation.contains(extorterId)) {
				if (extortersTargets.containsKey(extorterId)) {
					targetsList = extortersTargets.get(extorterId);
				} else {
					targetsList = new ArrayList<Integer>();
				}

				for (Integer targetId : this.retaliation.get(extorterId)) {
					if (!targetsList.contains(targetId)) {
						targetsList.add(targetId);
					}
				}
				extortersTargets.put(extorterId, targetsList);
				// }
			}

			// Independent Targets
			List<Integer> allocatedTargets = new ArrayList<Integer>();
			for (Integer extorterId : extortersTargets.keySet()) {
				for (Integer targetId : extortersTargets.get(extorterId)) {
					if (!allocatedTargets.contains(targetId)) {
						allocatedTargets.add(targetId);
					}
				}
			}

			List<Integer> generalTargetsList = new ArrayList<Integer>();
			for (Integer targetId : this.extorted.keySet()) {
				if (!allocatedTargets.contains(targetId)) {
					generalTargetsList.add(targetId);
				}
			}

			// Share Targets
			ExtorterAbstract extorter;
			for (Integer extorterId : extortersTargets.keySet()) {
				if (this.extorters.containsKey(extorterId)) {
					targetsList = extortersTargets.get(extorterId);

					for (Integer targetId : generalTargetsList) {
						if (!targetsList.contains(targetId)) {
							targetsList.add(targetId);
						}
					}

					extorter = this.extorters.get(extorterId);
					extorter.receiveNewTargets(targetsList);
				}
			}

			this.endCycle();
			this.die();
		}
	}

	/**
	 * Receive information from opponents released Targets
	 * 
	 * @param newTargets
	 *            List of targets that will be released from an opponent
	 *            Extorter
	 * @return none
	 */
	public void receiveNewTargets(List<Integer> newTargets) {
		for (Integer targetId : newTargets) {
			if (!this.extorted.containsKey(targetId)) {
				this.extorted.put(targetId, new Demand(0.0, 0.0));
			}
		}
	}

	/**
	 * End the cycle
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.97, interval = 1)
	public void endCycle() {
		for (Integer targetId : this.targetsToRemove) {
			if (this.extorted.containsKey(targetId)) {
				this.extorted.remove(targetId);
			}
		}

		// Output
		this.output.setWealth(this.wealth);

		this.outputRecorder.addRecord(this.output);
	}

	/**
	 * Remove the agent from the simulation
	 * 
	 * @param none
	 * @return none
	 */
	@SuppressWarnings("unchecked")
	protected void die() {
		Context<ExtorterAbstract> agent = ContextUtils.getContext(this);
		if (agent.size() > 1) {
			agent.remove(this);
		}
	}
}
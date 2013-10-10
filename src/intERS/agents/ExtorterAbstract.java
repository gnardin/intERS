package intERS.agents;

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

	// Cost of Fight
	protected double costFight;

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

	// Counterattack propensity [0;1]
	protected double counterattackPropensity;

	// Enlargement probability
	protected double enlargementProbability;

	// List of extorted Targets <Target Id, Amount demanded>
	protected Map<Integer, Double> extorted;

	// Extorter configuration
	protected ExtorterConf extorterConf;

	// List of Extorters <Extorter Id, Extorter Object>
	protected Map<Integer, ExtorterAbstract> extorters;

	// List of Extorters paid <Extorter Id, Extortion paid>
	protected Map<Integer, Double> extortersPaid;

	// List of protection requests <Target Id, Extorters list>
	protected Map<Integer, List<Integer>> protectionRequested;

	// Amount of wealth lost with fight
	private double lostWealth;

	// Output cycle data
	protected OutputExtorter output;

	// Output recorder
	protected OutputRecorder outputRecorder;

	// List of Target payments <Target Id, Payment>
	protected Map<Integer, Double> paid;

	// List of punishments <Target Id, Punishment>
	protected Map<Integer, Double> punishments;

	// List of Extorters to attack for protection <Extorter Id, List Target Ids>
	protected Map<Integer, List<Integer>> attackProtection;

	// List of Extorters to attack for retaliation <Extorter Id>
	protected List<Integer> attackRetaliation;

	// Protection propensity [0;1]
	protected double protectionPropensity;

	// List of received attack for protection <Extorter Id, List Target Ids>
	protected Map<Integer, List<Integer>> protection;

	// List of received retaliation <Extorter ID>
	protected List<Integer> retaliation;

	// List of available Targets <Target Id, Target Object>
	protected Map<Integer, TargetAbstract> targets;

	// Not Paying Tolerance
	protected double tolerance;

	// Accumulated wealth
	protected double wealth;

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
		this.extorterConf = extorterConf;

		this.enlargementProbability = 0;
		this.costFight = this.extorterConf.getCostFight() / 100;
		this.costPunish = this.extorterConf.getCostPunish() / 100;
		this.counterattackProtection = new ArrayList<Integer>();
		this.counterattackedProtection = new ArrayList<Integer>();
		this.counterattackRetaliation = new ArrayList<Integer>();
		this.counterattackedRetaliation = new ArrayList<Integer>();
		this.counterattackPropensity = this.extorterConf.getCounterattack() / 100;

		this.extorted = new HashMap<Integer, Double>();
		for (Integer target : initialTargets) {
			this.extorted.put(target, 0.0);
		}

		this.extorters = extorters;
		this.extortersPaid = new HashMap<Integer, Double>();
		this.protectionRequested = new HashMap<Integer, List<Integer>>();
		this.lostWealth = 0;
		this.outputRecorder = OutputRecorder.getInstance();
		this.paid = new HashMap<Integer, Double>();
		this.punishments = new HashMap<Integer, Double>();
		this.attackProtection = new HashMap<Integer, List<Integer>>();
		this.attackRetaliation = new ArrayList<Integer>();
		this.protectionPropensity = this.extorterConf.getRetaliation() / 100;
		this.protection = new HashMap<Integer, List<Integer>>();
		this.retaliation = new ArrayList<Integer>();
		this.targets = targets;
		this.tolerance = this.extorterConf.getTolerance() / 100;
		this.wealth = extorterConf.getInitialWealth();

		// Output
		this.output = new OutputExtorter(1, this.id,
				this.extorterConf.getType());
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
		return this.extorterConf.getType();
	}

	/**
	 * Return the imprecise number of extorted Targets
	 * 
	 * @param none
	 * @return Number of extorted Targets + noise
	 */
	public int getNumberExtorted() {
		return (int) (this.extorted.size() * RandomHelper.nextDoubleFromTo(1.0,
				1.5));
	}

	/**
	 * Return the number of extorted Targets
	 * 
	 * @param none
	 * @return Number of extorted Targets
	 */
	public int getRealNumberExtorted() {
		return this.extorted.size();
	}

	/**
	 * Return the imprecise wealth accumulated
	 * 
	 * @param none
	 * @return Wealth + noise
	 */
	public double getWealth() {
		return this.wealth * RandomHelper.nextDoubleFromTo(1.0, 1.5);
	}

	/**
	 * Return the real wealth accumulated
	 * 
	 * @param none
	 * @return Wealth
	 */
	public double getRealWealth() {
		return this.wealth;
	}

	/**
	 * Cycle initialization
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void beginCycle() {
		this.counterattackProtection.clear();
		this.counterattackedProtection.clear();
		this.counterattackRetaliation.clear();
		this.counterattackedRetaliation.clear();
		this.extortersPaid.clear();
		this.protectionRequested.clear();
		this.lostWealth = 0;

		int cycle = (int) RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount() + 1;
		this.output = new OutputExtorter(cycle, this.id,
				this.extorterConf.getType());

		this.paid.clear();
		this.punishments.clear();
		this.attackProtection.clear();
		this.attackRetaliation.clear();
		this.protection.clear();
		this.retaliation.clear();

		Evaluator eval = new Evaluator();
		try {
			eval.putVariable("TARGETS",
					new Integer(this.extorted.size()).toString());

			this.enlargementProbability = new Double(
					eval.evaluate(this.extorterConf.getEnlargementProbability())) / 100.0;
		} catch (EvaluationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the list of Targets to demand extortion from
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.05, interval = 1)
	public abstract void updateTargets();

	/**
	 * Update the amount to demand from each one in the extorted list
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.10, interval = 1)
	public abstract void updateExtortion();

	/**
	 * Demand the extortion to Targets
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.15, interval = 1)
	public void demandExtortion() {
		if (!this.extorted.isEmpty()) {

			// Output
			int numExtortion = 0;
			double totalExtortion = 0;

			TargetAbstract target;
			double calcExtortion;
			double extortion;
			for (Integer targetId : this.targets.keySet()) {
				target = this.targets.get(targetId);

				if (this.extorted.containsKey(target.getId())) {
					calcExtortion = this.extorted.get(target.getId());

					// Limits the extortion to an amount lesser or equal to the
					// Target income
					extortion = Math.min(target.getIncome(), calcExtortion);
					target.receiveExtortionRequest(this.id, extortion);

					// Output
					numExtortion++;
					totalExtortion += extortion;
				}
			}

			// Output
			this.output.setNumExtortion(numExtortion);
			this.output.setTotalExtortion(totalExtortion);
		}
	}

	/**
	 * Receive Protection Request
	 * 
	 * @param id
	 *            Identification of the Target requesting protection
	 * @param extorters
	 *            List of Extorters requesting protection against
	 * @return none
	 */
	public void receiveProtectionRequest(int id, List<Integer> extorters) {
		this.protectionRequested.put(id, extorters);

		// Output
		this.output.setNumProtectionRequested(this.output
				.getNumProtectionRequested() + 1);
	}

	/**
	 * Decide to attack other Extorters to protect
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.30, interval = 1)
	public abstract void decideToProtect();

	/**
	 * Protect by attacking other Extorters
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.35, interval = 1)
	public void attackProtection() {
		if (!this.attackProtection.isEmpty()) {
			// Output
			int numProtection = 0;

			ExtorterAbstract extorter;
			List<Integer> targetList;
			for (Integer extorterId : this.attackProtection.keySet()) {
				targetList = this.attackProtection.get(extorterId);

				extorter = this.extorters.get(extorterId);
				extorter.receiveAttackProtection(this.id, targetList);

				// Output
				numProtection++;
			}

			// Output
			this.output.setNumAttackProtection(numProtection);
		}
	}

	/**
	 * Receive protect attack from other Extorters
	 * 
	 * @param id
	 *            Identification of the protector Extorter
	 * @param targetList
	 *            List of Targets that required protection against the Extorter
	 * @result none
	 */
	public void receiveAttackProtection(int id, List<Integer> targetList) {
		this.protection.put(id, targetList);

		this.output.setNumReceivedAttackProtection(this.output
				.getNumReceivedAttackProtection() + 1);
	}

	/**
	 * Decide to counterattack because of protection
	 * 
	 * @param none
	 * @param none
	 */
	@ScheduledMethod(start = 1.40, interval = 1)
	public abstract void decideToCounterattackProtection();

	/**
	 * Counterattack
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.45, interval = 1)
	public void counterattackProtection() {
		if (!this.counterattackProtection.isEmpty()) {
			// Output
			int numCounterattackProtection = 0;

			double lossWealthProtection = 0;
			ExtorterAbstract extorter;
			for (Integer extorterId : this.counterattackProtection) {
				extorter = this.extorters.get(extorterId);

				if (this.protection.containsKey(extorterId)) {

					extorter.receiveCounterattackProtection(this.id);

					lossWealthProtection += this.costFight
							* extorter.getRealWealth();

					// Output
					numCounterattackProtection++;
				}
			}
			this.lostWealth += lossWealthProtection;

			// Output
			this.output
					.setNumCounterattackProtection(numCounterattackProtection);
			this.output.setTotalLostFightProtection(lossWealthProtection);
		}
	}

	/**
	 * Receive the counterattack because of protection
	 * 
	 * @param id
	 *            Identification of the counterattack Extorter
	 * @return none
	 */
	public void receiveCounterattackProtection(int id) {
		this.counterattackedProtection.add(id);

		ExtorterAbstract extorter = this.extorters.get(id);
		this.lostWealth += this.costFight * extorter.getRealWealth();

		// Output
		this.output.setNumReceivedCounterattackProtection(this.output
				.getNumReceivedCounterattackProtection() + 1);
		this.output.setTotalLostFightProtection(this.output
				.getTotalLostFightProtection()
				+ (this.costFight * extorter.getRealWealth()));
	}

	/**
	 * Inform if tried to help
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.50, interval = 1)
	public void informProtection() {
		if ((!this.protectionRequested.isEmpty())
				&& (!this.attackProtection.isEmpty())) {
			// Output
			int numProtectionProvided = 0;

			List<Integer> extorters;
			List<Integer> helped;
			TargetAbstract target;
			for (Integer targetId : this.protectionRequested.keySet()) {
				helped = new ArrayList<Integer>();

				extorters = this.protectionRequested.get(targetId);
				for (Integer extorterId : extorters) {
					if (this.attackProtection.containsKey(extorterId)) {
						helped.add(extorterId);
					}
				}

				if (!helped.isEmpty()) {
					target = this.targets.get(targetId);
					target.receiveProtectionInform(this.id, helped);

					// Output
					numProtectionProvided++;
				}
			}

			// Output
			this.output.setNumProtectionProvided(numProtectionProvided);
		}
	}

	/**
	 * Receive Target's payment
	 * 
	 * @param id
	 *            Target identification
	 * @param extortion
	 *            Amount paid
	 */
	public void receivePayment(int id, Map<Integer, Double> payments) {

		// If I was paid
		if (payments.containsKey(this.id)) {
			double extortion = payments.get(this.id);
			this.wealth += extortion;
			this.paid.put(id, extortion);

			// Output
			this.output.setNumExtortionReceived(this.output
					.getNumExtortionReceived() + 1);
			this.output.setTotalExtortionReceived(this.output
					.getTotalExtortionReceived() + extortion);
			// If I was not paid
		} else {
			for (Integer extorterId : payments.keySet()) {
				this.extortersPaid.put(extorterId, payments.get(extorterId));
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
	public abstract void decideToRetaliate();

	/**
	 * Retaliate
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.70, interval = 1)
	public void attackRetaliation() {
		if (!this.attackRetaliation.isEmpty()) {
			int numRetaliation = 0;

			ExtorterAbstract extorter;
			for (Integer extorterId : this.attackRetaliation) {
				extorter = this.extorters.get(extorterId);
				extorter.receiveAttackRetaliation(this.id);

				// Output
				numRetaliation++;
			}

			// Output
			this.output.setNumAttackRetaliation(numRetaliation);
		}
	}

	/**
	 * Receive retaliation attack from other Extorters
	 * 
	 * @param id
	 *            Identification of the retaliator Extorter
	 */
	public void receiveAttackRetaliation(int id) {
		this.retaliation.add(id);

		this.output.setNumAttackRetaliation(this.output
				.getNumAttackRetaliation() + 1);
	}

	/**
	 * Decide to counterattack retaliation
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.75, interval = 1)
	public abstract void decideToCounterattackRetaliation();

	/**
	 * Counterattack retaliation
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.80, interval = 1)
	public void counterattackRetaliation() {
		if (!this.counterattackRetaliation.isEmpty()) {
			// Output
			int numCounterattackRetaliation = 0;

			double lostWealthRetaliation = 0.0;
			ExtorterAbstract extorter;
			for (Integer extorterId : this.counterattackRetaliation) {
				extorter = this.extorters.get(extorterId);

				extorter.receiveCounterattackRetaliation(this.id);

				lostWealthRetaliation += this.costFight
						* extorter.getRealWealth();

				// Output
				numCounterattackRetaliation++;
			}
			this.lostWealth += lostWealthRetaliation;

			// Output
			this.output
					.setNumCounterattackProtection(numCounterattackRetaliation);
			this.output.setTotalLostFightRetaliation(lostWealthRetaliation);
		}
	}

	/**
	 * Receive counterattack retaliation from other Extorters
	 * 
	 * @param id
	 *            Identification of the counterattacker Extorter
	 */
	public void receiveCounterattackRetaliation(int id) {
		this.counterattackedRetaliation.add(id);

		ExtorterAbstract extorter = this.extorters.get(id);
		this.lostWealth += this.costFight * extorter.getRealWealth();

		// Output
		this.output.setNumReceivedCounterattackRetaliation(this.output
				.getNumReceivedCounterattackRetaliation() + 1);
		this.output.setTotalLostFightRetaliation(this.output
				.getTotalLostFightRetaliation()
				+ (this.costFight * extorter.getRealWealth()));
	}

	/**
	 * Decide to punish
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.85, interval = 1)
	public abstract void decideToPunish();

	/**
	 * Punish
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.90, interval = 1)
	public void punish() {
		// Output
		int numPunishment = 0;

		TargetAbstract target;
		double value;
		double totalValue = 0;
		for (Integer targetId : this.punishments.keySet()) {
			target = this.targets.get(targetId);

			value = this.punishments.get(targetId);
			target.receivePunishment(this.id, value);

			totalValue += value * this.costPunish;

			// Output
			numPunishment++;
		}

		// Output
		this.output.setNumPunishment(numPunishment);
		this.output.setTotalLostPunishment(totalValue);

		this.lostWealth += totalValue;
	}

	/**
	 * Decide to exit
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.95, interval = 1)
	public void decideToExit() {
		this.wealth -= this.lostWealth;

		if (this.wealth <= 0) {
			this.endCycle();
			this.die();
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
		// Output
		this.output.setWealth(this.wealth);
		this.output.setNumTargets(this.extorted.size());

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
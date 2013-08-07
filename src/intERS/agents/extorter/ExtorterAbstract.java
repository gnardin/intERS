package intERS.agents.extorter;

import intERS.agents.target.TargetAbstract;
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

	// TODO Graduated Cost of Fight
	// Cost of Fight
	protected double costFight;

	// Cost of Punish
	protected double costPunish;

	// List of Extorter to counterattack
	protected List<Integer> counterattack;

	// List of Extorter from which counterattack was received
	protected List<Integer> counterattacked;

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

	// List of help requests <Target Id, Extorters list>
	protected Map<Integer, List<Integer>> helpRequested;

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

	// List of Extorters to retaliate <Extorter Id, List Target Ids>
	protected Map<Integer, List<Integer>> retaliate;

	// Retaliate propensity [0;1]
	protected double retaliatePropensity;

	// List of received retaliation <Extorter Id, List Target Ids>
	protected Map<Integer, List<Integer>> retaliation;

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
			int id, ExtorterConf extorterConf) {
		this.id = id;
		this.extorterConf = extorterConf;

		this.enlargementProbability = 0;
		this.costFight = this.extorterConf.getCostFight() / 100;
		this.costPunish = this.extorterConf.getCostPunish() / 100;
		this.counterattack = new ArrayList<Integer>();
		this.counterattacked = new ArrayList<Integer>();
		this.counterattackPropensity = this.extorterConf.getCounterattack() / 100;

		this.extorted = new HashMap<Integer, Double>();
		for (Integer target : initialTargets) {
			this.extorted.put(target, 0.0);
		}

		this.extorters = extorters;
		this.helpRequested = new HashMap<Integer, List<Integer>>();
		this.lostWealth = 0;
		this.outputRecorder = OutputRecorder.getInstance();
		this.paid = new HashMap<Integer, Double>();
		this.punishments = new HashMap<Integer, Double>();
		this.retaliate = new HashMap<Integer, List<Integer>>();
		this.retaliatePropensity = this.extorterConf.getRetaliation() / 100;
		this.retaliation = new HashMap<Integer, List<Integer>>();
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
		this.counterattack.clear();
		this.counterattacked.clear();
		this.helpRequested.clear();
		this.lostWealth = 0;

		int cycle = (int) RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount() + 1;
		this.output = new OutputExtorter(cycle, this.id,
				this.extorterConf.getType());

		this.paid.clear();
		this.punishments.clear();
		this.retaliate.clear();
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
					target.receiveExtortion(this.id, extortion);

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
	 * Receive Help Request
	 * 
	 * @param id
	 *            Identification of the Target requesting help
	 * @param extorters
	 *            List of Extorters requesting help against
	 * @return none
	 */
	public void receiveHelpRequest(int id, List<Integer> extorters) {
		this.helpRequested.put(id, extorters);

		// Output
		this.output.setNumHelpRequested(this.output.getNumHelpRequested() + 1);
	}

	/**
	 * Decide to attack other Extorters
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.30, interval = 1)
	public abstract void decideToRetaliate();

	/**
	 * Retaliate by attacking other Extorters
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.35, interval = 1)
	public void retaliate() {
		if (!this.retaliate.isEmpty()) {
			// Output
			int numRetaliation = 0;

			ExtorterAbstract extorter;
			List<Integer> targetList;
			for (Integer extorterId : this.retaliate.keySet()) {
				targetList = this.retaliate.get(extorterId);

				extorter = this.extorters.get(extorterId);
				extorter.receiveRetaliation(this.id, targetList);

				// Output
				numRetaliation++;
			}

			// Output
			this.output.setNumRetaliation(numRetaliation);
		}
	}

	/**
	 * Receive retaliation from other Extorters
	 * 
	 * @param id
	 *            Identification of the retaliating Extorter
	 * @param targetList
	 *            List of Targets that required help against the Extorter
	 * @result none
	 */
	public void receiveRetaliation(int id, List<Integer> targetList) {
		this.retaliation.put(id, targetList);

		this.output.setNumReceivedRetaliation(this.output
				.getNumReceivedRetaliation() + 1);
	}

	/**
	 * Decide to counterattack
	 * 
	 * @param none
	 * @param none
	 */
	@ScheduledMethod(start = 1.40, interval = 1)
	public abstract void decideToCounterattack();

	/**
	 * Counterattack
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.45, interval = 1)
	public void counterattack() {
		if (!this.counterattack.isEmpty()) {
			// Output
			int numCounterattack = 0;

			ExtorterAbstract extorter;
			for (Integer extorterId : this.counterattack) {
				extorter = this.extorters.get(extorterId);

				if (this.retaliation.containsKey(extorterId)) {

					extorter.receiveCounterattack(this.id);

					this.lostWealth += this.costFight
							* extorter.getRealWealth();

					// Output
					numCounterattack++;
				}
			}

			// Output
			this.output.setNumCounterattack(numCounterattack);
			this.output.setTotalLostFight(this.lostWealth);
		}
	}

	/**
	 * Receive the counter attack
	 * 
	 * @param extorterId
	 *            Identification of the counterattack Extorter
	 * @return none
	 */
	public void receiveCounterattack(int extorterId) {
		this.counterattacked.add(extorterId);

		ExtorterAbstract extorter = this.extorters.get(extorterId);
		this.lostWealth += this.costFight * extorter.getRealWealth();

		// Output
		this.output.setNumReceivedCounterattack(this.output
				.getNumReceivedCounterattack() + 1);
		this.output.setTotalLostFight(this.output.getTotalLostFight()
				+ (this.costFight * extorter.getRealWealth()));
	}

	/**
	 * Inform if tried to help
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.50, interval = 1)
	public void informHelp() {
		if ((!this.helpRequested.isEmpty()) && (!this.retaliate.isEmpty())) {
			// Output
			int numHelpProvided = 0;

			List<Integer> extorters;
			List<Integer> helped;
			TargetAbstract target;
			for (Integer targetId : this.helpRequested.keySet()) {
				helped = new ArrayList<Integer>();

				extorters = this.helpRequested.get(targetId);
				for (Integer extorterId : extorters) {
					if (this.retaliate.containsKey(extorterId)) {
						helped.add(extorterId);
					}
				}

				if (!helped.isEmpty()) {
					target = this.targets.get(targetId);
					target.receiveHelpInform(this.id, helped);

					// Output
					numHelpProvided++;
				}
			}

			// Output
			this.output.setNumHelpProvided(numHelpProvided);
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
	public void receivePayment(int id, double extortion) {
		this.wealth += extortion;
		this.paid.put(id, extortion);

		// Output
		this.output.setNumExtortionReceived(this.output
				.getNumExtortionReceived() + 1);
		this.output.setTotalExtortionReceived(this.output
				.getTotalExtortionReceived() + extortion);
	}

	/**
	 * Decide to punish
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.65, interval = 1)
	public abstract void decideToPunish();

	/**
	 * Punish
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.70, interval = 1)
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
	@ScheduledMethod(start = 1.75, interval = 1)
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
	@ScheduledMethod(start = 1.80, interval = 1)
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
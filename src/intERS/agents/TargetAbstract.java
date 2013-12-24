package intERS.agents;

import intERS.conf.scenario.TargetConf;
import intERS.output.OutputRecorder;
import intERS.output.OutputTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;

public abstract class TargetAbstract {

	// Identification
	protected int id;

	// List of received extortions <Extorter Id, Extortion>
	protected Map<Integer, Double> extortions;

	// List of Extorters <Extorter Id, Extorter Object>
	protected Map<Integer, ExtorterAbstract> extorters;

	// List of Extorters to pay
	protected List<Integer> extortersToPay;

	// Fixed income wealth
	protected double income;

	// Cycle income wealth
	protected double incomeCurrent;

	// Published income wealth
	protected double incomePublished;

	// List Extorters protected against
	protected Map<Integer, List<Integer>> protectionInfo;

	// List Extorters to requested protection
	protected List<Integer> extortersAskProtection;

	// List Extorters to request protection against
	protected List<Integer> extortersAskProtectionAgainst;

	// Output cycle data
	protected OutputTarget output;

	// Output recorder
	protected OutputRecorder outputRecorder;

	// Percentage of the Income that may be used to pay Extortion
	protected double percIncomeForExtortion;

	// Accumulated Payment
	// protected double payment;

	// Accumulated Punishment
	// protected double punishment;

	// List of punishments per Extorter
	protected Map<Integer, Queue<Double>> punishments;

	// List of available Targets <Target Id, Target Object>
	protected Map<Integer, TargetAbstract> targets;

	// Target Configuration
	protected TargetConf targetConf;

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
	 *            Target identification
	 * @param targetConf
	 *            Target configuration parameters
	 * @return none
	 */
	public TargetAbstract(Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets, Integer id,
			TargetConf targetConf) {
		this.id = id;
		this.extortions = new HashMap<Integer, Double>();
		this.extorters = extorters;
		this.extortersToPay = new ArrayList<Integer>();
		this.protectionInfo = new HashMap<Integer, List<Integer>>();
		this.extortersAskProtection = new ArrayList<Integer>();
		this.extortersAskProtectionAgainst = new ArrayList<Integer>();

		this.income = RandomHelper.nextDoubleFromTo(targetConf.getMinIncome(),
				targetConf.getMaxIncome());
		this.incomePublished = this.income
				* (RandomHelper.nextDoubleFromTo(
						targetConf.getMinIncomeVariation(),
						targetConf.getMaxIncomeVariation()) / 100.0);

		this.outputRecorder = OutputRecorder.getInstance();

		this.percIncomeForExtortion = RandomHelper.nextDoubleFromTo(
				targetConf.getMinExtortion(), targetConf.getMaxExtortion()) / 100.0;

		this.punishments = new HashMap<Integer, Queue<Double>>();
		this.targetConf = targetConf;
		this.targets = targets;
		this.wealth = 0;

		// Output
		this.output = new OutputTarget(1, this.id, targetConf.getType());
		this.output.setIncome(this.income);
		this.outputRecorder.addRecord(this.output);
	}

	/**
	 * Return identification
	 * 
	 * @param none
	 * @return Target identification
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Return type
	 * 
	 * @param none
	 * @return Target type
	 */
	public String getType() {
		return this.targetConf.getType();
	}

	/**
	 * Return imprecise income
	 * 
	 * @param none
	 * @return Income + noise
	 */
	public double getIncome() {
		return this.incomePublished;
	}

	/**
	 * Return income
	 * 
	 * @param none
	 * @return Income
	 */
	public double getRealIncome() {
		return this.incomeCurrent;
	}

	/**
	 * Return wealth
	 * 
	 * @param none
	 * @return Wealth
	 */
	public double getRealWealth() {
		return this.wealth;
	}

	/**
	 * Return number of received extortions
	 * 
	 * @param none
	 * @return Number of received extortions
	 */
	public int getNumExtortions() {
		return extortions.size();
	}

	/**
	 * Cycle initialization
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1, interval = 1)
	public void beginCycle() {
		this.extortions.clear();
		this.extortersToPay.clear();
		this.protectionInfo.clear();
		this.extortersAskProtection.clear();
		this.extortersAskProtectionAgainst.clear();
		this.incomeCurrent = this.income
				* RandomHelper.nextDoubleFromTo(0.5, 1.5);

		int cycle = (int) RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount() + 1;
		this.output = new OutputTarget(cycle, this.id,
				this.targetConf.getType());
	}

	/**
	 * Receive extortion requests
	 * 
	 * @param extortion
	 *            Extortion request
	 * @return none
	 */
	public void receiveExtortionRequest(int id, double extortion) {
		this.extortions.put(id, extortion);

		// Output numExtortion and totalExtortion
		this.output.setNumExtortion(this.output.getNumExtortion() + 1);
		this.output.setTotalExtortion(this.output.getTotalExtortion()
				+ extortion);
	}

	/**
	 * Decide to which Extorters ask for protection
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.20, interval = 1)
	public abstract void decideAskForProtection();

	/**
	 * Request protection against some Extorters against others
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.25, interval = 1)
	public void requestProtection() {
		if ((!this.extortersAskProtection.isEmpty())
				&& (!this.extortersAskProtectionAgainst.isEmpty())) {
			ExtorterAbstract extorter;
			for (Integer extorterId : this.extortersAskProtection) {
				extorter = this.extorters.get(extorterId);
				extorter.receiveProtectionRequest(this.id,
						this.extortersAskProtectionAgainst);
			}

			// Output numProtectionRequested
			this.output.setNumProtectionRequested(this.extortersAskProtection
					.size());
		}
	}

	/**
	 * Receive the information about the Protection request
	 * 
	 * @param id
	 *            Extorter identification
	 * @param extorters
	 *            List of Extorters tried to protect against
	 * @return none
	 */
	public void receiveProtectionInform(int id, List<Integer> extorters) {
		this.protectionInfo.put(id, extorters);

		// Output numProtectionReceived
		this.output.setNumProtectionReceived(this.output
				.getNumProtectionReceived() + 1);
	}

	/**
	 * Decide to pay Extorters
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.40, interval = 1)
	public abstract void decideToPay();

	/**
	 * Perform the Extorters payment and inform those that are not paid
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.45, interval = 1)
	public void payInformNotPay() {
		Map<Integer, Double> payments = new HashMap<Integer, Double>();

		ExtorterAbstract extorter;
		double payment = 0;
		double value;
		for (Integer extorterId : this.extortersToPay) {
			extorter = this.extorters.get(extorterId);

			// If there is enough income
			value = this.extortions.get(extorterId);
			if ((this.incomeCurrent - (payment + value)) > 0) {
				payment += value;
				payments.put(extorterId, value);
			}
		}

		// Pay and inform Extorters about payments
		for (Integer extorterId : this.extortions.keySet()) {
			extorter = this.extorters.get(extorterId);
			extorter.receivePayment(this.id, payments);
		}

		this.incomeCurrent -= payment;
	}

	/**
	 * Receive the punishments
	 * 
	 * @param id
	 *            Extorter identification
	 * @param punishment
	 *            Applied punishment value
	 * @return none
	 */
	public void receivePunishment(int id, double punishment) {

		this.incomeCurrent -= punishment;

		// Update Punishment memory
		if (this.targetConf.getMemLength() >= 0) {
			Queue<Double> p = this.punishments.get(id);
			if (p == null) {
				p = new LinkedList<Double>();
			}

			if ((this.targetConf.getMemLength() > 0)
					&& (p.size() >= this.targetConf.getMemLength())) {
				p.poll();
			}
			p.offer(punishment);
			this.punishments.put(id, p);
		}

		// Output numPunishment and totalPunishment
		this.output.setNumPunishment(this.output.getNumPunishment() + 1);
		this.output.setTotalPunishment(this.output.getTotalPunishment()
				+ punishment);
	}

	/**
	 * Decide to exit
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.95, interval = 1)
	public void decideToExit() {
		if (this.incomeCurrent <= 0) {
			this.endCycle();
			this.die();
		} else {
			this.wealth += this.incomeCurrent;
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
		this.output.setWealth(this.wealth);
		this.output.setIncome(this.incomeCurrent);

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
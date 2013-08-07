package intERS.agents.target;

import intERS.agents.extorter.ExtorterAbstract;
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

	// List Extorters helped against
	protected Map<Integer, List<Integer>> helpInfo;

	// List Extorters to requested help
	protected List<Integer> extortersAskHelp;

	// List Extorters to request help against
	protected List<Integer> extortersAskHelpAgainst;

	// Output cycle data
	protected OutputTarget output;

	// Output recorder
	protected OutputRecorder outputRecorder;

	// Percentage of the Income that may be used to pay Extortion
	protected double percIncomeForExtortion;

	// Accumulated Payment
	protected double payment;

	// Accumulated Punishment
	protected double punishment;

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
			Map<Integer, TargetAbstract> targets, int id, TargetConf targetConf) {
		this.id = id;
		this.extortions = new HashMap<Integer, Double>();
		this.extorters = extorters;
		this.extortersToPay = new ArrayList<Integer>();
		this.helpInfo = new HashMap<Integer, List<Integer>>();
		this.extortersAskHelp = new ArrayList<Integer>();
		this.extortersAskHelpAgainst = new ArrayList<Integer>();

		this.income = RandomHelper.nextDoubleFromTo(targetConf.getMinIncome(),
				targetConf.getMaxIncome());
		this.incomePublished = this.income
				* RandomHelper.nextDoubleFromTo(1.0, 1.5);

		this.outputRecorder = OutputRecorder.getInstance();

		this.percIncomeForExtortion = RandomHelper.nextDoubleFromTo(
				targetConf.getMinExtortion(), targetConf.getMaxExtortion()) / 100;

		this.payment = 0;
		this.punishment = 0;
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
		this.helpInfo.clear();
		this.extortersAskHelp.clear();
		this.extortersAskHelpAgainst.clear();
		this.incomeCurrent = this.income
				* (1 + RandomHelper.nextDoubleFromTo(-0.5, 0.5));

		int cycle = (int) RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount() + 1;
		this.output = new OutputTarget(cycle, this.id,
				this.targetConf.getType());

		this.payment = 0;
		this.punishment = 0;
	}

	/**
	 * Receive extortion requests
	 * 
	 * @param extortion
	 *            Extortion request
	 * @return none
	 */
	public void receiveExtortion(int id, double extortion) {
		this.extortions.put(id, extortion);

		// Output numExtortion and totalExtortion
		this.output.setNumExtortion(this.output.getNumExtortion() + 1);
		this.output.setTotalExtortion(this.output.getTotalExtortion()
				+ extortion);
	}

	/**
	 * Decide to which Extorters ask for help
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.20, interval = 1)
	public abstract void decideAskForHelp();

	/**
	 * Request help to some Extorters against others
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.25, interval = 1)
	public void requestHelp() {
		if ((!this.extortersAskHelp.isEmpty())
				&& (!this.extortersAskHelpAgainst.isEmpty())) {
			ExtorterAbstract extorter;
			for (Integer extorterId : this.extortersAskHelp) {
				extorter = this.extorters.get(extorterId);
				extorter.receiveHelpRequest(this.id,
						this.extortersAskHelpAgainst);
			}

			// Output numHelpRequested
			this.output.setNumHelpRequested(this.extortersAskHelp.size());
		}
	}

	/**
	 * Receive the information about the Help request
	 * 
	 * @param id
	 *            Extorter identification
	 * @param extorters
	 *            List of Extorters helped against
	 * @return none
	 */
	public void receiveHelpInform(int id, List<Integer> extorters) {
		this.helpInfo.put(id, extorters);

		// Output numHelpReceived
		this.output.setNumHelpReceived(this.output.getNumHelpReceived() + 1);
	}

	/**
	 * Decide to pay Extorters
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.55, interval = 1)
	public abstract void decideToPay();

	/**
	 * Perform the Extorters payment
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.60, interval = 1)
	public void pay() {
		ExtorterAbstract extorter;
		double value;
		for (Integer extorterId : this.extortersToPay) {
			extorter = this.extorters.get(extorterId);

			// If there is enough income
			value = this.extortions.get(extorterId);
			if ((this.incomeCurrent - (this.payment + value)) > 0) {
				this.payment += value;
				extorter.receivePayment(this.id, value);
			}
		}
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

		this.punishment += punishment;

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
	@ScheduledMethod(start = 1.75, interval = 1)
	public abstract void decideToExit();

	/**
	 * End the cycle
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.80, interval = 1)
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
package intERS.agents;

import intERS.agents.entity.Demand;
import intERS.agents.entity.ExtorterInfo;
import intERS.conf.scenario.TargetConf;
import intERS.output.OutputRecorder;
import intERS.output.OutputTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;

public abstract class TargetAbstract {

	// Identification
	protected int id;

	// Type
	protected String type;

	// Memory length
	protected int memLength;

	// Number of Extorters per Target
	protected int numExtorterPerTarget;

	// Minimum income variation
	protected double minIncomeVariation;

	// Maximum income variation
	protected double maxIncomeVariation;

	// List of Extorters <Extorter Id, ExtorterInfo>
	protected Map<Integer, ExtorterAbstract> extorters;

	// List of Extorters Information <Extorter Id, ExtorterInfo>
	protected Map<Integer, ExtorterInfo> extortersInfo;

	// List of received extortions <Extorter Id, Demand>
	protected Map<Integer, Demand> extortions;

	// Rank of Extorters to pay <ExtorterId, Convinience>
	protected Map<Integer, Double> extortersRank;

	// Income available after payment
	protected double incomeAvailable;

	// Fixed income wealth
	protected double income;

	// Cycle income wealth
	protected double incomeCurrent;

	// List Extorters protected against
	protected Map<Integer, Map<Integer, Boolean[]>> protectionInfo;

	// List Extorters to requested protection
	protected List<Integer> extortersPaidProtection;

	// List Extorters to request protection against
	protected List<Integer> extortersPaidProtectionAgainst;

	// List of Extorters that protection failed
	protected List<Integer> protectionNotSuccessful;

	// List of Extorters to pay because protection failed
	protected List<Integer> extortersPaidNoProtection;

	// Percentage of the Income that may be used to pay Extortion
	protected double percAvailExtortionIncome;

	// Accumulated wealth
	protected double wealth;

	// Output cycle data
	protected OutputTarget output;

	// Output recorder
	protected OutputRecorder outputRecorder;

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
		this.type = targetConf.getType();
		this.memLength = targetConf.getMemLength();
		this.numExtorterPerTarget = targetConf.getExtorterPerTarget();
		this.minIncomeVariation = targetConf.getMinIncomeVariation() / 100;
		this.maxIncomeVariation = targetConf.getMaxIncomeVariation() / 100;

		this.extorters = extorters;
		this.extortersInfo = new HashMap<Integer, ExtorterInfo>();
		this.extortions = new HashMap<Integer, Demand>();
		this.extortersRank = new HashMap<Integer, Double>();
		this.protectionInfo = new HashMap<Integer, Map<Integer, Boolean[]>>();
		this.extortersPaidProtection = new ArrayList<Integer>();
		this.extortersPaidProtectionAgainst = new ArrayList<Integer>();
		this.protectionNotSuccessful = new ArrayList<Integer>();
		this.extortersPaidNoProtection = new ArrayList<Integer>();

		this.income = RandomHelper.nextDoubleFromTo(targetConf.getMinIncome(),
				targetConf.getMaxIncome());

		this.percAvailExtortionIncome = targetConf.getAvailExtortionIncome() / 100.0;

		this.wealth = 0;

		// Output
		this.outputRecorder = OutputRecorder.getInstance();
		this.output = new OutputTarget(0, this.id, targetConf.getType());
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
		return this.type;
	}

	/**
	 * Return current income
	 * 
	 * @param none
	 * @return Current income
	 */
	public double getCurrentIncome() {
		return this.incomeCurrent;
	}

	/**
	 * Return wealth
	 * 
	 * @param none
	 * @return Wealth
	 */
	public double getWealth() {
		return this.wealth;
	}

	/**
	 * Return number of received extortions
	 * 
	 * @param none
	 * @return Number of received extortions
	 */
	public int getNumExtortions() {
		return this.extortions.size();
	}

	/**
	 * Cycle initialization
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 0, interval = 1)
	public void beginCycle() {
		this.extortions.clear();
		this.extortersRank.clear();
		this.protectionInfo.clear();
		this.extortersPaidProtection.clear();
		this.extortersPaidProtectionAgainst.clear();
		this.protectionNotSuccessful.clear();
		this.extortersPaidNoProtection.clear();

		this.incomeCurrent = this.income
				* RandomHelper.nextDoubleFromTo(this.minIncomeVariation,
						this.maxIncomeVariation);

		this.incomeAvailable = this.incomeCurrent
				* this.percAvailExtortionIncome;

		int cycle = (int) RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount();
		this.output = new OutputTarget(cycle, this.id, this.type);

		this.output.setIncome(this.incomeCurrent);
	}

	/**
	 * Receive extortion demands
	 * 
	 * @param extorterId
	 *            Extorter identification
	 * @param demand
	 *            Extortion demand
	 * @return none
	 */
	public void receiveExtortionDemand(int extorterId, Demand demand) {
		this.extortions.put(extorterId, demand);

		if (!this.extortersInfo.containsKey(extorterId)) {
			this.extortersInfo
					.put(extorterId, new ExtorterInfo(this.memLength));
		}

		// Output numExtortion and totalExtortion
		this.output.setNumExtortion(this.output.getNumExtortion() + 1);
		this.output.setTotalExtortion(this.output.getTotalExtortion()
				+ demand.getExtortion());
	}

	/**
	 * Decide which Extorters to pay for protection
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.20, interval = 1)
	public abstract void decidePaymentProtection();

	/**
	 * Request protection against some Extorters against others
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.25, interval = 1)
	public void payProtection() {
		// Output
		int numExtortionPaid = 0;
		int numProtectionRequested = 0;
		double totalExtortionPaid = 0;

		double extortion;
		Object[] extorterIds = this.extortersRank.keySet().toArray();
		// Select the first possible Extorter it can pay extortion
		for (Object extorterId : extorterIds) {
			extortion = this.extortions.get((Integer) extorterId)
					.getExtortion();

			// Targets to pay
			if (((this.numExtorterPerTarget <= 0) || (this.extortersPaidProtection
					.size() < this.numExtorterPerTarget))
					&& (this.incomeAvailable >= extortion)) {
				this.extortersPaidProtection.add((Integer) extorterId);

				// Update the available income
				this.incomeAvailable -= extortion;
			} else {
				this.extortersPaidProtectionAgainst.add((Integer) extorterId);
			}
		}

		ExtorterAbstract extorter;
		ExtorterInfo extorterInfo;
		for (Integer extorterId : this.extortersPaidProtection) {
			extorter = this.extorters.get(extorterId);
			extortion = this.extortions.get(extorterId).getExtortion();

			extorter.receivePaymentProtection(this.id, extortion,
					this.extortersPaidProtectionAgainst);

			// Update the current income
			this.incomeCurrent -= extortion;

			numExtortionPaid++;
			if (!this.extortersPaidProtectionAgainst.isEmpty()) {
				numProtectionRequested++;
			}
			totalExtortionPaid += extortion;

			extorterInfo = this.extortersInfo.get(extorterId);
			extorterInfo
					.setNumRequestedProtectionAgainst(this.extortersPaidProtectionAgainst
							.size());
			this.extortersInfo.put(extorterId, extorterInfo);
		}

		// Output numPaymentProtection
		this.output.setNumPaymentProtection(numExtortionPaid);
		this.output.setNumProtectionRequested(numProtectionRequested);
		this.output.setNumExtortionPaid(numExtortionPaid);
		this.output.setTotalExtortionPaid(totalExtortionPaid);
	}

	/**
	 * Receive the information about the Protection request
	 * 
	 * @param extorterId
	 *            Extorter identification
	 * @param extorters
	 *            Information about which Extorter provided protection against
	 *            whom, and whether it was successful or not
	 * @return none
	 */
	public void receiveInformProtection(int extorterId,
			Map<Integer, Boolean[]> extorters) {
		this.protectionInfo.put(extorterId, extorters);

		int numReceivedProtectionAgainst = 0;

		boolean receivedProtection = true;
		boolean successfulProtection = true;
		for (Integer otherId : extorters.keySet()) {
			// Not protected
			if (!extorters.get(otherId)[0]) {
				receivedProtection = false;
				successfulProtection = false;

				if (!this.protectionNotSuccessful.contains(otherId)) {
					this.protectionNotSuccessful.add(otherId);
				}
				// Protected
			} else {
				// Not successful protection
				if (!extorters.get(otherId)[1]) {
					successfulProtection = false;

					if (!this.protectionNotSuccessful.contains(otherId)) {
						this.protectionNotSuccessful.add(otherId);
					}
				}

				numReceivedProtectionAgainst++;
			}
		}

		ExtorterInfo extorterInfo = this.extortersInfo.get(extorterId);

		if (receivedProtection) {
			this.output.setNumProtectionReceived(this.output
					.getNumProtectionReceived() + 1);

			extorterInfo
					.setNumReceivedProtectionAgainst(numReceivedProtectionAgainst);
		}

		if (successfulProtection) {
			this.output.setNumSuccessfulProtection(this.output
					.getNumSuccessfulProtection() + 1);
		}

		this.extortersInfo.put(extorterId, extorterInfo);
	}

	/**
	 * Decide to pay Extorters if Target does not receive payment
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.55, interval = 1)
	public abstract void decidePaymentNoProtection();

	/**
	 * Perform the Extorters payment and inform those that are not paid
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.60, interval = 1)
	public void payNoProtection() {
		Map<Integer, Double> payments = new HashMap<Integer, Double>();

		Demand demand;
		ExtorterInfo extorterInfo;
		int numExtortionNotPaid = 0;
		double totalExtortionNotPaid = 0;
		for (Integer extorterId : this.extortions.keySet()) {
			demand = this.extortions.get(extorterId);

			if (this.extortersPaidProtection.contains(extorterId)) {
				payments.put(extorterId, demand.getExtortion());
			} else if ((this.extortersPaidNoProtection.contains(extorterId))
					&& (this.incomeAvailable > demand.getExtortion())) {
				payments.put(extorterId, demand.getExtortion());

				this.incomeAvailable -= demand.getExtortion();
				this.incomeCurrent -= demand.getExtortion();
			} else {
				payments.put(extorterId, new Double(0));

				extorterInfo = this.extortersInfo.get(extorterId);
				extorterInfo.setNotPaidExtortion(true);
				this.extortersInfo.put(extorterId, extorterInfo);

				numExtortionNotPaid++;
				totalExtortionNotPaid += demand.getExtortion();
			}
		}

		// Pay and inform Extorters about payments
		ExtorterAbstract extorter;
		for (Integer extorterId : this.extortions.keySet()) {
			extorter = this.extorters.get(extorterId);
			extorter.receivePaymentNoProtection(this.id, payments);
		}

		// Output
		this.output.setNumExtortionNotPaid(numExtortionNotPaid);
		this.output.setTotalExtortionNotPaid(totalExtortionNotPaid);
	}

	/**
	 * Receive the punishments
	 * 
	 * @param extorterId
	 *            Extorter identification
	 * @param punishment
	 *            Applied punishment value
	 * @return none
	 */
	public void receivePunishment(int extorterId, double punishment) {

		this.incomeCurrent -= punishment;

		ExtorterInfo extorterInfo = this.extortersInfo.get(extorterId);
		extorterInfo.setReceivedPunishment(true);
		this.extortersInfo.put(extorterId, extorterInfo);

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
	public void decideExit() {
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
		// Update Extorters Information
		ExtorterInfo extorterInfo;
		for (Integer extorterId : this.extortersInfo.keySet()) {
			extorterInfo = this.extortersInfo.get(extorterId);
			extorterInfo.updateSuccessfulProtectionProb();
			extorterInfo.updatePunishmentProb();
			this.extortersInfo.put(extorterId, extorterInfo);
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
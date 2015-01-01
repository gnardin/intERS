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
	protected int															id;
	
	// Type
	protected String													type;
	
	// Memory length
	protected int															memLength;
	
	// Unknown protection probability
	protected double													unknownProtectionProb;
	
	// Unknown punishment probability
	protected double													unknownPunishmentProb;
	
	// Number of Extorters per Target
	protected int															numExtortersPerTarget;
	
	// Minimum income variation
	protected double													minIncomeVariation;
	
	// Maximum income variation
	protected double													maxIncomeVariation;
	
	// List of Extorters <Extorter Id, ExtorterAbstract>
	protected Map<Integer, ExtorterAbstract>	extorters;
	
	// List of Extorters Information <Extorter Id, ExtorterInfo>
	protected Map<Integer, ExtorterInfo>			extortersInfo;
	
	// List of received extortions <Extorter Id, Demand>
	protected Map<Integer, Demand>						extortions;
	
	// Rank of Extorters to pay <ExtorterId, Convenience>
	protected Map<Integer, Double>						extortersRanking;
	
	// Base income
	protected double													income;
	
	// Round income
	protected double													incomeCurrent;
	
	// Percentage of the income available for paying extortion
	protected double													percAvailExtortionIncome;
	
	// Round income available for paying extortion
	protected double													incomeAvailable;
	
	// List paid Extorters
	protected List<Integer>										paidExtorters;
	
	// Accumulated wealth
	protected double													wealth;
	
	// Output data
	protected OutputTarget										output;
	
	// Output recorder
	protected OutputRecorder									outputRecorder;
	
	
	/**
	 * Constructor
	 * 
	 * @param extorters
	 *          List of Extorters
	 * @param targets
	 *          List of Targets
	 * @param id
	 *          Target identification
	 * @param targetConf
	 *          Target configuration parameters
	 * @return none
	 */
	public TargetAbstract(Map<Integer, ExtorterAbstract> extorters,
			Map<Integer, TargetAbstract> targets, Integer id, TargetConf targetConf) {
		this.id = id;
		this.type = targetConf.getType();
		this.memLength = targetConf.getMemLength();
		this.unknownProtectionProb = targetConf.getUnknownProtectionProb();
		this.unknownPunishmentProb = targetConf.getUnknownPunishmentProb();
		this.wealth = targetConf.getInitialWealth();
		this.numExtortersPerTarget = targetConf.getExtortersPerTarget();
		this.minIncomeVariation = targetConf.getMinIncomeVariation() / 100;
		this.maxIncomeVariation = targetConf.getMaxIncomeVariation() / 100;
		
		this.extorters = extorters;
		this.extortersInfo = new HashMap<Integer, ExtorterInfo>();
		this.extortions = new HashMap<Integer, Demand>();
		this.extortersRanking = new HashMap<Integer, Double>();
		this.paidExtorters = new ArrayList<Integer>();
		
		this.income = RandomHelper.nextDoubleFromTo(targetConf.getMinIncome(),
				targetConf.getMaxIncome());
		
		this.percAvailExtortionIncome = targetConf.getAvailExtortionIncome() / 100.0;
		
		// Output
		this.output = new OutputTarget(0, this.type, this.id);
		this.output.setWealth(this.wealth);
		
		this.outputRecorder = OutputRecorder.getInstance();
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
	 * Round initialization
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.0, interval = 1)
	public void beginRound() {
		this.extortions.clear();
		this.extortersRanking.clear();
		this.paidExtorters.clear();
		
		this.incomeCurrent = this.income
				* RandomHelper.nextDoubleFromTo(this.minIncomeVariation,
						this.maxIncomeVariation);
		
		this.incomeAvailable = this.incomeCurrent * this.percAvailExtortionIncome;
		
		int round = (int) RunEnvironment.getInstance().getCurrentSchedule()
				.getTickCount();
		this.output = new OutputTarget(round, this.type, this.id);
		
		this.output.setIncome(this.incomeCurrent);
	}
	
	
	/**
	 * Receive extortion demands
	 * 
	 * @param extorterId
	 *          Extorter identification
	 * @param demand
	 *          Extortion demand
	 * @return none
	 */
	public void receiveExtortionDemand(int extorterId, Demand demand) {
		this.extortions.put(extorterId, demand);
		
		if(!this.extortersInfo.containsKey(extorterId)) {
			this.extortersInfo.put(extorterId, new ExtorterInfo(this.memLength,
					this.unknownProtectionProb, this.unknownPunishmentProb));
		}
		
		// Output numExtortion and totalExtortion
		this.output.setNumExtortion(this.output.getNumExtortion() + 1);
		this.output.setTotalExtortion(this.output.getTotalExtortion()
				+ demand.getExtortion());
	}
	
	
	/**
	 * Decide which Extorters to pay extortion
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.20, interval = 1)
	public abstract void decidePaymentExtortion();
	
	
	/**
	 * Pay extortion
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.25, interval = 1)
	public void payExtortion() {
		// Output
		int numExtortionPaid = 0;
		int numExtortionNotPaid = 0;
		int numProtectionRequested = 0;
		int numProtectionExtortionPaid = 0;
		double totalExtortionPaid = 0;
		double totalExtortionNotPaid = 0;
		
		// Decide to pay extortion to those Extorters it can pay
		double extortion;
		ExtorterInfo extorterInfo;
		for(Integer extorterId : this.extortersRanking.keySet()) {
			extortion = this.extortions.get(extorterId).getExtortion();
			
			// Extorters to pay
			if(((this.numExtortersPerTarget <= 0) || (this.paidExtorters.size() < this.numExtortersPerTarget))
					&& (this.incomeAvailable >= extortion)) {
				this.paidExtorters.add(extorterId);
				
				// Update the available and current income
				this.incomeAvailable -= extortion;
				this.incomeCurrent -= extortion;
				
				numExtortionPaid++;
				totalExtortionPaid += extortion;
			} else {
				extorterInfo = this.extortersInfo.get(extorterId);
				extorterInfo.setNotPaidExtortion(true);
				this.extortersInfo.put(extorterId, extorterInfo);
				
				numExtortionNotPaid++;
				totalExtortionNotPaid += extortion;
			}
		}
		
		// Inform all
		ExtorterAbstract extorter;
		List<Integer> otherExtorters;
		for(Integer extorterId : this.extortions.keySet()) {
			otherExtorters = new ArrayList<Integer>(this.extortersRanking.keySet());
			otherExtorters.remove(extorterId);
			
			if(this.paidExtorters.contains(extorterId)) {
				extortion = this.extortions.get(extorterId).getExtortion();
				
				// Update number of paid extortion and protection request
				if(otherExtorters.size() > 0) {
					numProtectionRequested++;
					numProtectionExtortionPaid++;
					
					extorterInfo = this.extortersInfo.get(extorterId);
					extorterInfo.setNumRequestedProtectionAgainst(otherExtorters.size());
					this.extortersInfo.put(extorterId, extorterInfo);
				}
			} else {
				extortion = 0;
			}
			
			extorter = this.extorters.get(extorterId);
			extorter.receivePaymentExtortion(this.id, extortion, otherExtorters);
		}
		
		// Output
		if(!this.extortions.isEmpty()) {
			this.output.setExtorted(true);
			this.output.setIncomeExtorted(this.incomeCurrent + totalExtortionPaid);
		}
		this.output.setNumPaymentProtection(numProtectionExtortionPaid);
		this.output.setNumProtectionRequested(numProtectionRequested);
		this.output.setNumExtortionPaid(numExtortionPaid);
		this.output.setTotalExtortionPaid(totalExtortionPaid);
		this.output.setNumExtortionNotPaid(numExtortionNotPaid);
		this.output.setTotalExtortionNotPaid(totalExtortionNotPaid);
	}
	
	
	/**
	 * Receive punishments
	 * 
	 * @param extorterId
	 *          Extorter identification
	 * @param punishment
	 *          Applied punishment value
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
	 * Receive the information about renounce
	 * 
	 * @param extorterId
	 *          Extorter identification
	 * @return none
	 */
	public void receiveInformRenounce() {
		ExtorterInfo extorterInfo;
		for(Integer extorterId : this.paidExtorters) {
			extorterInfo = this.extortersInfo.get(extorterId);
			extorterInfo.addNumReceivedProtectionAgainst();
			this.extortersInfo.put(extorterId, extorterInfo);
		}
	}
	
	
	/**
	 * Decide to exit
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.96, interval = 1)
	public void decideExit() {
		this.wealth += this.incomeCurrent;
		
		if(this.wealth <= 0) {
			this.endRound();
			this.die();
		}
	}
	
	
	/**
	 * Round end
	 * 
	 * @param none
	 * @return none
	 */
	@ScheduledMethod(start = 1.97, interval = 1)
	public void endRound() {
		// Update Extorters Information
		ExtorterInfo extorterInfo;
		for(Integer extorterId : this.extortersInfo.keySet()) {
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
		if(agent.size() > 1) {
			agent.remove(this);
		}
	}
}
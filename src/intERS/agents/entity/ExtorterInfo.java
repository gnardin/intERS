package intERS.agents.entity;

import java.util.LinkedList;
import java.util.Queue;

public class ExtorterInfo{
	
	// Constants
	private static final Integer	UNLIMITED	= -1;
	
	// Variables
	private int										memLength;
	
	private int										numRequestedProtectionAgainst;
	
	private int										numReceivedProtectionAgainst;
	
	private Queue<Double>					successfulProtection;
	
	private boolean								notPaidExtortion;
	
	private boolean								receivedPunishment;
	
	private Queue<Integer>				punishments;
	
	private double								unknownProtectionProb;
	
	private double								unknownPunishmentProb;
	
	
	public ExtorterInfo(int memLength, double unknownProtectionProb,
			double unknownPunishmentProb){
		this.memLength = memLength;
		this.unknownProtectionProb = unknownProtectionProb;
		this.unknownPunishmentProb = unknownPunishmentProb;
		
		this.numRequestedProtectionAgainst = 0;
		this.numReceivedProtectionAgainst = 0;
		this.successfulProtection = new LinkedList<Double>();
		
		this.notPaidExtortion = false;
		this.receivedPunishment = false;
		this.punishments = new LinkedList<Integer>();
	}
	
	
	public void addNumRequestedProtectionAgainst(){
		this.numRequestedProtectionAgainst++;
	}
	
	
	public void setNumRequestedProtectionAgainst(int numRequestedProtectionAgainst){
		this.numRequestedProtectionAgainst = numRequestedProtectionAgainst;
	}
	
	
	public void addNumReceivedProtectionAgainst(){
		this.numReceivedProtectionAgainst++;
	}
	
	
	public void setNumReceivedProtectionAgainst(int numReceivedProtectionAgainst){
		this.numReceivedProtectionAgainst = numReceivedProtectionAgainst;
	}
	
	
	public void updateSuccessfulProtectionProb(){
		double successfulProtectionProb;
		
		if((this.numRequestedProtectionAgainst > 0)
				&& ((this.memLength > 0) || (this.memLength == UNLIMITED))){
			if((this.memLength > 0)
					&& (this.successfulProtection.size() >= this.memLength)){
				this.successfulProtection.poll();
			}
			
			successfulProtectionProb = (double) numReceivedProtectionAgainst
					/ (double) this.numRequestedProtectionAgainst;
			
			this.successfulProtection.add(successfulProtectionProb);
		}
		
		this.numRequestedProtectionAgainst = 0;
		this.numReceivedProtectionAgainst = 0;
	}
	
	
	/**
	 * Extorter current successful protection probability
	 * 
	 * @param none
	 * @return Current successful protection probability
	 */
	public double getSuccessfulProtectionProb(){
		double successfulProtectionProb;
		
		if(this.successfulProtection.size() > 0){
			successfulProtectionProb = 0;
			for(Double prob : this.successfulProtection){
				successfulProtectionProb += prob;
			}
			
			successfulProtectionProb = (double) successfulProtectionProb
					/ (double) this.successfulProtection.size();
		}else{
			successfulProtectionProb = this.unknownProtectionProb;
		}
		
		return successfulProtectionProb;
	}
	
	
	/**
	 * Extorter has successful protection probability
	 * 
	 * @param none
	 * @return True if has successful protection probability, false otherwise
	 */
	public boolean hasSuccessfulProtectionProb(){
		if(this.successfulProtection.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	
	public void setNotPaidExtortion(boolean notPaidExtortion){
		this.notPaidExtortion = notPaidExtortion;
	}
	
	
	public void setReceivedPunishment(boolean receivedPunishment){
		this.receivedPunishment = receivedPunishment;
	}
	
	
	/**
	 * Updates the successful punishment probability
	 * 
	 * @param none
	 * @return none
	 */
	public void updatePunishmentProb(){
		if((this.notPaidExtortion)
				&& ((this.memLength > 0) || (this.memLength == UNLIMITED))){
			if((this.memLength > 0) && (this.punishments.size() >= this.memLength)){
				this.punishments.poll();
			}
			
			if(this.receivedPunishment){
				this.punishments.add(new Integer(1));
			}else{
				this.punishments.add(new Integer(0));
			}
		}
		
		this.notPaidExtortion = false;
		this.receivedPunishment = false;
	}
	
	
	/**
	 * Extorter current punishment probability
	 * 
	 * @param none
	 * @return Current punishment probability
	 */
	public double getPunishmentProb(){
		double punishmentProb;
		
		if(this.punishments.size() > 0){
			punishmentProb = 0;
			for(Integer prob : this.punishments){
				punishmentProb += prob;
			}
			
			punishmentProb = (double) punishmentProb
					/ (double) this.punishments.size();
		}else{
			punishmentProb = this.unknownPunishmentProb;
		}
		
		return punishmentProb;
	}
	
	
	/**
	 * Extorter has punishment probability
	 * 
	 * @param none
	 * @return True if has punishment probability, false otherwise
	 */
	public boolean hasPunishmentProb(){
		if(this.punishments.size() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	
	public Queue<Double> getSuccessfulProtection(){
		return this.successfulProtection;
	}
	
	
	public Queue<Integer> getPunishments(){
		return this.punishments;
	}
}
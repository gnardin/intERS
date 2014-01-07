package intERS.agents;

import java.util.LinkedList;
import java.util.Queue;

public class ExtorterInfo {
	protected int memLength;

	private int numRequestedProtectionAgainst;
	private int numReceivedProtectionAgainst;
	private Queue<Double> successfulProtection;

	private boolean notPaidExtortion;
	private boolean receivedPunishment;
	private Queue<Integer> punishments;

	public ExtorterInfo(int memLength) {
		this.memLength = memLength;

		this.numRequestedProtectionAgainst = 0;
		this.numReceivedProtectionAgainst = 0;
		this.successfulProtection = new LinkedList<Double>();

		this.notPaidExtortion = false;
		this.receivedPunishment = false;
		this.punishments = new LinkedList<Integer>();
	}

	public void setNumRequestedProtectionAgainst(
			int numRequestedProtectionAgainst) {
		this.numRequestedProtectionAgainst = numRequestedProtectionAgainst;
	}

	public void setNumReceivedProtectionAgainst(int numReceivedProtectionAgainst) {
		this.numReceivedProtectionAgainst = numReceivedProtectionAgainst;
	}

	/**
	 * Updates the successful protection probability
	 * 
	 * @param none
	 * @return none
	 */
	public void updateSuccessfulProtectionProb() {
		double successfulProtectionProb = 0;

		if (((this.numRequestedProtectionAgainst > 0)) && (this.memLength >= 0)) {
			if ((this.memLength > 0)
					&& (this.successfulProtection.size() >= this.memLength)) {
				this.successfulProtection.poll();
			}

			successfulProtectionProb = (double) numReceivedProtectionAgainst
					/ (double) this.numRequestedProtectionAgainst;

			this.successfulProtection.offer(successfulProtectionProb);
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
	public double getSuccessfulProtectionProb() {
		double successfulProtectionProb;

		if (this.successfulProtection.size() > 0) {
			successfulProtectionProb = 0;
			for (Double prob : this.successfulProtection) {
				successfulProtectionProb += prob;
			}

			successfulProtectionProb = successfulProtectionProb
					/ (double) this.successfulProtection.size();
		} else {
			successfulProtectionProb = 0.5;
		}

		return successfulProtectionProb;
	}

	public void setNotPaidExtortion(boolean notPaidExtortion) {
		this.notPaidExtortion = notPaidExtortion;
	}

	public void setReceivedPunishment(boolean receivedPunishment) {
		this.receivedPunishment = receivedPunishment;
	}

	/**
	 * Updates the successful protection probability
	 * 
	 * @param none
	 * @return none
	 */
	public void updatePunishmentProb() {
		if ((this.notPaidExtortion) && (this.memLength >= 0)) {
			if ((this.memLength > 0)
					&& (this.punishments.size() >= this.memLength)) {
				this.punishments.poll();
			}

			if (this.receivedPunishment) {
				this.punishments.offer(1);
			} else {
				this.punishments.offer(0);
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
	public double getPunishmentProb() {
		double punishmentProb;

		if (this.punishments.size() > 0) {
			punishmentProb = 0;
			for (Integer prob : this.punishments) {
				punishmentProb += prob;
			}

			punishmentProb = punishmentProb / (double) this.punishments.size();
		} else {
			punishmentProb = 0.5;
		}

		return punishmentProb;
	}

	/**
	 * Reset
	 * 
	 * @param none
	 * @return none
	 */
	public void reset() {
		this.numRequestedProtectionAgainst = 0;
		this.numReceivedProtectionAgainst = 0;
		this.successfulProtection.clear();

		this.notPaidExtortion = false;
		this.receivedPunishment = false;
		this.punishments.clear();
	}

	public Queue<Double> getExtortions() {
		return this.successfulProtection;
	}

	public Queue<Integer> getPunishments() {
		return this.punishments;
	}
}
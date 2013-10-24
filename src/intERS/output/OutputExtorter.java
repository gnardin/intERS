package intERS.output;

public class OutputExtorter extends OutputAbstract {

	private double wealth;
	private int numTargets;
	private int numExtortion;
	private double totalExtortion;
	private int numProtectionRequested;
	private int numProtectionProvided;
	private int numAttackProtection;
	private int numNonAttackProtection;
	private int numReceivedAttackProtection;
	private int numCounterattackProtection;
	private int numReceivedCounterattackProtection;
	private int numRunawayProtection;
	private int numAttackRetaliation;
	private int numNonAttackRetaliation;
	private int numReceivedAttackRetaliation;
	private int numCounterattackRetaliation;
	private int numReceivedCounterattackRetaliation;
	private int numRunawayRetaliation;
	private double totalLostFightProtection;
	private double totalLostFightRetaliation;
	private double totalLostPunishment;
	private int numExtortionReceived;
	private double totalExtortionReceived;
	private int numPunishment;

	public OutputExtorter(int cycle, int id, String type) {
		super(AgentType.EXTORTER, cycle, id, type);

		this.wealth = 0;
		this.numTargets = 0;
		this.numExtortion = 0;
		this.totalExtortion = 0;
		this.numProtectionRequested = 0;
		this.numProtectionProvided = 0;
		this.numAttackProtection = 0;
		this.numNonAttackProtection = 0;
		this.numReceivedAttackProtection = 0;
		this.numCounterattackProtection = 0;
		this.numReceivedCounterattackProtection = 0;
		this.numRunawayProtection = 0;
		this.numAttackRetaliation = 0;
		this.numNonAttackRetaliation = 0;
		this.numReceivedAttackRetaliation = 0;
		this.numCounterattackRetaliation = 0;
		this.numReceivedCounterattackRetaliation = 0;
		this.numRunawayRetaliation = 0;
		this.totalLostFightProtection = 0;
		this.totalLostFightRetaliation = 0;
		this.totalLostPunishment = 0;
		this.numExtortionReceived = 0;
		this.totalExtortionReceived = 0;
		this.numPunishment = 0;
	}

	@Override
	public String getHeader(String fs) {
		String str = new String();

		str += "type" + fs + "id" + fs + "wealth" + fs + "numTargets" + fs
				+ "numExtortion" + fs + "totalExtortion" + fs
				+ "numProtectionRequested" + fs + "numProtectionProvided" + fs
				+ "numAttackProtection" + fs + "numNonAttackProtection" + fs
				+ "numReceivedAttackProtection" + fs
				+ "numCounterattackProtection" + fs
				+ "numReceivedCounterattackProtection" + fs
				+ "numRunawayProtection" + fs + "numAttackRetaliation" + fs
				+ "numNonAttackRetaliation" + fs
				+ "numReceivedAttackRetaliation" + fs
				+ "numCounterattackRetaliation" + fs
				+ "numReceivedCounterattackRetaliation" + fs
				+ "numRunawayRetaliation" + fs + "totalLostFightProtection"
				+ fs + "totalLostFightRetaliation" + fs
				+ "numExtortionReceived" + fs + "totalExtortionReceived" + fs
				+ "numPunishment" + fs + "totalLostPunishment";

		return str;
	}

	@Override
	public String getLine(String fs) {
		String str = new String();

		str += this.type + fs + this.id + fs + this.wealth + fs
				+ this.numTargets + fs + this.numExtortion + fs
				+ this.totalExtortion + fs + this.numProtectionRequested + fs
				+ this.numProtectionProvided + fs + this.numAttackProtection
				+ fs + this.numNonAttackProtection + fs
				+ this.numReceivedAttackProtection + fs
				+ this.numCounterattackProtection + fs
				+ this.numReceivedCounterattackProtection + fs
				+ this.numRunawayProtection + fs + this.numAttackRetaliation
				+ fs + this.numNonAttackRetaliation + fs
				+ this.numReceivedAttackRetaliation + fs
				+ this.numCounterattackRetaliation + fs
				+ this.numReceivedCounterattackRetaliation + fs
				+ this.numRunawayRetaliation + fs
				+ this.totalLostFightProtection + fs
				+ this.totalLostFightRetaliation + fs
				+ this.numExtortionReceived + fs + this.totalExtortionReceived
				+ fs + this.numPunishment + fs + this.totalLostPunishment;

		return str;
	}

	public double getWealth() {
		return wealth;
	}

	public void setWealth(double wealth) {
		this.wealth = wealth;
	}

	public int getNumTargets() {
		return numTargets;
	}

	public void setNumTargets(int numTargets) {
		this.numTargets = numTargets;
	}

	public int getNumExtortion() {
		return numExtortion;
	}

	public void setNumExtortion(int numExtortion) {
		this.numExtortion = numExtortion;
	}

	public double getTotalExtortion() {
		return totalExtortion;
	}

	public void setTotalExtortion(double totalExtortion) {
		this.totalExtortion = totalExtortion;
	}

	public int getNumProtectionRequested() {
		return numProtectionRequested;
	}

	public void setNumProtectionRequested(int numHelpRequested) {
		this.numProtectionRequested = numHelpRequested;
	}

	public int getNumProtectionProvided() {
		return numProtectionProvided;
	}

	public void setNumProtectionProvided(int numProtectionProvided) {
		this.numProtectionProvided = numProtectionProvided;
	}

	public int getNumAttackProtection() {
		return numAttackProtection;
	}

	public void setNumAttackProtection(int numAttackProtection) {
		this.numAttackProtection = numAttackProtection;
	}

	public int getNumNonAttackProtection() {
		return numNonAttackProtection;
	}

	public void setNumNonAttackProtection(int numNonAttackProtection) {
		this.numNonAttackProtection = numNonAttackProtection;
	}

	public int getNumReceivedAttackProtection() {
		return numReceivedAttackProtection;
	}

	public void setNumReceivedAttackProtection(int numReceivedAttackProtection) {
		this.numReceivedAttackProtection = numReceivedAttackProtection;
	}

	public int getNumCounterattackProtection() {
		return numCounterattackProtection;
	}

	public void setNumCounterattackProtection(int numCounterattackProtection) {
		this.numCounterattackProtection = numCounterattackProtection;
	}

	public int getNumReceivedCounterattackProtection() {
		return numReceivedCounterattackProtection;
	}

	public void setNumReceivedCounterattackProtection(
			int numReceivedCounterattackProtection) {
		this.numReceivedCounterattackProtection = numReceivedCounterattackProtection;
	}

	public int getNumRunawayProtection() {
		return this.numRunawayProtection;
	}

	public void setNumRunawayProtection(int numRunawayProtection) {
		this.numRunawayProtection = numRunawayProtection;
	}

	public int getNumAttackRetaliation() {
		return numAttackRetaliation;
	}

	public void setNumAttackRetaliation(int numAttackRetaliation) {
		this.numAttackRetaliation = numAttackRetaliation;
	}

	public int getNumNonAttackRetaliation() {
		return numNonAttackRetaliation;
	}

	public void setNumNonAttackRetaliation(int numNonAttackRetaliation) {
		this.numNonAttackRetaliation = numNonAttackRetaliation;
	}

	public int getNumReceivedAttackRetaliation() {
		return numReceivedAttackRetaliation;
	}

	public void setNumReceivedAttackRetaliation(int numReceivedAttackRetaliation) {
		this.numReceivedAttackRetaliation = numReceivedAttackRetaliation;
	}

	public int getNumCounterattackRetaliation() {
		return numCounterattackRetaliation;
	}

	public void setNumCounterattackRetaliation(int numCounterattackRetaliation) {
		this.numCounterattackRetaliation = numCounterattackRetaliation;
	}

	public int getNumReceivedCounterattackRetaliation() {
		return numReceivedCounterattackRetaliation;
	}

	public void setNumReceivedCounterattackRetaliation(
			int numReceivedCounterattackRetaliation) {
		this.numReceivedCounterattackRetaliation = numReceivedCounterattackRetaliation;
	}

	public int getNumRunawayRetaliation() {
		return this.numRunawayRetaliation;
	}

	public void setNumRunawayRetaliation(int numRunawayRetaliation) {
		this.numRunawayRetaliation = numRunawayRetaliation;
	}

	public double getTotalLostFightProtection() {
		return totalLostFightProtection;
	}

	public void setTotalLostFightProtection(double totalLostFightProtection) {
		this.totalLostFightProtection = totalLostFightProtection;
	}

	public double getTotalLostFightRetaliation() {
		return totalLostFightRetaliation;
	}

	public void setTotalLostFightRetaliation(double totalLostFightRetaliation) {
		this.totalLostFightRetaliation = totalLostFightRetaliation;
	}

	public double getTotalLostPunishment() {
		return totalLostPunishment;
	}

	public void setTotalLostPunishment(double totalLostPunishment) {
		this.totalLostPunishment = totalLostPunishment;
	}

	public int getNumExtortionReceived() {
		return numExtortionReceived;
	}

	public void setNumExtortionReceived(int numExtortionReceived) {
		this.numExtortionReceived = numExtortionReceived;
	}

	public double getTotalExtortionReceived() {
		return totalExtortionReceived;
	}

	public void setTotalExtortionReceived(double totalExtortionReceived) {
		this.totalExtortionReceived = totalExtortionReceived;
	}

	public int getNumPunishment() {
		return numPunishment;
	}

	public void setNumPunishment(int numPunishment) {
		this.numPunishment = numPunishment;
	}
}
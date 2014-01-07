package intERS.output;

public class OutputExtorter extends OutputAbstract {

	private double extortion;
	private double punishment;
	private double wealth;
	private int numTargets;
	private int numExtortionDemanded;
	private double totalExtortionDemanded;
	private int numPaymentProtection;
	private int numProtectionRequested;
	private int numProtectionProvided;
	private int numSuccessfulProtection;
	private int numAttackProtection;
	private int numNonAttackProtection;
	private int numAttackProtectionReceived;
	private int numCounterattackProtection;
	private int numCounterattackProtectionReceived;
	private int numEscapeProtection;
	private double totalLostFightProtection;
	private int numAttackRetaliation;
	private int numNonAttackRetaliation;
	private int numAttackRetaliationReceived;
	private int numCounterattackRetaliation;
	private int numCounterattackRetaliationReceived;
	private int numEscapeRetaliation;
	private double totalLostFightRetaliation;
	private int numExtortionReceived;
	private double totalExtortionReceived;
	private int numPunishment;
	private double totalLostPunishment;

	public OutputExtorter(int cycle, int id, String type, double extortion,
			double punishment) {
		super(AgentType.EXTORTER, cycle, id, type);

		this.extortion = extortion;
		this.punishment = punishment;
		this.wealth = 0;
		this.numTargets = 0;
		this.numExtortionDemanded = 0;
		this.totalExtortionDemanded = 0;
		this.numPaymentProtection = 0;
		this.numProtectionRequested = 0;
		this.numProtectionProvided = 0;
		this.numSuccessfulProtection = 0;
		this.numAttackProtection = 0;
		this.numNonAttackProtection = 0;
		this.numAttackProtectionReceived = 0;
		this.numCounterattackProtection = 0;
		this.numCounterattackProtectionReceived = 0;
		this.numEscapeProtection = 0;
		this.totalLostFightProtection = 0;
		this.numAttackRetaliation = 0;
		this.numNonAttackRetaliation = 0;
		this.numAttackRetaliationReceived = 0;
		this.numCounterattackRetaliation = 0;
		this.numCounterattackRetaliationReceived = 0;
		this.numEscapeRetaliation = 0;
		this.totalLostFightRetaliation = 0;
		this.numExtortionReceived = 0;
		this.totalExtortionReceived = 0;
		this.numPunishment = 0;
		this.totalLostPunishment = 0;
	}

	@Override
	public String getHeader(String fs) {
		String str = new String();

		str += "type" + fs + "id" + fs + "extortion" + fs + "punishment" + fs
				+ "wealth" + fs + "numTargets" + fs + "numExtortionDemanded"
				+ fs + "totalExtortionDemanded" + fs + "numPaymentProtection"
				+ fs + "numProtectionRequested" + fs + "numProtectionProvided"
				+ fs + "numSuccessfulProtection" + fs + "numAttackProtection"
				+ fs + "numNonAttackProtection" + fs
				+ "numAttackProtectionReceived" + fs
				+ "numCounterattackProtection" + fs
				+ "numCounterattackProtectionReceived" + fs
				+ "numEscapeProtection" + fs + "totalLostFightProtection" + fs
				+ "numAttackRetaliation" + fs + "numNonAttackRetaliation" + fs
				+ "numAttackRetaliationReceived" + fs
				+ "numCounterattackRetaliation" + fs
				+ "numCounterattackRetaliationReceived" + fs
				+ "numEscapeRetaliation" + fs + "totalLostFightRetaliation"
				+ fs + "numExtortionReceived" + fs + "totalExtortionReceived"
				+ fs + "numPunishment" + fs + "totalLostPunishment";

		return str;
	}

	@Override
	public String getLine(String fs) {
		String str = new String();

		str += this.type + fs + this.id + fs + this.extortion + fs
				+ this.punishment + fs + this.wealth + fs + this.numTargets
				+ fs + this.numExtortionDemanded + fs
				+ this.totalExtortionDemanded + fs + this.numPaymentProtection
				+ fs + this.numProtectionRequested + fs
				+ this.numProtectionProvided + fs
				+ this.numSuccessfulProtection + fs + this.numAttackProtection
				+ fs + this.numNonAttackProtection + fs
				+ this.numAttackProtectionReceived + fs
				+ this.numCounterattackProtection + fs
				+ this.numCounterattackProtectionReceived + fs
				+ this.numEscapeProtection + fs + this.totalLostFightProtection
				+ fs + this.numAttackRetaliation + fs
				+ this.numNonAttackRetaliation + fs
				+ this.numAttackRetaliationReceived + fs
				+ this.numCounterattackRetaliation + fs
				+ this.numCounterattackRetaliationReceived + fs
				+ this.numEscapeRetaliation + fs
				+ this.totalLostFightRetaliation + fs
				+ this.numExtortionReceived + fs + this.totalExtortionReceived
				+ fs + this.numPunishment + fs + this.totalLostPunishment;

		return str;
	}

	public double getExtortion() {
		return this.extortion;
	}

	public double getPunishment() {
		return this.punishment;
	}

	public double getWealth() {
		return this.wealth;
	}

	public void setWealth(double wealth) {
		this.wealth = wealth;
	}

	public int getNumTargets() {
		return this.numTargets;
	}

	public void setNumTargets(int numTargets) {
		this.numTargets = numTargets;
	}

	public int getNumExtortionDemanded() {
		return this.numExtortionDemanded;
	}

	public void setNumExtortionDemanded(int numExtortionDemanded) {
		this.numExtortionDemanded = numExtortionDemanded;
	}

	public double getTotalExtortionDemanded() {
		return this.totalExtortionDemanded;
	}

	public void setTotalExtortionDemanded(double totalExtortionDemanded) {
		this.totalExtortionDemanded = totalExtortionDemanded;
	}

	public int getNumPaymentProtection() {
		return this.numPaymentProtection;
	}

	public void setNumPaymentProtection(int numHelpRequested) {
		this.numPaymentProtection = numHelpRequested;
	}

	public int getNumProtectionRequested() {
		return this.numProtectionRequested;
	}

	public void setNumProtectionRequested(int numProtectionRequested) {
		this.numProtectionRequested = numProtectionRequested;
	}

	public int getNumProtectionProvided() {
		return this.numProtectionProvided;
	}

	public void setNumProtectionProvided(int numProtectionProvided) {
		this.numProtectionProvided = numProtectionProvided;
	}

	public int getNumSuccessfulProtectionProvided() {
		return this.numSuccessfulProtection;
	}

	public void setNumSuccessfulProtectionProvided(
			int numSuccessfulProtectionProvided) {
		this.numSuccessfulProtection = numSuccessfulProtectionProvided;
	}

	public int getNumAttackProtection() {
		return this.numAttackProtection;
	}

	public void setNumAttackProtection(int numAttackProtection) {
		this.numAttackProtection = numAttackProtection;
	}

	public int getNumNonAttackProtection() {
		return this.numNonAttackProtection;
	}

	public void setNumNonAttackProtection(int numNonAttackProtection) {
		this.numNonAttackProtection = numNonAttackProtection;
	}

	public int getNumAttackProtectionReceived() {
		return this.numAttackProtectionReceived;
	}

	public void setNumAttackProtectionReceived(int numAttackProtectionReceived) {
		this.numAttackProtectionReceived = numAttackProtectionReceived;
	}

	public int getNumCounterattackProtection() {
		return this.numCounterattackProtection;
	}

	public void setNumCounterattackProtection(int numCounterattackProtection) {
		this.numCounterattackProtection = numCounterattackProtection;
	}

	public int getNumCounterattackProtectionReceived() {
		return this.numCounterattackProtectionReceived;
	}

	public void setNumCounterattackProtectionReceived(
			int numCounterattackProtectionReceived) {
		this.numCounterattackProtectionReceived = numCounterattackProtectionReceived;
	}

	public int getNumEscapeProtection() {
		return this.numEscapeProtection;
	}

	public void setNumEscapeProtection(int numEscapeProtection) {
		this.numEscapeProtection = numEscapeProtection;
	}

	public double getTotalLostFightProtection() {
		return this.totalLostFightProtection;
	}

	public void setTotalLostFightProtection(double totalLostFightProtection) {
		this.totalLostFightProtection = totalLostFightProtection;
	}

	public int getNumAttackRetaliation() {
		return this.numAttackRetaliation;
	}

	public void setNumAttackRetaliation(int numAttackRetaliation) {
		this.numAttackRetaliation = numAttackRetaliation;
	}

	public int getNumNonAttackRetaliation() {
		return this.numNonAttackRetaliation;
	}

	public void setNumNonAttackRetaliation(int numNonAttackRetaliation) {
		this.numNonAttackRetaliation = numNonAttackRetaliation;
	}

	public int getNumAttackRetaliationReceived() {
		return this.numAttackRetaliationReceived;
	}

	public void setNumAttackRetaliationReceived(int numAttackRetaliationReceived) {
		this.numAttackRetaliationReceived = numAttackRetaliationReceived;
	}

	public int getNumCounterattackRetaliation() {
		return this.numCounterattackRetaliation;
	}

	public void setNumCounterattackRetaliation(int numCounterattackRetaliation) {
		this.numCounterattackRetaliation = numCounterattackRetaliation;
	}

	public int getNumCounterattackRetaliationReceived() {
		return this.numCounterattackRetaliationReceived;
	}

	public void setNumCounterattackRetaliationReceived(
			int numCounterattackRetaliationReceived) {
		this.numCounterattackRetaliationReceived = numCounterattackRetaliationReceived;
	}

	public int getNumEscapeRetaliation() {
		return this.numEscapeRetaliation;
	}

	public void setNumEscapeRetaliation(int numEscapeRetaliation) {
		this.numEscapeRetaliation = numEscapeRetaliation;
	}

	public double getTotalLostFightRetaliation() {
		return this.totalLostFightRetaliation;
	}

	public void setTotalLostFightRetaliation(double totalLostFightRetaliation) {
		this.totalLostFightRetaliation = totalLostFightRetaliation;
	}

	public int getNumExtortionReceived() {
		return this.numExtortionReceived;
	}

	public void setNumExtortionReceived(int numExtortionReceived) {
		this.numExtortionReceived = numExtortionReceived;
	}

	public double getTotalExtortionReceived() {
		return this.totalExtortionReceived;
	}

	public void setTotalExtortionReceived(double totalExtortionReceived) {
		this.totalExtortionReceived = totalExtortionReceived;
	}

	public int getNumPunishment() {
		return this.numPunishment;
	}

	public void setNumPunishment(int numPunishment) {
		this.numPunishment = numPunishment;
	}

	public double getTotalLostPunishment() {
		return this.totalLostPunishment;
	}

	public void setTotalLostPunishment(double totalLostPunishment) {
		this.totalLostPunishment = totalLostPunishment;
	}
}
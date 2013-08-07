package intERS.output;

public class OutputExtorter extends OutputAbstract {

	private double wealth;
	private int numTargets;
	private int numExtortion;
	private double totalExtortion;
	private int numHelpRequested;
	private int numHelpProvided;
	private int numRetaliation;
	private int numReceivedRetaliation;
	private int numCounterattack;
	private int numReceivedCounterattack;
	private double totalLostFight;
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
		this.numHelpRequested = 0;
		this.numHelpProvided = 0;
		this.numRetaliation = 0;
		this.numReceivedRetaliation = 0;
		this.numCounterattack = 0;
		this.numReceivedCounterattack = 0;
		this.totalLostFight = 0;
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
				+ "numHelpRequested" + fs + "numHelpProvided" + fs
				+ "numRetaliation" + fs + "numReceivedRetaliation" + fs
				+ "numCounterattack" + fs + "numReceivedCounterattack" + fs
				+ "totalLostFight" + fs + "numExtortionReceived" + fs
				+ "totalExtortionReceived" + fs + "numPunishment" + fs
				+ "totalLostPunishment";

		return str;
	}

	@Override
	public String getLine(String fs) {
		String str = new String();

		str += this.type + fs + this.id + fs + this.wealth + fs
				+ this.numTargets + fs + this.numExtortion + fs
				+ this.totalExtortion + fs + this.numHelpRequested + fs
				+ this.numHelpProvided + fs + this.numRetaliation + fs
				+ this.numReceivedRetaliation + fs + this.numCounterattack + fs
				+ this.numReceivedCounterattack + fs + this.totalLostFight + fs
				+ this.numExtortionReceived + fs + this.totalExtortionReceived
				+ fs + this.numPunishment + fs + this.totalLostPunishment;

		return str;
	}

	public synchronized double getWealth() {
		return wealth;
	}

	public synchronized void setWealth(double wealth) {
		this.wealth = wealth;
	}

	public synchronized int getNumTargets() {
		return numTargets;
	}

	public synchronized void setNumTargets(int numTargets) {
		this.numTargets = numTargets;
	}

	public synchronized int getNumExtortion() {
		return numExtortion;
	}

	public synchronized void setNumExtortion(int numExtortion) {
		this.numExtortion = numExtortion;
	}

	public synchronized double getTotalExtortion() {
		return totalExtortion;
	}

	public synchronized void setTotalExtortion(double totalExtortion) {
		this.totalExtortion = totalExtortion;
	}

	public synchronized int getNumHelpRequested() {
		return numHelpRequested;
	}

	public synchronized void setNumHelpRequested(int numHelpRequested) {
		this.numHelpRequested = numHelpRequested;
	}

	public synchronized int getNumHelpProvided() {
		return numHelpProvided;
	}

	public synchronized void setNumHelpProvided(int numHelpProvided) {
		this.numHelpProvided = numHelpProvided;
	}

	public synchronized int getNumRetaliation() {
		return numRetaliation;
	}

	public synchronized void setNumRetaliation(int numRetaliation) {
		this.numRetaliation = numRetaliation;
	}

	public synchronized int getNumReceivedRetaliation() {
		return numReceivedRetaliation;
	}

	public synchronized void setNumReceivedRetaliation(
			int numReceivedRetaliation) {
		this.numReceivedRetaliation = numReceivedRetaliation;
	}

	public synchronized int getNumCounterattack() {
		return numCounterattack;
	}

	public synchronized void setNumCounterattack(int numCounterattack) {
		this.numCounterattack = numCounterattack;
	}

	public synchronized int getNumReceivedCounterattack() {
		return numReceivedCounterattack;
	}

	public synchronized void setNumReceivedCounterattack(
			int numReceivedCounterattack) {
		this.numReceivedCounterattack = numReceivedCounterattack;
	}

	public synchronized double getTotalLostFight() {
		return totalLostFight;
	}

	public synchronized void setTotalLostFight(double totalLostFight) {
		this.totalLostFight = totalLostFight;
	}

	public synchronized double getTotalLostPunishment() {
		return totalLostPunishment;
	}

	public synchronized void setTotalLostPunishment(double totalLostPunishment) {
		this.totalLostPunishment = totalLostPunishment;
	}

	public synchronized int getNumExtortionReceived() {
		return numExtortionReceived;
	}

	public synchronized void setNumExtortionReceived(int numExtortionReceived) {
		this.numExtortionReceived = numExtortionReceived;
	}

	public synchronized double getTotalExtortionReceived() {
		return totalExtortionReceived;
	}

	public synchronized void setTotalExtortionReceived(
			double totalExtortionReceived) {
		this.totalExtortionReceived = totalExtortionReceived;
	}

	public synchronized int getNumPunishment() {
		return numPunishment;
	}

	public synchronized void setNumPunishment(int numPunishment) {
		this.numPunishment = numPunishment;
	}
}
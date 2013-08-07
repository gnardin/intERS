package intERS.output;

public class OutputTarget extends OutputAbstract {

	private double wealth;
	private double income;
	private int numExtortion;
	private double totalExtortion;
	private int numHelpRequested;
	private int numHelpReceived;
	private int numPaid;
	private double totalPaid;
	private int numNotPaid;
	private double totalNotPaid;
	private int numPunishment;
	private double totalPunishment;

	public OutputTarget(int cycle, int id, String type) {
		super(AgentType.TARGET, cycle, id, type);

		this.wealth = 0;
		this.income = 0;
		this.numExtortion = 0;
		this.totalExtortion = 0;
		this.numHelpRequested = 0;
		this.numHelpReceived = 0;
		this.numPaid = 0;
		this.totalPaid = 0;
		this.numNotPaid = 0;
		this.totalNotPaid = 0;
		this.numPunishment = 0;
		this.totalPunishment = 0;
	}

	@Override
	public String getHeader(String fs) {
		String str = new String();

		str += "type" + fs + "id" + fs + "wealth" + fs + "income" + fs
				+ "numExtortion" + fs + "totalExtortion" + fs
				+ "numHelpRequested" + fs + "numHelpReceived" + fs + "numPaid"
				+ fs + "totalPaid" + fs + "numNotPaid" + fs + "totalNotPaid"
				+ fs + "numPunishment" + fs + "totalPunishment";

		return str;
	}

	@Override
	public String getLine(String fs) {
		String str = new String();

		str += this.type + fs + this.id + fs + this.wealth + fs + this.income
				+ fs + this.numExtortion + fs + this.totalExtortion + fs
				+ this.numHelpRequested + fs + this.numHelpReceived + fs
				+ this.numPaid + fs + this.totalPaid + fs + this.numNotPaid
				+ fs + this.totalNotPaid + fs + this.numPunishment + fs
				+ this.totalPunishment;

		return str;
	}

	public synchronized double getWealth() {
		return wealth;
	}

	public synchronized void setWealth(double wealth) {
		this.wealth = wealth;
	}

	public synchronized double getIncome() {
		return income;
	}

	public synchronized void setIncome(double income) {
		this.income = income;
	}

	public synchronized int getNumPunishment() {
		return numPunishment;
	}

	public synchronized void setNumPunishment(int numPunishment) {
		this.numPunishment = numPunishment;
	}

	public synchronized double getTotalPunishment() {
		return totalPunishment;
	}

	public synchronized void setTotalPunishment(double totalPunishment) {
		this.totalPunishment = totalPunishment;
	}

	public synchronized int getNumHelpRequested() {
		return numHelpRequested;
	}

	public synchronized void setNumHelpRequested(int numHelpRequested) {
		this.numHelpRequested = numHelpRequested;
	}

	public synchronized int getNumHelpReceived() {
		return numHelpReceived;
	}

	public synchronized void setNumHelpReceived(int numHelpReceived) {
		this.numHelpReceived = numHelpReceived;
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

	public synchronized int getNumPaid() {
		return numPaid;
	}

	public synchronized void setNumPaid(int numPaid) {
		this.numPaid = numPaid;
	}

	public synchronized double getTotalPaid() {
		return totalPaid;
	}

	public synchronized void setTotalPaid(double totalPaid) {
		this.totalPaid = totalPaid;
	}

	public synchronized int getNumNotPaid() {
		return numNotPaid;
	}

	public synchronized void setNumNotPaid(int numNotPaid) {
		this.numNotPaid = numNotPaid;
	}

	public synchronized double getTotalNotPaid() {
		return totalNotPaid;
	}

	public synchronized void setTotalNotPaid(double totalNotPaid) {
		this.totalNotPaid = totalNotPaid;
	}
}
package intERS.output;

public class OutputTarget extends OutputAbstract {

	private double wealth;
	private double income;
	private int numExtortion;
	private double totalExtortion;
	private int numProtectionRequested;
	private int numProtectionReceived;
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
		this.numProtectionRequested = 0;
		this.numProtectionReceived = 0;
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
				+ "numProtectionRequested" + fs + "numProtectionReceived" + fs
				+ "numPaid" + fs + "totalPaid" + fs + "numNotPaid" + fs
				+ "totalNotPaid" + fs + "numPunishment" + fs
				+ "totalPunishment";

		return str;
	}

	@Override
	public String getLine(String fs) {
		String str = new String();

		str += this.type + fs + this.id + fs + this.wealth + fs + this.income
				+ fs + this.numExtortion + fs + this.totalExtortion + fs
				+ this.numProtectionRequested + fs + this.numProtectionReceived
				+ fs + this.numPaid + fs + this.totalPaid + fs
				+ this.numNotPaid + fs + this.totalNotPaid + fs
				+ this.numPunishment + fs + this.totalPunishment;

		return str;
	}

	public double getWealth() {
		return wealth;
	}

	public void setWealth(double wealth) {
		this.wealth = wealth;
	}

	public double getIncome() {
		return income;
	}

	public void setIncome(double income) {
		this.income = income;
	}

	public int getNumPunishment() {
		return numPunishment;
	}

	public void setNumPunishment(int numPunishment) {
		this.numPunishment = numPunishment;
	}

	public double getTotalPunishment() {
		return totalPunishment;
	}

	public void setTotalPunishment(double totalPunishment) {
		this.totalPunishment = totalPunishment;
	}

	public int getNumProtectionRequested() {
		return numProtectionRequested;
	}

	public void setNumProtectionRequested(int numProtectionRequested) {
		this.numProtectionRequested = numProtectionRequested;
	}

	public int getNumProtectionReceived() {
		return numProtectionReceived;
	}

	public void setNumProtectionReceived(int numProtectionReceived) {
		this.numProtectionReceived = numProtectionReceived;
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

	public int getNumPaid() {
		return numPaid;
	}

	public void setNumPaid(int numPaid) {
		this.numPaid = numPaid;
	}

	public double getTotalPaid() {
		return totalPaid;
	}

	public void setTotalPaid(double totalPaid) {
		this.totalPaid = totalPaid;
	}

	public int getNumNotPaid() {
		return numNotPaid;
	}

	public void setNumNotPaid(int numNotPaid) {
		this.numNotPaid = numNotPaid;
	}

	public double getTotalNotPaid() {
		return totalNotPaid;
	}

	public void setTotalNotPaid(double totalNotPaid) {
		this.totalNotPaid = totalNotPaid;
	}
}
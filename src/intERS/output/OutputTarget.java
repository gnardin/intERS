package intERS.output;

public class OutputTarget extends OutputAbstract {
	
	private double	wealth;
	
	private double	income;
	
	private int			extorted;
	
	private double	incomeExtorted;
	
	private int			numExtortion;
	
	private double	totalExtortion;
	
	private int			numPaymentProtection;
	
	private int			numProtectionRequested;
	
	private int			numProtectionReceived;
	
	private int			numSuccessfulProtection;
	
	private int			numExtortionPaid;
	
	private double	totalExtortionPaid;
	
	private int			numExtortionNotPaid;
	
	private double	totalExtortionNotPaid;
	
	private int			numPunishment;
	
	private double	totalPunishment;
	
	
	public OutputTarget(int cycle, String type, int id) {
		super(AgentType.TARGET, cycle, type, id);
		
		this.wealth = 0;
		this.income = 0;
		this.extorted = 0;
		this.incomeExtorted = 0;
		this.numExtortion = 0;
		this.totalExtortion = 0;
		this.numPaymentProtection = 0;
		this.numProtectionRequested = 0;
		this.numProtectionReceived = 0;
		this.numSuccessfulProtection = 0;
		this.numExtortionPaid = 0;
		this.totalExtortionPaid = 0;
		this.numExtortionNotPaid = 0;
		this.totalExtortionNotPaid = 0;
		this.numPunishment = 0;
		this.totalPunishment = 0;
	}
	
	
	@Override
	public String getHeader(String fs) {
		String str = new String();
		
		str += "type" + fs + "id" + fs + "wealth" + fs + "income" + fs + "extorted"
				+ fs + "incomeExtorted" + fs + "numExtortion" + fs + "totalExtortion"
				+ fs + "numPaymentProtection" + fs + "numProtectionRequested" + fs
				+ "numProtectionReceived" + fs + "numSuccessfulProtection" + fs
				+ "numExtortionPaid" + fs + "totalExtortionPaid" + fs
				+ "numExtortionNotPaid" + fs + "totalExtortionNotPaid" + fs
				+ "numPunishment" + fs + "totalPunishment";
		
		return str;
	}
	
	
	@Override
	public String getLine(String fs) {
		String str = new String();
		
		str += this.type + fs + this.id + fs + this.wealth + fs + this.income + fs
				+ this.extorted + fs + this.incomeExtorted + fs + this.numExtortion
				+ fs + this.totalExtortion + fs + this.numPaymentProtection + fs
				+ this.numProtectionRequested + fs + this.numProtectionReceived + fs
				+ this.numSuccessfulProtection + fs + this.numExtortionPaid + fs
				+ this.totalExtortionPaid + fs + this.numExtortionNotPaid + fs
				+ this.totalExtortionNotPaid + fs + this.numPunishment + fs
				+ this.totalPunishment;
		
		return str;
	}
	
	
	public double getWealth() {
		return this.wealth;
	}
	
	
	public void setWealth(double wealth) {
		this.wealth = wealth;
	}
	
	
	public double getIncome() {
		return this.income;
	}
	
	
	public void setIncome(double income) {
		this.income = income;
	}
	
	
	public int getExtorted() {
		return this.extorted;
	}
	
	
	public void setExtorted(boolean extorted) {
		if(extorted) {
			this.extorted = 1;
		} else {
			this.extorted = 0;
		}
	}
	
	
	public double getIncomeExtorted() {
		return this.incomeExtorted;
	}
	
	
	public void setIncomeExtorted(double incomeExtorted) {
		this.incomeExtorted = incomeExtorted;
	}
	
	
	public int getNumPaymentProtection() {
		return this.numPaymentProtection;
	}
	
	
	public void setNumPaymentProtection(int numPaymentProtection) {
		this.numPaymentProtection = numPaymentProtection;
	}
	
	
	public int getNumProtectionRequested() {
		return this.numProtectionRequested;
	}
	
	
	public void setNumProtectionRequested(int numProtectionRequested) {
		this.numProtectionRequested = numProtectionRequested;
	}
	
	
	public int getNumProtectionReceived() {
		return this.numProtectionReceived;
	}
	
	
	public void setNumProtectionReceived(int numProtectionReceived) {
		this.numProtectionReceived = numProtectionReceived;
	}
	
	
	public int getNumSuccessfulProtection() {
		return this.numSuccessfulProtection;
	}
	
	
	public void setNumSuccessfulProtection(int numSuccessfulProtection) {
		this.numSuccessfulProtection = numSuccessfulProtection;
	}
	
	
	public int getNumExtortion() {
		return this.numExtortion;
	}
	
	
	public void setNumExtortion(int numExtortion) {
		this.numExtortion = numExtortion;
	}
	
	
	public double getTotalExtortion() {
		return this.totalExtortion;
	}
	
	
	public void setTotalExtortion(double totalExtortion) {
		this.totalExtortion = totalExtortion;
	}
	
	
	public int getNumExtortionPaid() {
		return this.numExtortionPaid;
	}
	
	
	public void setNumExtortionPaid(int numExtortionPaid) {
		this.numExtortionPaid = numExtortionPaid;
	}
	
	
	public double getTotalExtortionPaid() {
		return this.totalExtortionPaid;
	}
	
	
	public void setTotalExtortionPaid(double totalExtortionPaid) {
		this.totalExtortionPaid = totalExtortionPaid;
	}
	
	
	public int getNumExtortionNotPaid() {
		return this.numExtortionNotPaid;
	}
	
	
	public void setNumExtortionNotPaid(int numExtortionNotPaid) {
		this.numExtortionNotPaid = numExtortionNotPaid;
	}
	
	
	public double getTotalExtortionNotPaid() {
		return this.totalExtortionNotPaid;
	}
	
	
	public void setTotalExtortionNotPaid(double totalExtortionNotPaid) {
		this.totalExtortionNotPaid = totalExtortionNotPaid;
	}
	
	
	public int getNumPunishment() {
		return this.numPunishment;
	}
	
	
	public void setNumPunishment(int numPunishment) {
		this.numPunishment = numPunishment;
	}
	
	
	public double getTotalPunishment() {
		return this.totalPunishment;
	}
	
	
	public void setTotalPunishment(double totalPunishment) {
		this.totalPunishment = totalPunishment;
	}
}
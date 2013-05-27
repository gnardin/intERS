package intERS.conf.scenario;

public class ExtorterConf {

	public enum ExtortionType {
		PROPORTIONAL
	}

	public enum PunishmentType {
		FIXED, PROPORTIONAL, ESCALATION
	}

	private String type;
	private String enlargementProbability;
	private int number;
	private double initialWealth;
	private double tolerance;
	private double retaliation;
	private double counterattack;
	private double costFight;
	private double costPunish;

	// Extortion
	private ExtortionType extortionType;
	private double extortion;

	// Punishment
	private PunishmentType punishmentType;
	private double punishment;
	private double minEscalation;
	private double maxEscalation;
	private String formulaEscalation;

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEnlargementProbability() {
		return this.enlargementProbability;
	}

	public void setEnlargementProbability(String enlargementProbability) {
		this.enlargementProbability = enlargementProbability;
	}

	public int getNumberExtorters() {
		return this.number;
	}

	public void setNumberExtorters(int number) {
		this.number = number;
	}

	public double getInitialWealth() {
		return this.initialWealth;
	}

	public void setInitialWealth(double initialWealth) {
		this.initialWealth = initialWealth;
	}

	public double getTolerance() {
		return this.tolerance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public double getRetaliation() {
		return this.retaliation;
	}

	public void setRetaliation(double retaliation) {
		this.retaliation = retaliation;
	}

	public double getCounterattack() {
		return this.counterattack;
	}

	public void setCounterattack(double counterattack) {
		this.counterattack = counterattack;
	}

	public ExtortionType getExtortionType() {
		return extortionType;
	}

	public void setExtortionType(ExtortionType extortionType) {
		this.extortionType = extortionType;
	}

	public double getExtortion() {
		return this.extortion;
	}

	public void setExtortion(double extortion) {
		this.extortion = extortion;
	}

	public PunishmentType getPunishmentType() {
		return this.punishmentType;
	}

	public void setPunishmentType(PunishmentType punishmentType) {
		this.punishmentType = punishmentType;
	}

	public double getCostFight() {
		return this.costFight;
	}

	public void setCostFight(double costFight) {
		this.costFight = costFight;
	}

	public double getCostPunish() {
		return this.costPunish;
	}

	public void setCostPunish(double costPunish) {
		this.costPunish = costPunish;
	}

	public double getPunishment() {
		return this.punishment;
	}

	public void setPunishment(double punishment) {
		this.punishment = punishment;
	}

	public double getMinEscalation() {
		return this.minEscalation;
	}

	public void setMinEscalation(double minEscalation) {
		this.minEscalation = minEscalation;
	}

	public double getMaxEscalation() {
		return this.maxEscalation;
	}

	public void setMaxEscalation(double maxEscalation) {
		this.maxEscalation = maxEscalation;
	}

	public String getFormulaEscalation() {
		return this.formulaEscalation;
	}

	public void setFormulaEscalation(String formulaEscalation) {
		this.formulaEscalation = formulaEscalation;
	}

	@Override
	public String toString() {
		String str = new String();

		str = "EXTORTER \n";
		str += "Type.........................: [" + this.type + "]\n";
		str += "Enlargement Probability......: [" + this.enlargementProbability
				+ "]\n";
		str += "Number of Targets............: [" + this.number + "]\n";
		str += "Initial Wealth...............: [" + this.initialWealth + "]\n";
		str += "Tolerance....................: [" + this.tolerance + "]\n";
		str += "Retaliation..................: [" + this.retaliation + "]\n";
		str += "Counterattack................: [" + this.counterattack + "]\n";
		str += "Extortion Type...............: [" + this.extortionType.name()
				+ "]\n";
		str += "Extortion Value..............: [" + this.extortion + "]\n";
		str += "Punishment Type..............: [" + this.punishmentType.name()
				+ "]\n";
		str += "Cost to Fight................: [" + this.costFight + "]\n";
		str += "Cost to Punish...............: [" + this.costPunish + "]\n";
		if ((this.punishmentType.equals(PunishmentType.FIXED))
				|| (this.punishmentType.equals(PunishmentType.PROPORTIONAL))) {
			str += "Punishment Value.............: [" + this.punishment + "]\n";
		} else if (this.punishmentType.equals(PunishmentType.ESCALATION)) {
			str += "Escalation Minimum Value.....: [" + this.minEscalation
					+ "]\n";
			str += "Escalation Maximum Value.....: [" + this.maxEscalation
					+ "]\n";
			str += "Escalation Formula...........: [" + this.formulaEscalation
					+ "]\n";
		}

		return str;
	}
}
package intERS.conf.scenario;

public class TargetConf {

	private String targetClass;
	private String type;
	private int number;
	private int extorterPerTarget;
	private double minIncome;
	private double maxIncome;
	private double minIncomeVariation;
	private double maxIncomeVariation;
	private double availExtortionIncome;
	private int memLength;

	public String getTargetClass() {
		return this.targetClass;
	}

	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNumberTargets() {
		return this.number;
	}

	public void setNumberTargets(int number) {
		this.number = number;
	}

	public int getExtorterPerTarget() {
		return this.extorterPerTarget;
	}

	public void setExtorterPerTarget(int extorterPerTarget) {
		this.extorterPerTarget = extorterPerTarget;
	}

	public double getMinIncome() {
		return this.minIncome;
	}

	public void setMinIncome(double minIncome) {
		this.minIncome = minIncome;
	}

	public double getMaxIncome() {
		return this.maxIncome;
	}

	public void setMaxIncome(double maxIncome) {
		this.maxIncome = maxIncome;
	}

	public double getMinIncomeVariation() {
		return this.minIncomeVariation;
	}

	public void setMinIncomeVariation(double minIncomeVariation) {
		this.minIncomeVariation = minIncomeVariation;
	}

	public double getMaxIncomeVariation() {
		return this.maxIncomeVariation;
	}

	public void setMaxIncomeVariation(double maxIncomeVariation) {
		this.maxIncomeVariation = maxIncomeVariation;
	}

	public double getAvailExtortionIncome() {
		return this.availExtortionIncome;
	}

	public void setAvailExtortionIncome(double availExtortionIncome) {
		this.availExtortionIncome = availExtortionIncome;
	}

	public int getMemLength() {
		return this.memLength;
	}

	public void setMemLength(int memLength) {
		this.memLength = memLength;
	}

	@Override
	public String toString() {
		String str = new String();

		str += "TARGET\n";
		str += "Class........................: [" + this.targetClass + "]\n";
		str += "Type.........................: [" + this.type + "]\n";
		str += "Number of Targets............: [" + this.number + "]\n";
		str += "Extorters per Target.........: [" + this.extorterPerTarget
				+ "]\n";
		str += "Minimum Income...............: [" + this.minIncome + "]\n";
		str += "Maximum Income...............: [" + this.maxIncome + "]\n";
		str += "Minimun Income Variation.....: [" + this.minIncomeVariation
				+ "]\n";
		str += "Maximum Income Variation.....: [" + this.maxIncomeVariation
				+ "]\n";
		str += "Available Extortion Income...: [" + this.availExtortionIncome
				+ "]\n";
		str += "Memory Length................: [" + this.memLength + "]\n";

		return str;
	}
}
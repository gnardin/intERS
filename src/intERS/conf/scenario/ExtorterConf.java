package intERS.conf.scenario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import repast.simphony.random.RandomHelper;

public class ExtorterConf {
	private String extorterClass;
	private String type;
	private String enlargementProbability;
	private int numberExtorters;
	private double initialWealth;
	private double impulseProtection;
	private double impulseFight;
	private double impulseAttack;
	private double impulseCounterattack;
	private double costFightProtection;
	private double costFightAttack;
	private double costPunish;
	private String extortersCfgFilename;
	private double minExtort;
	private double maxExtort;
	private double stepExtort;
	private double minPunish;
	private double maxPunish;
	private double stepPunish;
	private boolean updateAtEnd;

	// ExtorterId, Extortion
	private Map<Integer, Double> extortions;

	// ExtorterId, Punishment
	private Map<Integer, Double> punishments;

	public ExtorterConf() {
		this.extortions = new HashMap<Integer, Double>();
		this.punishments = new HashMap<Integer, Double>();
	}

	public String getExtorterClass() {
		return this.extorterClass;
	}

	public void setExtorterClass(String extorterClass) {
		this.extorterClass = extorterClass;
	}

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
		return this.numberExtorters;
	}

	public void setNumberExtorters(int numberExtorters) {
		this.numberExtorters = numberExtorters;
	}

	public double getInitialWealth() {
		return this.initialWealth;
	}

	public void setInitialWealth(double initialWealth) {
		this.initialWealth = initialWealth;
	}

	public double getImpulseProtection() {
		return this.impulseProtection;
	}

	public void setImpulseProtection(double impulseProtection) {
		this.impulseProtection = impulseProtection;
	}

	public double getImpulseFight() {
		return this.impulseFight;
	}

	public void setImpulseFight(double impulseFight) {
		this.impulseFight = impulseFight;
	}

	public double getImpulseAttack() {
		return this.impulseAttack;
	}

	public void setImpulseAttack(double impulseAttack) {
		this.impulseAttack = impulseAttack;
	}

	public double getImpulseCounterattack() {
		return this.impulseCounterattack;
	}

	public void setImpulseCounterattack(double impulseCounterattack) {
		this.impulseCounterattack = impulseCounterattack;
	}

	public double getCostFightProtection() {
		return this.costFightProtection;
	}

	public void setCostFightProtection(double costFightProtection) {
		this.costFightProtection = costFightProtection;
	}

	public double getCostFightAttack() {
		return this.costFightAttack;
	}

	public void setCostFightAttack(double costFightAttack) {
		this.costFightAttack = costFightAttack;
	}

	public double getCostPunish() {
		return this.costPunish;
	}

	public void setCostPunish(double costPunish) {
		this.costPunish = costPunish;
	}

	public String getExtortersCfgFilename() {
		return this.extortersCfgFilename;
	}

	public void setExtortersCfgFilename(String extortersCfgFilename) {
		this.extortersCfgFilename = extortersCfgFilename;
	}

	public double getMinExtort() {
		return this.minExtort;
	}

	public void setMinExtort(double minExtort) {
		this.minExtort = minExtort;
	}

	public double getMaxExtort() {
		return this.maxExtort;
	}

	public void setMaxExtort(double maxExtort) {
		this.maxExtort = maxExtort;
	}

	public double getStepExtort() {
		return this.stepExtort;
	}

	public void setStepExtort(double stepExtort) {
		this.stepExtort = stepExtort;
	}

	public double getMinPunish() {
		return this.minPunish;
	}

	public void setMinPunish(double minPunish) {
		this.minPunish = minPunish;
	}

	public double getMaxPunish() {
		return this.maxPunish;
	}

	public void setMaxPunish(double maxPunish) {
		this.maxPunish = maxPunish;
	}

	public double getStepPunish() {
		return this.stepPunish;
	}

	public void setStepPunish(double stepPunish) {
		this.stepPunish = stepPunish;
	}

	public boolean getUpdateAtEnd() {
		return this.updateAtEnd;
	}

	public void setUpdateAtEnd(boolean updateAtEnd) {
		this.updateAtEnd = updateAtEnd;
	}

	/**
	 * Upload the Extorters' extortion and punishment values
	 * 
	 * @param path
	 *            Output directory path
	 * @param fieldSeparator
	 *            Field separator
	 * @return none
	 */
	public void upload(String path, String fieldSeparator) {
		boolean uploaded = false;
		String filename = path + File.separator + this.extortersCfgFilename;

		File directory = new File(path);
		directory.mkdirs();

		File file = new File(filename);
		if (file.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				String[] tokens;
				boolean error = false;
				Integer extorterId;
				Double extortion;
				Double punishment;
				while (((line = br.readLine()) != null) && (!error)) {
					tokens = line.split(fieldSeparator);
					if (tokens.length < 3) {
						error = true;
					} else {
						try {
							extorterId = Integer.valueOf(tokens[0]);
							extortion = Double.valueOf(tokens[1]) / 100.0;
							punishment = Double.valueOf(tokens[2]) / 100.0;

							this.extortions.put(extorterId, extortion);
							this.punishments.put(extorterId, punishment);
						} catch (NumberFormatException e) {
							error = true;
						}
					}
				}

				if (!error) {
					uploaded = true;
				}

				br.close();
			} catch (IOException e) {
			}
		}

		if (!uploaded) {
			this.extortions.clear();
			this.punishments.clear();

			// Extorter options
			List<Double> extortValues = new ArrayList<Double>();
			double extort = this.minExtort;
			while (extort <= this.maxExtort) {
				extortValues.add(extort);
				extort += this.stepExtort;
			}

			// Punishment options
			List<Double> punishValues = new ArrayList<Double>();
			double punish = this.minPunish;
			while (punish <= this.maxPunish) {
				punishValues.add(punish);
				punish += this.stepPunish;
			}

			for (int extorterId = 1; extorterId <= this.numberExtorters; extorterId++) {
				this.extortions.put(extorterId, extortValues.get(RandomHelper
						.nextIntFromTo(0, extortValues.size() - 1)) / 100.0);

				this.punishments.put(extorterId, punishValues.get(RandomHelper
						.nextIntFromTo(0, punishValues.size() - 1)) / 100.0);
			}

			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(file,
						false));

				String str;
				for (Integer extorterId : this.extortions.keySet()) {
					str = extorterId.toString() + ";"
							+ (this.extortions.get(extorterId) * 100)
							+ fieldSeparator
							+ (this.punishments.get(extorterId) * 100) + "\n";
					bw.write(str);
				}

				bw.flush();
				bw.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * Define the extortion value
	 * 
	 * @param extorterId
	 *            Extorter identification
	 * @return Extortion value
	 */
	public double getExtortion(int extorterId) {
		double extortionValue = 0;

		if (this.extortions.containsKey(extorterId)) {
			extortionValue = this.extortions.get(extorterId);
		}

		return extortionValue;
	}

	/**
	 * Define the punishment value
	 * 
	 * @param extorterId
	 *            Extorter identification
	 * @return Punishment value
	 */
	public double getPunishment(int extorterId) {
		double punishmentValue = 0;

		if (this.punishments.containsKey(extorterId)) {
			punishmentValue = this.punishments.get(extorterId);
		}

		return punishmentValue;
	}

	@Override
	public String toString() {
		String str = new String();

		str = "EXTORTER \n";
		str += "Class........................: [" + this.extorterClass + "]\n";
		str += "Type.........................: [" + this.type + "]\n";
		str += "Enlargement Probability......: [" + this.enlargementProbability
				+ "]\n";
		str += "Number of Extorters..........: [" + this.numberExtorters
				+ "]\n";
		str += "Initial Wealth...............: [" + this.initialWealth + "]\n";
		str += "Impulse Protection...........: [" + this.impulseProtection
				+ "]\n";
		str += "Impulse Fight................: [" + this.impulseFight + "]\n";
		str += "Impulse Attack...............: [" + this.impulseAttack + "]\n";
		str += "Impulse Counterattack........: [" + this.impulseCounterattack
				+ "]\n";
		str += "Cost of Fight Protection.....: [" + this.costFightProtection
				+ "]\n";
		str += "Cost of Fight Attack.........: [" + this.costFightAttack
				+ "]\n";
		str += "Cost of Punish...............: [" + this.costPunish + "]\n";
		str += "Extorters Config Filename....: [" + this.extortersCfgFilename
				+ "]\n";
		str += "Minimum Extort...............: [" + this.minExtort + "]\n";
		str += "Maximum Extort...............: [" + this.maxExtort + "]\n";
		str += "Step Extort..................: [" + this.stepExtort + "]\n";
		str += "Minimum Punish...............: [" + this.minPunish + "]\n";
		str += "Maximum Punish...............: [" + this.maxPunish + "]\n";
		str += "Step Punish..................: [" + this.stepPunish + "]\n";
		str += "Update Wealth at End.........: [" + this.updateAtEnd + "]\n";

		return str;
	}
}
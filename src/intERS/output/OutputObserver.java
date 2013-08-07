package intERS.output;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

public class OutputObserver extends OutputAbstract {

	private Map<String, Integer> targetsAlive;
	private Map<String, Integer> targetsExtorted;
	private Map<String, Integer> targesExtortion;
	private Map<String, Integer> extortersFree;
	private Map<String, Integer> extortersImprisoned;

	public OutputObserver(int cycle, int id, String type) {
		super(AgentType.OBSERVER, cycle, id, type);

		this.targetsAlive = new Hashtable<String, Integer>();
		this.targetsExtorted = new Hashtable<String, Integer>();
		this.extortersFree = new Hashtable<String, Integer>();
		this.extortersImprisoned = new Hashtable<String, Integer>();
		this.targesExtortion = new Hashtable<String, Integer>();
	}

	@Override
	public String getHeader(String fs) {
		String str = new String();
		Map<String, Integer> map;

		str += "type" + fs + "id" + fs;

		map = new TreeMap<String, Integer>(this.targetsAlive);
		for (String type : map.keySet()) {
			str += "TA" + type + fs;
		}

		map = new TreeMap<String, Integer>(this.targetsExtorted);
		for (String type : map.keySet()) {
			str += "TE" + type + fs;
		}

		map = new TreeMap<String, Integer>(this.targesExtortion);
		for (String type : map.keySet()) {
			str += "NE" + type + fs;
		}

		str += "totalTargets" + fs;

		String strImprisoned = new String();
		map = new TreeMap<String, Integer>(this.extortersFree);
		for (String type : map.keySet()) {
			str += "FR" + type + fs;
			strImprisoned += "IM" + type + fs;
		}

		str += "totalExtortersFree" + fs + strImprisoned
				+ "totalExtortersImprisoned";

		return str;
	}

	@Override
	public String getLine(String fs) {
		String str = new String();
		Map<String, Integer> map;

		str += this.type + fs + this.id + fs;

		int numTargets;
		int numTotalTargets = 0;
		map = new TreeMap<String, Integer>(this.targetsAlive);
		for (String type : map.keySet()) {
			numTargets = map.get(type);
			numTotalTargets += numTargets;

			str += numTargets + fs;
		}

		int numTargetsExtorted = 0;
		map = new TreeMap<String, Integer>(this.targetsExtorted);
		for (String type : map.keySet()) {
			numTargetsExtorted = map.get(type);

			str += numTargetsExtorted + fs;
		}

		int numExtortersPerTargetExtorted = 0;
		map = new TreeMap<String, Integer>(this.targesExtortion);
		for (String type : map.keySet()) {
			numExtortersPerTargetExtorted = map.get(type);

			str += numExtortersPerTargetExtorted + fs;
		}

		str += numTotalTargets + fs;

		int numFree;
		int numTotalFree = 0;
		int numImprisoned;
		int numTotalImprisoned = 0;
		String strImprisoned = new String();
		map = new TreeMap<String, Integer>(this.extortersFree);
		for (String type : map.keySet()) {
			numFree = map.get(type);
			numTotalFree += numFree;

			numImprisoned = 0;
			if (this.extortersImprisoned.containsKey(type)) {
				numImprisoned = this.extortersImprisoned.get(type);
			}
			numTotalImprisoned += numImprisoned;

			str += numFree + fs;
			strImprisoned += numImprisoned + fs;
		}

		str += numTotalFree + fs + strImprisoned + numTotalImprisoned;

		return str;
	}

	public synchronized int getNumTotalTargets() {
		int num = 0;
		for (Integer numTargets : this.targetsAlive.values()) {
			num += numTargets;
		}
		return num;
	}

	public synchronized int getNumTypeTargetsAlive(String type) {
		int num = 0;
		if (this.targetsAlive.containsKey(type)) {
			num = this.targetsAlive.get(type);
		}

		return num;
	}

	public synchronized void setNumTargetsAlive(String type, int numTargets) {
		this.targetsAlive.put(type, numTargets);
	}

	public synchronized int getNumTypeTargetsExtorted(String type) {
		int num = 0;
		if (this.targetsExtorted.containsKey(type)) {
			num = this.targetsExtorted.get(type);
		}

		return num;
	}

	public synchronized void setNumTargetsExtorted(String type, int numTargets) {
		this.targetsExtorted.put(type, numTargets);
	}

	public synchronized int getNumTargetExtortions(String type) {
		int num = 0;
		if (this.targesExtortion.containsKey(type)) {
			num = this.targesExtortion.get(type);
		}

		return num;
	}

	public synchronized void setNumTargetExtorions(String type,
			int numExtortions) {
		this.targesExtortion.put(type, numExtortions);
	}

	public synchronized int getNumTotalExtortersFree() {
		int num = 0;
		for (Integer numExtorters : this.extortersFree.values()) {
			num += numExtorters;
		}
		return num;
	}

	public synchronized int getNumTypeExtortersFree(String type) {
		int num = 0;
		if (this.extortersFree.containsKey(type)) {
			num = this.extortersFree.get(type);
		}

		return num;
	}

	public synchronized void setNumExtortersFree(String type,
			int numExtortersFree) {
		this.extortersFree.put(type, numExtortersFree);
	}

	public synchronized int getNumTotalExtortersImprisoned() {
		int num = 0;
		for (Integer numExtorters : this.extortersImprisoned.values()) {
			num += numExtorters;
		}
		return num;
	}

	public synchronized int getNumTypeExtortersImprisoned(String type) {
		int num = 0;
		if (this.extortersImprisoned.containsKey(type)) {
			num = this.extortersImprisoned.get(type);
		}

		return num;
	}

	public synchronized void setNumExtortersImprisoned(String type,
			int numExtortersImprisoned) {
		this.extortersImprisoned.put(type, numExtortersImprisoned);
	}
}
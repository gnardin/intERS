package intERS.statistics;

public interface DataSummaryInterface {

	public boolean add(String filename);

	public boolean writeAvg(String filename);

	public boolean writeSum(String filename);
}
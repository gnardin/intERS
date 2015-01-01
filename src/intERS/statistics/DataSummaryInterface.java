package intERS.statistics;

public interface DataSummaryInterface {
	
	public boolean add(String filename, String fieldSeparator);
	
	
	public boolean writeAvg(String filename, String fieldSeparator);
	
	
	public boolean writeSum(String filename, String fieldSeparator);
}
package intERS.test;

import intERS.statistics.DataSummaryGeneric;
import org.junit.Test;

public class DataSummaryGenericTest{
	
	@Test
	public void dataSummaryTest(){
		DataSummaryGeneric summary = new DataSummaryGeneric();
		
		summary.add("/data/workspace/gloders/intERS/output/observers1.csv", ";");
		summary.add("/data/workspace/gloders/intERS/output/observers2.csv", ";");
		
		summary.writeAvg("/data/workspace/gloders/intERS/output/avgObs.csv", ";");
		summary.writeSum("/data/workspace/gloders/intERS/output/sumObs.csv", ";");
	}
}
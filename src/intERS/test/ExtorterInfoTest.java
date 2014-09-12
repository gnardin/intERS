package intERS.test;

import intERS.agents.entity.ExtorterInfo;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ExtorterInfoTest{
	
	private ExtorterInfo	extorterInfo	= new ExtorterInfo(0, 0.0, 0.0);
	
	
	@Test
	public void createTest(){
		assertThat(this.extorterInfo.getSuccessfulProtectionProb(), is(0.5));
		assertThat(this.extorterInfo.getPunishmentProb(), is(0.5));
	}
	
	
	@Test
	public void successfulProtectionTest(){
		this.extorterInfo.setNumRequestedProtectionAgainst(1);
		this.extorterInfo.setNumReceivedProtectionAgainst(1);
		this.extorterInfo.updateSuccessfulProtectionProb();
		
		assertThat(this.extorterInfo.getSuccessfulProtectionProb(), is(1.0));
		
		this.extorterInfo.setNumRequestedProtectionAgainst(10);
		this.extorterInfo.setNumReceivedProtectionAgainst(5);
		this.extorterInfo.updateSuccessfulProtectionProb();
		
		assertThat(this.extorterInfo.getSuccessfulProtectionProb(), is(0.75));
		
		this.extorterInfo.setNumRequestedProtectionAgainst(10);
		this.extorterInfo.setNumReceivedProtectionAgainst(5);
		this.extorterInfo.updateSuccessfulProtectionProb();
		
		assertThat(this.extorterInfo.getSuccessfulProtectionProb(), is(0.5));
	}
	
	
	@Test
	public void punishmentTest(){
		this.extorterInfo.setNotPaidExtortion(true);
		this.extorterInfo.setReceivedPunishment(true);
		this.extorterInfo.updatePunishmentProb();
		
		assertThat(this.extorterInfo.getPunishmentProb(), is(1.0));
		
		this.extorterInfo.setNotPaidExtortion(true);
		this.extorterInfo.setReceivedPunishment(false);
		this.extorterInfo.updatePunishmentProb();
		
		assertThat(this.extorterInfo.getPunishmentProb(), is(0.5));
		
		this.extorterInfo.setNotPaidExtortion(true);
		this.extorterInfo.setReceivedPunishment(false);
		this.extorterInfo.updatePunishmentProb();
		
		assertThat(this.extorterInfo.getPunishmentProb(), is(0.0));
		
		this.extorterInfo.setNotPaidExtortion(false);
		this.extorterInfo.setReceivedPunishment(false);
		this.extorterInfo.updatePunishmentProb();
		
		assertThat(this.extorterInfo.getPunishmentProb(), is(0.0));
	}
}
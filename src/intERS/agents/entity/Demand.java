package intERS.agents.entity;

public class Demand{
	
	// Extortion value
	private double	extortion;
	
	// Punishment value
	private double	punishment;
	
	
	/**
	 * Constructor
	 * 
	 * @param extortion
	 *          Extortion value
	 * @param punishment
	 *          Punishment value
	 * @return none
	 */
	public Demand(double extortion, double punishment){
		this.extortion = extortion;
		this.punishment = punishment;
	}
	
	
	/**
	 * Return the extortion value
	 * 
	 * @return Extortion value
	 */
	public double getExtortion(){
		return this.extortion;
	}
	
	
	/**
	 * Return the punishment value
	 * 
	 * @return Punishment value
	 */
	public double getPunishment(){
		return this.punishment;
	}
}
package intERS.conf.scenario;

public class StateConf {
  
  
  private String stateClass;
  
  // Prison information
  private double prisonProbability;
  
  private int    prisonRounds;
  
  
  public String getStateClass() {
    return this.stateClass;
  }
  
  
  public void setStateClass(String stateClass) {
    this.stateClass = stateClass;
  }
  
  
  public double getPrisonProbability() {
    return this.prisonProbability;
  }
  
  
  public void setPrisonProbability(double prisonProbability) {
    this.prisonProbability = prisonProbability;
  }
  
  
  public int getPrisonRounds() {
    return this.prisonRounds;
  }
  
  
  public void setPrisonRounds(int prisonRounds) {
    this.prisonRounds = prisonRounds;
  }
  
  
  @Override
  public String toString() {
    String str = new String();
    
    str = "STATE \n";
    str += "Class........................: [" + this.stateClass + "]\n";
    str += "Prison Probability...........: [" + this.prisonProbability + "]\n";
    str += "Prison Rounds................: [" + this.prisonRounds + "]\n";
    
    return str;
  }
}
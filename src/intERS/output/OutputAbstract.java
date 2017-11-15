package intERS.output;

public abstract class OutputAbstract {
  
  
  public enum AgentType {
    EXTORTER,
    OBSERVER,
    TARGET;
  }
  
  protected AgentType agentType;
  
  protected int       cycle;
  
  protected String    type;
  
  protected int       id;
  
  
  public OutputAbstract(AgentType agentType, int cycle, String type, int id) {
    this.agentType = agentType;
    this.cycle = cycle;
    this.type = type;
    this.id = id;
  }
  
  
  public AgentType getAgentType() {
    return this.agentType;
  }
  
  
  public int getCycle() {
    return this.cycle;
  }
  
  
  public String getType() {
    return this.type;
  }
  
  
  public int getId() {
    return this.id;
  }
  
  
  public abstract String getHeader(String fs);
  
  
  public abstract String getLine(String fs);
}
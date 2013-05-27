package intERS.output;

public abstract class OutputAbstract {

	public enum AgentType {
		EXTORTER, OBSERVER, TARGET;
	}

	protected AgentType agentType;
	protected int id;
	protected String type;

	public OutputAbstract(AgentType agentType, int id, String type) {
		this.agentType = agentType;
		this.id = id;
		this.type = type;
	}

	public AgentType getAgentType() {
		return this.agentType;
	}

	public int getId() {
		return this.id;
	}

	public String getType() {
		return this.type;
	}

	public abstract String getHeader(String fs);

	public abstract String getLine(String fs);
}
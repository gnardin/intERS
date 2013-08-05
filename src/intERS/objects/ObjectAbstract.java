package intERS.objects;

public abstract class ObjectAbstract {

	public enum AgentType {
		EXTORTER, OBSERVER, TARGET;
	}

	protected AgentType agentType;
	protected int cycle;
	protected int id;
	protected String type;

	public ObjectAbstract(AgentType agentType, int cycle, int id, String type) {
		this.agentType = agentType;
		this.cycle = cycle;
		this.id = id;
		this.type = type;
	}

	public AgentType getAgentType() {
		return this.agentType;
	}

	public int getCycle() {
		return this.cycle;
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
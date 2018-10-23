package intERS.conf.scenario;

public class ExtorterConf {
  
  private String       extorterClass;
  
  private String       type;
  
  private String       enlargementProbability;
  
  private int          numberExtorters;
  
  private double       initialWealth;
  
  private double       impulseProtection;
  
  private double       impulseFightProtection;
  
  private double       impulseRetaliation;
  
  private double       impulseFightRetaliation;
  
  private double       costFightProtection;
  
  private double       costFightRetaliation;
  
  private double       costPunish;
  
  private boolean      enableExtortersCfg;
  
  private String       extortersCfgFilename;
  
  private double       minExtort;
  
  private double       maxExtort;
  
  private double       stepExtort;
  
  private double       minPunish;
  
  private double       maxPunish;
  
  private double       stepPunish;
  
  private boolean      updateAtEnd;
  
  private StrategyConf strategyConf;
  
  
  public ExtorterConf() {
  }
  
  
  public String getExtorterClass() {
    return this.extorterClass;
  }
  
  
  public void setExtorterClass( String extorterClass ) {
    this.extorterClass = extorterClass;
  }
  
  
  public String getType() {
    return this.type;
  }
  
  
  public void setType( String type ) {
    this.type = type;
  }
  
  
  public String getEnlargementProbability() {
    return this.enlargementProbability;
  }
  
  
  public void setEnlargementProbability( String enlargementProbability ) {
    this.enlargementProbability = enlargementProbability;
  }
  
  
  public int getNumberExtorters() {
    return this.numberExtorters;
  }
  
  
  public void setNumberExtorters( int numberExtorters ) {
    this.numberExtorters = numberExtorters;
  }
  
  
  public double getInitialWealth() {
    return this.initialWealth;
  }
  
  
  public void setInitialWealth( double initialWealth ) {
    this.initialWealth = initialWealth;
  }
  
  
  public double getImpulseProtection() {
    return this.impulseProtection;
  }
  
  
  public void setImpulseProtection( double impulseProtection ) {
    this.impulseProtection = impulseProtection;
  }
  
  
  public double getImpulseFightProtection() {
    return this.impulseFightProtection;
  }
  
  
  public void setImpulseFightProtection( double impulseFight ) {
    this.impulseFightProtection = impulseFight;
  }
  
  
  public double getImpulseRetaliation() {
    return this.impulseRetaliation;
  }
  
  
  public void setImpulseRetaliation( double impulseRetaliation ) {
    this.impulseRetaliation = impulseRetaliation;
  }
  
  
  public double getImpulseFightRetaliation() {
    return this.impulseFightRetaliation;
  }
  
  
  public void setImpulseFightRetaliation( double impulseFightRetaliation ) {
    this.impulseFightRetaliation = impulseFightRetaliation;
  }
  
  
  public double getCostFightProtection() {
    return this.costFightProtection;
  }
  
  
  public void setCostFightProtection( double costFightProtection ) {
    this.costFightProtection = costFightProtection;
  }
  
  
  public double getCostFightAttack() {
    return this.costFightRetaliation;
  }
  
  
  public void setCostFightRetaliation( double costFightRetaliation ) {
    this.costFightRetaliation = costFightRetaliation;
  }
  
  
  public double getCostPunish() {
    return this.costPunish;
  }
  
  
  public void setCostPunish( double costPunish ) {
    this.costPunish = costPunish;
  }
  
  
  public boolean getEnableExtortersCfg() {
    return this.enableExtortersCfg;
  }
  
  
  public void setEnableExtortersCfg( boolean enableExtortersCfg ) {
    this.enableExtortersCfg = enableExtortersCfg;
  }
  
  
  public String getExtortersCfgFilename() {
    return this.extortersCfgFilename;
  }
  
  
  public void setExtortersCfgFilename( String extortersCfgFilename ) {
    this.extortersCfgFilename = extortersCfgFilename;
  }
  
  
  public double getMinExtort() {
    return this.minExtort;
  }
  
  
  public void setMinExtort( double minExtort ) {
    this.minExtort = minExtort;
  }
  
  
  public double getMaxExtort() {
    return this.maxExtort;
  }
  
  
  public void setMaxExtort( double maxExtort ) {
    this.maxExtort = maxExtort;
  }
  
  
  public double getStepExtort() {
    return this.stepExtort;
  }
  
  
  public void setStepExtort( double stepExtort ) {
    this.stepExtort = stepExtort;
  }
  
  
  public double getMinPunish() {
    return this.minPunish;
  }
  
  
  public void setMinPunish( double minPunish ) {
    this.minPunish = minPunish;
  }
  
  
  public double getMaxPunish() {
    return this.maxPunish;
  }
  
  
  public void setMaxPunish( double maxPunish ) {
    this.maxPunish = maxPunish;
  }
  
  
  public double getStepPunish() {
    return this.stepPunish;
  }
  
  
  public void setStepPunish( double stepPunish ) {
    this.stepPunish = stepPunish;
  }
  
  
  public boolean getUpdateAtEnd() {
    return this.updateAtEnd;
  }
  
  
  public void setUpdateAtEnd( boolean updateAtEnd ) {
    this.updateAtEnd = updateAtEnd;
  }
  
  
  public StrategyConf getExtortersStrategy() {
    return this.strategyConf;
  }
  
  
  public void setExtortersStrategy( StrategyConf strategyConf ) {
    this.strategyConf = strategyConf;
  }
  
  
  @Override
  public String toString() {
    String str = new String();
    
    str = "EXTORTER \n";
    str += "Class........................: [" + this.extorterClass + "]\n";
    str += "Type.........................: [" + this.type + "]\n";
    str += "Enlargement Probability......: [" + this.enlargementProbability
        + "]\n";
    str += "Number of Extorters..........: [" + this.numberExtorters + "]\n";
    str += "Initial Wealth...............: [" + this.initialWealth + "]\n";
    str += "Impulse Protection...........: [" + this.impulseProtection + "]\n";
    str += "Impulse Fight Protection.....: [" + this.impulseFightProtection
        + "]\n";
    str += "Impulse Retaliation..........: [" + this.impulseRetaliation + "]\n";
    str += "Impulse Fight Retaliation....: [" + this.impulseFightRetaliation
        + "]\n";
    str += "Cost of Fight Protection.....: [" + this.costFightProtection
        + "]\n";
    str += "Cost of Fight Retaliation....: [" + this.costFightRetaliation
        + "]\n";
    str += "Cost of Punish...............: [" + this.costPunish + "]\n";
    str += "Enable Extorters Config......: [" + this.enableExtortersCfg + "]\n";
    str += "Extorters Config Filename....: [" + this.extortersCfgFilename
        + "]\n";
    str += "Minimum Extort...............: [" + this.minExtort + "]\n";
    str += "Maximum Extort...............: [" + this.maxExtort + "]\n";
    str += "Step Extort..................: [" + this.stepExtort + "]\n";
    str += "Minimum Punish...............: [" + this.minPunish + "]\n";
    str += "Maximum Punish...............: [" + this.maxPunish + "]\n";
    str += "Step Punish..................: [" + this.stepPunish + "]\n";
    str += "Update Wealth at End.........: [" + this.updateAtEnd + "]\n";
    
    return str;
  }
}
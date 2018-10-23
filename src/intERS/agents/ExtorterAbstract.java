package intERS.agents;

import intERS.agents.entity.Demand;
import intERS.conf.scenario.ExtorterConf;
import intERS.output.OutputExtorter;
import intERS.output.OutputRecorder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.random.RandomHelper;
import repast.simphony.util.ContextUtils;

public abstract class ExtorterAbstract {
  
  // Identification
  protected int                            id;
  
  // Type
  protected String                         type;
  
  // Cost of Fight Protection
  protected double                         costFightProtection;
  
  // Cost of Fight Retaliation
  protected double                         costFightRetaliation;
  
  // Cost of Punish
  protected double                         costPunish;
  
  // List of Extorter to counterattack because of Protection
  protected List<Integer>                  counterattackProtection;
  
  // List of Extorter from which counterattack because Protection was received
  protected List<Integer>                  counterattackedProtection;
  
  // List of Extorter to counterattack because of Retaliation
  protected List<Integer>                  counterattackRetaliation;
  
  // List of Extorter from which counterattack because Retaliation was
  // received
  protected List<Integer>                  counterattackedRetaliation;
  
  // Impulse to protect probability [0;1]
  protected double                         impulseProtectionProb;
  
  // Impulse to counterattack protection probability [0;1]
  protected double                         impulseFightProtectionProb;
  
  // Impulse to retaliation probability [0;1]
  protected double                         impulseRetaliationProb;
  
  // Impulse to counterattack retaliation probability [0;1]
  protected double                         impulseFightRetaliationProb;
  
  // Enlargement formula
  protected String                         enlargementFormula;
  
  // Enlargement probability
  protected double                         enlargementProb;
  
  // List of extorted Targets <Target Id, Demand>
  protected Map<Integer, Demand>           extorted;
  
  // List of Extorters <Extorter Id, Extorter Object>
  protected Map<Integer, ExtorterAbstract> extorters;
  
  // List of Extorters that Target was demanded extortion money from
  // <Extorter Id, Target List>
  protected Map<Integer, List<Integer>>    extorterTarget;
  
  // List of protection requests <Target Id, Extorters list>
  protected Map<Integer, List<Integer>>    targetExtorter;
  
  // Output data
  protected OutputExtorter                 output;
  
  // Output recorder
  protected OutputRecorder                 outputRecorder;
  
  // Target payments <Target Id, Payment>
  protected Map<Integer, Double>           paid;
  
  // Punishments <Target Id>
  protected List<Integer>                  punishments;
  
  // Extorters to attack for protection <Extorter Id, List Target Ids>
  protected Map<Integer, List<Integer>>    attackProtection;
  
  // Extorters to non attack for protection <Extorter Id, List Target Ids>
  protected Map<Integer, List<Integer>>    nonAttackProtection;
  
  // Extorters to attack for retaliation <Extorter Id, List Target Ids>
  protected Map<Integer, List<Integer>>    attackRetaliation;
  
  // Extorters non attack for retaliation <Extorter Id, List Target Ids>
  protected Map<Integer, List<Integer>>    nonAttackRetaliation;
  
  // List of received protection attacks <Extorter Id, List Target Ids>
  protected Map<Integer, List<Integer>>    protection;
  
  // List of received attacks <Extorter ID, List Target Ids>
  protected Map<Integer, List<Integer>>    retaliation;
  
  // List of available Targets <Target Id, Target Object>
  protected Map<Integer, TargetAbstract>   targets;
  
  // List of Targets to remove from extorted list
  protected List<Integer>                  targetsToRemove;
  
  // Extortion value
  protected double                         extortion;
  
  // Punishment value
  protected double                         punishment;
  
  // Flag indicating the moment in which the extortion should be added to the
  // Extorter's wealth
  protected boolean                        updateAtEnd;
  
  // Accumulated wealth
  protected double                         wealth;
  
  // Amount won in each round
  protected double                         wealthWon;
  
  // Amount lost in each round
  protected double                         wealthLost;
  
  // Amount lost in protection
  protected double                         lossWealthProtection;
  
  // Amount lost in retaliation
  protected double                         lossWealthRetaliation;
  
  
  /**
   * Constructor
   * 
   * @param extorters
   *          List of Extorters
   * @param targets
   *          List of Targets
   * @param id
   *          Extorter identification
   * @param extorterConf
   *          Extorter configuration
   * @return none
   */
  public ExtorterAbstract( Map<Integer, ExtorterAbstract> extorters, Map<Integer, TargetAbstract> targets, Set<Integer> initialTargets, Integer id, ExtorterConf extorterConf ) {
    this.id = id;
    this.type = extorterConf.getType();
    
    this.enlargementFormula = extorterConf.getEnlargementProbability();
    this.enlargementProb = 0;
    
    this.costFightProtection = extorterConf.getCostFightProtection() / 100;
    this.costFightRetaliation = extorterConf.getCostFightAttack() / 100;
    this.costPunish = extorterConf.getCostPunish() / 100;
    
    this.impulseProtectionProb = extorterConf.getImpulseProtection() / 100;
    this.impulseFightProtectionProb = extorterConf.getImpulseFightProtection()
        / 100;
    this.impulseRetaliationProb = extorterConf.getImpulseRetaliation() / 100;
    this.impulseFightRetaliationProb = extorterConf.getImpulseFightRetaliation()
        / 100;
    
    this.extorted = new HashMap<Integer, Demand>();
    for ( Integer target : initialTargets ) {
      this.extorted.put( target, new Demand( 0.0, 0.0 ) );
    }
    
    this.extorters = extorters;
    this.targets = targets;
    
    this.extortion = extorterConf.getExtortersStrategy()
        .getExtortion( this.id );
    this.punishment = extorterConf.getExtortersStrategy()
        .getPunishment( this.id );
    
    this.targetExtorter = new HashMap<Integer, List<Integer>>();
    this.paid = new HashMap<Integer, Double>();
    
    this.attackProtection = new HashMap<Integer, List<Integer>>();
    this.nonAttackProtection = new HashMap<Integer, List<Integer>>();
    this.protection = new HashMap<Integer, List<Integer>>();
    this.counterattackProtection = new ArrayList<Integer>();
    this.counterattackedProtection = new ArrayList<Integer>();
    
    this.attackRetaliation = new HashMap<Integer, List<Integer>>();
    this.nonAttackRetaliation = new HashMap<Integer, List<Integer>>();
    this.retaliation = new HashMap<Integer, List<Integer>>();
    this.counterattackRetaliation = new ArrayList<Integer>();
    this.counterattackedRetaliation = new ArrayList<Integer>();
    
    this.extorterTarget = new HashMap<Integer, List<Integer>>();
    this.punishments = new ArrayList<Integer>();
    
    this.targetsToRemove = new ArrayList<Integer>();
    
    this.updateAtEnd = extorterConf.getUpdateAtEnd();
    this.wealth = extorterConf.getInitialWealth();
    this.wealthWon = 0;
    this.wealthLost = 0;
    
    this.lossWealthProtection = 0;
    this.lossWealthRetaliation = 0;
    
    // Output
    this.outputRecorder = OutputRecorder.getInstance();
    this.output = new OutputExtorter( 0, this.type, this.id, this.extortion,
        this.punishment );
    this.output.setNumTargets( this.extorted.size() );
    this.output.setWealth( this.wealth );
    this.outputRecorder.addRecord( this.output );
  }
  
  
  /**
   * Return identification
   * 
   * @param none
   * @return Extorter identification
   */
  public int getId() {
    return this.id;
  }
  
  
  /**
   * Return Extorter type
   * 
   * @param none
   * @return Extorter type
   */
  public String getType() {
    return this.type;
  }
  
  
  /**
   * Return the extortion value
   * 
   * @return Extortion value
   */
  public double getExtortion() {
    return this.extortion;
  }
  
  
  /**
   * Return the punishment value
   * 
   * @return Punishment value
   */
  public double getPunishment() {
    return this.punishment;
  }
  
  
  /**
   * Return the number of extorted Targets
   * 
   * @param none
   * @return Number of extorted Targets
   */
  public int getNumberTargets() {
    return this.extorted.size();
  }
  
  
  /**
   * Return the imprecise number of extorted Targets
   * 
   * @param none
   * @return Number of extorted Targets that paid extortion
   */
  public int getNumberTargetsPaid() {
    return this.paid.size();
  }
  
  
  /**
   * Return the accumulated wealth
   * 
   * @param none
   * @return Wealth
   */
  public double getWealth() {
    return this.wealth;
  }
  
  
  /**
   * Round initialization
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.0, interval = 1 )
  public void beginRound() {
    for ( Integer targetId : this.extorted.keySet() ) {
      this.extorted.put( targetId, new Demand( 0.0, 0.0 ) );
    }
    
    this.targetExtorter.clear();
    
    this.attackProtection.clear();
    this.nonAttackProtection.clear();
    this.protection.clear();
    this.counterattackProtection.clear();
    this.counterattackedProtection.clear();
    
    this.attackRetaliation.clear();
    this.nonAttackRetaliation.clear();
    this.counterattackRetaliation.clear();
    this.counterattackedRetaliation.clear();
    this.retaliation.clear();
    
    this.extorterTarget.clear();
    this.paid.clear();
    this.punishments.clear();
    
    this.targetsToRemove.clear();
    
    this.wealthWon = 0;
    this.wealthLost = 0;
    
    this.lossWealthProtection = 0;
    this.lossWealthRetaliation = 0;
    
    Evaluator eval = new Evaluator();
    this.enlargementProb = 0;
    try {
      eval.putVariable( "TARGETS",
          new Integer( this.extorted.size() ).toString() );
      
      this.enlargementProb = new Double(
          eval.evaluate( this.enlargementFormula ) ) / 100.0;
    } catch ( EvaluationException e ) {
      e.printStackTrace();
    }
    
    int round = (int) RunEnvironment.getInstance().getCurrentSchedule()
        .getTickCount();
    this.output = new OutputExtorter( round, this.type, this.id, this.extortion,
        this.punishment );
  }
  
  
  /**
   * Update the list of Targets to demand extortion from
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.05, interval = 1 )
  public void updateTargets() {
    // Extorter has a probability to add new Target
    if ( (this.enlargementProb > 0)
        && (RandomHelper.nextDouble() <= this.enlargementProb)
        && (this.extorted.size() < this.targets.size()) ) {
      
      Object[] targetIds = this.targets.keySet().toArray();
      int targetId;
      boolean finish = false;
      while ( !finish ) {
        targetId = (Integer) targetIds[RandomHelper.nextIntFromTo( 0,
            targetIds.length - 1 )];
        if ( !this.extorted.containsKey( targetId ) ) {
          this.extorted.put( targetId, new Demand( 0, 0 ) );
          finish = true;
        }
      }
    }
  }
  
  
  /**
   * Update the extortion amount to demand from the Targets
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.10, interval = 1 )
  public void updateExtortionPunishment() {
    TargetAbstract target;
    Demand demand;
    double currentIncome;
    for ( Integer targetId : this.extorted.keySet() ) {
      target = this.targets.get( targetId );
      
      // NOTE: If necessary, include noise here
      currentIncome = target.getCurrentIncome();
      
      demand = new Demand( currentIncome * this.extortion,
          currentIncome * this.punishment );
      
      this.extorted.put( targetId, demand );
    }
  }
  
  
  /**
   * Demand the extortion to Targets
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.15, interval = 1 )
  public void demandExtortion() {
    // Output
    int numExtortionDemanded = 0;
    double totalExtortionDemanded = 0;
    
    TargetAbstract target;
    Demand demand;
    for ( Integer targetId : this.extorted.keySet() ) {
      if ( this.targets.containsKey( targetId ) ) {
        demand = this.extorted.get( targetId );
        
        target = this.targets.get( targetId );
        target.receiveExtortionDemand( this.id, demand );
        
        // Output
        numExtortionDemanded++;
        totalExtortionDemanded += demand.getExtortion();
      }
    }
    
    // Output
    this.output.setNumTargets( this.extorted.size() );
    this.output.setNumExtortionDemanded( numExtortionDemanded );
    this.output.setTotalExtortionDemanded( totalExtortionDemanded );
  }
  
  
  /**
   * Receive payment for protection
   * 
   * @param targetId
   *          Identification of the Target paying for protection
   * @param extortion
   *          Extortion value paid
   * @param extorters
   *          List of other Extorters demanding extortion from the Target
   * @return none
   */
  public void receivePaymentExtortion( int targetId, double extortion,
      List<Integer> extorters ) {
    
    this.targetExtorter.put( targetId, extorters );
    
    List<Integer> targetsList;
    for ( Integer extorterId : extorters ) {
      if ( this.extorterTarget.containsKey( extorterId ) ) {
        targetsList = this.extorterTarget.get( extorterId );
      } else {
        targetsList = new ArrayList<Integer>();
      }
      
      targetsList.add( targetId );
      this.extorterTarget.put( extorterId, targetsList );
    }
    
    if ( extortion > 0 ) {
      this.paid.put( targetId, extortion );
      
      if ( this.updateAtEnd ) {
        this.wealthWon += extortion;
      } else {
        this.wealth += extortion;
      }
      
      // Output
      this.output
          .setNumExtortionReceived( this.output.getNumExtortionReceived() + 1 );
      this.output.setTotalExtortionReceived(
          this.output.getTotalExtortionReceived() + extortion );
      this.output
          .setNumPaymentProtection( this.output.getNumPaymentProtection() + 1 );
      if ( !extorters.isEmpty() ) {
        this.output.setNumProtectionRequested(
            this.output.getNumProtectionRequested() + 1 );
      }
    }
  }
  
  
  /**
   * Decide to punish
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.30, interval = 1 )
  public abstract void decidePunishment();
  
  
  /**
   * Punish
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.35, interval = 1 )
  public void punish() {
    TargetAbstract target;
    double costPunish;
    double totalCostPunishment = 0;
    Demand demand;
    int numPunishments = 0;
    for ( Integer targetId : this.punishments ) {
      target = this.targets.get( targetId );
      demand = this.extorted.get( targetId );
      
      costPunish = demand.getPunishment();
      
      // Verify whether the Extorter has wealth to punish the Target
      if ( (this.wealth - this.wealthLost + this.wealthWon - totalCostPunishment
          - (costPunish * this.costPunish)) > 0 ) {
        target.receivePunishment( this.id, costPunish );
        
        numPunishments++;
        totalCostPunishment += costPunish * this.costPunish;
      }
    }
    
    if ( this.updateAtEnd ) {
      this.wealthLost += totalCostPunishment;
    } else {
      this.wealth -= totalCostPunishment;
    }
    
    // Output
    this.output.setNumPunishment( numPunishments );
    this.output.setTotalLostPunishment( totalCostPunishment );
  }
  
  
  /**
   * Decide to attack other Extorters to protect
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.40, interval = 1 )
  public abstract void decideProtection();
  
  
  /**
   * Protect by attacking other Extorters
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.45, interval = 1 )
  public void attackProtection() {
    ExtorterAbstract extorter;
    List<Integer> targetList;
    for ( Integer extorterId : this.attackProtection.keySet() ) {
      targetList = this.attackProtection.get( extorterId );
      
      extorter = this.extorters.get( extorterId );
      extorter.receiveAttackProtection( this.id, targetList );
    }
    
    // Output
    this.output.setNumAttackProtection( this.attackProtection.size() );
    this.output.setNumNonAttackProtection( this.nonAttackProtection.size() );
  }
  
  
  /**
   * Receive protect attack because of protection from other Extorters
   * 
   * @param extorterId
   *          Identification of the protector Extorter
   * @param targetList
   *          List of Targets that required protection against this Extorter
   * @result none
   */
  public void receiveAttackProtection( int extorterId,
      List<Integer> targetList ) {
    this.protection.put( extorterId, targetList );
    
    this.output.setNumAttackProtectionReceived(
        this.output.getNumAttackProtectionReceived() + 1 );
  }
  
  
  /**
   * Decide to counterattack because of protection
   * 
   * @param none
   * @param none
   */
  @ScheduledMethod ( start = 1.50, interval = 1 )
  public abstract void decideCounterattackProtection();
  
  
  /**
   * Counterattack because of protection
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.55, interval = 1 )
  public void counterattackProtection() {
    ExtorterAbstract extorter;
    for ( Integer extorterId : this.counterattackProtection ) {
      extorter = this.extorters.get( extorterId );
      
      extorter.receiveCounterattackProtection( this.id );
      
      this.lossWealthProtection += this.costFightProtection
          * extorter.getWealth();
    }
    // Output
    this.output
        .setNumCounterattackProtection( this.counterattackProtection.size() );
  }
  
  
  /**
   * Receive the counterattack because of protection
   * 
   * @param extorterId
   *          Identification of the counterattack Extorter
   * @return none
   */
  public void receiveCounterattackProtection( int extorterId ) {
    this.counterattackedProtection.add( extorterId );
    
    ExtorterAbstract extorter = this.extorters.get( extorterId );
    this.lossWealthProtection += this.costFightProtection
        * extorter.getWealth();
    
    // Output
    this.output.setNumCounterattackProtectionReceived(
        this.output.getNumCounterattackProtectionReceived() + 1 );
  }
  
  
  /**
   * Update the wealth protection
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.60, interval = 1 )
  public void updateWealthProtection() {
    if ( this.updateAtEnd ) {
      this.wealthLost += this.lossWealthProtection;
    } else {
      this.wealth -= this.lossWealthProtection;
    }
    
    this.output.setTotalLostFightProtection(
        this.output.getTotalLostFightProtection() + this.lossWealthProtection );
  }
  
  
  /**
   * Decide to retaliate
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.65, interval = 1 )
  public abstract void decideRetaliation();
  
  
  /**
   * Retaliation
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.70, interval = 1 )
  public void attackRetaliation() {
    List<Integer> targetsList;
    ExtorterAbstract extorter;
    for ( Integer extorterId : this.attackRetaliation.keySet() ) {
      extorter = this.extorters.get( extorterId );
      
      targetsList = this.attackRetaliation.get( extorterId );
      extorter.receiveAttackRetaliation( this.id, targetsList );
    }
    
    // Output
    this.output.setNumAttackRetaliation( this.attackRetaliation.size() );
    this.output.setNumNonAttackRetaliation( this.nonAttackRetaliation.size() );
  }
  
  
  /**
   * Receive retaliation attack from other Extorters
   * 
   * @param extorterId
   *          Identification of the retaliator Extorter
   * @param targetList
   *          List of targets I am being retaliate for
   */
  public void receiveAttackRetaliation( int extorterId,
      List<Integer> targetList ) {
    this.retaliation.put( extorterId, targetList );
    
    this.output.setNumAttackRetaliationReceived(
        this.output.getNumAttackRetaliationReceived() + 1 );
  }
  
  
  /**
   * Decide to counterattack retaliation
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.75, interval = 1 )
  public abstract void decideCounterattackRetaliation();
  
  
  /**
   * Counterattack retaliation
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.80, interval = 1 )
  public void counterattackRetaliation() {
    ExtorterAbstract extorter;
    for ( Integer extorterId : this.counterattackRetaliation ) {
      extorter = this.extorters.get( extorterId );
      
      extorter.receiveCounterattackRetaliation( this.id );
      
      this.lossWealthRetaliation += this.costFightRetaliation
          * extorter.getWealth();
    }
    
    // Output
    this.output
        .setNumCounterattackRetaliation( this.counterattackRetaliation.size() );
  }
  
  
  /**
   * Receive counterattack retaliation from other Extorters
   * 
   * @param extorterId
   *          Identification of the counterattacker Extorter
   * @return none
   */
  public void receiveCounterattackRetaliation( int extorterId ) {
    this.counterattackedRetaliation.add( extorterId );
    
    ExtorterAbstract extorter = this.extorters.get( extorterId );
    this.lossWealthRetaliation += this.costFightRetaliation
        * extorter.getWealth();
    
    // Output
    this.output.setNumCounterattackRetaliationReceived(
        this.output.getNumCounterattackRetaliationReceived() + 1 );
  }
  
  
  /**
   * Update the wealth retaliation
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.85, interval = 1 )
  public void updateWealthRetaliation() {
    if ( this.updateAtEnd ) {
      this.wealthLost += this.lossWealthRetaliation;
    } else {
      this.wealth -= this.lossWealthRetaliation;
    }
    
    this.output.setTotalLostFightRetaliation(
        this.output.getTotalLostFightRetaliation()
            + this.lossWealthRetaliation );
  }
  
  
  /**
   * Decide to exit
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.95, interval = 1 )
  public void decideExit() {
    
    if ( this.updateAtEnd ) {
      this.wealth += this.wealthWon - this.wealthLost;
    }
    
    if ( (this.wealth <= 0) || (this.extorted.size() == 0) ) {
      
      List<Integer> targetsList;
      List<Integer> extortersList;
      Map<Integer, List<Integer>> extortersTargets = new HashMap<Integer, List<Integer>>();
      
      // Extorter attacked and it was counterattacked
      for ( Integer extorterId : this.attackProtection.keySet() ) {
        if ( this.counterattackedProtection.contains( extorterId ) ) {
          if ( extortersTargets.containsKey( extorterId ) ) {
            targetsList = extortersTargets.get( extorterId );
          } else {
            targetsList = new ArrayList<Integer>();
          }
          
          for ( Integer targetId : this.targetExtorter.keySet() ) {
            extortersList = this.targetExtorter.get( targetId );
            if ( (extortersList.contains( extorterId ))
                && (!targetsList.contains( targetId )) ) {
              targetsList.add( targetId );
            }
          }
          
          extortersTargets.put( extorterId, targetsList );
        }
      }
      
      // Extorter was attacked
      for ( Integer extorterId : this.protection.keySet() ) {
        if ( this.counterattackProtection.contains( extorterId ) ) {
          if ( extortersTargets.containsKey( extorterId ) ) {
            targetsList = extortersTargets.get( extorterId );
          } else {
            targetsList = new ArrayList<Integer>();
          }
          
          for ( Integer targetId : this.protection.get( extorterId ) ) {
            if ( !targetsList.contains( targetId ) ) {
              targetsList.add( targetId );
            }
          }
          extortersTargets.put( extorterId, targetsList );
        }
      }
      
      // Extorter retaliated and it was counterretaliated
      for ( Integer extorterId : this.attackRetaliation.keySet() ) {
        if ( this.counterattackedRetaliation.contains( extorterId ) ) {
          if ( extortersTargets.containsKey( extorterId ) ) {
            targetsList = extortersTargets.get( extorterId );
          } else {
            targetsList = new ArrayList<Integer>();
          }
          
          for ( Integer targetId : this.extorterTarget.get( extorterId ) ) {
            if ( !targetsList.contains( targetId ) ) {
              targetsList.add( targetId );
            }
          }
          extortersTargets.put( extorterId, targetsList );
        }
      }
      
      // Extorter was retaliated
      for ( Integer extorterId : this.retaliation.keySet() ) {
        if ( this.counterattackRetaliation.contains( extorterId ) ) {
          if ( extortersTargets.containsKey( extorterId ) ) {
            targetsList = extortersTargets.get( extorterId );
          } else {
            targetsList = new ArrayList<Integer>();
          }
          
          for ( Integer targetId : this.retaliation.get( extorterId ) ) {
            if ( !targetsList.contains( targetId ) ) {
              targetsList.add( targetId );
            }
          }
          extortersTargets.put( extorterId, targetsList );
        }
      }
      
      // Independent Targets
      List<Integer> allocatedTargets = new ArrayList<Integer>();
      for ( Integer extorterId : extortersTargets.keySet() ) {
        for ( Integer targetId : extortersTargets.get( extorterId ) ) {
          if ( !allocatedTargets.contains( targetId ) ) {
            allocatedTargets.add( targetId );
          }
        }
      }
      
      List<Integer> generalTargetsList = new ArrayList<Integer>();
      for ( Integer targetId : this.extorted.keySet() ) {
        if ( !allocatedTargets.contains( targetId ) ) {
          generalTargetsList.add( targetId );
        }
      }
      
      // Share Targets
      ExtorterAbstract extorter;
      for ( Integer extorterId : extortersTargets.keySet() ) {
        if ( this.extorters.containsKey( extorterId ) ) {
          targetsList = extortersTargets.get( extorterId );
          
          for ( Integer targetId : generalTargetsList ) {
            if ( !targetsList.contains( targetId ) ) {
              targetsList.add( targetId );
            }
          }
          
          extorter = this.extorters.get( extorterId );
          extorter.receiveNewTargets( targetsList );
        }
      }
      
      this.endRound();
      this.die();
    } else {
      // Remove Targets that the Extorter
      // 1. did not receive extortion payment
      // 2. was not able to attack anyone
      // 3. was attacked by someone else
      List<Integer> removeList = new ArrayList<Integer>();
      List<Integer> removeProtectionList = new ArrayList<Integer>();
      List<Integer> targetsList;
      TargetAbstract target;
      boolean wasAttackedProtection;
      boolean attackedProtection;
      boolean wasAttackedRetaliation;
      boolean attackedRetaliation;
      
      for ( Integer targetId : this.extorted.keySet() ) {
        wasAttackedProtection = false;
        attackedProtection = false;
        wasAttackedRetaliation = false;
        attackedRetaliation = false;
        
        if ( !this.paid.containsKey( targetId ) ) {
          for ( Integer extorterId : this.protection.keySet() ) {
            targetsList = this.protection.get( extorterId );
            if ( targetsList.contains( targetId ) ) {
              wasAttackedProtection = true;
            }
          }
          
          for ( Integer extorterId : this.retaliation.keySet() ) {
            targetsList = this.retaliation.get( extorterId );
            if ( targetsList.contains( targetId ) ) {
              wasAttackedRetaliation = true;
            }
          }
          
          for ( Integer extorterId : this.attackProtection.keySet() ) {
            targetsList = this.attackProtection.get( extorterId );
            if ( targetsList.contains( targetId ) ) {
              attackedProtection = true;
            }
          }
          
          for ( Integer extorterId : this.attackRetaliation.keySet() ) {
            targetsList = this.attackRetaliation.get( extorterId );
            if ( targetsList.contains( targetId ) ) {
              attackedRetaliation = true;
            }
          }
          
          if ( (wasAttackedProtection) && (!attackedProtection)
              && (!attackedRetaliation) ) {
            removeProtectionList.add( targetId );
          } else if ( (wasAttackedRetaliation) && (!attackedProtection)
              && (!attackedRetaliation) ) {
            removeList.add( targetId );
          }
        }
      }
      
      for ( Integer targetId : removeProtectionList ) {
        this.extorted.remove( targetId );
        
        this.output.setNumRenounce( this.output.getNumRenounce() + 1 );
        
        target = this.targets.get( targetId );
        target.receiveInformRenounce();
      }
      
      for ( Integer targetId : removeList ) {
        this.extorted.remove( targetId );
      }
    }
  }
  
  
  /**
   * Receive information from opponents released Targets
   * 
   * @param newTargets
   *          List of targets that will be released from an opponent Extorter
   * @return none
   */
  public void receiveNewTargets( List<Integer> newTargets ) {
    for ( Integer targetId : newTargets ) {
      if ( !this.extorted.containsKey( targetId ) ) {
        this.extorted.put( targetId, new Demand( 0.0, 0.0 ) );
      }
    }
  }
  
  
  /**
   * Round end
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.97, interval = 1 )
  public void endRound() {
    // Remove non existent Targets from extorted list
    for ( Integer targetId : this.extorted.keySet() ) {
      if ( !this.targets.containsKey( targetId ) ) {
        this.targetsToRemove.add( targetId );
      }
    }
    
    for ( Integer targetId : this.targetsToRemove ) {
      if ( this.extorted.containsKey( targetId ) ) {
        this.extorted.remove( targetId );
      }
    }
    
    // Output
    this.output.setWealth( this.wealth );
    
    this.outputRecorder.addRecord( this.output );
  }
  
  
  /**
   * Remove the agent from the simulation
   * 
   * @param none
   * @return none
   */
  @SuppressWarnings ( "unchecked" )
  protected void die() {
    Context<ExtorterAbstract> agent = ContextUtils.getContext( this );
    if ( agent.size() > 1 ) {
      agent.remove( this );
    }
  }
}
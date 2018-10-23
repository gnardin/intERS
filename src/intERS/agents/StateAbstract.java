package intERS.agents;

import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.agents.entity.RankExtorter;
import intERS.conf.scenario.ScenarioConf;
import intERS.output.OutputObserver;
import intERS.output.OutputRecorder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ScheduledMethod;

public abstract class StateAbstract {
  
  protected static final String            TYPE = "0";
  
  protected int                            id;
  
  protected Context<Object>                context;
  
  protected Map<Integer, ExtorterAbstract> extorters;
  
  protected List<String>                   extorterTypes;
  
  protected Map<Integer, ExtorterAbstract> imprisoned;
  
  protected int                            imprisonedTotal;
  
  protected OutputRecorder                 outputRecorder;
  
  protected double                         prisonProbability;
  
  protected int                            prisonRounds;
  
  protected Map<Integer, Integer>          remainedRounds;
  
  protected ScenarioConf                   scenarioConf;
  
  protected Map<Integer, TargetAbstract>   targets;
  
  protected List<String>                   targetTypes;
  
  protected OutputObserver                 output;
  
  
  /**
   * Constructor
   * 
   * @param scenarioConf
   *          Scenario configuration
   * @param context
   *          Repast context
   * @param extorters
   *          Extorters list
   * @param targets
   *          Targets list
   * @param id
   *          State identification
   * @param prisonProbability
   *          Prison probability
   * @param prisonRounds
   *          Number of rounds to keep in Extorter in prison
   */
  public StateAbstract( ScenarioConf scenarioConf, Context<Object> context, Map<Integer, ExtorterAbstract> extorters, Map<Integer, TargetAbstract> targets, Integer id, Double prisonProbability, Integer prisonRounds ) {
    this.id = id;
    this.scenarioConf = scenarioConf;
    this.context = context;
    
    this.extorters = extorters;
    this.extorterTypes = new ArrayList<String>();
    ExtorterAbstract extorter;
    for ( Integer extorterId : this.extorters.keySet() ) {
      extorter = this.extorters.get( extorterId );
      
      if ( !this.extorterTypes.contains( extorter.getType() ) ) {
        this.extorterTypes.add( extorter.getType() );
      }
    }
    
    this.targets = targets;
    this.targetTypes = new ArrayList<String>();
    TargetAbstract target;
    for ( Integer targetId : this.targets.keySet() ) {
      target = this.targets.get( targetId );
      
      if ( !this.targetTypes.contains( target.getType() ) ) {
        this.targetTypes.add( target.getType() );
      }
    }
    
    this.imprisoned = new HashMap<Integer, ExtorterAbstract>();
    this.imprisonedTotal = 0;
    this.prisonProbability = prisonProbability / 100;
    this.prisonRounds = prisonRounds;
    this.remainedRounds = new HashMap<Integer, Integer>();
    
    // Output
    this.outputRecorder = OutputRecorder.getInstance();
    this.output = new OutputObserver( 0, TYPE, this.id );
    
    String type;
    int num = 0;
    for ( Integer targetId : this.targets.keySet() ) {
      target = this.targets.get( targetId );
      
      type = target.getType();
      num = this.output.getNumTargetsAlive( type );
      this.output.setNumTargetsAlive( type, num + 1 );
      
      num = this.output.getNumTargetsSurvived( type );
      this.output.setNumTargetsSurvived( type, num + 1 );
    }
    
    for ( Integer extorterId : this.extorters.keySet() ) {
      extorter = this.extorters.get( extorterId );
      
      type = extorter.getType();
      this.output.setNumExtortersFree( type,
          this.output.getNumExtortersFree( type ) + 1 );
    }
    
    for ( Integer extorterId : this.imprisoned.keySet() ) {
      extorter = this.imprisoned.get( extorterId );
      
      type = extorter.getType();
      this.output.setNumExtortersImprisoned( type,
          this.output.getNumExtortersImprisoned( type ) + 1 );
    }
    
    this.outputRecorder.addRecord( this.output );
  }
  
  
  /**
   * Begin round
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1, interval = 1, priority = 0 )
  public void beginRound() {
    boolean stop = false;
    Evaluator eval = new Evaluator();
    
    int round = (int) RunEnvironment.getInstance().getCurrentSchedule()
        .getTickCount();
    
    try {
      eval.putVariable( "CYCLE", new Integer( round - 1 ).toString() );
      eval.putVariable( "EXTORTERS",
          new Integer( this.extorters.size() + this.imprisonedTotal )
              .toString() );
      
      List<String> extorterTypesList = new ArrayList<String>();
      ExtorterAbstract extorter;
      for ( Integer extorterId : this.extorters.keySet() ) {
        extorter = this.extorters.get( extorterId );
        
        if ( !extorterTypesList.contains( extorter.getType() ) ) {
          extorterTypesList.add( extorter.getType() );
        }
      }
      for ( Integer extorterId : this.imprisoned.keySet() ) {
        extorter = this.imprisoned.get( extorterId );
        
        if ( !extorterTypesList.contains( extorter.getType() ) ) {
          extorterTypesList.add( extorter.getType() );
        }
      }
      eval.putVariable( "EXTORTER_TYPES",
          new Integer( extorterTypesList.size() ).toString() );
      
      eval.putVariable( "TARGETS",
          new Integer( this.targets.size() ).toString() );
      
      List<String> targetTypesList = new ArrayList<String>();
      TargetAbstract target;
      for ( Integer targetId : this.targets.keySet() ) {
        target = this.targets.get( targetId );
        
        if ( !targetTypesList.contains( target.getType() ) ) {
          targetTypesList.add( target.getType() );
        }
      }
      eval.putVariable( "TARGET_TYPES",
          new Integer( targetTypesList.size() ).toString() );
      
      stop = new Double(
          eval.evaluate( this.scenarioConf.getStopAt() ) ) == 1.0;
      if ( stop ) {
        RunEnvironment.getInstance().getCurrentSchedule().setFinishing( true );
      }
    } catch ( EvaluationException e ) {
      e.printStackTrace();
    }
    
    this.output = new OutputObserver( round, TYPE, this.id );
    
    for ( String type : this.targetTypes ) {
      this.output.setNumTargetsAlive( type, 0 );
    }
    
    String type;
    TargetAbstract target;
    for ( Integer targetId : this.targets.keySet() ) {
      target = this.targets.get( targetId );
      
      type = target.getType();
      this.output.setNumTargetsAlive( type,
          this.output.getNumTargetsAlive( type ) + 1 );
    }
    
    RankExtorter.getInstance().calcExtorterPunishmentProb( this.extorters );
  }
  
  
  /**
   * Decide to arrest or release an Extorter
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.98, interval = 1 )
  public abstract void decideToArrestRelease();
  
  
  /**
   * Round end
   * 
   * @param none
   * @return none
   */
  @ScheduledMethod ( start = 1.99, interval = 1 )
  public void endRound() {
    for ( String type : this.targetTypes ) {
      this.output.setNumTargetsSurvived( type, 0 );
    }
    
    for ( String type : this.extorterTypes ) {
      this.output.setNumExtortersFree( type, 0 );
      this.output.setNumExtortersImprisoned( type, 0 );
    }
    
    String type;
    TargetAbstract target;
    for ( Integer targetId : this.targets.keySet() ) {
      target = this.targets.get( targetId );
      
      type = target.getType();
      this.output.setNumTargetsSurvived( type,
          this.output.getNumTargetsSurvived( type ) + 1 );
    }
    
    ExtorterAbstract extorter;
    for ( Integer extorterId : this.extorters.keySet() ) {
      extorter = this.extorters.get( extorterId );
      
      type = extorter.getType();
      this.output.setNumExtortersFree( type,
          this.output.getNumExtortersFree( type ) + 1 );
    }
    
    for ( Integer extorterId : this.imprisoned.keySet() ) {
      extorter = this.imprisoned.get( extorterId );
      
      type = extorter.getType();
      this.output.setNumExtortersImprisoned( type,
          this.output.getNumExtortersImprisoned( type ) + 1 );
    }
    
    this.outputRecorder.addRecord( this.output );
  }
}
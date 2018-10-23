package intERS.main;

import intERS.agents.ExtorterAbstract;
import intERS.agents.StateAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.ExtorterConf;
import intERS.conf.scenario.ScenarioConf;
import intERS.conf.scenario.StrategyConf;
import intERS.conf.scenario.TargetConf;
import intERS.conf.simulation.OutputConf;
import intERS.output.OutputRecorder;
import intERS.utils.XML;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import repast.simphony.context.Context;
import repast.simphony.context.ContextEvent;
import repast.simphony.context.ContextListener;
import repast.simphony.context.DefaultContext;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;

public class IntERSBuilder extends DefaultContext<Object>
    implements ContextBuilder<Object> {
  
  private static final String            PROJECT_NAME                   = "intERS";
  
  private static final String            PARAM_SIM                      = "simulationRun";
  
  private static final String            PARAM_RANDOM_SEED              = "randomSeed";
  
  private static final String            PARAM_SCENARIO_FILENAME        = "scenarioFilename";
  
  private static final String            PARAM_SCHEMA_FILENAME          = "schemaFilename";
  
  // Output
  private final static String            PARAM_OUTPUT_DIRECTORY         = "outputDirectory";
  
  private final static String            PARAM_OUTPUT_FILENAME_EXTORTER = "outputFileExtorter";
  
  private final static String            PARAM_OUTPUT_FILENAME_OBSERVER = "outputFileObserver";
  
  private final static String            PARAM_OUTPUT_FILENAME_TARGET   = "outputFileTarget";
  
  private final static String            PARAM_OUTPUT_FILE_APPEND       = "outputFileAppend";
  
  private final static String            PARAM_OUTPUT_FIELD_SEPARATOR   = "outputFieldSeparator";
  
  private final static String            PARAM_OUTPUT_WRITE_EVERY       = "outputWriteEvery";
  
  private Map<Integer, ExtorterAbstract> extorters;
  
  private Map<Integer, TargetAbstract>   targets;
  
  
  @Override
  public Context<Object> build( Context<Object> context ) {
    context.setId( PROJECT_NAME );
    
    this.extorters = new Hashtable<Integer, ExtorterAbstract>();
    this.targets = new Hashtable<Integer, TargetAbstract>();
    
    // Get the parameter values
    Parameters params = RunEnvironment.getInstance().getParameters();
    Integer simulationRun = (Integer) params.getValue( PARAM_SIM );
    Integer randomSeed = (Integer) params.getValue( PARAM_RANDOM_SEED );
    RandomHelper.setSeed( randomSeed );
    
    String scenarioFilename = (String) params
        .getValue( PARAM_SCENARIO_FILENAME );
    String schemaFilename = (String) params.getValue( PARAM_SCHEMA_FILENAME );
    
    OutputConf outputConf = new OutputConf();
    outputConf
        .setDirectory( ((String) params.getValue( PARAM_OUTPUT_DIRECTORY ))
            + File.separator + simulationRun );
    outputConf.setFileExtorter(
        (String) params.getValue( PARAM_OUTPUT_FILENAME_EXTORTER ) );
    outputConf.setFileObserver(
        (String) params.getValue( PARAM_OUTPUT_FILENAME_OBSERVER ) );
    outputConf.setFileTarget(
        (String) params.getValue( PARAM_OUTPUT_FILENAME_TARGET ) );
    outputConf
        .setFileAppend( (Boolean) params.getValue( PARAM_OUTPUT_FILE_APPEND ) );
    outputConf.setFieldSeparator(
        (String) params.getValue( PARAM_OUTPUT_FIELD_SEPARATOR ) );
    outputConf
        .setWriteEvery( (Integer) params.getValue( PARAM_OUTPUT_WRITE_EVERY ) );
    
    // If the file does not exist, exit with an error
    if ( !(new File( scenarioFilename )).exists() ) {
      System.out.println( "Scenario file does not exist" );
      System.exit( 1 );
    }
    
    // Validate the Scenario XML file
    if ( !XML.isValid( scenarioFilename, schemaFilename ) ) {
      System.out.println( "Invalid XML" );
      System.exit( 1 );
    }
    ScenarioConf scenario = ScenarioConf.scenarioParser( scenarioFilename );
    
    // Associate the ContextListener to the Context
    context.addContextListener(
        new ContextUpdate( this.extorters, this.targets ) );
    
    // Create Target agents
    List<TargetConf> targetsScenario = scenario.getTargets();
    
    int id = 1;
    try {
      TargetAbstract target;
      for ( TargetConf targetConf : targetsScenario ) {
        
        @SuppressWarnings ( "unchecked" )
        Class<TargetAbstract> tClass = (Class<TargetAbstract>) Class
            .forName( targetConf.getTargetClass() );
        
        Constructor<TargetAbstract> tConstructor = tClass
            .getDeclaredConstructor( Map.class, Map.class, Integer.class,
                TargetConf.class );
        
        for ( int i = 0; i < targetConf.getNumberTargets(); i++ ) {
          target = tConstructor.newInstance( this.extorters, this.targets, id,
              targetConf );
          context.add( target );
          
          id++;
        }
      }
    } catch ( ClassNotFoundException e ) {
      e.printStackTrace();
    } catch ( NoSuchMethodException e ) {
      e.printStackTrace();
    } catch ( InvocationTargetException e ) {
      e.printStackTrace();
    } catch ( IllegalAccessException e ) {
      e.printStackTrace();
    } catch ( InstantiationException e ) {
      e.printStackTrace();
    }
    
    // Calculate the number of Targets per Extorter
    List<ExtorterConf> extortersScenario = scenario.getExtorters();
    
    StrategyConf strategyConf = new StrategyConf( simulationRun );
    
    int numExtorters = 0;
    for ( ExtorterConf extorterConf : extortersScenario ) {
      numExtorters += extorterConf.getNumberExtorters();
      
      strategyConf.upload( (String) params.getValue( PARAM_OUTPUT_DIRECTORY ),
          (String) params.getValue( PARAM_OUTPUT_FIELD_SEPARATOR ),
          extorterConf );
      
      extorterConf.setExtortersStrategy( strategyConf );
    }
    
    int targetsPerExtorter = 0;
    int numTargets = this.targets.size();
    
    Evaluator eval = new Evaluator();
    try {
      eval.putVariable( "TARGETS", new Integer( numTargets ).toString() );
      eval.putVariable( "EXTORTERS", new Integer( numExtorters ).toString() );
      
      targetsPerExtorter = new Double(
          eval.evaluate( scenario.getTargetPerExtorter() ) ).intValue();
      if ( targetsPerExtorter > numTargets ) {
        targetsPerExtorter = numTargets;
      }
    } catch ( EvaluationException e ) {
      e.printStackTrace();
      System.exit( 1 );
    }
    
    // Assign Extorters to Targets
    int totalNumExtorters = 0;
    for ( ExtorterConf extorterConf : extortersScenario ) {
      totalNumExtorters += extorterConf.getNumberExtorters();
    }
    
    Map<Integer, Set<Integer>> extorterTargets = new HashMap<Integer, Set<Integer>>();
    Set<Integer> targetsExtorter;
    
    Map<Integer, Integer> numTargetExtorters = new HashMap<Integer, Integer>();
    for ( Integer targetId : this.targets.keySet() ) {
      numTargetExtorters.put( targetId, 0 );
    }
    
    /**
     * DEFINE THAT ALL TARGETS ARE EXTORTED BY MORE THAN ONE EXTORTER AND ALL
     * EXTORTERS HAVE THE SAME NUMBER OF TARGETS ASSIGNED TO THEM
     */
    int targetId;
    boolean targetAssigned;
    boolean allTargetsExtortedByMoreThanTwo = false;
    double prob;
    do {
      for ( int extorterId = 1; extorterId <= totalNumExtorters; extorterId++ ) {
        
        if ( extorterTargets.containsKey( extorterId ) ) {
          targetsExtorter = extorterTargets.get( extorterId );
        } else {
          targetsExtorter = new HashSet<Integer>();
        }
        
        targetAssigned = false;
        do {
          targetId = RandomHelper.nextIntFromTo( 1, numTargets );
          
          if ( !targetsExtorter.contains( targetId ) ) {
            
            if ( numTargetExtorters.get( targetId ) > 0 ) {
              prob = (double) 1.0 / (double) numTargetExtorters.get( targetId );
            } else {
              prob = 1.0;
            }
            
            if ( RandomHelper.nextDouble() <= prob ) {
              targetsExtorter.add( targetId );
              extorterTargets.put( extorterId, targetsExtorter );
              targetAssigned = true;
              
              numTargetExtorters.put( targetId,
                  numTargetExtorters.get( targetId ) + 1 );
            }
          }
        } while ( !targetAssigned );
      }
      
      allTargetsExtortedByMoreThanTwo = true;
      for ( Integer tId : numTargetExtorters.keySet() ) {
        if ( numTargetExtorters.get( tId ) < 2 ) {
          allTargetsExtortedByMoreThanTwo = false;
        }
      }
    } while ( !allTargetsExtortedByMoreThanTwo );
    
    // Create Extorter agents
    Set<Integer> initialTargets;
    ExtorterAbstract extorter;
    id = 1;
    try {
      for ( ExtorterConf extorterConf : extortersScenario ) {
        
        @SuppressWarnings ( "unchecked" )
        Class<ExtorterAbstract> eClass = (Class<ExtorterAbstract>) Class
            .forName( extorterConf.getExtorterClass() );
        
        Constructor<ExtorterAbstract> eConstructor = eClass
            .getDeclaredConstructor( Map.class, Map.class, Set.class,
                Integer.class, ExtorterConf.class );
        
        numExtorters = extorterConf.getNumberExtorters();
        for ( int i = 0; i < numExtorters; i++ ) {
          initialTargets = extorterTargets.get( id );
          
          extorter = eConstructor.newInstance( this.extorters, this.targets,
              initialTargets, id, extorterConf );
          
          this.extorters.put( id, extorter );
          context.add( extorter );
          
          id++;
        }
      }
    } catch ( ClassNotFoundException e ) {
      e.printStackTrace();
    } catch ( NoSuchMethodException e ) {
      e.printStackTrace();
    } catch ( InvocationTargetException e ) {
      e.printStackTrace();
    } catch ( IllegalAccessException e ) {
      e.printStackTrace();
    } catch ( InstantiationException e ) {
      e.printStackTrace();
    }
    
    // Create a State
    try {
      @SuppressWarnings ( "unchecked" )
      Class<StateAbstract> sClass = (Class<StateAbstract>) Class
          .forName( scenario.getState().getStateClass() );
      
      Constructor<StateAbstract> sConstructor = sClass.getDeclaredConstructor(
          ScenarioConf.class, Context.class, Map.class, Map.class,
          Integer.class, Double.class, Integer.class );
      
      StateAbstract state = sConstructor.newInstance( scenario, context,
          this.extorters, this.targets, 1,
          scenario.getState().getPrisonProbability(),
          scenario.getState().getPrisonRounds() );
      context.add( state );
    } catch ( ClassNotFoundException e ) {
      e.printStackTrace();
    } catch ( NoSuchMethodException e ) {
      e.printStackTrace();
    } catch ( InvocationTargetException e ) {
      e.printStackTrace();
    } catch ( IllegalAccessException e ) {
      e.printStackTrace();
    } catch ( InstantiationException e ) {
      e.printStackTrace();
    }
    
    // Schedule the output writing
    ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
    ScheduleParameters scheduleRep = ScheduleParameters.createRepeating(
        outputConf.getWriteEvery(), outputConf.getWriteEvery() );
    ScheduleParameters scheduleEnd = ScheduleParameters.createAtEnd( 99 );
    
    OutputRecorder output = OutputRecorder.getInstance();
    output.setOutput( outputConf );
    schedule.schedule( scheduleRep, output, "write" );
    schedule.schedule( scheduleEnd, output, "close" );
    
    return context;
  }
}



/**
 * Update dynamically the Extorters and Targets available in the simulation
 */
class ContextUpdate implements ContextListener<Object> {
  
  Map<Integer, ExtorterAbstract> extorters;
  
  Map<Integer, TargetAbstract>   targets;
  
  
  public ContextUpdate( Map<Integer, ExtorterAbstract> extorters, Map<Integer, TargetAbstract> targets ) {
    this.extorters = extorters;
    this.targets = targets;
  }
  
  
  @Override
  public void eventOccured( ContextEvent<Object> event ) {
    if ( event.getType().equals( ContextEvent.REMOVED ) ) {
      Object obj = event.getTarget();
      if ( obj instanceof ExtorterAbstract ) {
        this.extorters.remove( ((ExtorterAbstract) obj).getId() );
      } else if ( obj instanceof TargetAbstract ) {
        this.targets.remove( ((TargetAbstract) obj).getId() );
      }
    } else if ( event.getType().equals( ContextEvent.ADDED ) ) {
      Object obj = event.getTarget();
      if ( obj instanceof ExtorterAbstract ) {
        ExtorterAbstract extorter = (ExtorterAbstract) obj;
        this.extorters.put( extorter.getId(), extorter );
      } else if ( obj instanceof TargetAbstract ) {
        TargetAbstract target = (TargetAbstract) obj;
        this.targets.put( target.getId(), target );
      }
    }
  }
}
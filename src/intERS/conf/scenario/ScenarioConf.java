package intERS.conf.scenario;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class ScenarioConf {
  
  private final static String SCENARIO                          = "scenario";
  
  private final static String STOP_AT                           = "stopAt";
  
  // State
  private final static String STATE                             = "state";
  
  private final static String STATE_CLASS                       = "stateClass";
  
  private final static String STATE_PRISON_PROBABILITY          = "prisonProbability";
  
  private final static String STATE_PRISON_ROUNDS               = "prisonRounds";
  
  // Target
  private final static String TARGET                            = "target";
  
  private final static String TARGET_CLASS                      = "targetClass";
  
  private final static String TARGET_TYPE                       = "targetType";
  
  private final static String TARGET_NUMBER                     = "numberOfTargets";
  
  private final static String TARGET_INITIAL_WEALTH             = "initialTargetWealth";
  
  private final static String TARGET_EXTORTERS                  = "extorterPerTarget";
  
  private final static String TARGET_MINIMUM_INCOME             = "minimumIncome";
  
  private final static String TARGET_MAXIMUM_INCOME             = "maximumIncome";
  
  private final static String TARGET_MINIMUM_PUBLISHED_INCOME   = "minimumIncomeVariation";
  
  private final static String TARGET_MAXIMUM_PUBLISHED_INCOME   = "maximumIncomeVariation";
  
  private final static String TARGET_AVAILABLE_EXTORTION_INCOME = "availableExtortionIncome";
  
  private final static String TARGET_MEMORY_LENGTH              = "memoryLength";
  
  private final static String TARGET_UNKNOWN_PROTECTION_PROB    = "unknownProtectionProb";
  
  private final static String TARGET_UNKNOWN_PUNISHMENT_PROB    = "unknownPunishmentProb";
  
  // Extorter
  private final static String TARGET_PER_EXTORTERS              = "targetPerExtorter";
  
  private final static String EXTORTER                          = "extorter";
  
  private final static String EXTORTER_CLASS                    = "extorterClass";
  
  private final static String EXTORTER_TYPE                     = "extorterType";
  
  private final static String EXTORTER_ENLARGEMENT_PROBABILITY  = "enlargementProbability";
  
  private final static String EXTORTER_NUMBER                   = "numberOfExtorters";
  
  private final static String EXTORTER_INITIAL_WEALTH           = "initialExtorterWealth";
  
  private final static String EXTORTER_IMPULSE_PROTECTION       = "impulseProtection";
  
  private final static String EXTORTER_IMPULSE_FIGHT_PROTECTION = "impulseFightProtection";
  
  private final static String EXTORTER_IMPULSE_RETALIATION      = "impulseRetaliation";
  
  private final static String EXTORTER_IMPULSE_FIGHT_RETALITION = "impulseFightRetaliation";
  
  private final static String EXTORTER_COST_FIGHT_PROTECTION    = "costOfFightProtection";
  
  private final static String EXTORTER_COST_FIGHT_RETALIATION   = "costOfFightRetaliation";
  
  private final static String EXTORTER_COST_PUNISH              = "costOfPunish";
  
  private final static String ENABLE_EXTORTER_CFG               = "enableExtortersCfg";
  
  private final static String EXTORTER_CFG_FILENAME             = "extortersCfgFilename";
  
  private final static String EXTORTER_MINIMUM_EXTORT           = "minimumExtort";
  
  private final static String EXTORTER_MAXIMUM_EXTORT           = "maximumExtort";
  
  private final static String EXTORTER_STEP_EXTORT              = "stepExtort";
  
  private final static String EXTORTER_MINIMUM_PUNISH           = "minimumPunish";
  
  private final static String EXTORTER_MAXIMUM_PUNISH           = "maximumPunish";
  
  private final static String EXTORTER_STEP_PUNISH              = "stepPunish";
  
  private final static String EXTORTER_UPDATE_AT_END            = "updateAtEnd";
  
  // Scenario
  private String              stopAt;
  
  // State information
  private StateConf           state;
  
  // Target information
  private List<TargetConf>    targets;
  
  // Extorter information
  private List<ExtorterConf>  extorters;
  
  // Rule defining Target per Extorter
  private String              targetPerExtorter;
  
  
  public ScenarioConf() {
    this.extorters = new ArrayList<ExtorterConf>();
    this.targets = new ArrayList<TargetConf>();
    this.state = new StateConf();
  }
  
  
  public static ScenarioConf scenarioParser( String scenarioFilename ) {
    ScenarioConf scenario = null;
    ExtorterConf extorter = null;
    TargetConf target = null;
    StateConf state = null;
    
    try {
      // First create a new XMLInputFactory
      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
      
      // Setup a new eventReader
      InputStream in = new FileInputStream( scenarioFilename );
      XMLEventReader eventReader = inputFactory.createXMLEventReader( in );
      
      // Read the XML document
      XMLEvent event;
      StartElement startElement;
      EndElement endElement;
      while ( eventReader.hasNext() ) {
        event = eventReader.nextEvent();
        
        if ( event.isStartElement() ) {
          startElement = event.asStartElement();
          
          // Create a Scenario
          if ( startElement.getName().getLocalPart() == (SCENARIO) ) {
            scenario = new ScenarioConf();
            continue;
            
            // Set stopAt attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( STOP_AT ) ) {
            event = eventReader.nextEvent();
            scenario.setStopAt( event.asCharacters().getData() );
            continue;
            
            // Create a State
          } else if ( startElement.getName().getLocalPart() == (STATE) ) {
            state = new StateConf();
            continue;
            
            // Set observerClass attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( STATE_CLASS ) ) {
            event = eventReader.nextEvent();
            state.setStateClass( event.asCharacters().getData() );
            continue;
            
            // Set prisonProbability attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( STATE_PRISON_PROBABILITY ) ) {
            event = eventReader.nextEvent();
            state.setPrisonProbability(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set prisonRounds attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( STATE_PRISON_ROUNDS ) ) {
            event = eventReader.nextEvent();
            state.setPrisonRounds(
                new Integer( event.asCharacters().getData() ) );
            continue;
            
            // Create a Target
          } else if ( startElement.getName().getLocalPart() == (TARGET) ) {
            target = new TargetConf();
            continue;
            
            // Set targetClass attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_CLASS ) ) {
            event = eventReader.nextEvent();
            target.setTargetClass( event.asCharacters().getData() );
            continue;
            
            // Set targetType attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_TYPE ) ) {
            event = eventReader.nextEvent();
            target.setType( event.asCharacters().getData() );
            continue;
            
            // Set numberTargets attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_NUMBER ) ) {
            event = eventReader.nextEvent();
            target.setNumberTargets(
                new Integer( event.asCharacters().getData() ) );
            continue;
            
            // Set initialWealth attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_INITIAL_WEALTH ) ) {
            event = eventReader.nextEvent();
            target.setInitialWealth(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set extorterPerTarget attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_EXTORTERS ) ) {
            event = eventReader.nextEvent();
            target.setExtorterPerTarget(
                new Integer( event.asCharacters().getData() ) );
            continue;
            
            // Set MinIncome attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_MINIMUM_INCOME ) ) {
            event = eventReader.nextEvent();
            target.setMinIncome( new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set MaxIncome attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_MAXIMUM_INCOME ) ) {
            event = eventReader.nextEvent();
            target.setMaxIncome( new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set MinIncomeVariation attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_MINIMUM_PUBLISHED_INCOME ) ) {
            event = eventReader.nextEvent();
            target.setMinIncomeVariation(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set MaxIncomeVariation attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_MAXIMUM_PUBLISHED_INCOME ) ) {
            event = eventReader.nextEvent();
            target.setMaxIncomeVariation(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set availableExtortionIncome attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_AVAILABLE_EXTORTION_INCOME ) ) {
            event = eventReader.nextEvent();
            target.setAvailExtortionIncome(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set MemLength attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_MEMORY_LENGTH ) ) {
            event = eventReader.nextEvent();
            target
                .setMemLength( new Integer( event.asCharacters().getData() ) );
            continue;
            
            // Set unknownProtectionProb attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_UNKNOWN_PROTECTION_PROB ) ) {
            event = eventReader.nextEvent();
            target.setUnknownProtectionProb(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set unknownPunishmentProb attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_UNKNOWN_PUNISHMENT_PROB ) ) {
            event = eventReader.nextEvent();
            target.setUnknownPunishmentProb(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set targetPerExtorter attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( TARGET_PER_EXTORTERS ) ) {
            event = eventReader.nextEvent();
            scenario.setTargetPerExtorter( event.asCharacters().getData() );
            continue;
            
            // Create a Extorter
          } else if ( startElement.getName().getLocalPart() == (EXTORTER) ) {
            extorter = new ExtorterConf();
            
            // Set extorterClass attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_CLASS ) ) {
            event = eventReader.nextEvent();
            extorter.setExtorterClass( event.asCharacters().getData() );
            continue;
            
            // Set extorterType attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_TYPE ) ) {
            event = eventReader.nextEvent();
            extorter.setType( event.asCharacters().getData() );
            continue;
            
            // Set extorterEnlargementProbability attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_ENLARGEMENT_PROBABILITY ) ) {
            event = eventReader.nextEvent();
            extorter
                .setEnlargementProbability( event.asCharacters().getData() );
            continue;
            
            // Set extorterNumber attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_NUMBER ) ) {
            event = eventReader.nextEvent();
            extorter.setNumberExtorters(
                new Integer( event.asCharacters().getData() ) );
            continue;
            
            // Set initWealth attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_INITIAL_WEALTH ) ) {
            event = eventReader.nextEvent();
            extorter.setInitialWealth(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set impulseProtection attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_IMPULSE_PROTECTION ) ) {
            event = eventReader.nextEvent();
            extorter.setImpulseProtection(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set impulseFightProtection attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_IMPULSE_FIGHT_PROTECTION ) ) {
            event = eventReader.nextEvent();
            extorter.setImpulseFightProtection(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set impulseRetaliation attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_IMPULSE_RETALIATION ) ) {
            event = eventReader.nextEvent();
            extorter.setImpulseRetaliation(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set impulseFightRetaliation attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_IMPULSE_FIGHT_RETALITION ) ) {
            event = eventReader.nextEvent();
            extorter.setImpulseFightRetaliation(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set costFightProtection attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_COST_FIGHT_PROTECTION ) ) {
            event = eventReader.nextEvent();
            extorter.setCostFightProtection(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set costFightRetaliation attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_COST_FIGHT_RETALIATION ) ) {
            event = eventReader.nextEvent();
            extorter.setCostFightRetaliation(
                new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set costPunish attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_COST_PUNISH ) ) {
            event = eventReader.nextEvent();
            extorter
                .setCostPunish( new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set extortersCfgFile attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( ENABLE_EXTORTER_CFG ) ) {
            event = eventReader.nextEvent();
            extorter.setEnableExtortersCfg(
                new Boolean( event.asCharacters().getData() ) );
            continue;
            
            // Set extortersCfgFilename attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_CFG_FILENAME ) ) {
            event = eventReader.nextEvent();
            extorter.setExtortersCfgFilename( event.asCharacters().getData() );
            continue;
            
            // Set minExtort attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_MINIMUM_EXTORT ) ) {
            event = eventReader.nextEvent();
            extorter
                .setMinExtort( new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set maxExtort attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_MAXIMUM_EXTORT ) ) {
            event = eventReader.nextEvent();
            extorter
                .setMaxExtort( new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set stepExtort attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_STEP_EXTORT ) ) {
            event = eventReader.nextEvent();
            extorter
                .setStepExtort( new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set minPunish attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_MINIMUM_PUNISH ) ) {
            event = eventReader.nextEvent();
            extorter
                .setMinPunish( new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set maxPunish attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_MAXIMUM_PUNISH ) ) {
            event = eventReader.nextEvent();
            extorter
                .setMaxPunish( new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set stepPunish attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_STEP_PUNISH ) ) {
            event = eventReader.nextEvent();
            extorter
                .setStepPunish( new Double( event.asCharacters().getData() ) );
            continue;
            
            // Set receiveAtEnd attribute
          } else if ( event.asStartElement().getName().getLocalPart()
              .equals( EXTORTER_UPDATE_AT_END ) ) {
            event = eventReader.nextEvent();
            extorter.setUpdateAtEnd(
                new Boolean( event.asCharacters().getData() ) );
            continue;
          }
        }
        
        // If we reach the end of an item element we add it to the list
        if ( event.isEndElement() ) {
          endElement = event.asEndElement();
          if ( endElement.getName().getLocalPart() == (EXTORTER) ) {
            scenario.addExtorter( extorter );
          } else if ( endElement.getName().getLocalPart() == (TARGET) ) {
            scenario.addTarget( target );
          } else if ( endElement.getName().getLocalPart() == (STATE) ) {
            scenario.setState( state );
          }
        }
      }
      
      // Close the file
      in.close();
      
    } catch ( FileNotFoundException e ) {
      e.printStackTrace();
    } catch ( IOException e ) {
      e.printStackTrace();
    } catch ( XMLStreamException e ) {
      e.printStackTrace();
    }
    
    return scenario;
  }
  
  
  public String getStopAt() {
    return this.stopAt;
  }
  
  
  public void setStopAt( String stopAt ) {
    this.stopAt = stopAt;
  }
  
  
  public StateConf getState() {
    return this.state;
  }
  
  
  public void setState( StateConf state ) {
    this.state = state;
  }
  
  
  public List<TargetConf> getTargets() {
    return this.targets;
  }
  
  
  private void addTarget( TargetConf target ) {
    this.targets.add( target );
  }
  
  
  public String getTargetPerExtorter() {
    return this.targetPerExtorter;
  }
  
  
  private void setTargetPerExtorter( String targetPerExtorter ) {
    this.targetPerExtorter = targetPerExtorter;
  }
  
  
  public List<ExtorterConf> getExtorters() {
    return this.extorters;
  }
  
  
  private void addExtorter( ExtorterConf extorter ) {
    this.extorters.add( extorter );
  }
  
  
  @Override
  public String toString() {
    String str = new String();
    
    str += state.toString();
    
    for ( TargetConf target : this.targets ) {
      str += target.toString();
    }
    
    str += "EXTORTERS\n";
    str += "Targets per Extorter.........: [" + this.targetPerExtorter + "]\n";
    
    for ( ExtorterConf extorter : this.extorters ) {
      str += extorter.toString();
    }
    
    return str;
  }
}
package intERS.impl.state;

import intERS.agents.ExtorterAbstract;
import intERS.agents.StateAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.ScenarioConf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import repast.simphony.context.Context;
import repast.simphony.random.RandomHelper;

public class StateAgent extends StateAbstract {
  
  
  public StateAgent(ScenarioConf scenarioConf, Context<Object> context,
      Map<Integer, ExtorterAbstract> extorters,
      Map<Integer, TargetAbstract> targets, Integer id,
      Double prisonProbability, Integer prisonRounds) {
    super(scenarioConf, context, extorters, targets, id, prisonProbability,
        prisonRounds);
  }
  
  
  @Override
  public void decideToArrestRelease() {
    
    // Determine the Extorters to arrest
    ExtorterAbstract extorter;
    Collection<ExtorterAbstract> removeFromContext = new ArrayList<ExtorterAbstract>();
    for(Integer extorterId : this.extorters.keySet()) {
      if(RandomHelper.nextDouble() < this.prisonProbability) {
        extorter = this.extorters.get(extorterId);
        
        this.imprisoned.put(extorterId, extorter);
        this.remainedRounds.put(extorterId, this.prisonRounds);
        removeFromContext.add(extorter);
        
        this.imprisonedTotal++;
      }
    }
    this.context.removeAll(removeFromContext);
    
    // Determine the Extorters to release from prison
    int rounds;
    Collection<Integer> removeFromPrison = new ArrayList<Integer>();
    for(Integer extorterId : this.remainedRounds.keySet()) {
      rounds = this.remainedRounds.get(extorterId) - 1;
      if(rounds < 0) {
        // Release from prison
        removeFromPrison.add(extorterId);
        // Put it back to the simulation
        extorter = this.imprisoned.get(extorterId);
        this.context.add(extorter);
        
        this.imprisonedTotal--;
      } else {
        this.remainedRounds.put(extorterId, rounds);
      }
    }
    
    for(Integer extorterId : removeFromPrison) {
      this.imprisoned.remove(extorterId);
      this.remainedRounds.remove(extorterId);
    }
  }
}
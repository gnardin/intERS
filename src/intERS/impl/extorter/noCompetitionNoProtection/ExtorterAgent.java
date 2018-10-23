package intERS.impl.extorter.noCompetitionNoProtection;

import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.conf.scenario.ExtorterConf;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExtorterAgent extends ExtorterAbstract {
  
  public ExtorterAgent( Map<Integer, ExtorterAbstract> extorters, Map<Integer, TargetAbstract> targets, Set<Integer> initialTargets, Integer id, ExtorterConf extorterConf ) {
    super( extorters, targets, initialTargets, id, extorterConf );
  }
  
  
  @Override
  public void decidePunishment() {
    for ( Integer targetId : this.extorted.keySet() ) {
      if ( !this.paid.containsKey( targetId ) ) {
        
        if ( !this.punishments.contains( targetId ) ) {
          this.punishments.add( targetId );
        }
      }
    }
  }
  
  
  @Override
  public void decideProtection() {
    List<Integer> nonProtectionList;
    for ( Integer targetId : this.targetExtorter.keySet() ) {
      
      // Protect only if paid
      if ( this.paid.containsKey( targetId ) ) {
        
        for ( Integer extorterId : this.targetExtorter.get( targetId ) ) {
          if ( this.nonAttackProtection.containsKey( extorterId ) ) {
            nonProtectionList = this.nonAttackProtection.get( extorterId );
          } else {
            nonProtectionList = new ArrayList<Integer>();
          }
          
          nonProtectionList.add( targetId );
          this.nonAttackProtection.put( extorterId, nonProtectionList );
        }
      }
    }
  }
  
  
  @Override
  public void decideCounterattackProtection() {
  }
  
  
  @Override
  public void decideRetaliation() {
    List<Integer> targetsList;
    for ( Integer extorterId : this.extorterTarget.keySet() ) {
      targetsList = this.extorterTarget.get( extorterId );
      
      this.nonAttackRetaliation.put( extorterId, targetsList );
    }
  }
  
  
  @Override
  public void decideCounterattackRetaliation() {
  }
}
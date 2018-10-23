package intERS.impl.target;

import intERS.agents.ExtorterAbstract;
import intERS.agents.TargetAbstract;
import intERS.agents.entity.RankExtorter;
import intERS.conf.scenario.TargetConf;
import intERS.utils.Sort;
import java.util.HashMap;
import java.util.Map;

public class TargetAgent extends TargetAbstract {
  
  private RankExtorter rankExtorter;
  
  
  public TargetAgent( Map<Integer, ExtorterAbstract> extorters, Map<Integer, TargetAbstract> targets, Integer id, TargetConf targetConf ) {
    super( extorters, targets, id, targetConf );
    
    this.rankExtorter = RankExtorter.getInstance();
  }
  
  
  @Override
  public void decidePaymentExtortion() {
    // Calculate the convenience of paying each Extorter
    double convenience;
    double sumPunishment;
    Map<Integer, Double> sort = new HashMap<Integer, Double>();
    for ( Integer extorterId : this.extortions.keySet() ) {
      sumPunishment = 0;
      for ( Integer otherExtorterId : this.extortions.keySet() ) {
        if ( otherExtorterId != extorterId ) {
          // If Target has experience
          if ( this.extortersInfo.get( otherExtorterId ).hasPunishmentProb() ) {
            sumPunishment += this.extortions.get( otherExtorterId )
                .getPunishment()
                * this.extortersInfo.get( otherExtorterId ).getPunishmentProb();
            // If Target has no experience
          } else {
            sumPunishment += this.extortions.get( otherExtorterId )
                .getPunishment()
                * this.rankExtorter.getPunishmentProb( otherExtorterId );
          }
        }
      }
      
      // Extortion + (Sum Prob. Punishment * (1 - Successful Protection))
      convenience = this.extortions.get( extorterId ).getExtortion()
          + (sumPunishment * (1 - this.extortersInfo.get( extorterId )
              .getSuccessfulProtectionProb()));
      
      sort.put( extorterId, convenience );
    }
    
    // Sort Extorters by their convenience
    this.extortersRanking = Sort.ascendingSortByValue( sort );
  }
}
package intERS.output;

import java.util.Hashtable;
import java.util.Map;
import java.util.TreeMap;

public class OutputObserver extends OutputAbstract {
  
  
  private Map<String, Integer> targetsAlive;
  
  private Map<String, Integer> targetsSurvived;
  
  private Map<String, Integer> extortersFree;
  
  private Map<String, Integer> extortersImprisoned;
  
  
  public OutputObserver(int cycle, String type, int id) {
    super(AgentType.OBSERVER, cycle, type, id);
    
    this.targetsAlive = new Hashtable<String, Integer>();
    this.targetsSurvived = new Hashtable<String, Integer>();
    this.extortersFree = new Hashtable<String, Integer>();
    this.extortersImprisoned = new Hashtable<String, Integer>();
  }
  
  
  @Override
  public String getHeader(String fs) {
    String str = new String();
    Map<String, Integer> map;
    
    str += "type" + fs + "id" + fs;
    
    map = new TreeMap<String, Integer>(this.targetsAlive);
    for(String type : map.keySet()) {
      str += "TA" + type + fs;
      str += "TS" + type + fs;
    }
    
    str += "totalTA" + fs + "totalTS" + fs;
    
    String strImprisoned = new String();
    map = new TreeMap<String, Integer>(this.extortersFree);
    for(String type : map.keySet()) {
      str += "FR" + type + fs;
      strImprisoned += "IM" + type + fs;
    }
    
    str += "totalFR" + fs + strImprisoned + "totalIM";
    
    return str;
  }
  
  
  @Override
  public String getLine(String fs) {
    String str = new String();
    Map<String, Integer> map;
    
    str += this.type + fs + this.id + fs;
    
    int numTargetsAlive = 0;
    int totalTargetsAlive = 0;
    map = new TreeMap<String, Integer>(this.targetsAlive);
    for(String type : map.keySet()) {
      numTargetsAlive = map.get(type);
      totalTargetsAlive += numTargetsAlive;
      
      str += numTargetsAlive + fs;
    }
    
    int numTargetsSurvived = 0;
    int totalTargetsSurvived = 0;
    map = new TreeMap<String, Integer>(this.targetsSurvived);
    for(String type : map.keySet()) {
      numTargetsSurvived = map.get(type);
      totalTargetsSurvived += numTargetsSurvived;
      
      str += numTargetsSurvived + fs;
    }
    
    str += totalTargetsAlive + fs + totalTargetsSurvived + fs;
    
    int numFree;
    int totalFree = 0;
    int numImprisoned;
    int totalImprisoned = 0;
    String strImprisoned = new String();
    map = new TreeMap<String, Integer>(this.extortersFree);
    for(String type : map.keySet()) {
      numFree = map.get(type);
      totalFree += numFree;
      
      numImprisoned = 0;
      if(this.extortersImprisoned.containsKey(type)) {
        numImprisoned = this.extortersImprisoned.get(type);
      }
      totalImprisoned += numImprisoned;
      
      str += numFree + fs;
      strImprisoned += numImprisoned + fs;
    }
    
    str += totalFree + fs + strImprisoned + totalImprisoned;
    
    return str;
  }
  
  
  public int getNumTargetsAlive(String type) {
    int num = 0;
    if(this.targetsAlive.containsKey(type)) {
      num = this.targetsAlive.get(type);
    }
    
    return num;
  }
  
  
  public void setNumTargetsAlive(String type, int numTargets) {
    this.targetsAlive.put(type, numTargets);
  }
  
  
  public int getNumTargetsSurvived(String type) {
    int num = 0;
    if(this.targetsSurvived.containsKey(type)) {
      num = this.targetsSurvived.get(type);
    }
    
    return num;
  }
  
  
  public void setNumTargetsSurvived(String type, int numTargets) {
    this.targetsSurvived.put(type, numTargets);
  }
  
  
  public int getNumExtortersFree(String type) {
    int num = 0;
    if(this.extortersFree.containsKey(type)) {
      num = this.extortersFree.get(type);
    }
    
    return num;
  }
  
  
  public void setNumExtortersFree(String type, int numExtortersFree) {
    this.extortersFree.put(type, numExtortersFree);
  }
  
  
  public int getNumExtortersImprisoned(String type) {
    int num = 0;
    if(this.extortersImprisoned.containsKey(type)) {
      num = this.extortersImprisoned.get(type);
    }
    
    return num;
  }
  
  
  public void setNumExtortersImprisoned(String type,
      int numExtortersImprisoned) {
    this.extortersImprisoned.put(type, numExtortersImprisoned);
  }
}
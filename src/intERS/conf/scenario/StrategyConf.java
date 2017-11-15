package intERS.conf.scenario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import repast.simphony.random.RandomHelper;

public class StrategyConf {
  
  
  private int                  simulationRun;
  
  private boolean              uploaded;
  
  // <Extorter Id, Extortion>
  private Map<Integer, Double> extortions;
  
  // <Extorter Id, Punishment>
  private Map<Integer, Double> punishments;
  
  
  /**
   * Create a strategy configuration
   * 
   * @param simulationRun
   *          Simulation run
   * @return none
   */
  public StrategyConf(int simulationRun) {
    this.simulationRun = simulationRun;
    this.uploaded = false;
    this.extortions = new HashMap<Integer, Double>();
    this.punishments = new HashMap<Integer, Double>();
  }
  
  
  /**
   * Upload the Extorters' extortion and punishment values
   * 
   * @param path
   *          Output directory path
   * @param fieldSeparator
   *          Field separator
   * @param configFile
   *          Configuration file
   * @return none
   */
  public void upload(String path, String fieldSeparator,
      ExtorterConf extorterConf) {
    
    String filename = path + File.separator
        + extorterConf.getExtortersCfgFilename();
    
    File directory = new File(path);
    directory.mkdirs();
    
    File file = new File(filename);
    if((file.exists()) && (extorterConf.getEnableExtortersCfg())) {
      try {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        String[] tokens;
        boolean error = false;
        Integer simRun;
        Integer extorterId;
        Double extortion;
        Double punishment;
        while(((line = br.readLine()) != null) && (!error)) {
          tokens = line.split(fieldSeparator);
          if(tokens.length < 4) {
            error = true;
          } else {
            try {
              simRun = Integer.valueOf(tokens[0]);
              extorterId = Integer.valueOf(tokens[1]);
              extortion = Double.valueOf(tokens[2]) / 100.0;
              punishment = Double.valueOf(tokens[3]) / 100.0;
              
              if(simRun == this.simulationRun) {
                this.extortions.put(extorterId, extortion);
                this.punishments.put(extorterId, punishment);
              }
              
            } catch(NumberFormatException e) {
              error = true;
            }
          }
        }
        
        if((!error) && (!this.extortions.isEmpty())
            && (!this.punishments.isEmpty())) {
          this.uploaded = true;
        } else {
          this.extortions.clear();
          this.punishments.clear();
        }
        
        br.close();
      } catch(IOException e) {
      }
    }
    
    if(!this.uploaded) {
      
      // Extorter options
      List<Double> extortValues = new ArrayList<Double>();
      double extort = extorterConf.getMinExtort();
      while(extort <= extorterConf.getMaxExtort()) {
        extortValues.add(extort);
        extort += extorterConf.getStepExtort();
      }
      
      // Punishment options
      List<Double> punishValues = new ArrayList<Double>();
      double punish = extorterConf.getMinPunish();
      while(punish <= extorterConf.getMaxPunish()) {
        punishValues.add(punish);
        punish += extorterConf.getStepPunish();
      }
      
      for(int extorterId = 1; extorterId <= extorterConf
          .getNumberExtorters(); extorterId++) {
        extort = extortValues
            .get(RandomHelper.nextIntFromTo(0, extortValues.size() - 1));
        this.extortions.put(extorterId, extort / (double) 100);
        
        punish = -1;
        while(punish < extort) {
          punish = punishValues
              .get(RandomHelper.nextIntFromTo(0, punishValues.size() - 1));
        }
        this.punishments.put(extorterId, punish / (double) 100);
      }
      
      if(extorterConf.getEnableExtortersCfg()) {
        try {
          BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
          
          String str;
          for(Integer extorterId : this.extortions.keySet()) {
            str = this.simulationRun + fieldSeparator + extorterId.toString()
                + fieldSeparator
                + (this.extortions.get(extorterId).intValue() * 100)
                + fieldSeparator
                + (this.punishments.get(extorterId).intValue() * 100) + "\n";
            bw.write(str);
          }
          
          bw.flush();
          bw.close();
        } catch(IOException e) {
        }
      }
    }
  }
  
  
  /**
   * Define the extortion value
   * 
   * @param extorterId
   *          Extorter identification
   * @return Extortion value
   */
  public double getExtortion(int extorterId) {
    double extortionValue = 0;
    
    if(this.extortions.containsKey(extorterId)) {
      extortionValue = this.extortions.get(extorterId);
    }
    
    return extortionValue;
  }
  
  
  /**
   * Define the punishment value
   * 
   * @param extorterId
   *          Extorter identification
   * @return Punishment value
   */
  public double getPunishment(int extorterId) {
    double punishmentValue = 0;
    
    if(this.punishments.containsKey(extorterId)) {
      punishmentValue = this.punishments.get(extorterId);
    }
    
    return punishmentValue;
  }
}
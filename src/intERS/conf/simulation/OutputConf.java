package intERS.conf.simulation;

public class OutputConf {
  
  
  private String  directory;
  
  private String  fileExtorter;
  
  private String  fileObserver;
  
  private String  fileTarget;
  
  private Boolean fileAppend;
  
  private String  fieldSeparator;
  
  private int     writeEvery;
  
  private String  filePrefixAvg;
  
  private String  filePrefixSum;
  
  private String  classExtorterStat;
  
  private String  classObserverStat;
  
  private String  classTargetStat;
  
  
  public String getDirectory() {
    return this.directory;
  }
  
  
  public void setDirectory(String directory) {
    this.directory = directory;
  }
  
  
  public String getFileExtorter() {
    return this.fileExtorter;
  }
  
  
  public void setFileExtorter(String fileExtorter) {
    this.fileExtorter = fileExtorter;
  }
  
  
  public String getFileObserver() {
    return this.fileObserver;
  }
  
  
  public void setFileObserver(String fileObserver) {
    this.fileObserver = fileObserver;
  }
  
  
  public String getFileTarget() {
    return this.fileTarget;
  }
  
  
  public void setFileTarget(String fileTarget) {
    this.fileTarget = fileTarget;
  }
  
  
  public Boolean getFileAppend() {
    return this.fileAppend;
  }
  
  
  public void setFileAppend(Boolean fileAppend) {
    this.fileAppend = fileAppend;
  }
  
  
  public String getFieldSeparator() {
    return this.fieldSeparator;
  }
  
  
  public void setFieldSeparator(String fieldSeparator) {
    this.fieldSeparator = fieldSeparator;
  }
  
  
  public int getWriteEvery() {
    return this.writeEvery;
  }
  
  
  public void setWriteEvery(int writeEvery) {
    this.writeEvery = writeEvery;
  }
  
  
  public String getFilePrefixAvg() {
    return this.filePrefixAvg;
  }
  
  
  public void setFilePrefixAvg(String filePrefixAvg) {
    this.filePrefixAvg = filePrefixAvg;
  }
  
  
  public String getFilePrefixSum() {
    return this.filePrefixSum;
  }
  
  
  public void setFilePrefixSum(String filePrefixSum) {
    this.filePrefixSum = filePrefixSum;
  }
  
  
  public String getClassExtorterStat() {
    return this.classExtorterStat;
  }
  
  
  public void setClassExtorterStat(String classExtorterStat) {
    this.classExtorterStat = classExtorterStat;
  }
  
  
  public String getClassObserverStat() {
    return this.classObserverStat;
  }
  
  
  public void setClassObserverStat(String classObserverStat) {
    this.classObserverStat = classObserverStat;
  }
  
  
  public String getClassTargetStat() {
    return this.classTargetStat;
  }
  
  
  public void setClassTargetStat(String classTargetStat) {
    this.classTargetStat = classTargetStat;
  }
  
  
  @Override
  public String toString() {
    String str = new String();
    
    str += "OUTPUT\n";
    str += "Directory....................: [" + this.directory + "]\n";
    str += "Filename Extorter............: [" + this.fileExtorter + "]\n";
    str += "Filename Observer............: [" + this.fileObserver + "]\n";
    str += "Filename Target..............: [" + this.fileTarget + "]\n";
    str += "File Append..................: [" + this.fileAppend + "]\n";
    str += "Field Separator..............: [" + this.fieldSeparator + "]\n";
    str += "Write Every..................: [" + this.writeEvery + "]\n";
    str += "File Prefix Avg..............: [" + this.filePrefixAvg + "]\n";
    str += "File Prefix Sum..............: [" + this.filePrefixSum + "]\n";
    str += "Class Extorter Statistic.....: [" + this.classExtorterStat + "]\n";
    str += "Class Observer Statistic.....: [" + this.classObserverStat + "]\n";
    str += "Class Target Statistic.......: [" + this.classTargetStat + "]\n";
    
    return str;
  }
}
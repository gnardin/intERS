package intERS.statistics;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DataAggregation {
  
  private int    numSims;
  
  private String baseDirectory;
  
  private String outputFieldSeparator;
  
  
  public DataAggregation( int numSims, String baseDirectory, String outputFieldSeparator ) {
    this.numSims = numSims;
    this.baseDirectory = baseDirectory;
    this.outputFieldSeparator = outputFieldSeparator;
  }
  
  
  /**
   * Aggregate data
   * 
   * @param classStat
   *          Full path to the class to summarize the raw data (implements
   *          DataSummaryInterface)
   * @param rawFile
   *          Raw file to process
   * @param avgFilename
   *          Output filename containing the average values
   * @param sumFilename
   *          Output filename containing the sum values
   */
  public void aggregate( String classStat, String rawFile, String avgFilename,
      String sumFilename ) {
    
    try {
      @SuppressWarnings ( "unchecked" )
      Class<DataSummaryInterface> ds = (Class<DataSummaryInterface>) Class
          .forName( classStat );
      Constructor<DataSummaryInterface> dataSummary = ds
          .getDeclaredConstructor();
      
      DataSummaryInterface summary = dataSummary.newInstance();
      
      String filename;
      for ( int sim = 0; sim < this.numSims; sim++ ) {
        filename = this.baseDirectory + File.separator + (sim + 1)
            + File.separator + rawFile;
        
        summary.add( filename, this.outputFieldSeparator );
      }
      
      summary.writeAvg( this.baseDirectory + File.separator + avgFilename,
          this.outputFieldSeparator );
      summary.writeSum( this.baseDirectory + File.separator + sumFilename,
          this.outputFieldSeparator );
      
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
  }
}
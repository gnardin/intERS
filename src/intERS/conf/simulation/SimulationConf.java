package intERS.conf.simulation;

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

public class SimulationConf{
	
	private final static String	SIMULATION									= "simulation";
	
	// General
	private final static String	NUMBER_RUNS									= "numberOfRuns";
	
	private final static String	SEED												= "seed";
	
	private final static String	STOP_AT											= "stopAt";
	
	private final static String	RS_DIRECTORY								= "rsDirectory";
	
	private final static String	XML_FILENAME								= "xmlFilename";
	
	private final static String	XSD_FILENAME								= "xsdFilename";
	
	// Output
	private final static String	OUTPUT											= "output";
	
	private final static String	OUTPUT_DIRECTORY						= "directory";
	
	private final static String	OUTPUT_FILENAME_EXTORTER		= "fileExtorter";
	
	private final static String	OUTPUT_FILENAME_OBSERVER		= "fileObserver";
	
	private final static String	OUTPUT_FILENAME_TARGET			= "fileTarget";
	
	private final static String	OUTPUT_FILE_APPEND					= "fileAppend";
	
	private final static String	OUTPUT_FIELD_SEPARATOR			= "fieldSeparator";
	
	private final static String	OUTPUT_WRITE_EVERY					= "writeEvery";
	
	private final static String	OUTPUT_FILE_PREFIX_AVG			= "filePrefixAvg";
	
	private final static String	OUTPUT_FILE_PREFIX_SUM			= "filePrefixSum";
	
	private final static String	OUTPUT_CLASS_EXTORTER_STAT	= "classExtorterStat";
	
	private final static String	OUTPUT_CLASS_OBSERVER_STAT	= "classObserverStat";
	
	private final static String	OUTPUT_CLASS_TARGET_STAT		= "classTargetStat";
	
	// General information
	private int									numberRuns;
	
	private List<Integer>				randomSeeds;
	
	private String							stopAt;
	
	private String							rsDirectory;
	
	private String							xmlFilename;
	
	private String							xsdFilename;
	
	// Output information
	private OutputConf					output;
	
	
	public SimulationConf(){
		this.output = new OutputConf();
		this.randomSeeds = new ArrayList<Integer>();
	}
	
	
	public static SimulationConf simulationParser(String simulationFilename){
		SimulationConf simulation = null;
		OutputConf output = null;
		
		try{
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			
			// Setup a new eventReader
			InputStream in = new FileInputStream(simulationFilename);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			
			// Read the XML document
			XMLEvent event;
			StartElement startElement;
			EndElement endElement;
			while(eventReader.hasNext()){
				event = eventReader.nextEvent();
				
				if(event.isStartElement()){
					startElement = event.asStartElement();
					
					// Create a Simulation
					if(startElement.getName().getLocalPart() == (SIMULATION)){
						simulation = new SimulationConf();
						
						// Set numberRuns attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(NUMBER_RUNS)){
						event = eventReader.nextEvent();
						simulation
								.setNumberRuns(new Integer(event.asCharacters().getData()));
						continue;
						
						// Set seed attribute
					}else if(event.asStartElement().getName().getLocalPart().equals(SEED)){
						event = eventReader.nextEvent();
						simulation
								.addRandomSeed(new Integer(event.asCharacters().getData()));
						continue;
						
						// Set stopAt attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(STOP_AT)){
						event = eventReader.nextEvent();
						simulation.setStopAt(event.asCharacters().getData());
						continue;
						
						// Set rsDirectory attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(RS_DIRECTORY)){
						event = eventReader.nextEvent();
						simulation.setRSDirectory(event.asCharacters().getData());
						continue;
						
						// Set xmlFilename attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(XML_FILENAME)){
						event = eventReader.nextEvent();
						simulation.setXMLFilename(event.asCharacters().getData());
						continue;
						
						// Set xsdFilename attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(XSD_FILENAME)){
						event = eventReader.nextEvent();
						simulation.setXSDFilename(event.asCharacters().getData());
						continue;
						
						// Create an Output
					}else if(event.asStartElement().getName().getLocalPart() == (OUTPUT)){
						output = new OutputConf();
						continue;
						
						// Set outputDirectory attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(OUTPUT_DIRECTORY)){
						event = eventReader.nextEvent();
						output.setDirectory(event.asCharacters().getData());
						continue;
						
						// Set outputFilePrefix attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(OUTPUT_FILENAME_EXTORTER)){
						event = eventReader.nextEvent();
						output.setFileExtorter(event.asCharacters().getData());
						continue;
						
						// Set outputFilePosfix attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(OUTPUT_FILENAME_OBSERVER)){
						event = eventReader.nextEvent();
						output.setFileObserver(event.asCharacters().getData());
						continue;
						
						// Set singleFile attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(OUTPUT_FILENAME_TARGET)){
						event = eventReader.nextEvent();
						output.setFileTarget(event.asCharacters().getData());
						continue;
						
						// Set fileAppend attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(OUTPUT_FILE_APPEND)){
						event = eventReader.nextEvent();
						output.setFileAppend(new Boolean(event.asCharacters().getData()));
						continue;
						
						// Set outputFieldSeparator attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(OUTPUT_FIELD_SEPARATOR)){
						event = eventReader.nextEvent();
						output.setFieldSeparator(event.asCharacters().getData());
						continue;
						
						// Set outputWriteEvery attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(OUTPUT_WRITE_EVERY)){
						event = eventReader.nextEvent();
						output.setWriteEvery(new Integer(event.asCharacters().getData()));
						continue;
						
						// Set filePrefixAvg attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(OUTPUT_FILE_PREFIX_AVG)){
						event = eventReader.nextEvent();
						output.setFilePrefixAvg(event.asCharacters().getData());
						continue;
						
						// Set filePrefixSum attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(OUTPUT_FILE_PREFIX_SUM)){
						event = eventReader.nextEvent();
						output.setFilePrefixSum(event.asCharacters().getData());
						continue;
						
						// Set classExtorterStat attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(OUTPUT_CLASS_EXTORTER_STAT)){
						event = eventReader.nextEvent();
						output.setClassExtorterStat(event.asCharacters().getData());
						continue;
						
						// Set classObserverStat attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(OUTPUT_CLASS_OBSERVER_STAT)){
						event = eventReader.nextEvent();
						output.setClassObserverStat(event.asCharacters().getData());
						continue;
						
						// Set classTargetStat attribute
					}else if(event.asStartElement().getName().getLocalPart()
							.equals(OUTPUT_CLASS_TARGET_STAT)){
						event = eventReader.nextEvent();
						output.setClassTargetStat(event.asCharacters().getData());
						continue;
					}
				}
				// If we reach the end of an item element we add it to the
				// list
				if(event.isEndElement()){
					endElement = event.asEndElement();
					if(endElement.getName().getLocalPart() == (OUTPUT)){
						simulation.addOutput(output);
					}
				}
			}
			
			// Close the file
			in.close();
			
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(XMLStreamException e){
			e.printStackTrace();
		}
		
		return simulation;
	}
	
	
	public int getNumberRuns(){
		return this.numberRuns;
	}
	
	
	private void setNumberRuns(int numberRuns){
		this.numberRuns = numberRuns;
	}
	
	
	public List<Integer> getRandomSeeds(){
		return this.randomSeeds;
	}
	
	
	private void addRandomSeed(int seed){
		this.randomSeeds.add(seed);
	}
	
	
	public int getSeed(int index){
		if(index < this.randomSeeds.size()){
			return this.randomSeeds.get(index);
		}else{
			return 0;
		}
	}
	
	
	public String getStopAt(){
		return this.stopAt;
	}
	
	
	private void setStopAt(String stopAt){
		this.stopAt = stopAt;
	}
	
	
	public String getRSDirectory(){
		return this.rsDirectory;
	}
	
	
	private void setRSDirectory(String rsDirectory){
		this.rsDirectory = rsDirectory;
	}
	
	
	public String getXMLFilename(){
		return this.xmlFilename;
	}
	
	
	private void setXMLFilename(String xmlFilename){
		this.xmlFilename = xmlFilename;
	}
	
	
	public String getXSDFilename(){
		return this.xsdFilename;
	}
	
	
	private void setXSDFilename(String xsdFilename){
		this.xsdFilename = xsdFilename;
	}
	
	
	public OutputConf getOutput(){
		return this.output;
	}
	
	
	private void addOutput(OutputConf output){
		this.output = output;
	}
	
	
	@Override
	public String toString(){
		String str = new String();
		
		str += "GENERAL\n";
		str += "Number of Runs...............: [" + this.numberRuns + "]\n";
		str += "RANDOM SEEDS\n";
		for(Integer seed : this.randomSeeds){
			str += "Seed.........................: [" + seed + "]\n";
		}
		str += "Number of Cycles.............: [" + this.stopAt + "]\n";
		str += "RS Directory.................: [" + this.rsDirectory + "]\n";
		str += "XML Filename.................: [" + this.xmlFilename + "]\n";
		str += "XSD Filename.................: [" + this.xsdFilename + "]\n";
		str += this.output.toString();
		
		return str;
	}
}
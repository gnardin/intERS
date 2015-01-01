package intERS.utils;

import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

public class XML {
	
	/**
	 * Validates a XML file based on a XSD Schema
	 * 
	 * @param xmlFilename
	 *          XML filename
	 * @param xsdFilename
	 *          XSD filename
	 * 
	 * @return True if valid, False otherwise
	 */
	public static boolean isValid(String xmlFilename, String xsdFilename) {
		boolean result = false;
		
		if((new File(xmlFilename)).exists() && ((new File(xsdFilename).exists()))) {
			try {
				Source xmlFile = new StreamSource(new File(xmlFilename));
				
				SchemaFactory schemaFactory = SchemaFactory
						.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				Schema schema = schemaFactory.newSchema(new File(xsdFilename));
				
				Validator validator = schema.newValidator();
				validator.validate(xmlFile);
				
				result = true;
			} catch(IOException e) {
				e.printStackTrace();
			} catch(SAXException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
}
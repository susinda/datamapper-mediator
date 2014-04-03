package org.wso2.carbon.mediator.datamapper;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.OMTextImpl;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.synapse.MessageContext;
import org.apache.synapse.util.AXIOMUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.wso2.datamapper.engine.core.DataMapper;
import org.wso2.datamapper.engine.models.MappingResourceModel;

public class DataMapperHelper {

	public static boolean mediateDataMapper(MessageContext context, String configkey, String inSchemaKey, String outSchemaKey) {

		InputStream configFileInputStream = getInputStream(context, configkey);
		InputStream inputSchemaStream = getInputStream(context, inSchemaKey);
		InputStream outputSchemaStream = getInputStream(context, outSchemaKey);
		
		OMElement inputMessage = context.getEnvelope();
		InputStream inStream = new ByteArrayInputStream(inputMessage.toString().getBytes());

		try {

			DataMapper mapper = new DataMapper();
			MappingResourceModel mappingResourceModel = new MappingResourceModel(inputSchemaStream, outputSchemaStream, configFileInputStream);
			String finalOutput = mapper.doMap(inputMessage, mappingResourceModel);

			StringBuilder result = new StringBuilder(finalOutput);
			OMElement outmessage = parseJsonToXml(result);

			if (outmessage != null) {
				OMElement firstChild = outmessage.getFirstElement();
				if (firstChild != null) {
					QName resultQName = firstChild.getQName();
					if (resultQName.getLocalPart().equals("Envelope")
							&& (resultQName
									.getNamespaceURI()
									.equals(SOAP11Constants.SOAP_ENVELOPE_NAMESPACE_URI) || resultQName
									.getNamespaceURI()
									.equals(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI))) {
						SOAPEnvelope soapEnvelope = AXIOMUtils
								.getSOAPEnvFromOM(outmessage.getFirstElement());
						if (soapEnvelope != null) {
							try {
								context.setEnvelope(soapEnvelope);
							} catch (AxisFault axisFault) {
								System.out.println(" errorrrrr ");
							}
						}
					} else {
						context.getEnvelope().getBody().setFirstChild(outmessage);
					}
				}
			}
			inStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	private static OMElement parseJsonToXml(StringBuilder sb) throws JSONException,
			XMLStreamException, IOException {
		StringWriter sw = new StringWriter(5120);
		JSONObject jsonObject = new JSONObject(sb.toString());
		MappedXMLStreamReader reader = new MappedXMLStreamReader(jsonObject);

		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlStreamWriter = factory.createXMLStreamWriter(sw);

		xmlStreamWriter.writeStartDocument();
		while (reader.hasNext()) {
			int x = reader.next();
			switch (x) {
			case XMLStreamConstants.START_ELEMENT:
				xmlStreamWriter.writeStartElement(reader.getPrefix(),
						reader.getLocalName(), reader.getNamespaceURI());
				int namespaceCount = reader.getNamespaceCount();
				for (int i = namespaceCount - 1; i >= 0; i--) {
					xmlStreamWriter.writeNamespace(
							reader.getNamespacePrefix(i),
							reader.getNamespaceURI(i));
				}
				int attributeCount = reader.getAttributeCount();
				for (int i = 0; i < attributeCount; i++) {
					xmlStreamWriter.writeAttribute(
							reader.getAttributePrefix(i),
							reader.getAttributeNamespace(i),
							reader.getAttributeLocalName(i),
							reader.getAttributeValue(i));
				}
				break;
			case XMLStreamConstants.START_DOCUMENT:
				break;
			case XMLStreamConstants.CHARACTERS:
				xmlStreamWriter.writeCharacters(reader.getText());
				break;
			case XMLStreamConstants.CDATA:
				xmlStreamWriter.writeCData(reader.getText());
				break;
			case XMLStreamConstants.END_ELEMENT:
				xmlStreamWriter.writeEndElement();
				break;
			case XMLStreamConstants.END_DOCUMENT:
				xmlStreamWriter.writeEndDocument();
				break;
			case XMLStreamConstants.SPACE:
				break;
			case XMLStreamConstants.COMMENT:
				xmlStreamWriter.writeComment(reader.getText());
				break;
			case XMLStreamConstants.DTD:
				xmlStreamWriter.writeDTD(reader.getText());
				break;
			case XMLStreamConstants.PROCESSING_INSTRUCTION:
				xmlStreamWriter.writeProcessingInstruction(
						reader.getPITarget(), reader.getPIData());
				break;
			case XMLStreamConstants.ENTITY_REFERENCE:
				xmlStreamWriter.writeEntityRef(reader.getLocalName());
				break;
			default:
				throw new RuntimeException("Error in converting JSON to XML");
			}
		}
		xmlStreamWriter.writeEndDocument();
		xmlStreamWriter.flush();
		xmlStreamWriter.close();

		OMElement element = AXIOMUtil.stringToOM(sw.toString());
		return element;
	}

	//FIXME This need to be implemented as to return InputStream not the file
	private static InputStream getInputStream(MessageContext context, String configkey) {
			
			InputStream inputStream = null;
			Object configEntry = context.getEntry(configkey);
	        if (configEntry instanceof OMTextImpl){
	        	OMTextImpl text = (OMTextImpl)configEntry;
	        	String content = text.getText();
	        	inputStream = new ByteArrayInputStream(content.getBytes());
	        }
	        return inputStream;
	 }
	    
}

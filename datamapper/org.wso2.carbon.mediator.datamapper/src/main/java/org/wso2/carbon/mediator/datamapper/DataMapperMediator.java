package org.wso2.carbon.mediator.datamapper;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.OMTextImpl;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.synapse.ManagedLifecycle;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.SynapseLog;
import org.apache.synapse.core.SynapseEnvironment;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.mediators.MediatorProperty;
import org.apache.synapse.mediators.Value;
import org.apache.synapse.transport.passthru.config.PassThroughConfiguration;
import org.apache.synapse.util.AXIOMUtils;
import org.apache.synapse.util.jaxp.DOOMResultBuilderFactory;
import org.apache.synapse.util.jaxp.DOOMSourceBuilderFactory;
import org.apache.synapse.util.jaxp.ResultBuilderFactory;
import org.apache.synapse.util.jaxp.SourceBuilderFactory;
import org.apache.synapse.util.jaxp.StreamResultBuilderFactory;
import org.apache.synapse.util.jaxp.StreamSourceBuilderFactory;
import org.apache.synapse.util.resolver.ResourceMap;
import org.apache.synapse.util.xpath.SourceXPathSupport;
import org.apache.synapse.util.xpath.SynapseXPath;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.wso2.datamapper.engine.core.DataMapper;

public class DataMapperMediator extends AbstractMediator implements ManagedLifecycle {

	public static String DATAMAPPER="datamapper";
	public static String CONFIG="config";
	public static String INPUTSCHEMA="inputSchema";
	public static String OUTPUTSCHEMA="outputSchema";
	
	private Value configurationKey=null;
	private Value inputSchemaKey=null;
	private Value outputSchemaKey=null;
	
    public Value getCongigurationKey() {
        return configurationKey;
    }
    public void setCongigurationKey(Value xsltKey) {
        this.configurationKey = xsltKey;
    }
    
    
    public Value getInputSchemaKey() {
        return inputSchemaKey;
    }
    public void setInputSchemaKey(Value xsltKey) {
        this.inputSchemaKey = xsltKey;
    }
    
    
    public Value getOutputSchemaKey() {
        return outputSchemaKey;
    }
    public void setOutputSchemaKey(Value xsltKey) {
        this.outputSchemaKey = xsltKey;
    }

	
    public boolean mediate(MessageContext context) {

        SynapseLog synLog = getLog(context);
        if (synLog.isTraceOrDebugEnabled()) {
            synLog.traceOrDebug("DataMapper mediator : start");
        }

        //org.apache.synapse.registry.Registry regInstance = context.getConfiguration().getRegistry();  
        //Object obj=regInstance.getResource(new Entry(generatedXsltKey),null);    
        String configkey = configurationKey.evaluateValue(context);
        String inSchemaKey = inputSchemaKey.evaluateValue(context);
        String outSchemaKey = outputSchemaKey.evaluateValue(context);
       
        DataMapperHelper.mediateDataMapper(context, configkey, inSchemaKey, outSchemaKey);
       
        if (synLog.isTraceOrDebugEnabled()) {
            synLog.traceOrDebug("DataMapper mediator : Done");
        }
        return true;
    }

    public boolean isContentAware() {
        return false;
    }

	@Override
    public void destroy() {
    }

	@Override
    public void init(SynapseEnvironment arg0) {
    }
}

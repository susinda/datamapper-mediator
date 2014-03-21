/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.mediator.datamapper.config.xml;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.synapse.Mediator;
import org.apache.synapse.SynapseConstants;
import org.apache.synapse.config.xml.*;
import org.apache.synapse.mediators.Value;
import org.jaxen.JaxenException;
import org.wso2.carbon.mediator.datamapper.DataMapperMediator;

import javax.xml.namespace.QName;

import java.util.Map;
import java.util.Properties;


public class DataMapperMediatorFactory extends AbstractMediatorFactory {

    private static final QName TAG_NAME
                = new QName(XMLConfigConstants.SYNAPSE_NAMESPACE, "datamapper");

    public QName getTagQName() {
        return TAG_NAME;
    }

    public Mediator createSpecificMediator(OMElement elem, Properties properties) {

        DataMapperMediator datamapperMediator = new DataMapperMediator();
        // ValueFactory for creating dynamic or static Value
        ValueFactory keyFac = new ValueFactory();
        

        OMAttribute configKeyAttribute = elem.getAttribute(new QName(DataMapperMediator.CONFIG));
        if (configKeyAttribute != null) {
            Value configKeyValue = keyFac.createValue(DataMapperMediator.CONFIG, elem);
            datamapperMediator.setCongigurationKey(configKeyValue);
        } else {
            handleException("The config attribute is required for the DataMapper mediator");
        }
        
        OMAttribute inputSchemaKeyAttribute = elem.getAttribute(new QName(DataMapperMediator.INPUTSCHEMA));
        if (inputSchemaKeyAttribute != null) {
            Value configKeyValue = keyFac.createValue(DataMapperMediator.INPUTSCHEMA, elem);
            datamapperMediator.setInputSchemaKey(configKeyValue);
        } else {
            handleException("The inputSchema attribute is required for the DataMapper mediator");
        }
        
        OMAttribute outputSchemaKeyAttribute = elem.getAttribute(new QName(DataMapperMediator.OUTPUTSCHEMA));
        if (outputSchemaKeyAttribute != null) {
            Value configKeyValue = keyFac.createValue(DataMapperMediator.OUTPUTSCHEMA, elem);
            datamapperMediator.setOutputSchemaKey(configKeyValue);
        } else {
            handleException("The outputSchema attribute is required for the DataMapper mediator");
        }
        
        /*
        OMElement inputSchemaElement = elem.getFirstChildWithName(new QName(SynapseConstants.SYNAPSE_NAMESPACE, DataMapperMediator.INPUTSCHEMA));
        if (inputSchemaElement != null) {
        	Value inputSchemaValue = new Value(inputSchemaElement.getText());
        	//Value inputSchemaKeyValue = keyFac2.createValue("inputSchemaKey", inputSchemaElement);
        	datamapperMediator.setInputSchemaKey(inputSchemaValue);
        } 

        OMElement outputSchemaElement = elem.getFirstChildWithName(new QName(SynapseConstants.SYNAPSE_NAMESPACE, DataMapperMediator.OUTPUTSCHEMA));
        if (outputSchemaElement != null) {
        	Value outputSchemaValue = new Value(outputSchemaElement.getText());
        	//Value outputSchemaKeyValue = keyFac3.createValue("outputSchemaKey", outputSchemaElement);
        	datamapperMediator.setOutputSchemaKey(outputSchemaValue);
        } 
        */

        // after successfully creating the mediator set its common attributes such as tracing etc
        processAuditStatus(datamapperMediator, elem);

        return datamapperMediator;
    }
}

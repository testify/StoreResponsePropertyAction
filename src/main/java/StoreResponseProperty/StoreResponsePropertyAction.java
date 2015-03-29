/*
 * Copyright 2015 Codice Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package StoreResponseProperty;

import org.codice.testify.actions.Action;
import org.codice.testify.objects.AllObjects;
import org.codice.testify.objects.TestifyLogger;
import org.codice.testify.objects.Response;
import org.codice.testify.objects.TestProperties;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * The StoreResponsePropertyAction class is a Testify Action service for Response elements as test properties
 */
public class StoreResponsePropertyAction implements BundleActivator, Action {

    private String xpathOptionText = "|XPATH|";
    private String nodeOptionText = "|NODE|";
    private boolean xpathOption = false;
    private boolean nodeOption = false;
    private int expectedActionElements = 3;

    @Override
    public void executeAction(String s) {

        TestifyLogger.debug("Running StoreResponsePropertyAction", this.getClass().getSimpleName());

        //Cast objects needed for this action
        TestProperties testProperties = (TestProperties) AllObjects.getObject("testProperties");
        Response response = (Response)AllObjects.getObject("response");
        String responseContent = response.getResponse();

        if (s != null) {
            //Check for XPATH option in assertioninfo
            if (s.startsWith(xpathOptionText)) {
                s = s.substring(xpathOptionText.length(), s.length());
                xpathOption = true;
                expectedActionElements = 2;
            }
            if (s.startsWith(nodeOptionText)) {
                s = s.substring(nodeOptionText.length(), s.length());
                nodeOption = true;
                expectedActionElements = 2;
            }
            //Split action info by "=="
            TestifyLogger.debug(s, this.getClass().getSimpleName());
            String[] actionElements = s.split("==");

            //If there are not enough action elements, then produce an error
            if (actionElements.length >= expectedActionElements) {

                // Get the property name for storage
                String propertyName = actionElements[0];

                // Use Xpath matching
                if (xpathOption) {
                    // Get Xpath Text from action elements
                    String xpathText = actionElements[1];

                    //Set up document and xpath objects
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XPathFactory xpathFactory = XPathFactory.newInstance();
                    XPath xpath = xpathFactory.newXPath();

                    //Parse processor response into document
                    Document document = null;
                    try {
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        document = builder.parse(new InputSource(new ByteArrayInputStream(responseContent.getBytes("utf-8"))));
                    } catch (ParserConfigurationException | SAXException | IOException e) {
                        TestifyLogger.error(e.getMessage(), this.getClass().getSimpleName());
                    }

                    //If document is not null, run xpath expression
                    if (document != null) {
                        try {
                            //Run xpath expression and return match
                            XPathExpression xpathExpression = xpath.compile(xpathText);
                            String xpathResult = (String) xpathExpression.evaluate(document.getDocumentElement(), XPathConstants.STRING);
                            // Store xpathResult in property
                            testProperties.addProperty(propertyName, xpathResult);
                            TestifyLogger.debug("Stored value of " + xpathResult + " under property name of " + propertyName, this.getClass().getSimpleName());

                        } catch (XPathExpressionException e) {
                            TestifyLogger.error("An error occurred processing xpath " + e.getMessage(), this.getClass().getSimpleName());
                        }
                    }
                    else {
                        TestifyLogger.error("The response did not contain valid xml, xpath match could not be performed", this.getClass().getSimpleName());
                    }
                }
                else if (nodeOption) {
                    String xpathText = actionElements[1];
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    Document doc = null;
                    try {
                        doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(responseContent)));
                    } catch (SAXException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    }

                    XPath xPath = XPathFactory.newInstance().newXPath();
                    Node result = null;
                    try {
                        result = (Node)xPath.evaluate(xpathText, doc, XPathConstants.NODE);
                    } catch (XPathExpressionException e) {
                        TestifyLogger.error("Error occurred evaluating xpath", this.getClass().getSimpleName());
                    }

                    testProperties.addProperty(propertyName, nodeToString(result));
                    TestifyLogger.debug("Stored value of " + nodeToString(result) + " under property name of " + propertyName, this.getClass().getSimpleName());
                    System.out.println(nodeToString(result));
                }
                else if (!nodeOption & !xpathOption) { // Use simple matching
                    // Get the start and end flags from the action elements into separate strings
                    String startFlag = actionElements[1];
                    String endFlag = actionElements[2];

                    //Check that the property name, start flag, and end flag are greater than one character
                    if (propertyName.length() > 0 && startFlag.length() > 0 && endFlag.length() > 0) {

                        //Check is processor response is null
                        if (responseContent != null) {

                            //Check if the start flag is contained in the response
                            if (responseContent.contains(startFlag)) {

                                //Get index of start element and create a modified string from the end of the start element
                                int start = responseContent.indexOf(startFlag);
                                String modResponseContent = responseContent.substring(start + startFlag.length(), responseContent.length());

                                //Check if the modified string contains the end element
                                if (modResponseContent.contains(endFlag)) {

                                    //Get the index of the end element
                                    int end = modResponseContent.indexOf(endFlag);

                                    //Take the value between the start and end elements and store the value in the test properties under the property name
                                    String value = modResponseContent.substring(0, end);
                                    testProperties.addProperty(propertyName, value);
                                    TestifyLogger.debug("Stored value of " + value + " under property name of " + propertyName, this.getClass().getSimpleName());

                                    //Add the modified test properties back into AllObjects
                                    AllObjects.setObject("testProperties", testProperties);

                                } else {
                                    TestifyLogger.error("End element: " + endFlag + " not found in response after start element: " + startFlag, this.getClass().getSimpleName());
                                }
                            } else {
                                TestifyLogger.error("Start element: " + startFlag + " not found in response", this.getClass().getSimpleName());
                            }
                        } else {
                            TestifyLogger.error("Processor response is null", this.getClass().getSimpleName());
                        }
                    } else {
                        TestifyLogger.error("Property name: " + propertyName + " , start element: " + startFlag + " , and end element: " + endFlag + " in action info must be at least one character", this.getClass().getSimpleName());
                    }
                }
            } else {
                TestifyLogger.error("Provided Action Info: " + s + " does not include a property name, start element, and end element separated by == (Ex: Property Name==Start Element==End Element)", this.getClass().getSimpleName());
            }
        } else {
            TestifyLogger.error("Action info must be provided in test file", this.getClass().getSimpleName());
        }
    }

    private String nodeToString(Node node) {
        StringWriter stringWriter = new StringWriter();
        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            TestifyLogger.error("Error occurred creating transformer", this.getClass().getSimpleName());
        }
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        try {
            transformer.transform(new DOMSource(node), new StreamResult(stringWriter));
        } catch (TransformerException e) {
            TestifyLogger.error("Error occurred transforming xml to string", this.getClass().getSimpleName());
        }
        return(stringWriter.toString());
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {

        //Register the StoreResponseProperty service
        bundleContext.registerService(Action.class.getName(), new StoreResponsePropertyAction(), null);

    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {

    }
}
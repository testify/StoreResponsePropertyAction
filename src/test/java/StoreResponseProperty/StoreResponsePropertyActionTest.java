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

import org.codice.testify.objects.AllObjects;
import org.codice.testify.objects.Response;
import org.codice.testify.objects.TestProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class StoreResponsePropertyActionTest {

    //Set objects
    StoreResponsePropertyAction storeResponsePropertyAction = new StoreResponsePropertyAction();
    Response response = new Response("start    Information  end");
    Response xmlResponse = new Response("<xq:employees xmlns:xq=\"http://xmlbeans.apache.org/samples/xquery/employees\">\n" +
            "    <xq:employee>\n" +
            "        <xq:name>Fred Jones</xq:name>\n" +
            "        <xq:address location=\"home\">\n" +
            "            <xq:street>900 Aurora Ave.</xq:street>\n" +
            "            <xq:city>Seattle</xq:city>\n" +
            "            <xq:state>WA</xq:state>\n" +
            "            <xq:zip>98115</xq:zip>\n" +
            "        </xq:address>\n" +
            "        <xq:address location=\"work\">\n" +
            "            <xq:street>2011 152nd Avenue NE</xq:street>\n" +
            "            <xq:city>Redmond</xq:city>\n" +
            "            <xq:state>WA</xq:state>\n" +
            "            <xq:zip>98052</xq:zip>\n" +
            "        </xq:address>\n" +
            "        <xq:phone location=\"work\">(425)555-5665</xq:phone>\n" +
            "        <xq:phone location=\"home\">(206)555-5555</xq:phone>\n" +
            "        <xq:phone location=\"mobile\">(206)555-4321</xq:phone>\n" +
            "    </xq:employee>\n" +
            "</xq:employees>");

    @Test
    public void testNoActionInfo() {

        AllObjects.setObject("response", response);
        AllObjects.setObject("testProperties", new TestProperties());
        storeResponsePropertyAction.executeAction(null);
        TestProperties testProperties = (TestProperties)AllObjects.getObject("testProperties");
        assert ( testProperties.getPropertyNames().isEmpty() );

    }

    @Test
    public void testMissingActionInfoSplitter() {

        AllObjects.setObject("response", response);
        AllObjects.setObject("testProperties", new TestProperties());
        storeResponsePropertyAction.executeAction("PropertyName==startend");
        TestProperties testProperties = (TestProperties)AllObjects.getObject("testProperties");
        assert ( testProperties.getPropertyNames().isEmpty() );

    }

    @Test
    public void testMissingActionInfoPropertyValue() {

        AllObjects.setObject("response", response);
        AllObjects.setObject("testProperties", new TestProperties());
        storeResponsePropertyAction.executeAction("==start==end");
        TestProperties testProperties = (TestProperties)AllObjects.getObject("testProperties");
        assert ( testProperties.getPropertyNames().isEmpty() );

    }

    @Test
    public void testMissingActionInfoStartValue() {

        AllObjects.setObject("response", response);
        AllObjects.setObject("testProperties", new TestProperties());
        storeResponsePropertyAction.executeAction("PropertyName====end");
        TestProperties testProperties = (TestProperties)AllObjects.getObject("testProperties");
        assert ( testProperties.getPropertyNames().isEmpty() );

    }

    @Test
    public void testMissingActionInfoEndValue() {

        AllObjects.setObject("response", response);
        AllObjects.setObject("testProperties", new TestProperties());
        storeResponsePropertyAction.executeAction("PropertyName==start==");
        TestProperties testProperties = (TestProperties)AllObjects.getObject("testProperties");
        assert ( testProperties.getPropertyNames().isEmpty() );

    }

    @Test
    public void testStartElementNotFound() {

        AllObjects.setObject("response", response);
        AllObjects.setObject("testProperties", new TestProperties());
        storeResponsePropertyAction.executeAction("PropertyName==SomethingCrazy==end");
        TestProperties testProperties = (TestProperties)AllObjects.getObject("testProperties");
        assert ( testProperties.getPropertyNames().isEmpty() );

    }

    @Test
    public void testEndElementNotFound() {

        AllObjects.setObject("response", response);
        AllObjects.setObject("testProperties", new TestProperties());
        storeResponsePropertyAction.executeAction("PropertyName==start==SomethingCrazy");
        TestProperties testProperties = (TestProperties)AllObjects.getObject("testProperties");
        assert ( testProperties.getPropertyNames().isEmpty() );

    }

    @Test
    public void testEndElementNotFoundAfterStart() {

        AllObjects.setObject("response", response);
        AllObjects.setObject("testProperties", new TestProperties());
        storeResponsePropertyAction.executeAction("PropertyName==start==art");
        TestProperties testProperties = (TestProperties)AllObjects.getObject("testProperties");
        assert ( testProperties.getPropertyNames().isEmpty() );

    }

    @Test
    public void testCorrectPropertyStore() {

        AllObjects.setObject("response", response);
        AllObjects.setObject("testProperties", new TestProperties());
        storeResponsePropertyAction.executeAction("PropertyName==start    ==  end");
        TestProperties testProperties = (TestProperties)AllObjects.getObject("testProperties");
        assert ( !testProperties.getPropertyNames().isEmpty() );
        assert ( testProperties.getFirstValue("PropertyName").equals("Information"));

    }

    @Test
    public void testNullResponse() {

        AllObjects.setObject("response", new Response(null));
        AllObjects.setObject("testProperties", new TestProperties());
        storeResponsePropertyAction.executeAction("PropertyName==start    ==  end");
        TestProperties testProperties = (TestProperties)AllObjects.getObject("testProperties");
        assert ( testProperties.getPropertyNames().isEmpty() );

    }

    @Test
    public void testXpathOption() {

        AllObjects.setObject("response", xmlResponse);
        AllObjects.setObject("testProperties", new TestProperties());
        String s = "|XPATH|PropertyName==//*[local-name() = 'name']";
        storeResponsePropertyAction.executeAction(s);
        TestProperties testProperties = (TestProperties)AllObjects.getObject("testProperties");
        assert ( testProperties.getFirstValue("PropertyName").equals("Fred Jones"));
    }
}
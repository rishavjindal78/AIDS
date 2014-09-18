package org.shunya.shared;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@XmlRootElement()
@XmlAccessorOrder(value = XmlAccessOrder.ALPHABETICAL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "properties"
})
public class FieldPropertiesMap {
    Map<String, FieldProperties> properties;

    public FieldPropertiesMap() {
        // For XML Initialization
    }

    public FieldPropertiesMap(Map<String, FieldProperties> fieldPropertiesMap) {
        this.properties = fieldPropertiesMap;
    }

    public FieldProperties get(String name) {
        return properties.get(name);
    }

    public static FieldPropertiesMap parseStringMap(Map<String, String> propMap) {
        Map<String, FieldProperties> propertiesMap = new HashMap<>();
        if (propMap != null) {
            for (String fieldName : propMap.keySet()) {
                propertiesMap.put(fieldName, new FieldProperties(fieldName, fieldName, propMap.get(fieldName), "", false, "input"));
            }
        }
        return new FieldPropertiesMap(propertiesMap);
    }

    public static String convertObjectToXml(FieldPropertiesMap fieldPropertiesMap) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(FieldPropertiesMap.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter sw = new StringWriter(1000);
        marshaller.marshal(fieldPropertiesMap, sw);
        return sw.toString();
    }

    public static FieldPropertiesMap convertXmlToObject(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(FieldPropertiesMap.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (FieldPropertiesMap) unmarshaller.unmarshal(new StringReader(xml));
    }

    public Set<String> keySet() {
        return properties.keySet();
    }

    public java.util.Collection<FieldProperties> values() {
        return properties.values();
    }
}

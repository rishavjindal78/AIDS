package org.shunya.shared;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "name",
        "value",
        "description",
        "required",
        "type",
        "displayName",
        "misc"
})
public class FieldProperties implements Serializable {
    private String name;
    private String displayName;
    private String value;
    private String description;
    private boolean required;
    private String type;
    private String misc;

    public FieldProperties() {
        //required by JAXB
    }

    public FieldProperties(String name,String displayName, String value,String description, boolean required, String type, String misc) {
        this.name = name;
        this.displayName=displayName;
        this.value = value;
        this.description = description;
        this.required = required;
        this.type = type;
        this.misc = misc;
    }

    public String getValue() {
        if (value == null)
            value = "";
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldProperties that = (FieldProperties) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMisc() {
        return misc;
    }

    public void setMisc(String misc) {
        this.misc = misc;
    }
}
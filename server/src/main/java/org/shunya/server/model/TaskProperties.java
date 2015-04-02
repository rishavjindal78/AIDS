package org.shunya.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "TASK_PROPERTIES")
@TableGenerator(name = "seqGen", table = "ID_GEN", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VALUE", pkColumnValue = "TASK_PROPERTIES", allocationSize = 2)
public class TaskProperties implements CustomProperties{
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqGen")
    @JsonIgnore
    private long id;
    @Lob
    private String properties;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }
}

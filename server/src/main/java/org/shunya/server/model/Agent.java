package org.shunya.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;

import static org.hibernate.annotations.CascadeType.DELETE;
import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

@Entity
@Table(name="AGENT")
@TableGenerator(name = "seqGen", table = "ID_GEN", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VALUE", pkColumnValue = "AGENT", allocationSize = 10)
public class Agent {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqGen")
    private long id;
    @OneToOne
    @JsonIgnore
    private User createdBy;
    @ManyToOne
    @JsonIgnore
    private Team team;
    private String name;
    private String description;
    private String baseUrl;
    @Cascade({SAVE_UPDATE, DELETE})
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToOne
    private AgentProperties agentProperties;

    public Boolean getPrivateAccess() {
        return privateAccess;
    }

    public void setPrivateAccess(Boolean privateAccess) {
        this.privateAccess = privateAccess;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    private Boolean privateAccess;

    public Agent() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Agent agent = (Agent) o;

        if (id != agent.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public AgentProperties getAgentProperties() {
        return agentProperties;
    }

    public void setAgentProperties(AgentProperties agentProperties) {
        this.agentProperties = agentProperties;
    }
}

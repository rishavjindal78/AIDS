package org.shunya.server.vo;

import org.shunya.server.AgentStatus;
import org.shunya.server.model.Team;
import org.shunya.server.model.User;

public class AgentVOBuilder {
    private long id;
    private User createdBy;
    private Team team;
    private String name;
    private String description;
    private String baseUrl;
    private Boolean privateAccess;
    private AgentStatus status;

    private AgentVOBuilder() {
    }

    public static AgentVOBuilder anAgentVO() {
        return new AgentVOBuilder();
    }

    public AgentVOBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public AgentVOBuilder withCreatedBy(User createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public AgentVOBuilder withTeam(Team team) {
        this.team = team;
        return this;
    }

    public AgentVOBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public AgentVOBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public AgentVOBuilder withBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public AgentVOBuilder withPrivateAccess(Boolean privateAccess) {
        this.privateAccess = privateAccess;
        return this;
    }

    public AgentVOBuilder withStatus(AgentStatus status) {
        this.status = status;
        return this;
    }

    public AgentVOBuilder but() {
        return anAgentVO().withId(id).withCreatedBy(createdBy).withTeam(team).withName(name).withDescription(description).withBaseUrl(baseUrl).withPrivateAccess(privateAccess).withStatus(status);
    }

    public AgentVO build() {
        AgentVO agentVO = new AgentVO();
        agentVO.setId(id);
        agentVO.setCreatedBy(createdBy);
        agentVO.setTeam(team);
        agentVO.setName(name);
        agentVO.setDescription(description);
        agentVO.setBaseUrl(baseUrl);
        agentVO.setPrivateAccess(privateAccess);
        agentVO.setStatus(status);
        return agentVO;
    }
}

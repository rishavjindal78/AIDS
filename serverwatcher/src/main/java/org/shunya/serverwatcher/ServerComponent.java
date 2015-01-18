package org.shunya.serverwatcher;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.time.LocalDateTime;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "name",
        "url",
        "expectedTokenString",
        "expectedResponseCode",
        "serverType",
        "username",
        "password"
})
public class ServerComponent {
    private String name;
    private String url;
    private String expectedTokenString;
    @XmlTransient
    private int statusCode;
    private ServerType serverType = ServerType.DEV;
    private int[] expectedResponseCode = {200};
    @XmlTransient
    private String response = "n/a";
    @XmlTransient
    private String exception;
    @XmlTransient
    private ServerStatus status = ServerStatus.UNKNOWN;
    @XmlTransient
    @JsonSerialize(using = JsonDateSerializer.class)
    private LocalDateTime lastStatusUpdateTime;
    @XmlTransient
    @JsonSerialize(using = JsonDateSerializer.class)
    private LocalDateTime lastUpTime;
    @XmlTransient
    private ServerStatus lastStatus = ServerStatus.UP;
    @XmlTransient
    private boolean stateChanged;

    private String username;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
        this.status = status;
        if (status == ServerStatus.UP) {
            lastUpTime = LocalDateTime.now();
        }
        updateTimeStamp();
        if (lastStatus != null && lastStatus != status) {
            setStateChanged(true);
        } else {
            setStateChanged(false);
        }
        lastStatus = status;
    }

    private void updateTimeStamp() {
        lastStatusUpdateTime = LocalDateTime.now();
    }

    public LocalDateTime getLastStatusUpdateTime() {
        return lastStatusUpdateTime;
    }

    public void setLastStatusUpdateTime(LocalDateTime lastStatusUpdateTime) {
        this.lastStatusUpdateTime = lastStatusUpdateTime;
    }

    public LocalDateTime getLastUpTime() {
        return lastUpTime;
    }

    public void setLastUpTime(LocalDateTime lastUpTime) {
        this.lastUpTime = lastUpTime;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
        updateTimeStamp();
    }

    public String getExpectedTokenString() {
        return expectedTokenString;
    }

    public void setExpectedTokenString(String expectedTokenString) {
        this.expectedTokenString = expectedTokenString;
    }

    public int[] getExpectedResponseCode() {
        return expectedResponseCode;
    }

    public void setExpectedResponseCode(int... expectedResponseCode) {
        this.expectedResponseCode = expectedResponseCode;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public ServerStatus getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(ServerStatus lastStatus) {
        this.lastStatus = lastStatus;
    }

    public boolean isStateChanged() {
        return stateChanged;
    }

    public void setStateChanged(boolean stateChanged) {
        this.stateChanged = stateChanged;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

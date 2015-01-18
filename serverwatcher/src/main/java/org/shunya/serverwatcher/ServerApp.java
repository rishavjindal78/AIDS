package org.shunya.serverwatcher;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.http.client.CookieStore;

import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "id",
        "name",
        "owner",
        "notificationEmailIds",
        "failureNotificationInterval",
        "minNotificationIntervalMinutes",
        "pingSchedule",
        "comments",
        "rcasUrl",
        "username",
        "password",
        "maxCookieAgeInHours",
        "serverType",
        "enabled",
        "chatChannel",
        "contactDL",
        "componentGroups",
        "leadDeveloper",
        "diskComponents",
        "useHttpProxy",
        "diskUsageSchedule"
})
public class ServerApp implements Comparable<ServerApp> {
    private Long id;
    @JsonIgnore
    @XmlTransient
    private String jobScheduleId;
    private String name;
    private String chatChannel;
    private String contactDL;
    private String leadDeveloper;
    private List<ComponentGroup> componentGroups = new ArrayList<>(10);
    private List<DiskComponent> diskComponents = new ArrayList<>(10);
    private String owner;
    private String notificationEmailIds;
    private ServerType serverType = ServerType.PROD;
    @JsonIgnore
    private long failureNotificationInterval;
    @XmlTransient
    @JsonIgnore
    private Period failureNotificationPeriod;
    @JsonIgnore
    private long minNotificationIntervalMinutes = 30;
    @XmlTransient
    @JsonIgnore
    private LocalDateTime lastNotificationTime = LocalDateTime.now().minusMonths(1);
    private String pingSchedule;
    private String diskUsageSchedule;
    @XmlTransient
    @JsonSerialize(using = JsonDateSerializer.class)
    private LocalDateTime lastStatusUpdateTime;

    private String comments;
    @JsonIgnore
    private String rcasUrl;
    @JsonIgnore
    private String username;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private long maxCookieAgeInHours = 10;
    @XmlTransient
    @JsonIgnore
    private long lastCookieTime;
    @XmlTransient
    @JsonIgnore
    private CookieStore cookieStore;
    @JsonIgnore
    @XmlTransient
    private ScheduledFuture<?> scheduledFuture;

    private boolean useHttpProxy = false;

    private boolean enabled = true;

    public void addGroupComponent(ComponentGroup componentGroup) {
        componentGroups.add(componentGroup);
    }

    public List<ComponentGroup> getComponentGroups() {
        return componentGroups;
    }

    public void setComponentGroups(List<ComponentGroup> componentGroups) {
        this.componentGroups = componentGroups;
    }

    public String getRcasUrl() {
        return rcasUrl;
    }

    public void setRcasUrl(String rcasUrl) {
        this.rcasUrl = rcasUrl;
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

    public String getPingSchedule() {
        return pingSchedule;
    }

    public void setPingSchedule(String pingSchedule) {
        this.pingSchedule = pingSchedule;
    }

    public ServerStatus getStatus() {
        boolean allOk = true;
        for (ComponentGroup group : componentGroups) {
            for (ServerComponent component : group.getComponentList()) {
                if (component.getStatus() != ServerStatus.UP) {
                    allOk = false;
                    break;
                }
            }
        }
        return allOk ? ServerStatus.UP : ServerStatus.UNKNOWN;
    }

    public void setStatus(ServerStatus status) {
        for (ComponentGroup group : componentGroups) {
            for (ServerComponent component : group.getComponentList()) {
                component.setStatus(status);
            }
        }
    }

    public LocalDateTime getLastStatusUpdateTime() {
        return lastStatusUpdateTime;
    }

    public void setLastStatusUpdateTime(LocalDateTime lastStatusUpdateTime) {
        this.lastStatusUpdateTime = lastStatusUpdateTime;
    }

    public Period getFailureNotificationPeriod() {
        return failureNotificationPeriod;
    }

    public void setFailureNotificationPeriod(Period failureNotificationPeriod) {
        this.failureNotificationPeriod = failureNotificationPeriod;
    }

    public long getFailureNotificationInterval() {
        return failureNotificationInterval;
    }

    public void setFailureNotificationInterval(long failureNotificationInterval) {
        this.failureNotificationInterval = failureNotificationInterval;
    }

    public String getNotificationEmailIds() {
        return notificationEmailIds;
    }

    public void setNotificationEmailIds(String notificationEmailIds) {
        this.notificationEmailIds = notificationEmailIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMaxCookieAgeInHours() {
        return maxCookieAgeInHours;
    }

    public void setMaxCookieAgeInHours(long maxCookieAgeInHours) {
        this.maxCookieAgeInHours = maxCookieAgeInHours;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    @Override
    public int hashCode() {
        return Long.valueOf(id).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ServerApp))
            return false;
        return id.equals(((ServerApp) o).getId());
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int compareTo(ServerApp o) {
        return getId().compareTo(o.getId());
    }

    public long getMinNotificationIntervalMinutes() {
        return minNotificationIntervalMinutes;
    }

    public void setMinNotificationIntervalMinutes(long minNotificationIntervalMinutes) {
        this.minNotificationIntervalMinutes = minNotificationIntervalMinutes;
    }

    public LocalDateTime getLastNotificationTime() {
        return lastNotificationTime;
    }

    public void setLastNotificationTime(LocalDateTime lastNotificationTime) {
        this.lastNotificationTime = lastNotificationTime;
    }

    public String getContactDL() {
        return contactDL;
    }

    public void setContactDL(String contactDL) {
        this.contactDL = contactDL;
    }

    public String getChatChannel() {
        return chatChannel;
    }

    public void setChatChannel(String chatChannel) {
        this.chatChannel = chatChannel;
    }

    public String getLeadDeveloper() {
        return leadDeveloper;
    }

    public void setLeadDeveloper(String leadDeveloper) {
        this.leadDeveloper = leadDeveloper;
    }

    public void setServerResponse(String response) {
        for (ComponentGroup group : componentGroups) {
            for (ServerComponent serverComponent : group.getComponentList()) {
                serverComponent.setResponse(response);
            }
        }
    }

    public CookieStore getCookieStore() {
        if (TimeUnit.HOURS.convert(System.currentTimeMillis() - lastCookieTime, TimeUnit.MILLISECONDS) < maxCookieAgeInHours)
            return cookieStore;
        System.out.println("cookieStore expired - " + getName());
        return null;
    }

    public void setCookieStore(CookieStore cookieStore) {
        this.cookieStore = cookieStore;
        this.lastCookieTime = System.currentTimeMillis();
    }

    public boolean isUseHttpProxy() {
        return useHttpProxy;
    }

    public void setUseHttpProxy(boolean useHttpProxy) {
        this.useHttpProxy = useHttpProxy;
    }

    public String getJobScheduleId() {
        return jobScheduleId;
    }

    public void setJobScheduleId(String jobScheduleId) {
        this.jobScheduleId = jobScheduleId;
    }

    public String getDiskUsageSchedule() {
        return diskUsageSchedule;
    }

    public void setDiskUsageSchedule(String diskUsageSchedule) {
        this.diskUsageSchedule = diskUsageSchedule;
    }

    public List<DiskComponent> getDiskComponents() {
        return diskComponents;
    }

    public void setDiskComponents(List<DiskComponent> diskComponents) {
        this.diskComponents = diskComponents;
    }

    public ScheduledFuture<?> getScheduledFuture() {
        return scheduledFuture;
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }
}

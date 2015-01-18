package org.shunya.serverwatcher;


public class ServerAppBuilder extends ServerApp {

    public ServerAppBuilder withDiskUsageSchedule(String diskUsageSchedule){
        setDiskUsageSchedule(diskUsageSchedule);
        return this;
    }

    public ServerAppBuilder useHttpProxy(boolean useHttpProxy){
        setUseHttpProxy(useHttpProxy);
        return this;
    }

    public ServerAppBuilder withContactDL(String contactDL) {
        setContactDL(contactDL);
        return this;
    }

    public ServerAppBuilder withChatChannel(String chatChannel) {
        setChatChannel(chatChannel);
        return this;
    }

    public ServerAppBuilder withLeadDeveloper(String leadDeveloper) {
        setLeadDeveloper(leadDeveloper);
        return this;
    }

    public ServerAppBuilder withMinNotificationIntervalMinutes(long interval) {
        setMinNotificationIntervalMinutes(interval);
        return this;
    }

    public ServerAppBuilder withServerType(ServerType serverType) {
        setServerType(serverType);
        return this;
    }

    public ServerAppBuilder withId(long id) {
        setId(id);
        return this;
    }

    public ServerAppBuilder withAppName(String appName) {
        setName(appName);
        return this;
    }

    public ServerAppBuilder withComponentGroup(ComponentGroup group){
        getComponentGroups().add(group);
        return this;
    }

    public ServerAppBuilder withDiskComponent(DiskComponent component){
        getDiskComponents().add(component);
        return this;
    }

    public ServerAppBuilder withPingSchedule(String pingSchedule) {
        setPingSchedule(pingSchedule);
        return this;
    }

    public ServerAppBuilder withRcasUrl(String rcasUrl) {
        setRcasUrl(rcasUrl);
        return this;
    }

    public ServerAppBuilder withUsername(String username) {
        setUsername(username);
        return this;
    }

    public ServerAppBuilder withPassword(String password) {
        setPassword(password);
        return this;
    }

    public ServerAppBuilder withNotificationEmailIds(String notificationEmailIds) {
        setNotificationEmailIds(notificationEmailIds);
        return this;
    }

    public ServerApp build() {
        return this;
    }

}

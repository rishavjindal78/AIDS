package org.shunya.serverwatcher;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ComponentGroup {
    private String groupName;
    private List<ServerComponent> componentList;
    private int minRunningCount;
    @XmlTransient
    private ServerStatus status = ServerStatus.UNKNOWN;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<ServerComponent> getComponentList() {
        return componentList;
    }

    public void setComponentList(List<ServerComponent> componentList) {
        this.componentList = componentList;
    }

    public int getMinRunningCount() {
        return minRunningCount;
    }

    public void setMinRunningCount(int minRunningCount) {
        this.minRunningCount = minRunningCount;
    }

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
        this.status = status;
    }

    public void calculateStatus() {
        AtomicInteger runningCount = new AtomicInteger(0);
        getComponentList()
                .stream()
                .filter(component -> component.getStatus() == ServerStatus.UP)
                .forEach(component -> runningCount.incrementAndGet());
        if (runningCount.get() >= minRunningCount) {
            status = ServerStatus.UP;
        } else {
            status = ServerStatus.DOWN;
        }
    }
}

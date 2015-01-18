package org.shunya.serverwatcher;

import java.util.List;

public class ComponentGroup {
    private String groupName;
    private List<ServerComponent> componentList;

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
}

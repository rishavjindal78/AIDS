package org.shunya.serverwatcher;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;

public class ComponentGroupBuilder {
    private String groupName;
    private List<ServerComponent> componentList;
    private int minRunningCount;

    private ComponentGroupBuilder() {
    }

    public static ComponentGroupBuilder aComponentGroup() {
        return new ComponentGroupBuilder();
    }

    public ComponentGroupBuilder withGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public ComponentGroupBuilder withComponentList(List<ServerComponent> componentList) {
        this.componentList = componentList;
        return this;
    }

    public ComponentGroupBuilder withComponentList(ServerComponent... componentArray) {
        this.componentList = asList(componentArray);
        return this;
    }

    public ComponentGroupBuilder withMinRunningCount(int runningCount){
        this.minRunningCount = runningCount;
        return this;
    }

    public ComponentGroupBuilder but() {
        return aComponentGroup().withGroupName(groupName).withComponentList(componentList);
    }

    public ComponentGroup build() {
        ComponentGroup componentGroup = new ComponentGroup();
        componentGroup.setGroupName(groupName);
        componentGroup.setComponentList(componentList);
        componentGroup.setMinRunningCount(minRunningCount);
        return componentGroup;
    }
}

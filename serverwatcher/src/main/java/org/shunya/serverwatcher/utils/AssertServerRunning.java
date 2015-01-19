package org.shunya.serverwatcher.utils;

import org.shunya.serverwatcher.ComponentGroup;
import org.shunya.serverwatcher.ServerStatus;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class AssertServerRunning implements Predicate<ComponentGroup> {
    final Predicate<Integer> predicate;

    public AssertServerRunning(Predicate<Integer> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(ComponentGroup componentGroup) {
        AtomicInteger runningCount = new AtomicInteger(0);
        componentGroup.getComponentList()
                .stream()
                .filter(component -> component.getStatus() == ServerStatus.UP)
                .forEach(component -> runningCount.incrementAndGet());
        return predicate.test(runningCount.get());
    }
}

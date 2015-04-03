package org.shunya.server.services;

import java.util.List;

public interface AidsJobScheduler {
    void schedule(String cronExpression, long taskId);

    void unSchedule(long taskId);

    List<String> predict(String cronExpression, int times);
}

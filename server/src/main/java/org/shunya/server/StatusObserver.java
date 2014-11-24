package org.shunya.server;

import org.shunya.server.model.TaskRun;

public interface StatusObserver {
    void notifyStatus(TaskRun taskRun, String message);
}

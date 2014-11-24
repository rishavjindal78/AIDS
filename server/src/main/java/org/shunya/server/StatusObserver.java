package org.shunya.server;

import org.shunya.shared.model.TaskRun;

public interface StatusObserver {
    void notifyStatus(TaskRun taskRun, String message);
}

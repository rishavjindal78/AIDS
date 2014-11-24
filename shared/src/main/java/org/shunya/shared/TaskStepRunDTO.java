package org.shunya.shared;

import org.shunya.shared.model.RunState;
import org.shunya.shared.model.RunStatus;

import java.util.Date;

public class TaskStepRunDTO {
    private long id;
    private String logs;
    private boolean status;
    private Date startTime;
    private Date finishTime;
    private RunState runState = RunState.NEW;
    private RunStatus runStatus = RunStatus.NOT_RUN;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public RunState getRunState() {
        return runState;
    }

    public void setRunState(RunState runState) {
        this.runState = runState;
    }

    public RunStatus getRunStatus() {
        return runStatus;
    }

    public void setRunStatus(RunStatus runStatus) {
        this.runStatus = runStatus;
    }
}

package org.shunya.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.shunya.shared.RunState;
import org.shunya.shared.RunStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "TASK_STEP_RUN")
@TableGenerator(name = "seqGen", table = "ID_GEN", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VALUE", pkColumnValue = "TASK_STEP_RUN", allocationSize = 10)
public class TaskStepRun implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqGen")
    private long id;
    private int sequence;
    @Lob
    @Basic(fetch = FetchType.EAGER)
//	@Column(columnDefinition="blob(6M)")
    private String logs;
    private boolean status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishTime;
    @ManyToOne
    private TaskStep taskStep;
    @ManyToOne
    @JsonIgnore
    private TaskRun taskRun;
    //	@Basic(optional = false)
//	@Column(nullable = false, columnDefinition = "char(1) default 'A'")
    @Enumerated(EnumType.STRING)
    private RunState runState = RunState.NEW;
    @Enumerated(EnumType.STRING)
    private RunStatus runStatus = RunStatus.NOT_RUN;
    @OneToOne
    private Agent agent;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
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

    public TaskStep getTaskStep() {
        return taskStep;
    }

    public void setTaskStep(TaskStep taskStep) {
        this.taskStep = taskStep;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TaskStepRun))
            return false;
        TaskStepRun other = (TaskStepRun) obj;
        if (id != other.id)
            return false;
        return true;
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

    @Override
    public String toString() {
        return "TaskStepRun{" +
                "status = " + status +
                ", sequence = " + sequence +
                '}';
    }

    public TaskRun getTaskRun() {
        return taskRun;
    }

    public void setTaskRun(TaskRun taskRun) {
        this.taskRun = taskRun;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}

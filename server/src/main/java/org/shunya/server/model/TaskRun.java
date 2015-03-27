package org.shunya.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.*;
import org.shunya.shared.RunState;
import org.shunya.shared.RunStatus;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.swing.text.Document;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hibernate.annotations.CascadeType.DELETE;
import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

@Entity
@Table(name = "TASK_RUN")
@TableGenerator(name = "seqGen", table = "ID_GEN", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VALUE", pkColumnValue = "TASK_RUN", allocationSize = 10)
public class TaskRun implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqGen")
    private long id;
    private String name;
    private String comments;
    @OneToOne
    private User runBy;
    @Lob
    @Basic(fetch = FetchType.EAGER)
//	@Column(columnDefinition="blob(6M)")
    private String logs;
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishTime;
    @Cascade({SAVE_UPDATE, DELETE})
    @OneToMany(mappedBy = "taskRun")
//    @Fetch(FetchMode.SELECT)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<TaskStepRun> taskStepRuns = new ArrayList<>();
    @ManyToOne
    private Task task;
    @ManyToOne
    @JsonIgnore
    private Team team;
    private boolean status;
    private boolean notifyStatus = false;
    @Transient
    private int progress;
    @Transient
    private transient Document logDocument;
    @Transient
    private int cacheId = 100;
    //	@Basic(optional = false)
//	@Column(nullable = false, columnDefinition = "char(1) default 'A'")
    @Enumerated(EnumType.STRING)
    private RunState runState = RunState.NEW;
    @Enumerated(EnumType.STRING)
    private RunStatus runStatus = RunStatus.NOT_RUN;
    private boolean clearAlert = false;
    @OneToOne
    private Agent agent;

    //	@Lob
//	@Basic(fetch=FetchType.EAGER)
//	@Column(columnDefinition="blob(6M)")
//	private String xml;

    public User getRunBy() {
        return runBy;
    }

    public void setRunBy(User runBy) {
        this.runBy = runBy;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setClearAlert(boolean clearAlert) {
        this.clearAlert = clearAlert;
    }

    public boolean isClearAlert() {
        return clearAlert;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<TaskStepRun> getTaskStepRuns() {
        return taskStepRuns;
    }

    public void setTaskStepRuns(List<TaskStepRun> taskStepRuns) {
        this.taskStepRuns = taskStepRuns;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
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

    public int getProgress() {
        if (runState != RunState.COMPLETED) {
            int total = getTaskStepRuns().size();
            int completed = (int) getTaskStepRuns().stream().filter(taskStepRun1 -> taskStepRun1.getRunState() == RunState.COMPLETED).count();
            if (total > 0) {
                progress = 100 * completed / total;
            }
        } else {
            progress = 100;
        }
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @XmlTransient()
    public Document getLogDocument() {
        return logDocument;
    }

    public void setLogDocument(Document logDocument) {
        this.logDocument = logDocument;
    }

    /*public Long getVersion() {
        return version;
    }
    public void setVersion(Long version) {
        this.version = version;
    }*/
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
        if (!(obj instanceof TaskRun))
            return false;
        TaskRun other = (TaskRun) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskLog{" +
                "id=" + id +
                ", taskStepLogs=" + taskStepRuns +
                ", status=" + status +
                ", progress=" + progress +
                ", runStatus=" + runStatus +
                '}';
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(boolean notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Transient
    public String timeConsumed() {
        if (startTime == null) {
            return "NA";
        }
        if (finishTime == null) {
            finishTime = new Date();
        }
        long millis = finishTime.getTime() - startTime.getTime();
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public String getLogs() {
        return logs;
    }

    public void setLogs(String logs) {
        this.logs = logs;
    }

    public int getCacheId() {
        return cacheId;
    }

    public void setCacheId(int cacheId) {
        this.cacheId = cacheId;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }
}

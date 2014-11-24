package org.shunya.server.model;

import org.hibernate.annotations.*;
import org.shunya.shared.model.RunState;
import org.shunya.shared.model.RunStatus;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.swing.text.Document;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private boolean status;
    private boolean notifyStatus = false;
    @Transient
    private int progress;
    @Transient
    private transient Document logDocument;
    //	@Basic(optional = false)
//	@Column(nullable = false, columnDefinition = "char(1) default 'A'")
    @Enumerated(EnumType.STRING)
    private RunState runState = RunState.NEW;
    @Enumerated(EnumType.STRING)
    private RunStatus runStatus = RunStatus.NOT_RUN;
    private boolean clearAlert = false;

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
}

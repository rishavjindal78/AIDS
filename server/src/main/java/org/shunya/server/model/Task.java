package org.shunya.server.model;

import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.OrderBy;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

import static org.hibernate.annotations.CascadeType.DELETE;
import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

@Entity
@Table(name = "TASK")
@TableGenerator(name = "seqGen", table = "ID_GEN", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VALUE", pkColumnValue = "TASK_DATA", allocationSize = 10)
@XmlRootElement()
//@XmlAccessorOrder(value=XmlAccessOrder.ALPHABETICAL)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "author",
        "name",
        "description",
        "inputParams",
        "stepDataList"
})
public class Task implements Serializable {
    private static final long serialVersionUID = 3450975996342231267L;
    @Id
    @XmlTransient
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqGen")
    private long id;
    @OneToOne
    private User author;
    private String name;
    @Column(length = 500)
    private String description;
    private String tags;
    @ManyToOne
    private Team team;
    /*@Column(length = 500)
    private String comments;*/
    private boolean abortOnFirstFailure = true;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String inputParams;
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
    @LazyCollection(LazyCollectionOption.FALSE)
    @BatchSize(size = 10)
    @OrderBy(clause = "sequence asc")
    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER)
    private List<TaskStep> stepDataList;
    /*@OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    @XmlTransient
    @LazyCollection(LazyCollectionOption.TRUE)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.DELETE})
    private List<TaskRun> taskRuns;
    */@ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Agent> agentList;
    private boolean notifyStatus = false;
    @OneToOne
    @Cascade({SAVE_UPDATE, DELETE})
    @LazyCollection(LazyCollectionOption.FALSE)
    private TaskProperties taskProperties;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /*public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }*/

    public List<TaskStep> getStepDataList() {
        return stepDataList;
    }

    public void setStepDataList(List<TaskStep> stepDataList) {
        this.stepDataList = stepDataList;
    }

   /* public List<TaskRun> getTaskRuns() {
        return taskRuns;
    }

    public void setTaskRuns(List<TaskRun> taskRuns) {
        this.taskRuns = taskRuns;
    }*/

    public String getInputParams() {
        return inputParams;
    }

    public void setInputParams(String inputParams) {
        this.inputParams = inputParams;
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
        if (!(obj instanceof Task))
            return false;
        Task other = (Task) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<Agent> getAgentList() {
        return agentList;
    }

    public void setAgentList(List<Agent> agentList) {
        this.agentList = agentList;
    }

    public boolean isAbortOnFirstFailure() {
        return abortOnFirstFailure;
    }

    public void setAbortOnFirstFailure(boolean abortOnFirstFailure) {
        this.abortOnFirstFailure = abortOnFirstFailure;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean isNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(boolean notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    public TaskProperties getTaskProperties() {
        return taskProperties;
    }

    public void setTaskProperties(TaskProperties taskProperties) {
        this.taskProperties = taskProperties;
    }
}

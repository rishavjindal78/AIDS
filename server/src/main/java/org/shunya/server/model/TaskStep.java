package org.shunya.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "TASK_STEP")
@TableGenerator(name = "seqGen", table = "ID_GEN", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VALUE", pkColumnValue = "TASK_STEP_DATA", allocationSize = 10)
public class TaskStep implements Serializable {
    private static final long serialVersionUID = 1907841119637052268L;
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqGen")
    private long id;
    private int sequence;
    private String name;

    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass;
    }

    private String taskClass;
    @Column(length = 500)
    private String description;
    @OneToOne
    @JsonIgnore
    private User author;
    private boolean active = true;
    private boolean failOver = false;
    private boolean ignoreFailure = false;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String inputParams;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String outputParams;
    @ManyToOne
    @JsonIgnore
    private Task task;//TODO : To keep till we get better redirection strategy
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "taskStep", fetch = FetchType.LAZY)
    @JsonIgnore
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<TaskStepRun> taskStepRuns;
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Agent> agentList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getInputParams() {
        return inputParams;
    }

    public void setInputParams(String inputParams) {
        this.inputParams = inputParams;
    }

    public String getOutputParams() {
        return outputParams;
    }

    public void setOutputParams(String outputParams) {
        this.outputParams = outputParams;
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
        if (!(obj instanceof TaskStep))
            return false;
        TaskStep other = (TaskStep) obj;
        if (id != other.id)
            return false;
        return true;
    }

    public boolean isFailOver() {
        return failOver;
    }

    public void setFailOver(boolean failOver) {
        this.failOver = failOver;
    }

    public List<TaskStepRun> getTaskStepRuns() {
        return taskStepRuns;
    }

    public void setTaskStepRuns(List<TaskStepRun> taskStepRuns) {
        this.taskStepRuns = taskStepRuns;
    }

    public Set<Agent> getAgentList() {
        return agentList;
    }

    public void setAgentList(Set<Agent> agentList) {
        this.agentList = agentList;
    }


    public boolean isIgnoreFailure() {
        return ignoreFailure;
    }

    public void setIgnoreFailure(boolean ignoreFailure) {
        this.ignoreFailure = ignoreFailure;
    }
}

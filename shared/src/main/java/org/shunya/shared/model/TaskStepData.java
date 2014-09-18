package org.shunya.shared.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "TASK_STEP_DATA")
@TableGenerator(name = "seqGen", table = "ID_GEN", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VALUE", pkColumnValue = "TASK_STEP_DATA", allocationSize = 10)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "sequence",
        "name",
        "className",
        "description",
        "author",
        "active",
        "failOver",
        "inputParams",
        "outputParams"
})
@XmlRootElement
public class TaskStepData implements Serializable {
    private static final long serialVersionUID = 1907841119637052268L;
    @Id
    @XmlTransient
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqGen")
    private long id;
    private int sequence;
    private String name;
    private String className;
    @Column(length = 500)
    private String description;
    private String author;
    private boolean active = true;
    private boolean failOver = false;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String inputParams;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String outputParams;
    @ManyToOne
    @XmlTransient
    @JsonIgnore
    private TaskData taskData;
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "taskStepData", fetch = FetchType.LAZY)
    @XmlTransient
    @JsonIgnore
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<TaskStepRun> taskStepRuns;
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Agent> agentList;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public TaskData getTaskData() {
        return taskData;
    }

    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
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
        if (!(obj instanceof TaskStepData))
            return false;
        TaskStepData other = (TaskStepData) obj;
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

    public List<Agent> getAgentList() {
        return agentList;
    }

    public void setAgentList(List<Agent> agentList) {
        this.agentList = agentList;
    }
}

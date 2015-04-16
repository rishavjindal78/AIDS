package org.shunya.server.model;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Date;

import static org.hibernate.annotations.CascadeType.*;

@Entity(name = "db_document")
@TableGenerator(name = "seqGen", table = "ID_GEN", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VALUE", pkColumnValue = "DOCUMENT_ID", allocationSize = 1)
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int downloads;
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadDate;
    private DocumentStorage storage = DocumentStorage.DB;
    private String localPath;
    private String description;
    private boolean deprecated = false;

    @OneToOne(fetch = FetchType.LAZY)
    @Cascade({SAVE_UPDATE, DELETE, ALL, REMOVE})
    @LazyCollection(LazyCollectionOption.FALSE)
    private AttachmentContent attachmentContent;

    private long length;
    private String md5;
    private String tags;
    @OneToOne
    private Category category;
    private boolean archived = false;
    @OneToOne
    private User author;
    @ManyToOne
    private Team team;

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDownloads() {
        return downloads;
    }

    public void setDownloads(int downloads) {
        this.downloads = downloads;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public DocumentStorage getStorage() {
        return storage;
    }

    public void setStorage(DocumentStorage storage) {
        this.storage = storage;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public AttachmentContent getAttachmentContent() {
        return attachmentContent;
    }

    public void setAttachmentContent(AttachmentContent attachmentContent) {
        this.attachmentContent = attachmentContent;
    }
}

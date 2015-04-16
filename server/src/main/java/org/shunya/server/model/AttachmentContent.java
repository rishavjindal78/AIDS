package org.shunya.server.model;

import javax.persistence.*;

@Entity
@Table(name = "ATTACHMENT_CONTENT")
@TableGenerator(name = "seqGen", table = "ID_GEN", pkColumnName = "GEN_KEY", valueColumnName = "GEN_VALUE", pkColumnValue = "ATTACHMENT_CONTENT", allocationSize = 1)
public class AttachmentContent {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqGen")
    private long id;

    @Lob
    @Basic(fetch = FetchType.LAZY)
//    @Column(columnDefinition="blob(5M)")
    private byte[] content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}

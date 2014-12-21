package org.shunya.server.vo;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadDTO {
    private MultipartFile file;
    private String description;
    private String tags;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}

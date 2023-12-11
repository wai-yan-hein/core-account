package com.dms.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class CVFile {
    private String fileId;
    private String fileName;
    private Long fileSize;
    private String fileDescription;
    private String fileParentId;
    private boolean file;
    private String fileContent;
    private String fileExtension;
    private String filePath;
    private String fileLink;
    private String fileColor;
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
    private boolean deleted;
    private boolean favorite;
    private List<CVFile> child;

    @Override
    public String toString() {
        return fileDescription;
    }

}

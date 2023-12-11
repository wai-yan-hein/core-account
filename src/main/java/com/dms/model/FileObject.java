package com.dms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileObject {

    private String parentId;
    private String folderName;
    private Long totalSpace;
    private Long usedSpace;
    private Long freeSpace;
}

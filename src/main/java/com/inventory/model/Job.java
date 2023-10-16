package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "job")
public class Job {
    @EmbeddedId
    private JobKey key;
    @Column(name = "job_name")
    private String jobName;
    @Column(name = "start_date", columnDefinition = "DATE")
    private Date startDate;
    @Column(name = "end_date", columnDefinition = "DATE")
    private Date endDate;
    @Column(name = "finished")
    private boolean finished;
    @Column(name = "deleted")
    private boolean deleted;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
}

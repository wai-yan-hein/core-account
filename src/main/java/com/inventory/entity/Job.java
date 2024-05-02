package com.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "job")
public class Job {

    @EmbeddedId
    private JobKey key;
    @Column(name = "job_name")
    private String jobName;
    @Column(name = "start_date", columnDefinition = "DATE")
    private LocalDate startDate;
    @Column(name = "end_date", columnDefinition = "DATE")
    private LocalDate endDate;
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
    @Column(name = "dept_id")
    private int deptId;
    @Column(name = "output_cost")
    private Double outputCost;
    @Column(name = "output_qty")
    private Double outputQty;

    @Override
    public String toString() {
        return jobName;
    }

    public Job(JobKey key, String jobName) {
        this.key = key;
        this.jobName = jobName;
    }

    public Job() {
    }

    public Job(String code, String name) {
        this.key = new JobKey();
        this.key.setJobNo(code);
        this.jobName = name;
    }

}

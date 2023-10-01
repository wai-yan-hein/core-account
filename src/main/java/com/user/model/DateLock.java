package com.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import lombok.Data;

@Entity
@Table(name = "date_lock")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DateLock {

    @EmbeddedId
    private DateLockKey key;
    @Column(name = "remark")
    private String remark;
    @Column(name = "start_date", columnDefinition = "DATE")
    private Date startDate;
    @Column(name = "end_date", columnDefinition = "DATE")
    private Date endDate;
    @Column(name = "date_lock")
    private boolean dateLock;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    private transient ZonedDateTime createdDateTime;
    private transient ZonedDateTime updatedDateTime;

}

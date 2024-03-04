package com.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class JobKey implements Serializable {

    @Column(name = "job_no")
    private String jobNo;
    @Column(name = "comp_code")
    private String compCode;

    public JobKey(String jobNo, String compCode) {
        this.jobNo = jobNo;
        this.compCode = compCode;
    }

    public JobKey() {
    }
}

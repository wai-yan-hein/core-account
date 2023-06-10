package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;

@Embeddable
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentHisDetailKey implements Serializable {

    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "unique_id")
    private Integer uniqueId;
    @Column(name = "dept_id")
    private Integer deptId;
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Dell
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "department")
public class DepartmentA {

    @EmbeddedId
    private DepartmentAKey key;
    @Column(name = "dept_name")
    private String deptName;
    @Column(name = "parent_dept")
    private String parentDept;
    @Column(name = "active")
    private boolean active;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_dt",columnDefinition = "TIMESTAMP")
    private LocalDateTime createdDt;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_dt",columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDt;
    @Column(name = "usr_code")
    private String userCode;
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "map_dept_id")
    private Integer mapDeptId;
    @Column(name = "deleted")
    private boolean deleted;
    @Transient
    private List<DepartmentA> child;

   public DepartmentA(String deptCode, String deptName) {
       this.key = new DepartmentAKey();
       this.key.setDeptCode(deptCode);
       this.deptName = deptName;
    }
   
   public DepartmentA(DepartmentAKey key, String deptName) {
        this.key = key;
        this.deptName = deptName;
    }

    public DepartmentA() {
    }

    @Override
    public String toString() {
        return deptName;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "department_user")
public class DepartmentUser {

    @Id
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "dept_name")
    private String deptName;
    @Column(name = "inv_queue")
    private String inventoryQ;
    @Column(name = "acc_queue")
    private String accountQ;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    public DepartmentUser(Integer deptId) {
        this.deptId = deptId;
    }

    public DepartmentUser(Integer deptId, String deptName) {
        this.deptId = deptId;
        this.deptName = deptName;
    }

    public DepartmentUser() {
    }

}

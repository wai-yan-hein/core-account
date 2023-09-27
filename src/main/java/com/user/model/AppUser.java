/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author wai yan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Data
@Table(name = "appuser")
public class AppUser {

    @Id
    @Column(name = "user_code")
    private String userCode;
    @Column(name = "user_name")
    private String userName;
    @Column(name = "user_short_name")
    private String userShortName;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "active")
    private boolean active;
    @Column(name = "role_code")
    private String roleCode;
    @Column(name = "doctor_id")
    private String doctorId;
    @Column(name = "dept_id")
    private Integer deptId;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "loc_code")
    private String locCode;
    @Column(name = "dept_code")
    private String deptCode;

    public AppUser(String userCode, String userName) {
        this.userCode = userCode;
        this.userName = userName;
    }

    public AppUser() {
    }

}

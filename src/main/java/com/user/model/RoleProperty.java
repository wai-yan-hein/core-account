/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "role_prop")
public class RoleProperty {

    @EmbeddedId
    private RolePropertyKey key;
    @Column(name = "prop_value")
    private String propValue;
    @Column(name = "remark")
    private String remark;
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    public RoleProperty() {
    }

    public RoleProperty(RolePropertyKey key, String propValue) {
        this.key = key;
        this.propValue = propValue;
    }

}

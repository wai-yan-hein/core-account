/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.acc.model;

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
 * @author Lenovo
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "business_type")
public class BusinessType {

    @Id
    @Column(name = "bus_id")
    private Integer busId;
    @Column(name = "bus_name")
    private String busName;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date")
    private Date updatedDate;

    @Override
    public String toString() {
        return busName;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Entity
@Table(name = "date_filter")
public class DateModel {

    @Id
    @Column(name = "description_1")
    private String description;
    @Column(name = "month_name")
    private String monthName;
    @Column(name = "month_1")
    private Integer month;
    @Column(name = "year_1")
    private Integer year;
    @Column(name = "start_date")
    private String startDate;
    @Column(name = "end_date")
    private String endDate;
}

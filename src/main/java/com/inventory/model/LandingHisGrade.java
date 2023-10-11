/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Entity
@Table(name = "landing_his_grade")
public class LandingHisGrade {

    @Embedded
    private LandingHisGradeKey key;
    @Column(name = "match_count")
    private double matchCount;
    @Column(name = "match_percent")
    private double matchPercent;
    @Transient
    private String stockName;
    @Column(name = "choose")
    private boolean choose;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class DateModel {
  private String description;
    private String monthName;
    private Integer month;
    private Integer year;
    private String startDate;
    private String endDate;
}

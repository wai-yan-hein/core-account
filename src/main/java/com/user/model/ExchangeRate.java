/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class ExchangeRate {

    private ExchangeKey key;
    private Date exDate;
    private Double homeFactor;
    private String homeCur;
    private Double targetFactor;
    private String targetCur;
    private Double exRate;
    private Date createdDate;
    private String createdBy;
    private Date updatedDate;
    private String updatedBy;
    private boolean deleted;
}

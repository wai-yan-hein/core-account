/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author DELL
 */
@Data
public class ProcessHisDetailKey {

    private String vouNo;
    private String stockCode;
    private String locCode;
    private String compCode;
    private Integer deptId;
    private Integer uniqueId;
}
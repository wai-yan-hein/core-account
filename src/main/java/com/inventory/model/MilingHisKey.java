/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author DELL
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class MilingHisKey implements Serializable{

    @Column(name = "vou_no")
    private String vouNo;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "dept_id")
    private Integer deptId;
}

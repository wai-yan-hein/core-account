/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class RetInKey implements Serializable{

    @Column(name = "unique_id")
    private int uniqueId;
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "vou_no")
    private String vouNo;

}

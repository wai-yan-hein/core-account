/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;


/**
 *
 * @author wai yan
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class MillingRawDetailKey implements Serializable {
    @Column(name = "comp_code")
    private String compCode;
    @Column(name = "unique_id")
    private int uniqueId;
    @Column(name = "vou_no")
    private String vouNo;


}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.model;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

/**
 * @author SAI
 */
@Data
@Embeddable
public class SeqKeyAccount implements Serializable {
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "seq_option")
    private String seqOption;
    @Column(name = "period")
    private String period;
    @Column(name = "comp_code")
    private String compCode;
}

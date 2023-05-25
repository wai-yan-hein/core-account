/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import jakarta.persistence.Column;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class SeqKey implements Serializable{

    @Column(name = "seq_option")
    private String seqOption;
    @Column(name = "period")
    private String period;
    @Column(name = "comp_code")
    private String compCode;
}

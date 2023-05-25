/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Entity
@Table(name = "seq_table")
public class SeqTable {

    @EmbeddedId
    private SeqKey key;
    @Column(name = "seq_no")
    private Integer seqNo;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.model;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
/**
 * @author winswe
 */
@Data
@Entity
@Table(name = "seq_table")
public class SeqAccountTable implements java.io.Serializable {
    @EmbeddedId
    private SeqKeyAccount key;
    @Column(name = "seq_no")
    private Integer seqNo;

}
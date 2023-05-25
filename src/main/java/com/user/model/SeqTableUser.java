/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

/**
 * @author winswe
 */
@Data
@Entity
@Table(name = "seq_table_user")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeqTableUser implements java.io.Serializable {

    @EmbeddedId
    private SeqKeyUser key;
    @Column(name = "seq_no")
    private Integer seqNo;
}

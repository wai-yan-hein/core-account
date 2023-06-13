/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author winswe
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "seq_table_user")
public class SeqTableUser implements java.io.Serializable {

    @EmbeddedId
    private SeqKeyUser key;
    @Column(name = "seq_no")
    private Integer seqNo;
}

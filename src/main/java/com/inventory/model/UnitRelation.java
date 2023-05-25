/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name = "unit_relation")public class UnitRelation {

    @EmbeddedId
    private RelationKey key;
    @Column(name = "rel_name")
    private String relName;
    @Column(name = "intg_upd_status")
    private String intgUpdStatus;
    @Column(name = "updated_date")
    private Date updatedDate;
    private transient List<UnitRelationDetail> detailList;

    public UnitRelation() {
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
@Embeddable
public class MachinePropertyKey implements Serializable {

    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "prop_key")
    private String propKey;

    public MachinePropertyKey(Integer macId, String propKey) {
        this.macId = macId;
        this.propKey = propKey;
    }

    public MachinePropertyKey() {
    }

}

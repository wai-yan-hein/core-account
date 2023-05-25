/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

/**
 * @author SAI
 */
@Data
@Embeddable
public class SeqKeyUser implements Serializable {
    @Column(name = "option")
    private String option;
    @Column(name = "period")
    private String period;

    public SeqKeyUser() {
    }

    public SeqKeyUser(String option, String period) {
        this.option = option;
        this.period = period;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author winswe
 */
@Data
public class VUsrCompAssign implements java.io.Serializable {

    private UsrCompRoleKey key;
    private String compName;

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author wai yan
 */
@Data
public class UsrCompRoleKey implements Serializable {
    private String userCode;
    private String compCode;
    private String roleCode;
}

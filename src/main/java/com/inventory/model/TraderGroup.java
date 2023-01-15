/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;

/**
 *
 * @author Lenovo
 */
@Data
public class TraderGroup {

    private TraderGroupKey key;
    private String userCode;
    private String groupName;
    private String account;

    public TraderGroup(String groupCode, String groupName) {
        this.key = new TraderGroupKey();
        key.setGroupCode(groupCode);
        this.groupName = groupName;
    }

    public TraderGroup() {
    }

}

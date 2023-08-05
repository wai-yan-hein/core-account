/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author Lenovo
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TraderGroup {

    private TraderGroupKey key;
    private String userCode;
    private String groupName;

    public TraderGroup(String groupCode, String groupName) {
        this.key = new TraderGroupKey();
        key.setGroupCode(groupCode);
        this.groupName = groupName;
    }

    public TraderGroup() {
    }

    @Override
    public String toString() {
        return groupName;
    }
    

}

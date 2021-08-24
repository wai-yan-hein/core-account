/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * @author WSwe
 */
@Data
public class MachineInfo implements java.io.Serializable {

    private Integer machineId;
    private String machineName;
    private String ipAddress;
    private Date regDate;
    private Date updatedDate;
}

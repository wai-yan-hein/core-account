/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.user.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author WSwe
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "machine_info")
public class MachineInfo {

    @Id
    @Column(name = "mac_id")
    private Integer macId;
    @Column(name = "mac_name")
    private String machineName;
    @Column(name = "mac_ip")
    private String machineIp;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedDate;
    @Column(name = "pro_update")
    private boolean proUpdate;
    @Column(name = "mac_address")
    private String macAddress;
    @Column(name = "serial_no")
    private String serialNo;
    @Column(name = "os_name")
    private String osName;
    @Column(name = "os_version")
    private String osVersion;
    @Column(name = "os_arch")
    private String osArch;

    public MachineInfo(Integer macId, String machineName) {
        this.macId = macId;
        this.machineName = machineName;
    }

    public MachineInfo() {
    }
    

}

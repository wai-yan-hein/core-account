package com.dms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContractDto {

    private String contractNo;
    private String compCode;
    private String contractName;
    private String productName;
    private String traderCode;
    private String traderName;
    private String agentCode;
    private String agentName;
    private String remark;
    private Integer statusId;
    private String statusName;
    private String statusColor;
    private LocalDateTime contractDate;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Boolean deleted;
    private String offerNo;
    private String curCode;
    private ZonedDateTime zonedDateTime;

}

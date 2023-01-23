
package com.user.model;

import lombok.Data;
import java.util.Date;


/**
 *
 * @author DELL
 */
@Data
public class CurExchange {
    private ExchangeKey key;
    private Date exDate;
    private String homeCur;
    private String exCur;
    private String remark;
    private Double exRate;
    private Date createdDate;
    private String createdBy;
    private String updatedBy;
    private Date updatedDate;
    private Integer macId;
    private boolean deleted;
}

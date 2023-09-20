package cv.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class StockValue {
    private String stockUserCode;
    private String stockCode;
    private String stockName;
    private String stockTypeUserCode;
    private String stockTypeName;
    private String balRel;
    private Float purAvgPrice;
    private Float purAvgAmount;
    private Float inAvgPrice;
    private Float inAvgAmount;
    private Float stdPrice;
    private Float stdAmount;
    private Float recentPrice;
    private Float recentAmt;
    private Float fifoPrice;
    private Float fifoAmt;
    private Float lifoPrice;
    private Float lifoAmt;
    private Float ioRecentPrice;
    private Float ioRecentAmt;
    private double qty;
    private String relation;
}

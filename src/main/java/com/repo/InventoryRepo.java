/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.repo;

import com.H2Repo;
import com.acc.model.VDescription;
import com.inventory.entity.CFont;
import com.common.Global;
import com.common.ProUtil;
import com.common.ReportFilter;
import com.common.ReturnObject;
import com.common.Util1;
import com.inventory.dto.SaleNote;
import com.inventory.entity.AccSetting;
import com.inventory.entity.Category;
import com.inventory.entity.CategoryKey;
import com.inventory.entity.Expense;
import com.inventory.entity.GRN;
import com.inventory.entity.GRNDetail;
import com.inventory.entity.GRNKey;
import com.inventory.entity.General;
import com.inventory.entity.GradeDetail;
import com.inventory.entity.GradeDetailKey;
import com.inventory.entity.Job;
import com.inventory.entity.JobKey;
import com.inventory.entity.LabourGroup;
import com.inventory.entity.LabourGroupKey;
import com.inventory.entity.LabourPaymentDetail;
import com.inventory.entity.LabourPaymentDto;
import com.inventory.entity.LandingHisPrice;
import com.inventory.entity.LandingHis;
import com.inventory.entity.LandingHisGrade;
import com.inventory.entity.LandingHisKey;
import com.inventory.entity.LandingHisQty;
import com.inventory.entity.Location;
import com.inventory.entity.LocationKey;
import com.inventory.entity.Message;
import com.inventory.entity.MessageType;
import com.inventory.entity.MillingExpense;
import com.inventory.entity.MillingHis;
import com.inventory.entity.MillingHisKey;
import com.inventory.entity.MillingOutDetail;
import com.inventory.entity.MillingRawDetail;
import com.inventory.entity.MillingUsage;
import com.inventory.entity.OPHis;
import com.inventory.entity.OPHisDetail;
import com.inventory.entity.OPHisKey;
import com.inventory.entity.OrderHis;
import com.inventory.entity.OrderHisDetail;
import com.inventory.entity.OrderHisKey;
import com.inventory.entity.OrderStatus;
import com.inventory.entity.OrderStatusKey;
import com.inventory.entity.OutputCost;
import com.inventory.entity.OutputCostKey;
import com.inventory.entity.Pattern;
import com.inventory.entity.PaymentHis;
import com.inventory.entity.PaymentHisDetail;
import com.inventory.entity.PriceOption;
import com.inventory.entity.ProcessHis;
import com.inventory.entity.ProcessHisDetail;
import com.inventory.entity.ProcessHisDetailKey;
import com.inventory.entity.ProcessHisKey;
import com.inventory.entity.PurExpense;
import com.inventory.entity.PurHis;
import com.inventory.entity.PurHisDetail;
import com.inventory.entity.PurHisKey;
import com.inventory.entity.PurOrderHis;
import com.inventory.entity.PurOrderHisDetail;
import com.inventory.entity.PurOrderHisKey;
import com.inventory.entity.Region;
import com.inventory.entity.RegionKey;
import com.inventory.entity.RelationKey;
import com.inventory.entity.ReorderLevel;
import com.inventory.entity.RetInHis;
import com.inventory.entity.RetInHisDetail;
import com.inventory.entity.RetInHisKey;
import com.inventory.entity.RetOutHis;
import com.inventory.entity.RetOutHisDetail;
import com.inventory.entity.RetOutHisKey;
import com.inventory.entity.SaleExpense;
import com.inventory.entity.SaleHis;
import com.inventory.entity.SaleHisDetail;
import com.inventory.entity.SaleHisKey;
import com.inventory.entity.SaleMan;
import com.inventory.entity.SaleManKey;
import com.inventory.entity.SaleOrderJoin;
import com.inventory.entity.Stock;
import com.inventory.entity.StockBrand;
import com.inventory.entity.StockBrandKey;
import com.inventory.entity.StockColor;
import com.inventory.entity.StockCriteria;
import com.inventory.entity.StockFormula;
import com.inventory.entity.StockFormulaPrice;
import com.inventory.entity.StockFormulaPriceKey;
import com.inventory.entity.StockFormulaKey;
import com.inventory.entity.StockFormulaQty;
import com.inventory.entity.StockIOKey;
import com.inventory.entity.StockInOut;
import com.inventory.entity.StockInOutDetail;
import com.inventory.entity.ConsignHisDetail;
import com.inventory.entity.ConsignHis;
import com.inventory.entity.ConsignHisKey;
import com.inventory.entity.OrderFileJoin;
import com.inventory.entity.OrderNote;
import com.inventory.entity.StockKey;
import com.inventory.entity.StockPayment;
import com.inventory.entity.StockPaymentDetail;
import com.inventory.entity.StockType;
import com.inventory.entity.StockTypeKey;
import com.inventory.entity.StockUnit;
import com.inventory.entity.StockUnitKey;
import com.inventory.entity.Trader;
import com.inventory.entity.TraderGroup;
import com.inventory.entity.TraderGroupKey;
import com.inventory.entity.TraderKey;
import com.inventory.entity.TransferHis;
import com.inventory.entity.TransferHisDetail;
import com.inventory.entity.TransferHisKey;
import com.inventory.entity.UnitRelation;
import com.inventory.entity.UnitRelationDetail;
import com.inventory.entity.VLanding;
import com.inventory.entity.VPurchase;
import com.inventory.entity.VOrder;
import com.inventory.entity.VPurOrder;
import com.inventory.entity.VReturnIn;
import com.inventory.entity.VReturnOut;
import com.inventory.entity.VSale;
import com.inventory.entity.VStockIO;
import com.inventory.entity.VConsign;
import com.inventory.entity.VTransfer;
import com.inventory.entity.VouDiscount;
import com.inventory.entity.VouStatus;
import com.inventory.entity.VouStatusKey;
import com.inventory.entity.WareHouse;
import com.inventory.entity.WareHouseKey;
import com.inventory.entity.WeightHis;
import com.inventory.entity.WeightHisDetail;
import com.inventory.entity.WeightHisKey;
import com.inventory.entity.WeightLossDetail;
import com.inventory.entity.WeightLossHis;
import com.inventory.entity.WeightLossHisKey;
import com.model.WeightColumn;
import com.ui.management.model.ClosingBalance;
import java.time.Duration;
import java.util.List;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class InventoryRepo {

    @Autowired
    private WebClient inventoryApi;
    @Autowired
    public boolean localDatabase;
    @Autowired
    private H2Repo h2Repo;

    public Mono<Location> getDefaultLocation() {
        String locCode = Global.hmRoleProperty.get(ProUtil.DEFAULT_LOCATION);
        String locCodeByUser = Global.loginUser.getLocCode();
        return findLocation(Util1.isNull(locCodeByUser, locCode));
    }

    public Mono<Stock> getDefaultStock() {
        String stockCode = Global.hmRoleProperty.get(ProUtil.DEFAULT_LOCATION);
        return findStock(stockCode);
    }

    public Mono<SaleMan> getDefaultSaleMan() {
        String code = Global.hmRoleProperty.get(ProUtil.DEFAULT_SALEMAN);
        return findSaleMan(code);
    }

    public Mono<Trader> getDefaultCustomer() {
        String code = Global.hmRoleProperty.get(ProUtil.DEFAULT_CUSTOMER);
        return findTrader(code);
    }

    public Mono<Trader> getDefaultSupplier() {
        String code = Global.hmRoleProperty.get(ProUtil.DEFAULT_SUPPLIER);
        return findTrader(code);
    }

    public Mono<List<PriceOption>> getPriceOption(String option) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getPriceOption")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .queryParam("option", option)
                .build())
                .retrieve()
                .bodyToFlux(PriceOption.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<PriceOption>> getUpdatePriceOption(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdatePriceOption")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(PriceOption.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Pattern>> getUpdatePattern(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdatePattern")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(Pattern.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Job>> getUpdateJob(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateJob")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(Job.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<OutputCost>> getOutputCost(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateOutputCost")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(OutputCost.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<AccSetting>> getAccSetting(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdatedAccSetting")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(AccSetting.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<WareHouse>> getWarehouse(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/warehouse/getUpdatedWarehouse")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(WareHouse.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Category>> getCategory() {
        if (localDatabase) {
            return h2Repo.getCategory();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getCategory")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(Category.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockFormula>> getStockFormula() {
        if (localDatabase) {
            return h2Repo.getStockFormula(Global.compCode);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getStockFormula")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(StockFormula.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockFormulaPrice>> getStockFormulaPrice(String formulaCode) {
        if (localDatabase) {
            return h2Repo.getStockFormulaPrice(formulaCode);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getStockFormulaPrice")
                .queryParam("compCode", Global.compCode)
                .queryParam("formulaCode", formulaCode)
                .build())
                .retrieve()
                .bodyToFlux(StockFormulaPrice.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockFormulaQty>> getStockFormulaQty(String formulaCode) {
        if (localDatabase) {
            return h2Repo.getStockFormulaQty(formulaCode);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getStockFormulaQty")
                .queryParam("compCode", Global.compCode)
                .queryParam("formulaCode", formulaCode)
                .build())
                .retrieve()
                .bodyToFlux(StockFormulaQty.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<GradeDetail>> getGradeDetail(String formulaCode, String criteriaCode) {
        if (localDatabase) {
            return h2Repo.getGradeDetail(formulaCode, criteriaCode);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getGradeDetail")
                .queryParam("compCode", Global.compCode)
                .queryParam("formulaCode", formulaCode)
                .queryParam("criteriaCode", criteriaCode)
                .build())
                .retrieve()
                .bodyToFlux(GradeDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<GradeDetail>> getStockFormulaGrade(String formulaCode) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getStockFormulaGrade")
                .queryParam("compCode", Global.compCode)
                .queryParam("formulaCode", formulaCode)
                .build())
                .retrieve()
                .bodyToFlux(GradeDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<LandingHisPrice>> getLandingHisPrice(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/landing/getLandingHisPrice")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(LandingHisPrice.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<LandingHisQty>> getLandingHisQty(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/landing/getLandingHisQty")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(LandingHisQty.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<LandingHisGrade>> getLandingHisGrade(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/landing/getLandingHisGrade")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(LandingHisGrade.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<LandingHisGrade> getLandingChooseGrade(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/landing/getLandingChooseGrade")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(LandingHisGrade.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Category>> getUpdateCategory(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateCategory")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(Category.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });

    }

    public Mono<List<SaleMan>> getSaleMan() {
        if (localDatabase) {
            return h2Repo.getSaleMan();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getSaleMan")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(SaleMan.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<SaleMan>> getUpdateSaleMan(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateSaleMan")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(SaleMan.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<SaleMan> findSaleMan(String code) {
        SaleManKey key = new SaleManKey();
        key.setCompCode(Global.compCode);
        key.setSaleManCode(code);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/findSaleMan")
                .body(Mono.just(key), SaleManKey.class)
                .retrieve()
                .bodyToMono(SaleMan.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockBrand>> getStockBrand() {
        if (localDatabase) {
            return h2Repo.getBrand();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getBrand")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(StockBrand.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockBrand>> getUpdateBrand(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateBrand")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(StockBrand.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockType>> getStockType() {
        if (localDatabase) {
            return h2Repo.getStockType();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getType")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(StockType.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockType>> getUpdateStockType(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateStockType")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(StockType.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockUnit>> getStockUnit() {
        if (localDatabase) {
            return h2Repo.getStockUnit();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUnit")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(StockUnit.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockUnit>> getUpdateUnit(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateUnit")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(StockUnit.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockUnit>> getUnit(String relCode, Integer deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getRelation")
                .queryParam("relCode", relCode)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve()
                .bodyToFlux(StockUnit.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Trader> findTrader(String code) {
        TraderKey key = new TraderKey();
        key.setCode(Util1.isNull(code, "-"));
        key.setCompCode(Global.compCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/findTrader")
                .body(Mono.just(key), TraderKey.class)
                .retrieve()
                .bodyToMono(Trader.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });

    }

    public Mono<TraderGroup> findTraderGroup(String code, Integer deptId) {
        TraderGroupKey key = new TraderGroupKey();
        key.setGroupCode(Util1.isNull(code, "-"));
        key.setCompCode(Global.compCode);
        return inventoryApi.post()
                .uri("/setup/findTraderGroup")
                .body(Mono.just(key), TraderGroupKey.class)
                .retrieve()
                .bodyToMono(TraderGroup.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Region> findRegion(String code) {
        log.info("region code : " + code);
        if (Util1.isNullOrEmpty(code)) {
            return Mono.empty();
        }
        RegionKey key = new RegionKey();
        key.setRegCode(code);
        key.setCompCode(Global.compCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/findRegion")
                .body(Mono.just(key), RegionKey.class)
                .retrieve()
                .bodyToMono(Region.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Region>> getUpdateRegion(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateRegion")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(Region.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Trader>> getCustomer() {
        if (localDatabase) {
            return h2Repo.getCustomer();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getCustomer")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve().bodyToFlux(Trader.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<OutputCost>> getOutputCost() {
        if (localDatabase) {
            return h2Repo.getOutputCost();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getOutputCost")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(OutputCost.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Trader>> getEmployee() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getEmployee")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve().bodyToFlux(Trader.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Trader>> getSupplier() {
        if (localDatabase) {
            return h2Repo.getSupplier();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getSupplier")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(Trader.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Trader>> getTraderList(String text, String type) {
        if (localDatabase) {
            return h2Repo.searchTrader(text, type, Global.compCode, ProUtil.getDepId());
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getTraderList")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .queryParam("text", text)
                .queryParam("type", type)
                .build())
                .retrieve()
                .bodyToFlux(Trader.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("getTraderList :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Trader>> getUpdateTrader(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateTrader")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(Trader.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<GRN>> getBatchList(String batchNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/grn/getBatchList")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .queryParam("batchNo", batchNo)
                .build())
                .retrieve().bodyToFlux(GRN.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<GRN> findByBatchNo(String batchNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/grn/findByBatchNo")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .queryParam("batchNo", batchNo)
                .build())
                .retrieve().bodyToMono(GRN.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<GRN> findGRN(String vouNo) {
        GRNKey key = new GRNKey();
        key.setVouNo(vouNo);
        key.setCompCode(Global.compCode);
        return inventoryApi.post()
                .uri("/grn/findGRN")
                .body(Mono.just(key), GRNKey.class)
                .retrieve()
                .bodyToMono(GRN.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Expense>> getExpense() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/expense/getExpense")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(Expense.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Trader> findTraderRFID(String rfId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/findTraderRFID")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .queryParam("rfId", rfId)
                .build())
                .retrieve()
                .bodyToMono(Trader.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Region>> getRegion() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getRegion")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(Region.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Location> findLocation(String locCode) {
        if (Util1.isNullOrEmpty(locCode)) {
            return Mono.empty();
        }
        LocationKey key = new LocationKey();
        key.setCompCode(Global.compCode);
        key.setLocCode(locCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/findLocation")
                .body(Mono.just(key), LocationKey.class)
                .retrieve()
                .bodyToMono(Location.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockBrand> findBrand(String brandCode) {
        StockBrandKey key = new StockBrandKey();
        key.setCompCode(Global.compCode);
        key.setBrandCode(brandCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/findBrand")
                .body(Mono.just(key), StockBrandKey.class)
                .retrieve()
                .bodyToMono(StockBrand.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Job> findJob(String jobCode) {
        JobKey key = new JobKey();
        key.setCompCode(Global.compCode);
        key.setJobNo(jobCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/findJob")
                .body(Mono.just(key), JobKey.class)
                .retrieve()
                .bodyToMono(Job.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<LabourGroup> findLabourGroup(String code) {
        if (Util1.isNullOrEmpty(code)) {
            return Mono.empty();
        }
        LabourGroupKey key = new LabourGroupKey();
        key.setCompCode(Global.compCode);
        key.setCode(code);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/findLabourGroup")
                .body(Mono.just(key), LabourGroupKey.class)
                .retrieve()
                .bodyToMono(LabourGroup.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<WareHouse> findWareHouse(String code) {
        WareHouseKey key = new WareHouseKey();
        key.setCompCode(Global.compCode);
        key.setCode(code);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/warehouse/findWareHouse")
                .body(Mono.just(key), WareHouseKey.class)
                .retrieve()
                .bodyToMono(WareHouse.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<VouStatus> findVouStatus(String code) {
        VouStatusKey key = new VouStatusKey();
        key.setCompCode(Global.compCode);
        key.setCode(code);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/findVouStatus")
                .body(Mono.just(key), VouStatusKey.class)
                .retrieve()
                .bodyToMono(VouStatus.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<OrderStatus> findOrderStatus(String code) {
        OrderStatusKey key = new OrderStatusKey();
        key.setCompCode(Global.compCode);
        key.setCode(code);
        if (localDatabase) {
            return h2Repo.find(key);
        }

        return inventoryApi.post()
                .uri("/setup/findOrderStatus")
                .body(Mono.just(key), OrderStatusKey.class)
                .retrieve()
                .bodyToMono(OrderStatus.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockInOutDetail>> getStockIODetail(String vouNo, boolean local) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/stockio/getStockIODetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(StockInOutDetail.class)
                .collectList();
    }

    public Mono<List<StockInOutDetail>> getStockIODetailByJob(String jobNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/stockio/getStockIODetailByJob")
                .queryParam("jobId", jobNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(StockInOutDetail.class)
                .collectList();
    }

    public Mono<StockUnit> findUnit(String unitCode) {
        StockUnitKey key = new StockUnitKey();
        key.setCompCode(Global.compCode);
        key.setUnitCode(unitCode);
        if (localDatabase) {
            return h2Repo.findUnit(key);
        }
        return inventoryApi.post()
                .uri("/setup/findUnit")
                .body(Mono.just(key), StockUnitKey.class)
                .retrieve()
                .bodyToMono(StockUnit.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<UnitRelation> findRelation(String relCode) {
        RelationKey key = new RelationKey();
        key.setCompCode(Global.compCode);
        key.setRelCode(relCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/findUnitRelation")
                .body(Mono.just(key), RelationKey.class)
                .retrieve()
                .bodyToMono(UnitRelation.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockFormula> findStockFormula(String formualCode) {
        if (localDatabase) {

        }
        StockFormulaKey key = new StockFormulaKey();
        key.setCompCode(Global.compCode);
        key.setFormulaCode(formualCode);
        if (localDatabase) {
            return h2Repo.findStockFormula(key);
        }
        return inventoryApi.post()
                .uri("/setup/findStockFormula")
                .body(Mono.just(key), StockFormulaKey.class)
                .retrieve()
                .bodyToMono(StockFormula.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Category> findCategory(String catCode) {
        CategoryKey key = new CategoryKey();
        key.setCompCode(Global.compCode);
        key.setCatCode(catCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/findCategory")
                .body(Mono.just(key), CategoryKey.class)
                .retrieve()
                .bodyToMono(Category.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Stock> findStock(String stockCode) {
        StockKey key = new StockKey();
        key.setCompCode(Global.compCode);
        key.setStockCode(stockCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/findStock")
                .body(Mono.just(key), StockKey.class)
                .retrieve()
                .bodyToMono(Stock.class)
                .onErrorResume((e) -> {
                    log.error("findStock : " + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockType> findGroup(String typeCode) {
        StockTypeKey key = new StockTypeKey();
        key.setCompCode(Global.compCode);
        key.setStockTypeCode(typeCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/findType")
                .body(Mono.just(key), StockTypeKey.class)
                .retrieve()
                .bodyToMono(StockType.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<WeightLossHis> findWeightLoss(WeightLossHisKey key) {
        return inventoryApi.post()
                .uri("/weight/findWeightLoss")
                .body(Mono.just(key), WeightLossHisKey.class)
                .retrieve()
                .bodyToMono(WeightLossHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<WeightLossDetail>> getWeightLossDetail(String vouNo, Integer deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/weight/getWeightLossDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve()
                .bodyToFlux(WeightLossDetail.class)
                .collectList();
    }

    public Mono<ProcessHis> findProcess(ProcessHisKey key, boolean local) {
        if (local) {
            return h2Repo.findProcess(key);
        }
        return inventoryApi.post()
                .uri("/process/findProcess")
                .body(Mono.just(key), ProcessHisKey.class)
                .retrieve()
                .bodyToMono(ProcessHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Location>> getLocation() {
        if (localDatabase) {
            return h2Repo.getLocation();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getLocation")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(Location.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Location>> getUpdateLocation(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateLocation")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(Location.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<LabourGroup>> getUpdateLabourGroup(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateLabourGroup")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(LabourGroup.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Stock>> getStock(boolean active) {
        if (localDatabase) {
            return h2Repo.getStock(active);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getStock")
                .queryParam("compCode", Global.compCode)
                .queryParam("active", active)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(Stock.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Flux<Stock> searchStock(ReportFilter filter) {
        if (localDatabase) {
            return h2Repo.searchStock(filter);
        }
        return inventoryApi
                .post()
                .uri("/setup/searchStock")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(Stock.class);
    }

    public Mono<List<Stock>> getUpdateStock(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateStock")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(Stock.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockFormula>> getUpdateStockFormula(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateStockFormula")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(StockFormula.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockFormulaPrice>> getUpdateStockFormulaPrice(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateStockFormulaPrice")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(StockFormulaPrice.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockFormulaQty>> getUpdateStockFormulaQty(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateStockFormulaQty")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(StockFormulaQty.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<GradeDetail>> getUpdateGradeDetail(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateGradeDetail")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(GradeDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockCriteria>> getUpdateStockCriteria(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateStockCriteria")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(StockCriteria.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Stock>> getStock(String str) {
        if (localDatabase) {
            return h2Repo.getStock(str);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getStockList")
                .queryParam("text", str)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(Stock.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockCriteria>> searchStockCriteria(String str) {
        if (localDatabase) {
            //return h2Repo.getStock(str);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/searchStockCriteria")
                .queryParam("text", str)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(StockCriteria.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockCriteria>> getStockCriteria(boolean active) {
        if (localDatabase) {
            //return h2Repo.getStock(str);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getStockCriteria")
                .queryParam("active", active)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(StockCriteria.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Stock>> getService() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getService")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(Stock.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<General>> deleteStock(StockKey key) {
        return inventoryApi.post()
                .uri("/setup/deleteStock")
                .body(Mono.just(key), StockKey.class)
                .retrieve()
                .bodyToFlux(General.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restoreStock(StockKey key) {
        return inventoryApi.post()
                .uri("/setup/restoreStock")
                .body(Mono.just(key), StockKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<VouStatus>> getVoucherStatus() {
        if (localDatabase) {
            return h2Repo.getVouStatus();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getVouStatus")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(VouStatus.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<VouStatus>> getUpdateVouStatus(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateVouStatus")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(VouStatus.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<OrderStatus>> getOrderStatus() {
        if (localDatabase) {
            return h2Repo.getOrderStatus();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getOrderStatus")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(OrderStatus.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<LabourGroup>> getLabourGroup() {
        if (localDatabase) {
            return h2Repo.getLabourGroup();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getLabourGroup")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(LabourGroup.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<WareHouse>> getWareHouse() {
        if (localDatabase) {
            return h2Repo.getWarehouse();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/warehouse/getWareHouse")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(WareHouse.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockColor>> getStockColor() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getStockColor")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(StockColor.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Job>> getJob(ReportFilter ReportFilter) {
        if (localDatabase) {
            return h2Repo.getJob(ReportFilter);
        }
        return inventoryApi.post()
                .uri("/setup/getJob")
                .body(Mono.just(ReportFilter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(Job.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<OrderStatus>> getUpdateOrderStatus(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateOrderStatus")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(OrderStatus.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<UnitRelation>> getUnitRelation() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUnitRelation")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(UnitRelation.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<UnitRelation>> getUpdateRelation(String updatedDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUpdateRelation")
                .queryParam("updatedDate", updatedDate)
                .build())
                .retrieve()
                .bodyToFlux(UnitRelation.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Trader> saveTrader(Trader t) {
        return inventoryApi.post()
                .uri("/setup/saveTrader")
                .body(Mono.just(t), Trader.class)
                .retrieve()
                .bodyToMono(Trader.class)
                .doOnSuccess((s) -> {
                    if (localDatabase) {
                        h2Repo.save(s);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<OutputCost> saveOutputCost(OutputCost t) {
        return inventoryApi.post()
                .uri("/setup/saveOutputCost")
                .body(Mono.just(t), OutputCost.class)
                .retrieve()
                .bodyToMono(OutputCost.class)
                .doOnSuccess((s) -> {
                    if (localDatabase) {
                        h2Repo.save(s);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Stock> saveStock(Stock s) {
        return inventoryApi.post()
                .uri("/setup/saveStock")
                .body(Mono.just(s), Stock.class)
                .retrieve()
                .bodyToMono(Stock.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Location> saveLocation(Location loc) {
        return inventoryApi.post()
                .uri("/setup/saveLocation")
                .body(Mono.just(loc), Location.class)
                .retrieve()
                .bodyToMono(Location.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Region> saveRegion(Region reg) {
        return inventoryApi.post()
                .uri("/setup/saveRegion")
                .body(Mono.just(reg), Region.class)
                .retrieve()
                .bodyToMono(Region.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.error(e);
                });
    }

    public Mono<SaleMan> saveSaleMan(SaleMan s) {
        return inventoryApi.post()
                .uri("/setup/saveSaleMan")
                .body(Mono.just(s), SaleMan.class)
                .retrieve()
                .bodyToMono(SaleMan.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.error(e);
                });
    }

    public Mono<StockBrand> saveBrand(StockBrand s) {
        return inventoryApi.post()
                .uri("/setup/saveBrand")
                .body(Mono.just(s), StockBrand.class)
                .retrieve()
                .bodyToMono(StockBrand.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.error(e);
                });
    }

    public Mono<Expense> saveExpense(Expense s) {
        return inventoryApi.post()
                .uri("/expense/saveExpense")
                .body(Mono.just(s), Expense.class)
                .retrieve()
                .bodyToMono(Expense.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockType> saveStockType(StockType t) {
        return inventoryApi.post()
                .uri("/setup/saveType")
                .body(Mono.just(t), StockType.class)
                .retrieve()
                .bodyToMono(StockType.class)
                .doOnSuccess((st) -> {
                    if (localDatabase) {
                        h2Repo.save(st);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.error(e);
                });
    }

    public Mono<StockUnit> saveStockUnit(StockUnit unit) {
        return inventoryApi.post()
                .uri("/setup/saveUnit")
                .body(Mono.just(unit), StockUnit.class)
                .retrieve()
                .bodyToMono(StockUnit.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.error(e);
                });
    }

    public Mono<VouStatus> saveVouStatus(VouStatus vou) {
        return inventoryApi.post()
                .uri("/setup/saveVoucherStatus")
                .body(Mono.just(vou), VouStatus.class)
                .retrieve()
                .bodyToMono(VouStatus.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<OrderStatus> saveOrderStatus(OrderStatus vou) {
        return inventoryApi.post()
                .uri("/setup/saveOrderStatus")
                .body(Mono.just(vou), OrderStatus.class)
                .retrieve()
                .bodyToMono(OrderStatus.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<LabourGroup> saveLabourGroup(LabourGroup vou) {
        return inventoryApi.post()
                .uri("/setup/saveLabourGroup")
                .body(Mono.just(vou), LabourGroup.class)
                .retrieve()
                .bodyToMono(LabourGroup.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockColor> saveStockColor(StockColor color) {
        return inventoryApi.post()
                .uri("/setup/saveStockColor")
                .body(Mono.just(color), StockColor.class)
                .retrieve()
                .bodyToMono(StockColor.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<WareHouse> saveWareHouse(WareHouse vou) {
        return inventoryApi.post()
                .uri("/warehouse/saveWareHouse")
                .body(Mono.just(vou), WareHouse.class)
                .retrieve()
                .bodyToMono(WareHouse.class)
                .doOnSuccess((w) -> {
                    if (localDatabase) {
                        h2Repo.save(w);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Job> saveJob(Job job) {
        return inventoryApi.post()
                .uri("/setup/saveJob")
                .body(Mono.just(job), Job.class)
                .retrieve()
                .bodyToMono(Job.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Category> saveCategory(Category category) {
        return inventoryApi.post()
                .uri("/setup/saveCategory")
                .body(Mono.just(category), Category.class)
                .retrieve()
                .bodyToMono(Category.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.error(e);
                });
    }

    public Mono<Pattern> savePattern(Pattern pattern) {
        return inventoryApi.post()
                .uri("/setup/savePattern")
                .body(Mono.just(pattern), Pattern.class)
                .retrieve()
                .bodyToMono(Pattern.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockCriteria> saveStockCriteria(StockCriteria s) {
        return inventoryApi.post()
                .uri("/setup/saveStockCriteria")
                .body(Mono.just(s), StockCriteria.class)
                .retrieve()
                .bodyToMono(StockCriteria.class)
                .doOnSuccess((s1) -> {
                    if (localDatabase) {
                        h2Repo.save(s1);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockFormula> saveStockFormula(StockFormula s) {
        return inventoryApi.post()
                .uri("/setup/saveStockFormula")
                .body(Mono.just(s), StockFormula.class)
                .retrieve()
                .bodyToMono(StockFormula.class)
                .doOnSuccess((s1) -> {
                    if (localDatabase) {
                        h2Repo.save(s1);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockFormulaPrice> saveStockFormulaPrice(StockFormulaPrice s) {
        return inventoryApi.post()
                .uri("/setup/saveStockFormulaPrice")
                .body(Mono.just(s), StockFormulaPrice.class)
                .retrieve()
                .bodyToMono(StockFormulaPrice.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockFormulaQty> saveStockFormulaQty(StockFormulaQty s) {
        return inventoryApi.post()
                .uri("/setup/saveStockFormulaQty")
                .body(Mono.just(s), StockFormulaQty.class)
                .retrieve()
                .bodyToMono(StockFormulaQty.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<GradeDetail> saveGradeDetail(GradeDetail s) {
        return inventoryApi.post()
                .uri("/setup/saveGradeDetail")
                .body(Mono.just(s), GradeDetail.class)
                .retrieve()
                .bodyToMono(GradeDetail.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<WeightLossHis> saveWeightLoss(WeightLossHis loss) {
        return inventoryApi.post()
                .uri("/weight/saveWeightLoss")
                .body(Mono.just(loss), WeightLossHis.class)
                .retrieve()
                .bodyToMono(WeightLossHis.class)
                .onErrorResume(e -> {
                    if (localDatabase) {
                        int status = JOptionPane.showConfirmDialog(Global.parentForm,
                                "Can't save voucher to cloud. Do you want save local?",
                                "Offline", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (status == JOptionPane.YES_OPTION) {
                            return h2Repo.save(loss);
                        }
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<WeightLossHis> uploadWeightLoss(WeightLossHis loss) {
        return inventoryApi.post()
                .uri("/weight/saveWeightLoss")
                .body(Mono.just(loss), WeightLossHis.class)
                .retrieve()
                .bodyToMono(WeightLossHis.class)
                .onErrorResume(e -> {
                    return Mono.error(e);
                });
    }

    public Mono<Boolean> delete(Pattern p) {
        return inventoryApi.post()
                .uri("/setup/deletePattern")
                .body(Mono.just(p), Pattern.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(StockFormulaPriceKey p) {
        return inventoryApi.post()
                .uri("/setup/deleteStockFormulaDetail")
                .body(Mono.just(p), StockFormulaPriceKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(GradeDetailKey p) {
        return inventoryApi.post()
                .uri("/setup/deleteGradeDetail")
                .body(Mono.just(p), GradeDetailKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnSuccess((s1) -> {
                    if (localDatabase) {
                        h2Repo.delete(p);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> deleteMilling(MillingHis his) {
        MillingHisKey key = his.getKey();
        return inventoryApi.post()
                .uri("/milling/deleteMilling")
                .body(Mono.just(key), MillingHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(LandingHisKey key) {
        return inventoryApi.post()
                .uri("/landing/deleteLanding")
                .body(Mono.just(key), LandingHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(LandingHisKey key) {
        return inventoryApi.post()
                .uri("/landing/restoreLanding")
                .body(Mono.just(key), LandingHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<UnitRelation> saveUnitRelation(UnitRelation rel) {
        return inventoryApi.post()
                .uri("/setup/saveUnitRelation")
                .body(Mono.just(rel), UnitRelation.class)
                .retrieve()
                .bodyToMono(UnitRelation.class)
                .doOnSuccess((t) -> {
                    if (localDatabase) {
                        h2Repo.save(t);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<General> getPrice(String stockCode, String vouDate, String unit) {
        return getPurRecentPrice(stockCode, vouDate, unit)
                .flatMap(t -> {
                    if (t.getAmount() == 0) {
                        return getStockIORecentPrice(stockCode, vouDate, unit);
                    } else {
                        return Mono.just(t);
                    }
                });
    }

    public Mono<General> getPrice(String stockCode, String vouDate, String unit, String type) {
        return switch (type) {
            case "PUR-R" ->
                getPurRecentPrice(stockCode, vouDate, unit);
            case "PUR-A" ->
                getPurAvgPrice(stockCode, vouDate, unit);
            case "PRO-R" ->
                getProductionRecentPrice(stockCode, vouDate, unit);
            case "WL-R" ->
                getWeightLossRecentPrice(stockCode, vouDate, unit);
            default ->
                null;
        };
    }

    public Mono<General> getPurRecentPrice(String stockCode, String vouDate, String unit) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getPurchaseRecentPrice")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(General.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<General> getWeightLossRecentPrice(String stockCode, String vouDate, String unit) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getWeightLossRecentPrice")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToMono(General.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<General> getPurAvgPrice(String stockCode, String vouDate, String unit) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getPurAvgPrice")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(General.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<General> getProductionRecentPrice(String stockCode, String vouDate, String unit) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getProductionRecentPrice")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(General.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<General> getSaleRecentPrice(String stockCode, String vouDate, String unit) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getSaleRecentPrice")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(General.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<General> getStockIORecentPrice(String stockCode, String vouDate, String unit) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getStockIORecentPrice")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .build())
                .retrieve()
                .bodyToMono(General.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<General> getWeightAvgPrice(String stockCode, String locCode) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getWeightAvgPrice")
                .queryParam("stockCode", stockCode)
                .queryParam("locCode", locCode)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(General.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<UnitRelationDetail>> getRelationDetail(String code, Integer deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getUnitRelationDetail")
                .queryParam("code", code)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve()
                .bodyToFlux(UnitRelationDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<CFont>> getFont() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-font")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(CFont.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockInOut> findStockIO(String vouNo, boolean local) {
        StockIOKey key = new StockIOKey();
        key.setCompCode(Global.compCode);
        key.setVouNo(vouNo);
        return inventoryApi.post()
                .uri("/stockio/findStockIO")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(StockInOut.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<TransferHis> findTransfer(String vouNo, Integer deptId, boolean local) {
        TransferHisKey key = new TransferHisKey();
        key.setCompCode(Global.compCode);
        key.setVouNo(vouNo);
        if (local) {
            return h2Repo.findTransfer(key);
        }
        return inventoryApi.post()
                .uri("/transfer/findTransfer")
                .body(Mono.just(key), TransferHisKey.class)
                .retrieve()
                .bodyToMono(TransferHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<SaleHis> findSale(String vouNo) {
        SaleHisKey key = new SaleHisKey();
        key.setVouNo(vouNo);
        key.setCompCode(Global.compCode);
        return inventoryApi.post()
                .uri("/sale/findSale")
                .body(Mono.just(key), SaleHisKey.class)
                .retrieve()
                .bodyToMono(SaleHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<OrderNote> findOrderNote(String vouNo) {
//        if (local) {
//            return h2Repo.findSale(key);
//        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/orderNote/findOrderNote")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build()).retrieve().bodyToMono(OrderNote.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<MillingHis> findMilling(String vouNo, Integer deptId, boolean local) {
        MillingHisKey key = new MillingHisKey();
        key.setVouNo(vouNo);
        key.setCompCode(Global.compCode);
        return inventoryApi.post()
                .uri("/milling/findMilling")
                .body(Mono.just(key), MillingHisKey.class)
                .retrieve()
                .bodyToMono(MillingHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<LandingHis> findLanding(String vouNo) {
        LandingHisKey key = new LandingHisKey();
        key.setVouNo(vouNo);
        key.setCompCode(Global.compCode);
        return inventoryApi.post()
                .uri("/landing/findLanding")
                .body(Mono.just(key), LandingHisKey.class)
                .retrieve()
                .bodyToMono(LandingHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<OrderHis> findOrder(String vouNo) {
        OrderHisKey key = new OrderHisKey();
        key.setVouNo(vouNo);
        key.setCompCode(Global.compCode);
        return inventoryApi.post()
                .uri("/order/findOrder")
                .body(Mono.just(key), OrderHisKey.class)
                .retrieve()
                .bodyToMono(OrderHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<OPHis> findOpening(OPHisKey key) {
        return inventoryApi.post()
                .uri("/setup/findOpening")
                .body(Mono.just(key), OPHisKey.class)
                .retrieve()
                .bodyToMono(OPHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<PurHis> findPurchase(String vouNo) {
        PurHisKey key = new PurHisKey();
        key.setCompCode(Global.compCode);
        key.setVouNo(vouNo);
        return inventoryApi.post()
                .uri("/pur/findPur")
                .body(Mono.just(key), PurHisKey.class)
                .retrieve()
                .bodyToMono(PurHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<RetInHis> findReturnIn(String vouNo) {
        RetInHisKey key = new RetInHisKey();
        key.setCompCode(Global.compCode);
        key.setVouNo(vouNo);
        return inventoryApi.post()
                .uri("/returnIn/findReturnIn")
                .body(Mono.just(key), RetInHisKey.class)
                .retrieve()
                .bodyToMono(RetInHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Flux<VReturnIn> getReturnInVoucher(ReportFilter filter) {
        return inventoryApi
                .post()
                .uri("/returnIn/getReturnIn")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VReturnIn.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Flux.empty();
                });
    }

    public Mono<List<RetInHisDetail>> getReturnInDetail(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/returnIn/getReturnInDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(RetInHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<RetOutHis> findReturnOut(String vouNo, Integer deptId, boolean local) {
        RetOutHisKey key = new RetOutHisKey();
        key.setCompCode(Global.compCode);
        key.setVouNo(vouNo);
        if (local) {
            return h2Repo.findRetOutHis(key);
        }
        return inventoryApi.post()
                .uri("/retout/findReturnOut")
                .body(Mono.just(key), RetOutHisKey.class)
                .retrieve()
                .bodyToMono(RetOutHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<VReturnOut>> getReturnOutVoucher(ReportFilter filter) {
        if (filter.isLocal()) {
            return h2Repo.searchReturnOutVoucher(filter);
        }
        return inventoryApi.post()
                .uri("/retout/getReturnOut")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VReturnOut.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<RetOutHisDetail>> getReturnOutDetail(String vouNo, Integer depId, boolean local) {
        if (local) {
            return h2Repo.searchReturnOutDetail(vouNo, depId);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/retout/getReturnOutDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", depId)
                .build())
                .retrieve()
                .bodyToFlux(RetOutHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<General> getSmallQty(String stockCode, String unit) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getSmallQty")
                .queryParam("stockCode", stockCode)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().bodyToMono(General.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<ReorderLevel> saveReorder(ReorderLevel rl) {
        return inventoryApi.post()
                .uri("/setup/saveReorder")
                .body(Mono.just(rl), ReorderLevel.class)
                .retrieve()
                .bodyToMono(ReorderLevel.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<General>> deleteTrader(TraderKey key) {
        return inventoryApi.post()
                .uri("/setup/deleteTrader")
                .body(Mono.just(key), TraderKey.class)
                .retrieve()
                .bodyToFlux(General.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Integer> deleteOutputCost(OutputCostKey key) {
        return inventoryApi.post()
                .uri("/setup/deleteOutputCost")
                .body(Mono.just(key), OutputCostKey.class)
                .retrieve()
                .bodyToMono(Integer.class)
                .doOnSuccess((s1) -> {
                    if (localDatabase) {
                        h2Repo.delete(key);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<TraderGroup> saveTraderGroup(TraderGroup rl) {
        return inventoryApi.post()
                .uri("/setup/saveTraderGroup")
                .body(Mono.just(rl), TraderGroup.class)
                .retrieve()
                .bodyToMono(TraderGroup.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<TraderGroup>> getTraderGroup() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getTraderGroup")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve()
                .bodyToFlux(TraderGroup.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Pattern>> getPattern(String stockCode, String vouDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getPattern")
                .queryParam("stockCode", stockCode)
                .queryParam("compCode", Global.compCode)
                .queryParam("vouDate", vouDate == null ? "-" : vouDate)
                .build())
                .retrieve()
                .bodyToFlux(Pattern.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(OPHisKey key) {
        return inventoryApi.post()
                .uri("/setup/deleteOpening")
                .body(Mono.just(key), OPHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(OPHisKey key) {
        return inventoryApi.post()
                .uri("/setup/restoreOpening")
                .body(Mono.just(key), OPHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(SaleHis sh) {
        SaleHisKey key = sh.getKey();
        return inventoryApi.post()
                .uri("/sale/deleteSale")
                .body(Mono.just(key), SaleHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> updateSaleSPay(String vouNo, boolean sPay) {
        return inventoryApi.put()
                .uri(builder -> builder.path("/sale/updateSPay")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("sPay", sPay)
                .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(OrderNote sh) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/orderNote/updateOrderNote")
                .queryParam("vouNo", sh.getVouNo())
                .queryParam("compCode", sh.getCompCode())
                .queryParam("deleted", sh.getDeleted())
                .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(OrderHisKey key) {
        return inventoryApi.post()
                .uri("/order/deleteOrder")
                .body(Mono.just(key), OrderHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(SaleHis sh) {
        SaleHisKey key = sh.getKey();
        return inventoryApi.post()
                .uri("/sale/restoreSale")
                .body(Mono.just(key), SaleHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(OrderNote sh) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/orderNote/updateOrderNote")
                .queryParam("vouNo", sh.getVouNo())
                .queryParam("compCode", sh.getCompCode())
                .queryParam("deleted", sh.getDeleted())
                .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restoreMilling(MillingHis his) {
        MillingHisKey key = his.getKey();
        return inventoryApi.post()
                .uri("/milling/restoreMilling")
                .body(Mono.just(key), MillingHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(OrderHisKey key) {
        return inventoryApi.post()
                .uri("/sale/restoreSale")
                .body(Mono.just(key), OrderHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(RetInHisKey key) {
        return inventoryApi.post()
                .uri("/returnIn/restoreReturnIn")
                .body(Mono.just(key), RetInHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(RetOutHisKey key) {
        return inventoryApi.post()
                .uri("/retout/restoreReturnOut")
                .body(Mono.just(key), RetOutHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(PurHisKey key) {
        return inventoryApi.post()
                .uri("/pur/deletePur")
                .body(Mono.just(key), PurHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(PurHisKey key) {
        return inventoryApi.post()
                .uri("/pur/restorePur")
                .body(Mono.just(key), PurHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(RetInHisKey key) {
        return inventoryApi.post()
                .uri("/returnIn/deleteReturnIn")
                .body(Mono.just(key), RetInHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(RetOutHisKey key) {
        return inventoryApi.post()
                .uri("/retout/deleteReturnOut")
                .body(Mono.just(key), RetOutHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(StockIOKey key) {
        return inventoryApi.post()
                .uri("/stockio/deleteStockIO")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(StockIOKey key) {
        return inventoryApi.post()
                .uri("/stockio/restoreStockIO")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(TransferHisKey key) {
        return inventoryApi.post()
                .uri("/transfer/deleteTransfer")
                .body(Mono.just(key), TransferHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(TransferHisKey key) {
        return inventoryApi.post()
                .uri("/transfer/restoreTransfer")
                .body(Mono.just(key), TransferHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(ProcessHisKey key) {
        return inventoryApi.post()
                .uri("/process/deleteProcess")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(GRNKey key) {
        return inventoryApi.post()
                .uri("/grn/deleteGRN")
                .body(Mono.just(key), GRNKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(GRNKey key) {
        return inventoryApi.post()
                .uri("/grn/restoreGRN")
                .body(Mono.just(key), GRNKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> open(GRNKey key) {
        return inventoryApi.post()
                .uri("/grn/openGRN")
                .body(Mono.just(key), GRNKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(ProcessHisDetailKey key) {
        return inventoryApi.post()
                .uri("/process/deleteProcessDetail")
                .body(Mono.just(key), ProcessHisDetailKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(ProcessHisKey key) {
        return inventoryApi.post()
                .uri("/process/restoreProcess")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(WeightLossHisKey key) {
        return inventoryApi.post()
                .uri("/weight/deleteWeightLoss")
                .body(Mono.just(key), WeightLossHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(WeightLossHisKey key) {
        return inventoryApi.post()
                .uri("/weight/restoreWeightLoss")
                .body(Mono.just(key), WeightLossHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<ProcessHis> saveProcess(ProcessHis his) {
        return inventoryApi.post()
                .uri("/process/saveProcess")
                .body(Mono.just(his), ProcessHis.class)
                .retrieve()
                .bodyToMono(ProcessHis.class)
                .onErrorResume(e -> {
                    if (localDatabase) {
                        int status = JOptionPane.showConfirmDialog(Global.parentForm,
                                "Can't save voucher to cloud. Do you want save local?",
                                "Offline", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (status == JOptionPane.YES_OPTION) {
                            return h2Repo.save(his);
                        }
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<ProcessHis> uploadProcess(ProcessHis his) {
        return inventoryApi.post()
                .uri("/process/saveProcess")
                .body(Mono.just(his), ProcessHis.class)
                .retrieve()
                .bodyToMono(ProcessHis.class)
                .onErrorResume(e -> {
                    return Mono.error(e);
                });
    }

    public Mono<ProcessHisDetail> saveProcessDetail(ProcessHisDetail his) {
        return inventoryApi.post()
                .uri("/process/saveProcessDetail")
                .body(Mono.just(his), ProcessHisDetail.class)
                .retrieve()
                .bodyToMono(ProcessHisDetail.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<ProcessHisDetail>> getProcessDetail(String vouNo, Integer deptId, boolean local) {
        if (local) {
            return h2Repo.getProcessDetail(vouNo, deptId);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/process/getProcessDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve()
                .bodyToFlux(ProcessHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<ProcessHis>> getProcess(ReportFilter f) {
        if (f.isLocal()) {
            return h2Repo.getProcessHistory(f);
        }
        return inventoryApi
                .post()
                .uri("/process/getProcess")
                .body(Mono.just(f), ReportFilter.class)
                .retrieve()
                .bodyToFlux(ProcessHis.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Flux<VPurchase> getPurchaseVoucher(ReportFilter filter) {
        return inventoryApi.post()
                .uri("/pur/getPur")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VPurchase.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Flux.empty();
                });
    }

    public Mono<List<MillingHis>> getMillingVoucher(ReportFilter filter) {
        if (filter.isLocal()) {
//            return h2Repo.searchPurchaseVoucher(filter);
        }
        return inventoryApi.post()
                .uri("/milling/getMilling")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(MillingHis.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<PurHisDetail>> getPurDetail(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/pur/getPurDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(PurHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<General> getSaleVoucherInfo(String vouDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/sale/getSaleVoucherInfo")
                .queryParam("vouDate", vouDate)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToMono(General.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<GRN> saveGRN(GRN grn) {
        return inventoryApi.post()
                .uri("/grn")
                .body(Mono.just(grn), GRN.class)
                .retrieve()
                .bodyToMono(GRN.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<WeightHis> saveWeight(WeightHis his) {
        return inventoryApi.post()
                .uri("weight/saveWeight")
                .body(Mono.just(his), WeightHis.class)
                .retrieve()
                .bodyToMono(WeightHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(WeightHisKey key) {
        return inventoryApi.post()
                .uri("/weight/deleteWeight")
                .body(Mono.just(key), WeightHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(WeightHisKey key) {
        return inventoryApi.post()
                .uri("/weight/restoreWeight")
                .body(Mono.just(key), WeightHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<GRN>> getGRNHistory(ReportFilter filter) {
        return inventoryApi
                .post()
                .uri("/grn/history")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(GRN.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Flux<WeightHis> getWeightHistory(ReportFilter filter) {
        return inventoryApi
                .post()
                .uri("/weight/getWeightHistory")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(WeightHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Flux.empty();
                });
    }

    public Mono<List<WeightHisDetail>> getWeightDetail(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/weight/getWeightDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(WeightHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<WeightColumn>> getWeightColumn(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/weight/getWeightColumn")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(WeightColumn.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<LandingHis>> getLandingHistory(ReportFilter filter) {
        return inventoryApi
                .post()
                .uri("/landing/history")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(LandingHis.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Flux<PaymentHis> getPaymentHistory(ReportFilter filter) {
        return inventoryApi
                .post()
                .uri("/payment/getPaymentHistory")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(PaymentHis.class)
                .onErrorResume((e) -> {
                    log.error("getPaymentHistory : " + e.getMessage());
                    return Flux.empty();
                });
    }

    public Flux<StockPayment> getStockPaymentHistory(ReportFilter filter) {
        return inventoryApi
                .post()
                .uri("/stockPayment/getPaymentHistory")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(StockPayment.class);
    }

    public Flux<LabourPaymentDto> getLabourPaymentHistory(ReportFilter filter) {
        return inventoryApi
                .post()
                .uri("/labourPayment/history")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(LabourPaymentDto.class);
    }

    public Mono<List<LabourPaymentDetail>> getLabourPaymentDetail(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/labourPayment/getDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(LabourPaymentDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockPaymentDetail>> getStockPaymentDetail(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/stockPayment/getPaymentDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(StockPaymentDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<SaleHisDetail>> getSaleByBatch(String batchNo, boolean detail) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/sale/getSaleByBatch")
                .queryParam("batchNo", batchNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .queryParam("detail", detail)
                .build())
                .retrieve().bodyToFlux(SaleHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<PurHis> save(PurHis ph) {
        return inventoryApi.post()
                .uri("/pur/savePurchase")
                .body(Mono.just(ph), PurHis.class)
                .retrieve()
                .bodyToMono(PurHis.class)
                .onErrorResume((e) -> {
                    if (localDatabase) {
                        int status = JOptionPane.showConfirmDialog(Global.parentForm,
                                "Can't save voucher to cloud. Do you want save local?",
                                "Offline", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (status == JOptionPane.YES_OPTION) {
                            return h2Repo.save(ph);
                        }
                        return Mono.error(new RuntimeException(e.getMessage()));
                    }
                    return Mono.error(new RuntimeException(e.getMessage()));
                });
    }

    public Mono<MillingHis> save(MillingHis ph) {
        return inventoryApi.post()
                .uri("/milling/saveMilling")
                .body(Mono.just(ph), MillingHis.class)
                .retrieve()
                .bodyToMono(MillingHis.class)
                .onErrorResume((e) -> {
                    if (localDatabase) {
                        int status = JOptionPane.showConfirmDialog(Global.parentForm,
                                "Can't save voucher to cloud. Do you want save local?",
                                "Offline", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (status == JOptionPane.YES_OPTION) {
//                            return h2Repo.save(ph);
                        }
                        return Mono.error(new RuntimeException(e.getMessage()));
                    }
                    return Mono.error(new RuntimeException(e.getMessage()));
                });
    }

    public Mono<LandingHis> save(LandingHis ph) {
        return inventoryApi.post()
                .uri("/landing/saveLanding")
                .body(Mono.just(ph), LandingHis.class)
                .retrieve()
                .bodyToMono(LandingHis.class)
                .onErrorResume((e) -> {
                    return Mono.error(new RuntimeException(e.getMessage()));
                });
    }

    public Mono<PurHis> uploadPurchase(PurHis ph) {
        return inventoryApi.post()
                .uri("/pur/savePurchase")
                .body(Mono.just(ph), PurHis.class)
                .retrieve()
                .bodyToMono(PurHis.class)
                .onErrorResume((e) -> {
                    return Mono.error(e);
                });
    }

    public Mono<RetInHis> save(RetInHis rh) {
        return inventoryApi.post()
                .uri("/returnIn/saveReturnIn")
                .body(Mono.just(rh), RetInHis.class)
                .retrieve()
                .bodyToMono(RetInHis.class);

    }

    public Mono<RetInHis> uploadRetIn(RetInHis rh) {
        return inventoryApi.post()
                .uri("/returnIn/saveReturnIn")
                .body(Mono.just(rh), RetInHis.class)
                .retrieve()
                .bodyToMono(RetInHis.class)
                .onErrorResume((e) -> {
                    return Mono.error(e);
                });
    }

    public Mono<RetOutHis> save(RetOutHis ro) {
        return inventoryApi.post()
                .uri("/retout/saveReturnOut")
                .body(Mono.just(ro), RetInHis.class)
                .retrieve()
                .bodyToMono(RetOutHis.class)
                .onErrorResume((e) -> {
                    if (localDatabase) {
                        int status = JOptionPane.showConfirmDialog(Global.parentForm,
                                "Can't save voucher to cloud. Do you want save local?",
                                "Offline", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (status == JOptionPane.YES_OPTION) {
                            return h2Repo.save(ro);
                        }
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<RetOutHis> uploadRetOut(RetOutHis ro) {
        return inventoryApi.post()
                .uri("/retout/saveReturnOut")
                .body(Mono.just(ro), RetInHis.class)
                .retrieve()
                .bodyToMono(RetOutHis.class)
                .onErrorResume((e) -> {
                    return Mono.error(e);
                });
    }

    public Mono<SaleHis> save(SaleHis sh) {
        return inventoryApi.post()
                .uri("/sale/saveSale")
                .body(Mono.just(sh), SaleHis.class)
                .retrieve()
                .bodyToMono(SaleHis.class);

    }

    public Mono<OrderNote> save(OrderNote sh) {
        return inventoryApi.post()
                .uri("/orderNote/saveOrderNote")
                .body(Mono.just(sh), OrderNote.class)
                .retrieve()
                .bodyToMono(OrderNote.class)
                .onErrorResume(e -> {
                    if (localDatabase) {
                        int status = JOptionPane.showConfirmDialog(Global.parentForm,
                                "Can't save voucher to cloud. Do you want save local?",
                                "Offline", JOptionPane.YES_NO_OPTION,
                                JOptionPane.ERROR_MESSAGE);
                        if (status == JOptionPane.YES_OPTION) {
//                            return h2Repo.save(sh);
                        }
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<SaleHis> uploadSale(SaleHis sh) {
        return inventoryApi.post()
                .uri("/sale/saveSale")
                .body(Mono.just(sh), SaleHis.class)
                .retrieve()
                .bodyToMono(SaleHis.class)
                .onErrorResume(e -> {
                    return Mono.error(e);
                });
    }

    public Mono<TransferHis> save(TransferHis th) {

        return inventoryApi.post()
                .uri("/transfer/saveTransfer")
                .body(Mono.just(th), TransferHis.class)
                .retrieve()
                .bodyToMono(TransferHis.class);
    }

    public Mono<List<TransferHisDetail>> getTransferDetail(String vouNo, Integer deptId, boolean local) {
        if (local) {
            return h2Repo.getTransferDetail(vouNo, deptId);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/transfer/getTransferDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve()
                .bodyToFlux(TransferHisDetail.class)
                .collectList();
    }

    public Flux<VTransfer> getTrasnfer(ReportFilter filter) {
        return inventoryApi
                .post()
                .uri("/transfer/getTransfer")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VTransfer.class)
                .onErrorResume(e -> {
                    log.error("error :" + e.getMessage());
                    return Flux.empty();
                });
    }

    public Mono<TransferHis> uploadTransfer(TransferHis th) {
        return inventoryApi.post()
                .uri("/transfer/saveTransfer")
                .body(Mono.just(th), TransferHis.class)
                .retrieve()
                .bodyToMono(TransferHis.class)
                .onErrorResume(e -> {
                    return Mono.error(e);
                });
    }

    public Mono<StockInOut> save(StockInOut sio) {
        return inventoryApi.post()
                .uri("/stockio/saveStockIO")
                .body(Mono.just(sio), StockInOut.class)
                .retrieve()
                .bodyToMono(StockInOut.class);
    }

    public Mono<StockInOut> uploadStockInOut(StockInOut sio) {
        return inventoryApi.post()
                .uri("/stockio/saveStockIO")
                .body(Mono.just(sio), StockInOut.class)
                .retrieve()
                .bodyToMono(StockInOut.class)
                .onErrorResume(e -> {
                    return Mono.error(e);
                });
    }

    public Mono<OPHis> save(OPHis op) {
        return inventoryApi.post()
                .uri("/setup/saveOpening")
                .body(Mono.just(op), OPHis.class)
                .retrieve()
                .bodyToMono(OPHis.class);
    }

    public Mono<OrderHis> save(OrderHis sh) {
        return inventoryApi.post()
                .uri("/order/saveOrder")
                .body(Mono.just(sh), OrderHis.class)
                .retrieve()
                .bodyToMono(OrderHis.class)
                .onErrorResume(e -> {
                    if (localDatabase) {
                        int status = JOptionPane.showConfirmDialog(Global.parentForm,
                                "Can't save voucher to cloud. Do you want save local?",
                                "Offline", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (status == JOptionPane.YES_OPTION) {
                            return h2Repo.save(sh);
                        }
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<OrderHis> uploadOrder(OrderHis sh) {
        return inventoryApi.post()
                .uri("/order/saveOrder")
                .body(Mono.just(sh), OrderHis.class)
                .retrieve()
                .bodyToMono(OrderHis.class)
                .onErrorResume(e -> {
                    return Mono.error(e);
                });
    }

    public Mono<AccSetting> save(AccSetting sh) {
        return inventoryApi.post()
                .uri("/setup/saveAccSetting")
                .body(Mono.just(sh), AccSetting.class)
                .retrieve()
                .bodyToMono(AccSetting.class)
                .doOnSuccess((acc) -> {
                    if (localDatabase) {
                        h2Repo.save(acc);
                    }
                })
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<PaymentHis> savePayment(PaymentHis ph) {
        return inventoryApi.post()
                .uri("/payment/savePayment")
                .body(Mono.just(ph), PaymentHis.class)
                .retrieve()
                .bodyToMono(PaymentHis.class);
    }

    public Mono<StockPayment> saveStockPayment(StockPayment ph) {
        return inventoryApi.post()
                .uri("/stockPayment/savePayment")
                .body(Mono.just(ph), StockPayment.class)
                .retrieve()
                .bodyToMono(StockPayment.class);
    }

    public Mono<LabourPaymentDto> saveLabourPayment(LabourPaymentDto dto) {
        return inventoryApi.post()
                .uri("/labourPayment")
                .body(Mono.just(dto), LabourPaymentDto.class)
                .retrieve()
                .bodyToMono(LabourPaymentDto.class)
                .onErrorResume((e) -> {
                    log.error("saveLabourPayment :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<VSale>> paymentReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/payment/paymentReport")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(VSale.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("paymentReport :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<AccSetting>> getAccSetting() {
        if (localDatabase) {
            return h2Repo.getAccSetting();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getAccSetting")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(AccSetting.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<ClosingBalance>> getStockBalance(String stockCode, boolean summary) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getStockBalance")
                .queryParam("stockCode", stockCode)
                .queryParam("calSale", Util1.getBoolean(ProUtil.getProperty("disable.calcuate.sale.stock")))
                .queryParam("calPur", Util1.getBoolean(ProUtil.getProperty("disable.calcuate.purchase.stock")))
                .queryParam("calRI", Util1.getBoolean(ProUtil.getProperty("disable.calcuate.returnin.stock")))
                .queryParam("calRO", Util1.getBoolean(ProUtil.getProperty("disable.calcuate.returnout.stock")))
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .queryParam("macId", Global.macId)
                .queryParam("summary", summary)
                .build())
                .retrieve().bodyToFlux(ClosingBalance.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.error(e);
                });
    }

    public Flux<ClosingBalance> getStockBalanceQty(ReportFilter filter) {
        return inventoryApi.post()
                .uri("/report/getStockBalanceQty")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(ClosingBalance.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Flux.empty();
                });
    }

    public Mono<List<ClosingBalance>> getStockBalanceByWeight(String stockCode, boolean summary) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getStockBalanceByWeight")
                .queryParam("stockCode", stockCode)
                .queryParam("calSale", Util1.getBoolean(ProUtil.getProperty("disable.calcuate.sale.stock")))
                .queryParam("calPur", Util1.getBoolean(ProUtil.getProperty("disable.calcuate.purchase.stock")))
                .queryParam("calRI", Util1.getBoolean(ProUtil.getProperty("disable.calcuate.returnin.stock")))
                .queryParam("calRO", Util1.getBoolean(ProUtil.getProperty("disable.calcuate.returnout.stock")))
                .queryParam("calMill", ProUtil.isDisableMill())
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .queryParam("macId", Global.macId)
                .queryParam("summary", summary)
                .build())
                .retrieve().bodyToFlux(ClosingBalance.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.error(e);
                });
    }

    public Mono<List<SaleHisDetail>> getSaleDetail(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/sale/getSaleDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(SaleHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Flux<OrderFileJoin> getOrderNoteDetail(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/orderNote/getOrderNoteDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(OrderFileJoin.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<MillingRawDetail>> getRawDetail(String vouNo, int deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/milling/getRawDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve()
                .bodyToFlux(MillingRawDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<MillingOutDetail>> getOutputDetail(String vouNo, int deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/milling/getOutputDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve()
                .bodyToFlux(MillingOutDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<MillingUsage>> getUsageDetail(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/milling/getUsageDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(MillingUsage.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<PaymentHisDetail>> getPaymentDetail(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/payment/getPaymentDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(PaymentHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("getPaymentDetail :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<VSale>> getSaleReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getSaleReport")
                .queryParam("vouNo", vouNo)
                .queryParam("macId", Global.macId)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(VSale.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<byte[]> getReturnInReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getReturnInReport")
                .queryParam("vouNo", vouNo)
                .queryParam("macId", Global.macId)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(ByteArrayResource::getByteArray);
    }

    public Mono<byte[]> getReturnOutReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getReturnOutReport")
                .queryParam("vouNo", vouNo)
                .queryParam("macId", Global.macId)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(ByteArrayResource::getByteArray);
    }

    public Mono<List<VSale>> getSaleByBatchReport(String vouNo, String grnVouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getSaleByBatchReport")
                .queryParam("vouNo", vouNo)
                .queryParam("grnVouNo", grnVouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(VSale.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<byte[]> getOrderReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getOrderReport")
                .queryParam("vouNo", vouNo)
                .queryParam("macId", Global.macId)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(ByteArrayResource::getByteArray)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<VOrder>> getOrder(ReportFilter filter) {
        if (filter.isLocal()) {
            return h2Repo.getOrderHistory(filter);
        }
        return inventoryApi.post()
                .uri("/order/getOrder")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VOrder.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<OrderHisDetail>> getOrderDetail(String vouNo, int deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/order/getOrderDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve().bodyToFlux(OrderHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });

    }

    public Flux<VSale> getSaleHistory(ReportFilter filter) {
        return inventoryApi.post()
                .uri("/sale/getSale")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VSale.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Flux.empty();
                });
    }

    public Flux<OrderNote> getOrderNoteHistory(ReportFilter filter) {
        return inventoryApi.post()
                .uri("/orderNote/getOrderNote")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(OrderNote.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Flux.empty();
                });
    }

    public Mono<List<OPHisDetail>> getOpeningDetail(String vouNo, String compCode, Integer deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getOpeningDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve()
                .bodyToFlux(OPHisDetail.class)
                .collectList();
    }

    public Mono<List<OPHis>> getOpeningHistory(ReportFilter filter) {
        return inventoryApi.post()
                .uri("/setup/getOpening")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(OPHis.class)
                .collectList();
    }

    public Mono<List<PaymentHisDetail>> getTraderBalance(String traderCode, String tranOption) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/payment/getTraderBalance")
                .queryParam("traderCode", traderCode)
                .queryParam("tranOption", tranOption)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(PaymentHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("getCustomerBalance :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<PaymentHis> getTraderBalanceSummary(String traderCode, String tranOption) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/payment/getTraderBalanceSummary")
                .queryParam("traderCode", traderCode)
                .queryParam("tranOption", tranOption)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(PaymentHis.class)
                .onErrorResume((e) -> {
                    log.error("getTraderBalanceSummary :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockPaymentDetail>> getTraderStockBalanceQty(String traderCode, String tranOption) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/stockPayment/getTraderStockBalanceQty")
                .queryParam("traderCode", traderCode)
                .queryParam("tranOption", tranOption)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(StockPaymentDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("getTraderStockBalance :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockPaymentDetail>> getTraderStockBalanceBag(String traderCode, String tranOption) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/stockPayment/getTraderStockBalanceBag")
                .queryParam("traderCode", traderCode)
                .queryParam("tranOption", tranOption)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(StockPaymentDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("getTraderStockBalance :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<WeightLossHis>> getWeightLoss(ReportFilter filter) {
        return inventoryApi
                .post()
                .uri("/weight/getWeightLoss")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(WeightLossHis.class)
                .collectList();
    }

    public Mono<Boolean> delete(String vouNo) {
        return inventoryApi.delete()
                .uri(uriBuilder -> uriBuilder
                .path("/payment/deletePayment")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build(vouNo))
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.just(false);
                });
    }

    public Mono<Boolean> cleanData() {
        return inventoryApi.delete()
                .uri(uriBuilder -> uriBuilder
                .path("/setup/cleanData")
                .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.just(false);
                });
    }

    public Mono<Boolean> restore(String vouNo) {
        return inventoryApi.delete()
                .uri(uriBuilder -> uriBuilder
                .path("/payment/restorePayment")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.just(false);
                });
    }

    public Mono<List<GRNDetail>> getGRNDetail(String vouNo, Integer deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/grn/getGRNDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve().bodyToFlux(GRNDetail.class)
                .collectList();
    }

    public Mono<List<PurExpense>> getPurExpense(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/expense/getPurExpense")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(PurExpense.class)
                .collectList();
    }

    public Mono<List<SaleExpense>> getSaleExpense(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/expense/getSaleExpense")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(SaleExpense.class)
                .collectList();
    }

    public Mono<List<VouDiscount>> getVoucherDiscount(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/sale/getVoucherDiscount")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(VouDiscount.class)
                .collectList();
    }

    public Mono<List<SaleNote>> getSaleNote(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/sale/getSaleNote")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(SaleNote.class)
                .collectList();
    }

    public Mono<List<VouDiscount>> searchDiscountDescription(String str) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/sale/searchDiscountDescription")
                .queryParam("str", str)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(VouDiscount.class)
                .collectList();
    }

    public Mono<List<SaleOrderJoin>> getSaleOrder(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/sale/getSaleOrder")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(SaleOrderJoin.class)
                .collectList();
    }

    public Mono<List<MillingExpense>> getMillingExpense(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/milling/getMillingExpense")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(MillingExpense.class)
                .collectList();
    }

    public Mono<List<VPurchase>> getPurchaseReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getPurchaseReport")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(VPurchase.class)
                .collectList();
    }

    public Mono<byte[]> getGRNReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getGRNReport")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(ByteArrayResource::getByteArray)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<VPurchase>> getPurchaseWeightReport(String vouNo, String batchNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getPurWeightReport")
                .queryParam("vouNo", vouNo)
                .queryParam("batchNo", batchNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(VPurchase.class)
                .collectList();
    }

    public Mono<List<GRNDetail>> getGRNDetailBatch(String batchNo, Integer deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/grn/getGRNDetailBatch")
                .queryParam("batchNo", batchNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve()
                .bodyToFlux(GRNDetail.class)
                .collectList();
    }

    public Mono<List<VDescription>> getDescription(String str, String tranType) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/pur/getDescription")
                .queryParam("compCode", Global.compCode)
                .queryParam("str", str)
                .queryParam("tranType", tranType)
                .build())
                .retrieve()
                .bodyToFlux(VDescription.class)
                .collectList();
    }

    public Mono<String> sendDownloadMessage(String entity, String message) {
        Message mg = new Message();
        mg.setHeader(MessageType.DOWNLOAD);
        mg.setEntity(entity);
        mg.setMessage(message);
        return inventoryApi.post()
                .uri("/message/send")
                .body(Mono.just(mg), Message.class)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<List<VTransfer>> getTransferReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/transfer/getTransferReport")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(VTransfer.class)
                .collectList();
    }

    public Mono<List<VStockIO>> getStockInOutVoucher(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getStockInOutVoucher")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(VStockIO.class)
                .collectList();
    }

    public Flux<VStockIO> getStockIO(ReportFilter filter) {
        return inventoryApi
                .post()
                .uri("/stockio/getStockIO")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VStockIO.class)
                .onErrorResume((t) -> Flux.empty());
    }

    public Flux<Message> receiveMessage() {
        return inventoryApi.get().uri(builder -> builder.path("/message/receive")
                .queryParam("messageId", Global.macId)
                .build())
                .retrieve()
                .bodyToFlux(Message.class)
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofDays(1))
                        .doAfterRetry(retrySignal -> log.error("Inventory Retrying..."))
                )
                .onErrorResume(throwable -> {
                    // Log the error or handle it as needed
                    log.error("Error receiving messages: " + throwable.getMessage());
                    // Return an empty Flux or some default value
                    return Flux.empty();
                });
    }

    public Mono<ReturnObject> getReport(ReportFilter filter) {
        return inventoryApi.post()
                .uri("/report/getReport")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
    }

    public Mono<List<ReorderLevel>> getReorderLevel(ReportFilter filter) {
        return inventoryApi.post()
                .uri("/report/getReorderLevel")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(ReorderLevel.class)
                .collectList();
    }

    public Mono<VLanding> getLandingReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/getLandingReport")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToMono(VLanding.class);
    }

    public Mono<Boolean> delete(StockFormulaKey key) {
        return inventoryApi.post()
                .uri("/setup/deleteFormula")
                .body(Mono.just(key), StockFormulaKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<ConsignHis> saveConsign(ConsignHis his) {
        return inventoryApi.post()
                .uri("consign")
                .body(Mono.just(his), ConsignHis.class)
                .retrieve()
                .bodyToMono(ConsignHis.class);
    }

    public Mono<Boolean> delete(ConsignHisKey key) {
        return inventoryApi.post()
                .uri("/consign/deleteConsign")
                .body(Mono.just(key), ConsignHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(ConsignHisKey key) {
        return inventoryApi.post()
                .uri("/consign/restoreConsign")
                .body(Mono.just(key), ConsignHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Flux<VConsign> getStockIssRecHistory(ReportFilter filter) {
        return inventoryApi
                .post()
                .uri("/consign/getConsignHistory")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VConsign.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Flux.empty();
                });
    }

    public Mono<List<ConsignHisDetail>> getStockIssRecDetail(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/consign/getConsignDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(ConsignHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<ConsignHis> findStockIR(String vouNo, boolean local) {
        ConsignHisKey key = new ConsignHisKey();
        key.setCompCode(Global.compCode);
        key.setVouNo(vouNo);
        return inventoryApi.post()
                .uri("/consign/findConsign")
                .body(Mono.just(key), ConsignHisKey.class)
                .retrieve()
                .bodyToMono(ConsignHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    //purchase order
    public Mono<PurOrderHis> savePurOrder(PurOrderHis his) {
        return inventoryApi.post()
                .uri("purOrder/savePurOrder")
                .body(Mono.just(his), PurOrderHis.class)
                .retrieve()
                .bodyToMono(PurOrderHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(PurOrderHisKey key) {
        return inventoryApi.post()
                .uri("/purOrder/deletePurOrder")
                .body(Mono.just(key), PurOrderHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(PurOrderHisKey key) {
        return inventoryApi.post()
                .uri("/purOrder/restorePurOrder")
                .body(Mono.just(key), PurOrderHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<VPurOrder>> getPurOrderHistory(ReportFilter filter) {
        return inventoryApi
                .post()
                .uri("/purOrder/getPurOrderHistory")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(VPurOrder.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<PurOrderHisDetail>> getPurOrderHisDetail(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/purOrder/getPurOrderHisDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(PurOrderHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<PurOrderHis> findPurOrder(String vouNo, boolean local) {
        PurOrderHisKey key = new PurOrderHisKey();
        key.setCompCode(Global.compCode);
        key.setVouNo(vouNo);
        return inventoryApi.post()
                .uri("/purOrder/findPurOrder")
                .body(Mono.just(key), PurOrderHisKey.class)
                .retrieve()
                .bodyToMono(PurOrderHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<LabourPaymentDetail>> calulateLabourPayment(ReportFilter filter) {
        return inventoryApi.post()
                .uri("/labourPayment/calculatePayment")
                .body(Mono.just(filter), ReportFilter.class)
                .retrieve()
                .bodyToFlux(LabourPaymentDetail.class)
                .collectList();
    }

    public Mono<Boolean> deleteLabourPayment(String vouNo) {
        return inventoryApi.delete()
                .uri("labourPayment/delete/{fileId}/{compCode}", vouNo, Global.compCode)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> restoreLabourPayment(String vouNo) {
        return inventoryApi.put()
                .uri("labourPayment/restore/{fileId}/{compCode}", vouNo, Global.compCode)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> deleteStockPayment(String vouNo) {
        return inventoryApi.delete()
                .uri("stockPayment/delete/{vouNo}/{compCode}", vouNo, Global.compCode)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> restoreStockPayment(String vouNo) {
        return inventoryApi.put()
                .uri("stockPayment/restore/{vouNo}/{compCode}", vouNo, Global.compCode)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.H2Repo;
import com.common.FilterObject;
import com.inventory.model.CFont;
import com.user.model.Currency;
import com.common.Global;
import com.common.ProUtil;
import com.common.Util1;
import com.inventory.model.AccSetting;
import com.inventory.model.Category;
import com.inventory.model.CategoryKey;
import com.inventory.model.Expense;
import com.inventory.model.GRN;
import com.inventory.model.GRNKey;
import com.inventory.model.General;
import com.inventory.model.Location;
import com.inventory.model.LocationKey;
import com.inventory.model.OPHis;
import com.inventory.model.OPHisKey;
import com.inventory.model.OrderHis;
import com.inventory.model.OrderHisDetail;
import com.inventory.model.OrderHisKey;
import com.inventory.model.Pattern;
import com.inventory.model.PaymentHis;
import com.inventory.model.PaymentHisDetail;
import com.inventory.model.PaymentHisKey;
import com.inventory.model.PriceOption;
import com.inventory.model.ProcessHis;
import com.inventory.model.ProcessHisDetail;
import com.inventory.model.ProcessHisDetailKey;
import com.inventory.model.ProcessHisKey;
import com.inventory.model.ProcessType;
import com.inventory.model.PurHis;
import com.inventory.model.PurHisKey;
import com.inventory.model.Region;
import com.inventory.model.RegionKey;
import com.inventory.model.RelationKey;
import com.inventory.model.ReorderLevel;
import com.inventory.model.RetInHis;
import com.inventory.model.RetInHisKey;
import com.inventory.model.RetOutHis;
import com.inventory.model.RetOutHisKey;
import com.inventory.model.SaleHis;
import com.inventory.model.SaleHisDetail;
import com.inventory.model.SaleHisKey;
import com.inventory.model.SaleMan;
import com.inventory.model.SaleManKey;
import com.inventory.model.Stock;
import com.inventory.model.StockBrand;
import com.inventory.model.StockBrandKey;
import com.inventory.model.StockIOKey;
import com.inventory.model.StockInOut;
import com.inventory.model.StockInOutDetail;
import com.inventory.model.StockKey;
import com.inventory.model.StockType;
import com.inventory.model.StockTypeKey;
import com.inventory.model.StockUnit;
import com.inventory.model.StockUnitKey;
import com.inventory.model.Trader;
import com.inventory.model.TraderGroup;
import com.inventory.model.TraderGroupKey;
import com.inventory.model.TraderKey;
import com.inventory.model.TransferHis;
import com.inventory.model.TransferHisDetail;
import com.inventory.model.TransferHisKey;
import com.inventory.model.UnitRelation;
import com.inventory.model.UnitRelationDetail;
import com.inventory.model.VSale;
import com.inventory.model.VStockBalance;
import com.inventory.model.VTransfer;
import com.inventory.model.VouStatus;
import com.inventory.model.VouStatusKey;
import com.inventory.model.WeightLossHis;
import com.inventory.model.WeightLossHisKey;
import java.util.List;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        String locCode = Global.hmRoleProperty.get("default.location");
        return findLocation(locCode, Global.deptId);
    }

    public Mono<Stock> getDefaultStock() {
        String stockCode = Global.hmRoleProperty.get("default.stock");
        return findStock(stockCode);
    }

    public Mono<SaleMan> getDefaultSaleMan() {
        String code = Global.hmRoleProperty.get("default.saleman");
        return findSaleMan(code, Global.deptId);
    }

    public Mono<List<PriceOption>> getPriceOption(String option) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-price-option")
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

    public Mono<List<Category>> getCategory() {
        if (localDatabase) {
            return h2Repo.getCategory();
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-category")
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
                .uri(builder -> builder.path("/setup/get-saleman")
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

    public Mono<SaleMan> findSaleMan(String code, Integer deptId) {
        SaleManKey key = new SaleManKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setSaleManCode(code);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/find-saleman")
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
                .uri(builder -> builder.path("/setup/get-brand")
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
                .uri(builder -> builder.path("/setup/get-type")
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
                .uri(builder -> builder.path("/setup/get-unit")
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
                .uri(builder -> builder.path("/setup/get-relation")
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

    public Mono<Trader> findTrader(String code, Integer deptId) {
        TraderKey key = new TraderKey();
        key.setCode(Util1.isNull(code, "-"));
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/find-trader")
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
        key.setDeptId(deptId);
        return inventoryApi.post()
                .uri("/setup/find-trader-group")
                .body(Mono.just(key), TraderGroupKey.class)
                .retrieve()
                .bodyToMono(TraderGroup.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Region> findRegion(String code) {
        RegionKey key = new RegionKey();
        key.setRegCode(Util1.isNull(code, "-"));
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        return inventoryApi.post()
                .uri("/setup/find-region")
                .body(Mono.just(key), RegionKey.class)
                .retrieve()
                .bodyToMono(Region.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<Trader>> getCustomer() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-customer")
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
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-supplier")
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
                .uri(builder -> builder.path("/setup/get-trader-list")
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
                .uri(builder -> builder.path("/grn/get-batch-list")
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

    public Mono<List<Expense>> getExpense() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/expense/get-expense")
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
                .uri(builder -> builder.path("/setup/find-trader-rfid")
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
                .uri(builder -> builder.path("/setup/get-region")
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

    public Mono<Location> findLocation(String locCode, Integer deptId) {
        LocationKey key = new LocationKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setLocCode(locCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/find-location")
                .body(Mono.just(key), LocationKey.class)
                .retrieve()
                .bodyToMono(Location.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockBrand> findBrand(String brandCode, Integer deptId) {
        StockBrandKey key = new StockBrandKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setBrandCode(brandCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/find-brand")
                .body(Mono.just(key), StockBrandKey.class)
                .retrieve()
                .bodyToMono(StockBrand.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<VouStatus> findVouStatus(String code, Integer deptId) {
        VouStatusKey key = new VouStatusKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setCode(code);

        if (localDatabase) {
            return h2Repo.find(key);
        }

        return inventoryApi.post()
                .uri("/setup/find-voucher-status")
                .body(Mono.just(key), VouStatusKey.class)
                .retrieve()
                .bodyToMono(VouStatus.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<StockInOutDetail>> searchStkIODetail(String vouNo, Integer deptId) {
        if (localDatabase) {
            return h2Repo.searchStkIODetail(vouNo, Global.compCode, deptId);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/stockio/get-stockio-detail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve()
                .bodyToFlux(StockInOutDetail.class)
                .collectList();
    }

    public Mono<StockUnit> findUnit(String unitCode, Integer deptId) {
        StockUnitKey key = new StockUnitKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setUnitCode(unitCode);
        return inventoryApi.post()
                .uri("/setup/find-unit")
                .body(Mono.just(key), StockUnitKey.class)
                .retrieve()
                .bodyToMono(StockUnit.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<UnitRelation> findRelation(String relCode, Integer deptId) {
        RelationKey key = new RelationKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setRelCode(relCode);
        return inventoryApi.post()
                .uri("/setup/find-unit-relation")
                .body(Mono.just(key), RelationKey.class)
                .retrieve()
                .bodyToMono(UnitRelation.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Category> findCategory(String catCode, Integer deptId) {
        CategoryKey key = new CategoryKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setCatCode(catCode);
        return inventoryApi.post()
                .uri("/setup/find-category")
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
        key.setDeptId(Global.deptId);
        key.setStockCode(stockCode);
        if (localDatabase) {
            return h2Repo.find(key);
        }
        return inventoryApi.post()
                .uri("/setup/find-stock")
                .body(Mono.just(key), StockKey.class)
                .retrieve()
                .bodyToMono(Stock.class)
                .onErrorResume((e) -> {
                    log.error("findStock : " + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockType> findGroup(String typeCode, Integer deptId) {
        StockTypeKey key = new StockTypeKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setStockTypeCode(typeCode);
        return inventoryApi.post()
                .uri("/setup/find-type")
                .body(Mono.just(key), StockTypeKey.class)
                .retrieve()
                .bodyToMono(StockType.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<WeightLossHis> findWeightLoss(String vouNo, Integer deptId) {
        WeightLossHisKey key = new WeightLossHisKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setVouNo(vouNo);
        return inventoryApi.post()
                .uri("/weight/find-weight-loss")
                .body(Mono.just(key), StockTypeKey.class)
                .retrieve()
                .bodyToMono(WeightLossHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<ProcessHis> findProcess(ProcessHisKey key) {
        return inventoryApi.post()
                .uri("/process/find-process")
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
                .uri(builder -> builder.path("/setup/get-location")
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

    public Mono<List<Stock>> getStock(boolean active) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-stock")
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

    public Mono<List<Stock>> getStock(String str) {
        if (localDatabase) {
            return h2Repo.getStock(str);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-stock-list")
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

    public Mono<List<Stock>> getService() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-service")
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

    public Mono<List<String>> deleteStock(StockKey key) {
        return inventoryApi.post()
                .uri("/setup/delete-stock")
                .body(Mono.just(key), StockKey.class)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
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
                .uri(builder -> builder.path("/setup/get-voucher-status")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
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

    public Mono<List<UnitRelation>> getUnitRelation() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-unit-relation")
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
                .uri("/setup/save-trader")
                .body(Mono.just(t), Trader.class)
                .retrieve()
                .bodyToMono(Trader.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Stock> saveStock(Stock s) {
        return inventoryApi.post()
                .uri("/setup/save-stock")
                .body(Mono.just(s), Stock.class)
                .retrieve()
                .bodyToMono(Stock.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Currency> saveCurrency(Currency c) {
        return inventoryApi.post()
                .uri("/setup/save-currency")
                .body(Mono.just(c), Currency.class)
                .retrieve()
                .bodyToMono(Currency.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Location> saveLocaiton(Location loc) {
        return inventoryApi.post()
                .uri("/setup/save-location")
                .body(Mono.just(loc), Location.class)
                .retrieve()
                .bodyToMono(Location.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Region> saveRegion(Region reg) {
        return inventoryApi.post()
                .uri("/setup/save-region")
                .body(Mono.just(reg), Region.class)
                .retrieve()
                .bodyToMono(Region.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<SaleMan> saveSaleMan(SaleMan s) {
        return inventoryApi.post()
                .uri("/setup/save-saleman")
                .body(Mono.just(s), SaleMan.class)
                .retrieve()
                .bodyToMono(SaleMan.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockBrand> saveBrand(StockBrand s) {
        return inventoryApi.post()
                .uri("/setup/save-brand")
                .body(Mono.just(s), StockBrand.class)
                .retrieve()
                .bodyToMono(StockBrand.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Expense> saveExpense(Expense s) {
        return inventoryApi.post()
                .uri("/expense/save-expense")
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
                .uri("/setup/save-type")
                .body(Mono.just(t), StockType.class)
                .retrieve()
                .bodyToMono(StockType.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<StockUnit> saveStockUnit(StockUnit unit) {
        return inventoryApi.post()
                .uri("/setup/save-unit")
                .body(Mono.just(unit), StockUnit.class)
                .retrieve()
                .bodyToMono(StockUnit.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<VouStatus> saveVouStatus(VouStatus vou) {
        return inventoryApi.post()
                .uri("/setup/save-voucher-status")
                .body(Mono.just(vou), VouStatus.class)
                .retrieve()
                .bodyToMono(VouStatus.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<ProcessType> saveProcessType(ProcessType vou) {
        return inventoryApi.post()
                .uri("/setup/save-process-type")
                .body(Mono.just(vou), ProcessType.class)
                .retrieve()
                .bodyToMono(ProcessType.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Category> saveCategory(Category category) {
        return inventoryApi.post()
                .uri("/setup/save-category")
                .body(Mono.just(category), Category.class)
                .retrieve()
                .bodyToMono(Category.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Pattern> savePattern(Pattern pattern) {
        return inventoryApi.post()
                .uri("/setup/save-pattern")
                .body(Mono.just(pattern), Pattern.class)
                .retrieve()
                .bodyToMono(Pattern.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<WeightLossHis> saveWeightLoss(WeightLossHis loss) {
        return inventoryApi.post()
                .uri("/weight/save-weight-loss")
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
                .uri("/weight/save-weight-loss")
                .body(Mono.just(loss), WeightLossHis.class)
                .retrieve()
                .bodyToMono(WeightLossHis.class)
                .onErrorResume(e -> {
                    return Mono.error(e);
                });
    }

    public Mono<Boolean> delete(Pattern p) {
        return inventoryApi.post()
                .uri("/setup/delete-pattern")
                .body(Mono.just(p), Pattern.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<UnitRelation> saveUnitRelation(UnitRelation rel) {
        return inventoryApi.post()
                .uri("/setup/save-unit-relation")
                .body(Mono.just(rel), UnitRelation.class)
                .retrieve()
                .bodyToMono(UnitRelation.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
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
        if (localDatabase) {
//            return h2Repo.getPurRecentPrice(stockCode, vouDate, unit, Global.compCode, Global.deptId);G
            General general = new General();
            general.setAmount(0.0F);
            return Mono.justOrEmpty(general);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/get-purchase-recent-price")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
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
                .uri(builder -> builder.path("/report/get-weight-loss-recent-price")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
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

    public Mono<General> getPurAvgPrice(String stockCode, String vouDate, String unit) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/get-purchase-avg-price")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
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
                .uri(builder -> builder.path("/report/get-production-recent-price")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
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
                .uri(builder -> builder.path("/report/get-sale-recent-price")
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
                .uri(builder -> builder.path("/report/get-stock-io-recent-price")
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

    public Mono<List<UnitRelationDetail>> getRelationDetail(String code, Integer deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-unit-relation-detail")
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

    public Mono<List<ProcessType>> getProcessType() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-process-type")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(ProcessType.class)
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

    public Mono<StockInOut> findStockIO(String vouNo, Integer deptId) {
        StockIOKey key = new StockIOKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setVouNo(vouNo);
        if (localDatabase) {
            return h2Repo.findStkIO(key);
        }
        return inventoryApi.post()
                .uri("/stockio/find-stockio")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(StockInOut.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<TransferHis> findTransfer(String vouNo, Integer deptId) {
        TransferHisKey key = new TransferHisKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setVouNo(vouNo);
        return inventoryApi.post()
                .uri("/transfer/find-transfer")
                .body(Mono.just(key), TransferHisKey.class)
                .retrieve()
                .bodyToMono(TransferHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<SaleHis> findSale(String vouNo, Integer deptId) {
        SaleHisKey key = new SaleHisKey();
        key.setVouNo(vouNo);
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        if (localDatabase) {
            return h2Repo.findSale(key);
        }
        return inventoryApi.post()
                .uri("/sale/find-sale")
                .body(Mono.just(key), SaleHisKey.class)
                .retrieve()
                .bodyToMono(SaleHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<OrderHis> findOrder(String vouNo, Integer deptId) {
        OrderHisKey key = new OrderHisKey();
        key.setVouNo(vouNo);
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        return inventoryApi.post()
                .uri("/order/find-order")
                .body(Mono.just(key), OrderHisKey.class)
                .retrieve()
                .bodyToMono(OrderHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<OPHis> findOpening(OPHisKey key) {
        if (localDatabase) {
            return h2Repo.findOpening(key);
        }
        return inventoryApi.post()
                .uri("/setup/find-opening")
                .body(Mono.just(key), OPHisKey.class)
                .retrieve()
                .bodyToMono(OPHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<PurHis> findPurchase(String vouNo, Integer deptId) {
        PurHisKey key = new PurHisKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setVouNo(vouNo);
        return inventoryApi.post()
                .uri("/pur/find-pur")
                .body(Mono.just(key), PurHisKey.class)
                .retrieve()
                .bodyToMono(PurHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<RetInHis> findReturnIn(String vouNo, Integer deptId) {
        RetInHisKey key = new RetInHisKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setVouNo(vouNo);
        return inventoryApi.post()
                .uri("/retin/find-retin")
                .body(Mono.just(key), RetInHisKey.class)
                .retrieve()
                .bodyToMono(RetInHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<RetOutHis> findReturnOut(String vouNo, Integer deptId) {
        RetOutHisKey key = new RetOutHisKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setVouNo(vouNo);
        return inventoryApi.post()
                .uri("/retout/find-retout")
                .body(Mono.just(key), RetOutHisKey.class)
                .retrieve()
                .bodyToMono(RetOutHis.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<General> getSmallQty(String stockCode, String unit) {
        if (localDatabase) {
            return h2Repo.getSmallQty(stockCode, unit, Global.compCode, Global.deptId);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/get-smallest_qty")
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
                .uri("/setup/save-reorder")
                .body(Mono.just(rl), ReorderLevel.class)
                .retrieve()
                .bodyToMono(ReorderLevel.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<String>> deleteTrader(TraderKey key) {
        return inventoryApi.post()
                .uri("/setup/delete-trader")
                .body(Mono.just(key), TraderKey.class)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<TraderGroup> saveTraderGroup(TraderGroup rl) {
        return inventoryApi.post()
                .uri("/setup/save-trader-group")
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
                .uri(builder -> builder.path("/setup/get-trader-group")
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

    public Mono<List<Pattern>> getPattern(String stockCode, Integer deptId, String vouDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-pattern")
                .queryParam("stockCode", stockCode)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .queryParam("vouDate", vouDate)
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
                .uri("/setup/delete-opening")
                .body(Mono.just(key), OPHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(SaleHisKey key) {
        return inventoryApi.post()
                .uri("/sale/delete-sale")
                .body(Mono.just(key), SaleHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> delete(OrderHisKey key) {
        return inventoryApi.post()
                .uri("/order/delete-order")
                .body(Mono.just(key), OrderHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(SaleHisKey key) {
        return inventoryApi.post()
                .uri("/sale/restore-sale")
                .body(Mono.just(key), SaleHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<Boolean> restore(OrderHisKey key) {
        return inventoryApi.post()
                .uri("/sale/restore-sale")
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
                .uri("/retin/restore-retin")
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
                .uri("/retout/restore-retout")
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
                .uri("/pur/delete-pur")
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
                .uri("/pur/restore-pur")
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
                .uri("/retin/delete-retin")
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
                .uri("/retout/delete-retout")
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
                .uri("/stockio/delete-stockio")
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
                .uri("/stockio/restore-stockio")
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
                .uri("/transfer/delete-transfer")
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
                .uri("/transfer/restore-transfer")
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
                .uri("/process/delete-process")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(GRNKey key) {
        return inventoryApi.post()
                .uri("/grn/delete-grn")
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
                .uri("/grn/open-grn")
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
                .uri("/process/delete-process-detail")
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
                .uri("/process/restore-process")
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
                .uri("/weight/delete-weight-loss")
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
                .uri("/weight/restore-weight-loss")
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
                .uri("/process/save-process")
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
                .uri("/process/save-process")
                .body(Mono.just(his), ProcessHis.class)
                .retrieve()
                .bodyToMono(ProcessHis.class)
                .onErrorResume(e -> {
                    return Mono.error(e);
                });
    }

    public Mono<ProcessHisDetail> saveProcessDetail(ProcessHisDetail his) {
        return inventoryApi.post()
                .uri("/process/save-process-detail")
                .body(Mono.just(his), ProcessHisDetail.class)
                .retrieve()
                .bodyToMono(ProcessHisDetail.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<ProcessHisDetail>> getProcessDetail(String vouNo, Integer deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/process/get-process-detail")
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

    public Mono<List<ProcessHis>> getProcess(FilterObject f) {
        return inventoryApi
                .post()
                .uri("/process/get-process")
                .body(Mono.just(f), FilterObject.class)
                .retrieve()
                .bodyToFlux(ProcessHis.class)
                .collectList();
    }

    public Mono<General> getSaleVoucherInfo(String vouDate) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/sale/get-sale-voucher-info")
                .queryParam("vouDate", vouDate)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
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

    public Mono<List<GRN>> getGRNHistory(FilterObject filter) {
        return inventoryApi
                .post()
                .uri("/grn/history")
                .body(Mono.just(filter), FilterObject.class)
                .retrieve()
                .bodyToFlux(GRN.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<PaymentHis>> getPaymentHistory(FilterObject filter) {
        return inventoryApi
                .post()
                .uri("/payment/getPaymentHistory")
                .body(Mono.just(filter), FilterObject.class)
                .retrieve()
                .bodyToFlux(PaymentHis.class)
                .collectList();
    }

    public Mono<List<SaleHisDetail>> getSaleByBatch(String batchNo, boolean detail) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/sale/get-sale-by-batch")
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
                .uri("/pur/save-pur")
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
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<PurHis> uploadPurchase(PurHis ph) {
        return inventoryApi.post()
                .uri("/pur/save-pur")
                .body(Mono.just(ph), PurHis.class)
                .retrieve()
                .bodyToMono(PurHis.class)
                .onErrorResume((e) -> {
                    return Mono.error(e);
                });
    }

    public Mono<RetInHis> save(RetInHis rh) {
        return inventoryApi.post()
                .uri("/retin/save-retin")
                .body(Mono.just(rh), RetInHis.class)
                .retrieve()
                .bodyToMono(RetInHis.class)
                .onErrorResume((e) -> {
                    if (localDatabase) {
                        int status = JOptionPane.showConfirmDialog(Global.parentForm,
                                "Can't save voucher to cloud. Do you want save local?",
                                "Offline", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (status == JOptionPane.YES_OPTION) {
                            return h2Repo.save(rh);
                        }
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<RetInHis> uploadRetIn(RetInHis rh) {
        return inventoryApi.post()
                .uri("/retin/save-retin")
                .body(Mono.just(rh), RetInHis.class)
                .retrieve()
                .bodyToMono(RetInHis.class)
                .onErrorResume((e) -> {
                    return Mono.error(e);
                });
    }

    public Mono<RetOutHis> save(RetOutHis ro) {
        return inventoryApi.post()
                .uri("/retout/save-retout")
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
                .uri("/retout/save-retout")
                .body(Mono.just(ro), RetInHis.class)
                .retrieve()
                .bodyToMono(RetOutHis.class)
                .onErrorResume((e) -> {
                    return Mono.error(e);
                });
    }

    public Mono<SaleHis> save(SaleHis sh) {
        return inventoryApi.post()
                .uri("/sale/save-sale")
                .body(Mono.just(sh), SaleHis.class)
                .retrieve()
                .bodyToMono(SaleHis.class)
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

    public Mono<SaleHis> uploadSale(SaleHis sh) {
        return inventoryApi.post()
                .uri("/sale/save-sale")
                .body(Mono.just(sh), SaleHis.class)
                .retrieve()
                .bodyToMono(SaleHis.class)
                .onErrorResume(e -> {
                    return Mono.error(e);
                });
    }

    public Mono<TransferHis> save(TransferHis th) {

        return inventoryApi.post()
                .uri("/transfer/save-transfer")
                .body(Mono.just(th), TransferHis.class)
                .retrieve()
                .bodyToMono(TransferHis.class)
                .onErrorResume(e -> {
                    if (localDatabase) {
                        int status = JOptionPane.showConfirmDialog(Global.parentForm,
                                "Can't save voucher to cloud. Do you want save local?",
                                "Offline", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (status == JOptionPane.YES_OPTION) {
                            return h2Repo.save(th);
                        }
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<List<TransferHisDetail>> getTrasnferDetail(String vouNo, Integer deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/transfer/get-transfer-detail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve()
                .bodyToFlux(TransferHisDetail.class)
                .collectList();
    }

    public Mono<List<VTransfer>> getTrasnfer(FilterObject filter) {
        return inventoryApi
                .post()
                .uri("/transfer/get-transfer")
                .body(Mono.just(filter), FilterObject.class)
                .retrieve()
                .bodyToFlux(VTransfer.class)
                .collectList();
    }

    public Mono<TransferHis> uploadTransfer(TransferHis th) {
        return inventoryApi.post()
                .uri("/transfer/save-transfer")
                .body(Mono.just(th), TransferHis.class)
                .retrieve()
                .bodyToMono(TransferHis.class)
                .onErrorResume(e -> {
                    return Mono.error(e);
                });
    }

    public Mono<StockInOut> save(StockInOut sio) {
        return inventoryApi.post()
                .uri("/stockio/save-stockio")
                .body(Mono.just(sio), StockInOut.class)
                .retrieve()
                .bodyToMono(StockInOut.class)
                .onErrorResume(e -> {
                    if (localDatabase) {
                        int status = JOptionPane.showConfirmDialog(Global.parentForm,
                                "Can't save voucher to cloud. Do you want save local?",
                                "Offline", JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (status == JOptionPane.YES_OPTION) {
                            return h2Repo.save(sio);
                        }
                        return Mono.error(e);
                    }
                    return Mono.error(e);
                });
    }

    public Mono<StockInOut> uploadStockInOut(StockInOut sio) {
        return inventoryApi.post()
                .uri("/stockio/save-stockio")
                .body(Mono.just(sio), StockInOut.class)
                .retrieve()
                .bodyToMono(StockInOut.class)
                .onErrorResume(e -> {
                    return Mono.error(e);
                });
    }

    public Mono<OrderHis> save(OrderHis sh) {
        return inventoryApi.post()
                .uri("/order/save-order")
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
                .uri("/order/save-order")
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
                .bodyToMono(PaymentHis.class)
                .onErrorResume((e) -> {
                    log.error("savePayment :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<AccSetting>> getAccSetting() {
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

    public Mono<List<VStockBalance>> getStockBalance(String stockCode, boolean summary) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/get-stock-balance")
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
                .retrieve().bodyToFlux(VStockBalance.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.error(e);
                });
    }

    public Mono<List<SaleHisDetail>> getSaleDetail(String vouNo, int deptId, String intgUpdateStatus) {
        if (intgUpdateStatus == null) {
            return h2Repo.getSaleDetail(vouNo, deptId);
        }
        return inventoryApi.get()
                .uri(builder -> builder.path("/sale/get-sale-detail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve().bodyToFlux(SaleHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<PaymentHisDetail>> getPaymentDetail(String vouNo, int deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/payment/getPaymentDetail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve()
                .bodyToFlux(PaymentHisDetail.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("getPaymentDetail :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<byte[]> getSaleReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/get-sale-report")
                .queryParam("vouNo", vouNo)
                .queryParam("macId", Global.macId)
                .build())
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(ByteArrayResource::getByteArray)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<byte[]> getOrderReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/get-order-report")
                .queryParam("vouNo", vouNo)
                .queryParam("macId", Global.macId)
                .build())
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(ByteArrayResource::getByteArray)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<VSale>> getOrder(FilterObject filter) {
        return inventoryApi.post()
                .uri("/order/get-order")
                .body(Mono.just(filter), FilterObject.class)
                .retrieve()
                .bodyToFlux(VSale.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<OrderHisDetail>> getOrderDetail(String vouNo, int deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/order/get-order-detail")
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

    public Mono<List<VSale>> getSaleHistory(FilterObject filter) {
        if (filter.isLocal()) {
            return h2Repo.getSaleHistory(filter);
        }
        return inventoryApi.post()
                .uri("/sale/get-sale")
                .body(Mono.just(filter), FilterObject.class)
                .retrieve()
                .bodyToFlux(VSale.class)
                .collectList()
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.empty();
                });
    }

    public Mono<List<PaymentHisDetail>> getCustomerBalance(String traderCode) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/payment/getCustomerBalance")
                .queryParam("traderCode", traderCode)
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

    public Mono<List<WeightLossHis>> getWeightLoss(FilterObject filter) {
        return inventoryApi
                .post()
                .uri("/weight/get-weight-loss")
                .body(Mono.just(filter), FilterObject.class)
                .retrieve()
                .bodyToFlux(WeightLossHis.class)
                .collectList();
    }

    public Mono<Boolean> delete(PaymentHisKey key) {
        return inventoryApi.post()
                .uri("/payment/deletePayment")
                .body(Mono.just(key), PaymentHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.just(false);
                });
    }

    public Mono<Boolean> restore(PaymentHisKey key) {
        return inventoryApi.post()
                .uri("/payment/restorePayment")
                .body(Mono.just(key), PaymentHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume((e) -> {
                    log.error("error :" + e.getMessage());
                    return Mono.just(false);
                });
    }
}

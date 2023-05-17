/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

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
import com.inventory.model.OrderDetail;
import com.inventory.model.OrderHis;
import com.inventory.model.OrderHisDetail;
import com.inventory.model.OrderHisKey;
import com.inventory.model.Pattern;
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
import com.inventory.model.TransferHisKey;
import com.inventory.model.UnitRelation;
import com.inventory.model.UnitRelationDetail;
import com.inventory.model.VSale;
import com.inventory.model.VStockBalance;
import com.inventory.model.VouStatus;
import com.inventory.model.VouStatusKey;
import com.inventory.model.WeightLossHis;
import com.inventory.model.WeightLossHisKey;
import java.util.List;
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

    private List<PriceOption> listPO = null;
    @Autowired
    private WebClient inventoryApi;

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
                .collectList();

    }

    public Mono<List<Category>> getCategory() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-category")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve().bodyToFlux(Category.class).collectList();
    }

    public Flux<SaleMan> getSaleMan() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-saleman")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve().bodyToFlux(SaleMan.class);
    }

    public Mono<SaleMan> findSaleMan(String code, Integer deptId) {
        SaleManKey key = new SaleManKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setSaleManCode(code);
        return inventoryApi.post()
                .uri("/setup/find-saleman")
                .body(Mono.just(key), SaleManKey.class)
                .retrieve()
                .bodyToMono(SaleMan.class);
    }

    public Mono<List<StockBrand>> getStockBrand() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-brand")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve().bodyToFlux(StockBrand.class).collectList();
    }

    public Mono<List<StockType>> getStockType() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-type")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve().bodyToFlux(StockType.class).collectList();
    }

    public Mono<List<StockUnit>> getStockUnit() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-unit")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve().bodyToFlux(StockUnit.class).collectList();
    }

    public Flux<StockUnit> getUnit(String relCode, Integer deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-relation")
                .queryParam("relCode", relCode)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve().bodyToFlux(StockUnit.class);
    }

    public Mono<Trader> findTrader(String code, Integer deptId) {
        TraderKey key = new TraderKey();
        key.setCode(Util1.isNull(code, "-"));
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        return inventoryApi.post()
                .uri("/setup/find-trader")
                .body(Mono.just(key), TraderKey.class)
                .retrieve()
                .bodyToMono(Trader.class);
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
                .bodyToMono(TraderGroup.class);
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
                .bodyToMono(Region.class);
    }

    public Flux<Trader> getCustomer() {
        Flux<Trader> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-customer")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve().bodyToFlux(Trader.class);
        return result;
    }

    public Flux<Trader> getSupplier() {
        Flux<Trader> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-supplier")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve().bodyToFlux(Trader.class);
        return result;
    }

    public Mono<List<Trader>> getTraderList(String text, String type) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-trader-list")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .queryParam("text", text)
                .queryParam("type", type)
                .build())
                .retrieve()
                .bodyToFlux(Trader.class)
                .collectList();
    }

    public Mono<List<GRN>> getBatchList(String batchNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/grn/get-batch-list")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .queryParam("batchNo", batchNo)
                .build())
                .retrieve().bodyToFlux(GRN.class)
                .collectList();
    }

    public Mono<List<Expense>> getExpense() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/expense/get-expense")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(Expense.class)
                .collectList();
    }

    public Mono<Trader> findTraderRFID(String rfId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/find-trader-rfid")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .queryParam("rfId", rfId)
                .build())
                .retrieve()
                .bodyToMono(Trader.class);
    }

    public Mono<List<Region>> getRegion() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-region")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(Region.class)
                .collectList();
    }

    public Mono<Location> findLocation(String locCode, Integer deptId) {
        LocationKey key = new LocationKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setLocCode(locCode);
        return inventoryApi.post()
                .uri("/setup/find-location")
                .body(Mono.just(key), LocationKey.class)
                .retrieve()
                .bodyToMono(Location.class).cache();
    }

    public Mono<StockBrand> findBrand(String brandCode, Integer deptId) {
        StockBrandKey key = new StockBrandKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setBrandCode(brandCode);
        return inventoryApi.post()
                .uri("/setup/find-brand")
                .body(Mono.just(key), StockBrandKey.class)
                .retrieve()
                .bodyToMono(StockBrand.class);
    }

    public Mono<VouStatus> findVouStatus(String code, Integer deptId) {
        VouStatusKey key = new VouStatusKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setCode(code);
        return inventoryApi.post()
                .uri("/setup/find-voucher-status")
                .body(Mono.just(key), VouStatusKey.class)
                .retrieve()
                .bodyToMono(VouStatus.class);
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
                .bodyToMono(StockUnit.class);
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
                .bodyToMono(UnitRelation.class);
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
                .bodyToMono(Category.class);
    }

    public Mono<Stock> findStock(String stockCode) {
        StockKey key = new StockKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setStockCode(stockCode);
        return inventoryApi.post()
                .uri("/setup/find-stock")
                .body(Mono.just(key), StockKey.class)
                .exchangeToMono((t) -> {
                    if (t.statusCode().is2xxSuccessful()) {
                        return t.bodyToMono(Stock.class);
                    }
                    return null;
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
                .bodyToMono(StockType.class);
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
                .bodyToMono(WeightLossHis.class);
    }

    public Mono<ProcessHis> findProcess(ProcessHisKey key) {
        return inventoryApi.post()
                .uri("/process/find-process")
                .body(Mono.just(key), ProcessHisKey.class)
                .retrieve()
                .bodyToMono(ProcessHis.class);
    }
    
    public Mono<List<Location>> getLocation() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-location")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve().bodyToFlux(Location.class).collectList();
    }

    public Flux<Stock> getStock(boolean active) {
        Flux<Stock> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-stock")
                .queryParam("compCode", Global.compCode)
                .queryParam("active", active)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve().bodyToFlux(Stock.class);
        return result;
    }

    public Mono<List<Stock>> getStock(String str) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-stock-list")
                .queryParam("text", str)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(Stock.class)
                .collectList();
    }

    public Mono<List<Stock>> getService() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-service")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(Stock.class)
                .collectList();
    }

    public Mono<List<String>> deleteStock(StockKey key) {
        return inventoryApi.post()
                .uri("/setup/delete-stock")
                .body(Mono.just(key), StockKey.class)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList();
    }

    public Mono<List<VouStatus>> getVoucherStatus() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-voucher-status")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve()
                .bodyToFlux(VouStatus.class)
                .collectList();
    }

    public Mono<List<UnitRelation>> getUnitRelation() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-unit-relation")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", ProUtil.getDepId())
                .build())
                .retrieve().bodyToFlux(UnitRelation.class).collectList();
    }

    public Mono<Trader> saveTrader(Trader t) {
        return inventoryApi.post()
                .uri("/setup/save-trader")
                .body(Mono.just(t), Trader.class)
                .retrieve()
                .bodyToMono(Trader.class);
    }

    public Mono<Stock> saveStock(Stock s) {
        return inventoryApi.post()
                .uri("/setup/save-stock")
                .body(Mono.just(s), Stock.class)
                .retrieve()
                .bodyToMono(Stock.class);
    }

    public Mono<Currency> saveCurrency(Currency c) {
        return inventoryApi.post()
                .uri("/setup/save-currency")
                .body(Mono.just(c), Currency.class)
                .retrieve()
                .bodyToMono(Currency.class);
    }

    public Mono<Location> saveLocaiton(Location loc) {
        return inventoryApi.post()
                .uri("/setup/save-location")
                .body(Mono.just(loc), Location.class)
                .retrieve()
                .bodyToMono(Location.class);
    }

    public Mono<Region> saveRegion(Region reg) {
        return inventoryApi.post()
                .uri("/setup/save-region")
                .body(Mono.just(reg), Region.class)
                .retrieve()
                .bodyToMono(Region.class);
    }

    public Mono<SaleMan> saveSaleMan(SaleMan s) {
        return inventoryApi.post()
                .uri("/setup/save-saleman")
                .body(Mono.just(s), SaleMan.class)
                .retrieve()
                .bodyToMono(SaleMan.class);
    }

    public Mono<StockBrand> saveBrand(StockBrand s) {
        return inventoryApi.post()
                .uri("/setup/save-brand")
                .body(Mono.just(s), StockBrand.class)
                .retrieve()
                .bodyToMono(StockBrand.class);
    }

    public Mono<Expense> saveExpense(Expense s) {
        return inventoryApi.post()
                .uri("/expense/save-expense")
                .body(Mono.just(s), Expense.class)
                .retrieve()
                .bodyToMono(Expense.class);
    }

    public Mono<StockType> saveStockType(StockType t) {
        return inventoryApi.post()
                .uri("/setup/save-type")
                .body(Mono.just(t), StockType.class)
                .retrieve()
                .bodyToMono(StockType.class);
    }

    public Mono<StockUnit> saveStockUnit(StockUnit unit) {
        return inventoryApi.post()
                .uri("/setup/save-unit")
                .body(Mono.just(unit), StockUnit.class)
                .retrieve()
                .bodyToMono(StockUnit.class);
    }

    public Mono<VouStatus> saveVouStatus(VouStatus vou) {
        return inventoryApi.post()
                .uri("/setup/save-voucher-status")
                .body(Mono.just(vou), VouStatus.class)
                .retrieve()
                .bodyToMono(VouStatus.class);
    }

    public Mono<ProcessType> saveProcessType(ProcessType vou) {
        return inventoryApi.post()
                .uri("/setup/save-process-type")
                .body(Mono.just(vou), ProcessType.class)
                .retrieve()
                .bodyToMono(ProcessType.class);
    }

    public Mono<Category> saveCategory(Category category) {
        return inventoryApi.post()
                .uri("/setup/save-category")
                .body(Mono.just(category), Category.class)
                .retrieve()
                .bodyToMono(Category.class);
    }

    public Mono<Pattern> savePattern(Pattern pattern) {
        return inventoryApi.post()
                .uri("/setup/save-pattern")
                .body(Mono.just(pattern), Pattern.class)
                .retrieve()
                .bodyToMono(Pattern.class);
    }

    public Mono<WeightLossHis> saveWeightLoss(WeightLossHis loss) {
        return inventoryApi.post()
                .uri("/weight/save-weight-loss")
                .body(Mono.just(loss), WeightLossHis.class)
                .retrieve()
                .bodyToMono(WeightLossHis.class);
    }

    public Mono<Boolean> delete(Pattern p) {
        return inventoryApi.post()
                .uri("/setup/delete-pattern")
                .body(Mono.just(p), Pattern.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<UnitRelation> saveUnitRelation(UnitRelation rel) {
        return inventoryApi.post()
                .uri("/setup/save-unit-relation")
                .body(Mono.just(rel), UnitRelation.class)
                .retrieve()
                .bodyToMono(UnitRelation.class);
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
                .uri(builder -> builder.path("/report/get-purchase-recent-price")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().bodyToMono(General.class);
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
                .retrieve().bodyToMono(General.class);
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
                .retrieve().bodyToMono(General.class);
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
                .retrieve().bodyToMono(General.class);
    }

    public Mono<General> getSaleRecentPrice(String stockCode, String vouDate, String unit) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/get-sale-recent-price")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToMono(General.class);
    }

    public Mono<General> getStockIORecentPrice(String stockCode, String vouDate, String unit) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/get-stock-io-recent-price")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .build())
                .retrieve().bodyToMono(General.class);
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
                .collectList();
    }

    public Mono<List<ProcessType>> getProcessType() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-process-type")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(ProcessType.class)
                .collectList();
    }

    public Mono<List<CFont>> getFont() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-font")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve()
                .bodyToFlux(CFont.class)
                .collectList();
    }

    public Mono<StockInOut> findStockIO(String vouNo, Integer deptId) {
        StockIOKey key = new StockIOKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        key.setVouNo(vouNo);
        return inventoryApi.post()
                .uri("/stockio/find-stockio")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(StockInOut.class);
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
                .bodyToMono(TransferHis.class);
    }

    public Mono<SaleHis> findSale(String vouNo, Integer deptId) {
        SaleHisKey key = new SaleHisKey();
        key.setVouNo(vouNo);
        key.setCompCode(Global.compCode);
        key.setDeptId(deptId);
        return inventoryApi.post()
                .uri("/sale/find-sale")
                .body(Mono.just(key), SaleHisKey.class)
                .retrieve()
                .bodyToMono(SaleHis.class);
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
                .bodyToMono(OrderHis.class);
    }

    public Mono<OPHis> findOpening(OPHisKey key) {
        return inventoryApi.post()
                .uri("/setup/find-opening")
                .body(Mono.just(key), OPHisKey.class)
                .retrieve()
                .bodyToMono(OPHis.class);
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
                .bodyToMono(PurHis.class);
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
                .bodyToMono(RetInHis.class);
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
                .bodyToMono(RetOutHis.class);
    }

    public Mono<General> getSmallQty(String stockCode, String unit) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/get-smallest_qty")
                .queryParam("stockCode", stockCode)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().bodyToMono(General.class);
    }

    public Mono<ReorderLevel> saveReorder(ReorderLevel rl) {
        return inventoryApi.post()
                .uri("/setup/save-reorder")
                .body(Mono.just(rl), ReorderLevel.class)
                .retrieve()
                .bodyToMono(ReorderLevel.class);
    }

    public Mono<List<String>> deleteTrader(TraderKey key) {
        return inventoryApi.post()
                .uri("/setup/delete-trader")
                .body(Mono.just(key), TraderKey.class)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList();
    }

    public Mono<TraderGroup> saveTraderGroup(TraderGroup rl) {
        return inventoryApi.post()
                .uri("/setup/save-trader-group")
                .body(Mono.just(rl), TraderGroup.class)
                .retrieve()
                .bodyToMono(TraderGroup.class);
    }

    public Mono<List<TraderGroup>> getTraderGroup() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-trader-group")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve()
                .bodyToFlux(TraderGroup.class)
                .collectList();
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
                .collectList();
    }

    public Mono<Boolean> delete(OPHisKey key) {
        return inventoryApi.post()
                .uri("/setup/delete-opening")
                .body(Mono.just(key), OPHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(SaleHisKey key) {
        return inventoryApi.post()
                .uri("/sale/delete-sale")
                .body(Mono.just(key), SaleHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(OrderHisKey key) {
        return inventoryApi.post()
                .uri("/order/delete-order")
                .body(Mono.just(key), OrderHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> restore(SaleHisKey key) {
        return inventoryApi.post()
                .uri("/sale/restore-sale")
                .body(Mono.just(key), SaleHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> restore(OrderHisKey key) {
        return inventoryApi.post()
                .uri("/sale/restore-sale")
                .body(Mono.just(key), OrderHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> restore(RetInHisKey key) {
        return inventoryApi.post()
                .uri("/retin/restore-retin")
                .body(Mono.just(key), RetInHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> restore(RetOutHisKey key) {
        return inventoryApi.post()
                .uri("/retout/restore-retout")
                .body(Mono.just(key), RetOutHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(PurHisKey key) {
        return inventoryApi.post()
                .uri("/pur/delete-pur")
                .body(Mono.just(key), PurHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> restore(PurHisKey key) {
        return inventoryApi.post()
                .uri("/pur/restore-pur")
                .body(Mono.just(key), PurHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(RetInHisKey key) {
        return inventoryApi.post()
                .uri("/retin/delete-retin")
                .body(Mono.just(key), RetInHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(RetOutHisKey key) {
        return inventoryApi.post()
                .uri("/retout/delete-retout")
                .body(Mono.just(key), RetOutHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(StockIOKey key) {
        return inventoryApi.post()
                .uri("/stockio/delete-stockio")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> restore(StockIOKey key) {
        return inventoryApi.post()
                .uri("/stockio/restore-stockio")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(TransferHisKey key) {
        return inventoryApi.post()
                .uri("/transfer/delete-transfer")
                .body(Mono.just(key), TransferHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> restore(TransferHisKey key) {
        return inventoryApi.post()
                .uri("/transfer/restore-transfer")
                .body(Mono.just(key), TransferHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
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
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> open(GRNKey key) {
        return inventoryApi.post()
                .uri("/grn/open-grn")
                .body(Mono.just(key), GRNKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(ProcessHisDetailKey key) {
        return inventoryApi.post()
                .uri("/process/delete-process-detail")
                .body(Mono.just(key), ProcessHisDetailKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> restore(ProcessHisKey key) {
        return inventoryApi.post()
                .uri("/process/restore-process")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> delete(WeightLossHisKey key) {
        return inventoryApi.post()
                .uri("/weight/delete-weight-loss")
                .body(Mono.just(key), WeightLossHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<Boolean> restore(WeightLossHisKey key) {
        return inventoryApi.post()
                .uri("/weight/restore-weight-loss")
                .body(Mono.just(key), WeightLossHisKey.class)
                .retrieve()
                .bodyToMono(Boolean.class);
    }

    public Mono<ProcessHis> saveProcess(ProcessHis his) {
        return inventoryApi.post()
                .uri("/process/save-process")
                .body(Mono.just(his), ProcessHis.class)
                .retrieve()
                .bodyToMono(ProcessHis.class);
    }

    public Mono<ProcessHisDetail> saveProcessDetail(ProcessHisDetail his) {
        return inventoryApi.post()
                .uri("/process/save-process-detail")
                .body(Mono.just(his), ProcessHisDetail.class)
                .retrieve()
                .bodyToMono(ProcessHisDetail.class);
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
                .collectList();
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
                .retrieve().bodyToMono(General.class);
    }

    public Mono<GRN> saveGRN(GRN grn) {
        return inventoryApi.post()
                .uri("/grn")
                .body(Mono.just(grn), GRN.class)
                .retrieve()
                .bodyToMono(GRN.class);
    }

    public Flux<GRN> getGRNHistory(FilterObject filter) {
        Flux<GRN> result = inventoryApi
                .post()
                .uri("/grn/history")
                .body(Mono.just(filter), FilterObject.class)
                .retrieve()
                .bodyToFlux(GRN.class);
        return result;
    }

    public Flux<SaleHisDetail> getSaleByBatch(String batchNo, boolean detail) {
        Flux<SaleHisDetail> result = inventoryApi.get()
                .uri(builder -> builder.path("/sale/get-sale-by-batch")
                .queryParam("batchNo", batchNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .queryParam("detail", detail)
                .build())
                .retrieve().bodyToFlux(SaleHisDetail.class);
        return result;
    }

    public Mono<PurHis> save(PurHis ph) {
        return inventoryApi.post()
                .uri("/pur/save-pur")
                .body(Mono.just(ph), PurHis.class)
                .retrieve()
                .bodyToMono(PurHis.class);
    }

    public Mono<SaleHis> save(SaleHis sh) {
        return inventoryApi.post()
                .uri("/sale/save-sale")
                .body(Mono.just(sh), SaleHis.class)
                .retrieve()
                .bodyToMono(SaleHis.class);
    }

    public Mono<OrderHis> save(OrderHis sh) {
        return inventoryApi.post()
                .uri("/order/save-order")
                .body(Mono.just(sh), OrderHis.class)
                .retrieve()
                .bodyToMono(OrderHis.class);
    }

    public Mono<AccSetting> save(AccSetting sh) {
        return inventoryApi.post()
                .uri("/setup/saveAccSetting")
                .body(Mono.just(sh), AccSetting.class)
                .retrieve()
                .bodyToMono(AccSetting.class);
    }

    public Mono<List<AccSetting>> getAccSetting() {
        return inventoryApi.get()
                .uri(builder -> builder.path("/setup/getAccSetting")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToFlux(AccSetting.class)
                .collectList();
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
                .collectList();
    }

    public Mono<List<SaleHisDetail>> getSaleDetail(String vouNo, int deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/sale/get-sale-detail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve().bodyToFlux(SaleHisDetail.class)
                .collectList();
    }

    public Mono<byte[]> getSaleReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/get-sale-report")
                .queryParam("vouNo", vouNo)
                .queryParam("macId", Global.macId)
                .build())
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(ByteArrayResource::getByteArray);
    }

    public Mono<byte[]> getOrderReport(String vouNo) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/report/get-order-report")
                .queryParam("vouNo", vouNo)
                .queryParam("macId", Global.macId)
                .build())
                .retrieve()
                .bodyToMono(ByteArrayResource.class)
                .map(ByteArrayResource::getByteArray);
    }

    public Mono<List<VSale>> getOrder(FilterObject filter) {
        return inventoryApi.post()
                .uri("/order/get-order")
                .body(Mono.just(filter), FilterObject.class)
                .retrieve()
                .bodyToFlux(VSale.class)
                .collectList();
    }

    public Mono<List<OrderHisDetail>> getOrderDetail(String vouNo, int deptId) {
        return inventoryApi.get()
                .uri(builder -> builder.path("/order/get-order-detail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", deptId)
                .build())
                .retrieve().bodyToFlux(OrderHisDetail.class)
                .collectList();

    }

    public Mono<List<VSale>> getSaleHistory(FilterObject filter) {
        return inventoryApi.post()
                .uri("/sale/get-sale")
                .body(Mono.just(filter), FilterObject.class)
                .retrieve()
                .bodyToFlux(VSale.class)
                .collectList();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.inventory.model.CFont;
import com.user.model.Currency;
import com.common.Global;
import com.common.ReturnObject;
import com.common.Util1;
import com.inventory.model.Category;
import com.inventory.model.CategoryKey;
import com.inventory.model.General;
import com.inventory.model.Location;
import com.inventory.model.LocationKey;
import com.inventory.model.OPHis;
import com.inventory.model.OPHisKey;
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
import com.inventory.model.VouStatus;
import com.inventory.model.VouStatusKey;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
@Slf4j
public class InventoryRepo {

    int min = 1;
    private List<PriceOption> listPO = null;
    @Autowired
    private WebClient inventoryApi;

    public Trader getDefaultCustomer() {
        String traderCode = Global.hmRoleProperty.get("default.customer");
        return findTrader(traderCode);
    }

    public Trader getDefaultSupplier() {
        String traderCode = Global.hmRoleProperty.get("default.supplier");
        return findTrader(traderCode);
    }

    public Location getDefaultLocation() {
        String locCode = Global.hmRoleProperty.get("default.location");
        return findLocation(locCode);
    }

    public SaleMan getDefaultSaleMan() {
        String code = Global.hmRoleProperty.get("default.saleman");
        return findSaleMan(code);
    }

    public List<PriceOption> getPriceOption() {
        if (listPO == null) {
            Mono<ResponseEntity<List<PriceOption>>> result = inventoryApi.get()
                    .uri(builder -> builder.path("/setup/get-price-option")
                    .queryParam("compCode", Global.compCode)
                    .build())
                    .retrieve().toEntityList(PriceOption.class);
            listPO = result.block(Duration.ofMinutes(min)).getBody();
        }
        return listPO;
    }

    public List<Category> getCategory() {
        Mono<ResponseEntity<List<Category>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-category")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(Category.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<SaleMan> getSaleMan() {
        Mono<ResponseEntity<List<SaleMan>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-saleman")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(SaleMan.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public SaleMan findSaleMan(String code) {
        SaleManKey key = new SaleManKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setSaleManCode(code);
        Mono<SaleMan> result = inventoryApi.post()
                .uri("/setup/find-saleman")
                .body(Mono.just(key), SaleManKey.class)
                .retrieve()
                .bodyToMono(SaleMan.class);
        return result.doOnError((t) -> {
            log.error(t.getMessage());
        }).block();
    }

    public List<StockBrand> getStockBrand() {
        Mono<ResponseEntity<List<StockBrand>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-brand")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(StockBrand.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<StockType> getStockType() {
        Mono<ResponseEntity<List<StockType>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-type")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(StockType.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<StockUnit> getStockUnit() {
        Mono<ResponseEntity<List<StockUnit>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-unit")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(StockUnit.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public Trader findTrader(String code) {
        TraderKey key = new TraderKey();
        key.setCode(Util1.isNull(code, "-"));
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        Mono<Trader> result = inventoryApi.post()
                .uri("/setup/find-trader")
                .body(Mono.just(key), TraderKey.class)
                .retrieve()
                .bodyToMono(Trader.class);
        return result.doOnError((t) -> {
            log.error(t.getMessage());
        }).block();
    }

    public TraderGroup findTraderGroup(String code) {
        TraderGroupKey key = new TraderGroupKey();
        key.setGroupCode(Util1.isNull(code, "-"));
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        Mono<TraderGroup> result = inventoryApi.post()
                .uri("/setup/find-trader-group")
                .body(Mono.just(key), TraderGroupKey.class)
                .retrieve()
                .bodyToMono(TraderGroup.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Region findRegion(String code) {
        RegionKey key = new RegionKey();
        key.setRegCode(Util1.isNull(code, "-"));
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        Mono<Region> result = inventoryApi.post()
                .uri("/setup/find-region")
                .body(Mono.just(key), RegionKey.class)
                .retrieve()
                .bodyToMono(Region.class);
        return result.block(Duration.ofMinutes(min));
    }

    public List<Trader> getCustomer() {
        Mono<ResponseEntity<List<Trader>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-customer")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(Trader.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Trader> getSupplier() {
        Mono<ResponseEntity<List<Trader>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-supplier")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(Trader.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Trader> getTraderList(String text, String type) {
        Mono<ResponseEntity<List<Trader>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-trader-list")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .queryParam("text", text)
                .queryParam("type", type)
                .build())
                .retrieve().toEntityList(Trader.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Region> getRegion() {
        Mono<ResponseEntity<List<Region>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-region")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(Region.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public Location findLocation(String locCode) {
        LocationKey key = new LocationKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setLocCode(locCode);
        Mono<Location> result = inventoryApi.post()
                .uri("/setup/find-location")
                .body(Mono.just(key), LocationKey.class)
                .retrieve()
                .bodyToMono(Location.class);
        return result.doOnError((t) -> {
            log.error(t.getMessage());
        }).block();
    }

    public StockBrand findBrand(String brandCode) {
        StockBrandKey key = new StockBrandKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setBrandCode(brandCode);
        Mono<StockBrand> result = inventoryApi.post()
                .uri("/setup/find-brand")
                .body(Mono.just(key), StockBrandKey.class)
                .retrieve()
                .bodyToMono(StockBrand.class);
        return result.block(Duration.ofMinutes(min));
    }

    public VouStatus findVouStatus(String code) {
        VouStatusKey key = new VouStatusKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setCode(code);
        Mono<VouStatus> result = inventoryApi.post()
                .uri("/setup/find-voucher-status")
                .body(Mono.just(key), VouStatusKey.class)
                .retrieve()
                .bodyToMono(VouStatus.class);
        return result.block(Duration.ofMinutes(min));
    }

    public StockUnit findUnit(String unitCode) {
        StockUnitKey key = new StockUnitKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setUnitCode(unitCode);
        Mono<StockUnit> result = inventoryApi.post()
                .uri("/setup/find-unit")
                .body(Mono.just(key), StockUnitKey.class)
                .retrieve()
                .bodyToMono(StockUnit.class);
        return result.block(Duration.ofMinutes(min));
    }

    public UnitRelation findRelation(String relCode) {
        RelationKey key = new RelationKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setRelCode(relCode);
        Mono<UnitRelation> result = inventoryApi.post()
                .uri("/setup/find-unit-relation")
                .body(Mono.just(key), RelationKey.class)
                .retrieve()
                .bodyToMono(UnitRelation.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Category findCategory(String catCode) {
        CategoryKey key = new CategoryKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setCatCode(catCode);
        Mono<Category> result = inventoryApi.post()
                .uri("/setup/find-brand")
                .body(Mono.just(key), CategoryKey.class)
                .retrieve()
                .bodyToMono(Category.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Stock findStock(String stockCode) {
        StockKey key = new StockKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setStockCode(stockCode);
        Mono<Stock> result = inventoryApi.post()
                .uri("/setup/find-stock")
                .body(Mono.just(key), StockKey.class)
                .retrieve()
                .bodyToMono(Stock.class);
        return result.block(Duration.ofMinutes(min));
    }

    public StockType findGroup(String typeCode) {
        StockTypeKey key = new StockTypeKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setStockTypeCode(typeCode);
        Mono<StockType> result = inventoryApi.post()
                .uri("/setup/find-type")
                .body(Mono.just(key), StockTypeKey.class)
                .retrieve()
                .bodyToMono(StockType.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Currency findCurrency(String curCode) {
        Mono<ResponseEntity<Currency>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/find-currency")
                .queryParam("curCode", curCode)
                .build())
                .retrieve().toEntity(Currency.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Location> getLocation() {
        Mono<ResponseEntity<List<Location>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-location")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(Location.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Stock> getStock(boolean active) {
        Mono<ResponseEntity<List<Stock>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-stock")
                .queryParam("compCode", Global.compCode)
                .queryParam("active", active)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(Stock.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Stock> getStock(String str) {
        Mono<ResponseEntity<List<Stock>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-stock-list")
                .queryParam("text", str)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(Stock.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<String> deleteStock(StockKey key) {
        Mono<ResponseEntity<List<String>>> result = inventoryApi.post()
                .uri("/setup/delete-stock")
                .body(Mono.just(key), StockKey.class)
                .retrieve()
                .toEntityList(String.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Currency> getCurrency() {
        Mono<ResponseEntity<List<Currency>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-currency").build())
                .retrieve().toEntityList(Currency.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<VouStatus> getVoucherStatus() {
        Mono<ResponseEntity<List<VouStatus>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-voucher-status")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(VouStatus.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<UnitRelation> getUnitRelation() {
        Mono<ResponseEntity<List<UnitRelation>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-unit-relation")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(UnitRelation.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public Trader saveTrader(Trader t) {
        Mono<Trader> result = inventoryApi.post()
                .uri("/setup/save-trader")
                .body(Mono.just(t), Trader.class)
                .retrieve()
                .bodyToMono(Trader.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Stock saveStock(Stock s) {
        Mono<Stock> result = inventoryApi.post()
                .uri("/setup/save-stock")
                .body(Mono.just(s), Stock.class)
                .retrieve()
                .bodyToMono(Stock.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Currency saveCurrency(Currency c) {
        Mono<Currency> result = inventoryApi.post()
                .uri("/setup/save-currency")
                .body(Mono.just(c), Currency.class)
                .retrieve()
                .bodyToMono(Currency.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Location saveLocaiton(Location loc) {
        Mono<Location> result = inventoryApi.post()
                .uri("/setup/save-location")
                .body(Mono.just(loc), Location.class)
                .retrieve()
                .bodyToMono(Location.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Region saveRegion(Region reg) {
        Mono<Region> result = inventoryApi.post()
                .uri("/setup/save-region")
                .body(Mono.just(reg), Region.class)
                .retrieve()
                .bodyToMono(Region.class);
        return result.block(Duration.ofMinutes(min));
    }

    public SaleMan saveSaleMan(SaleMan s) {
        Mono<SaleMan> result = inventoryApi.post()
                .uri("/setup/save-saleman")
                .body(Mono.just(s), SaleMan.class)
                .retrieve()
                .bodyToMono(SaleMan.class);
        return result.block(Duration.ofMinutes(min));
    }

    public StockBrand saveBrand(StockBrand s) {
        Mono<StockBrand> result = inventoryApi.post()
                .uri("/setup/save-brand")
                .body(Mono.just(s), StockBrand.class)
                .retrieve()
                .bodyToMono(StockBrand.class);
        return result.block(Duration.ofMinutes(min));
    }

    public StockType saveStockType(StockType t) {
        Mono<StockType> result = inventoryApi.post()
                .uri("/setup/save-type")
                .body(Mono.just(t), StockType.class)
                .retrieve()
                .bodyToMono(StockType.class);
        return result.block(Duration.ofMinutes(min));
    }

    public StockUnit saveStockUnit(StockUnit unit) {
        Mono<StockUnit> result = inventoryApi.post()
                .uri("/setup/save-unit")
                .body(Mono.just(unit), StockUnit.class)
                .retrieve()
                .bodyToMono(StockUnit.class);
        return result.block(Duration.ofMinutes(min));
    }

    public VouStatus saveVouStatus(VouStatus vou) {
        Mono<VouStatus> result = inventoryApi.post()
                .uri("/setup/save-voucher-status")
                .body(Mono.just(vou), VouStatus.class)
                .retrieve()
                .bodyToMono(VouStatus.class);
        return result.block(Duration.ofMinutes(min));
    }

    public ProcessType saveProcessType(ProcessType vou) {
        Mono<ProcessType> result = inventoryApi.post()
                .uri("/setup/save-process-type")
                .body(Mono.just(vou), ProcessType.class)
                .retrieve()
                .bodyToMono(ProcessType.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Category saveCategory(Category category) {
        Mono<Category> result = inventoryApi.post()
                .uri("/setup/save-category")
                .body(Mono.just(category), Category.class)
                .retrieve()
                .bodyToMono(Category.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Pattern savePattern(Pattern pattern) {
        Mono<Pattern> result = inventoryApi.post()
                .uri("/setup/save-pattern")
                .body(Mono.just(pattern), Pattern.class)
                .retrieve()
                .bodyToMono(Pattern.class);
        return result.block(Duration.ofMinutes(min));
    }

    public void delete(Pattern p) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/setup/delete-pattern")
                .body(Mono.just(p), Pattern.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public UnitRelation saveUnitRelation(UnitRelation rel) {
        Mono<UnitRelation> result = inventoryApi.post()
                .uri("/setup/save-unit-relation")
                .body(Mono.just(rel), UnitRelation.class)
                .retrieve()
                .bodyToMono(UnitRelation.class);
        return result.block(Duration.ofMinutes(min));
    }

    public Float getPurRecentPrice(String stockCode, String vouDate, String unit) {
        Mono<General> result = inventoryApi.get()
                .uri(builder -> builder.path("/report/get-purchase-recent-price")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().bodyToMono(General.class);
        return Util1.getFloat(result.block().getAmount());
    }

    public Float getPurAvgPrice(String stockCode, String vouDate, String unit) {
        Mono<General> result = inventoryApi.get()
                .uri(builder -> builder.path("/report/get-purchase-avg-price")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().bodyToMono(General.class);
        return Util1.getFloat(result.block().getAmount());
    }

    public Float getProductionRecentPrice(String stockCode, String vouDate, String unit) {
        Mono<General> result = inventoryApi.get()
                .uri(builder -> builder.path("/report/get-production-recent-price")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().bodyToMono(General.class);
        return Util1.getFloat(result.block().getAmount());
    }

    public Float getSaleRecentPrice(String stockCode, String vouDate, String unit) {
        Mono<General> result = inventoryApi.get()
                .uri(builder -> builder.path("/report/get-sale-recent-price")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().bodyToMono(General.class);
        return Util1.getFloat(result.block().getAmount());
    }

    public Float getStockIORecentPrice(String stockCode, String vouDate, String unit) {
        Mono<General> result = inventoryApi.get()
                .uri(builder -> builder.path("/report/get-stock-io-recent-price")
                .queryParam("stockCode", stockCode)
                .queryParam("vouDate", vouDate)
                .queryParam("unit", unit)
                .build())
                .retrieve().bodyToMono(General.class);
        return Util1.getFloat(result.block().getAmount());
    }

    public List<UnitRelationDetail> getRelationDetail(String code) {
        Mono<ResponseEntity<List<UnitRelationDetail>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-unit-relation-detail")
                .queryParam("code", code)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(UnitRelationDetail.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<ProcessType> getProcessType() {
        Mono<ResponseEntity<List<ProcessType>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-process-type")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(ProcessType.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<CFont> getFont() {
        Mono<ResponseEntity<List<CFont>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-font")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(CFont.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public StockInOut findStockIO(String vouNo) {
        StockIOKey key = new StockIOKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setVouNo(vouNo);
        Mono<StockInOut> result = inventoryApi.post()
                .uri("/stockio/find-stockio")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(StockInOut.class);
        return result.block(Duration.ofMinutes(min));
    }

    public TransferHis findTransfer(String vouNo) {
        TransferHisKey key = new TransferHisKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setVouNo(vouNo);
        Mono<TransferHis> result = inventoryApi.post()
                .uri("/transfer/find-transfer")
                .body(Mono.just(key), TransferHisKey.class)
                .retrieve()
                .bodyToMono(TransferHis.class);
        return result.block(Duration.ofMinutes(min));
    }

    public SaleHis findSale(String vouNo) {
        SaleHisKey key = new SaleHisKey();
        key.setVouNo(vouNo);
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        Mono<SaleHis> result = inventoryApi.post()
                .uri("/sale/find-sale")
                .body(Mono.just(key), SaleHisKey.class)
                .retrieve()
                .bodyToMono(SaleHis.class);
        return result.block(Duration.ofMinutes(min));
    }

    public OPHis findOpening(String vouNo) {
        OPHisKey key = new OPHisKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setVouNo(vouNo);
        Mono<OPHis> result = inventoryApi.post()
                .uri("/setup/find-opening")
                .body(Mono.just(key), OPHisKey.class)
                .retrieve()
                .bodyToMono(OPHis.class);
        return result.block(Duration.ofMinutes(min));
    }

    public PurHis findPurchase(String vouNo) {
        PurHisKey key = new PurHisKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setVouNo(vouNo);
        Mono<PurHis> result = inventoryApi.post()
                .uri("/pur/find-pur")
                .body(Mono.just(key), PurHisKey.class)
                .retrieve()
                .bodyToMono(PurHis.class);
        return result.block(Duration.ofMinutes(min));
    }

    public RetInHis findReturnIn(String vouNo) {
        RetInHisKey key = new RetInHisKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setVouNo(vouNo);
        Mono<RetInHis> result = inventoryApi.post()
                .uri("/retin/find-retin")
                .body(Mono.just(key), RetInHisKey.class)
                .retrieve()
                .bodyToMono(RetInHis.class);
        return result.block(Duration.ofMinutes(min));
    }

    public RetOutHis findReturnOut(String vouNo) {
        RetOutHisKey key = new RetOutHisKey();
        key.setCompCode(Global.compCode);
        key.setDeptId(Global.deptId);
        key.setVouNo(vouNo);
        Mono<RetOutHis> result = inventoryApi.post()
                .uri("/retout/find-retout")
                .body(Mono.just(key), RetOutHisKey.class)
                .retrieve()
                .bodyToMono(RetOutHis.class);
        return result.block(Duration.ofMinutes(min));
    }

    public float getSmallQty(String stockCode, String unit) {
        Mono<Float> result = inventoryApi.get()
                .uri(builder -> builder.path("/report/get-smallest_qty")
                .queryParam("stockCode", stockCode)
                .queryParam("unit", unit)
                .build())
                .retrieve().bodyToMono(Float.class);
        return result.block(Duration.ofMinutes(min));
    }

    public ReorderLevel saveReorder(ReorderLevel rl) {
        Mono<ReorderLevel> result = inventoryApi.post()
                .uri("/setup/save-reorder")
                .body(Mono.just(rl), ReorderLevel.class)
                .retrieve()
                .bodyToMono(ReorderLevel.class);
        return result.block(Duration.ofMinutes(min));
    }

    public List<String> deleteTrader(TraderKey key) {
        Mono<ResponseEntity<List<String>>> result = inventoryApi.post()
                .uri("/setup/delete-trader")
                .body(Mono.just(key), TraderKey.class)
                .retrieve()
                .toEntityList(String.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public TraderGroup saveTraderGroup(TraderGroup rl) {
        Mono<TraderGroup> result = inventoryApi.post()
                .uri("/setup/save-trader-group")
                .body(Mono.just(rl), TraderGroup.class)
                .retrieve()
                .bodyToMono(TraderGroup.class);
        return result.block(Duration.ofMinutes(min));
    }

    public List<TraderGroup> getTraderGroup() {
        Mono<ResponseEntity<List<TraderGroup>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-trader-group")
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(TraderGroup.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Pattern> getPattern(String stockCode) {
        Mono<ResponseEntity<List<Pattern>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-pattern")
                .queryParam("stockCode", stockCode)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(Pattern.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public void delete(OPHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/setup/delete-opening")
                .body(Mono.just(key), OPHisKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void delete(SaleHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/sale/delete-sale")
                .body(Mono.just(key), SaleHisKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void restore(SaleHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/sale/restore-sale")
                .body(Mono.just(key), SaleHisKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void restore(RetInHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/retin/restore-retin")
                .body(Mono.just(key), RetInHisKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void restore(RetOutHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/retout/restore-retout")
                .body(Mono.just(key), RetOutHisKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void delete(PurHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/pur/delete-pur")
                .body(Mono.just(key), PurHisKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void restore(PurHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/pur/restore-pur")
                .body(Mono.just(key), PurHisKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void delete(RetInHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/retin/delete-retin")
                .body(Mono.just(key), RetInHisKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void delete(RetOutHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/retout/delete-retout")
                .body(Mono.just(key), RetOutHisKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void delete(StockIOKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/stockio/delete-stockio")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void restore(StockIOKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/stockio/restore-stockio")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void delete(TransferHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/transfer/delete-transfer")
                .body(Mono.just(key), TransferHisKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void restore(TransferHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/transfer/restore-transfer")
                .body(Mono.just(key), TransferHisKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void delete(ProcessHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/process/delete-process")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void delete(ProcessHisDetailKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/process/delete-process-detail")
                .body(Mono.just(key), ProcessHisDetailKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public void restore(ProcessHisKey key) {
        Mono<ReturnObject> result = inventoryApi.post()
                .uri("/process/restore-process")
                .body(Mono.just(key), StockIOKey.class)
                .retrieve()
                .bodyToMono(ReturnObject.class);
        result.block(Duration.ofMinutes(min));
    }

    public ProcessHis saveProcess(ProcessHis his) {
        Mono<ProcessHis> result = inventoryApi.post()
                .uri("/process/save-process")
                .body(Mono.just(his), ProcessHis.class)
                .retrieve()
                .bodyToMono(ProcessHis.class);
        return result.block(Duration.ofMinutes(min));
    }

    public ProcessHisDetail saveProcessDetail(ProcessHisDetail his) {
        Mono<ProcessHisDetail> result = inventoryApi.post()
                .uri("/process/save-process-detail")
                .body(Mono.just(his), ProcessHisDetail.class)
                .retrieve()
                .bodyToMono(ProcessHisDetail.class);
        return result.block(Duration.ofMinutes(min));
    }

    public List<ProcessHisDetail> getProcessDetail(String vouNo) {
        Mono<ResponseEntity<List<ProcessHisDetail>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/process/get-process-detail")
                .queryParam("vouNo", vouNo)
                .queryParam("compCode", Global.compCode)
                .queryParam("deptId", Global.deptId)
                .build())
                .retrieve().toEntityList(ProcessHisDetail.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }
}

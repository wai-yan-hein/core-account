/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.inventory.ui.common;

import com.inventory.model.CFont;
import com.user.model.Currency;
import com.common.Global;
import com.common.Util1;
import com.inventory.model.Category;
import com.inventory.model.General;
import com.inventory.model.Location;
import com.inventory.model.OPHis;
import com.inventory.model.Pattern;
import com.inventory.model.PriceOption;
import com.inventory.model.ProcessType;
import com.inventory.model.PurHis;
import com.inventory.model.Region;
import com.inventory.model.ReorderLevel;
import com.inventory.model.RetInHis;
import com.inventory.model.RetOutHis;
import com.inventory.model.SaleHis;
import com.inventory.model.SaleMan;
import com.inventory.model.Stock;
import com.inventory.model.StockBrand;
import com.inventory.model.StockInOut;
import com.inventory.model.StockKey;
import com.inventory.model.StockType;
import com.inventory.model.StockUnit;
import com.inventory.model.SysProperty;
import com.inventory.model.Trader;
import com.inventory.model.TraderGroup;
import com.inventory.model.TraderKey;
import com.inventory.model.TransferHis;
import com.inventory.model.TransferHisKey;
import com.inventory.model.UnitRelation;
import com.inventory.model.UnitRelationDetail;
import com.inventory.model.VouStatus;
import java.time.Duration;
import java.util.List;
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
public class InventoryRepo {

    int min = 1;
    private List<PriceOption> listPO = null;
    @Autowired
    private WebClient inventoryApi;

    public Trader getDefaultCustomer() {
        String traderCode = Global.hmRoleProperty.get("default.customer");
        return findTrader(new TraderKey(Util1.isNull(traderCode, "-"), Global.compCode));
    }

    public Trader getDefaultSupplier() {
        String traderCode = Global.hmRoleProperty.get("default.supplier");
        return findTrader(new TraderKey(Util1.isNull(traderCode, "-"), Global.compCode));
    }

    public Location getDefaultLocation() {
        String locCode = Global.hmRoleProperty.get("default.location");
        return findLocation(Util1.isNull(locCode, "-"));
    }

    public SaleMan getDefaultSaleMan() {
        String code = Global.hmRoleProperty.get("default.saleman");
        return findSaleMan(Util1.isNull(code, "-"));
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
                .build())
                .retrieve().toEntityList(Category.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<SaleMan> getSaleMan() {
        Mono<ResponseEntity<List<SaleMan>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-saleman")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(SaleMan.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public SaleMan findSaleMan(String smCode) {
        Mono<ResponseEntity<SaleMan>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/find-saleman")
                .queryParam("smCode", smCode)
                .build())
                .retrieve().toEntity(SaleMan.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<StockBrand> getStockBrand() {
        Mono<ResponseEntity<List<StockBrand>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-brand")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(StockBrand.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<StockType> getStockType() {
        Mono<ResponseEntity<List<StockType>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-type")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(StockType.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<StockUnit> getStockUnit() {
        Mono<ResponseEntity<List<StockUnit>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-unit")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(StockUnit.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public Trader findTrader(TraderKey key) {
        Mono<Trader> result = inventoryApi.post()
                .uri("/setup/find-trader")
                .body(Mono.just(key), Trader.class)
                .retrieve()
                .bodyToMono(Trader.class);
        return result.block(Duration.ofMinutes(min));
    }

    public List<Trader> getCustomer() {
        Mono<ResponseEntity<List<Trader>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-customer")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(Trader.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Trader> getSupplier() {
        Mono<ResponseEntity<List<Trader>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-supplier")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(Trader.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Trader> getTraderList(String text, String type) {
        Mono<ResponseEntity<List<Trader>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-trader-list")
                .queryParam("compCode", Global.compCode)
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
                .build())
                .retrieve().toEntityList(Region.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public Location findLocation(String locCode) {
        Mono<ResponseEntity<Location>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/find-location")
                .queryParam("locCode", locCode)
                .build())
                .retrieve().toEntity(Location.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Location> getLocation() {
        Mono<ResponseEntity<List<Location>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-location")
                .queryParam("compCode", Global.compCode)
                .build())
                .retrieve().toEntityList(Location.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Stock> getStock(boolean active) {
        Mono<ResponseEntity<List<Stock>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-stock")
                .queryParam("compCode", Global.compCode)
                .queryParam("active", active)
                .build())
                .retrieve().toEntityList(Stock.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Stock> getStock(String str) {
        Mono<ResponseEntity<List<Stock>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-stock-list")
                .queryParam("text", str)
                .queryParam("compCode", Global.compCode)
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
                .build())
                .retrieve().toEntityList(VouStatus.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<UnitRelation> getUnitRelation() {
        Mono<ResponseEntity<List<UnitRelation>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-unit-relation")
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
        Mono<StockInOut> result = inventoryApi.get()
                .uri(builder -> builder.path("/stockio/find-stockio")
                .queryParam("code", vouNo)
                .build())
                .retrieve().bodyToMono(StockInOut.class);
        return result.block(Duration.ofMinutes(min));
    }

    public TransferHis findTransfer(TransferHisKey key) {
        Mono<TransferHis> result = inventoryApi.post()
                .uri("/transfer/find-transfer")
                .body(Mono.just(key), TransferHisKey.class)
                .retrieve()
                .bodyToMono(TransferHis.class);
        return result.block(Duration.ofMinutes(min));
    }

    public SaleHis findSale(String vouNo) {
        Mono<SaleHis> result = inventoryApi.get()
                .uri(builder -> builder.path("/sale/find-sale")
                .queryParam("code", vouNo)
                .build())
                .retrieve().bodyToMono(SaleHis.class);
        return result.block(Duration.ofMinutes(min));
    }

    public OPHis findOpening(String vouNo) {
        Mono<OPHis> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/find-opening")
                .queryParam("code", vouNo)
                .build())
                .retrieve().bodyToMono(OPHis.class);
        return result.block(Duration.ofMinutes(min));
    }

    public PurHis findPurchase(String vouNo) {
        Mono<PurHis> result = inventoryApi.get()
                .uri(builder -> builder.path("/pur/find-pur")
                .queryParam("code", vouNo)
                .build())
                .retrieve().bodyToMono(PurHis.class);
        return result.block(Duration.ofMinutes(min));
    }

    public RetInHis findReturnIn(String vouNo) {
        Mono<RetInHis> result = inventoryApi.get()
                .uri(builder -> builder.path("/retin/find-retin")
                .queryParam("code", vouNo)
                .build())
                .retrieve().bodyToMono(RetInHis.class);
        return result.block(Duration.ofMinutes(min));
    }

    public RetOutHis findReturnOut(String vouNo) {
        Mono<RetOutHis> result = inventoryApi.get()
                .uri(builder -> builder.path("/retout/find-retout")
                .queryParam("code", vouNo)
                .build())
                .retrieve().bodyToMono(RetOutHis.class);
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
                .build())
                .retrieve().toEntityList(TraderGroup.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

    public List<Pattern> getPattern(String stockCode) {
        Mono<ResponseEntity<List<Pattern>>> result = inventoryApi.get()
                .uri(builder -> builder.path("/setup/get-pattern")
                .queryParam("stockCode", stockCode)
                .build())
                .retrieve().toEntityList(Pattern.class);
        return result.block(Duration.ofMinutes(min)).getBody();
    }

}

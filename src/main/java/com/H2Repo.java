/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.common.Global;
import com.h2.service.BrandService;
import com.h2.service.CategoryService;
import com.h2.service.LocationService;
import com.h2.service.MachineInfoService;
import com.h2.service.MenuService;
import com.h2.service.PrivilegeCompanyService;
import com.h2.service.SaleHisService;
import com.h2.service.SaleManService;
import com.h2.service.StockService;
import com.h2.service.StockTypeService;
import com.h2.service.StockUnitService;
import com.h2.service.TraderInvService;
import com.h2.service.UserService;
import com.h2.service.VouStatusService;
import com.inventory.model.AppUser;
import com.inventory.model.Category;
import com.inventory.model.CategoryKey;
import com.inventory.model.Location;
import com.inventory.model.LocationKey;
import com.inventory.model.MachineInfo;
import com.inventory.model.SaleHis;
import com.inventory.model.SaleMan;
import com.inventory.model.SaleManKey;
import com.inventory.model.Stock;
import com.inventory.model.StockBrand;
import com.inventory.model.StockBrandKey;
import com.inventory.model.StockKey;
import com.inventory.model.StockType;
import com.inventory.model.StockTypeKey;
import com.inventory.model.StockUnit;
import com.inventory.model.StockUnitKey;
import com.inventory.model.Trader;
import com.inventory.model.TraderKey;
import com.inventory.model.VRoleMenu;
import com.inventory.model.VouStatus;
import com.inventory.model.VouStatusKey;
import com.user.model.VRoleCompany;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 *
 * @author Lenovo
 */
@Component
public class H2Repo {

    @Autowired
    private LocationService locationService;
    @Autowired
    private StockUnitService stockUnitService;
    @Autowired
    private SaleManService saleManService;
    @Autowired
    private StockService stockService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private StockTypeService typeService;
    @Autowired
    private TraderInvService traderInvService;
    @Autowired
    private VouStatusService vouStatusService;
    @Autowired
    private SaleHisService saleHisService;
    @Autowired
    private UserService userService;
    @Autowired
    private MachineInfoService machineInfoService;
    @Autowired
    private PrivilegeCompanyService pcService;
    @Autowired
    private MenuService menuService;

    public Mono<List<Location>> getLocation() {
        return Mono.justOrEmpty(locationService.findAll(Global.compCode));
    }

    public Mono<Location> find(LocationKey key) {
        return Mono.justOrEmpty(locationService.find(key));
    }

    public Mono<List<StockUnit>> getStockUnit() {
        return Mono.justOrEmpty(stockUnitService.findAll(Global.compCode));
    }

    public Mono<StockUnit> find(StockUnitKey key) {
        return Mono.justOrEmpty(stockUnitService.find(key));
    }

    public Mono<List<SaleMan>> getSaleMan() {
        return Mono.justOrEmpty(saleManService.findAll(Global.compCode));
    }

    public Mono<SaleMan> find(SaleManKey key) {
        return Mono.justOrEmpty(saleManService.find(key));
    }

    public Mono<Stock> find(StockKey key) {
        return Mono.justOrEmpty(stockService.find(key));
    }

    public Mono<List<Stock>> getStock(String str) {
        return Mono.justOrEmpty(stockService.getStock(str, Global.compCode, Global.deptId));
    }

    public Mono<List<Category>> getCategory() {
        return Mono.justOrEmpty(categoryService.findAll(Global.compCode));
    }

    public Mono<Category> find(CategoryKey key) {
        return Mono.justOrEmpty(categoryService.find(key));
    }

    public Mono<List<StockBrand>> getBrand() {
        return Mono.justOrEmpty(brandService.findAll(Global.compCode));
    }

    public Mono<StockBrand> find(StockBrandKey key) {
        return Mono.justOrEmpty(brandService.find(key));
    }

    public Mono<List<StockType>> getStockType() {
        return Mono.justOrEmpty(typeService.findAll(Global.compCode));
    }

    public Mono<StockType> find(StockTypeKey key) {
        return Mono.justOrEmpty(typeService.find(key));
    }

    public Mono<List<Trader>> getTrader() {
        return Mono.justOrEmpty(traderInvService.findAll(Global.compCode));
    }

    public Mono<Trader> find(TraderKey key) {
        return Mono.justOrEmpty(traderInvService.find(key));
    }

    public Mono<List<VouStatus>> getVouStatus() {
        return Mono.justOrEmpty(vouStatusService.findAll(Global.compCode));
    }

    public Mono<VouStatus> find(VouStatusKey key) {
        return Mono.justOrEmpty(vouStatusService.find(key));
    }

    public Mono<SaleHis> save(SaleHis sh) {
        return Mono.justOrEmpty(saleHisService.save(sh));
    }
    
    public Mono<AppUser> login(String userName, String password) {
        return Mono.justOrEmpty(userService.login(userName, password));
    }
    
    public Mono<MachineInfo> getMachineInfo(String machineName) {
        return Mono.justOrEmpty(machineInfoService.getMachineInfo(machineName));
    }
    
    public Mono<List<AppUser>> getAppUser() {
        return Mono.justOrEmpty(userService.findAll());
    }
    
    public Mono<List<VRoleCompany>> getPrivilegeCompany(String roleCode) {
        return Mono.justOrEmpty(pcService.getPrivilegeCompany(roleCode));
    }
    
    public Mono<List<VRoleMenu>> getMenuTree(String roleCode, String compCode) {
        List<VRoleMenu> menus = menuService.getMenuTree(roleCode, compCode);
        menus.removeIf(m -> !m.isAllow());
        return Mono.justOrEmpty(menus);
    }
}

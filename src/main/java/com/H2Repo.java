/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.acc.model.BusinessType;
import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import com.acc.model.DepartmentA;
import com.acc.model.DepartmentAKey;
import com.acc.model.Gl;
import com.acc.model.TraderA;
import com.acc.model.TraderAKey;
import com.common.Global;
import com.h2.service.BrandService;
import com.h2.service.BusinessTypeService;
import com.h2.service.COAService;
import com.h2.service.CategoryService;
import com.h2.service.CompanyInfoService;
import com.h2.service.CurrencyService;
import com.h2.service.DepartmentUserService;
import com.h2.service.DepartmentAccService;
import com.h2.service.ExchangeRateService;
import com.h2.service.LocationService;
import com.h2.service.MacPropertyService;
import com.h2.service.MachineInfoService;
import com.h2.service.MenuService;
import com.h2.service.PrivilegeCompanyService;
import com.h2.service.ProjectService;
import com.h2.service.PurHisService;
import com.h2.service.RetInService;
import com.h2.service.RetOutService;
import com.h2.service.RolePropertyService;
import com.h2.service.RoleService;
import com.h2.service.SaleHisService;
import com.h2.service.SaleManService;
import com.h2.service.StockService;
import com.h2.service.StockTypeService;
import com.h2.service.StockUnitService;
import com.h2.service.SystemPropertyService;
import com.h2.service.TraderAService;
import com.h2.service.TraderInvService;
import com.h2.service.UserService;
import com.h2.service.VRoleCompanyService;
import com.h2.service.VRoleMenuService;
import com.h2.service.VouStatusService;
import com.inventory.model.AppRole;
import com.inventory.model.AppUser;
import com.inventory.model.Category;
import com.inventory.model.CategoryKey;
import com.inventory.model.Location;
import com.inventory.model.LocationKey;
import com.inventory.model.MachineInfo;
import com.inventory.model.PurHis;
import com.inventory.model.RetInHis;
import com.inventory.model.RetOutHis;
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
import com.user.model.CompanyInfo;
import com.user.model.Currency;
import com.user.model.DepartmentUser;
import com.user.model.ExchangeRate;
import com.user.model.MachineProperty;
import com.user.model.Menu;
import com.user.model.PrivilegeCompany;
import com.user.model.Project;
import com.user.model.ProjectKey;
import com.user.model.RoleProperty;
import com.user.model.SysProperty;
import com.user.model.VRoleCompany;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
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
    private PurHisService purHisService;
    @Autowired
    private RetInService retInService;
    @Autowired
    private RetOutService retOutService;
    @Autowired
    private UserService userService;
    @Autowired
    private MachineInfoService machineInfoService;
    @Autowired
    private PrivilegeCompanyService pcService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private SystemPropertyService spService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private CompanyInfoService companyInfoService;
    @Autowired
    private BusinessTypeService businessTypeService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private RolePropertyService rolePropertyService;
    @Autowired
    private MacPropertyService macPropertyService;
    @Autowired
    private DepartmentAccService departmentAccService;
    @Autowired
    private TraderAService traderAccService;
    @Autowired
    private COAService coaService;
    @Autowired
    private VRoleCompanyService vRoleCompanyService;
    @Autowired
    private VRoleMenuService vRoleMenuService;
    @Autowired
    private DepartmentUserService deptUserService;

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

    public Mono<List<Trader>> searchTrader(String str, String type, String compCode, Integer deptId) {
        return Mono.justOrEmpty(traderInvService.searchTrader(str, type, compCode, deptId));
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

    public Mono<PurHis> save(PurHis purHis) {
        return Mono.justOrEmpty(purHisService.save(purHis));
    }

    public Mono<RetInHis> save(RetInHis rh) {
        return Mono.justOrEmpty(retInService.save(rh));
    }

    public Mono<RetOutHis> save(RetOutHis rh) {
        return Mono.justOrEmpty(retOutService.save(rh));
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
        return Mono.justOrEmpty(vRoleCompanyService.getPrivilegeCompany(roleCode));
    }

    public Mono<List<VRoleMenu>> getPRoleMenu(String roleCode, String compCode) {
        List<VRoleMenu> menus = getRoleMenuTree(roleCode, compCode);
        menus.removeIf(m -> !m.isAllow());
        return Mono.justOrEmpty(menus);
    }

    private List<VRoleMenu> getRoleMenuTree(String roleCode, String compCode) {
        List<VRoleMenu> roles = vRoleMenuService.getMenuChild(roleCode, "1", compCode);
        if (!roles.isEmpty()) {
            for (VRoleMenu role : roles) {
                getRoleMenuChild(role);
            }
        }
        return roles;
    }

    private void getRoleMenuChild(VRoleMenu parent) {
        List<VRoleMenu> roles = vRoleMenuService.getMenuChild(parent.getRoleCode(), parent.getMenuCode(), parent.getCompCode());
        parent.setChild(roles);
        if (!roles.isEmpty()) {
            for (VRoleMenu role : roles) {
                getRoleMenuChild(role);
            }
        }
    }

    public Mono<List<VRoleMenu>> getRoleMenu(String roleCode, String compCode) {
        List<VRoleMenu> roles = vRoleMenuService.getMenu(roleCode, "1", compCode);
        if (!roles.isEmpty()) {
            for (VRoleMenu role : roles) {
                getMenuChild(role);
            }
        }
        return Mono.justOrEmpty(roles);
    }

    private void getMenuChild(VRoleMenu parent) {
        List<VRoleMenu> roles = vRoleMenuService.getMenu(parent.getRoleCode(), parent.getMenuCode(), parent.getCompCode());
        parent.setChild(roles);
        if (!roles.isEmpty()) {
            for (VRoleMenu role : roles) {
                getMenuChild(role);
            }
        }
    }

    public Mono<List<VRoleMenu>> getReport(String roleCode, String menuClass, String compCode) {
        return Mono.justOrEmpty(vRoleMenuService.getReport(roleCode, menuClass, compCode));
    }

    public HashMap<String, String> getProperty(String compCode, String roleCode, Integer macId) {
        return userService.getProperty(compCode, roleCode, macId);
    }

    public Mono<List<SysProperty>> getSysProperty(String compCode) {
        return Mono.justOrEmpty(spService.getSystemProperty(compCode));
    }

    public Mono<List<AppRole>> getAppRole(String compCode) {
        return Mono.justOrEmpty(roleService.findAll(compCode));
    }

    public Mono<List<CompanyInfo>> getCompany(boolean active) {
        return Mono.justOrEmpty(companyInfoService.findAll(active));
    }

    public Mono<List<BusinessType>> getBusinessType() {
        return Mono.justOrEmpty(businessTypeService.findAll());
    }

    public Mono<List<Currency>> getCurrency() {
        return Mono.justOrEmpty(currencyService.findAll());
    }

    public Mono<List<MachineInfo>> getMacList() {
        return Mono.justOrEmpty(machineInfoService.findAll());
    }

    public Mono<List<Project>> getProject(String compCode) {
        return Mono.justOrEmpty(projectService.searchProject(compCode));
    }

    public Mono<Currency> findCurrency(String curCode) {
        return Mono.justOrEmpty(currencyService.findById(curCode));
    }

    public Mono<List<ExchangeRate>> search(String startDate, String endDate, String targetCur, String compCode) {
        return Mono.justOrEmpty(exchangeRateService.searchExchange(startDate, endDate, targetCur, compCode));
    }

    public Mono<List<Menu>> getMenuTree(String compCode) {
        return Mono.justOrEmpty(menuService.getMenuTree(compCode));
    }

    public Mono<List<PrivilegeCompany>> searchCompany(String roleCode) {
        return Mono.justOrEmpty(pcService.getPC(roleCode));
    }

    public List<RoleProperty> getRoleProperty(String roleCode) {
        return rolePropertyService.getRoleProperty(roleCode);
    }

    public List<MachineProperty> getMacProperty(Integer macId) {
        return macPropertyService.getMacProperty(macId);
    }

    public Mono<List<DepartmentA>> getDepartmentAccount() {
        return Mono.justOrEmpty(departmentAccService.findAll(Global.compCode));
    }

    public Mono<CompanyInfo> findCompany(String compCode) {
        return Mono.justOrEmpty(companyInfoService.findById(compCode));
    }

    public Mono<AppRole> finRole(String roleCode) {
        return Mono.justOrEmpty(roleService.findById(roleCode));
    }

    public Mono<DepartmentUser> findDepartment(Integer deptId) {
        return Mono.justOrEmpty(deptUserService.findById(deptId));
    }

    public Mono<List<DepartmentUser>> getDeparment() {
        return Mono.justOrEmpty(deptUserService.findAll());
    }

    public Mono<BusinessType> find(Integer id) {
        return Mono.justOrEmpty(businessTypeService.findById(id));
    }

    public Mono<List<Menu>> getMenuParent(String compCode) {
        return Mono.justOrEmpty(menuService.getMenuDynamic(compCode));
    }

    public Mono<Project> find(ProjectKey key) {
        return Mono.justOrEmpty(projectService.findById(key));
    }

    public Mono<List<Project>> searchProjectByCode(String code, String compCode) {
        return Mono.justOrEmpty(projectService.search(code, compCode));
    }

    public Mono<DepartmentA> find(DepartmentAKey key) {
        return Mono.justOrEmpty(departmentAccService.find(key));
    }

    public Flux<TraderA> getTraderAccount() {
        return Flux.fromIterable(traderAccService.findAll(Global.compCode));
    }

    public Flux<TraderA> findTraderAccount(String str) {
        return Flux.fromIterable(traderAccService.getTrader(str, Global.compCode));
    }

    public Flux<ChartOfAccount> getChartofAccount() {
        return Flux.fromIterable(coaService.findAll(Global.compCode));
    }

    public Mono<ChartOfAccount> find(COAKey key) {
        return Mono.justOrEmpty(coaService.findById(key));
    }

//    public Mono<Gl> save(Gl sh) {
//        return Mono.justOrEmpty(saleHisService.save(sh));
//    }
}

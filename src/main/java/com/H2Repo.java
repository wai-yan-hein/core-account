/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.acc.model.BusinessType;
import com.acc.model.COAKey;
import com.acc.model.ChartOfAccount;
import com.acc.model.DateModel;
import com.acc.model.DepartmentA;
import com.acc.model.DepartmentAKey;
import com.acc.model.TraderA;
import com.common.Global;
import com.common.ReportFilter;
import com.common.Util1;
import com.h2.dao.DateFilterRepo;
import com.h2.service.AccSettingService;
import com.h2.service.BrandService;
import com.h2.service.BusinessTypeService;
import com.h2.service.COAService;
import com.h2.service.CategoryService;
import com.h2.service.CompanyInfoService;
import com.h2.service.CurrencyService;
import com.h2.service.DateLockService;
import com.h2.service.DepartmentUserService;
import com.h2.service.DepartmentAccService;
import com.h2.service.ExchangeRateService;
import com.h2.service.JobService;
import com.h2.service.LabourGroupService;
import com.h2.service.LocationService;
import com.h2.service.MacPropertyService;
import com.h2.service.MachineInfoService;
import com.h2.service.MenuService;
import com.h2.service.OPHisService;
import com.h2.service.OrderDetailService;
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
import com.inventory.entity.AppRole;
import com.user.model.AppUser;
import com.inventory.entity.Category;
import com.inventory.entity.CategoryKey;
import com.inventory.entity.Location;
import com.inventory.entity.LocationKey;
import com.user.model.MachineInfo;
import com.inventory.entity.OrderHis;
import com.inventory.entity.PurHis;
import com.inventory.entity.RetInHis;
import com.inventory.entity.RetOutHis;
import com.inventory.entity.SaleHis;
import com.inventory.entity.SaleMan;
import com.inventory.entity.SaleManKey;
import com.inventory.entity.Stock;
import com.inventory.entity.StockBrand;
import com.inventory.entity.StockBrandKey;
import com.inventory.entity.StockKey;
import com.inventory.entity.StockType;
import com.inventory.entity.StockTypeKey;
import com.inventory.entity.StockUnit;
import com.inventory.entity.StockUnitKey;
import com.inventory.entity.Trader;
import com.inventory.entity.TraderKey;
import com.inventory.entity.VRoleMenu;
import com.inventory.entity.VouStatus;
import com.inventory.entity.VouStatusKey;
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
import com.user.model.CompanyInfo;
import com.h2.service.OrderHisService;
import com.h2.service.OrderStatusService;
import com.h2.service.OutputCostService;
import com.h2.service.PatternService;
import com.h2.service.PrivilegeMenuService;
import com.h2.service.ProcessHisDetailService;
import com.h2.service.ProcessHisService;
import com.h2.service.PurHisDetailService;
import com.h2.service.RegionService;
import com.h2.service.RelationService;
import com.h2.service.ReportService;
import com.h2.service.RetInDetailService;
import com.h2.service.RetOutDetailService;
import com.h2.service.SaleHisDetailService;
import com.h2.service.StockCriteriaService;
import com.h2.service.StockFormulaService;
import com.h2.service.StockInOutDetailService;
import com.h2.service.StockInOutService;
import com.h2.service.TransferHisDetailService;
import com.h2.service.TransferHisService;
import com.h2.service.WareHouseService;
import com.h2.service.WeightLossService;
import com.inventory.entity.AccSetting;
import com.inventory.entity.General;
import com.inventory.entity.GradeDetail;
import com.inventory.entity.GradeDetailKey;
import com.inventory.entity.Job;
import com.inventory.entity.JobKey;
import com.inventory.entity.LabourGroup;
import com.inventory.entity.LabourGroupKey;
import com.inventory.entity.OPHis;
import com.inventory.entity.OPHisKey;
import com.inventory.entity.OrderHisDetail;
import com.inventory.entity.OrderHisKey;
import com.inventory.entity.OrderStatus;
import com.inventory.entity.OrderStatusKey;
import com.inventory.entity.OutputCost;
import com.inventory.entity.OutputCostKey;
import com.inventory.entity.Pattern;
import com.inventory.entity.ProcessHis;
import com.inventory.entity.ProcessHisDetail;
import com.inventory.entity.ProcessHisKey;
import com.inventory.entity.SaleHisDetail;
import com.inventory.entity.SaleHisKey;
import com.inventory.entity.PurHisDetail;
import com.inventory.entity.PurHisKey;
import com.inventory.entity.Region;
import com.inventory.entity.RegionKey;
import com.inventory.entity.RelationKey;
import com.inventory.entity.RetInHisDetail;
import com.inventory.entity.RetInHisKey;
import com.inventory.entity.RetOutHisDetail;
import com.inventory.entity.RetOutHisKey;
import com.inventory.entity.StockCriteria;
import com.inventory.entity.StockFormula;
import com.inventory.entity.StockFormulaKey;
import com.inventory.entity.StockFormulaPrice;
import com.inventory.entity.StockFormulaPriceKey;
import com.inventory.entity.StockFormulaQty;
import com.inventory.entity.StockIOKey;
import com.inventory.entity.StockInOut;
import com.inventory.entity.StockInOutDetail;
import com.inventory.entity.TransferHis;
import com.inventory.entity.TransferHisDetail;
import com.inventory.entity.TransferHisKey;
import com.inventory.entity.UnitRelation;
import com.inventory.entity.VOrder;
import com.inventory.entity.VSale;
import com.inventory.entity.VPurchase;
import com.inventory.entity.VReturnIn;
import com.inventory.entity.VReturnOut;
import com.inventory.entity.VTransfer;
import com.inventory.entity.WareHouse;
import com.inventory.entity.WareHouseKey;
import com.inventory.entity.WeightLossHis;
import com.user.model.DateLock;
import com.user.model.DepartmentKey;
import com.user.model.MenuKey;
import com.user.model.PrivilegeMenu;
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
    private RegionService regionService;
    @Autowired
    private RelationService relationService;
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
    private OrderStatusService orderStatusService;
    @Autowired
    private SaleHisService saleHisService;
    @Autowired
    private OrderHisService orderHisService;
    @Autowired
    private PurHisService purHisService;
    @Autowired
    private PurHisDetailService purDetailService;
    @Autowired
    private RetInService retInService;
    @Autowired
    private RetInDetailService retInDetailService;
    @Autowired
    private RetOutService retOutService;
    @Autowired
    private RetOutDetailService retOutDetailService;
    @Autowired
    private StockInOutService stockInOutService;
    @Autowired
    private StockInOutDetailService stkIODetailService;
    @Autowired
    private TransferHisService transferHisService;
    @Autowired
    private OPHisService opHisService;
    @Autowired
    private UserService userService;
    @Autowired
    private MachineInfoService machineInfoService;
    @Autowired
    private PrivilegeCompanyService pcService;
    @Autowired
    private PrivilegeMenuService pmService;
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
    @Autowired
    private ProcessHisService processHisService;
    @Autowired
    private WeightLossService weightLossService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private SaleHisDetailService saleHisDetailService;
    @Autowired
    private TransferHisDetailService transferHisDetailService;
    @Autowired
    private ProcessHisDetailService processHIsDetailService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private DateFilterRepo dateFilterRepo;
    @Autowired
    private DateLockService dateLockService;
    @Autowired
    private StockFormulaService stockFormulaService;
    @Autowired
    private StockCriteriaService stockCriteriaService;
    @Autowired
    private LabourGroupService labourGroupService;
    @Autowired
    private JobService jobService;
    @Autowired
    private PatternService patternService;
    @Autowired
    private OutputCostService outputCostService;
    @Autowired
    private AccSettingService accSettingService;
    @Autowired
    private WareHouseService wareHouseService;
//    @Autowired
//    private Langua

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
        return Mono.justOrEmpty(stockService.getStock(str, Global.compCode, 0));
    }

    public Mono<List<Stock>> getStock(boolean active) {
        List<Stock> listB = active ? stockService.findActiveStock(Global.compCode) : stockService.findAll(Global.compCode);
        return Mono.justOrEmpty(listB);
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

    public Mono<List<StockFormula>> getStockFormula(String compCode) {
        return Mono.justOrEmpty(stockFormulaService.getFormula(compCode));
    }

    public Mono<StockFormula> findStockFormula(StockFormulaKey key) {
        return Mono.justOrEmpty(stockFormulaService.find(key));
    }

    public Mono<List<VouStatus>> getVouStatus() {
        return Mono.justOrEmpty(vouStatusService.findAll(Global.compCode));
    }

    public Mono<VouStatus> find(VouStatusKey key) {
        return Mono.justOrEmpty(vouStatusService.find(key));
    }

    public Mono<List<OrderStatus>> getOrderStatus() {
        return Mono.justOrEmpty(orderStatusService.findAll(Global.compCode));
    }

    public Mono<OrderStatus> find(OrderStatusKey key) {
        return Mono.justOrEmpty(orderStatusService.find(key));
    }

    public Mono<List<StockInOutDetail>> searchStkIODetail(String vouNo, String compCode, Integer deptId) {
        return Mono.justOrEmpty(stkIODetailService.search(vouNo, compCode, deptId));
    }

    public Mono<StockInOut> findStkIO(StockIOKey key) {
        return Mono.justOrEmpty(stockInOutService.findById(key));
    }

    public Mono<OPHis> findOpening(OPHisKey key) {
        return Mono.justOrEmpty(opHisService.findByCode(key));
    }

    public Mono<SaleHis> save(SaleHis sh) {
        return Mono.justOrEmpty(saleHisService.save(sh));
    }

//    public Mono<Language> save(Language sh) {
//        return Mono.justOrEmpty(saleHisService.save(sh));
//    } 
    public Mono<Job> save(Job sh) {
        return Mono.justOrEmpty(jobService.save(sh));
    }

    public Mono<Pattern> save(Pattern sh) {
        return Mono.justOrEmpty(patternService.save(sh));
    }

    public Mono<OrderHis> save(OrderHis oh) {
        return Mono.justOrEmpty(orderHisService.save(oh));
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

    public Mono<StockInOut> save(StockInOut sio) {
        return Mono.justOrEmpty(stockInOutService.save(sio));
    }

    public Mono<TransferHis> save(TransferHis th) {
        return Mono.justOrEmpty(transferHisService.save(th));
    }

    public Mono<ProcessHis> save(ProcessHis ph) {
        return Mono.justOrEmpty(processHisService.save(ph));
    }

    public Mono<WeightLossHis> save(WeightLossHis wh) {
        return Mono.justOrEmpty(weightLossService.save(wh));
    }

    public AppUser save(AppUser user) {
        return userService.save(user);
    }

    public Currency save(Currency currency) {
        return currencyService.save(currency);
    }

    public CompanyInfo save(CompanyInfo info) {
        return companyInfoService.save(info);
    }

    public AppRole save(AppRole role) {
        return roleService.save(role);
    }

    public RoleProperty save(RoleProperty rp) {
        return rolePropertyService.save(rp);
    }

    public PrivilegeCompany save(PrivilegeCompany pc) {
        return pcService.save(pc);
    }

    public PrivilegeMenu save(PrivilegeMenu pc) {
        return pmService.save(pc);
    }

    public DepartmentUser save(DepartmentUser du) {
        return deptUserService.save(du);
    }

    public SysProperty save(SysProperty sp) {
        return spService.save(sp);
    }

    public MachineProperty save(MachineProperty rp) {
        return macPropertyService.save(rp);
    }

    public BusinessType save(BusinessType rp) {
        return businessTypeService.save(rp);
    }

    public Menu save(Menu obj) {
        return menuService.save(obj);
    }

    public Project save(Project obj) {
        return projectService.save(obj);
    }

    public ExchangeRate save(ExchangeRate r) {
        return exchangeRateService.save(r);
    }

    public Trader save(Trader obj) {
        return traderInvService.save(obj);
    }

    public Stock save(Stock obj) {
        return stockService.save(obj);
    }

    public Location save(Location obj) {
        return locationService.save(obj);
    }

    public Region save(Region obj) {
        return regionService.save(obj);
    }

    public SaleMan save(SaleMan obj) {
        return saleManService.save(obj);
    }

    public StockBrand save(StockBrand obj) {
        return brandService.save(obj);
    }

    public Category save(Category obj) {
        return categoryService.save(obj);
    }

    public StockType save(StockType obj) {
        return typeService.save(obj);
    }

    public StockUnit save(StockUnit obj) {
        return stockUnitService.save(obj);
    }

    public VouStatus save(VouStatus obj) {
        return vouStatusService.save(obj);
    }

    public OrderStatus save(OrderStatus obj) {
        return orderStatusService.save(obj);
    }

    public UnitRelation save(UnitRelation obj) {
        return relationService.save(obj);
    }

    public StockFormula save(StockFormula obj) {
        return stockFormulaService.save(obj);
    }

    public StockFormulaPrice save(StockFormulaPrice obj) {
        return stockFormulaService.save(obj);
    }

    public StockCriteria save(StockCriteria obj) {
        return stockCriteriaService.save(obj);
    }

    public Mono<General> getPurRecentPrice(String stockCode, String vouDate, String unit, String compCode, Integer deptId) {
        return Mono.justOrEmpty(reportService.getPurchaseRecentPrice(stockCode, vouDate, unit, compCode, deptId));
    }

    public Mono<General> getSmallQty(String stockCode, String unit, String compCode, Integer deptId) {
        return Mono.justOrEmpty(reportService.getSmallestQty(stockCode, unit, compCode, deptId));
    }

    public Mono<AppUser> login(String userName, String password) {
        return Mono.justOrEmpty(userService.login(userName, password));
    }

    public MachineInfo getMachineInfo(String machineName) {
        return machineInfoService.getMachineInfo(machineName);
    }

    public Mono<List<AppUser>> getAppUser() {
        return Mono.justOrEmpty(userService.findAll());
    }

    public Mono<List<CompanyInfo>> getPrivilegeCompany(String roleCode) {
        return Mono.justOrEmpty(vRoleCompanyService.getPrivilegeCompany(roleCode));
    }

    public Mono<List<VRoleMenu>> getPrivilegeMenu(String roleCode, String compCode) {
        List<VRoleMenu> menus = getRoleMenuTree(roleCode, compCode, true);
        return Mono.justOrEmpty(menus);
    }

    private List<VRoleMenu> getRoleMenuTree(String roleCode, String compCode, boolean privilege) {
        List<VRoleMenu> roles = vRoleMenuService.getMenu(roleCode, "#", compCode, privilege);
        if (!roles.isEmpty()) {
            for (VRoleMenu role : roles) {
                getRoleMenuChild(role, false);
            }
        }
        return roles;
    }

    private void getRoleMenuChild(VRoleMenu parent, boolean privilege) {
        List<VRoleMenu> roles = vRoleMenuService.getMenu(parent.getRoleCode(), parent.getMenuCode(), parent.getCompCode(), privilege);
        parent.setChild(roles);
        if (!roles.isEmpty()) {
            for (VRoleMenu role : roles) {
                getRoleMenuChild(role, privilege);
            }
        }
    }

    public Mono<List<VRoleMenu>> getRoleMenu(String roleCode, String compCode) {
        List<VRoleMenu> roles = vRoleMenuService.getMenu(roleCode, "#", compCode, false);
        if (!roles.isEmpty()) {
            for (VRoleMenu role : roles) {
                getMenuChild(role, false);
            }
        }
        return Mono.justOrEmpty(roles);
    }

    private void getMenuChild(VRoleMenu parent, boolean privilege) {
        List<VRoleMenu> roles = vRoleMenuService.getMenu(parent.getRoleCode(), parent.getMenuCode(), parent.getCompCode(), privilege);
        parent.setChild(roles);
        if (!roles.isEmpty()) {
            for (VRoleMenu role : roles) {
                getMenuChild(role, privilege);
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

    public Mono<List<DateLock>> getDateLock() {
        return Flux.fromIterable(dateLockService.findAll(Global.compCode)).map(dateLock -> {
            dateLock.setCreatedDateTime(Util1.toZonedDateTime(dateLock.getCreatedDate()));
            dateLock.setUpdatedDateTime(Util1.toZonedDateTime(dateLock.getUpdatedDate()));
            return dateLock;
        }).collectList();
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
    public Mono<MachineInfo> findMachine(Integer macId) {
        return Mono.justOrEmpty(machineInfoService.find(macId));
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

    public List<RoleProperty> getRoleProperty(String roleCode, String compCode) {
        return rolePropertyService.getRoleProperty(roleCode, compCode);
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

    public Mono<DepartmentUser> findDepartment(DepartmentKey key) {
        return Mono.justOrEmpty(deptUserService.findById(key));
    }

    public Mono<List<DepartmentUser>> getDeparment(Boolean active, String compCode) {
        return Mono.justOrEmpty(deptUserService.findAll(active, compCode));
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

    public Mono<List<TraderA>> getTraderAccount() {
        return Mono.justOrEmpty(traderAccService.findAll(Global.compCode));
    }

    public Mono<List<TraderA>> getTrader(String str) {
        return Mono.justOrEmpty(traderAccService.getTrader(str, Global.compCode));
    }

    public Mono<List<Trader>> getCustomer() {
        return Mono.justOrEmpty(traderInvService.getTrader(Global.compCode, "CUS"));
    }

    public Mono<List<Trader>> getSupplier() {
        return Mono.justOrEmpty(traderInvService.getTrader(Global.compCode, "SUP"));
    }

    public Flux<ChartOfAccount> getChartofAccount() {
        return Flux.fromIterable(coaService.getCOA(Global.compCode));
    }

    public Flux<ChartOfAccount> getCOA(int coaLevel) {
        return Flux.fromIterable(coaService.getCOA(coaLevel, Global.compCode));
    }

    public Mono<ChartOfAccount> find(COAKey key) {
        return Mono.justOrEmpty(coaService.findById(key));
    }

    public Mono<List<ChartOfAccount>> searchCOA(String str, int level) {
        return Mono.justOrEmpty(coaService.searchCOA(str, level, Global.compCode));
    }

    public Mono<List<ChartOfAccount>> getCOATree() {
        return Mono.justOrEmpty(coaService.getCOATree(Global.compCode));
    }

    public Flux<ChartOfAccount> getTraderCOA() {
        return Flux.fromIterable(coaService.getTraderCOA(Global.compCode));
    }

    public Mono<List<ChartOfAccount>> getCOA3(String headCode) {
        return Mono.justOrEmpty(coaService.getCOA(headCode, Global.compCode));
    }

    public Mono<List<ChartOfAccount>> getCOAByGroup(String groupCode) {
        return Mono.justOrEmpty(coaService.getCOAByGroup(groupCode, Global.compCode));
    }

    public Mono<List<ChartOfAccount>> getCOAByHead(String headCode) {
        return Mono.justOrEmpty(coaService.getCOAByHead(headCode, Global.compCode));
    }

    public Flux<ChartOfAccount> getCOAChild(String coaCode) {
        return Flux.fromIterable(coaService.getCOAChild(coaCode, Global.compCode));
    }

    public List<DateModel> getDate() {
        return dateFilterRepo.findAll();
    }

    public ChartOfAccount save(ChartOfAccount obj) {
        return coaService.save(obj);
    }

    public DepartmentA save(DepartmentA obj) {
        return departmentAccService.save(obj);
    }

    public TraderA save(TraderA obj) {
        return traderAccService.save(obj);
    }

    public MachineInfo save(MachineInfo obj) {
        return machineInfoService.save(obj);
    }

    public DateLock save(DateLock obj) {
        return dateLockService.save(obj);
    }

    public Mono<List<VPurchase>> searchPurchaseVoucher(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getTraderCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        return Mono.justOrEmpty(reportService.getPurchaseHistory(fromDate, toDate, cusCode, vouNo, userCode, remark, locCode, compCode, deptId, deleted, projectNo, curCode));
    }

    public Mono<PurHis> findPurchase(PurHisKey key) {
        return Mono.justOrEmpty(purHisService.findById(key));
    }

    public Mono<List<PurHisDetail>> searchPurchaseDetail(String vouNo, Integer depId) {
        return Mono.justOrEmpty(purDetailService.search(vouNo, Global.compCode, depId));
    }

    public Mono<List<VReturnIn>> searchReturnInVoucher(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getTraderCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        return Mono.justOrEmpty(reportService.getReturnInHistory(fromDate, toDate, cusCode, vouNo, userCode, remark, locCode, compCode, deptId, deleted, projectNo, curCode));
    }

    public Mono<RetInHis> findRetInHis(RetInHisKey key) {
        return Mono.justOrEmpty(retInService.findById(key));
    }

    public Mono<List<RetInHisDetail>> searchReturnInDetail(String vouNo, Integer depId) {
        return Mono.justOrEmpty(retInDetailService.search(vouNo, Global.compCode, depId));
    }

    public Mono<List<VReturnOut>> searchReturnOutVoucher(ReportFilter filter) {
        String fromDate = Util1.isNull(filter.getFromDate(), "-");
        String toDate = Util1.isNull(filter.getToDate(), "-");
        String vouNo = Util1.isNull(filter.getVouNo(), "-");
        String userCode = Util1.isNull(filter.getUserCode(), "-");
        String cusCode = Util1.isNull(filter.getTraderCode(), "-");
        String remark = Util1.isNull(filter.getRemark(), "-");
        String locCode = Util1.isNull(filter.getLocCode(), "-");
        String compCode = filter.getCompCode();
        Integer deptId = filter.getDeptId();
        String deleted = String.valueOf(filter.isDeleted());
        String projectNo = Util1.isAll(filter.getProjectNo());
        String curCode = Util1.isAll(filter.getCurCode());
        return Mono.justOrEmpty(reportService.getReturnOutHistory(fromDate, toDate, cusCode, vouNo, userCode, remark, locCode, compCode, deptId, deleted, projectNo, curCode));
    }

    public Mono<RetOutHis> findRetOutHis(RetOutHisKey key) {
        return Mono.justOrEmpty(retOutService.findById(key));
    }

    public Mono<List<RetOutHisDetail>> searchReturnOutDetail(String vouNo, Integer depId) {
        return Mono.justOrEmpty(retOutDetailService.search(vouNo, Global.compCode, depId));
    }

    public Mono<List<VSale>> getSaleHistory(ReportFilter filter) {
        return Mono.justOrEmpty(saleHisService.getSale(filter));
    }

    public Mono<List<VOrder>> getOrderHistory(ReportFilter filter) {
        return Mono.justOrEmpty(orderHisService.getOrder(filter));
    }

    public Mono<List<VTransfer>> getTransferHistory(ReportFilter filter) {
        return Mono.justOrEmpty(transferHisService.getTransfer(filter));
    }

    public Mono<List<ProcessHis>> getProcessHistory(ReportFilter filter) {
        return Mono.justOrEmpty(processHisService.getProcess(filter));
    }

    public Mono<SaleHis> findSale(SaleHisKey key) {
        return Mono.justOrEmpty(saleHisService.find(key));
    }

    public Mono<OrderHis> findOrder(OrderHisKey key) {
        return Mono.justOrEmpty(orderHisService.findById(key));
    }

    public Mono<TransferHis> findTransfer(TransferHisKey key) {
        return Mono.justOrEmpty(transferHisService.findById(key));
    }

    public Mono<ProcessHis> findProcess(ProcessHisKey key) {
        return Mono.justOrEmpty(processHisService.findById(key));
    }

    public Mono<StockUnit> findUnit(StockUnitKey key) {
        return Mono.justOrEmpty(stockUnitService.find(key));
    }

    public Mono<List<SaleHisDetail>> getSaleDetail(String vouNo, int deptId) {
        return Mono.justOrEmpty(saleHisDetailService.searchDetail(vouNo, Global.compCode, deptId));
    }

    public Mono<List<OrderHisDetail>> getOrderDetail(String vouNo, int deptId) {
        return Mono.justOrEmpty(orderDetailService.searchDetail(vouNo, Global.compCode, deptId));
    }

    public Mono<List<TransferHisDetail>> getTransferDetail(String vouNo, int deptId) {
        return Mono.justOrEmpty(transferHisDetailService.searchDetail(vouNo, Global.compCode, deptId));
    }

    public Mono<List<ProcessHisDetail>> getProcessDetail(String vouNo, int deptId) {
        return Mono.justOrEmpty(processHIsDetailService.searchDeatil(vouNo, Global.compCode, deptId));
    }

    public Mono<Boolean> deleteSale(SaleHisKey key) {
        saleHisService.delete(key);
        return Mono.justOrEmpty(true);
    }

    public boolean delete(MenuKey obj) {
        return menuService.delete(obj);
    }

    public boolean delete(GradeDetailKey key) {
        return stockFormulaService.delete(key);
    }

    public Boolean delete(StockFormulaPriceKey key) {
        return stockFormulaService.delete(key);
    }

    public Mono<Boolean> restoreSale(SaleHisKey key) {
        saleHisService.restore(key);
        return Mono.justOrEmpty(true);
    }

    public List<VSale> getSaleReport(String vouNo) {
        return saleHisService.getSaleReport(vouNo, Global.compCode, Global.deptId);
    }

    public Mono<List<Stock>> searchStock(ReportFilter filter) {
        String stockCode = Util1.isAll(filter.getStockCode());
        String typCode = Util1.isAll(filter.getStockTypeCode());
        String catCode = Util1.isAll(filter.getCatCode());
        String brandCode = Util1.isAll(filter.getBrandCode());
        Integer deptId = filter.getDeptId();
        String compCode = filter.getCompCode();
        boolean active = filter.isActive();
        boolean deleted = filter.isDeleted();
        return Mono.justOrEmpty(stockService.search(stockCode, typCode, catCode, brandCode, compCode, deptId, active, deleted));
    }

    public Mono<Region> find(RegionKey key) {
        return Mono.justOrEmpty(regionService.findByCode(key));
    }

    public Mono<UnitRelation> find(RelationKey key) {
        return Mono.justOrEmpty(relationService.findByKey(key));
    }

    public LabourGroup save(LabourGroup t) {
        return labourGroupService.save(t);
    }

    public Mono<List<LabourGroup>> getLabourGroup() {
        return Mono.justOrEmpty(labourGroupService.findAll(Global.compCode));
    }

    public Mono<List<StockFormulaPrice>> getStockFormulaPrice(String formulaCode) {
        return Mono.justOrEmpty(stockFormulaService.getStockFormulaPrice(formulaCode, Global.compCode));
    }

    public Mono<List<StockFormulaQty>> getStockFormulaQty(String formulaCode) {
        return Mono.justOrEmpty(stockFormulaService.getStockFormulaQty(formulaCode, Global.compCode));
    }

    public Mono<List<GradeDetail>> getGradeDetail(String formulaCode, String criteriaCode) {
        return Mono.justOrEmpty(stockFormulaService.getGradeDetail(formulaCode, criteriaCode, Global.compCode));
    }

    public StockFormulaQty save(StockFormulaQty t) {
        return stockFormulaService.save(t);
    }

    public GradeDetail save(GradeDetail t) {
        return stockFormulaService.save(t);
    }

    public Mono<List<Job>> getJob(ReportFilter ReportFilter) {
        return Mono.justOrEmpty(jobService.findAll(ReportFilter));
    }

    public Mono<Job> find(JobKey key) {
        return Mono.justOrEmpty(jobService.findById(key));
    }

    public OutputCost save(OutputCost s) {
        return outputCostService.save(s);
    }

    public Mono<List<OutputCost>> getOutputCost() {
        return Mono.justOrEmpty(outputCostService.findAll(Global.compCode));
    }

    public boolean delete(OutputCostKey key) {
        return outputCostService.delete(key);
    }

    public AccSetting save(AccSetting acc) {
        return accSettingService.save(acc);
    }

    public Mono<List<AccSetting>> getAccSetting() {
        return Mono.justOrEmpty(accSettingService.findAll(Global.compCode));
    }

    public Mono<List<WareHouse>> getWarehouse() {
        return Mono.justOrEmpty(wareHouseService.findAll(Global.compCode));
    }

    public WareHouse save(WareHouse w) {
        return wareHouseService.save(w);
    }

    public Mono<WareHouse> find(WareHouseKey key) {
        return Mono.justOrEmpty(wareHouseService.findById(key));
    }

    public Mono<LabourGroup> find(LabourGroupKey key) {
        return Mono.justOrEmpty(labourGroupService.findById(key));
    }
}

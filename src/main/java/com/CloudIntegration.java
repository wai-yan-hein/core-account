/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template */package com;

import com.repo.AccountRepo;
import com.common.DateLockUtil;
import com.common.Global;
import com.common.SelectionObserver;
import com.h2.service.AccSettingService;
import com.h2.service.BrandService;
import com.h2.service.BusinessTypeService;
import com.h2.service.COAService;
import com.h2.service.CategoryService;
import com.h2.service.CompanyInfoService;
import com.h2.service.CurrencyService;
import com.h2.service.DateLockService;
import com.h2.service.DepartmentAccService;
import com.h2.service.DepartmentUserService;
import com.h2.service.LocationService;
import com.h2.service.PriceOptionService;
import com.h2.service.RelationService;
import com.h2.service.SaleHisService;
import com.h2.service.SaleManService;
import com.h2.service.ExchangeRateService;
import com.h2.service.JobService;
import com.h2.service.LabourGroupService;
import com.h2.service.MacPropertyService;
import com.h2.service.MachineInfoService;
import com.h2.service.MenuService;
import com.h2.service.OrderHisService;
import com.h2.service.OrderStatusService;
import com.h2.service.OutputCostService;
import com.h2.service.PatternService;
import com.h2.service.PrivilegeCompanyService;
import com.h2.service.PrivilegeMenuService;
import com.h2.service.ProcessHisService;
import com.h2.service.ProjectService;
import com.h2.service.RegionService;
import com.h2.service.RolePropertyService;
import com.h2.service.RoleService;
import com.h2.service.StockCriteriaService;
import com.h2.service.StockFormulaService;
import com.h2.service.StockService;
import com.h2.service.StockTypeService;
import com.h2.service.StockUnitService;
import com.h2.service.SystemPropertyService;
import com.h2.service.TraderAService;
import com.h2.service.TraderInvService;
import com.h2.service.TransferHisService;
import com.h2.service.UserService;
import com.h2.service.VouStatusService;
import com.h2.service.WareHouseService;
import com.h2.service.WeightLossService;
import com.inventory.entity.OrderHis;
import com.inventory.entity.ProcessHis;
import com.inventory.entity.SaleHis;
import com.inventory.entity.TransferHis;
import com.inventory.entity.WeightLossHis;
import com.repo.InventoryRepo;
import com.repo.UserRepo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * * * @author Lenovo
 */
@Component
@Slf4j
public class CloudIntegration {

    @Autowired
    private boolean localDatabase;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private InventoryRepo inventoryRepo;
    @Autowired
    private AccountRepo accountRepo;
    @Autowired
    private RegionService regionService;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockTypeService stockTypeService;
    @Autowired
    private UserService userService;
    @Autowired
    private BusinessTypeService businessTypeService;
    @Autowired
    private CompanyInfoService companyInfoService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private DepartmentUserService departmentService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private StockUnitService stockUnitService;
    @Autowired
    private RelationService relationService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private SaleManService saleManService;
    @Autowired
    private TraderInvService traderInvService;
    @Autowired
    private VouStatusService vouStatusService;
    @Autowired
    private OrderStatusService orderStatusService;
    @Autowired
    private PriceOptionService priceOptionService;
    @Autowired
    private SaleHisService saleHisService;
    @Autowired
    private ExchangeRateService exchangeRateService;
    @Autowired
    private MachineInfoService machineInfoService;
    @Autowired
    private MacPropertyService macPropertyService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private PrivilegeCompanyService pcService;
    @Autowired
    private PrivilegeMenuService pmService;
    @Autowired
    private ProjectService pService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RolePropertyService rpService;
    @Autowired
    private SystemPropertyService sysPropertyService;
    @Autowired
    private DateLockService dateLockService;
    @Autowired
    private COAService coaService;
    @Autowired
    private TraderAService traderService;
    @Autowired
    private DepartmentAccService departmentAService;
    @Autowired
    OrderHisService orderHisService;
    private TransferHisService transferHisService;
    @Autowired
    private WeightLossService weightLossService;
    @Autowired
    private ProcessHisService processHisService;
    @Autowired
    private DateLockUtil dateLockUtil;
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
    private WareHouseService warehouseService;
    private SelectionObserver observer;

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void startDownload() {
        if (localDatabase) {
            observer.selected("download", "download start.");
            downloadUser();
            downloadInventory();
            downloadAccount();
            observer.selected("download", "download end.");
        }
    }

    public void startUpload() {
        if (localDatabase) {
            uploadInvData();
        }
    }

    public void uploadInvData() {
        uploadOrder();
        uploadTransfer();
        uploadWeightLoss();
        uploadManufacture();
    }

    public int uploadSaleCount() {
        return saleHisService.getUploadCount();
    }

    public String uploadSale() throws Exception {
        List<SaleHis> list = saleHisService.unUploadVoucher(Global.compCode);
        if (!list.isEmpty()) {
            list.forEach((h) -> {
                SaleHis sh = inventoryRepo.uploadSale(h).block();
                saleHisService.updateACK(sh.getKey());
            });
        }
        return "Upload Success.";
    }

    public void uploadOrder() {
        List<OrderHis> list = orderHisService.unUploadVoucher(Global.compCode);
        if (!list.isEmpty()) {
            list.forEach((l) -> {
                inventoryRepo.uploadOrder(l).subscribe((r) -> {
                    r.setIntgUpdStatus("ACK");
                    orderHisService.updateACK(r.getKey());
                });
            });
        }
    }


    public void uploadTransfer() {
        List<TransferHis> list = transferHisService.unUpload(Global.compCode);
        if (!list.isEmpty()) {
            list.forEach((l) -> {
                inventoryRepo.uploadTransfer(l).subscribe((r) -> {
                    r.setIntgUpdStatus("ACK");
                    transferHisService.updateACK(r.getKey());
                });
            });
        }
    }

    public void uploadWeightLoss() {
        List<WeightLossHis> list = weightLossService.unUpload(Global.compCode);
        if (!list.isEmpty()) {
            list.forEach((l) -> {
                inventoryRepo.uploadWeightLoss(l).subscribe((r) -> {
                    r.setIntgUpdStatus("ACK");
                    weightLossService.updateACK(r.getKey());
                });
            });
        }
    }

    public void uploadManufacture() {
        List<ProcessHis> list = processHisService.unUpload(Global.compCode);
        if (!list.isEmpty()) {
            list.forEach((l) -> {
                inventoryRepo.uploadProcess(l).subscribe((r) -> {
                    r.setIntgUpdStatus("ACK");
                    processHisService.updateACK(r.getKey());
                });
            });
        }
    }

    private void downloadUser() {
        downloadAppUser();
        downloadBusinessType();
        downloadCompanyInfo();
        downloadCurrency();
        downloadDepartment();
        downloadExchangeRate();
        downloadMachineInfo();
        downloadMacProperty();
        downloadMenu();
        downloadPC();
        downloadPM();
        downloadProject();
        downloadRole();
        downloadRoleProperty();
        downloadSystemProperty();
        downloadDateLock();
    }

    private void downloadAccount() {
        downloadDepartmentAccount();
        downloadTraderAccount();
        downloadChartofAccount();
    }

    public void downloadChartofAccount() {
        accountRepo.getUpdateChartOfAccountByDate(coaService.getMaxDate()).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadChartOfAccount list : " + t.size());
                t.forEach((coa) -> {
                    coaService.save(coa);
                });
                observer.selected("download", "downloadChartOfAccount done.");
            }
        }).subscribe();
    }

    public void downloadTraderAccount() {
        accountRepo.getUpdateTraderByDate(traderService.getMaxDate()).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadTrader list : " + t.size());
                t.forEach((tr) -> {
                    traderService.save(tr);
                });
            }
            observer.selected("download", "downloadTrader done.");
        }).subscribe();
    }

    public void downloadDepartmentAccount() {
        accountRepo.getUpdateDepartmentAByDate(departmentAService.getMaxDate()).doOnSuccess((d) -> {
            if (d != null) {
                observer.selected("download", "downloadDepartmentAccount list : " + d.size());
                d.forEach((da) -> {
                    departmentAService.save(da);
                });
            }
            observer.selected("download", "downloadDepartmentAccount done.");
        }).subscribe();
    }

    public void downloadAppUser() {
        userRepo.getAppUserByDate(userService.getMaxDate()).doOnSuccess((u) -> {
            if (u != null) {
                observer.selected("download", "downloadAppUser list : " + u.size());
                u.forEach((a) -> {
                    userService.save(a);
                });
                observer.selected("download", "downloadAppUser done.");
            }
        }).subscribe();
    }

    public void downloadBusinessType() {
        userRepo.getBusinessTypeByDate(businessTypeService.getMaxDate()).doOnSuccess((b) -> {
            if (b != null) {
                observer.selected("download", "downloadAppUser list : " + b.size());
                b.forEach((a) -> {
                    businessTypeService.save(a);
                });
                observer.selected("download", "downloadAppUser done.");
            }
        }).subscribe();
    }

    public void downloadCompanyInfo() {
        userRepo.getCompanyInfoByDate(companyInfoService.getMaxDate()).doOnSuccess((c) -> {
            if (c != null) {
                observer.selected("download", "downloadCompanyInfo list : " + c.size());
                c.forEach((a) -> {
                    companyInfoService.save(a);
                });
                observer.selected("download", "downloadCompanyInfo done.");
            }
        }).subscribe();
    }

    public void downloadCurrency() {
        userRepo.getCurrencyByDate(currencyService.getMaxDate()).doOnSuccess((c) -> {
            if (c != null) {
                observer.selected("download", "downloadCurrency list : " + c.size());
                c.forEach((a) -> {
                    currencyService.save(a);
                });
                observer.selected("download", "downloadCurrency done.");
            }
        }).subscribe();
    }

    public void downloadDepartment() {
        userRepo.getDepartmentByDate(departmentService.getMaxDate()).doOnSuccess((d) -> {
            if (d != null) {
                observer.selected("download", "downloadDepartment list : " + d.size());
                d.forEach((a) -> {
                    departmentService.save(a);
                });
                observer.selected("download", "downloadDepartment done.");
            }
        }).subscribe();
    }

    public void downloadExchangeRate() {
        userRepo.getExchangeRateByDate(exchangeRateService.getMaxDate()).doOnSuccess((ex) -> {
            if (ex != null) {
                observer.selected("download", "downloadExchangeRate list : " + ex.size());
                ex.forEach((a) -> {
                    exchangeRateService.save(a);
                });
                observer.selected("download", "downloadExchangeRate done.");
            }
        }).subscribe();
    }

    public void downloadMachineInfo() {
        userRepo.getMachineInfoByDate(machineInfoService.getMaxDate()).doOnSuccess((m) -> {
            if (m != null) {
                observer.selected("download", "downloadMachineInfo list : " + m.size());
                m.forEach((a) -> {
                    machineInfoService.save(a);
                });
                observer.selected("download", "downloadMachineInfo done.");
            }
        }).subscribe();
    }

    public void downloadMacProperty() {
        userRepo.getMacPropertyByDate(macPropertyService.getMaxDate()).doOnSuccess((m) -> {
            if (m != null) {
                observer.selected("download", "downloadMacProperty list : " + m.size());
                m.forEach((a) -> {
                    macPropertyService.save(a);
                });
                observer.selected("download", "downloadMacProperty done.");
            }
        }).subscribe();
    }

    public void downloadMenu() {
        userRepo.getMenuByDate(menuService.getMaxDate()).doOnSuccess((m) -> {
            if (m != null) {
                observer.selected("download", "downloadMenu list : " + m.size());
                m.forEach((a) -> {
                    menuService.save(a);
                });
                observer.selected("download", "downloadMenu done.");
            }
        }).subscribe();
    }

    public void downloadPC() {
        userRepo.getPCByDate(pcService.getMaxDate()).doOnSuccess((p) -> {
            if (p != null) {
                observer.selected("download", "downloadPC list : " + p.size());
                p.forEach((a) -> {
                    pcService.save(a);
                });
                observer.selected("download", "downloadPC done.");
            }
        }).subscribe();
    }

    public void downloadPM() {
        userRepo.getPMByDate(pmService.getMaxDate()).doOnSuccess((m) -> {
            if (m != null) {
                observer.selected("download", "downloadPM list : " + m.size());
                m.forEach((a) -> {
                    pmService.save(a);
                });
                observer.selected("download", "downloadPM done.");
            }
        }).subscribe();
    }

    public void downloadProject() {
        userRepo.getProjectByDate(pService.getMaxDate()).doOnSuccess((p) -> {
            if (p != null) {
                observer.selected("download", "downloadProject list : " + p.size());
                p.forEach((a) -> {
                    pService.save(a);
                });
                observer.selected("download", "downloadPM done.");
            }
        }).subscribe();
    }

    public void downloadRole() {
        userRepo.getRoleByDate(roleService.getMaxDate()).doOnSuccess((r) -> {
            if (r != null) {
                observer.selected("download", "downloadRole list : " + r.size());
                r.forEach((a) -> {
                    roleService.save(a);
                });
                observer.selected("download", "downloadRole done.");
            }
        }).subscribe();
    }

    public void downloadRoleProperty() {
        userRepo.getRolePropByDate(rpService.getMaxDate()).doOnSuccess((r) -> {
            if (r != null) {
                observer.selected("download", "downloadRoleProperty list : " + r.size());
                r.forEach((a) -> {
                    rpService.save(a);
                });
                observer.selected("download", "downloadRoleProperty done.");
            }
        }).subscribe();
    }

    public void downloadSystemProperty() {
        userRepo.getSystemPropertyByDate(sysPropertyService.getMaxDate()).doOnSuccess((r) -> {
            if (r != null) {
                observer.selected("download", "downloadSystemProperty list : " + r.size());
                r.forEach((a) -> {
                    sysPropertyService.save(a);
                });
                observer.selected("download", "downloadSystemProperty done.");
            }
        }).subscribe();
    }

    public void downloadDateLock() {
        userRepo.getDateLockByDate(dateLockService.getMaxDate()).doOnSuccess((r) -> {
            if (r != null) {
                observer.selected("download", "downloadDateLock list : " + r.size());
                r.forEach((a) -> {
                    dateLockService.save(a);
                });
                dateLockUtil.initLockDate();
                observer.selected("download", "downloadDateLock done.");
            }
        }).subscribe();
    }

    private void downloadInventory() {
        downloadPriceOption();
        downloadVouStatus();
        downloadOrderStatus();
        downloadInvTrader();
        downloadSaleMan();
        downloadLocation();
        downloadRelation();
        downloadUnit();
        downloadBrand();
        downloadStockType();
        downloadCategory();
        downloadStock();
        downloadStockFormula();
        downloadStockFormulaPrice();
        downloadStockFormulaQty();
        downloadGradeDetail();
        downloadStockCriteria();
        downloadLabourGroup();
        downloadJob();
        downloadPattern();
        downloadOuputStatus();
        downloadAccSettings();
        downloadWarehouse();
        downloadRegion();
    }

    public void downloadPriceOption() {
        inventoryRepo.getUpdatePriceOption(priceOptionService.getMaxDate()).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadPriceOption list : " + t.size());
                t.forEach((s) -> {
                    priceOptionService.save(s);
                });
                observer.selected("download", "downloadPriceOption done.");
            }
        }).subscribe();
    }

    public void downloadVouStatus() {
        inventoryRepo.getUpdateVouStatus(vouStatusService.getMaxDate()).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadVouStatus list : " + t.size());
                t.forEach((s) -> {
                    vouStatusService.save(s);
                });
                observer.selected("download", "downloadVouStatus done.");
            }
        }).subscribe();
    }

    public void downloadOrderStatus() {
        inventoryRepo.getUpdateOrderStatus(orderStatusService.getMaxDate()).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadOrderStatus list : " + t.size());
                t.forEach((s) -> {
                    orderStatusService.save(s);
                });
                observer.selected("download", "downloadOrderStatus done.");
            }
        }).subscribe();
    }

    public void downloadInvTrader() {
        inventoryRepo.getUpdateTrader(traderInvService.getMaxDate()).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadInvTrader list : " + t.size());
                t.forEach((s) -> {
                    traderInvService.save(s);
                });
                observer.selected("download", "downloadInvTrader done.");
            }
        }).subscribe();
    }

    public void downloadSaleMan() {
        inventoryRepo.getUpdateSaleMan(saleManService.getMaxDate()).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadSaleMan list : " + t.size());
                t.forEach((s) -> {
                    saleManService.save(s);
                });
                observer.selected("download", "downloadSaleMan done.");
            }
        }).subscribe();
    }

    public void downloadLocation() {
        inventoryRepo.getUpdateLocation(locationService.getMaxDate()).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadLocation list : " + t.size());
                t.forEach((s) -> {
                    locationService.save(s);
                });
                observer.selected("download", "downloadLocation done.");
            }
        }).subscribe();
    }

    public void downloadRelation() {
        inventoryRepo.getUpdateRelation(relationService.getMaxDate()).doOnSuccess((t) -> {

            if (t != null) {
                observer.selected("download", "downloadRelation list : " + t.size());
                t.forEach((s) -> {
                    relationService.save(s);
                });
                observer.selected("download", "downloadRelation done.");
            }
        }).subscribe();
    }

    public void downloadUnit() {
        inventoryRepo.getUpdateUnit(stockService.getMaxDate()).doOnSuccess((t) -> {
            if (t != null) {

                observer.selected("download", "downloadUnit list : " + t.size());
                t.forEach((s) -> {
                    stockUnitService.save(s);
                });
                observer.selected("download", "downloadUnit done.");
            }
        }).subscribe();
    }

    public void downloadBrand() {
        inventoryRepo.getUpdateBrand(brandService.getMaxDate()).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadBrand list : " + t.size());
                t.forEach((s) -> {
                    brandService.save(s);
                });
                observer.selected("download", "downloadBrand done.");
            }
        }).subscribe();
    }

    public void downloadCategory() {
        inventoryRepo.getUpdateCategory(categoryService.getMaxDate()).doOnSuccess((t) -> {

            if (t != null) {
                observer.selected("download", "downloadCategory list : " + t.size());
                t.forEach((s) -> {
                    categoryService.save(s);
                });
                observer.selected("download", "downloadCategory done.");
            }
        }).subscribe();
    }

    public void downloadStockType() {
        inventoryRepo.getUpdateStockType(stockTypeService.getMaDate()).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadStockType list : " + t.size());
                t.forEach((s) -> {
                    stockTypeService.save(s);
                });
                observer.selected("download", "downloadStockType done.");
            }
        }).subscribe();
    }

    public void downloadStock() {
        String maxDate = stockService.getMaxDate();
        inventoryRepo.getUpdateStock(maxDate).doOnSuccess((t) -> {

            if (t != null) {
                observer.selected("download", "downloadStock list : " + t.size());
                t.forEach((s) -> {
                    stockService.save(s);
                });
                observer.selected("download", "downloadStock done.");
            }
        }).subscribe();
    }

    public void downloadStockFormula() {
        String maxDate = stockFormulaService.getMaxDate();
        inventoryRepo.getUpdateStockFormula(maxDate).doOnSuccess((t) -> {

            if (t != null) {
                observer.selected("download", "downloadStock formula list : " + t.size());
                t.forEach((s) -> {
                    stockFormulaService.save(s);
                });
                observer.selected("download", "downloadStock done.");
            }
        }).subscribe();
    }

    public void downloadStockFormulaPrice() {
        String maxDate = stockFormulaService.getMaxDateSFPrice();
        inventoryRepo.getUpdateStockFormulaPrice(maxDate).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadStock formula price list : " + t.size());
                t.forEach((s) -> {
                    stockFormulaService.save(s);
                });
                observer.selected("download", "downloadStock done.");
            }
        }).subscribe();
    }

    public void downloadStockFormulaQty() {
        String maxDate = stockFormulaService.getMaxDateSFQty();
        inventoryRepo.getUpdateStockFormulaQty(maxDate).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadStock formula qty list : " + t.size());
                t.forEach((s) -> {
                    stockFormulaService.save(s);
                });
                observer.selected("download", "downloadStock done.");
            }
        }).subscribe();
    }

    public void downloadGradeDetail() {
        String maxDate = stockFormulaService.getMaxDateGD();
        inventoryRepo.getUpdateGradeDetail(maxDate).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadStock formula gd list : " + t.size());
                t.forEach((s) -> {
                    stockFormulaService.save(s);
                });
            }
            observer.selected("download", "downloadStock done.");
        }).subscribe();
    }

    public void downloadStockCriteria() {
        String maxDate = stockCriteriaService.getMaxDate();
        inventoryRepo.getUpdateStockCriteria(maxDate).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadStock criteria list : " + t.size());
                t.forEach((s) -> {
                    stockCriteriaService.save(s);
                });
                observer.selected("download", "downloadStock done.");
            }
        }).subscribe();
    }

    public void downloadJob() {
        String maxDate = jobService.getMaxDate();
        inventoryRepo.getUpdateJob(maxDate).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "job list : " + t.size());
                t.forEach((s) -> {
                    jobService.save(s);
                });
                observer.selected("download", "dwonloadJob done.");
            }
        }).subscribe();
    }

    public void downloadPattern() {
        String maxDate = patternService.getMaxDate();
        inventoryRepo.getUpdatePattern(maxDate).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "pattern list : " + t.size());
                t.forEach((s) -> {
                    patternService.save(s);
                });
                observer.selected("download", "downloadPattern done.");
            }
        }).subscribe();
    }

    public void downloadLabourGroup() {
        String maxDate = labourGroupService.getMaxDate();
        inventoryRepo.getUpdateLabourGroup(maxDate).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadLabourGroup list : " + t.size());
                t.forEach((s) -> {
                    labourGroupService.save(s);
                });
                observer.selected("download", "downloadLabourGroup done.");
            }
        }).subscribe();
    }

    public void downloadRegion() {
        String maxDate = regionService.getMaxDate();
        inventoryRepo.getUpdateRegion(maxDate).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "downloadRegion list : " + t.size());
                t.forEach((s) -> {
                    regionService.save(s);
                });
                observer.selected("download", "downloadRegion done.");
            }
        }).subscribe();
    }

    public void downloadOuputStatus() {
        String maxDate = outputCostService.getMaxDate();
        inventoryRepo.getOutputCost(maxDate).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "OutputCost List : " + t.size());
                t.forEach((s) -> {
                    outputCostService.save(s);
                });
                observer.selected("download", "dwonloadOutputCost done.");
            }
        }).subscribe();
    }

    public void downloadAccSettings() {
        String maxDate = accSettingService.getMaxDate();
        inventoryRepo.getAccSetting(maxDate).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "accSetting list : " + t.size());
                t.forEach((acc) -> {
                    accSettingService.save(acc);
                });
                observer.selected("download", "downloadAccSetting done.");
            }
        }).subscribe();
    }

    public void downloadWarehouse() {
        String maxDate = warehouseService.getMaxDate();
        inventoryRepo.getWarehouse(maxDate).doOnSuccess((t) -> {
            if (t != null) {
                observer.selected("download", "WareHouse list : " + t.size());
                t.forEach((w) -> {
                    warehouseService.save(w);
                });
                observer.selected("download", "downloadWarehouse done.");
            }
        }).subscribe();
    }

    public void start() {
        if (localDatabase) {
            startDownload();
        } else {
            observer.selected("enable", true);
        }
    }
}

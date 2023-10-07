/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.repo.AccountRepo;
import com.acc.model.Gl;
import com.acc.model.GlKey;
import com.common.DateLockUtil;
import com.common.Global;
import com.common.SelectionObserver;
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
import com.h2.service.GlService;
import com.h2.service.MacPropertyService;
import com.h2.service.MachineInfoService;
import com.h2.service.MenuService;
import com.h2.service.OrderHisService;
import com.h2.service.OrderStatusService;
import com.h2.service.PrivilegeCompanyService;
import com.h2.service.PrivilegeMenuService;
import com.h2.service.ProcessHisService;
import com.h2.service.ProjectService;
import com.h2.service.PurHisService;
import com.h2.service.RegionService;
import com.h2.service.RetInService;
import com.h2.service.RetOutService;
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
import com.h2.service.WeightLossService;
import com.inventory.model.OrderHis;
import com.inventory.model.ProcessHis;
import com.inventory.model.PurHis;
import com.inventory.model.RetInHis;
import com.inventory.model.RetOutHis;
import com.inventory.model.SaleHis;
import com.inventory.model.TransferHis;
import com.inventory.model.WeightLossHis;
import com.repo.InventoryRepo;
import com.repo.UserRepo;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Lenovo
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
    private GlService glService;
    @Autowired
    OrderHisService orderHisService;
    @Autowired
    PurHisService purHisService;
    @Autowired
    RetInService retInService;
    @Autowired
    RetOutService retOutService;
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
            uploadAccountData();
        }
    }
    
    public void uploadInvData() {
        uploadOrder();
        uploadTransfer();
        uploadWeightLoss();
        uploadManufacture();
        uploadPurchase();
        uploadReturnIn();
        uploadReturnOut();
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
        log.info("need to upload order his : " + list.size());
        if (!list.isEmpty()) {
            list.forEach((l) -> {
                inventoryRepo.uploadOrder(l).subscribe((r) -> {
                    r.setIntgUpdStatus("ACK");
                    orderHisService.updateACK(r.getKey());
                });
            });
            
        }
    }
    
    public void uploadPurchase() {
        List<PurHis> list = purHisService.unUploadVoucher(Global.compCode);
        log.info("need to upload purchase his : " + list.size());
        if (!list.isEmpty()) {
            list.forEach((pur) -> {
                inventoryRepo.uploadPurchase(pur).subscribe((p) -> {
                    p.setIntgUpdStatus("ACK");
                    purHisService.updateACK(p.getKey());
                });
            });
        }
    }
    
    public void uploadReturnIn() {
        List<RetInHis> list = retInService.unUploadVoucher(Global.compCode);
        log.info("need to upload ReturnIn his : " + list.size());
        if (!list.isEmpty()) {
            list.forEach((in) -> {
                inventoryRepo.uploadRetIn(in).subscribe((n) -> {
                    n.setIntgUpdStatus("ACK");
                    retInService.updateACK(n.getKey());
                });
            });
            
        }
    }
    
    public void uploadReturnOut() {
        List<RetOutHis> list = retOutService.unUploadVoucher(Global.compCode);
        log.info("need to upload ReturnOut his : " + list.size());
        if (!list.isEmpty()) {
            list.forEach((out) -> {
                inventoryRepo.uploadRetOut(out).subscribe((o) -> {
                    o.setIntgUpdStatus("ACK");
                    retOutService.updateACK(o.getKey());
                });
            });
        }
    }
    
    public void uploadTransfer() {
        List<TransferHis> list = transferHisService.unUpload(Global.compCode);
        log.info("need to upload transfer : " + list.size());
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
        log.info("need to upload weight loss : " + list.size());
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
        log.info("need to upload manufacture : " + list.size());
        if (!list.isEmpty()) {
            list.forEach((l) -> {
                inventoryRepo.uploadProcess(l).subscribe((r) -> {
                    r.setIntgUpdStatus("ACK");
                    processHisService.updateACK(r.getKey());
                });
            });
            
        }
    }
    
    public void uploadAccountData() {
        uploadGL();
    }
    
    public void uploadGL() {
        List<Gl> list = glService.unUploadVoucher(Global.compCode);
        if (!list.isEmpty()) {
            log.info("need to upload Gl list : " + list.size());
            list.forEach((l) -> {
                accountRepo.uploadGL(l).subscribe((gl) -> {
                    GlKey key = gl.getKey();
                    key.setGlCode(gl.getKey().getGlCode());
                    key.setCompCode(gl.getKey().getCompCode());
                    key.setDeptId(gl.getKey().getDeptId());
                    glService.updateACK(key);
                }, (e) -> {
                    observer.selected("download", "to uploadGl : " + e.getMessage());
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
        accountRepo.getUpdateChartOfAccountByDate(coaService.getMaxDate()).subscribe((t) -> {
            observer.selected("download", "downloadChartOfAccount list : " + t.size());
            t.forEach((coa) -> {
                coaService.save(coa);
            });
            observer.selected("download", "downloadChartOfAccount done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadTraderAccount() {
        accountRepo.getUpdateTraderByDate(traderService.getMaxDate()).subscribe((t) -> {
            observer.selected("download", "downloadTrader list : " + t.size());
            t.forEach((tr) -> {
                traderService.save(tr);
            });
            observer.selected("download", "downloadTrader done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadDepartmentAccount() {
        accountRepo.getUpdateDepartmentAByDate(departmentAService.getMaxDate()).subscribe((d) -> {
            observer.selected("download", "downloadDepartmentAccount list : " + d.size());
            d.forEach((da) -> {
                departmentAService.save(da);
            });
            observer.selected("download", "downloadDepartmentAccount done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadAppUser() {
        userRepo.getAppUserByDate(userService.getMaxDate()).subscribe((u) -> {
            observer.selected("download", "downloadAppUser list : " + u.size());
            u.forEach((a) -> {
                userService.save(a);
            });
            observer.selected("download", "downloadAppUser done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadBusinessType() {
        userRepo.getBusinessTypeByDate(businessTypeService.getMaxDate()).subscribe((b) -> {
            observer.selected("download", "downloadAppUser list : " + b.size());
            b.forEach((a) -> {
                businessTypeService.save(a);
            });
            observer.selected("download", "downloadAppUser done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadCompanyInfo() {
        userRepo.getCompanyInfoByDate(companyInfoService.getMaxDate()).subscribe((c) -> {
            observer.selected("download", "downloadCompanyInfo list : " + c.size());
            c.forEach((a) -> {
                companyInfoService.save(a);
            });
            observer.selected("download", "downloadCompanyInfo done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadCurrency() {
        userRepo.getCurrencyByDate(currencyService.getMaxDate()).subscribe((c) -> {
            observer.selected("download", "downloadCurrency list : " + c.size());
            c.forEach((a) -> {
                currencyService.save(a);
            });
            observer.selected("download", "downloadCurrency done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadDepartment() {
        userRepo.getDepartmentByDate(departmentService.getMaxDate()).subscribe((d) -> {
            observer.selected("download", "downloadDepartment list : " + d.size());
            d.forEach((a) -> {
                departmentService.save(a);
            });
            observer.selected("download", "downloadDepartment done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadExchangeRate() {
        userRepo.getExchangeRateByDate(exchangeRateService.getMaxDate()).subscribe((ex) -> {
            observer.selected("download", "downloadExchangeRate list : " + ex.size());
            ex.forEach((a) -> {
                exchangeRateService.save(a);
            });
            observer.selected("download", "downloadExchangeRate done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadMachineInfo() {
        userRepo.getMachineInfoByDate(machineInfoService.getMaxDate()).subscribe((m) -> {
            observer.selected("download", "downloadMachineInfo list : " + m.size());
            m.forEach((a) -> {
                machineInfoService.save(a);
            });
            observer.selected("download", "downloadMachineInfo done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadMacProperty() {
        userRepo.getMacPropertyByDate(macPropertyService.getMaxDate()).subscribe((m) -> {
            observer.selected("download", "downloadMacProperty list : " + m.size());
            m.forEach((a) -> {
                macPropertyService.save(a);
            });
            observer.selected("download", "downloadMacProperty done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadMenu() {
        userRepo.getMenuByDate(menuService.getMaxDate()).subscribe((m) -> {
            observer.selected("download", "downloadMenu list : " + m.size());
            m.forEach((a) -> {
                menuService.save(a);
            });
            observer.selected("download", "downloadMenu done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadPC() {
        userRepo.getPCByDate(pcService.getMaxDate()).subscribe((p) -> {
            observer.selected("download", "downloadPC list : " + p.size());
            p.forEach((a) -> {
                pcService.save(a);
            });
            observer.selected("download", "downloadPC done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadPM() {
        userRepo.getPMByDate(pmService.getMaxDate()).subscribe((m) -> {
            observer.selected("download", "downloadPM list : " + m.size());
            m.forEach((a) -> {
                pmService.save(a);
            });
            observer.selected("download", "downloadPM done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadProject() {
        userRepo.getProjectByDate(pService.getMaxDate()).subscribe((p) -> {
            observer.selected("download", "downloadProject list : " + p.size());
            p.forEach((a) -> {
                pService.save(a);
            });
            observer.selected("download", "downloadPM done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadRole() {
        userRepo.getRoleByDate(roleService.getMaxDate()).subscribe((r) -> {
            observer.selected("download", "downloadRole list : " + r.size());
            r.forEach((a) -> {
                roleService.save(a);
            });
            observer.selected("download", "downloadRole done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadRoleProperty() {
        userRepo.getRolePropByDate(rpService.getMaxDate()).subscribe((r) -> {
            observer.selected("download", "downloadRoleProperty list : " + r.size());
            r.forEach((a) -> {
                rpService.save(a);
            });
            observer.selected("download", "downloadRoleProperty done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadSystemProperty() {
        userRepo.getSystemPropertyByDate(sysPropertyService.getMaxDate()).subscribe((r) -> {
            observer.selected("download", "downloadSystemProperty list : " + r.size());
            r.forEach((a) -> {
                sysPropertyService.save(a);
            });
            observer.selected("download", "downloadSystemProperty done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
    }
    
    public void downloadDateLock() {
        log.info("date lock time : " + dateLockService.getMaxDate());
        userRepo.getDateLockByDate(dateLockService.getMaxDate()).subscribe((r) -> {
            observer.selected("download", "downloadDateLock list : " + r.size());
            r.forEach((a) -> {
                dateLockService.save(a);
            });
            dateLockUtil.initLockDate();
            observer.selected("download", "downloadDateLock done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            observer.selected("download", err.getMessage());
        });
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
        downloadStockCriteria();
    }
    
    public void downloadPriceOption() {
        inventoryRepo.getUpdatePriceOption(priceOptionService.getMaxDate()).subscribe((t) -> {
            observer.selected("download", "downloadPriceOption list : " + t.size());
            t.forEach((s) -> {
                priceOptionService.save(s);
            });
            observer.selected("download", "downloadPriceOption done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadVouStatus() {
        inventoryRepo.getUpdateVouStatus(vouStatusService.getMaxDate()).subscribe((t) -> {
            observer.selected("download", "downloadVouStatus list : " + t.size());
            t.forEach((s) -> {
                vouStatusService.save(s);
            });
            observer.selected("download", "downloadVouStatus done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadOrderStatus() {
        inventoryRepo.getUpdateOrderStatus(orderStatusService.getMaxDate()).subscribe((t) -> {
            observer.selected("download", "downloadOrderStatus list : " + t.size());
            t.forEach((s) -> {
                orderStatusService.save(s);
            });
            observer.selected("download", "downloadOrderStatus done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadInvTrader() {
        inventoryRepo.getUpdateTrader(traderInvService.getMaxDate()).subscribe((t) -> {
            observer.selected("download", "downloadInvTrader list : " + t.size());
            t.forEach((s) -> {
                traderInvService.save(s);
            });
            observer.selected("download", "downloadInvTrader done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadSaleMan() {
        inventoryRepo.getUpdateSaleMan(saleManService.getMaxDate()).subscribe((t) -> {
            observer.selected("download", "downloadSaleMan list : " + t.size());
            t.forEach((s) -> {
                saleManService.save(s);
            });
            observer.selected("download", "downloadSaleMan done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadLocation() {
        inventoryRepo.getUpdateLocation(locationService.getMaxDate()).subscribe((t) -> {
            observer.selected("download", "downloadLocation list : " + t.size());
            t.forEach((s) -> {
                locationService.save(s);
            });
            observer.selected("download", "downloadLocation done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadRelation() {
        inventoryRepo.getUpdateRelation(relationService.getMaxDate()).subscribe((t) -> {
            observer.selected("download", "downloadRelation list : " + t.size());
            t.forEach((s) -> {
                relationService.save(s);
            });
            observer.selected("download", "downloadRelation done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadUnit() {
        inventoryRepo.getUpdateUnit(stockService.getMaxDate()).subscribe((t) -> {
            observer.selected("download", "downloadUnit list : " + t.size());
            t.forEach((s) -> {
                stockUnitService.save(s);
            });
            observer.selected("download", "downloadUnit done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadBrand() {
        inventoryRepo.getUpdateBrand(brandService.getMaxDate()).subscribe((t) -> {
            observer.selected("download", "downloadBrand list : " + t.size());
            t.forEach((s) -> {
                brandService.save(s);
            });
            observer.selected("download", "downloadBrand done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadCategory() {
        inventoryRepo.getUpdateCategory(categoryService.getMaxDate()).subscribe((t) -> {
            observer.selected("download", "downloadCategory list : " + t.size());
            t.forEach((s) -> {
                categoryService.save(s);
            });
            observer.selected("download", "downloadCategory done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadStockType() {
        inventoryRepo.getUpdateStockType(stockTypeService.getMaDate()).subscribe((t) -> {
            observer.selected("download", "downloadStockType list : " + t.size());
            t.forEach((s) -> {
                stockTypeService.save(s);
            });
            observer.selected("download", "downloadStockType done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadStock() {
        String maxDate = stockService.getMaxDate();
        inventoryRepo.getUpdateStock(maxDate).subscribe((t) -> {
            observer.selected("download", "downloadStock list : " + t.size());
            t.forEach((s) -> {
                stockService.save(s);
            });
            observer.selected("download", "downloadStock done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadStockFormula() {
        String maxDate = stockFormulaService.getMaxDate();
        inventoryRepo.getUpdateStockFormula(maxDate).subscribe((t) -> {
            log.info("stock formula = " + t.size());
            observer.selected("download", "downloadStock formula list : " + t.size());
            t.forEach((s) -> {
                stockFormulaService.save(s);
                log.info("stock formula detail = " + s.getListDtl().size());
                s.getListDtl().forEach((dtl) -> {
                    stockFormulaService.save(dtl);
                });
            });
            observer.selected("download", "downloadStock done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadStockCriteria() {
        String maxDate = stockCriteriaService.getMaxDate();
        inventoryRepo.getUpdateStockCriteria(maxDate).subscribe((t) -> {
            log.info("stock criteria = " + t.size());
            observer.selected("download", "downloadStock criteria list : " + t.size());
            t.forEach((s) -> {
                stockCriteriaService.save(s);
            });
            observer.selected("download", "downloadStock done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void downloadRegion() {
        String maxDate = regionService.getMaxDate();
        inventoryRepo.getUpdateRegion(maxDate).subscribe((t) -> {
            observer.selected("download", "downloadRegion list : " + t.size());
            t.forEach((s) -> {
                regionService.save(s);
            });
            observer.selected("download", "downloadRegion done.");
        }, (e) -> {
            observer.selected("download", e.getMessage());
        });
    }
    
    public void start() {
        if (localDatabase) {
            startDownload();
        } else {
            observer.selected("enable", true);
        }
    }
}

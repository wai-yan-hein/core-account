/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.repo.AccountRepo;
import com.acc.model.Gl;
import com.acc.model.GlKey;
import com.common.Global;
import com.common.SelectionObserver;
import com.h2.service.BrandService;
import com.h2.service.BusinessTypeService;
import com.h2.service.COAService;
import com.h2.service.CategoryService;
import com.h2.service.CompanyInfoService;
import com.h2.service.CurrencyService;
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
import com.h2.service.PrivilegeCompanyService;
import com.h2.service.PrivilegeMenuService;
import com.h2.service.ProcessHisService;
import com.h2.service.ProjectService;
import com.h2.service.PurHisService;
import com.h2.service.RetInService;
import com.h2.service.RetOutService;
import com.h2.service.RolePropertyService;
import com.h2.service.RoleService;
import com.h2.service.StockInOutService;
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
import com.inventory.model.StockInOut;
import com.inventory.model.TransferHis;
import com.inventory.model.WeightLossHis;
import com.repo.InventoryRepo;
import com.repo.UserRepo;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

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
    @Autowired
    private StockInOutService stockInOutService;
    @Autowired
    private TransferHisService transferHisService;
    @Autowired
    private WeightLossService weightLossService;
    @Autowired
    private ProcessHisService processHisService;
    @Autowired
    private TaskScheduler taskScheduler;
    private SelectionObserver observer;

    public void setObserver(SelectionObserver observer) {
        this.observer = observer;
    }

    public void startDownload() {
        if (localDatabase) {
            log.info("download start.");
            downloadUser();
            downloadInventory();
            downloadAccount();
            log.info("download end.");
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
        uploadStockInOut();
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

    public void uploadStockInOut() {
        List<StockInOut> list = stockInOutService.unUpload(Global.compCode);
        log.info("need to upload stockinout : " + list.size());
        if (!list.isEmpty()) {
            list.forEach((l) -> {
                inventoryRepo.uploadStockInOut(l).subscribe((r) -> {
                    r.setIntgUpdStatus("ACK");
                    stockInOutService.updateACK(r.getKey());
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
                    log.error("to uploadGl : " + e.getMessage());
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
    }

    private void downloadAccount() {
        downloadDepartmentAccount();
        downloadTraderAccount();
        downloadChartofAccount();
    }

    private void downloadChartofAccount() {
        log.info(coaService.getMaxDate());
        accountRepo.getUpdateChartOfAccountByDate(coaService.getMaxDate()).subscribe((t) -> {
            log.info("downloadChartOfAccount list : " + t.size());
            t.forEach((coa) -> {
                coaService.save(coa);
            });
            log.info("downloadChartOfAccount done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadTraderAccount() {
        accountRepo.getUpdateTraderByDate(traderService.getMaxDate()).subscribe((t) -> {
            log.info("downloadTrader list : " + t.size());
            t.forEach((tr) -> {
                traderService.save(tr);
            });
            log.info("downloadTrader done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadDepartmentAccount() {
        accountRepo.getUpdateDepartmentAByDate(departmentAService.getMaxDate()).subscribe((d) -> {
            log.info("downloadDepartmentAccount list : " + d.size());
            d.forEach((da) -> {
                departmentAService.save(da);
            });
            log.info("downloadDepartmentAccount done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadAppUser() {
        userRepo.getAppUserByDate(userService.getMaxDate()).subscribe((u) -> {
            log.info("downloadAppUser list : " + u.size());
            u.forEach((a) -> {
                userService.save(a);
            });
            observer.selected("download", "downloadAppUser done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadBusinessType() {
        userRepo.getBusinessTypeByDate(businessTypeService.getMaxDate()).subscribe((b) -> {
            log.info("downloadAppUser list : " + b.size());
            b.forEach((a) -> {
                businessTypeService.save(a);
            });
            observer.selected("download", "downloadAppUser done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadCompanyInfo() {
        userRepo.getCompanyInfoByDate(companyInfoService.getMaxDate()).subscribe((c) -> {
            log.info("downloadCompanyInfo list : " + c.size());
            c.forEach((a) -> {
                companyInfoService.save(a);
            });
            observer.selected("download", "downloadCompanyInfo done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadCurrency() {
        userRepo.getCurrencyByDate(currencyService.getMaxDate()).subscribe((c) -> {
            log.info("downloadCurrency list : " + c.size());
            c.forEach((a) -> {
                currencyService.save(a);
            });
            observer.selected("download", "downloadCurrency done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadDepartment() {
        userRepo.getDepartmentByDate(departmentService.getMaxDate()).subscribe((d) -> {
            log.info("downloadDepartment list : " + d.size());
            d.forEach((a) -> {
                departmentService.save(a);
            });
            observer.selected("download", "downloadDepartment done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadExchangeRate() {
        userRepo.getExchangeRateByDate(exchangeRateService.getMaxDate()).subscribe((ex) -> {
            log.info("downloadExchangeRate list : " + ex.size());
            ex.forEach((a) -> {
                exchangeRateService.save(a);
            });
            observer.selected("download", "downloadExchangeRate done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadMachineInfo() {
        userRepo.getMachineInfoByDate(machineInfoService.getMaxDate()).subscribe((m) -> {
            log.info("downloadMachineInfo list : " + m.size());
            m.forEach((a) -> {
                machineInfoService.save(a);
            });
            observer.selected("download", "downloadMachineInfo done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadMacProperty() {
        userRepo.getMacPropertyByDate(macPropertyService.getMaxDate()).subscribe((m) -> {
            log.info("downloadMacProperty list : " + m.size());
            m.forEach((a) -> {
                macPropertyService.save(a);
            });
            observer.selected("download", "downloadMacProperty done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadMenu() {
        userRepo.getMenuByDate(menuService.getMaxDate()).subscribe((m) -> {
            log.info("downloadMenu list : " + m.size());
            m.forEach((a) -> {
                menuService.save(a);
            });
            observer.selected("download", "downloadMenu done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadPC() {
        userRepo.getPCByDate(pcService.getMaxDate()).subscribe((p) -> {
            log.info("downloadPC list : " + p.size());
            p.forEach((a) -> {
                pcService.save(a);
            });
            observer.selected("download", "downloadPC done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadPM() {
        userRepo.getPMByDate(pmService.getMaxDate()).subscribe((m) -> {
            log.info("downloadPM list : " + m.size());
            m.forEach((a) -> {
                pmService.save(a);
            });
            observer.selected("download", "downloadPM done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadProject() {
        userRepo.getProjectByDate(pService.getMaxDate()).subscribe((p) -> {
            log.info("downloadProject list : " + p.size());
            p.forEach((a) -> {
                pService.save(a);
            });
            observer.selected("download", "downloadPM done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadRole() {
        userRepo.getRoleByDate(roleService.getMaxDate()).subscribe((r) -> {
            log.info("downloadRole list : " + r.size());
            r.forEach((a) -> {
                roleService.save(a);
            });
            observer.selected("download", "downloadRole done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadRoleProperty() {
        userRepo.getRolePropByDate(rpService.getMaxDate()).subscribe((r) -> {
            log.info("downloadRoleProperty list : " + r.size());
            r.forEach((a) -> {
                rpService.save(a);
            });
            observer.selected("download", "downloadRoleProperty done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadSystemProperty() {
        userRepo.getSystemPropertyByDate(sysPropertyService.getMaxDate()).subscribe((r) -> {
            log.info("downloadSystemProperty list : " + r.size());
            r.forEach((a) -> {
                sysPropertyService.save(a);
            });
            observer.selected("download", "downloadSystemProperty done.");
        }, (err) -> {
            observer.selected("download", "offline.");
            log.error(err.getMessage());
        });
    }

    private void downloadInventory() {
        downloadPriceOption();
        downloadVouStatus();
        downloadInvTrader();
        downloadSaleMan();
        downloadLocation();
        downloadRelation();
        downloadUnit();
        downloadBrand();
        downloadStockType();
        downloadCategory();
        downloadStock();
    }

    private void downloadPriceOption() {
        inventoryRepo.getUpdatePriceOption(priceOptionService.getMaxDate()).subscribe((t) -> {
            log.info("downloadPriceOption list : " + t.size());
            t.forEach((s) -> {
                priceOptionService.save(s);
            });
            log.info("downloadPriceOption done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadVouStatus() {
        inventoryRepo.getUpdateVouStatus(vouStatusService.getMaxDate()).subscribe((t) -> {
            log.info("downloadVouStatus list : " + t.size());
            t.forEach((s) -> {
                vouStatusService.save(s);
            });
            log.info("downloadVouStatus done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadInvTrader() {
        inventoryRepo.getUpdateTrader(traderInvService.getMaxDate()).subscribe((t) -> {
            log.info("downloadInvTrader list : " + t.size());
            t.forEach((s) -> {
                traderInvService.save(s);
            });
            log.info("downloadInvTrader done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadSaleMan() {
        inventoryRepo.getUpdateSaleMan(saleManService.getMaxDate()).subscribe((t) -> {
            log.info("downloadSaleMan list : " + t.size());
            t.forEach((s) -> {
                saleManService.save(s);
            });
            log.info("downloadSaleMan done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadLocation() {
        inventoryRepo.getUpdateLocation(locationService.getMaxDate()).subscribe((t) -> {
            log.info("downloadLocation list : " + t.size());
            t.forEach((s) -> {
                locationService.save(s);
            });
            log.info("downloadLocation done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadRelation() {
        inventoryRepo.getUpdateRelation(relationService.getMaxDate()).subscribe((t) -> {
            log.info("downloadRelation list : " + t.size());
            t.forEach((s) -> {
                relationService.save(s);
            });
            log.info("downloadRelation done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadUnit() {
        inventoryRepo.getUpdateUnit(stockService.getMaxDate()).subscribe((t) -> {
            log.info("downloadUnit list : " + t.size());
            t.forEach((s) -> {
                stockUnitService.save(s);
            });
            log.info("downloadUnit done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadBrand() {
        inventoryRepo.getUpdateBrand(brandService.getMaxDate()).subscribe((t) -> {
            log.info("downloadBrand list : " + t.size());
            t.forEach((s) -> {
                brandService.save(s);
            });
            log.info("downloadBrand done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadCategory() {
        inventoryRepo.getUpdateCategory(categoryService.getMaxDate()).subscribe((t) -> {
            log.info("downloadCategory list : " + t.size());
            t.forEach((s) -> {
                categoryService.save(s);
            });
            log.info("downloadCategory done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadStockType() {
        inventoryRepo.getUpdateStockType(stockTypeService.getMaDate()).subscribe((t) -> {
            log.info("downloadStockType list : " + t.size());
            t.forEach((s) -> {
                stockTypeService.save(s);
            });
            log.info("downloadStockType done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    private void downloadStock() {
        String maxDate = stockService.getMaxDate();
        inventoryRepo.getUpdateStock(maxDate).subscribe((t) -> {
            log.info("downloadStock list : " + t.size());
            t.forEach((s) -> {
                stockService.save(s);
            });
            log.info("downloadStock done.");
        }, (e) -> {
            log.error(e.getMessage());
        });
    }

    public void start() {
        if (localDatabase) {
            taskScheduler.scheduleAtFixedRate(() -> {
                startDownload();
            }, Duration.ofMinutes(15));
        } else {
            observer.selected("enable", true);
        }

    }
}

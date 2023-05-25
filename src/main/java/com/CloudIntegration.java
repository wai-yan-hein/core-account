/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.h2.service.BrandService;
import com.h2.service.BusinessTypeService;
import com.h2.service.CategoryService;
import com.h2.service.CompanyInfoService;
import com.h2.service.CurrencyService;
import com.h2.service.DepartmentUserService;
import com.h2.service.LocationService;
import com.h2.service.RelationService;
import com.h2.service.SaleManService;
import com.h2.service.ExchangeRateService;
import com.h2.service.MacPropertyService;
import com.h2.service.MachineInfoService;
import com.h2.service.MenuService;
import com.h2.service.PrivilegeCompanyService;
import com.h2.service.PrivilegeMenuService;
import com.h2.service.ProjectService;
import com.h2.service.RolePropertyService;
import com.h2.service.RoleService;
import com.h2.service.StockService;
import com.h2.service.StockTypeService;
import com.h2.service.StockUnitService;
import com.h2.service.UserService;
import com.inventory.ui.common.InventoryRepo;
import com.user.common.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public void startDownload() {
        if (localDatabase) {
            downloadUser();
            downloadInventory();
            downloadAccount();
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
    }

    private void downloadAppUser() {
        userRepo.getAppUserByDate(userService.getMaxDate()).subscribe((u) -> {
            log.info("user size = " + u.size());
            u.forEach((a) -> {
                userService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadBusinessType() {
        userRepo.getBusinessTypeByDate(businessTypeService.getMaxDate()).subscribe((b) -> {
            log.info("bus type size = " + b.size());
            b.forEach((a) -> {
                businessTypeService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadCompanyInfo() {
        userRepo.getCompanyInfoByDate(companyInfoService.getMaxDate()).subscribe((c) -> {
            log.info("comp info size = " + c.size());
            c.forEach((a) -> {
                companyInfoService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadCurrency() {
        userRepo.getCurrencyByDate(currencyService.getMaxDate()).subscribe((c) -> {
            log.info("currency size = " + c.size());
            c.forEach((a) -> {
                currencyService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadDepartment() {
        userRepo.getDepartmentByDate(departmentService.getMaxDate()).subscribe((d) -> {
            log.info("dept size = " + d.size());
            d.forEach((a) -> {
                departmentService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadExchangeRate() {
        userRepo.getExchangeRateByDate(exchangeRateService.getMaxDate()).subscribe((ex) -> {
            log.info("exchange rate size = " + ex.size());
            ex.forEach((a) -> {
                exchangeRateService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadMachineInfo() {
        userRepo.getMachineInfoByDate(machineInfoService.getMaxDate()).subscribe((m) -> {
            log.info("machine info size = " + m.size());
            m.forEach((a) -> {
                machineInfoService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadMacProperty() {
        userRepo.getMacPropertyByDate(macPropertyService.getMaxDate()).subscribe((m) -> {
            log.info("mac prop size = " + m.size());
            m.forEach((a) -> {
                macPropertyService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadMenu() {
        userRepo.getMenuByDate(menuService.getMaxDate()).subscribe((m) -> {
            log.info("menu size = " + m.size());
            m.forEach((a) -> {
                menuService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadPC() {
        userRepo.getPCByDate(pcService.getMaxDate()).subscribe((p) -> {
            log.info("pc size = " + p.size());
            p.forEach((a) -> {
                pcService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadPM() {
        userRepo.getPMByDate(pmService.getMaxDate()).subscribe((m) -> {
            log.info("pm size = " + m.size());
            m.forEach((a) -> {
                pmService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadProject() {
        userRepo.getProjectByDate(pService.getMaxDate()).subscribe((p) -> {
            log.info("project size = " + p.size());
            p.forEach((a) -> {
                pService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadRole() {
        userRepo.getRoleByDate(roleService.getMaxDate()).subscribe((r) -> {
            log.info("role size = " + r.size());
            r.forEach((a) -> {
                roleService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadRoleProperty() {
        userRepo.getRolePropByDate(rpService.getMaxDate()).subscribe((r) -> {
            log.info("role prop size = " + r.size());
            r.forEach((a) -> {
                rpService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadInventory() {
        downloadSaleMan();
        downloadLocation();
        downloadRelation();
        downloadUnit();
        downloadBrand();
        downloadStockType();
        downloadCategory();
        downloadStock();
    }

    private void downloadAccount() {

    }

    private void downloadInvTrader() {
        inventoryRepo.getUpdateSaleMan(saleManService.getMaxDate()).subscribe((t) -> {
            log.info("downloadInvTrader list : " + t.size());
            t.forEach((s) -> {
                saleManService.save(s);
            });
            log.info("downloadInvTrader done.");
        }, (e) -> {
            log.info(e.getMessage());
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
            log.info(e.getMessage());
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
            log.info(e.getMessage());
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
            log.info(e.getMessage());
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
            log.info(e.getMessage());
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
            log.info(e.getMessage());
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
            log.info(e.getMessage());
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
            log.info(e.getMessage());
        });
    }

    private void downloadStock() {
        inventoryRepo.getUpdateStock(stockService.getMaxDate()).subscribe((t) -> {
            log.info("downloadStock list : " + t.size());
            t.forEach((s) -> {
                stockService.save(s);
            });
            log.info("downloadStock done.");
        }, (e) -> {
            log.info(e.getMessage());
        });
    }
}

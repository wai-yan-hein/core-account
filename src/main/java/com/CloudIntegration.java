/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.common.Global;
import com.h2.dao.SaleHisDetailDao;
import com.h2.service.BrandService;
import com.h2.service.BusinessTypeService;
import com.h2.service.CategoryService;
import com.h2.service.CompanyInfoService;
import com.h2.service.CurrencyService;
import com.h2.service.DepartmentUserService;
import com.h2.service.LocationService;
import com.h2.service.PriceOptionService;
import com.h2.service.RelationService;
import com.h2.service.SaleHisService;
import com.h2.service.SaleManService;
import com.h2.service.StockService;
import com.h2.service.StockTypeService;
import com.h2.service.StockUnitService;
import com.h2.service.TraderInvService;
import com.h2.service.UserService;
import com.h2.service.VouStatusService;
import com.inventory.model.SaleHis;
import com.inventory.model.SaleHisDetail;
import com.inventory.ui.common.InventoryRepo;
import com.user.common.UserRepo;
import java.util.List;
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
    @Autowired
    private TraderInvService traderInvService;
    @Autowired
    private VouStatusService vouStatusService;
    @Autowired
    private PriceOptionService priceOptionService;
    @Autowired
    private SaleHisService saleHisService;
    @Autowired
    private SaleHisDetailDao saleHisDetailDao;

    public void startDownload() {
        if (localDatabase) {
            downloadUser();
            downloadInventory();
            downloadAccount();
        }
    }

    public void startUpload() {
        if (localDatabase) {
            List<SaleHis> list = saleHisService.findAll(Global.compCode);
            if (!list.isEmpty()) {
                log.info("need to upload sale his : " + list.size());
            }
            List<SaleHisDetail> listD = saleHisDetailDao.findAll(Global.compCode);
            if (!listD.isEmpty()) {
                log.info("need to upload sale his detail : " + listD.size());
            }
        }
    }

    private void downloadUser() {
        downloadAppUser();
        downloadBusinessType();
        downloadCompanyInfo();
        downloadCurrency();
        downloadDepartment();
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

    private void downloadAccount() {

    }

    private void downloadPriceOption() {
        inventoryRepo.getUpdatePriceOption(priceOptionService.getMaxDate()).subscribe((t) -> {
            log.info("downloadPriceOption list : " + t.size());
            t.forEach((s) -> {
                priceOptionService.save(s);
            });
            log.info("downloadPriceOption done.");
        }, (e) -> {
            log.info(e.getMessage());
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
            log.info(e.getMessage());
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

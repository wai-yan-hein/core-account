/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com;

import com.h2.service.BusinessTypeService;
import com.h2.service.CompanyInfoService;
import com.h2.service.CurrencyService;
import com.h2.service.DepartmentUserService;
import com.h2.service.StockService;
import com.h2.service.StockTypeService;
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
    }

    private void downloadAppUser() {
        userRepo.getAppUser().subscribe((u) -> {
            u.forEach((a) -> {
                userService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadBusinessType() {
        userRepo.getBusinessType().subscribe((b) -> {
            b.forEach((a) -> {
                businessTypeService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadCompanyInfo() {
        userRepo.getCompany(true).subscribe((c) -> {
            c.forEach((a) -> {
                companyInfoService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadCurrency() {
        userRepo.getCurrency().subscribe((c) -> {
            c.forEach((a) -> {
                currencyService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadDepartment() {
        userRepo.getDeparment().subscribe((d) -> {
            d.forEach((a) -> {
                departmentService.save(a);
            });
        }, (err) -> {
            log.info(err.getMessage());
        });
    }

    private void downloadInventory() {
        downloadStockType();
        downloadStock();
    }

    private void downloadAccount() {

    }

    private void downloadCategory() {
        inventoryRepo.getUpdateStockType(stockTypeService.getMaDate()).subscribe((t) -> {
            log.info("downloadCategory list : " + t.size());
            t.forEach((s) -> {
                stockTypeService.save(s);
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

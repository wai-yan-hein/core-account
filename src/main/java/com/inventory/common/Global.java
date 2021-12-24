/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.common;

import com.inventory.model.AccSetting;
import com.inventory.model.AppUser;
import com.inventory.model.Category;
import com.inventory.model.Currency;
import com.inventory.model.Location;
import com.inventory.model.MachineInfo;
import com.inventory.model.Region;
import com.inventory.model.SaleMan;
import com.inventory.model.Stock;
import com.inventory.model.StockBrand;
import com.inventory.model.StockType;
import com.inventory.model.StockUnit;
import com.inventory.model.Trader;
import com.inventory.model.UnitPattern;
import com.inventory.model.UnitRelation;
import com.inventory.model.VouStatus;
import java.awt.Color;
import java.awt.Font;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;

/**
 *
 * @author winswe
 */
public class Global {

    public static ServerSocket sock;
    public static Font lableFont = new java.awt.Font("Arial", 1, 12);
    public static Font amtFont = new java.awt.Font("Arial Nova Light", 1, 13);
    public static Font textFont = new java.awt.Font("Zawgyi-One", 0, 12);
    public static Font menuFont = new java.awt.Font("Zawgyi-One", 1, 13);
    public static Font companyFont = new java.awt.Font("Zawgyi-One", 0, 18);
    public static Font shortCutFont = new java.awt.Font("Arial Nova Light", 0, 12);
    public static int tblRowHeight = 24;
    public static Font tblHeaderFont = new java.awt.Font("Arial Nova Light", 1, 13);
    public static Color selectionColor;
    public static String uuid;
    public static boolean synceFinish = true;
    public static AppUser loginUser;
    public static String roleCode;
    public static String compCode;
    public static int sessionId;
    public static String sessionName;
    public static String loginDate;
    public static Integer macId;
    public static String machineName;
    public static boolean mqConStatus = false;
    public static int x;
    public static int y;
    public static int width;
    public static int height;
    public static JFrame parentForm;
    public static String companyName;
    public static String reportPath = "report";
    public static String fontPath = "font";
    //public static HashMap<String, String> sysProperties = new HashMap<>();
    public static List<VouStatus> listVouStatus = new ArrayList<>();
    public static List<Category> listCategory = new ArrayList<>();
    public static List<StockBrand> listStockBrand = new ArrayList<>();
    public static List<SaleMan> listSaleMan = new ArrayList<>();
    public static List<StockType> listStockType = new ArrayList<>();
    public static List<Location> listLocation = new ArrayList<>();
    public static List<StockUnit> listStockUnit = new ArrayList<>();
    public static List<AccSetting> listAccSetting = new ArrayList<>();
    public static List<Region> listRegion = new ArrayList<>();
    public static List<UnitPattern> listUnitPattern = new ArrayList<>();
    public static List<UnitRelation> listUnitRelation = new ArrayList<>();
    public static List<Stock> listStock = new ArrayList<>();
    public static List<Trader> listCustomer = new ArrayList<>();
    public static List<Trader> listSupplier = new ArrayList<>();
    public static List<Trader> listTrader = new ArrayList<>();
    public static List<Currency> listCurrency = new ArrayList<>();
    public static List<AppUser> listAppUser = new ArrayList<>();
    public static List<MachineInfo> listMachine = new ArrayList<>();
    public static Map<String, String> hmRoleProperty = new HashMap<>();
    public static Currency defaultCurrency;
    public static Location defaultLocation;
    public static Trader defaultCustomer;
    public static Trader defaultSupplier;
    public static SaleMan defaultSaleMan;

}

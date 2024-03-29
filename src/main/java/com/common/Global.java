/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import com.acc.model.DateModel;
import com.inventory.entity.AccSetting;
import com.user.model.AppUser;
import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 *
 * @author wai yan
 */
public class Global {

    public static final Color BG_COLOR = new Color(240, 242, 250);
    public static final Color GREEN = new Color(37, 196, 26);
    public static Font lableFont = new java.awt.Font("Arial", 1, 13);
    public static Font amtFont = new java.awt.Font("Arial Nova Light", 1, 14);
    public static Font textFont = new java.awt.Font("Zawgyi-One", 0, 13);
    public static Font menuFont = new java.awt.Font("Zawgyi-One", 1, 14);
    public static Font companyFont = new java.awt.Font("Zawgyi-One", 0, 18);
    public static Font shortCutFont = new java.awt.Font("Arial Nova Light", 0, 3);
    public static int tblRowHeight = 25;
    public static Font tblHeaderFont = new java.awt.Font("Arial Nova Light", 1, 14);
    public static Color selectionColor;
    public static String uuid;
    public static boolean batchLock = false;
    public static AppUser loginUser;
    public static String roleCode;
    public static String compCode;
    public static int sessionId;
    public static String sessionName;
    public static String loginDate;
    public static Integer macId;
    public static String machineName;
    public static Integer deptId;
    public static boolean mqConStatus = false;
    public static int x;
    public static int y;
    public static int width;
    public static int height;
    public static String dateFormat = "dd/MM/yyyy";
    public static JFrame parentForm;
    public static JDialog dialog;
    public static String startDate;
    public static String endate;
    public static String companyName;
    public static String companyAddress;
    public static String companyPhone;
    public static String reportPath = "report";
    public static String accountRP = "report/account/";
    public static String fontName;
    public static Map<String, String> hmRoleProperty = new HashMap<>();
    public static Map<String, String> hmUser = new HashMap<>();
    public static Map<String, AccSetting> hmAcc = new HashMap<>();
    public static String currency;
    public static List<DateModel> listDate;
    public static double version = 2.0;

    public static void clear() {
        lableFont = new java.awt.Font("Arial", 1, 13);
        amtFont = new java.awt.Font("Arial Nova Light", 1, 14);
        textFont = new java.awt.Font("Zawgyi-One", 0, 13);
        menuFont = new java.awt.Font("Zawgyi-One", 1, 14);
        companyFont = new java.awt.Font("Zawgyi-One", 0, 18);
        shortCutFont = new java.awt.Font("Arial Nova Light", 0, 3);
        tblRowHeight = 25;
        tblHeaderFont = new java.awt.Font("Arial Nova Light", 1, 14);
        selectionColor = null;
        uuid = null;
        batchLock = false;
        loginUser = null;
        roleCode = null;
        compCode = null;
        sessionId = 0;
        sessionName = null;
        loginDate = null;
        macId = null;
        machineName = null;
        deptId = null;
        mqConStatus = false;
        x = 0;
        y = 0;
        width = 0;
        height = 0;
        dateFormat = "dd/MM/yyyy";
        parentForm = null;
        dialog = null;
        startDate = null;
        endate = null;
        companyName = null;
        companyAddress = null;
        companyPhone = null;
        reportPath = "report";
        accountRP = "report/account/";
        fontName = null;
        hmRoleProperty.clear();
        hmUser.clear();
        hmAcc.clear();
        currency = null;
        listDate = null;
        version = 2.0;
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.inventory.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import org.slf4j.LoggerFactory;

/**
 *
 * @author WSwe
 */
public class Util1 {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Util1.class);

    public static String getEngChar(int i) {
        String[] engChar = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"};

        if ((i + 1) < 0 || (i + 1) > engChar.length) {
            return null;
        } else {
            return engChar[i + 1];
        }
    }

    public static boolean isNumber(Object obj) {
        boolean status = false;

        try {
            if (!Util1.isNull(obj)) {
                Double.parseDouble(obj.toString());
                status = true;
            }
        } catch (NumberFormatException ex) {
            status = false;
            logger.error("NumberUtil.isNumber : " + ex.getMessage());
        }
        return status;
    }

    /*public static String getPropValue(String key) {
        String strFilter = "v.propKey = '" + key + "'";
        List<SystemProperty> listSP = HibernateUtil.findAllHSQL("SystemProperty", strFilter);

        if (!listSP.isEmpty()) {
            return listSP.get(0).getPropValue();
        } else {
            return null;
        }
    }*/
    public static Date toDate(Object objDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        try {
            if (objDate != null) {
                date = formatter.parse(objDate.toString());
            }
        } catch (ParseException ex) {
            logger.info("toDateStr Error : " + ex.getMessage());
        }

        return date;
    }

    public static Date toJavaDate(Object objDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
        Date date = null;

        try {
            if (objDate != null) {
                date = formatter.parse(objDate.toString());
            }
        } catch (ParseException ex) {
            logger.info("toDateStr Error : " + ex.getMessage());
        }

        return date;
    }

    public static boolean isDate(String str) {

        return str.length() == 10;
    }

    public static boolean isSameDate(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return false;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(d1).equals(sdf.format(d2));
    }

    public static String toDateStrMYSQL(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            date = formatter.format(toDate(strDate));
        } catch (Exception ex) {
            logger.info("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return date;
    }

    public static boolean isMySqLDate(String strDate) {
        boolean status = true;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;
        try {
            date = formatter.format(toDate(strDate));
        } catch (Exception ex) {
            status = false;
            logger.info("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return status;
    }

    public static String toDateTimeStrMYSQL(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = null;

        try {
            date = formatter.format(toDate(strDate, "dd/MM/yyyy"));
        } catch (Exception ex) {
            logger.info("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return date;
    }

    public static String getTodayDateTimeStrMySql() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = null;

        try {
            date = formatter.format(new Date());
        } catch (Exception ex) {

        }

        return date;
    }

    public static String toDateStr(String strDate, String inFormat, String outFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(outFormat);
        String date = null;

        try {
            /*if (strDate.contains("-")) {
                date = strDate;
            } else {*/
            date = formatter.format(toDate(strDate, inFormat));
            //}
        } catch (Exception ex) {
            try {
                date = formatter.format(toDate(strDate, outFormat));
            } catch (Exception ex1) {
                logger.info("toDateStr : " + ex1.getMessage());
            }
        }

        return date;
    }

    public static String toDateStrMYSQLEnd(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            date = formatter.format(toDate(strDate, "dd/MM/yyyy")) + " 23:59:59";
        } catch (Exception ex) {
            logger.info("toDateStrMYSQL Error : " + ex.getMessage());
        }

        return date;
    }

    public static Date toDate(Object objDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = null;

        try {
            date = formatter.parse(objDate.toString());
        } catch (ParseException ex) {
            try {
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                date = formatter.parse(objDate.toString());
            } catch (ParseException ex1) {
                logger.info("toDateStr Error : " + ex1.getMessage());
            }
        }

        return date;
    }

    public static String getFileExtension(String content) {
        String extension = "";

        if (content.contains("jpeg")) {
            extension = "jpg";
        } else if (content.contains("gif")) {
            extension = "gif";
        } else if (content.contains("tif")) {
            extension = "tif";
        } else if (content.contains("png")) {
            extension = "png";
        } else if (content.contains("bmp")) {
            extension = "bmp";
        }

        return extension;
    }

    public static String toDateStr(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String strDate = null;

        try {
            strDate = formatter.format(date);
        } catch (Exception ex) {
            System.out.println("toDateStr Error : " + ex.getMessage());
        }

        return strDate;
    }

    public static Date getTodayDate() {
        Date todayDate = Calendar.getInstance().getTime();
        return todayDate;
    }

    public static String toDateStrMYSQL(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            date = formatter.format(toDate(strDate, format));
        } catch (Exception ex) {
            logger.info("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return date;
    }

    public static String addDateTo(String date, int ttlDay) {
        String output = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(toDate(date, "dd/MM/yyyy")); // Now use today date.
            c.add(Calendar.DATE, ttlDay);
            output = formatter.format(c.getTime());
        } catch (Exception ex) {
            logger.info("addDateTo : " + ex.getMessage());
        }

        return output;
    }

    public static Date addDateTo(Date date, int ttlDay) {
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        Date tmp = null;

        try {
            //c.setTime(toDate(date, "yyyy-MM-dd")); // Now use today date.
            c.setTime(date);
            c.add(Calendar.DATE, ttlDay);
            tmp = c.getTime();
        } catch (Exception ex) {
            logger.info("addDateTo : " + ex.getMessage());
        }

        return tmp;
    }

    public static String isNull(String strValue, String value) {
        if (strValue == null) {
            return value;
        } else if (strValue.isEmpty() || strValue.equals("")) {
            return value;
        } else {
            return strValue;
        }
    }

    public static boolean isNull(String value) {
        boolean status = false;
        if (value == null) {
            status = true;
        } else if (value.isBlank()) {
            status = true;
        }
        return status;
    }

    public static boolean isNull(Object value) {
        boolean status = false;
        if (value == null) {
            status = true;
        } else if (value.toString().isBlank()) {
            status = true;
        }
        return status;
    }

    public static String isNullObj(Object obj, String value) {
        if (obj == null) {
            return value;
        } else {
            return value;
        }
    }

    public static Date getLastDayOfMonth(String strDate, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate(strDate, format));

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        Date lastDayOfMonth = calendar.getTime();
        return lastDayOfMonth;
    }

    public static int getDatePart(Date d, String format) {
        int intValue = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            String value = sdf.format(d);

            if (!value.isEmpty()) {
                intValue = Integer.parseInt(value);
            }
        } catch (Exception ex) {

        }

        return intValue;
    }

    public static Double NZeroDouble(Object number) {
        Double value = 0.0;

        try {
            if (number == null) {
                return new Double(0);
            } else {
                return value = Double.parseDouble(number.toString());
            }
        } catch (NumberFormatException ex) {
            return new Double(0);
        }
    }

    public static int isNullZero(Integer value) {
        if (value == null) {
            return 0;
        } else {
            return value;
        }
    }

    public static double nullZero(String value) {
        if (value == null) {
            return 0;
        }

        if (value.isEmpty()) {
            return 0;
        }

        return Double.parseDouble(value);
    }

    public static boolean getNullTo(Boolean value) {
        if (value == null) {
            return false;
        } else {
            return value;
        }
    }

    public static String getPeriod(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMyyyy");
        String strPeriod = null;
        Date date = toDate(strDate, format);

        if (date != null) {
            strPeriod = formatter.format(date);
        }

        return strPeriod;
    }

    public static String getPeriod(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMyyyy");
        String strPeriod = null;

        if (date != null) {
            strPeriod = formatter.format(date);
        }

        return strPeriod;
    }

    public static String getZawgyiText(HashMap<Integer, Integer> hmIngZgy, String text) {
        String tmpStr = "";

        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                String tmpS = Character.toString(text.charAt(i));
                int tmpChar = (int) text.charAt(i);

                if (hmIngZgy.containsKey(tmpChar)) {
                    char tmpc = (char) hmIngZgy.get(tmpChar).intValue();
                    if (tmpStr.isEmpty()) {
                        tmpStr = Character.toString(tmpc);
                    } else {
                        tmpStr = tmpStr + Character.toString(tmpc);
                    }
                } else if (tmpS.equals("ƒ")) {
                    if (tmpStr.isEmpty()) {
                        tmpStr = "ႏ";
                    } else {
                        tmpStr = tmpStr + "ႏ";
                    }
                } else if (tmpStr.isEmpty()) {
                    tmpStr = tmpS;
                } else {
                    tmpStr = tmpStr + tmpS;
                }
            }
        }

        return tmpStr;
    }

    public static Double getDouble(Object number) {
        double value = 0.0;
        if (number != null) {
            if (!number.toString().isEmpty()) {
                value = Double.parseDouble(number.toString());
            }
        }
        return value;
    }

    public static Float getFloat(Object number) {
        float value = 0.0f;
        if (number != null) {
            if (!number.toString().isEmpty()) {
                value = Float.parseFloat(number.toString());
            }
        }
        return value;
    }

    public static Float gerFloatOne(Object number) {
        float value = 1.0f;
        if (!Util1.isNull(number)) {
            value = Float.parseFloat(number.toString());
        }
        return value;
    }

    public static Long getLong(Object number) {
        long value = 0;
        if (number != null) {
            value = Long.parseLong(number.toString());
        }
        return value;
    }

    public static Integer getInteger(Object obj) {
        return Util1.isNull(obj) ? 0 : Integer.parseInt(obj.toString());
    }

    public static String getString(String str) {
        String value = "";
        if (str != null) {
            value = str;
        }
        return value;
    }

    public static String getString(Object obj) {
        String value = "";
        if (obj != null) {
            value = obj.toString();
        }
        return value;
    }

    public static String getStringValue(Object obj) {
        String value = "";
        if (obj != null) {
            value = obj.toString();
        }
        return value;
    }

    public static boolean getBoolean(Boolean obj) {
        if (obj == null) {
            obj = false;
        }
        return obj;

    }

    public static boolean getBoolean(String obj) {
        boolean status = false;
        if (!Util1.isNull(obj)) {
            status = obj.equals("1") || obj.toLowerCase().equals("true");
        }
        return status;

    }

    public static DefaultFormatterFactory getDecimalFormat() {
        return new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat("#,##0.###;(#,##0.###)")));
    }

    public static int getCurrentMonth() {
        LocalDate currentdate = LocalDate.now();
        return currentdate.getMonth().getValue();

    }

    public static boolean isValidDateFormat(Object dateStr, String dateFromat) {
        boolean status = true;
        DateFormat formatter = new SimpleDateFormat(dateFromat);

        Date date = null;
        if (isDate(dateStr.toString())) {
            try {
                date = formatter.parse(dateStr.toString());
            } catch (ParseException ex) {
                logger.info("isValidDateFormat Error : " + ex.getMessage());
                status = false;

            }

        } else {
            status = false;
        }
        return status;
    }

    public static String toFormatDate(String obj) {
        String[] arr = null;
        //int year = Calendar.getInstance().get(Calendar.YEAR);
        //String strYear = String.valueOf(year).substring(0, 2);
        //logger.info("String year .." + strYear);
        arr = obj.split("(?<=\\G.{2})");

        String format = arr[0] + "/" + arr[1] + "/" + arr[2] + arr[3];
        return format;

    }

    public static JDialog getLoading(JDialog owner, ImageIcon icon) {
        JDialog dialog = new JDialog(owner, false);
        dialog.getContentPane().setBackground(Color.white);
        dialog.setSize(70, 70);
        dialog.getContentPane().setLayout(new BorderLayout());
        JLabel lblImg = new JLabel(icon);
        lblImg.setLocation(70, 0);
        dialog.add(lblImg);
        dialog.getContentPane().add(lblImg, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(null);
        dialog.setUndecorated(true);
        dialog.validate();
        return dialog;
    }

    public static JDialog getLoading(JFrame owner, ImageIcon icon) {
        JDialog dialog = new JDialog(owner, false);
        dialog.getContentPane().setBackground(Color.white);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(70, 70);
        dialog.getContentPane().setLayout(new BorderLayout());
        JLabel lblImg = new JLabel(icon);
        lblImg.setLocation(70, 0);
        dialog.add(lblImg);
        dialog.getContentPane().add(lblImg, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(null);
        dialog.setUndecorated(true);
        dialog.validate();
        return dialog;
    }

    public static Dimension getScreenSize() {
        //Calculate dialog position to centre.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();

        return screen;
    }

    public static String getComputerName() {
        String computerName = "";

        try {
            computerName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.info("getComputerName : " + e.toString());
        }

        return computerName;
    }

    public static String getIPAddress() {
        String iPAddress = "";

        try {
            iPAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.info("getIPAddress : " + e.toString());
        }

        return iPAddress;
    }

    public static String toFormatPattern(Double value) {
        final String pattern = "#,##0.###;(#,##0.###)";
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.applyPattern(pattern);
        return df.format(value);

    }

    public static String getString(boolean value) {
        return value ? "true" : "false";
    }

    public static boolean isPositive(Float value) {
        return value > 0;
    }

    /* public static Date getTodayDate() {
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm a");
    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Yangon"));
    String strDateTime = sdf.format(new Date());
    return Util1.toDate(strDateTime, "dd/MM/yyyy");
    }*/
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JasperReportsContext;

/**
 * @author WSwe
 */
@Slf4j
public class Util1 {

    /**
     *
     */
    public static final String DECIMAL_FORMAT = "###,##0.##;(###,##0.##)";

    public static void print(String pName) {

    }

    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || obj.toString().isEmpty();
    }

    public static boolean isNumber(Object obj) {
        boolean status = false;
        try {
            if (!Util1.isNull(obj)) {
                String str = obj.toString();
                if (str.contains(",")) {
                    str = str.replaceAll(",", "");
                }
                Float.valueOf(str);
                status = true;
            }
        } catch (NumberFormatException ex) {
        }

        return status;
    }

    public static String getPropValue(String key) {
        return Global.hmRoleProperty.get(key);
    }

    public static boolean isProperValid(String key) {
        return Util1.getBoolean(Global.hmRoleProperty.get(key));
    }

    public static Date toDate(Object objDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;

        try {
            if (objDate != null) {
                date = formatter.parse(objDate.toString());
            }
        } catch (ParseException ex) {
            log.info("toDateStr Error : " + ex.getMessage());
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
            log.info("toDateStr Error : " + ex.getMessage());
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
            log.info("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return date;
    }

    public static boolean isMySqLDate(String strDate) {
        boolean status = true;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            formatter.format(toDate(strDate));
        } catch (Exception ex) {
            status = false;
            log.info("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return status;
    }

    public static String toDateTimeStrMYSQL(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = null;

        try {
            date = formatter.format(toDate(strDate, "dd/MM/yyyy"));
        } catch (Exception ex) {
            log.info("toDateTimeStrMYSQL : " + ex.getMessage());
        }

        return date;
    }

    public static String getTodayDateTimeStrMySql() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = null;

        try {
            date = formatter.format(new Date());
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage());
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
                log.info("toDateStr : " + ex1.getMessage());
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
            log.info("toDateStrMYSQL Error : " + ex.getMessage());
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
                log.info("toDateStr Error : " + ex1.getMessage());
            }
        }

        return date;
    }

    public static Date toDateFormat(Date date, String format) {
        SimpleDateFormat f = new SimpleDateFormat(format);
        try {
            return f.parse(f.format(date));
        } catch (ParseException ex) {
            log.error("toDateFormat: " + ex.getMessage());
        }
        return null;
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
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Yangon")).getTime();
    }

    public static String getTodayDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Yangon"));
        String strDateTime = sdf.format(new Date());
        return strDateTime;
    }

    public static String toDateStrMYSQL(String strDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String date = null;

        try {
            date = formatter.format(toDate(strDate, format));
        } catch (Exception ex) {
            log.info("toDateTimeStrMYSQL : " + ex.getMessage());
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
            log.info("addDateTo : " + ex.getMessage());
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
            log.info("addDateTo : " + ex.getMessage());
        }

        return tmp;
    }

    public static String isNull(String strValue, String value) {
        if (null != strValue) {
            if (!strValue.isEmpty() && !strValue.equals("")) {
                return strValue;
            } else {
                return value;
            }
        } else {
            return value;
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
        } catch (NumberFormatException ex) {
        }

        return intValue;
    }

    public static int isNullZero(Integer value) {
        return Objects.requireNonNullElse(value, 0);
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
        return Objects.requireNonNullElse(value, false);
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
        StringBuilder tmpStr = new StringBuilder();

        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                String tmpS = Character.toString(text.charAt(i));
                int tmpChar = text.charAt(i);

                if (hmIngZgy.containsKey(tmpChar)) {
                    char tmpc = (char) hmIngZgy.get(tmpChar).intValue();
                    if (tmpStr.length() == 0) {
                        tmpStr = new StringBuilder(Character.toString(tmpc));
                    } else {
                        tmpStr.append(tmpc);
                    }
                } else if (tmpS.equals("ƒ")) {
                    if (tmpStr.length() == 0) {
                        tmpStr = new StringBuilder("ႏ");
                    } else {
                        tmpStr.append("ႏ");
                    }
                } else if (tmpStr.length() == 0) {
                    tmpStr = new StringBuilder(tmpS);
                } else {
                    tmpStr.append(tmpS);
                }
            }
        }

        return tmpStr.toString();
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
        try {
            if (number != null) {
                String str = number.toString();
                if (!str.isEmpty()) {
                    if (str.contains(",")) {
                        str = str.replaceAll(",", "");
                    }
                    value = Float.parseFloat(str);
                }
            }
        } catch (NumberFormatException e) {
            log.error(String.format("getFloat: %s", e.getMessage()));
        }
        return value;
    }

    public static Float gerFloatOne(Object number) {
        float value = 1.0f;
        if (Util1.getFloat(number) > 0) {
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
        return Util1.isNull(obj) ? 0 : Integer.valueOf(obj.toString());
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
            status = obj.equals("1") || obj.equalsIgnoreCase("true");
        }
        return status;
    }

    public static DefaultFormatterFactory getDecimalFormat() {
        return new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat(DECIMAL_FORMAT)));
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
                log.info("isValidDateFormat Error : " + ex.getMessage());
                status = false;

            }

        } else {
            status = false;
        }
        return status;
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
        return toolkit.getScreenSize();
    }

    public static String getComputerName() {
        String computerName = "";

        try {
            computerName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.info("getComputerName : " + e);
        }

        return computerName;
    }

    public static String getIPAddress() {
        String iPAddress = "";

        try {
            iPAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.info("getIPAddress : " + e);
        }

        return iPAddress;
    }

    public static String toFormatPattern(Double value) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.applyPattern(DECIMAL_FORMAT);
        return df.format(value);

    }

    public static String toFormatDate(String obj) {
        String[] arr = obj.split("(?<=\\G.{2})");
        return arr[0] + "/" + arr[1] + "/" + arr[2] + arr[3];
    }

    public static String getString(boolean value) {
        return value ? "true" : "false";
    }

    public static boolean isPositive(Object value) {
        return Util1.getFloat(value) > 0;
    }

    public static Date toDateTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat f2 = new SimpleDateFormat("dd/MM/yyyy");
        LocalDateTime now = LocalDateTime.now();
        String strDate = f2.format(date) + " " + now.getHour() + ":"
                + now.getMinute() + ":" + now.getSecond();
        try {
            date = formatter.parse(strDate);
        } catch (ParseException ex) {
            log.error(String.format("toDateTime: %s", ex.getMessage()));
        }
        return date;
    }

    public static void extractZipToJson(byte[] zipData, String exportPath) {
        try {
            File file = new File(exportPath.concat(".zip"));
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(zipData);
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        try {
            new ZipFile(exportPath.concat(".zip")).extractAll("temp");
        } catch (ZipException ex) {
            log.error(ex.getMessage());
        }
    }

    private static Resolution getResolution() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        return new Resolution(width, height);
    }

    public static Resolution getPopSize() {
        Resolution r = getResolution();
        r.setWidth(r.getWidth() / 4);
        r.setHeight((r.getHeight() / 4));
        return r;
    }

    public static Date formatDate(Object obj) {
        if (obj != null) {
            DateFormat f1 = new SimpleDateFormat("dd/MM/yyyy");
            try {
                return f1.parse(obj.toString());
            } catch (ParseException ex) {
            }
            try {
                f1 = new SimpleDateFormat("dd/MM/yy");
                return f1.parse(obj.toString());
            } catch (ParseException ex) {
            }
            int length = obj.toString().length();
            return Util1.toDate(toFormatDate(obj.toString(), length), "dd/MM/yyyy");
        }
        return null;
    }

    public static String toFormatDate(String obj, int length) {
        String[] arr = obj.split("(?<=\\G.{2})");
        if (length == 8) {
            String format = arr[0] + "/" + arr[1] + "/" + arr[2] + arr[3];
            return format;
        }
        return arr[0] + "/" + arr[1] + "/20" + arr[2];

    }

    public static List<Date> getDaysBetweenDates(Date startdate, Date endate) {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (!calendar.getTime().after(endate)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static String minusDay(String sqlFormat, int minusDay) {
        LocalDate date = LocalDate.parse(sqlFormat);
        LocalDate minusDays = date.minusDays(minusDay);
        return minusDays.toString();
    }

    public static void initJasperContext() {
        JasperReportsContext jc = DefaultJasperReportsContext.getInstance();
        jc.setProperty("net.sf.jasperreports.default.font.name", Global.fontName);
        jc.setProperty("net.sf.jasperreports.default.pdf.font.name", Global.fontName);
        jc.setProperty("net.sf.jasperreports.default.pdf.encoding", "Identity-H");
        jc.setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
        jc.setProperty("net.sf.jasperreports.viewer.zoom", "1");
    }

}

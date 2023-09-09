/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.myanmartools.TransliterateZ2U;
import com.google.myanmartools.ZawgyiDetector;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
    public static final String DECIMAL_FORMAT = "###,###.###;(###,###.###)";
    public static final String DECIMAL_FORMAT1 = "###,##0;(###,##0)";
    public static final String DECIMAL_FORMAT2 = "###,###.#;(###,###.#)";
    public static final String DECIMAL_FORMAT3 = "###,###.000;(###,###.000)";

    private static final DecimalFormat df2 = new DecimalFormat("0");
    public static String SYNC_DATE;
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    public static HashMap<String, String> hmSysProp = new HashMap<>();
    public static boolean DARK_MODE = false;

    public static void print(String pName) {

    }

    public static boolean isNullOrEmpty(Object obj) {
        return obj == null || obj.toString().isEmpty();
    }

    public static String getOldDate() {
        return Util1.toDateTimeStrMYSQL(Util1.toDate("1998-10-07"));
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

    public static String getProperty(String key) {
        return hmSysProp.get(key);
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

    public static boolean isMultiCur() {
        return Util1.getBoolean(hmSysProp.get("system.multi.currency.flag"));
    }

    public static LocalDateTime toDateTime(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        return LocalDateTime.of(date.toLocalDate(), LocalTime.of(now.getHour(), now.getMinute(), now.getSecond()));
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

    public static String toDateTimeStrMYSQL(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return date == null ? null : formatter.format(date);
    }

    public static String toDateTimeStrMYSQL(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date == null ? null : formatter.format(date);
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

    public static LocalDateTime toDate(String mysql) {
        LocalDate localDate = LocalDate.parse(mysql);
        return localDate.atStartOfDay();
    }

    public static LocalDateTime toDate(Object date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(date.toString(), formatter);
    }

    public static Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime != null) {
            ZoneId zoneId = ZoneId.systemDefault();
            return Date.from(localDateTime.atZone(zoneId).toInstant());
        }
        return null;
    }

    public static LocalDateTime convertToLocalDateTime(Date date) {
        if (date != null) {
            Instant instant = date.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            return instant.atZone(zoneId).toLocalDateTime();
        }
        return null;
    }

    public static Date getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
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

    public static String toDateStr(String dateStr, String inFormat, String outFormat) {
        DateFormat inputFormat = new SimpleDateFormat(inFormat);
        DateFormat outputFormat = new SimpleDateFormat(outFormat);
        try {
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            log.error("toDateStr : " + e.getMessage());
            return null;
        }
    }

    public static String toDateStr(LocalDate date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return date.format(formatter);
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

    public static String toDateStr(LocalDateTime dateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        // Format the LocalDateTime
        return dateTime.format(formatter);
    }

    public static LocalDateTime getTodayLocalDateTime() {
        long currentTimeMillis = Calendar.getInstance(TimeZone.getTimeZone("Asia/Yangon")).getTimeInMillis();
        return LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(currentTimeMillis), ZoneId.of("Asia/Yangon"));
    }

    public static String getTodayDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy h:mm a");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Yangon"));
        String strDateTime = sdf.format(new Date());
        return strDateTime;
    }

    public static String toDateStrMYSQL(String dateStr, String format) {
        DateFormat inputFormat = new SimpleDateFormat(format);
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return null;
        }
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
        if (number == null) {
            return 0.0; // Return default value if input is null
        }
        String str = number.toString().trim().replace(",", "");
        try {
            return Double.valueOf(str); // Convert string to double
        } catch (NumberFormatException e) {
            log.error("getDouble : " + e.getMessage());
            return 0.0; // Return default value if parsing fails
        }
    }

    public static String getString(Object obj) {
        return obj == null ? null : obj.toString().strip();
    }

    public static boolean getBoolean(Object obj) {
        return obj != null && (obj.toString().equals("1") || obj.toString().equalsIgnoreCase("true"));

    }

    public static Float getFloat(Object number) {
        if (number == null) {
            return 0.0f; // Return a default value if the input is null
        }
        try {
            String str = number.toString().replaceAll(",", ""); // Remove commas
            if (!str.isEmpty()) {
                return Float.valueOf(str);
            }
        } catch (NumberFormatException e) {
            log.error(String.format("getFloat:", number));
        }

        return 0.0f;
    }

    public static Float getFloatOne(Object number) {
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

    public static Integer getIntegerOne(Object obj) {
        return Util1.isNull(obj) ? 1 : Integer.valueOf(obj.toString());
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
        if (!Util1.isNull(obj)) {
            return obj.equals("1") || obj.equalsIgnoreCase("true");
        }
        return false;
    }

    public static DefaultFormatterFactory getDecimalFormat() {
        return new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat(DECIMAL_FORMAT)));
    }

    public static DefaultFormatterFactory getDecimalFormat1() {
        return new DefaultFormatterFactory(new NumberFormatter(new DecimalFormat(DECIMAL_FORMAT1)));
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

    public static String getServerIp(String hostName) {
        try {
            InetAddress address = InetAddress.getByName(hostName);
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Unable to resolve hostname: " + hostName);
        }
        return "127.0.0.1";
    }

    public static String toFormatPattern(Double value) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.applyPattern(DECIMAL_FORMAT);
        return df.format(value);

    }

    public static String getString(boolean value) {
        return value ? "true" : "false";
    }

    public static boolean isPositive(Object value) {
        return Util1.getFloat(value) >= 0;
    }

    public static Date toDate(String str, String format) {
        if (str != null) {
            DateFormat dateFormat = new SimpleDateFormat(format);
            try {
                return dateFormat.parse(str);
            } catch (ParseException e) {
                log.error("toDate : " + e.getMessage());
            }
        }
        return null;
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
        r.setWidth(r.getWidth() / 3);
        r.setHeight((r.getHeight() / 4));
        return r;
    }

    public static Date formatDate(String input) {
        // First, check the length of the input string to determine the date format
        SimpleDateFormat[] dateFormats = {
            new SimpleDateFormat("ddMMyy"),
            new SimpleDateFormat("ddMMyyyy"),
            new SimpleDateFormat("dd/MM/yyyy")
        };

        for (SimpleDateFormat dateFormat : dateFormats) {
            try {
                Date date = dateFormat.parse(input);
                return date;
            } catch (ParseException ignored) {
                // If parsing fails, ignore and try the next format
            }
        }

        // If none of the formats match, throw an exception or handle it accordingly
        JOptionPane.showMessageDialog(Global.parentForm, "Invalid Date Format. Example ddMMyy,ddMMyyyy,dd/MM/yyyy");
        return null;
    }

    public static LocalDateTime formatLocalDateTime(String input) {
        DateTimeFormatter formatter = switch (input.length()) {
            case 6 ->
                DateTimeFormatter.ofPattern("ddMMyy");
            case 8 ->
                DateTimeFormatter.ofPattern("ddMMyyyy");
            default ->
                DateTimeFormatter.ofPattern("dd/MM/yyyy");
        }; // For input format "010123"
        // For input format "01012023"
        // For input format "01/01/2023"

        try {
            LocalDate localDate = LocalDate.parse(input, formatter);
            return localDate.atStartOfDay();
        } catch (Exception e) {
            log.error(e.getMessage());
            // If none of the formats match, throw an exception or handle it accordingly
            JOptionPane.showMessageDialog(Global.parentForm, "Invalid Date Format. Example ddMMyy,ddMMyyyy,dd/MM/yyyy");
            return null;
        }

    }

    public static String toFormatDate(String obj, int length) {
        String[] arr = obj.split("(?<=\\G.{2})");
        if (length == 8) {
            String format = arr[0] + "/" + arr[1] + "/" + arr[2] + arr[3];
            return format;
        }
        return arr[0] + "/" + arr[1] + "/20" + arr[2];

    }

    public static Date parseDate(String date, String dateFormat) {
        DateFormat format = new SimpleDateFormat(dateFormat);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            log.error("parseDate : " + e.getMessage());
            return null;
        }
    }

    public static LocalDate parseLocalDate(String date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(date, formatter);
    }

    public static LocalDateTime parseLocalDateTime(String date, String format) {
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            return LocalDateTime.parse(date, formatter);
        }
        return null;
    }

    public static LocalDateTime parseLocalDateTime(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

    }

    public static List<LocalDate> getDaysBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
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

    public static String convertToUniCode(String str) {
        if (isNull(str)) {
            return null;
        }
        ZawgyiDetector zd = new ZawgyiDetector();
        Double score = zd.getZawgyiProbability(str);
        if (getBoolean(df2.format(score))) {
            TransliterateZ2U z2U = new TransliterateZ2U("Zawgyi to Unicode");
            return z2U.convert(str);
        }
        return str;
    }

    public static void writeJsonFile(Object data, String exportPath) throws IOException {
        try (Writer writer = new FileWriter(exportPath, StandardCharsets.UTF_8)) {
            gson.toJson(data, writer);
        }
    }

    public static void exit() {
        try {
            String processName = ManagementFactory.getRuntimeMXBean().getName();
            long pid = Long.parseLong(processName.split("@")[0]);
            // Create a process instance for the "kill" command
            ProcessBuilder builder = new ProcessBuilder("taskkill", "/PID", Long.toString(pid));
            Process process = builder.start();
            int exitCode = process.waitFor();
            System.out.println("Process exited with code " + exitCode);
            // Wait for the "kill" command to finish executing
        } catch (IOException | InterruptedException | NumberFormatException e) {
            log.error(e.getMessage());
        }

    }

    public static String getMacAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            byte[] macAddressBytes = networkInterface.getHardwareAddress();
            StringBuilder macAddressBuilder = new StringBuilder();
            if (macAddressBytes != null) {
                for (byte b : macAddressBytes) {
                    macAddressBuilder.append(String.format("%02X:", b));
                }

                if (macAddressBuilder.length() > 0) {
                    macAddressBuilder.deleteCharAt(macAddressBuilder.length() - 1);
                }
                return macAddressBuilder.toString();
            }
        } catch (SocketException | UnknownHostException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static String getBaseboardSerialNumber() {
        String os = System.getProperty("os.name").toLowerCase();
        var command = "";
        if (os.contains("win")) {
            command = "wmic baseboard get serialnumber";
        } else if (os.contains("nix") || os.contains("nux")) {
            command = "dmidecode -t baseboard | grep 'Serial Number'";
        } else if (os.contains("mac")) {
            command = "ioreg -l | grep IOPlatformSerialNumber";
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Unsupported operating system.");
            System.exit(0);
        }
        try {
            ProcessBuilder processBuilder;
            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                processBuilder = new ProcessBuilder("bash", "-c", command);
            }
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String no = line.trim();
                    if (!no.isEmpty()) {
                        if (!no.startsWith("Serial")) {
                            return no;
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("getBaseboardSerialNumber :" + e.getMessage());
        }
        return getProcessId();

    }

    private static String getProcessId() {

        String os = System.getProperty("os.name").toLowerCase();
        try {
            String command = "";
            if (os.contains("win")) {
                command = "wmic cpu get processorid";
            } else if (os.contains("nix") || os.contains("nux")) {
                command = "dmidecode -t baseboard | grep 'Serial Number'";
            } else if (os.contains("mac")) {
                command = "ioreg -l | grep IOPlatformSerialNumber";
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "Unsupported operating system.");
                System.exit(0);
            }
            ProcessBuilder processBuilder;
//                    = new ProcessBuilder("cmd.exe", "/c", command);
            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            } else {
                processBuilder = new ProcessBuilder("bash", "-c", command);
            }
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String no = line.trim();
                    if (!no.isEmpty()) {
                        if (!no.startsWith("ProcessorId")) {
                            return no;
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static String cleanString(String str) {
        return str == null ? "" : str.replace(" ", "").toLowerCase();
    }

    public static Double toNull(double value) {
        return value == 0 ? null : value;
    }

    public static String isAll(String value) {
        if (value != null) {
            if (value.equals("All")) {
                return "-";
            }
        }
        return Util1.isNull(value, "-");
    }

    public static String getPassword() {
        return Util1.toDateStr(Util1.getTodayDate(), "yyyyMMdd");
    }

    public static <T> byte[] listToByteArray(List<T> obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(obj);
            return jsonString.getBytes();
        } catch (JsonProcessingException e) {
            log.error("Failed to convert list to byte array: " + e.getMessage());
        }
        return null; // Handle the failure to convert to byte array
    }

    public static String cleanStr(String str) {
        if (str != null) {
            return str.replaceAll(" ", "").toLowerCase();
        }
        return "";
    }

    public static double getTotalWeight(double wt, String qtyStr) {
        String[] parts = qtyStr.split("\\.");
        int qty = Integer.parseInt(parts[0]);
        int decimalWt = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        double ttlWt = (qty * wt) + decimalWt;
        return ttlWt;
    }

    public static float getTotalWeight(float wt, String qtyStr) {
        String[] parts = qtyStr.split("\\.");
        int qty = Integer.parseInt(parts[0]);
        int decimalWt = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        float ttlWt = (qty * wt) + decimalWt;
        return ttlWt;
    }

    public static boolean compareDate(String source, String target, String pattern) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime d1 = LocalDateTime.parse(source, formatter);
            LocalDateTime d2 = LocalDateTime.parse(target, formatter);
            return true;
            //return d1.isBefore(d2);
        } catch (Exception ex) {
            log.error("compareDate : " + ex.getMessage());
        }
        return false;
    }

    public static LocalDateTime toLocalDateTime(long timestampUtc) {
// Convert long timestamp to Instant
        Instant instant = Instant.ofEpochMilli(timestampUtc);
// Convert Instant to LocalDateTime in a specific time zone
        ZoneId zoneId = ZoneId.systemDefault(); // Change to the desired time zone
        return instant.atZone(zoneId).toLocalDateTime();
    }

    public static String autoCorrectSheetName(String input) {
        // Replace invalid characters with spaces
        // Limit the sheet name to a maximum of 31 characters
        if (input.length() > 31) {
            input = input.substring(0, 31);
        }
        return input;
    }

    public static void openFolder(String folderPath) {
        File folder = new File(folderPath);

        // Check if the folder exists
        if (folder.exists() && folder.isDirectory()) {
            try {
                // Open the folder using the default file manager
                Desktop.getDesktop().open(folder);
            } catch (IOException e) {
                log.error("openFolder : " + e.getMessage());
                // Handle the exception as needed
            }
        } else {
            log.error("openFolder : " + "Folder does not exit.");
        }
    }

    public static String replaceSpecialCharactersWithSpace(String input) {
        // Define a regular expression pattern to match the specified special characters
        String regex = "[~!@#$%^&*()\\-+={}\\[\\]:;'\"<>?/\\\\]";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher object for the input string
        Matcher matcher = pattern.matcher(input);

        // Replace matched special characters with spaces
        String result = matcher.replaceAll("");

        return result;
    }

}

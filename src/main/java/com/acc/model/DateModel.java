/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.acc.model;

/**
 *
 * @author Lenovo
 */
public class DateModel {

    private String monthName;
    private int month;
    private int year;
    private String text;
    private int day;
    private String startDate;
    private String endDate;

    public DateModel() {

    }

    public DateModel(String monthName, int month, int year, String text) {
        this.monthName = monthName;
        this.month = month;
        this.year = year;
        this.text = text;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getText() {
        if (text.equals("-")) {
            text = monthName + "/" + year;
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}

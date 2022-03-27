/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.common;

/**
 *
 * @author Lenovo
 */
public interface PanelControl {

    void save();

    void delete();

    void newForm();

    void history();

    void print();

    void refresh();

    void filter();

    String panelName();
}

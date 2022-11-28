/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.acc.editor;

import com.common.SelectionObserver;
import com.user.model.Currency;
import java.awt.event.KeyEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author DELL
 */
public class CurrencyAAutoCompleterTest {
    
    public CurrencyAAutoCompleterTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of setSelectionObserver method, of class CurrencyAAutoCompleter.
     */
    @Test
    public void testSetSelectionObserver() {
        System.out.println("setSelectionObserver");
        SelectionObserver selectionObserver = null;
        CurrencyAAutoCompleter instance = new CurrencyAAutoCompleter();
        instance.setSelectionObserver(selectionObserver);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mouseSelect method, of class CurrencyAAutoCompleter.
     */
    @Test
    public void testMouseSelect() {
        System.out.println("mouseSelect");
        CurrencyAAutoCompleter instance = new CurrencyAAutoCompleter();
        instance.mouseSelect();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of closePopup method, of class CurrencyAAutoCompleter.
     */
    @Test
    public void testClosePopup() {
        System.out.println("closePopup");
        CurrencyAAutoCompleter instance = new CurrencyAAutoCompleter();
        instance.closePopup();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of showPopup method, of class CurrencyAAutoCompleter.
     */
    @Test
    public void testShowPopup() {
        System.out.println("showPopup");
        CurrencyAAutoCompleter instance = new CurrencyAAutoCompleter();
        instance.showPopup();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of selectNextPossibleValue method, of class CurrencyAAutoCompleter.
     */
    @Test
    public void testSelectNextPossibleValue() {
        System.out.println("selectNextPossibleValue");
        CurrencyAAutoCompleter instance = new CurrencyAAutoCompleter();
        instance.selectNextPossibleValue();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of selectPreviousPossibleValue method, of class CurrencyAAutoCompleter.
     */
    @Test
    public void testSelectPreviousPossibleValue() {
        System.out.println("selectPreviousPossibleValue");
        CurrencyAAutoCompleter instance = new CurrencyAAutoCompleter();
        instance.selectPreviousPossibleValue();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCurrency method, of class CurrencyAAutoCompleter.
     */
    @Test
    public void testGetCurrency() {
        System.out.println("getCurrency");
        CurrencyAAutoCompleter instance = new CurrencyAAutoCompleter();
        Currency expResult = null;
        Currency result = instance.getCurrency();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCurrency method, of class CurrencyAAutoCompleter.
     */
    @Test
    public void testSetCurrency() {
        System.out.println("setCurrency");
        Currency currency = null;
        CurrencyAAutoCompleter instance = new CurrencyAAutoCompleter();
        instance.setCurrency(currency);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of keyTyped method, of class CurrencyAAutoCompleter.
     */
    @Test
    public void testKeyTyped() {
        System.out.println("keyTyped");
        KeyEvent e = null;
        CurrencyAAutoCompleter instance = new CurrencyAAutoCompleter();
        instance.keyTyped(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of keyPressed method, of class CurrencyAAutoCompleter.
     */
    @Test
    public void testKeyPressed() {
        System.out.println("keyPressed");
        KeyEvent e = null;
        CurrencyAAutoCompleter instance = new CurrencyAAutoCompleter();
        instance.keyPressed(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of keyReleased method, of class CurrencyAAutoCompleter.
     */
    @Test
    public void testKeyReleased() {
        System.out.println("keyReleased");
        KeyEvent e = null;
        CurrencyAAutoCompleter instance = new CurrencyAAutoCompleter();
        instance.keyReleased(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of selected method, of class CurrencyAAutoCompleter.
     */
    @Test
    public void testSelected() {
        System.out.println("selected");
        Object source = null;
        Object selectObj = null;
        CurrencyAAutoCompleter instance = new CurrencyAAutoCompleter();
        instance.selected(source, selectObj);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

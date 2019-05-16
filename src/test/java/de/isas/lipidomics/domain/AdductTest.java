/*
 * Copyright 2019 nils.hoffmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.isas.lipidomics.domain;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nils.hoffmann
 */
public class AdductTest {
    
    public AdductTest() {
    }

    /**
     * Test of setChargeSign method, of class Adduct.
     */
    @Test
    public void testSetChargeSign() {
        System.out.println("setChargeSign");
        Integer sign = null;
        Adduct instance = new Adduct();
        instance.setChargeSign(sign);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSumFormula method, of class Adduct.
     */
    @Test
    public void testGetSumFormula() {
        System.out.println("getSumFormula");
        Adduct instance = new Adduct();
        String expResult = "";
        String result = instance.getSumFormula();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAdductString method, of class Adduct.
     */
    @Test
    public void testGetAdductString() {
        System.out.println("getAdductString");
        Adduct instance = new Adduct();
        String expResult = "";
        String result = instance.getAdductString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCharge method, of class Adduct.
     */
    @Test
    public void testGetCharge() {
        System.out.println("getCharge");
        Adduct instance = new Adduct();
        Integer expResult = null;
        Integer result = instance.getCharge();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getChargeSign method, of class Adduct.
     */
    @Test
    public void testGetChargeSign() {
        System.out.println("getChargeSign");
        Adduct instance = new Adduct();
        Integer expResult = null;
        Integer result = instance.getChargeSign();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSumFormula method, of class Adduct.
     */
    @Test
    public void testSetSumFormula() {
        System.out.println("setSumFormula");
        String sumFormula = "";
        Adduct instance = new Adduct();
        instance.setSumFormula(sumFormula);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setAdductString method, of class Adduct.
     */
    @Test
    public void testSetAdductString() {
        System.out.println("setAdductString");
        String adductString = "";
        Adduct instance = new Adduct();
        instance.setAdductString(adductString);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCharge method, of class Adduct.
     */
    @Test
    public void testSetCharge() {
        System.out.println("setCharge");
        Integer charge = null;
        Adduct instance = new Adduct();
        instance.setCharge(charge);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class Adduct.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        Adduct instance = new Adduct();
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of canEqual method, of class Adduct.
     */
    @Test
    public void testCanEqual() {
        System.out.println("canEqual");
        Object other = null;
        Adduct instance = new Adduct();
        boolean expResult = false;
        boolean result = instance.canEqual(other);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class Adduct.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        Adduct instance = new Adduct();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Adduct.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Adduct instance = new Adduct();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

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
public class LipidAdductTest {
    
    public LipidAdductTest() {
    }

    /**
     * Test of getSumFormula method, of class LipidAdduct.
     */
    @Test
    public void testGetSumFormula() {
        System.out.println("getSumFormula");
        LipidAdduct instance = null;
        String expResult = "";
        String result = instance.getSumFormula();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLipid method, of class LipidAdduct.
     */
    @Test
    public void testGetLipid() {
        System.out.println("getLipid");
        LipidAdduct instance = null;
        LipidSpecies expResult = null;
        LipidSpecies result = instance.getLipid();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAdduct method, of class LipidAdduct.
     */
    @Test
    public void testGetAdduct() {
        System.out.println("getAdduct");
        LipidAdduct instance = null;
        Adduct expResult = null;
        Adduct result = instance.getAdduct();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setLipid method, of class LipidAdduct.
     */
    @Test
    public void testSetLipid() {
        System.out.println("setLipid");
        LipidSpecies lipid = null;
        LipidAdduct instance = null;
        instance.setLipid(lipid);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setAdduct method, of class LipidAdduct.
     */
    @Test
    public void testSetAdduct() {
        System.out.println("setAdduct");
        Adduct adduct = null;
        LipidAdduct instance = null;
        instance.setAdduct(adduct);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class LipidAdduct.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        LipidAdduct instance = null;
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of canEqual method, of class LipidAdduct.
     */
    @Test
    public void testCanEqual() {
        System.out.println("canEqual");
        Object other = null;
        LipidAdduct instance = null;
        boolean expResult = false;
        boolean result = instance.canEqual(other);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class LipidAdduct.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        LipidAdduct instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class LipidAdduct.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        LipidAdduct instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

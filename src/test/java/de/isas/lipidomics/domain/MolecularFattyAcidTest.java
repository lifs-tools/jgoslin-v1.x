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
public class MolecularFattyAcidTest {
    
    public MolecularFattyAcidTest() {
    }

    /**
     * Test of getNDoubleBonds method, of class MolecularFattyAcid.
     */
    @Test
    public void testGetNDoubleBonds() {
        System.out.println("getNDoubleBonds");
        MolecularFattyAcid instance = null;
        int expResult = 0;
        int result = instance.getNDoubleBonds();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class MolecularFattyAcid.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        MolecularFattyAcid instance = null;
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of canEqual method, of class MolecularFattyAcid.
     */
    @Test
    public void testCanEqual() {
        System.out.println("canEqual");
        Object other = null;
        MolecularFattyAcid instance = null;
        boolean expResult = false;
        boolean result = instance.canEqual(other);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class MolecularFattyAcid.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        MolecularFattyAcid instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class MolecularFattyAcid.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        MolecularFattyAcid instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

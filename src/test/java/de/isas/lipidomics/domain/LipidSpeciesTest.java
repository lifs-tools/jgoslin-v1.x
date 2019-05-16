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

import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nils.hoffmann
 */
public class LipidSpeciesTest {
    
    public LipidSpeciesTest() {
    }

    /**
     * Test of getLipidString method, of class LipidSpecies.
     */
    @Test
    public void testGetLipidSpeciesString() {
        System.out.println("getLipidSpeciesString");
        LipidSpecies instance = null;
        String expResult = "";
        String result = instance.getLipidString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLipidCategory method, of class LipidSpecies.
     */
    @Test
    public void testGetLipidCategory() {
        System.out.println("getLipidCategory");
        LipidSpecies instance = null;
        LipidCategory expResult = null;
        LipidCategory result = instance.getLipidCategory();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLipidClass method, of class LipidSpecies.
     */
    @Test
    public void testGetLipidClass() {
        System.out.println("getLipidClass");
        LipidSpecies instance = null;
        Optional<LipidClass> expResult = null;
        Optional<LipidClass> result = instance.getLipidClass();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getHeadGroup method, of class LipidSpecies.
     */
    @Test
    public void testGetHeadGroup() {
        System.out.println("getHeadGroup");
        LipidSpecies instance = null;
        String expResult = "";
        String result = instance.getHeadGroup();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getInfo method, of class LipidSpecies.
     */
    @Test
    public void testGetInfo() {
        System.out.println("getInfo");
        LipidSpecies instance = null;
        LipidSpeciesInfo expResult = null;
        LipidSpeciesInfo result = instance.getInfo().get();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class LipidSpecies.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        LipidSpecies instance = null;
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of canEqual method, of class LipidSpecies.
     */
    @Test
    public void testCanEqual() {
        System.out.println("canEqual");
        Object other = null;
        LipidSpecies instance = null;
        boolean expResult = false;
        boolean result = instance.canEqual(other);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class LipidSpecies.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        LipidSpecies instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class LipidSpecies.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        LipidSpecies instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

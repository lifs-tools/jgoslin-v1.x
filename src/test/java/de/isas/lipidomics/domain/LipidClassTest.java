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
public class LipidClassTest {
    
    public LipidClassTest() {
    }

    /**
     * Test of values method, of class LipidClass.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        LipidClass[] expResult = null;
        LipidClass[] result = LipidClass.values();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of valueOf method, of class LipidClass.
     */
    @Test
    public void testValueOf() {
        System.out.println("valueOf");
        String arg0 = "";
        LipidClass expResult = null;
        LipidClass result = LipidClass.valueOf(arg0);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCategory method, of class LipidClass.
     */
    @Test
    public void testGetCategory() {
        System.out.println("getCategory");
        LipidClass instance = null;
        LipidCategory expResult = null;
        LipidCategory result = instance.getCategory();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAbbreviation method, of class LipidClass.
     */
    @Test
    public void testGetAbbreviation() {
        System.out.println("getAbbreviation");
        LipidClass instance = null;
        String expResult = "";
        String result = instance.getAbbreviation();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLipidMapsClassName method, of class LipidClass.
     */
    @Test
    public void testGetLipidMapsClassName() {
        System.out.println("getLipidMapsClassName");
        LipidClass instance = null;
        String expResult = "";
        String result = instance.getLipidMapsClassName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLysoAbbreviation method, of class LipidClass.
     */
    @Test
    public void testGetLysoAbbreviation() {
        System.out.println("getLysoAbbreviation");
        LipidClass lipidClass = null;
        LipidClass instance = null;
        String expResult = "";
        String result = instance.getLysoAbbreviation(lipidClass);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of forHeadGroup method, of class LipidClass.
     */
    @Test
    public void testForHeadGroup() {
        System.out.println("forHeadGroup");
        String headGroup = "";
        Optional<LipidClass> expResult = null;
        Optional<LipidClass> result = LipidClass.forHeadGroup(headGroup);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

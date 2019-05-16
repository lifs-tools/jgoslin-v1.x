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
public class LipidLevelTest {
    
    public LipidLevelTest() {
    }

    /**
     * Test of values method, of class LipidLevel.
     */
    @Test
    public void testValues() {
        System.out.println("values");
        LipidLevel[] expResult = null;
        LipidLevel[] result = LipidLevel.values();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of valueOf method, of class LipidLevel.
     */
    @Test
    public void testValueOf() {
        System.out.println("valueOf");
        String arg0 = "";
        LipidLevel expResult = null;
        LipidLevel result = LipidLevel.valueOf(arg0);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}

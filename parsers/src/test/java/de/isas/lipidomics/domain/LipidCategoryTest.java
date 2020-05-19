/*
 * Copyright 2020 nilshoffmann.
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

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nilshoffmann
 */
public class LipidCategoryTest {

    @Test
    public void testValues() {
        assertEquals(8, LipidCategory.values().length);
    }

    @Test
    public void testValueOf() {
        Assertions.assertSame(LipidCategory.FA, LipidCategory.valueOf("FA"));
        Assertions.assertSame(LipidCategory.GL, LipidCategory.valueOf("GL"));
        Assertions.assertSame(LipidCategory.GP, LipidCategory.valueOf("GP"));
        Assertions.assertSame(LipidCategory.PK, LipidCategory.valueOf("PK"));
        Assertions.assertSame(LipidCategory.SL, LipidCategory.valueOf("SL"));
        Assertions.assertSame(LipidCategory.SP, LipidCategory.valueOf("SP"));
        Assertions.assertSame(LipidCategory.ST, LipidCategory.valueOf("ST"));
        Assertions.assertSame(LipidCategory.UNDEFINED, LipidCategory.valueOf("UNDEFINED"));
    }

    @Test
    public void testGetFullName() {
        Assertions.assertEquals("Fattyacyls", LipidCategory.valueOf("FA").getFullName());
        Assertions.assertEquals("Glycerolipids", LipidCategory.valueOf("GL").getFullName());
        Assertions.assertEquals("Glycerophospholipids", LipidCategory.valueOf("GP").getFullName());
        Assertions.assertEquals("Polyketides", LipidCategory.valueOf("PK").getFullName());
        Assertions.assertEquals("Saccharolipids", LipidCategory.valueOf("SL").getFullName());
        Assertions.assertEquals("Sphingolipids", LipidCategory.valueOf("SP").getFullName());
        Assertions.assertEquals("Sterollipids", LipidCategory.valueOf("ST").getFullName());
        Assertions.assertEquals("Undefined lipid category", LipidCategory.valueOf("UNDEFINED").getFullName());
    }

    @Test
    public void testForFullName() {
        Assertions.assertSame(LipidCategory.FA, LipidCategory.forFullName("Fattyacyls"));
        Assertions.assertSame(LipidCategory.GL, LipidCategory.forFullName("Glycerolipids"));
        Assertions.assertSame(LipidCategory.GP, LipidCategory.forFullName("Glycerophospholipids"));
        Assertions.assertSame(LipidCategory.PK, LipidCategory.forFullName("Polyketides"));
        Assertions.assertSame(LipidCategory.SL, LipidCategory.forFullName("Saccharolipids"));
        Assertions.assertSame(LipidCategory.SP, LipidCategory.forFullName("Sphingolipids"));
        Assertions.assertSame(LipidCategory.ST, LipidCategory.forFullName("Sterollipids"));
        Assertions.assertSame(LipidCategory.UNDEFINED, LipidCategory.forFullName("UNDEFINED"));
    }

}

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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author nilshoffmann
 */
public class HeadGroupTest {

    @Test
    public void testGetNormalizedName() {
        HeadGroup hg = new HeadGroup("Cholesterol");
        assertEquals("ST 27:1;1", hg.getNormalizedName());
    }

    @Test
    public void testToString() {
        HeadGroup hg = new HeadGroup("BMP");
        assertEquals("BMP", hg.toString());
    }

    @Test
    public void testGetName() {
        HeadGroup hg = new HeadGroup("BMP");
        assertEquals("BMP", hg.getName());
    }

    @Test
    public void testGetRawName() {
        HeadGroup hg = new HeadGroup("Cholesterol");
        assertEquals("Cholesterol", hg.getRawName());
    }

    @Test
    public void testGetLipidClass() {
        HeadGroup hg = new HeadGroup("Cholesterol");
        assertEquals(LipidClass.ST_27_1_1, hg.getLipidClass());
    }

    @Test
    public void testGetLipidCategory() {
        HeadGroup hg = new HeadGroup("Cholesterol");
        assertEquals(LipidCategory.ST, hg.getLipidCategory());
    }

}

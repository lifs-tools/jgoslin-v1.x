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
public class AdductTest {

    @Test
    public void testNegativeAdduct() {
        Adduct a = new Adduct("", "-H", 1, -1);
        assertEquals(1, a.getPositiveElementaryCharge());
        assertEquals(-1, a.getCharge());
        assertEquals(-1, a.getChargeSign());
        assertEquals("[M-H]1-", a.getLipidString());
        assertEquals(-1, a.getElements().get(Element.ELEMENT_H));
        assertEquals("H", a.getElements().getSumFormula());
        assertEquals("-H", a.getAdductString());
        assertEquals(-Element.ELEMENT_H.getMass(), a.getElements().getMass());
    }

    @Test
    public void testPositiveAdduct() {
        Adduct a = new Adduct("", "+NH4", 1, 1);
        assertEquals(1, a.getCharge());
        assertEquals(1, a.getChargeSign());
        assertEquals("[M+NH4]1+", a.getLipidString());
        assertEquals(1, a.getElements().get(Element.ELEMENT_N));
        assertEquals(4, a.getElements().get(Element.ELEMENT_H));
        assertEquals("H4N", a.getElements().getSumFormula());
        assertEquals("+NH4", a.getAdductString());
        assertEquals(Element.ELEMENT_N.getMass() + (4 * (Element.ELEMENT_H.getMass())), a.getElements().getMass());
    }

}

/*
 * Copyright 2020  nils.hoffmann.
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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author  nils.hoffmann
 */
public class ElementTest {

    @Test
    public void testGetMass() {
        assertEquals(Element.ELEMENT_C.getMass(), 12.0, 1e-10);
        assertEquals(Element.ELEMENT_H.getMass(), 1.007825035, 1e-10);
        assertEquals(Element.ELEMENT_N.getMass(), 14.0030740, 1e-10);
        assertEquals(Element.ELEMENT_O.getMass(), 15.99491463, 1e-10);
        assertEquals(Element.ELEMENT_P.getMass(), 30.973762, 1e-10);
        assertEquals(Element.ELEMENT_S.getMass(), 31.9720707, 1e-10);
        assertEquals(Element.ELEMENT_H2.getMass(), 2.014101779, 1e-10);
        assertEquals(Element.ELEMENT_C13.getMass(), 13.0033548378, 1e-10);
        assertEquals(Element.ELEMENT_N15.getMass(), 15.0001088984, 1e-10);
        assertEquals(Element.ELEMENT_O17.getMass(), 16.9991315, 1e-10);
        assertEquals(Element.ELEMENT_O18.getMass(), 17.9991604, 1e-10);
        assertEquals(Element.ELEMENT_P32.getMass(), 31.973907274, 1e-10);
        assertEquals(Element.ELEMENT_S33.getMass(), 32.97145876, 1e-10);
        assertEquals(Element.ELEMENT_S34.getMass(), 33.96786690, 1e-10);
    }

    @Test
    public void testForName() {
        assertSame(Element.ELEMENT_C, Element.forName("C").get());
        assertSame(Element.ELEMENT_C, Element.forName("12C").get());
        assertSame(Element.ELEMENT_H, Element.forName("H").get());
        assertSame(Element.ELEMENT_H, Element.forName("1H").get());
        assertSame(Element.ELEMENT_N, Element.forName("N").get());
        assertSame(Element.ELEMENT_N, Element.forName("14N").get());
        assertSame(Element.ELEMENT_O, Element.forName("O").get());
        assertSame(Element.ELEMENT_O, Element.forName("15O").get());
        assertSame(Element.ELEMENT_P, Element.forName("P").get());
        assertSame(Element.ELEMENT_P, Element.forName("30P").get());
        assertSame(Element.ELEMENT_S, Element.forName("S").get());
        assertSame(Element.ELEMENT_S, Element.forName("31S").get());
        assertSame(Element.ELEMENT_H2, Element.forName("H'").get());
        assertSame(Element.ELEMENT_H2, Element.forName("2H").get());
        assertSame(Element.ELEMENT_C13, Element.forName("C'").get());
        assertSame(Element.ELEMENT_C13, Element.forName("13C").get());
        assertSame(Element.ELEMENT_N15, Element.forName("N'").get());
        assertSame(Element.ELEMENT_N15, Element.forName("15N").get());
        assertSame(Element.ELEMENT_O17, Element.forName("O'").get());
        assertSame(Element.ELEMENT_O17, Element.forName("17O").get());
        assertSame(Element.ELEMENT_O18, Element.forName("O''").get());
        assertSame(Element.ELEMENT_O18, Element.forName("18O").get());
        assertSame(Element.ELEMENT_P32, Element.forName("P'").get());
        assertSame(Element.ELEMENT_P32, Element.forName("32P").get());
        assertSame(Element.ELEMENT_S33, Element.forName("S'").get());
        assertSame(Element.ELEMENT_S33, Element.forName("33S").get());
        assertSame(Element.ELEMENT_S34, Element.forName("S''").get());
        assertSame(Element.ELEMENT_S34, Element.forName("34S").get());
    }
    
    @Test
    public void testForNameEmpty() {
        assertEquals(Optional.empty(), Element.forName("85P"));
    }
}


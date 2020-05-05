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

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author nilshoffmann
 */
public class ElementTableTest {

    @Test
    public void testEmptyElementTable() {
        ElementTable table = new ElementTable();
        assertEquals(0.0d, table.getMass(), 1e-10);
        Stream.of(Element.values()).forEach((e) -> {
            assertEquals(0.0d, table.getMass(e), 1e-10);
        });
    }

    @Test
    public void testElementTableIncrementBy() {
        double testMass = 
            (Element.ELEMENT_C.getMass() * 5) + 
            (Element.ELEMENT_H.getMass() * 21) + 
            (Element.ELEMENT_P.getMass() * 1) + 
            (Element.ELEMENT_O.getMass() * 4);
        ElementTable et = new ElementTable();
        et.incrementBy(Element.ELEMENT_C, 5);
        et.incrementBy(Element.ELEMENT_H, 21);
        et.incrementBy(Element.ELEMENT_P, 1);
        et.incrementBy(Element.ELEMENT_O, 4);
        assertEquals(testMass, et.getMass(), 1e-10);
    }
    
    @Test
    public void testElementTableIncrement() {
        double testMass = 
            (Element.ELEMENT_C.getMass() * 5) + 
            (Element.ELEMENT_H.getMass() * 21) + 
            (Element.ELEMENT_P.getMass() * 1) + 
            (Element.ELEMENT_O.getMass() * 4);
        ElementTable et = new ElementTable();
        IntStream.range(0, 5).forEach((value) -> {
            et.increment(Element.ELEMENT_C);
        });
        IntStream.range(0, 21).forEach((value) -> {
            et.increment(Element.ELEMENT_H);
        });
        IntStream.range(0, 1).forEach((value) -> {
            et.increment(Element.ELEMENT_P);
        });
        IntStream.range(0, 4).forEach((value) -> {
            et.increment(Element.ELEMENT_O);
        });
        assertEquals(testMass, et.getMass(), 1e-10);
    }

}

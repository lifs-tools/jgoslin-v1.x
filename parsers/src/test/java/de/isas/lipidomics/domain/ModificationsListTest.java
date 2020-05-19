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

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author nilshoffmann
 */
public class ModificationsListTest {

    @Test
    public void testSize() {
        ModificationsList ml = new ModificationsList();
        ml.add(Pair.of(2, "Ke"));
        assertEquals(1, ml.size());
    }

    @Test
    public void testIsEmpty() {
        ModificationsList ml = new ModificationsList();
        assertTrue(ml.isEmpty());
    }

    @Test
    public void testCountFor() {
        ModificationsList ml = new ModificationsList();
        ml.add(Pair.of(2, "Ke"));
        assertEquals(1, ml.countFor("Ke"));
    }

    @Test
    public void testCountForHydroxy() {
        ModificationsList ml = new ModificationsList();
        ml.add(Pair.of(2, "OH"));
        ml.add(Pair.of(5, "OH"));
        assertEquals(2, ml.countForHydroxy());
    }

}

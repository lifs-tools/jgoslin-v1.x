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

import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nils.hoffmann
 */
public class StructuralFattyAcidTest {

    @Test
    public void testGetNDoubleBonds() {
        StructuralFattyAcid instanceZero = new StructuralFattyAcid("FA1", 0, 2, 0, 0, false);
        assertEquals(0, instanceZero.getNDoubleBonds());
        StructuralFattyAcid instanceOne = new StructuralFattyAcid("FA1", 0, 2, 0, 1, false);
        assertEquals(1, instanceOne.getNDoubleBonds());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testGetNDoubleBondsException() {
        StructuralFattyAcid instanceZero = new StructuralFattyAcid("FA1", 0, 2, 0, -1, false);
    }

    @Test
    public void testGetName() {
        StructuralFattyAcid instance = new StructuralFattyAcid("FAX", 0, 2, 0, 0, false);
        assertEquals("FAX", instance.getName());
    }

    @Test
    public void testGetPosition() {
        StructuralFattyAcid instance = new StructuralFattyAcid("FAX", 1, 2, 0, 0, false);
        assertEquals(1, instance.getPosition());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testGetPositionException() {
        StructuralFattyAcid instanceZero = new StructuralFattyAcid("FA1", -2, 2, 0, 0, false);
    }

    @Test
    public void testGetNCarbon() {
        StructuralFattyAcid instance = new StructuralFattyAcid("FAX", 1, 2, 0, 0, false);
        assertEquals(2, instance.getNCarbon());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testGetNCarbonException() {
        StructuralFattyAcid instance = new StructuralFattyAcid("FAX", 1, 1, 0, 0, false);
    }

    @Test
    public void testGetNHydroxy() {
        StructuralFattyAcid instance = new StructuralFattyAcid("FAX", 1, 2, 1, 0, false);
        assertEquals(1, instance.getNHydroxy());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testGetNHydroxyException() {
        StructuralFattyAcid instance = new StructuralFattyAcid("FAX", 1, 2, -1, 0, false);
    }

}

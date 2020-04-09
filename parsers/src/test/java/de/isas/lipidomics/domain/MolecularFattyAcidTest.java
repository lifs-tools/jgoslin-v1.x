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
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nils.hoffmann
 */
public class MolecularFattyAcidTest {
    
     @Test
    public void testGetNDoubleBonds() {
        FattyAcid instanceZero = new FattyAcid("FA1", 0, 2, 0, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals(0, instanceZero.getNDoubleBonds());
        FattyAcid instanceOne = new FattyAcid("FA1", 0, 2, 0, 1, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals(1, instanceOne.getNDoubleBonds());
    }

    @Test
    public void testGetNDoubleBondsException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            FattyAcid instanceZero = new FattyAcid("FA1", 0, 2, 0, -1, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        });
    }

    @Test
    public void testGetName() {
        FattyAcid instance = new FattyAcid("FAX", 0, 2, 0, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals("FAX", instance.getName());
    }

    @Test
    public void testGetPosition() {
        FattyAcid instance = new FattyAcid("FAX", 1, 2, 0, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals(1, instance.getPosition());
    }

    @Test
    public void testGetPositionException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            FattyAcid instanceZero = new FattyAcid("FA1", -2, 2, 0, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        });
    }

    @Test
    public void testGetNCarbon() {
        FattyAcid instance = new FattyAcid("FAX", 1, 2, 0, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals(2, instance.getNCarbon());
    }

    @Test
    public void testGetNCarbonException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            FattyAcid instance = new FattyAcid("FAX", 1, -1, 0, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        });
    }

    @Test
    public void testGetNHydroxy() {
        FattyAcid instance = new FattyAcid("FAX", 1, 2, 1, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals(1, instance.getNHydroxy());
    }

    @Test
    public void testGetNHydroxyException() {
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            FattyAcid instance = new FattyAcid("FAX", 1, 2, -1, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        });
    }
}

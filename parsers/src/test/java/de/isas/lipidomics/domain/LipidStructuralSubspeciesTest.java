/*
 * Copyright 2019  nils.hoffmann.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nils.hoffmann
 */
public class LipidStructuralSubspeciesTest {

    @Test
    public void testGetLipidSpeciesString() {
        LipidStructuralSubspecies lss = new LipidStructuralSubspecies(
                new HeadGroup("PG"),
                new FattyAcid("FA1", 1, 8, 0, 1, LipidFaBondType.ESTER, false, ModificationsList.NONE),
                new FattyAcid("FA2", 2, 12, 1, 1, LipidFaBondType.ESTER, false, ModificationsList.NONE)
        );
        String expectedSpecies = "PG 20:2;1";
        assertEquals(expectedSpecies, lss.getLipidString(LipidLevel.SPECIES));
        assertEquals("FA1", lss.getFa().get("FA1").getName());
        assertEquals("FA2", lss.getFa().get("FA2").getName());
        String expectedMolSubSpecies = "PG 8:1-12:1;1";
        assertEquals(expectedMolSubSpecies, lss.getLipidString(LipidLevel.MOLECULAR_SUBSPECIES));
        String expectedStructuralSubSpecies = "PG 8:1/12:1;1";
        assertEquals(expectedStructuralSubSpecies, lss.getLipidString(LipidLevel.STRUCTURAL_SUBSPECIES));
    }

}

/*
 * Copyright 2019 nilshoffmann.
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
 * @author nilshoffmann
 */
public class LipidStructuralSubspeciesTest {

    @Test
    public void testGetLipidSpeciesString() {

        LipidStructuralSubspecies lss = new LipidStructuralSubspecies(
                "PG",
                new StructuralFattyAcid("FA1", -1, 8, 0, 1, LipidFaBondType.ESTER, false),
                new StructuralFattyAcid("FA2", -1, 12, 1, 1, LipidFaBondType.ESTER, false)
        );
        String expectedSpecies = "PG 20:2;1";
        assertEquals(expectedSpecies, lss.getLipidString(LipidLevel.SPECIES));
        String expectedMolSubSpecies = "PG 8:1_12:1;1";
        assertEquals(expectedMolSubSpecies, lss.getLipidString(LipidLevel.MOLECULAR_SUBSPECIES));
        String expectedStructuralSubSpecies = "PG 8:1/12:1;1";
        assertEquals(expectedStructuralSubSpecies, lss.getLipidString(LipidLevel.STRUCTURAL_SUBSPECIES));
    }

}

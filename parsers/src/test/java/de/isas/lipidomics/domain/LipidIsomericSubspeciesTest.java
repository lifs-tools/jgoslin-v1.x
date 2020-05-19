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

import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nilshoffmann
 */
public class LipidIsomericSubspeciesTest {

    @Test
    public void testGetLipidSpeciesString() {
        Map<Integer, String> fa1DbPos = new LinkedHashMap<>();
        fa1DbPos.put(5, "E");
        Map<Integer, String> fa2DbPos = new LinkedHashMap<>();
        fa2DbPos.put(9, "Z");
        LipidIsomericSubspecies lss = new LipidIsomericSubspecies(
                new HeadGroup("PG"),
                new FattyAcid("FA1", 1, 8, 0, LipidFaBondType.ESTER, false, ModificationsList.NONE, 1, fa1DbPos),
                new FattyAcid("FA2", 2, 12, 1, LipidFaBondType.ESTER, false, ModificationsList.NONE, 1, fa2DbPos)
        );
        assertEquals("FA1", lss.getFa().get("FA1").getName());
        assertEquals("FA2", lss.getFa().get("FA2").getName());
        LipidCategory expectedCategory = LipidCategory.GP;
        assertEquals(expectedCategory, lss.getLipidCategory());
        assertEquals(LipidClass.PG, lss.getLipidClass());
        String expectedSpecies = "PG 20:2;1";
        assertEquals(expectedSpecies, lss.getLipidString(LipidLevel.SPECIES));
        String expectedMolSubSpecies = "PG 8:1(5E)-12:1(9Z);1";
        assertEquals(expectedMolSubSpecies, lss.getLipidString(LipidLevel.MOLECULAR_SUBSPECIES));
        String expectedStructuralSubSpecies = "PG 8:1(5E)/12:1(9Z);1";
        assertEquals(expectedStructuralSubSpecies, lss.getLipidString(LipidLevel.STRUCTURAL_SUBSPECIES));
        String expectedIsomericSubSpecies = "PG 8:1(5E)/12:1(9Z);1";
        assertEquals(expectedIsomericSubSpecies, lss.getLipidString(LipidLevel.ISOMERIC_SUBSPECIES));
    }
}

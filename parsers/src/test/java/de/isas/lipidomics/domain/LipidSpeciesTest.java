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

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nils.hoffmann
 */
public class LipidSpeciesTest {

    @Test
    public void testGetLipidSpeciesString() {

        LipidSpecies lss = new LipidSpecies(
                "PG",
                Optional.of(new LipidSpeciesInfo(LipidLevel.SPECIES, 20, 1, 2, LipidFaBondType.ESTER))
        );
        String expectedSpecies = "PG 20:2;1";
        assertEquals(expectedSpecies, lss.getLipidString(LipidLevel.SPECIES));
        
        try {
            String expectedMolSubSpecies = "PG 8:1_12:1;1";
            assertEquals(expectedMolSubSpecies, lss.getLipidString(LipidLevel.MOLECULAR_SUBSPECIES));
        } catch(RuntimeException e) {
            //should fail, can not generate a structural sub species from the molecular species level
        }
        
        try {
            String expectedStructuralSubSpecies = "PG 8:1_12:1;1";
            assertEquals(expectedStructuralSubSpecies, lss.getLipidString(LipidLevel.STRUCTURAL_SUBSPECIES));
        } catch(RuntimeException e) {
            //should fail, can not generate a structural sub species from the molecular species level
        }
    }

}

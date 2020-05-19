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
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author nilshoffmann
 */
public class FattyAcidTest {

    @Test
    public void testBuildSubstructureName() {
        FattyAcid molFa = new FattyAcid("FA1", 5, 0, 1, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals("5:1", molFa.buildSubstructureName(LipidLevel.MOLECULAR_SUBSPECIES));
        FattyAcid strFa = new FattyAcid("FA1", 1, 12, 2, 1, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals("12:1;2", strFa.buildSubstructureName(LipidLevel.STRUCTURAL_SUBSPECIES));
        Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
        doubleBondPositions.put(9, "Z");
        FattyAcid isomFa = new FattyAcid("FA1", 1, 18, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE, 1, doubleBondPositions);
        assertEquals("18:1(9Z)", isomFa.buildSubstructureName(LipidLevel.ISOMERIC_SUBSPECIES));
    }

    @Test
    public void testGetElements() {
    }

    @Test
    public void testGetType() {
        FattyAcid molFa = new FattyAcid("FA1", 0, 0, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals(FattyAcidType.MOLECULAR, molFa.getType());
        FattyAcid strFa = new FattyAcid("FA1", 1, 0, 0, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals(FattyAcidType.STRUCTURAL, strFa.getType());
        Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
        doubleBondPositions.put(9, "Z");
        FattyAcid isomFa = new FattyAcid("FA1", 1, 18, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE, 1, doubleBondPositions);
        assertEquals(FattyAcidType.ISOMERIC, isomFa.getType());
    }

    @Test
    public void testGetName() {
        FattyAcid fa = new FattyAcid("FA1", 0, 0, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals("FA1", fa.getName());
    }

    @Test
    public void testGetPosition() {
        FattyAcid fa = new FattyAcid("FA1", 0, 0, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals(-1, fa.getPosition());
    }

    @Test
    public void testGetNCarbonNHydroxyNDoubleBonds() {
        FattyAcid fa = new FattyAcid("FA1", 5, 2, 1, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals(5, fa.getNCarbon());
        assertEquals(2, fa.getNHydroxy());
        assertEquals(1, fa.getNDoubleBonds());
    }

    @Test
    public void testGetLipidFaBondType() {
        FattyAcid fa = new FattyAcid("FA1", 5, 2, 1, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        assertEquals(LipidFaBondType.UNDEFINED, fa.getLipidFaBondType());
        fa = new FattyAcid("FA1", 5, 2, 1, LipidFaBondType.ETHER_PLASMANYL, false, ModificationsList.NONE);
        assertEquals(LipidFaBondType.ETHER_PLASMANYL, fa.getLipidFaBondType());
        fa = new FattyAcid("FA1", 5, 2, 1, LipidFaBondType.ETHER_PLASMENYL, false, ModificationsList.NONE);
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, fa.getLipidFaBondType());
        fa = new FattyAcid("FA1", 5, 2, 1, LipidFaBondType.ETHER_UNSPECIFIED, false, ModificationsList.NONE);
        assertEquals(LipidFaBondType.ETHER_UNSPECIFIED, fa.getLipidFaBondType());
        fa = new FattyAcid("FA1", 5, 2, 1, LipidFaBondType.ESTER, false, ModificationsList.NONE);
        assertEquals(LipidFaBondType.ESTER, fa.getLipidFaBondType());
    }

    @Test
    public void testIsLcb() {
        FattyAcid fa = new FattyAcid("LCB", 18, 3, 1, LipidFaBondType.UNDEFINED, true, ModificationsList.NONE);
        assertEquals(LipidFaBondType.UNDEFINED, fa.getLipidFaBondType());
        assertTrue(fa.isLcb());
        assertEquals(18, fa.getNCarbon());
        assertEquals(3, fa.getNHydroxy());
        assertEquals(1, fa.getNDoubleBonds());
    }

    @Test
    public void testGetModifications() {
        ModificationsList ml = new ModificationsList();
        ml.add(Pair.of(2, "OH"));
        FattyAcid fa = new FattyAcid("LCB", 18, 3, 1, LipidFaBondType.UNDEFINED, true, ml);
        assertEquals(LipidFaBondType.UNDEFINED, fa.getLipidFaBondType());
        assertTrue(fa.isLcb());
        assertEquals(18, fa.getNCarbon());
        assertEquals(3, fa.getNHydroxy());
        assertEquals(1, fa.getNDoubleBonds());
        assertEquals(1, fa.getModifications().size());
        assertEquals(1, fa.getModifications().countFor("OH"));
        assertEquals(2, fa.getModifications().get(0).getKey());
    }

    @Test
    public void testIsomericFattyAcidBuilder() {
        Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
        doubleBondPositions.put(9, "E");
        FattyAcid fa = new FattyAcid("FA1", 1, 18, 0, LipidFaBondType.ETHER_PLASMENYL, false, ModificationsList.NONE, 1, doubleBondPositions);
        assertEquals(FattyAcidType.ISOMERIC, fa.getType());
        assertEquals(18, fa.getNCarbon());
        assertEquals(0, fa.getNHydroxy());
        assertEquals(1, fa.getNDoubleBonds());
        assertFalse(fa.isLcb());
        assertEquals(LipidFaBondType.ESTER, fa.getLipidFaBondType());
        assertEquals("FA1", fa.getName());
        assertEquals(ModificationsList.NONE, fa.getModifications());
        assertEquals(1, fa.getDoubleBondPositions().size());
        assertEquals("E", fa.getDoubleBondPositions().get(9));
    }

    @Test
    public void testStructuralFattyAcidBuilder() {
        FattyAcid fa = FattyAcid.structuralFattyAcidBuilder().name("FA1").position(1).nCarbon(5).nHydroxy(2).nDoubleBonds(1).
                lcb(false).lipidFaBondType(LipidFaBondType.ESTER).modifications(ModificationsList.NONE).build();
        assertEquals(FattyAcidType.STRUCTURAL, fa.getType());
        assertEquals(5, fa.getNCarbon());
        assertEquals(2, fa.getNHydroxy());
        assertEquals(1, fa.getNDoubleBonds());
        assertFalse(fa.isLcb());
        assertEquals(LipidFaBondType.ESTER, fa.getLipidFaBondType());
        assertEquals("FA1", fa.getName());
        assertEquals(ModificationsList.NONE, fa.getModifications());
    }

    @Test
    public void testMolecularFattyAcidBuilder() {
        FattyAcid fa = FattyAcid.molecularFattyAcidBuilder().name("FA1").nCarbon(5).nHydroxy(2).nDoubleBonds(1).lcb(false).lipidFaBondType(LipidFaBondType.ETHER_PLASMANYL).modifications(ModificationsList.NONE).build();
        assertEquals(FattyAcidType.MOLECULAR, fa.getType());
        assertEquals(5, fa.getNCarbon());
        assertEquals(2, fa.getNHydroxy());
        assertEquals(1, fa.getNDoubleBonds());
        assertFalse(fa.isLcb());
        assertEquals(LipidFaBondType.ETHER_PLASMANYL, fa.getLipidFaBondType());
        assertEquals("FA1", fa.getName());
        assertEquals(ModificationsList.NONE, fa.getModifications());
    }

}

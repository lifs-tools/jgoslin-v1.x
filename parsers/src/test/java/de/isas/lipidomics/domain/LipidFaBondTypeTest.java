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

import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author nilshoffmann
 */
public class LipidFaBondTypeTest {

    @Test
    public void testEtherHeadGroup() {
        String etherHeadGroup1 = "PE O";
        String etherHeadGroup2 = "PE-O";
        assertEquals(LipidFaBondType.ETHER_UNSPECIFIED, LipidFaBondType.getLipidFaBondType(etherHeadGroup1));
        assertEquals(LipidFaBondType.ETHER_UNSPECIFIED, LipidFaBondType.getLipidFaBondType(etherHeadGroup2));
    }

    @Test
    public void testEtherHeadGroupAndFaEtherUnspecifiedOrUndefinedThrowException() {
        String etherHeadGroup1 = "PE O";
        assertEquals(LipidFaBondType.ETHER_UNSPECIFIED, LipidFaBondType.getLipidFaBondType(etherHeadGroup1));
        assertEquals(LipidFaBondType.ETHER_UNSPECIFIED, LipidFaBondType.getLipidFaBondType(etherHeadGroup1, FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_UNSPECIFIED).build()));
        Assertions.assertThrows(ConstraintViolationException.class, () -> { // should raise exception, fa bond type must not be UNDEFINED!
            assertEquals(LipidFaBondType.UNDEFINED, LipidFaBondType.getLipidFaBondType(etherHeadGroup1, FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.UNDEFINED).build()));
        });
    }

    @Test
    public void testEtherHeadGroupOverruledBySpecificFaEther() {
        String etherHeadGroup1 = "PE O";
        assertEquals(LipidFaBondType.ETHER_UNSPECIFIED, LipidFaBondType.getLipidFaBondType(etherHeadGroup1));
        assertEquals(LipidFaBondType.ETHER_PLASMANYL, LipidFaBondType.getLipidFaBondType(etherHeadGroup1, FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_PLASMANYL).build()));
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, LipidFaBondType.getLipidFaBondType(etherHeadGroup1, FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_PLASMENYL).build()));
    }

    @Test
    public void testEtherHeadGroupDominatesFaEster() {
        String etherHeadGroup1 = "PE O";
        assertEquals(LipidFaBondType.ETHER_UNSPECIFIED, LipidFaBondType.getLipidFaBondType(etherHeadGroup1));
        assertEquals(LipidFaBondType.ETHER_UNSPECIFIED, LipidFaBondType.getLipidFaBondType(etherHeadGroup1, FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ESTER).build()));
    }
    
    @Test
    public void testNormalHeadGroupOverruledByFaEther() {
        String etherHeadGroup1 = "PE";
        assertEquals(LipidFaBondType.UNDEFINED, LipidFaBondType.getLipidFaBondType(etherHeadGroup1));
        assertEquals(LipidFaBondType.ETHER_PLASMANYL, LipidFaBondType.getLipidFaBondType(etherHeadGroup1, FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_PLASMANYL).build()));
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, LipidFaBondType.getLipidFaBondType(etherHeadGroup1, FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_PLASMENYL).build()));
    }
    
    @Test
    public void testNormalHeadGroupAndFaEster() {
        String etherHeadGroup1 = "PE";
        assertEquals(LipidFaBondType.UNDEFINED, LipidFaBondType.getLipidFaBondType(etherHeadGroup1));
        assertEquals(LipidFaBondType.ESTER, LipidFaBondType.getLipidFaBondType(etherHeadGroup1, FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ESTER).build()));
    }
    
    @Test
    public void testNormalHeadGroupAndFaEtherAndEster() {
        String etherHeadGroup1 = "PE";
        assertEquals(LipidFaBondType.UNDEFINED, LipidFaBondType.getLipidFaBondType(etherHeadGroup1));
        assertEquals(LipidFaBondType.ETHER_PLASMANYL, 
                LipidFaBondType.getLipidFaBondType(etherHeadGroup1, 
                        FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_PLASMANYL).build(),
                        FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ESTER).build()
                )
        );
        assertEquals(LipidFaBondType.ETHER_PLASMANYL, 
                LipidFaBondType.getLipidFaBondType(etherHeadGroup1, 
                        FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ESTER).build(),
                        FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_PLASMANYL).build()
                )
        );
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, 
                LipidFaBondType.getLipidFaBondType(etherHeadGroup1, 
                        FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_PLASMENYL).build(),
                        FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ESTER).build()
                )
        );
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, 
                LipidFaBondType.getLipidFaBondType(etherHeadGroup1, 
                        FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ESTER).build(),
                        FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_PLASMENYL).build()
                )
        );
        Assertions.assertThrows(ConstraintViolationException.class, () -> { // should raise exception, fa bond type must not be UNDEFINED!
            assertEquals(LipidFaBondType.ETHER_PLASMENYL, 
                LipidFaBondType.getLipidFaBondType(etherHeadGroup1, 
                        FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_PLASMANYL).build(),
                        FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_PLASMENYL).build()
                )
            );
            assertEquals(LipidFaBondType.ETHER_PLASMENYL, 
                LipidFaBondType.getLipidFaBondType(etherHeadGroup1, 
                        FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_PLASMENYL).build(),
                        FattyAcid.structuralFattyAcidBuilder().lipidFaBondType(LipidFaBondType.ETHER_PLASMANYL).build()
                )
            );
        });
    }
}

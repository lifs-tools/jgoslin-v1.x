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
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.palinom.goslin.GoslinVisitorParser;
import de.isas.lipidomics.palinom.lipidmaps.LipidMapsVisitorParser;
import de.isas.lipidomics.palinom.sumformula.SumFormulaVisitorParser;
import de.isas.lipidomics.palinom.swisslipids.SwissLipidsVisitorParser;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nilshoffmann
 */
public class LipidAdductTest {

    @Test
    public void testCalculateMass() throws ParsingException {
        double expectedMass = 673.4813805;
        String lipidAdduct = "PA 16:0-18:1[M-H]1-";
        GoslinVisitorParser gvp = new GoslinVisitorParser();
        LipidAdduct la = gvp.parse(lipidAdduct);
        assertEquals(expectedMass, la.getMass(), 1e-6);
    }

    @Test
    public void testCalculateSumFormulaWithoutAdduct() throws ParsingException {
        double expectedMass = 674.488656244;
        String expectedSumFormula = "C37H71O8P";
        SumFormulaVisitorParser sp = new SumFormulaVisitorParser();
        ElementTable et = sp.parse(expectedSumFormula);
        assertEquals(expectedSumFormula, et.getSumFormula());
        assertEquals(expectedMass, et.getMass(), 1e-6);

        String lipidAdduct = "PA 16:0-18:1";
        GoslinVisitorParser gvp = new GoslinVisitorParser();
        LipidAdduct la = gvp.parse(lipidAdduct);
        assertEquals(expectedSumFormula, la.getSumFormula());
        assertEquals(expectedMass, la.getMass(), 1e-6);
    }

    @Test
    public void testSumFormulaForPECerHydroxy() throws ParsingException {
        String lipidAdduct = "PE-Cer(d14:1(4E)/24:0)";
        String expectedSumFormula = "C40H81N2O6P";
        double expectedMass = 716.583225;
        SwissLipidsVisitorParser slp = new SwissLipidsVisitorParser();
        LipidAdduct la = slp.parse(lipidAdduct);
        assertEquals(expectedSumFormula, la.getSumFormula());
        assertEquals(expectedMass, la.getMass(), 1e-6);
    }

    @Test
    public void testSumFormulaForDGPlasmenyl() throws ParsingException {
        String lipidAdduct = "DG(P-14:0/22:4/0:0)";
        String expectedSumFormula = "C39H68O4"; // C39H68O4
        double expectedMass = 600.51178;
        LipidAdduct la = new SwissLipidsVisitorParser().parse(lipidAdduct);
        assertEquals(14, la.getLipid().getFa().get("FA1").getNCarbon());
        assertEquals(1, la.getLipid().getFa().get("FA1").getNDoubleBonds());
        assertEquals(22, la.getLipid().getFa().get("FA2").getNCarbon());
        assertEquals(4, la.getLipid().getFa().get("FA2").getNDoubleBonds());
        assertEquals(0, la.getLipid().getFa().get("FA3").getNCarbon());
        assertEquals(0, la.getLipid().getFa().get("FA3").getNDoubleBonds());
        assertEquals(expectedSumFormula, la.getSumFormula());
        assertEquals(expectedMass, la.getMass(), 1e-4);
    }

    @Test
    public void testSumFormulaForDGPlasmanyl() throws ParsingException {
        String lipidAdduct = "DG(O-20:1(11Z)/0:0/16:2(9Z,12Z))";
        String expectedSumFormula = "C39H72O4";
        LipidAdduct la = new SwissLipidsVisitorParser().parse(lipidAdduct);
        assertEquals(expectedSumFormula, la.getSumFormula());
    }

    @Test
    public void testSumFormulaForPCPlasmenyl() throws ParsingException {
        String lipidAdduct = "PC(P-13:0/24:1(15Z))";
        String expectedSumFormula = "C45H88NO7P";
        LipidAdduct la = new SwissLipidsVisitorParser().parse(lipidAdduct);
        assertEquals(expectedSumFormula, la.getSumFormula());
    }

    @Test
    public void testSumFormulaForSE() throws ParsingException {
        String lipidAdduct = "SE(27:1/10:0)";
        String expectedSumFormula = "C37H64O2"; //fa_2
        LipidAdduct la = new SwissLipidsVisitorParser().parse(lipidAdduct);
        assertEquals(expectedSumFormula, la.getSumFormula());
    }
    
    @Test
    public void testSumFormulaForSESpecies() throws ParsingException {
        String lipidAdduct = "SE(43:2)";
        String expectedSumFormula = "C43H74O2";
        LipidAdduct la = new SwissLipidsVisitorParser().parse(lipidAdduct);
        assertEquals(expectedSumFormula, la.getSumFormula());
        assertEquals("SE 27:1/16:1", la.getNormalizedLipidString(LipidLevel.SPECIES));
        assertEquals("SE 27:1/16:1", la.getLipidString(LipidLevel.SPECIES));
        
        lipidAdduct = "SE(27:1/16:1)";
        la = new SwissLipidsVisitorParser().parse(lipidAdduct);
        assertEquals(expectedSumFormula, la.getSumFormula());
        assertEquals("SE 27:1/16:1", la.getNormalizedLipidString(LipidLevel.STRUCTURAL_SUBSPECIES));
        assertEquals("SE 27:1/16:1", la.getNormalizedLipidString(LipidLevel.SPECIES));
    }

    @Test
    public void testSumFormulaForDG() throws ParsingException {
        String lipidAdduct = "DG(14:1_18:3)";
        String expectedSumFormula = "C35H60O5";
        LipidAdduct la = new SwissLipidsVisitorParser().parse(lipidAdduct);
        assertEquals(expectedSumFormula, la.getSumFormula());
    }

    @Test
    public void testSumFormulaForCer() throws ParsingException {
        String lipidAdduct = "Cer(d32:0)";
        String expectedSumFormula = "C32H65NO3"; // 2H
        double expectedMass = 511.496;
        LipidAdduct la = new SwissLipidsVisitorParser().parse(lipidAdduct);
        assertEquals(LipidLevel.SPECIES, la.getLipid().getInfo().get().getLevel());
        assertEquals(true, la.getLipid().getInfo().get().isLcb());
        assertEquals(expectedSumFormula, la.getSumFormula());
        assertEquals(expectedMass, la.getMass(), 1e-3);

        //lipid maps
        lipidAdduct = "Cer(d18:1(8Z)/16:0(2OH[R]))"; //1 O
        expectedSumFormula = "C34H67NO4";

        lipidAdduct = "PE-Cer(d15:1(4E)/22:0(2OH))"; // 1P 
        expectedSumFormula = "C39H79N2O7P";
    }

    @Test
    public void testCalculateSumFormula() throws ParsingException {
        String expectedSumFormula = "C37H70O8P";
        String lipidAdduct = "PA 16:0-18:1[M-H]1-";
        GoslinVisitorParser gvp = new GoslinVisitorParser();
        LipidAdduct la = gvp.parse(lipidAdduct);
        assertEquals(expectedSumFormula, la.getSumFormula());

        expectedSumFormula = "C53H93NO10P"; // this is the sum formula of M+CH3COO
        // the M sum formula is C51H90NO8P
        lipidAdduct = "PC 21:0-22:6[M+CH3COO]1-";
        la = gvp.parse(lipidAdduct);
        assertEquals(expectedSumFormula, la.getSumFormula());
        assertEquals(934.6542591, la.getMass(), 1e-6);

        expectedSumFormula = "C51H91NO8P"; // sum formula for M+H
        la = gvp.parse("PC 21:0-22:6[M+H]1+");
        assertEquals(expectedSumFormula, la.getSumFormula());
        assertEquals(876.6476822, la.getMass(), 1e-6);
    }
    
    @Test
    public void testGlcCerModSumFormula() throws ParsingException {
        String ref = "GlcCer(d18:2(4E,8Z)/16:0(2OH[R]))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = new LipidMapsVisitorParser().parse(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("GlcCer", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals("HexCer 18:2(4E,8Z);2/16:0;1(2OH[R])", lipidAdduct.getNormalizedLipidString());
        assertEquals(LipidCategory.SP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
//        assertEquals("HexCer 18:2(4E,8Z);2/16:0;1", lipidAdduct.getNormalizedLipidString());
        assertNotNull(lipidAdduct.getLipid());
        assertNotNull(lipidAdduct.getElements());
        assertNotNull(lipidAdduct.getSumFormula());
        assertNotNull(lipidAdduct.getMass());
        assertEquals("C40H75NO9", lipidAdduct.getSumFormula());
        assertEquals(713.544, lipidAdduct.getMass(), 1e-3);
    }
    
    @Test
    public void testPip3SumFormula() throws ParsingException {
        String ref = "PIP3[3',4',5'](17:0/20:4(5Z,8Z,11Z,14Z))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = new LipidMapsVisitorParser().parse(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PIP3[3',4',5']", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals("PIP3[3',4',5'] 17:0/20:4(5Z,8Z,11Z,14Z)", lipidAdduct.getNormalizedLipidString());
        assertEquals(LipidCategory.GP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
//        assertNotNull(lipidAdduct.getLipid());
//        assertNotNull(lipidAdduct.getElements());
//        assertNotNull(lipidAdduct.getSumFormula());
//        assertNotNull(lipidAdduct.getMass());
        assertEquals(1112.44, lipidAdduct.getMass(), 1e-3);
        assertEquals("C46H84O22P4", lipidAdduct.getSumFormula());
    }
    
    @Test
    public void testDGDGSumFormula() throws ParsingException {
        String ref = "MGDG(18:0(9Z)/18:2(9Z,12Z))";
        String refSumFormula = "C45H82O10";
        System.out.println("Testing lipid name " + ref);
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            LipidAdduct lipidAdduct = new LipidMapsVisitorParser().parse(ref);
        });
//        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
//        assertEquals(LipidCategory.GL, lipidAdduct.getLipid().getLipidCategory());
//        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
//        assertEquals(refSumFormula, lipidAdduct.getSumFormula());
//        assertEquals(782.591, lipidAdduct.getMass(), 1e-3);
    }
}

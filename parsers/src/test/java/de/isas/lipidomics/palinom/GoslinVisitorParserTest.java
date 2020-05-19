/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.palinom.goslin.GoslinVisitorParser;
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.domain.Adduct;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidCategory;
import de.isas.lipidomics.domain.LipidClass;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nils.hoffmann
 */
public class GoslinVisitorParserTest {

    @Test
    public void LPCWithExtraLetters() throws ParsingException {
        String ref = "LPC 18:1(9Z)/20a1:2(9Z,12E)";
        System.out.println("Testing lipid name " + ref);
        Assertions.assertThrows(ParsingException.class, () -> {
            LipidAdduct lipidAdduct = parseLipidName(ref);
        });
    }

    @Test
    public void testPE_Structural() throws ParsingException {
        String ref = "PE 18:1/18:1(11Z)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA1").getNDoubleBonds());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA2").getNDoubleBonds());
        assertEquals(ref, lipidAdduct.getLipid().getLipidString(LipidLevel.STRUCTURAL_SUBSPECIES));
    }

    @Test
    public void testPE_Isomeric() throws ParsingException {
        String ref = "PE 18:0/18:1(11Z)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(0, lipidAdduct.getLipid().getFa().get("FA1").getNDoubleBonds());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA2").getNDoubleBonds());
        assertEquals(ref, lipidAdduct.getLipid().getLipidString(LipidLevel.ISOMERIC_SUBSPECIES));
    }

    @Test
    public void testPE_O() throws ParsingException {
        String ref = "PE O-18:0a/16:2;1";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.GP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals("PE O-34:2;1a", lipidAdduct.getLipid().getLipidString(LipidLevel.SPECIES));
        assertEquals("PE O-18:0a/16:2;1", lipidAdduct.getLipid().toString());
    }

    @Test
    public void testCh() throws ParsingException {
        String ref = "Ch";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(ref, lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.ST, lipidAdduct.getLipid().getLipidCategory());
    }

    @Test
    public void testChE() throws ParsingException {
        String ref = "ChE 12:1";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("ChE", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.ST, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(12, lipidAdduct.getLipid().getInfo().getNCarbon());
        assertEquals(1, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNHydroxy());
    }

    @Test
    public void testMediators() throws ParsingException {
        String ref1 = "11,12-DHET";
        String ref2 = "5(6)-EET";
        System.out.println("Testing lipid name " + ref1);
        LipidAdduct lipidAdduct = parseLipidName(ref1);
        assertNotNull(lipidAdduct);
        System.out.println(lipidAdduct);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(ref1, lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.FA, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
//        assertEquals("",lipidAdduct.getAdduct().getAdductString());
//        assertEquals(Integer.valueOf(1),lipidAdduct.getAdduct().getCharge());
//        assertEquals(Integer.valueOf(1),lipidAdduct.getAdduct().getChargeSign());
    }

    @Test
    public void testPL_adduct() throws ParsingException {
        String ref = "PE 18:3;1-16:2";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        System.out.println(lipidAdduct);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());

        String ref2 = ref + " [M+H]1+";
        System.out.println("Testing lipid name " + ref2);
        LipidAdduct lipidAdduct2 = parseLipidName(ref2);
        assertNotNull(lipidAdduct2);
        System.out.println(lipidAdduct2);
        assertEquals(new Adduct("", "+H", 1, 1), lipidAdduct2.getAdduct());
        assertEquals(LipidLevel.MOLECULAR_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
    }

    @Test
    public void testPL_hyphen() throws ParsingException {

        String ref = "PE 18:3;1-16:2";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidMolecularSubspecies lipid = LipidMolecularSubspecies.class.cast(lipidAdduct.getLipid());
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals("PE", lipid.getHeadGroup().getName());
        assertEquals("FA1", lipid.getFa().
                get("FA1").
                getName());
        assertEquals(18, lipid.getFa().
                get("FA1").
                getNCarbon());
        assertEquals(3, lipid.getFa().
                get("FA1").
                getNDoubleBonds());
        assertEquals(1, lipid.getFa().
                get("FA1").
                getNHydroxy());
        assertEquals("FA2", lipid.getFa().
                get("FA2").
                getName());
        assertEquals(16, lipid.getFa().
                get("FA2").
                getNCarbon());
        assertEquals(2, lipid.getFa().
                get("FA2").
                getNDoubleBonds());
        assertEquals(0, lipid.getFa().
                get("FA2").
                getNHydroxy());
        assertEquals(LipidLevel.MOLECULAR_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
    }

    @Test
    public void testLysoPL() throws ParsingException {
        String ref1 = "LPE 18:0";
        System.out.println("Testing lysolipid name " + ref1);
        LipidAdduct lipidAdduct = parseLipidName(ref1);
        assertNotNull(lipidAdduct);
        LipidMolecularSubspecies lipid1 = (LipidMolecularSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid1);
        System.out.println(lipid1);
        assertEquals("LPE", lipid1.getHeadGroup().getName());
        assertEquals("FA1", lipid1.getFa().
                get("FA1").
                getName());
        assertEquals(18, lipid1.getFa().
                get("FA1").
                getNCarbon());
        assertEquals(0, lipid1.getFa().
                get("FA1").
                getNDoubleBonds());
        assertEquals(0, lipid1.getFa().
                get("FA1").
                getNHydroxy());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
    }

    @Test
    public void testImplicitLyso() throws ParsingException {
//        assertThrows(ConstraintViolationException.class, () -> {
        String ref2 = "PE 18:0-0:0";
        System.out.println("Testing implicit lysolipid name " + ref2);
        LipidAdduct lipidAdduct = parseLipidName(ref2);
        assertNotNull(lipidAdduct);
        LipidMolecularSubspecies lipid2 = (LipidMolecularSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid2);
        System.out.println(lipid2);
        assertEquals("PE", lipid2.getHeadGroup().getName());
        assertEquals("FA1", lipid2.getFa().
                get("FA1").
                getName());
        assertEquals(18, lipid2.getFa().
                get("FA1").
                getNCarbon());
        assertEquals(0, lipid2.getFa().
                get("FA1").
                getNDoubleBonds());
        assertEquals(0, lipid2.getFa().
                get("FA1").
                getNHydroxy());
        assertEquals(2, lipid2.getFa().size());
        assertEquals("FA2", lipid2.getFa().
                get("FA2").
                getName());
        assertEquals(0, lipid2.getFa().
                get("FA2").
                getNCarbon());
        assertEquals(0, lipid2.getFa().
                get("FA2").
                getNDoubleBonds());
        assertEquals(0, lipid2.getFa().
                get("FA2").
                getNHydroxy());
        assertEquals(LipidLevel.MOLECULAR_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
    }

    @Test
    public void testPL_slash() throws ParsingException {
        String ref = "PE 18:3;1/16:2";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals("PE", lipid.getHeadGroup().getName());
        assertEquals("FA1", lipid.getFa().
                get("FA1").
                getName());
        assertEquals(18, lipid.getFa().
                get("FA1").
                getNCarbon());
        assertEquals(3, lipid.getFa().
                get("FA1").
                getNDoubleBonds());
        assertEquals(1, lipid.getFa().
                get("FA1").
                getNHydroxy());
        assertEquals("FA2", lipid.getFa().
                get("FA2").
                getName());
        assertEquals(16, lipid.getFa().
                get("FA2").
                getNCarbon());
        assertEquals(2, lipid.getFa().
                get("FA2").
                getNDoubleBonds());
        assertEquals(0, lipid.getFa().
                get("FA2").
                getNHydroxy());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
    }

    @Test
    public void testPL_Pe_adduct() throws ParsingException {
        String ref = "PE 16:1/12:0[M+H]1+";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals("PE", lipid.getHeadGroup().getName());
        assertEquals("FA1", lipid.getFa().
                get("FA1").
                getName());
        assertEquals(16, lipid.getFa().
                get("FA1").
                getNCarbon());
        assertEquals(1, lipid.getFa().
                get("FA1").
                getNDoubleBonds());
        assertEquals(0, lipid.getFa().
                get("FA1").
                getNHydroxy());
        assertEquals(LipidFaBondType.ESTER, lipid.getFa().
                get("FA1").
                getLipidFaBondType());
        assertFalse(lipid.getFa().
                get("FA1").
                isLcb());
        assertEquals("FA2", lipid.getFa().
                get("FA2").
                getName());
        assertEquals(12, lipid.getFa().
                get("FA2").
                getNCarbon());
        assertEquals(0, lipid.getFa().
                get("FA2").
                getNDoubleBonds());
        assertEquals(0, lipid.getFa().
                get("FA2").
                getNHydroxy());
        assertEquals("[M+H]1+", lipidAdduct.
                getAdduct().
                getLipidString());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
    }

    @Test
    public void testPL_Plasmanyl_slash() throws ParsingException {
        String ref = "PE O-18:3;1a/16:2";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals("PE", lipid.getHeadGroup().getName());
        assertEquals("FA1", lipid.getFa().
                get("FA1").
                getName());
        assertEquals(18, lipid.getFa().
                get("FA1").
                getNCarbon());
        assertEquals(3, lipid.getFa().
                get("FA1").
                getNDoubleBonds());
        assertEquals(1, lipid.getFa().
                get("FA1").
                getNHydroxy());
        assertEquals(LipidFaBondType.ETHER_PLASMANYL, lipid.getFa().
                get("FA1").
                getLipidFaBondType());
        assertFalse(lipid.getFa().
                get("FA1").
                isLcb());
        assertEquals("FA2", lipid.getFa().
                get("FA2").
                getName());
        assertEquals(16, lipid.getFa().
                get("FA2").
                getNCarbon());
        assertEquals(2, lipid.getFa().
                get("FA2").
                getNDoubleBonds());
        assertEquals(0, lipid.getFa().
                get("FA2").
                getNHydroxy());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
    }

    @Test
    public void testPC_Species_Ether() throws ParsingException {
        String ref = "PC O-34:1";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidSpecies lipid = (LipidSpecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals("PC", lipid.getHeadGroup().getName());
        assertTrue(lipid.isEtherLipid());
        assertEquals(LipidLevel.SPECIES, lipid.getInfo().getLevel());
        assertEquals(LipidFaBondType.ETHER_UNSPECIFIED, lipid.getInfo().getLipidFaBondType());
        assertEquals(ref, lipid.toString());
    }

    @Test
    public void testLPC_Species_Ether() throws ParsingException {
        String ref = "LPC O-16:0";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidSpecies lipid = (LipidSpecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals("LPC", lipid.getHeadGroup().getName());
        assertTrue(lipid.isEtherLipid());
        assertEquals(LipidFaBondType.ETHER_UNSPECIFIED, lipid.getInfo().getLipidFaBondType());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipid.getInfo().getLevel());
        assertEquals(ref, lipid.toString());
        assertEquals(ref, lipid.getLipidString(LipidLevel.SPECIES));
        assertEquals(ref, lipid.getLipidString(LipidLevel.STRUCTURAL_SUBSPECIES));
        assertEquals(ref, lipid.getLipidString(LipidLevel.MOLECULAR_SUBSPECIES));
        assertEquals(ref, lipid.getLipidString(LipidLevel.ISOMERIC_SUBSPECIES));
        assertEquals(ref, lipid.getLipidString());
    }

    @Test
    public void testPE_Plasmenyl_StructuralSpecies() throws ParsingException {
        String ref = "PE O-16:1p/18:1";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, lipid.getInfo().getLipidFaBondType());
        System.out.println(lipid);
        assertEquals(LipidCategory.GP, lipid.getLipidCategory());
        assertEquals(LipidClass.PE, lipid.getLipidClass());
        assertEquals("PE", lipid.getHeadGroup().getName());
        assertEquals("FA1", lipid.getFa().
                get("FA1").
                getName());
        assertEquals(16, lipid.getFa().
                get("FA1").
                getNCarbon());
        assertEquals(1, lipid.getFa().
                get("FA1").
                getNDoubleBonds());
        assertEquals(0, lipid.getFa().
                get("FA1").
                getNHydroxy());
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, lipid.getFa().
                get("FA1").
                getLipidFaBondType());
        assertFalse(lipid.getFa().
                get("FA1").
                isLcb());
        assertEquals("FA2", lipid.getFa().
                get("FA2").
                getName());
        assertEquals(18, lipid.getFa().
                get("FA2").
                getNCarbon());
        assertEquals(1, lipid.getFa().
                get("FA2").
                getNDoubleBonds());
        assertEquals(0, lipid.getFa().
                get("FA2").
                getNHydroxy());
        assertEquals(LipidFaBondType.ESTER, lipid.getFa().
                get("FA2").
                getLipidFaBondType());
    }

    @Test
    public void testPL_Plasmenyl_slash() throws ParsingException {
        String ref = "PE O 18:3;1p/16:2;1";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, lipid.getInfo().getLipidFaBondType());
        System.out.println(lipid);
        assertEquals(LipidCategory.GP, lipid.getLipidCategory());
        assertEquals(LipidClass.PE, lipid.getLipidClass());
        assertEquals("PE", lipid.getHeadGroup().getName());
        assertEquals("FA1", lipid.getFa().
                get("FA1").
                getName());
        assertEquals(18, lipid.getFa().
                get("FA1").
                getNCarbon());
        assertEquals(3, lipid.getFa().
                get("FA1").
                getNDoubleBonds());
        assertEquals(1, lipid.getFa().
                get("FA1").
                getNHydroxy());
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, lipid.getFa().
                get("FA1").
                getLipidFaBondType());
        assertFalse(lipid.getFa().
                get("FA1").
                isLcb());
        assertEquals("FA2", lipid.getFa().
                get("FA2").
                getName());
        assertEquals(16, lipid.getFa().
                get("FA2").
                getNCarbon());
        assertEquals(2, lipid.getFa().
                get("FA2").
                getNDoubleBonds());
        assertEquals(1, lipid.getFa().
                get("FA2").
                getNHydroxy());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
    }

    @Test
    public void testTag() throws ParsingException {
        String ref = "TAG 14:0_16:0_18:1";
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidMolecularSubspecies lipid = (LipidMolecularSubspecies) lipidAdduct.getLipid();
        System.out.println(lipid);
        assertEquals(3, lipid.getFa().size());
        assertEquals(14, lipid.getFa().get("FA1").getNCarbon());
        assertEquals(0, lipid.getFa().get("FA1").getNDoubleBonds());
        assertEquals(0, lipid.getFa().get("FA1").getNHydroxy());

        assertEquals(16, lipid.getFa().get("FA2").getNCarbon());
        assertEquals(0, lipid.getFa().get("FA2").getNDoubleBonds());
        assertEquals(0, lipid.getFa().get("FA2").getNHydroxy());

        assertEquals(18, lipid.getFa().get("FA3").getNCarbon());
        assertEquals(1, lipid.getFa().get("FA3").getNDoubleBonds());
        assertEquals(0, lipid.getFa().get("FA3").getNHydroxy());

        assertEquals("TAG 14:0-16:0-18:1", lipid.getLipidString(LipidLevel.MOLECULAR_SUBSPECIES));
        assertEquals(LipidCategory.GL, lipid.getLipidCategory());
        assertEquals(LipidClass.TAG, lipid.getLipidClass());
    }

    @Test
    public void testIsomericSubspecies() throws ParsingException {
        String ref = "TAG 16:0/20:2(11Z,14Z)/22:4(7Z,10Z,13Z,16Z)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("TAG", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals("GL", lipidAdduct.getLipidString(LipidLevel.CATEGORY));
        assertEquals("TAG", lipidAdduct.getLipidString(LipidLevel.CLASS));
        assertEquals("TAG 58:6", lipidAdduct.getLipidString(LipidLevel.SPECIES));
        assertEquals("TAG 16:0-20:2(11Z,14Z)-22:4(7Z,10Z,13Z,16Z)", lipidAdduct.getLipidString(LipidLevel.MOLECULAR_SUBSPECIES));
        assertEquals("TAG 16:0/20:2(11Z,14Z)/22:4(7Z,10Z,13Z,16Z)", lipidAdduct.getLipidString(LipidLevel.STRUCTURAL_SUBSPECIES));
        assertEquals("TAG 16:0/20:2(11Z,14Z)/22:4(7Z,10Z,13Z,16Z)", lipidAdduct.getLipidString(LipidLevel.ISOMERIC_SUBSPECIES));
        assertEquals(LipidCategory.GL, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(58, lipidAdduct.getLipid().getInfo().getNCarbon());
        assertEquals(6, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNHydroxy());
    }

    @Test
    public void testDgDgAdduct() throws ParsingException {
        String ref = "DGDG 16:0-16:1";
        String refWithAdduct = ref + "[M+NH4]1+";
        double expectedMass = 908.630498;
        String expectedSumFormula = "C47H90NO15"; // sum formula of precursor without adduct is C47H86O15
        System.out.println("Testing lipid name " + refWithAdduct);
        LipidAdduct lipidAdduct = parseLipidName(refWithAdduct);
        assertEquals("[M+NH4]1+", lipidAdduct.getAdduct().getLipidString());
        assertEquals(LipidLevel.MOLECULAR_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(LipidCategory.GL, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidClass.DGDG, lipidAdduct.getLipid().getLipidClass());
        assertEquals(expectedMass, lipidAdduct.getMass(), 1e-6);
        assertEquals(expectedSumFormula, lipidAdduct.getSumFormula());
    }

    @Test
    public void testCholesterolAdduct() throws ParsingException {
        String ref = "ST 27:1;1[M+NH4]1+";
        double expectedMass = 404.3886918;
        String expectedSumFormula = "C27H50NO"; //C27H46O is the original precursor sum formula without adduct
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals("[M+NH4]1+", lipidAdduct.getAdduct().getLipidString());
        assertEquals(LipidLevel.SPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(LipidCategory.ST, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidClass.ST_27_1_1, lipidAdduct.getLipid().getLipidClass());
        assertEquals(expectedSumFormula, lipidAdduct.getSumFormula());
        assertEquals(expectedMass, lipidAdduct.getMass(), 1e-6);
    }

    @Test
    public void testSHexCer() throws ParsingException {
        String ref = "SHexCer 18:0;3/26:0;1";
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(LipidCategory.SP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidClass.SHEXCER, lipidAdduct.getLipid().getLipidClass());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("LCB").getPosition());
        assertEquals(2, lipidAdduct.getLipid().getFa().get("FA1").getPosition());
    }

    protected LipidAdduct parseLipidName(String ref) throws ParsingException {
        GoslinVisitorParser parser = new GoslinVisitorParser();
        LipidAdduct lipid = parser.parse(ref);
        return lipid;
    }
}

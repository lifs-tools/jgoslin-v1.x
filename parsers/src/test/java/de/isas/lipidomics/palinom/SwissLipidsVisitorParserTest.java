/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.domain.Adduct;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidCategory;
import de.isas.lipidomics.domain.LipidClass;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.palinom.swisslipids.SwissLipidsVisitorParser;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
public class SwissLipidsVisitorParserTest {

    @Test
    public void testNAPE() throws ParsingException {
        String ref = "NAPE (2:0/4:0/14:0)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("NAPE", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(3, lipidAdduct.getLipid().getFa().size());
        assertEquals(0, lipidAdduct.getLipid().getFa().get("FA1").getNDoubleBonds());
        assertEquals(2, lipidAdduct.getLipid().getFa().get("FA1").getNCarbon());
        assertEquals(0, lipidAdduct.getLipid().getFa().get("FA2").getNDoubleBonds());
        assertEquals(4, lipidAdduct.getLipid().getFa().get("FA2").getNCarbon());
        assertEquals(0, lipidAdduct.getLipid().getFa().get("FA3").getNDoubleBonds());
        assertEquals(14, lipidAdduct.getLipid().getFa().get("FA3").getNCarbon());
    }

    @Test
    public void testPE_Structural() throws ParsingException {
        String ref = "PE(18:1/18:1(11Z))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA1").getNDoubleBonds());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA2").getNDoubleBonds());
    }

    @Test
    public void testPE_Isomeric() throws ParsingException {
        String ref = "PE(18:0/18:1(11Z))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(0, lipidAdduct.getLipid().getFa().get("FA1").getNDoubleBonds());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA2").getNDoubleBonds());
    }

    @Test
    public void testCh() throws ParsingException {
        String ref = "CE(12:1)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("CE", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.ST, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(12, lipidAdduct.getLipid().getInfo().getNCarbon());
        assertEquals(1, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNHydroxy());
    }

    @Test
    public void testFas() throws ParsingException {
        String ref = "FA(18:4)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        System.out.println(lipidAdduct);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("FA", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.FA, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.SPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(18, lipidAdduct.getLipid().getInfo().getNCarbon());
        assertEquals(4, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNHydroxy());
    }

    @Test
    public void testMediators() throws ParsingException {
        String ref1 = "11,12-DiHETrE";
        String ref2 = "5,6-EpETrE";
        System.out.println("Testing first mediator name " + ref1);
        LipidAdduct lipidAdduct = parseLipidName(ref1);
        assertNotNull(lipidAdduct);
        System.out.println(lipidAdduct);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(ref1, lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.FA, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidClass.forHeadGroup(ref1), lipidAdduct.getLipid().getLipidClass());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());

        System.out.println("Testing second mediator name " + ref2);
        lipidAdduct = parseLipidName(ref2);
        assertNotNull(lipidAdduct);
        System.out.println(lipidAdduct);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(ref2, lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.FA, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidClass.forHeadGroup(ref2), lipidAdduct.getLipid().getLipidClass());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
    }

    @Test
    public void testPL_hyphen() throws ParsingException {

        String ref = "PE(18:3_16:2)";
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
        assertEquals(0, lipid.getFa().
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
    }

    @Test
    public void testLysoPL() throws ParsingException {
        String ref1 = "LPE(18:0)";
        System.out.println("Testing lysolipid name " + ref1);
        LipidAdduct lipidAdduct1 = parseLipidName(ref1);
        assertNotNull(lipidAdduct1);
        LipidSpecies lipid1 = lipidAdduct1.getLipid();
        assertNotNull(lipid1);
        System.out.println(lipid1);
        assertEquals("LPE", lipid1.getHeadGroup().getName());
        LipidSpeciesInfo li = lipid1.getInfo();
        assertEquals(18, li.
                getNCarbon());
        assertEquals(0, li.
                getNDoubleBonds());
        assertEquals(0, li.
                getNHydroxy());
    }

    @Test
    public void testFailForImplicitLyso() throws ParsingException {
        String ref2 = "PE(18:0_0:0)";
        System.out.println("Testing implicit lysolipid name " + ref2);
        LipidAdduct lipidAdduct2 = parseLipidName(ref2);
        assertNotNull(lipidAdduct2);
        LipidMolecularSubspecies lipid2 = (LipidMolecularSubspecies) lipidAdduct2.getLipid();
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
    }

    @Test
    public void testPG_isomeric() throws ParsingException {
        String ref = "PG(0:0/16:2(9Z,12Z))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
    }

    @Test
    public void testPE_plasmanyl() throws ParsingException {
        String ref = "PE(O-18:3/16:2)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals(LipidCategory.GP, lipid.getLipidCategory());
        assertEquals(LipidClass.PE, lipid.getLipidClass());
        assertEquals("PE", lipid.getHeadGroup().getName());
        assertEquals(LipidFaBondType.ETHER_PLASMANYL, lipid.getFa().get("FA1").getLipidFaBondType());
        assertEquals("FA1", lipid.getFa().
                get("FA1").
                getName());
        assertEquals(18, lipid.getFa().
                get("FA1").
                getNCarbon());
        assertEquals(3, lipid.getFa().
                get("FA1").
                getNDoubleBonds());
        assertEquals(0, lipid.getFa().
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
    }

    @Test
    public void testPE_plasmenyl() throws ParsingException {
        String ref = "PE(P-18:0/16:2)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals(LipidCategory.GP, lipid.getLipidCategory());
        assertEquals(LipidClass.PE, lipid.getLipidClass());
        assertEquals("PE", lipid.getHeadGroup().getName());
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, lipid.getFa().get("FA1").getLipidFaBondType());
        assertEquals("FA1", lipid.getFa().
                get("FA1").
                getName());
        assertEquals(18, lipid.getFa().
                get("FA1").
                getNCarbon());
        assertEquals(1, lipid.getFa().
                get("FA1").
                getNDoubleBonds());
        assertEquals(0, lipid.getFa().
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
    }

    @Test
    public void testPE_plasmenyl_Species() throws ParsingException {
        String ref = "PE(P-32:0)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        System.out.println(lipidAdduct);
        assertEquals(LipidCategory.GP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidClass.PE, lipidAdduct.getLipid().getLipidClass());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, lipidAdduct.getLipid().getInfo().getLipidFaBondType());
        assertEquals(1, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(32, lipidAdduct.getLipid().getInfo().getNCarbon());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNHydroxy());
        assertEquals(0, lipidAdduct.getLipid().getFa().size());
    }

    @Test
    public void testTag() throws ParsingException {
        String ref = "TG(14:0_16:0_18:1)";
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

        assertEquals("TG 14:0-16:0-18:1", lipid.getLipidString(LipidLevel.MOLECULAR_SUBSPECIES));
    }

    @Test
    public void testSmSpeciesHydroxy() throws ParsingException {
        String ref = "SM(d32:0)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("SM", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.SP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.SPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(32, lipidAdduct.getLipid().getInfo().getNCarbon());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(2, lipidAdduct.getLipid().getInfo().getNHydroxy());
    }

    @Test
    public void testSmSpeciesPlain() throws ParsingException {
        String ref = "SM(d32:0)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("SM", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.SP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.SPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(32, lipidAdduct.getLipid().getInfo().getNCarbon());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(2, lipidAdduct.getLipid().getInfo().getNHydroxy());
    }

    @Test
    public void testMhdg() throws ParsingException {
        String ref = "MHDG (18:3/16:1)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("MHDG", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.GL, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(34, lipidAdduct.getLipid().getInfo().getNCarbon());
        assertEquals(4, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNHydroxy());
    }

    @Test
    public void testDhdg() throws ParsingException {
        String ref = "DHDG (16:0/16:1)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("DHDG", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.GL, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(32, lipidAdduct.getLipid().getInfo().getNCarbon());
        assertEquals(1, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNHydroxy());
    }

    @Test
    public void testHex2Cer() throws ParsingException {
        String ref = "Hex2Cer(d18:1/16:0)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("Hex2Cer", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.SP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(34, lipidAdduct.getLipid().getInfo().getNCarbon());
        assertEquals(1, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(2, lipidAdduct.getLipid().getInfo().getNHydroxy());
    }

    @Test
    public void testIsomericSubspecies() throws ParsingException {
        String ref = "TG(16:0/20:2(11Z,14Z)/22:4(7Z,10Z,13Z,16Z))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("TG", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.GL, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(58, lipidAdduct.getLipid().getInfo().getNCarbon());
        Integer nDoubleBonds = lipidAdduct.getLipid().getFa().values().stream().collect(Collectors.summingInt((t) -> {
            return t.getNDoubleBonds();
        }));
        assertEquals(6, nDoubleBonds);
        assertEquals(6, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNHydroxy());
    }

    @Test
    public void testIsomericSubspecies2() throws ParsingException {
        String ref = "PE(30:5(15Z,18Z,21Z,24Z,27Z)/20:3(8Z,11Z,14Z))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.GP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(50, lipidAdduct.getLipid().getInfo().getNCarbon());
        Integer nDoubleBonds = lipidAdduct.getLipid().getFa().values().stream().collect(Collectors.summingInt((t) -> {
            return t.getNDoubleBonds();
        }));
        assertEquals(8, nDoubleBonds);
        assertEquals(8, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNHydroxy());
    }

    @Test
    public void testCholesterolEster() throws ParsingException {
        String ref = "CE(10:0)";
        String expectedSumFormula = "C37H64O2";
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("CE", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.ST, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(10, lipidAdduct.getLipid().getInfo().getNCarbon());
        Integer nDoubleBonds = lipidAdduct.getLipid().getFa().values().stream().collect(Collectors.summingInt((t) -> {
            return t.getNDoubleBonds();
        }));
        assertEquals(0, nDoubleBonds);
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNHydroxy());
        assertEquals(expectedSumFormula, lipidAdduct.getSumFormula());
    }

    @Test
    public void testSterolEster() throws ParsingException {
        String ref = "SE(27:1/10:0)";
        String expectedSumFormula = "C37H64O2";
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("SE 27:1", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.ST, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(10, lipidAdduct.getLipid().getInfo().getNCarbon());
        Integer nDoubleBonds = lipidAdduct.getLipid().getFa().values().stream().collect(Collectors.summingInt((t) -> {
            return t.getNDoubleBonds();
        }));
        assertEquals(0, nDoubleBonds);
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNHydroxy());
        assertEquals(expectedSumFormula, lipidAdduct.getSumFormula());
    }

    @Test
    public void testSterolEsterSpecies() throws ParsingException {
        String ref = "SE(43:2)";
        String expectedSumFormula = "C43H74O2";
        double expectedMass = 622.569;
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("SE", lipidAdduct.getLipid().getHeadGroup().getName());
        assertEquals(LipidCategory.ST, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidClass.SE_27_1, lipidAdduct.getLipid().getLipidClass());
        assertEquals(LipidLevel.SPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(16, lipidAdduct.getLipid().getInfo().getNCarbon());
        assertEquals(1, lipidAdduct.getLipid().getInfo().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().getNHydroxy());
        assertEquals("SE 27:1/16:1", lipidAdduct.getNormalizedLipidString(LipidLevel.SPECIES));
        assertEquals(expectedSumFormula, lipidAdduct.getSumFormula());
        assertEquals(expectedMass, lipidAdduct.getMass(), 1e-3);
    }

    @Test
    public void testNeuGcNeuAcSlashHashGroup() throws ParsingException {
        String ref = "GD1a(NeuGc/NeuAc) (d20:1(4E)/14:1(9Z))";
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
    }

    @Test
    public void testNeuAcNeuGcSlashHashGroup() throws ParsingException {
        String ref = "GD1a(NeuAc/NeuGc) (d20:1(4E)/14:1)";
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
    }

    @Test
    public void testPlasmenylEther() throws ParsingException {
        String ref = "PE(P-16:0/22:6)";
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA1").getPosition());
        assertEquals(2, lipidAdduct.getLipid().getFa().get("FA2").getPosition());
    }

    @Test
    public void testHydroxyls() throws ParsingException {
        String ref = "M(IP)2C(t18:0/20:0(2OH))";
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("LCB").getPosition());
        assertEquals(3, lipidAdduct.getLipid().getFa().get("LCB").getNHydroxy());
        assertEquals(2, lipidAdduct.getLipid().getFa().get("FA1").getPosition());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA1").getNHydroxy());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA1").getModifications().size());
        assertEquals("M(IP)2C 18:0;3/20:0;1", lipidAdduct.getLipidString());

        ref = "M(IP)2C(t18:0/20:1(9Z)(2OH))";
        lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("LCB").getPosition());
        assertEquals(3, lipidAdduct.getLipid().getFa().get("LCB").getNHydroxy());
        assertEquals(2, lipidAdduct.getLipid().getFa().get("FA1").getPosition());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA1").getNHydroxy());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA1").getModifications().size());
        assertEquals("M(IP)2C 18:0;3/20:1(9Z);1", lipidAdduct.getLipidString());

        ref = "PC(18:0_20:0(2OH))";
        lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(LipidLevel.MOLECULAR_SUBSPECIES, lipidAdduct.getLipid().getInfo().getLevel());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(-1, lipidAdduct.getLipid().getFa().get("FA1").getPosition());
        assertEquals(0, lipidAdduct.getLipid().getFa().get("FA1").getNHydroxy());
        assertEquals(-1, lipidAdduct.getLipid().getFa().get("FA2").getPosition());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA2").getNHydroxy());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA2").getModifications().size());
        assertEquals("PC 18:0-20:0;1", lipidAdduct.getLipidString());
    }

    protected LipidAdduct parseLipidName(String ref) throws ParsingException {
        SwissLipidsVisitorParser parser = new SwissLipidsVisitorParser();
        LipidAdduct lipid = parser.parse(ref);
        return lipid;
    }
}

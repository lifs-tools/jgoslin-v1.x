/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.palinom.lipidmaps.LipidMapsVisitorParser;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
public class LipidMapsVisitorParserTest {

    @Test
    public void testPK_Structural() throws ParsingException {
        String ref = "PHENOL(15:2)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PHENOL", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(1, lipidAdduct.getLipid().getFa().size());
        assertEquals(15, lipidAdduct.getLipid().getFa().get("FA1").getNCarbon());
        assertEquals(2, lipidAdduct.getLipid().getFa().get("FA1").getNDoubleBonds());
    }

    @Test
    public void testPK_Isomeric() throws ParsingException {
        String ref = "PHENOL(15:2(8Z,11Z))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PHENOL", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(1, lipidAdduct.getLipid().getFa().size());
        assertEquals(15, lipidAdduct.getLipid().getFa().get("FA1").getNCarbon());
        assertEquals(2, lipidAdduct.getLipid().getFa().get("FA1").getNDoubleBonds());
    }

    @Test
    public void testPE_Structural() throws ParsingException {
        String ref = "PE(18:1/18:2(11Z,13E))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA1").getNDoubleBonds());
        assertEquals(2, lipidAdduct.getLipid().getFa().get("FA2").getNDoubleBonds());
    }

    @Test
    public void testPE_Isomeric() throws ParsingException {
        String ref = "PE(18:0/18:2(11Z,13E))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(0, lipidAdduct.getLipid().getFa().get("FA1").getNDoubleBonds());
        assertEquals(2, lipidAdduct.getLipid().getFa().get("FA2").getNDoubleBonds());
    }

    @Test
    public void testMIPC() throws ParsingException {
        String ref = "M(IP)2C(t36:0(OH))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("M(IP)2C", lipidAdduct.getLipid().getHeadGroup());
    }

    @Test
    public void testSulfoHexCer() throws ParsingException {
        String ref = "SulfoHexCer(d36:1)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("SulfoHexCer", lipidAdduct.getLipid().getHeadGroup());
    }

    @Test
    public void testCh() throws ParsingException {
        String ref = "Cholesterol";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(ref, lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.ST, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
    }

    @Test
    public void testChE() throws ParsingException {
        String ref = "CE(12:1)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("CE", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.ST, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(12, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(1, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
    }

    @Test
    public void testFas() throws ParsingException {
        String ref = "FA(18:4)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        System.out.println(lipidAdduct);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("FA", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.FA, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.SPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
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
        assertEquals(ref1, lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.FA, lipidAdduct.getLipid().getLipidCategory());
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
//        assertEquals("",lipidAdduct.getAdduct().getAdductString());
//        assertEquals(Integer.valueOf(1),lipidAdduct.getAdduct().getCharge());
//        assertEquals(Integer.valueOf(1),lipidAdduct.getAdduct().getChargeSign());
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
        assertEquals("PE", lipid.getHeadGroup());
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
    }

    @Test
    public void testLysoPL() throws ParsingException {
        String ref1 = "LPE 18:0";
        System.out.println("Testing lysolipid name " + ref1);
        LipidAdduct lipidAdduct1 = parseLipidName(ref1);
        assertNotNull(lipidAdduct1);
        LipidMolecularSubspecies lipid1 = (LipidMolecularSubspecies) lipidAdduct1.getLipid();
        assertNotNull(lipid1);
        System.out.println(lipid1);
        assertEquals("LPE", lipid1.getHeadGroup());
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
    }

    //LPE O-16:1p/12:0
    @Test
    public void testFailForImplicitLyso() throws ParsingException {
//        assertThrows(ConstraintViolationException.class, () -> {
        String ref2 = "PE 18:0-0:0";
        System.out.println("Testing implicit lysolipid name " + ref2);
        LipidAdduct lipidAdduct2 = parseLipidName(ref2);
        assertNotNull(lipidAdduct2);
        LipidMolecularSubspecies lipid2 = (LipidMolecularSubspecies) lipidAdduct2.getLipid();
        assertNotNull(lipid2);
        System.out.println(lipid2);
        assertEquals("PE", lipid2.getHeadGroup());
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
//        });
    }

    @Test
    public void testPE_plasmanyl() throws ParsingException {
        String ref = "PE(O-18:3;1/16:2)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals(LipidCategory.GP, lipid.getLipidCategory());
        assertEquals(LipidClass.PE, lipid.getLipidClass().get());
        assertEquals("PE", lipid.getHeadGroup());
        assertEquals(LipidFaBondType.ETHER_PLASMANYL, lipid.getFa().get("FA1").getLipidFaBondType());
        assertEquals("FA1", lipid.getFa().
                get("FA1").
                getName());
        assertEquals(18, lipid.getFa().
                get("FA1").
                getNCarbon());
        // these are actually 3 + 1 (double bond after ether)
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
    }

    @Test
    public void testPE_plasmenyl() throws ParsingException {
        String ref = "PE(P-18:0/16:2;1)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals(LipidCategory.GP, lipid.getLipidCategory());
        assertEquals(LipidClass.PE, lipid.getLipidClass().get());
        assertEquals("PE", lipid.getHeadGroup());
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, lipid.getFa().get("FA1").getLipidFaBondType());
        assertEquals("FA1", lipid.getFa().
                get("FA1").
                getName());
        assertEquals(18, lipid.getFa().
                get("FA1").
                getNCarbon());
        // these are actually 0 + 1 (double bond after ether)
        assertEquals(0, lipid.getFa().
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
        assertEquals(1, lipid.getFa().
                get("FA2").
                getNHydroxy());
    }

    @Test
    public void testTag() throws ParsingException {
        String ref = "TG(14:0-16:0-18:1)";
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

        assertEquals("TG 14:0_16:0_18:1", lipid.getLipidString(LipidLevel.MOLECULAR_SUBSPECIES));
    }

    @Test
    public void testSmSpeciesHydroxy() throws ParsingException {
        String ref = "SM(d32:0)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("SM", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.SP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.SPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(32, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(2, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
    }

    @Test
    public void testSmSpeciesPlain() throws ParsingException {
        String ref = "SM(32:0)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("SM", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.SP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.SPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(32, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
    }

    @Test
    public void testHex3Cer() throws ParsingException {
        String ref = "Hex3Cer(d18:1/16:0)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("Hex3Cer", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.SP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(34, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(1, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(2, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
    }

    @Test
    public void testFaWithModification() throws ParsingException {
        String ref = "CoA(4:0(OH))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("CoA", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.FA, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.SPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(4, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
    }

    @Test
    public void testIsomericSubspecies() throws ParsingException {
        String ref = "TG(16:0/20:2(11Z,14Z)/22:4(7Z,10Z,13Z,16Z))[iso6]";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("TG", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.GL, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(58, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(6, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
//        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA3").getModifications().size());
    }

    @Test
    public void testModification() throws ParsingException {
        String ref = "FA(6:0(OH,Ke))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("FA", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.FA, lipidAdduct.getLipid().getLipidCategory());
        // this is a species level fatty acyl
        assertEquals(LipidLevel.SPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(6, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
        assertEquals(2, lipidAdduct.getLipid().getInfo().get().getModifications().size());
        assertEquals("FA 6:0(OH,Ke)", lipidAdduct.getLipid().getLipidString());
    }

    @Test
    public void testPip2IsomericSubspeciesLevel() throws ParsingException {
        String ref = "PIP2[3',5'](16:0/18:2(9Z,12Z))";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PIP2", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.GP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(34, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(2, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
        assertEquals(2, lipidAdduct.getLipid().getFa().get("FA2").getDoubleBondPositions().size());
        assertEquals("PIP2 16:0/18:2(9Z,12Z)", lipidAdduct.getLipid().getLipidString());
        assertEquals("PIP2 34:2", lipidAdduct.getLipid().getLipidString(LipidLevel.SPECIES));
    }

    protected LipidAdduct parseLipidName(String ref) throws ParsingException {
        LipidMapsVisitorParser parser = new LipidMapsVisitorParser();
        LipidAdduct lipid = parser.parse(ref);
        return lipid;
    }

}

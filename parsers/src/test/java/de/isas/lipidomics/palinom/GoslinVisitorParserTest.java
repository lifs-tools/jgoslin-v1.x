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
import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
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
    public void testPE_Structural() throws ParsingException {
        String ref = "PE 18:1/18:1(11Z)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA1").getNDoubleBonds());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA2").getNDoubleBonds());
    }

    @Test
    public void testPE_Isomeric() throws ParsingException {
        String ref = "PE 18:0/18:1(11Z)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(0, lipidAdduct.getLipid().getFa().get("FA1").getNDoubleBonds());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA2").getNDoubleBonds());
    }

    @Test
    public void testPE_O() throws ParsingException {
        String ref = "PE O-18:0a/16:2;1";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.GP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals("PE O-34:2;1a", lipidAdduct.getLipid().getLipidString(LipidLevel.SPECIES));
        assertEquals("PE O-18:0a/16:2;1", lipidAdduct.getLipid().toString());
    }

    @Test
    public void testCh() throws ParsingException {
        String ref = "Ch";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(ref, lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.ST, lipidAdduct.getLipid().getLipidCategory());
    }

    @Test
    public void testChE() throws ParsingException {
        String ref = "ChE 12:1";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("ChE", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.ST, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(12, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(1, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
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

        String ref2 = ref + " [M+H]1+";
        System.out.println("Testing lipid name " + ref2);
        LipidAdduct lipidAdduct2 = parseLipidName(ref2);
        assertNotNull(lipidAdduct2);
        System.out.println(lipidAdduct2);
        assertEquals(new Adduct("", "+H", 1, 1), lipidAdduct2.getAdduct());
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
    public void testPL_slash() throws ParsingException {
        String ref = "PE 18:3;1/16:2";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
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
    public void testPL_Pe_adduct() throws ParsingException {
        String ref = "PE 16:1/12:0[M+H]1+";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals("PE", lipid.getHeadGroup());
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
                getAdduct().getLipidString());
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
        assertEquals("PC", lipid.getHeadGroup());
        assertTrue(lipid.isEtherLipid());
        assertEquals(LipidFaBondType.ETHER_UNSPECIFIED, lipid.getInfo().get().getLipidFaBondType());
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
        assertEquals("LPC", lipid.getHeadGroup());
        assertTrue(lipid.isEtherLipid());
        assertEquals(LipidFaBondType.ETHER_UNSPECIFIED, lipid.getInfo().get().getLipidFaBondType());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipid.getInfo().get().getLevel());
        assertEquals(ref, lipid.toString());
        assertEquals(ref, lipid.getLipidString(LipidLevel.SPECIES));
        assertEquals(ref, lipid.getLipidString(LipidLevel.MOLECULAR_SUBSPECIES));
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            assertEquals(ref, lipid.getLipidString(LipidLevel.ISOMERIC_SUBSPECIES));
        });
    }
    
    @Test
    public void testPE_Plasmenyl_StructuralSpecies() throws ParsingException {
        String ref = "PE O-16:1p/18:1";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, lipid.getInfo().get().getLipidFaBondType());
        System.out.println(lipid);
        assertEquals(LipidCategory.GP, lipid.getLipidCategory());
        assertEquals(LipidClass.PE, lipid.getLipidClass().get());
        assertEquals("PE", lipid.getHeadGroup());
        assertEquals("FA1", lipid.getFa().
                get("FA1").
                getName());
        assertEquals(16, lipid.getFa().
                get("FA1").
                getNCarbon());
        assertEquals(1, lipid.getFa().
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
        assertEquals(LipidFaBondType.ETHER_PLASMENYL, lipid.getInfo().get().getLipidFaBondType());
        System.out.println(lipid);
        assertEquals(LipidCategory.GP, lipid.getLipidCategory());
        assertEquals(LipidClass.PE, lipid.getLipidClass().get());
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

        assertEquals(ref, lipid.getLipidString(LipidLevel.MOLECULAR_SUBSPECIES));
        assertEquals(LipidCategory.GL, lipid.getLipidCategory());
        assertEquals(LipidClass.TAG, lipid.getLipidClass().get());
    }

    protected LipidAdduct parseLipidName(String ref) throws ParsingException {
        GoslinVisitorParser parser = new GoslinVisitorParser();
        LipidAdduct lipid = parser.parse(ref);
        return lipid;
    }
}

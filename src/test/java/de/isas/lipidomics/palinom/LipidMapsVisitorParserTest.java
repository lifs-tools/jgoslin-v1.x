/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.domain.Adduct;
import de.isas.lipidomics.domain.Bond;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidCategory;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author nilshoffmann
 */
public class LipidMapsVisitorParserTest {

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
        LipidMolecularSubspecies lipid1 = (LipidMolecularSubspecies)lipidAdduct1.getLipid();
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
    
    @Test(expected=ConstraintViolationException.class)
    public void testFailForImplicitLyso() throws ParsingException {
        String ref2 = "PE 18:0-0:0";
        System.out.println("Testing implicit lysolipid name " + ref2);
        LipidAdduct lipidAdduct2 = parseLipidName(ref2);
        assertNotNull(lipidAdduct2);
        LipidMolecularSubspecies lipid2 = (LipidMolecularSubspecies)lipidAdduct2.getLipid();
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
    }

    @Test
    public void testPE_plasmanyl() throws ParsingException{
        String ref = "PE(O-18:3;1/16:2)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
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
    public void testPE_plasmenyl() throws ParsingException{
        String ref = "PE(P-18:0/16:2;1)";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
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

    protected LipidAdduct parseLipidName(String ref) throws ParsingException {
        LipidMapsVisitorParser parser = new LipidMapsVisitorParser();
        LipidAdduct lipid = parser.parse(ref);
        return lipid;
    }
}

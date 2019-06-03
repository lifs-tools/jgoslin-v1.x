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
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author nilshoffmann
 */
public class GoslinVisitorParserTest {

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
    public void testPL_slash() throws ParsingException{
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
    public void testPL_Plasmanyl_slash() throws ParsingException{
        String ref = "PE O 18:3;1a/16:2";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals("PE O", lipid.getHeadGroup());
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
    public void testPL_Plasmenyl_slash() throws ParsingException{
        String ref = "PE O 18:3;1p/16:2";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals("PE O", lipid.getHeadGroup());
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
        assertEquals(0, lipid.getFa().
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
        assertEquals(LipidClass.TG, lipid.getLipidClass().get());
    }

    protected LipidAdduct parseLipidName(String ref) throws ParsingException {
        PaLiNomVisitorParser parser = new PaLiNomVisitorParser();
        LipidAdduct lipid = parser.parse(ref);
        return lipid;
    }
}

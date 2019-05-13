/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.domain.Adduct;
import de.isas.lipidomics.domain.Lipid;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidCategory;
import org.antlr.v4.runtime.RecognitionException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author nilshoffmann
 */
public class PaLiNomVisitorParserTest {

    @Test
    public void testCh() {
        String ref = "Ch";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(ref, lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.ST, LipidCategory.valueOf(lipidAdduct.getLipid().getLipidCategory()));
    }
    
    
    @Test
    public void testMediators() {
        String ref1 = "11,12-DHET";
        String ref2 = "5(6)-EET";
        System.out.println("Testing lipid name " + ref1);
        LipidAdduct lipidAdduct = parseLipidName(ref1);
        assertNotNull(lipidAdduct);
        System.out.println(lipidAdduct);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(ref1, lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.MEDIATOR, LipidCategory.valueOf(lipidAdduct.getLipid().getLipidCategory()));
//        assertEquals("",lipidAdduct.getAdduct().getAdductString());
//        assertEquals(Integer.valueOf(1),lipidAdduct.getAdduct().getCharge());
//        assertEquals(Integer.valueOf(1),lipidAdduct.getAdduct().getChargeSign());
    }
    
    
    @Test
    public void testPL_adduct() {
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
    public void testPL_underscore() {

        String ref = "PE 18:3;1-16:2";
        System.out.println("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        Lipid lipid = lipidAdduct.getLipid();
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
//
//    @Test
//    public void testPL_slash() {
//
//        String ref = "PE 18:3;1/16:2";
//        System.out.println("Testing lipid name " + ref);
//        LipidAdduct lipidAdduct = parseLipidName(ref);
//        assertNotNull(lipidAdduct);
//        Lipid lipid = lipidAdduct.getLipid();
//        assertNotNull(lipid);
//        System.out.println(lipid);
//        assertEquals("PE", lipid.getHeadGroup());
//        assertEquals("FA1", lipid.getFa().
//            get("FA1").
//            getName());
//        assertEquals(18, lipid.getFa().
//            get("FA1").
//            getNCarbon());
//        assertEquals(3, lipid.getFa().
//            get("FA1").
//            getNDoubleBonds());
//        assertEquals(1, lipid.getFa().
//            get("FA1").
//            getNHydroxy());
//        assertEquals("FA2", lipid.getFa().
//            get("FA2").
//            getName());
//        assertEquals(16, lipid.getFa().
//            get("FA2").
//            getNCarbon());
//        assertEquals(2, lipid.getFa().
//            get("FA2").
//            getNDoubleBonds());
//        assertEquals(0, lipid.getFa().
//            get("FA2").
//            getNHydroxy());
//    }
//    
//    @Test
//    public void testCl() {
//        String ref = "CL 18:1-18:1-18:1-18:1";
//        LipidAdduct lipidAdduct = parseLipidName(ref);
//        assertNotNull(lipidAdduct);
//        Lipid lipid = lipidAdduct.getLipid();
//        System.out.println(lipid);
//        assertEquals(4, lipid.getFa().size());
//        assertEquals(18, lipid.getFa().get("FA1").getNCarbon());
//        assertEquals(1, lipid.getFa().get("FA1").getNDoubleBonds());
//        assertEquals(0, lipid.getFa().get("FA1").getNHydroxy());
//        assertEquals(lipid.getFa().get("FA1").getNDoubleBonds(), lipid.getFa().get("FA1").getDoubleBondLocations().size());
//        
//        assertEquals(18, lipid.getFa().get("FA2").getNCarbon());
//        assertEquals(1, lipid.getFa().get("FA2").getNDoubleBonds());
//        assertEquals(0, lipid.getFa().get("FA2").getNHydroxy());
//        assertEquals(lipid.getFa().get("FA2").getNDoubleBonds(), lipid.getFa().get("FA2").getDoubleBondLocations().size());
//        
//        assertEquals(18, lipid.getFa().get("FA3").getNCarbon());
//        assertEquals(1, lipid.getFa().get("FA3").getNDoubleBonds());
//        assertEquals(0, lipid.getFa().get("FA3").getNHydroxy());
//        assertEquals(lipid.getFa().get("FA3").getNDoubleBonds(), lipid.getFa().get("FA3").getDoubleBondLocations().size());
//        
//        assertEquals(18, lipid.getFa().get("FA4").getNCarbon());
//        assertEquals(1, lipid.getFa().get("FA4").getNDoubleBonds());
//        assertEquals(0, lipid.getFa().get("FA4").getNHydroxy());
//        assertEquals(lipid.getFa().get("FA4").getNDoubleBonds(), lipid.getFa().get("FA4").getDoubleBondLocations().size());
//    }

    protected LipidAdduct parseLipidName(String ref) throws RecognitionException {
        PaLiNomVisitorParser parser = new PaLiNomVisitorParser();
        LipidAdduct lipid = parser.parse(ref);
        return lipid;
    }
}

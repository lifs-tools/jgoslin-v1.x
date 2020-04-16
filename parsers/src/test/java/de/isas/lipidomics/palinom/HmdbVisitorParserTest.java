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
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import de.isas.lipidomics.palinom.hmdb.HmdbVisitorParser;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
public class HmdbVisitorParserTest {

    @Test
    public void testNAPE() throws ParsingException {
        String ref = "NAPE (2:0/4:0/14:0)";
        log.info("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("NAPE", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
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
        log.info("Testing lipid name " + ref);
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
        String ref = "PE(18:0/18:1(11Z))";
        log.info("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(2, lipidAdduct.getLipid().getFa().size());
        assertEquals(0, lipidAdduct.getLipid().getFa().get("FA1").getNDoubleBonds());
        assertEquals(1, lipidAdduct.getLipid().getFa().get("FA2").getNDoubleBonds());
    }

    @Test
    public void testCh() throws ParsingException {
        String ref = "CE(12:1)";
        log.info("Testing lipid name " + ref);
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
        log.info("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        log.info("" + lipidAdduct);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("FA", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.FA, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.SPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(18, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(4, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
    }

    @Test
    public void testMediators() throws ParsingException {
        String ref1 = "11,12-DiHETrE";
        String ref2 = "5,6-EpETrE";
        log.info("Testing first mediator name " + ref1);
        LipidAdduct lipidAdduct = parseLipidName(ref1);
        assertNotNull(lipidAdduct);
        log.info("" + lipidAdduct);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(ref1, lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.FA, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidClass.forHeadGroup(ref1).get(), lipidAdduct.getLipid().getLipidClass().get());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());

        log.info("Testing second mediator name " + ref2);
        lipidAdduct = parseLipidName(ref2);
        assertNotNull(lipidAdduct);
        log.info("" + lipidAdduct);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals(ref2, lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.FA, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidClass.forHeadGroup(ref2).get(), lipidAdduct.getLipid().getLipidClass().get());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
    }

    @Test
    public void testPL_hyphen() throws ParsingException {

        String ref = "PE(18:3_16:2)";
        log.info("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidMolecularSubspecies lipid = LipidMolecularSubspecies.class.cast(lipidAdduct.getLipid());
        assertNotNull(lipid);
        log.info("" + lipid);
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
        log.info("Testing lysolipid name " + ref1);
        LipidAdduct lipidAdduct1 = parseLipidName(ref1);
        assertNotNull(lipidAdduct1);
        LipidSpecies lipid1 = lipidAdduct1.getLipid();
        assertNotNull(lipid1);
        log.info("" + lipid1);
        assertEquals("LPE", lipid1.getHeadGroup());
        LipidSpeciesInfo li = lipid1.getInfo().get();
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
        log.info("Testing implicit lysolipid name " + ref2);
        LipidAdduct lipidAdduct2 = parseLipidName(ref2);
        assertNotNull(lipidAdduct2);
        LipidMolecularSubspecies lipid2 = (LipidMolecularSubspecies) lipidAdduct2.getLipid();
        assertNotNull(lipid2);
        log.info("" + lipid2);
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
    public void testPG_isomeric() throws ParsingException {
        String ref = "PG(0:0/16:2(9Z,12Z))";
        log.info("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        log.info("" + lipid);
    }

    @Test
    public void testPE_plasmanyl() throws ParsingException {
        String ref = "PE(O-18:3/16:2)";
        log.info("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        log.info("" + lipid);
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
        log.info("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidStructuralSubspecies lipid = (LipidStructuralSubspecies) lipidAdduct.getLipid();
        assertNotNull(lipid);
        log.info("" + lipid);
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
        assertEquals(0, lipid.getFa().
                get("FA2").
                getNHydroxy());
    }

    @Test
    public void testTag() throws ParsingException {
        String ref = "TG(14:0_16:0_18:1)";
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertNotNull(lipidAdduct);
        LipidMolecularSubspecies lipid = (LipidMolecularSubspecies) lipidAdduct.getLipid();
        log.info("" + lipid);
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
        log.info("Testing lipid name " + ref);
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
        String ref = "SM(d32:0)";
        log.info("Testing lipid name " + ref);
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
    public void testMhdg() throws ParsingException {
        String ref = "MHDG (18:3/16:1)";
        log.info("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("MHDG", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.GL, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(34, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(4, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
    }

    @Test
    public void testDhdg() throws ParsingException {
        String ref = "DHDG (16:0/16:1)";
        log.info("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("DHDG", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.GL, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(32, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(1, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
    }

    @Test
    public void testHex2Cer() throws ParsingException {
        String ref = "Hex2Cer(d18:1/16:0)";
        log.info("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("Hex2Cer", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.SP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.STRUCTURAL_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(34, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        assertEquals(1, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(2, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
    }

    @Test
    public void testIsomericSubspecies() throws ParsingException {
        String ref = "TG(16:0/20:2(11Z,14Z)/22:4(7Z,10Z,13Z,16Z))";
        log.info("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("TG", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.GL, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(58, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        Integer nDoubleBonds = lipidAdduct.getLipid().getFa().values().stream().collect(Collectors.summingInt((t) -> {
            return t.getNDoubleBonds();
        }));
        assertEquals(6, nDoubleBonds);
        assertEquals(6, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
    }

    @Test
    public void testIsomericSubspecies2() throws ParsingException {
        String ref = "PE(30:5(15Z,18Z,21Z,24Z,27Z)/20:3(8Z,11Z,14Z))";
        log.info("Testing lipid name " + ref);
        LipidAdduct lipidAdduct = parseLipidName(ref);
        assertEquals(Adduct.NONE, lipidAdduct.getAdduct());
        assertEquals("PE", lipidAdduct.getLipid().getHeadGroup());
        assertEquals(LipidCategory.GP, lipidAdduct.getLipid().getLipidCategory());
        assertEquals(LipidLevel.ISOMERIC_SUBSPECIES, lipidAdduct.getLipid().getInfo().get().getLevel());
        assertEquals(50, lipidAdduct.getLipid().getInfo().get().getNCarbon());
        Integer nDoubleBonds = lipidAdduct.getLipid().getFa().values().stream().collect(Collectors.summingInt((t) -> {
            return t.getNDoubleBonds();
        }));
        assertEquals(8, nDoubleBonds);
        assertEquals(8, lipidAdduct.getLipid().getInfo().get().getNDoubleBonds());
        assertEquals(0, lipidAdduct.getLipid().getInfo().get().getNHydroxy());
    }

    @Test
    public void testFuranFaShoudThrowParseTreeVisitorException() throws ParsingException {
        String ref = "PE-NMe2(9D5/13D5)";
        log.info("Testing lipid name " + ref);
        assertThrows(ParseTreeVisitorException.class, () -> {
            LipidAdduct lipidAdduct = parseLipidName(ref);
        });
    }

    @Test
    public void testInterlinkFaShouldThrowParseTreeVisitorException() throws ParsingException {
        String ref = "PC(DiMe(11,3)/DiMe(11,3))";
        log.info("Testing lipid name " + ref);
        assertThrows(ParseTreeVisitorException.class, () -> {
            LipidAdduct lipidAdduct = parseLipidName(ref);
        });
    }

    @Test
    public void testLipidSuffixShouldThrowParseTreeVisitorException() throws ParsingException {
        String ref = "TG(a-21:0/i-15:0/14:0)[rac]";
        assertThrows(ParseTreeVisitorException.class, () -> {
            LipidAdduct lipidAdduct = parseLipidName(ref);
        });
    }
    
    @Test
    public void testMatchFuranLipid() {
        String ref = "CE(13D3)";
        String regex = ".*\\([MD0-9]+(/[MD0-9]+)?(/[0-9:]+)?\\).*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(ref);
        assertTrue(m.matches());
        
        ref = "PE-NMe(9D3/9D5)";
        m = p.matcher(ref);
        assertTrue(m.matches());
        
        ref = "PE-NMe(9D3/9D5/21:0)";
        m = p.matcher(ref);
        assertTrue(m.matches());
    }

    protected LipidAdduct parseLipidName(String ref) throws ParsingException {
        HmdbVisitorParser parser = new HmdbVisitorParser();
        LipidAdduct lipid = parser.parse(ref);
        return lipid;
    }

}

/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.domain.Lipid;
import de.isas.lipidomics.palinom.PaLiNomParser.LipidIdentifierContext;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.RecognitionException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

/**
 *
 * @author nilshoffmann
 */
public class PaLiNomParserTest {

    @Test
    public void testPL_underscore() {

        String ref = "PE 18:3;1-16:2";
        System.out.println("Testing lipid name " + ref);
        Lipid lipid = parseLipidName(ref);
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
    public void testPL_slash() {

        String ref = "PE 18:3;1/16:2";
        System.out.println("Testing lipid name " + ref);
        Lipid lipid = parseLipidName(ref);
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
    public void testCl() {
        String ref = "CL 18:1-18:1-18:1-18:1";
        Lipid lipid = parseLipidName(ref);
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals(4, lipid.getFa().size());
        assertEquals(18, lipid.getFa().get("FA1").getNCarbon());
        assertEquals(1, lipid.getFa().get("FA1").getNDoubleBonds());
        assertEquals(0, lipid.getFa().get("FA1").getNHydroxy());
        assertEquals(lipid.getFa().get("FA1").getNDoubleBonds(), lipid.getFa().get("FA1").getDoubleBondLocations().size());
        
        assertEquals(18, lipid.getFa().get("FA2").getNCarbon());
        assertEquals(1, lipid.getFa().get("FA2").getNDoubleBonds());
        assertEquals(0, lipid.getFa().get("FA2").getNHydroxy());
        assertEquals(lipid.getFa().get("FA2").getNDoubleBonds(), lipid.getFa().get("FA2").getDoubleBondLocations().size());
        
        assertEquals(18, lipid.getFa().get("FA3").getNCarbon());
        assertEquals(1, lipid.getFa().get("FA3").getNDoubleBonds());
        assertEquals(0, lipid.getFa().get("FA3").getNHydroxy());
        assertEquals(lipid.getFa().get("FA3").getNDoubleBonds(), lipid.getFa().get("FA3").getDoubleBondLocations().size());
        
        assertEquals(18, lipid.getFa().get("FA4").getNCarbon());
        assertEquals(1, lipid.getFa().get("FA4").getNDoubleBonds());
        assertEquals(0, lipid.getFa().get("FA4").getNHydroxy());
        assertEquals(lipid.getFa().get("FA4").getNDoubleBonds(), lipid.getFa().get("FA4").getDoubleBondLocations().size());
    }

    protected Lipid parseLipidName(String ref) throws RecognitionException {
        PaLiNomLexer lexer = new PaLiNomLexer(CharStreams.fromString(ref));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PaLiNomParser parser = new PaLiNomParser(tokens);
        LipidIdentifierContext context = parser.lipidIdentifier();
        PaLiNomListenerParser visitor = new PaLiNomListenerParser();
        Lipid lipid = visitor.visit(context);
        return lipid;
    }
}

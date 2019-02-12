/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.domain.Lipid;
import static de.isas.lipidomics.palinom.PaLiNomLexer.ruleNames;
import de.isas.lipidomics.palinom.PaLiNomParser.LipidIdentifierContext;
import java.util.Arrays;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
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

        String ref = "PE 18:3;1_16:2";
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

    protected Lipid parseLipidName(String ref) throws RecognitionException {
        PaLiNomLexer lexer = new PaLiNomLexer(CharStreams.fromString(ref));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PaLiNomParser parser = new PaLiNomParser(tokens);
        LipidIdentifierContext context = parser.lipidIdentifier();
        PaLiNomVisitor visitor = new PaLiNomVisitor();
        Lipid lipid = visitor.visit(context);
        return lipid;
    }
}

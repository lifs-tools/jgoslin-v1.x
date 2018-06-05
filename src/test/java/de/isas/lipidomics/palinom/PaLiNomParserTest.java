/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.domain.Lipid;
import de.isas.lipidomics.palinom.PaLiNomParser.LipidIdentifierContext;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.CharStreams;
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

        PaLiNomLexer lexer = new PaLiNomLexer(CharStreams.fromString(ref));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PaLiNomParser parser = new PaLiNomParser(tokens);
        LipidIdentifierContext context = parser.lipidIdentifier();
        PaLiNomVisitor visitor = new PaLiNomVisitor();
        Lipid lipid = visitor.visit(context);
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals("PE", lipid.getHeadGroup());
        assertEquals("FA1", lipid.getFa1().getName());
        assertEquals(18, lipid.getFa1().getNCarbon());
        assertEquals(3, lipid.getFa1().getNDoubleBond());
        assertEquals(1, lipid.getFa1().getNHydroxy());
        assertEquals("FA2", lipid.getFa2().getName());
        assertEquals(16, lipid.getFa2().getNCarbon());
        assertEquals(2, lipid.getFa2().getNDoubleBond());
        assertEquals(0, lipid.getFa2().getNHydroxy());
    }
    
     @Test
    public void testPL_slash() {

        String ref = "PE 18:3;1/16:2";

        PaLiNomLexer lexer = new PaLiNomLexer(CharStreams.fromString(ref));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PaLiNomParser parser = new PaLiNomParser(tokens);
        LipidIdentifierContext context = parser.lipidIdentifier();
        PaLiNomVisitor visitor = new PaLiNomVisitor();
        Lipid lipid = visitor.visit(context);
        assertNotNull(lipid);
        System.out.println(lipid);
        assertEquals("PE", lipid.getHeadGroup());
        assertEquals("FA1", lipid.getFa1().getName());
        assertEquals(18, lipid.getFa1().getNCarbon());
        assertEquals(3, lipid.getFa1().getNDoubleBond());
        assertEquals(1, lipid.getFa1().getNHydroxy());
        assertEquals("FA2", lipid.getFa2().getName());
        assertEquals(16, lipid.getFa2().getNCarbon());
        assertEquals(2, lipid.getFa2().getNDoubleBond());
        assertEquals(0, lipid.getFa2().getNHydroxy());
    }
}

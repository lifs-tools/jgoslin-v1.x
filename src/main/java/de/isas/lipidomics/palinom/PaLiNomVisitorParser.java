/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.domain.LipidAdduct;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

/**
 *
 * @author nilshoffmann
 */
@Slf4j
public class PaLiNomVisitorParser {

    public LipidAdduct parse(String lipidString) {
        CharStream charStream = CharStreams.fromString(lipidString);
        PaLiNomLexer lexer = new PaLiNomLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        log.info("Parsing lipid identifier: {}", lipidString);
        PaLiNomParser parser = new PaLiNomParser(tokens);
        parser.setBuildParseTree(true);
        PaLiNomParser.LipidIdentifierContext context = parser.lipidIdentifier();
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new RuntimeException("Parsing of " + lipidString + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!");
        }
        LipidVisitor lipidVisitor = new LipidVisitor();
        return lipidVisitor.visit(context);
    }

}

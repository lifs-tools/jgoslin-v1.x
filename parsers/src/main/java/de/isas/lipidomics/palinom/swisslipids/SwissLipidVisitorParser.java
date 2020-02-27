/*
 * 
 */
package de.isas.lipidomics.palinom.swisslipids;

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.palinom.SwissLipidsLexer;
import de.isas.lipidomics.palinom.SwissLipidsParser;
import de.isas.lipidomics.palinom.SyntaxErrorListener;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
public class SwissLipidVisitorParser {

    public LipidAdduct parse(String lipidString, SyntaxErrorListener listener) throws ParsingException {
        return parseWithSwissLipidsGrammar(lipidString, listener);
    }

    private LipidAdduct parseWithSwissLipidsGrammar(String lipidString, SyntaxErrorListener listener) throws ParsingException, RecognitionException {
        CharStream charStream = CharStreams.fromString(lipidString);
        SwissLipidsLexer lexer = new SwissLipidsLexer(charStream);
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
        TokenStream tokens = new CommonTokenStream(lexer);
        log.info("Parsing swiss lipids identifier: {}", lipidString);
        SwissLipidsParser parser = new SwissLipidsParser(tokens);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(listener);
        parser.setBuildParseTree(true);
        SwissLipidsParser.LipidContext context = parser.lipid();
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new ParsingException("Parsing of " + lipidString + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
        }
        SwissLipidsVisitorImpl lipidVisitor = new SwissLipidsVisitorImpl();
        return lipidVisitor.visit(context);
    }
    

    public LipidAdduct parse(String lipidString) throws ParsingException {
        return parse(lipidString, new SyntaxErrorListener());
    }

}

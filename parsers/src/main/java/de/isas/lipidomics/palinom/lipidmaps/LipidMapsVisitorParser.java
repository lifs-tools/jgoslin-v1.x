/*
 * 
 */
package de.isas.lipidomics.palinom.lipidmaps;

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.palinom.LipidMapsLexer;
import de.isas.lipidomics.palinom.LipidMapsParser;
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
public class LipidMapsVisitorParser {

    public LipidAdduct parse(String lipidString, SyntaxErrorListener listener) throws ParsingException {
        return parseWithLipidMapsGrammar(lipidString, listener);
    }

    private LipidAdduct parseWithLipidMapsGrammar(String lipidString, SyntaxErrorListener listener) throws ParsingException, RecognitionException {
        CharStream charStream = CharStreams.fromString(lipidString);
        LipidMapsLexer lexer = new LipidMapsLexer(charStream);
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
        TokenStream tokens = new CommonTokenStream(lexer);
        log.info("Parsing lipid maps identifier: {}", lipidString);
        LipidMapsParser parser = new LipidMapsParser(tokens);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(listener);
        parser.setBuildParseTree(true);
        LipidMapsParser.LipidContext context = parser.lipid();
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new ParsingException("Parsing of " + lipidString + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
        }
        LipidMapsVisitorImpl lipidVisitor = new LipidMapsVisitorImpl();
        return lipidVisitor.visit(context);
    }
    

    public LipidAdduct parse(String lipidString) throws ParsingException {
        return parse(lipidString, new SyntaxErrorListener());
    }

}

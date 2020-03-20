/*
 * 
 */
package de.isas.lipidomics.palinom.goslin;

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.palinom.GoslinFragmentsLexer;
import de.isas.lipidomics.palinom.GoslinFragmentsParser;
import de.isas.lipidomics.palinom.SyntaxErrorListener;
import de.isas.lipidomics.palinom.VisitorParser;
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
public class GoslinFragmentsVisitorParser implements VisitorParser {

    @Override
    public LipidAdduct parse(String lipidString, SyntaxErrorListener listener) throws ParsingException {
        return parseWithModernGrammar(lipidString, listener);
    }

    private LipidAdduct parseWithModernGrammar(String lipidString, SyntaxErrorListener listener) throws ParsingException, RecognitionException {
        CharStream charStream = CharStreams.fromString(lipidString);
        GoslinFragmentsLexer lexer = new GoslinFragmentsLexer(charStream);
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
        TokenStream tokens = new CommonTokenStream(lexer);
        log.info("Parsing lipid identifier: {}", lipidString);
        GoslinFragmentsParser parser = new GoslinFragmentsParser(tokens);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(listener);
        parser.setBuildParseTree(true);
        GoslinFragmentsParser.LipidContext context = parser.lipid();
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new ParsingException("Parsing of " + lipidString + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
        }
        GoslinFragmentsVisitorImpl lipidVisitor = new GoslinFragmentsVisitorImpl();
        return lipidVisitor.visit(context);
    }

}

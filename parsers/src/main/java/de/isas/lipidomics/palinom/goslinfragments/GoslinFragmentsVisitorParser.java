/*
 * 
 */
package de.isas.lipidomics.palinom.goslinfragments;

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
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 * Parser implementation for the GoslinFragments grammar.
 *
 * @author nils.hoffmann
 */
@Slf4j
public class GoslinFragmentsVisitorParser implements VisitorParser<LipidAdduct> {

    @Override
    public LipidAdduct parse(String lipidString, SyntaxErrorListener listener) throws ParsingException {
        return parseWithModernGrammar(lipidString, listener);
    }

    private LipidAdduct parseWithModernGrammar(String lipidString, SyntaxErrorListener listener) throws ParsingException, RecognitionException {
        CharStream charStream = CharStreams.fromString(lipidString);
        GoslinFragmentsLexer lexer = new GoslinFragmentsLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        log.info("Parsing lipid identifier: {}", lipidString);
        GoslinFragmentsParser parser = new GoslinFragmentsParser(tokens);
        prepare(parser, lexer, listener);
        try {
            GoslinFragmentsParser.LipidContext context = parser.lipid();
            if (parser.getNumberOfSyntaxErrors() > 0) {
                throw new ParsingException("Parsing of " + lipidString + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
            }
            GoslinFragmentsVisitorImpl lipidVisitor = new GoslinFragmentsVisitorImpl();
            return lipidVisitor.visit(context);
        } catch (ParseCancellationException pce) {
            throw new ParsingException("Parsing of " + lipidString + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
        }
    }

}

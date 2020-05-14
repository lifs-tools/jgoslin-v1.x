/*
 * 
 */
package de.isas.lipidomics.palinom.swisslipids;

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.palinom.SwissLipidsLexer;
import de.isas.lipidomics.palinom.SwissLipidsParser;
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
 * Parser implementation for the SwissLipids grammar.
 *
 * @author nils.hoffmann
 */
@Slf4j
public class SwissLipidsVisitorParser implements VisitorParser<LipidAdduct> {

    @Override
    public LipidAdduct parse(String lipidString, SyntaxErrorListener listener) throws ParsingException {
        return parseWithSwissLipidsGrammar(lipidString, listener);
    }

    private LipidAdduct parseWithSwissLipidsGrammar(String lipidString, SyntaxErrorListener listener) throws ParsingException, RecognitionException {
        CharStream charStream = CharStreams.fromString(lipidString);
        SwissLipidsLexer lexer = new SwissLipidsLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        log.info("Parsing swiss lipids identifier: {}", lipidString);
        SwissLipidsParser parser = new SwissLipidsParser(tokens);
        prepare(parser, lexer, listener);
        try {
            SwissLipidsParser.LipidContext context = parser.lipid();
            if (parser.getNumberOfSyntaxErrors() > 0) {
                throw new ParsingException("Parsing of " + lipidString + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
            }
            SwissLipidsVisitorImpl lipidVisitor = new SwissLipidsVisitorImpl();
            return lipidVisitor.visit(context);
        } catch (ParseCancellationException pce) {
            throw new ParsingException("Parsing of " + lipidString + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
        }
    }

}

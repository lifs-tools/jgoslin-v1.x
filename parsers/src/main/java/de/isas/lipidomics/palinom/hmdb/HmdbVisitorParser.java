/*
 * 
 */
package de.isas.lipidomics.palinom.hmdb;

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.palinom.HMDBLexer;
import de.isas.lipidomics.palinom.HMDBParser;
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
 * Parser implementation for the Hmdb grammar.
 *
 * @author nils.hoffmann
 */
@Slf4j
public class HmdbVisitorParser implements VisitorParser<LipidAdduct> {

    @Override
    public LipidAdduct parse(String lipidString, SyntaxErrorListener listener) throws ParsingException {
        return parseWithHmdbGrammar(lipidString, listener);
    }

    private LipidAdduct parseWithHmdbGrammar(String lipidString, SyntaxErrorListener listener) throws ParsingException, RecognitionException {
        CharStream charStream = CharStreams.fromString(lipidString);
        HMDBLexer lexer = new HMDBLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        log.info("Parsing HMDB lipids identifier: {}", lipidString);
        HMDBParser parser = new HMDBParser(tokens);
        prepare(parser, lexer, listener);
        try {
            HMDBParser.LipidContext context = parser.lipid();
            if (parser.getNumberOfSyntaxErrors() > 0) {
                throw new ParsingException("Parsing of " + lipidString + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
            }
            HmdbVisitorImpl lipidVisitor = new HmdbVisitorImpl();
            return lipidVisitor.visit(context);
        } catch (ParseCancellationException pce) {
            throw new ParsingException("Parsing of " + lipidString + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
        }
    }

}

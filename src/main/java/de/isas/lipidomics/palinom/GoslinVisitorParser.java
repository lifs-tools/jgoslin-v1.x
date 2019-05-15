/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.domain.LipidAdduct;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;

/**
 *
 * @author nilshoffmann
 */
@Slf4j
public class GoslinVisitorParser {

    public LipidAdduct parse(String lipidString, SyntaxErrorListener listener) throws ParsingException {
        CharStream charStream = CharStreams.fromString(lipidString);
        GoslinLexer lexer = new GoslinLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        log.info("Parsing lipid identifier: {}", lipidString);
        GoslinParser parser = new GoslinParser(tokens);
        parser.addErrorListener(listener);
        parser.setBuildParseTree(true);
        GoslinParser.LipidContext context = parser.lipid();
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new ParsingException("Parsing of " + lipidString + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
        }
        GoslinVisitorImpl lipidVisitor = new GoslinVisitorImpl();
        return lipidVisitor.visit(context);
    }

    public LipidAdduct parse(String lipidString) throws ParsingException {
        return parse(lipidString, new SyntaxErrorListener());
    }

}

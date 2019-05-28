/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.palinom.exceptions.PalinomVisitorException;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;

/**
 *
 * @author nilshoffmann
 */
@Slf4j
public class PaLiNomVisitorParser {

    public LipidAdduct parse(String lipidString, SyntaxErrorListener listener) throws ParsingException {
//        try {
//            return parseWithLipidMapsGrammar(lipidString, listener);
//        } catch (PalinomVisitorException pve) {
//            log.info("Parsing {} with lipid maps grammar of failed, trying LipidCreator grammar!", lipidString);
        return parseWithModernGrammar(lipidString, listener);
//        }
    }

    private LipidAdduct parseWithModernGrammar(String lipidString, SyntaxErrorListener listener) throws ParsingException, RecognitionException {
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

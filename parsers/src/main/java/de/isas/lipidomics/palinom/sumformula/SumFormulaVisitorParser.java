/*
 * Copyright 2020  nils.hoffmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.isas.lipidomics.palinom.sumformula;

import de.isas.lipidomics.domain.ElementTable;
import de.isas.lipidomics.palinom.SumFormulaLexer;
import de.isas.lipidomics.palinom.SumFormulaParser;
import de.isas.lipidomics.palinom.SyntaxErrorListener;
import de.isas.lipidomics.palinom.VisitorParser;
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.TokenStream;

/**
 * Parser implementation for the SumFormula grammar.
 *
 * @author  nils.hoffmann
 */
@Slf4j
public class SumFormulaVisitorParser implements VisitorParser<ElementTable> {

    @Override
    public ElementTable parse(String sumFormula, SyntaxErrorListener listener) throws ParsingException {
        return parseWithGrammar(sumFormula, listener);
    }

    private ElementTable parseWithGrammar(String sumFormula, SyntaxErrorListener listener) throws ParsingException, RecognitionException {
        CharStream charStream = CharStreams.fromString(sumFormula);
        SumFormulaLexer lexer = new SumFormulaLexer(charStream);
        lexer.removeErrorListener(ConsoleErrorListener.INSTANCE);
        lexer.addErrorListener(listener);
        TokenStream tokens = new CommonTokenStream(lexer);
        log.info("Parsing sum formula: {}", sumFormula);
        SumFormulaParser parser = new SumFormulaParser(tokens);
        parser.removeErrorListener(ConsoleErrorListener.INSTANCE);
        parser.addErrorListener(listener);
        parser.setBuildParseTree(true);
        SumFormulaParser.MoleculeContext context = parser.molecule();
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new ParsingException("Parsing of " + sumFormula + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
        }
        SumFormulaVisitorImpl visitor = new SumFormulaVisitorImpl();
        return visitor.visit(context);
    }
}

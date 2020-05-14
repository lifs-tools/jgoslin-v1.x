/*
 * Copyright 2019 nils.hoffmann.
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
package de.isas.lipidomics.palinom;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.ParseCancellationException;

/**
 *
 * @author nils.hoffmann
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class SyntaxErrorListener extends BaseErrorListener {

    private final List<SyntaxError> syntaxErrors = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line, int charPositionInLine,
            String msg, RecognitionException e) {
        syntaxErrors.add(new SyntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e));
        throw new ParseCancellationException(getErrorString());
    }

    public String getErrorString() {
        return syntaxErrors.stream().map((error) -> {
            if (error.getOffendingSymbol() == null) {
                return String.format(
                        "Parser %s syntax error at line %d, position %d: %s",
                        error.getRecognizer().getGrammarFileName(),
                        error.getLine(),
                        error.getCharPositionInLine(),
                        error.getMessage());
            } else {
                return String.format(
                        "Parser %s syntax error on '%s', at line %d, position %d: %s",
                        error.getRecognizer().getGrammarFileName(),
                        error.getOffendingSymbol(),
                        error.getLine(),
                        error.getCharPositionInLine(),
                        error.getMessage());
            }
        }).collect(Collectors.joining("\n"));
    }

}

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
package de.isas.lipidomics.palinom.comparison;

import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.palinom.LipidMapsLexer;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.SyntaxErrorListener;
import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.palinom.lipidmaps.LipidMapsVisitorParser;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
public class LipidMapsComparisonTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/de/isas/lipidomics/palinom/testfiles/lipid-maps-test.csv", numLinesToSkip = 0, delimiter = '\t', encoding = "UTF-8", lineSeparator = "\n")
    public void isValidLipidNameForLipidMapsTest(String lipidName) throws ParsingException {
        CharStream charStream = CharStreams.fromString(lipidName);
        LipidMapsLexer lexer = new LipidMapsLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        log.info("Parsing LipidMaps identifier: {}", lipidName);
        LipidMapsParser parser = new LipidMapsParser(tokens);
        SyntaxErrorListener listener = new SyntaxErrorListener();
        parser.addErrorListener(listener);
        parser.setBuildParseTree(true);
        LipidMapsParser.LipidContext context = parser.lipid();
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new ParsingException("Parsing of " + lipidName + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
        }
        LipidAdduct la = null;
        LipidMapsVisitorParser visitorParser = new LipidMapsVisitorParser();
        if ("MGDG(18:0(9Z)/18:2(9Z,12Z))".equals(lipidName)) {
            try {
                la = visitorParser.parse(lipidName, listener);
                Assertions.fail("Lipid " + lipidName + " should cause ConstraintViolationException!");
            } catch (ConstraintViolationException cve) {
            }
        } else {
            la = visitorParser.parse(lipidName, listener);
            Assertions.assertNotNull(la);
        }
    }

}

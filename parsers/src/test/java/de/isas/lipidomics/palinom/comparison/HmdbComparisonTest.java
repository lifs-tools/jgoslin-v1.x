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
import de.isas.lipidomics.palinom.HMDBLexer;
import de.isas.lipidomics.palinom.HMDBParser;
import de.isas.lipidomics.palinom.SyntaxErrorListener;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.palinom.hmdb.HmdbVisitorParser;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
public class HmdbComparisonTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/de/isas/lipidomics/palinom/testfiles/hmdb-test.csv", numLinesToSkip = 0, delimiter = '\t', encoding = "UTF-8", lineSeparator = "\n")
    public void isValidLipidNameForHmdbTest(String lipidName) throws ParsingException {
        CharStream charStream = CharStreams.fromString(lipidName);
        HMDBLexer lexer = new HMDBLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        log.info("Parsing HMDB identifier: {}", lipidName);
        HMDBParser parser = new HMDBParser(tokens);
        SyntaxErrorListener listener = new SyntaxErrorListener();
        parser.addErrorListener(listener);
        parser.setBuildParseTree(true);
        HMDBParser.LipidContext context = parser.lipid();
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new ParsingException("Parsing of " + lipidName + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
        }
        HmdbVisitorParser visitorParser = new HmdbVisitorParser();
        if ( // expected failures, since these are currently unsupported
               lipidName.contains("i-") 
            || lipidName.contains("a-") 
            || lipidName.endsWith("[rac]") 
            || lipidName.matches(".*\\([MD0-9]+(/[MD0-9]+)?(/[0-9:]+)?\\).*")
            || lipidName.contains("MonoMe")
            || lipidName.contains("DiMe")
            ) {
            assertThrows(ParseTreeVisitorException.class, () -> {
                LipidAdduct la = visitorParser.parse(lipidName, listener);
            });
        } else {
            LipidAdduct la = visitorParser.parse(lipidName, listener);
            Assertions.assertNotNull(la);
        }
        String ce = "CE(13D3)";
    }
}

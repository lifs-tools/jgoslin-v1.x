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

import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
@RunWith(JUnitParamsRunner.class)
public class LipidMapsNamesTest {

    @Test
    @FileParameters("classpath:de/isas/lipidomics/palinom/wenk-lm-lipids.txt")
    public void isValidLipidMapsNameForSingaporeanStudy(String lipidMapsName) throws ParsingException {
        CharStream charStream = CharStreams.fromString(lipidMapsName);
        LipidMapsLexer lexer = new LipidMapsLexer(charStream);
        TokenStream tokens = new CommonTokenStream(lexer);
        log.info("Parsing lipid maps identifier: {}", lipidMapsName);
        LipidMapsParser parser = new LipidMapsParser(tokens);
        SyntaxErrorListener listener = new SyntaxErrorListener();
        parser.addErrorListener(listener);
        parser.setBuildParseTree(true);
        LipidMapsParser.LipidContext context = parser.lipid();
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new ParsingException("Parsing of " + lipidMapsName + " failed with " + parser.getNumberOfSyntaxErrors() + " syntax errors!\n" + listener.getErrorString());
        }
    }

    @Test
    @FileParameters("classpath:de/isas/lipidomics/palinom/wenk-lm-lipids.txt")
    public void isValidLipidMapsNameForVisitorParser(String lipidMapsName) throws ParsingException {
        log.info("Parsing lipid maps identifier: {}", lipidMapsName);
        LipidMapsVisitorParser parser = new LipidMapsVisitorParser();
        LipidAdduct lipidAdduct = parser.parse(lipidMapsName);
        LipidSpecies ls = lipidAdduct.getLipid();
        log.info("Lipid maps name {}:{}", lipidMapsName, ls.getLipidString(ls.getInfo().orElse(LipidSpeciesInfo.NONE).getLevel()));
    }
}

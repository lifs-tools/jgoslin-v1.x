/*
 * Copyright 2020 nilshoffmann.
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
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
public class SwissLipidsCompleteIT {

    @ParameterizedTest
    @CsvFileSource(resources = "/de/isas/lipidomics/palinom/swisslipids-names-Feb-10-2020.tsv", numLinesToSkip = 1, delimiter = '\t', encoding = "UTF-8", lineSeparator = "\n")
    public void isValidSwissLipidsNameForCurrentSwissLipids(
            String swissLipidsId,
            String swissLipidsLevel,
            String swissLipidsName,
            String swissLipidsAbbreviation,
            String swissLipidsSynonyms1,
            String swissLipidsSynonyms2,
            String swissLipidsSynonyms3,
            String swissLipidsSynonyms4,
            String swissLipidsSynonyms5
    ) throws ParsingException, IOException {
        String lipidName = swissLipidsAbbreviation;
        LipidMapsVisitorParser parser = new LipidMapsVisitorParser();
        LipidAdduct lipidAdduct;
        try {
            lipidAdduct = parser.parse(lipidName);
            LipidSpecies ls = lipidAdduct.getLipid();
            assertNotNull(ls);
        } catch (ParsingException ex) {
            fail("Parsing current SwissLipids identifier: " + swissLipidsAbbreviation + " with transformed name " + lipidName + " failed - name unsupported in grammar!");
        } catch (ParseTreeVisitorException pve) {
            fail("Parsing current SwissLipids identifier: " + swissLipidsAbbreviation + " with transformed name " + lipidName + " failed - missing implementation!");
        }
    }
}

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

import de.isas.lipidomics.palinom.goslin.GoslinVisitorParser;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.palinom.lipidmaps.LipidMapsVisitorParser;
import de.isas.lipidomics.palinom.swisslipids.SwissLipidsVisitorParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
public class GoslinComparisonTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/de/isas/lipidomics/palinom/testfiles/goslin-short.csv", numLinesToSkip = 0, delimiter = '\t', encoding = "UTF-8", lineSeparator = "\n")
    public void isValidLipidNameForGoslinShort(String lipidName) throws ParsingException {
        GoslinVisitorParser parser = new GoslinVisitorParser();
        LipidAdduct la = parser.parse(lipidName);
        Assertions.assertNotNull(la);
//        LipidSpecies l = la.getLipid();
//        log.info("{}\t{} ({})", lipidName, l.getLipidString());
//        assertEquals(lipidName, l.getLipidString());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/de/isas/lipidomics/palinom/testfiles/goslin-test.csv", numLinesToSkip = 0, delimiter = '\t', encoding = "UTF-8", lineSeparator = "\n")
    public void isValidLipidNameForGoslinTest(String lipidName) throws ParsingException {
        GoslinVisitorParser parser = new GoslinVisitorParser();
        LipidAdduct la = parser.parse(lipidName);
        Assertions.assertNotNull(la);
//        LipidSpecies l = la.getLipid();
//        log.info("{}\t{} ({})", lipidName, l.getLipidString());
//        assertEquals(lipidName, l.getLipidString());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/de/isas/lipidomics/palinom/testfiles/lipid-maps-test.csv", numLinesToSkip = 0, delimiter = '\t', encoding = "UTF-8", lineSeparator = "\n")
    public void isValidLipidNameForLipidMapsTest(String lipidName) throws ParsingException {
        LipidMapsVisitorParser parser = new LipidMapsVisitorParser();
        LipidAdduct la = parser.parse(lipidName);
        Assertions.assertNotNull(la);
//        LipidSpecies l = la.getLipid();
//        log.info("{}\t{} ({})", lipidName, l.getLipidString());
//        assertEquals(lipidName, l.getLipidString());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/de/isas/lipidomics/palinom/testfiles/swiss-lipids-test.csv", numLinesToSkip = 0, delimiter = '\t', encoding = "UTF-8", lineSeparator = "\n")
    public void isValidLipidNameForSwissLipidsTest(String lipidName) throws ParsingException {
        SwissLipidsVisitorParser parser = new SwissLipidsVisitorParser();
        LipidAdduct la = parser.parse(lipidName);
        Assertions.assertNotNull(la);
//        LipidSpecies l = la.getLipid();
//        log.info("{}\t{} ({})", lipidName, l.getLipidString());
//        assertEquals(lipidName, l.getLipidString());
    }
}

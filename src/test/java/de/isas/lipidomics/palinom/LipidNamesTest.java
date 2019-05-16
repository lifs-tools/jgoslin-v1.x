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

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author nils.hoffmann
 */
@RunWith(JUnitParamsRunner.class)
public class LipidNamesTest {

    @Test
    @FileParameters("classpath:de/isas/lipidomics/palinom/lipidnames.txt")
    public void isValidLipidName(String lipidName) throws ParsingException {
        GoslinVisitorParser parser = new GoslinVisitorParser();
        parser.parse(lipidName);
    }

    @Test
    @FileParameters("classpath:de/isas/lipidomics/palinom/lipidnames-invalid.txt")
    public void isInvalidLipidName(String lipidName) {
        GoslinVisitorParser parser = new GoslinVisitorParser();
        try {
            parser.parse(lipidName);
            Assert.fail("Test case for " + lipidName + " should cause parsing error!");
        } catch (ParsingException rex) {

        }
    }
    
    @Test
    @FileParameters("classpath:de/isas/lipidomics/palinom/wenk-lipids.txt")
    public void isValidLipidNameForSingaporeanStudy(String lipidName) throws ParsingException {
        GoslinVisitorParser parser = new GoslinVisitorParser();
        parser.parse(lipidName);
    }
}

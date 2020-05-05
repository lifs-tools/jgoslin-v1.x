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
package de.isas.lipidomics.domain;

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.palinom.goslin.GoslinVisitorParser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author nilshoffmann
 */
public class LipidAdductTest {

    @Test
    public void testCalculateMass() throws ParsingException {
        double expectedMass = 673.4813805;
        String lipidAdduct = "PA 16:0-18:1[M-H]1-";
        GoslinVisitorParser gvp = new GoslinVisitorParser();
        LipidAdduct la = gvp.parse(lipidAdduct);
        assertEquals(expectedMass, la.getMass()-Element.ELEMENT_H.getMass(), 1e-10);
    }

    @Test
    public void testCalculateSumFormula() throws ParsingException {
        String expectedSumFormula = "C37H71O8P";
        String lipidAdduct = "PA 16:0-18:1[M-H]1-";
        GoslinVisitorParser gvp = new GoslinVisitorParser();
        LipidAdduct la = gvp.parse(lipidAdduct);
        assertEquals(expectedSumFormula, la.getSumFormula());
        
        expectedSumFormula = "C51H90NO8P";
        lipidAdduct = "PC 21:0-22:6[M+CH3COO]1-";
        la = gvp.parse(lipidAdduct);
        assertEquals(expectedSumFormula, la.getSumFormula());
        double expectedMass = 934.6542591;
        assertEquals(expectedMass, la.getMass(), 1e-10);
    }
}

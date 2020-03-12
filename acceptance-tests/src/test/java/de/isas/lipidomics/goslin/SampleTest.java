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
package de.isas.lipidomics.goslin;

import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.palinom.goslin.GoslinVisitorParser;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author nilshoffmann
 */
public class SampleTest {
    
    
    @Test
    public void testLipidParser() throws ParsingException {
        GoslinVisitorParser gp = new GoslinVisitorParser();
        LipidAdduct la = gp.parse("PC 18:1;1/20:2");
        Assert.assertNotNull(la);
    }
}

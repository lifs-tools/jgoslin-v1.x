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
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.palinom.exceptions.PalinomVisitorException;
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import java.io.IOException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author nilshoffmann
 */
@Slf4j
@RunWith(JUnitParamsRunner.class)
public class SwissLipidsCompleteTest {

    @Test
    @Parameters(source = SwissLipidsProvider.class)
    public void isValidSwissLipidsNameForCurrentSwissLipids(String swissLipidsName) throws ParsingException, IOException {
        log.info("Parsing current SwissLipids identifier: {}", swissLipidsName);
        GoslinVisitorParser parser = new GoslinVisitorParser();
        LipidAdduct lipidAdduct;
        try {
            lipidAdduct = parser.parse(swissLipidsName);
            LipidSpecies ls = lipidAdduct.getLipid();
            log.info("SwissLipids name {}: {}", swissLipidsName, ls.getLipidString(ls.getInfo().orElse(LipidSpeciesInfo.NONE).getLevel()));
            Assert.assertEquals(swissLipidsName, ls.getLipidString(ls.getInfo().orElse(LipidSpeciesInfo.NONE).getLevel()));
        } catch (ParsingException ex) {
            log.info("Parsing current SwissLipids identifier: {} failed - name unsupported in grammar!", swissLipidsName);
//            log.error("Caught parsing exception:", ex);
//            Assert.fail(ex.getMessage());
        } catch (PalinomVisitorException pve) {
            log.info("Parsing current SwissLipids identifier: {} failed - missing implementation!", swissLipidsName);
//            log.error("Caught palinom visitor exception:", pve);
//            Assert.fail(pve.getMessage());
        }
    }
}

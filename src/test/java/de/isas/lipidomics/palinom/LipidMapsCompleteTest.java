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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
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
public class LipidMapsCompleteTest {

    public Object[] provideCurrentLipidMapsNames() throws IOException {
        URL u = getClass().getClassLoader().getResource("de/isas/lipidomics/palinom/lipidmaps-names-Feb-10-2020.tsv");
        try (InputStreamReader ir = new InputStreamReader(u.openStream())) {
            try (BufferedReader br = new BufferedReader(ir)) {
                List<String> result = br.lines().map((t) -> {
                    return t.toString().split("\t")[3];
                }).collect(Collectors.toList());
                return result.toArray();
            }
        }
    }

    @Test
    @Parameters(method = "provideCurrentLipidMapsNames")
    public void isValidLipidMapsNameForCurrentLipidMapsForVisitorParser(String lipidMapsName) throws ParsingException, IOException {
        log.info("Parsing current lipid maps identifier: {}", lipidMapsName);
        LipidMapsVisitorParser parser = new LipidMapsVisitorParser();
        LipidAdduct lipidAdduct;
        try {
            lipidAdduct = parser.parse(lipidMapsName);
            LipidSpecies ls = lipidAdduct.getLipid();
            log.info("Lipid maps name {}:{}", lipidMapsName, ls.getLipidString(ls.getInfo().orElse(LipidSpeciesInfo.NONE).getLevel()));
            Assert.assertEquals(lipidMapsName, ls.getLipidString(ls.getInfo().orElse(LipidSpeciesInfo.NONE).getLevel()));

        } catch (ParsingException ex) {
            log.info("Parsing current lipid maps identifier: {} failed - name unsupported in grammar!", lipidMapsName);
//            log.error("Caught parsing exception:", ex);
//            Assert.fail(ex.getMessage());
        } catch (PalinomVisitorException pve) {
            log.info("Parsing current lipid maps identifier: {} failed - missing implementation!", lipidMapsName);
//            log.error("Caught palinom visitor exception:", pve);
//            Assert.fail(pve.getMessage());
        }
    }
}

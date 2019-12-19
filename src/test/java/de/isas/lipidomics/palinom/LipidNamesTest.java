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
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import org.junit.Assert;
import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
@RunWith(JUnitParamsRunner.class)
public class LipidNamesTest {

    @Test
    @FileParameters("classpath:de/isas/lipidomics/palinom/wenk-lipids.txt")
    public void isValidLipidNameForSingaporeanStudy(String lipidName) throws ParsingException {
        GoslinVisitorParser parser = new GoslinVisitorParser();
        LipidAdduct la = parser.parse(lipidName);
        LipidSpecies l = la.getLipid();
        log.info("{}\t{} ({})", lipidName, l.getLipidString(l.getInfo().get().getLevel()), l.getInfo().get().getLevel());
        Assert.assertEquals(lipidName, l.getLipidString(l.getInfo().get().getLevel()));
    }
}

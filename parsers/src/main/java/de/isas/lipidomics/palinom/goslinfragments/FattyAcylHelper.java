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
package de.isas.lipidomics.palinom.goslinfragments;

import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.palinom.GoslinFragmentsParser;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;

/**
 *
 * @author nilshoffmann
 */
public class FattyAcylHelper {

    public LipidFaBondType getLipidLcbBondType(String headGroup, GoslinFragmentsParser.LcbContext lcbContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        return lfbt;
    }

    public Integer getNHydroxyl(GoslinFragmentsParser.LcbContext lcbContext) {
        Integer hydroxyl = 0;
        if (lcbContext.lcb_pure() != null) {
            GoslinFragmentsParser.Lcb_pureContext pureCtx = lcbContext.lcb_pure();
            return HandlerUtils.asInt(pureCtx, hydroxyl);
        }
        throw new ParseTreeVisitorException("Uninitialized lcb_core context!");
    }

    public LipidFaBondType getLipidFaBondType(String headGroup, GoslinFragmentsParser.FaContext faContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        if (faContext.ether() != null) {
            if ("a".equals(faContext.ether().getText())) {
                lfbt = LipidFaBondType.ETHER_PLASMANYL;
            } else if ("p".equals(faContext.ether().getText())) {
                lfbt = LipidFaBondType.ETHER_PLASMENYL;
            } else {
                throw new ParseTreeVisitorException("Unknown ether context value: " + faContext.ether());
            }
        }
        return lfbt;
    }
}

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

import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.palinom.GoslinFragmentsParser;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Helper class for FA and LCB handling.
 *
 * @author nilshoffmann
 */
public class FattyAcylHelper {

    public LipidFaBondType getLipidLcbBondType(HeadGroup headGroup, GoslinFragmentsParser.LcbContext lcbContext) throws ParseTreeVisitorException {
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

    public LipidFaBondType getLipidFaBondType(HeadGroup headGroup, GoslinFragmentsParser.FaContext faContext) throws ParseTreeVisitorException {
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

    public Map<Integer, String> resolveDoubleBondPosition(GoslinFragmentsParser.Db_positionContext dbContext, Map<Integer, String> doubleBondPositions) {
        if (dbContext.db_single_position() != null) {
            doubleBondPositions.put(
                    Integer.parseInt(dbContext.db_single_position().db_position_number().getText()),
                    Optional.ofNullable(dbContext.db_single_position().cistrans()).map((t) -> {
                        return t.getText();
                    }).orElse(""));
        } else {
            for (GoslinFragmentsParser.Db_positionContext dbSubContext : dbContext.db_position()) {
                resolveDoubleBondPosition(dbSubContext, doubleBondPositions);
            }
        }
        return doubleBondPositions;
    }

    /**
     * Resolve double bond positions from the given Db_positionsContext.
     *
     * @param context the double bond context.
     * @return a map of position to double bond configuration mappings.
     */
    public Map<Integer, String> resolveDoubleBondPositions(GoslinFragmentsParser.Db_positionsContext context) {
        Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
        if (context.db_position() != null) {
            return resolveDoubleBondPosition(context.db_position(), doubleBondPositions);
        } else {
            throw new ParseTreeVisitorException("Unhandled state in IsomericFattyAcid - double bond positions!");
        }
    }
}

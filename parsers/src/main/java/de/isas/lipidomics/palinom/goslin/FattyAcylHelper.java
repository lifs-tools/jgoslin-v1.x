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
package de.isas.lipidomics.palinom.goslin;

import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.palinom.GoslinParser;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Helper class for FA and LCB handling.
 *
 * @author nilshoffmann
 */
public class FattyAcylHelper {

    /**
     * Returns the lipid fa bond type for an LcbContext.
     *
     * @param headGroup the head group.
     * @param lcbContext the lcb context.
     * @return the lipid fa bond type.
     */
    public LipidFaBondType getLipidLcbBondType(HeadGroup headGroup, GoslinParser.LcbContext lcbContext) {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        return lfbt;
    }

    /**
     * Returns the number of hydroxyls for an LcbContext.
     *
     * @param lcbContext the lcb context.
     * @return the number of hydroxyls.
     */
    public Integer getNHydroxyl(GoslinParser.LcbContext lcbContext) {
        Integer hydroxyl = 0;
        if (lcbContext.lcb_pure() != null) {
            GoslinParser.Lcb_pureContext pureCtx = lcbContext.lcb_pure();
            return HandlerUtils.asInt(pureCtx, hydroxyl);
        }
        throw new ParseTreeVisitorException("Uninitialized lcb_core context!");
    }

    /**
     * Returns the lipid fa bond type for an FaContext.
     *
     * @param headGroup the head group.
     * @param faContext the fa context.
     * @return the lipid fa bond type.
     * @throws ParseTreeVisitorException for unknown ether context values.
     */
    public LipidFaBondType getLipidFaBondType(HeadGroup headGroup, GoslinParser.FaContext faContext) throws ParseTreeVisitorException {
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

    /**
     * Resolve double bond positions from the given Db_positionContext.
     *
     * @param dbContext the double bond context.
     * @param doubleBondPositions a map of position to double bond configuration
     * mappings.
     * @return a map of position to double bond configuration mappings.
     */
    public Map<Integer, String> resolveDoubleBondPosition(GoslinParser.Db_positionContext dbContext, Map<Integer, String> doubleBondPositions) {
        if (dbContext.db_single_position() != null) {
            doubleBondPositions.put(
                    Integer.parseInt(dbContext.db_single_position().db_position_number().getText()),
                    Optional.ofNullable(dbContext.db_single_position().cistrans()).map((t) -> {
                        return t.getText();
                    }).orElse(""));
        } else {
            for (GoslinParser.Db_positionContext dbSubContext : dbContext.db_position()) {
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
    public Map<Integer, String> resolveDoubleBondPositions(GoslinParser.Db_positionsContext context) {
        Map<Integer, String> doubleBondPositions = new TreeMap<>();
        if (context.db_position() != null) {
            return resolveDoubleBondPosition(context.db_position(), doubleBondPositions);
        } else {
            throw new ParseTreeVisitorException("Unhandled state in IsomericFattyAcid - double bond positions!");
        }
    }
}

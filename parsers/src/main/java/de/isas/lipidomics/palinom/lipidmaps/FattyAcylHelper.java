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
package de.isas.lipidomics.palinom.lipidmaps;

import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.ModificationsList;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.LipidMapsParser.ModificationContext;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Helper class for FA and LCB handling.
 *
 * @author nilshoffmann
 */
public class FattyAcylHelper {

    public LipidFaBondType getLipidLcbBondType(LipidMapsParser.LcbContext lcbContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        return lfbt;
    }

    public LipidFaBondType getLipidFaBondType(LipidMapsParser.FaContext faContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        if (faContext.fa_unmod().ether() != null) {
            if (null == faContext.fa_unmod().ether().getText()) {
                throw new ParseTreeVisitorException("Undefined ether context value!");
            } else {
                switch (faContext.fa_unmod().ether().getText()) {
                    case "O-":
                        lfbt = LipidFaBondType.ETHER_PLASMANYL;
                        break;
                    case "P-":
                        lfbt = LipidFaBondType.ETHER_PLASMENYL;
                        break;
                    default:
                        throw new ParseTreeVisitorException("Unknown ether context value: " + faContext.fa_unmod().ether());
                }
            }
        }
        return lfbt;
    }

    public Integer getHydroxyCount(LipidMapsParser.LcbContext ctx) {
        Integer nHydroxy = Optional.ofNullable(ctx.hydroxyl_lcb()).map((t) -> {
            String hydroxy = t.getText();
            switch (hydroxy) {
                case "m":
                    return 1;
                case "d":
                    return 2;
                case "t":
                    return 3;
            }
            return Integer.valueOf(0);
        }).orElse(0);
        return nHydroxy;
    }

    public Map<Integer, String> resolveDoubleBondPosition(LipidMapsParser.Db_positionContext dbContext, Map<Integer, String> doubleBondPositions) {
        if (dbContext.db_single_position() != null) {
            doubleBondPositions.put(
                    Integer.parseInt(dbContext.db_single_position().db_position_number().getText()),
                    Optional.ofNullable(dbContext.db_single_position().cistrans()).map((t) -> {
                        return t.getText();
                    }).orElse(""));
        } else {
            for (LipidMapsParser.Db_positionContext dbSubContext : dbContext.db_position()) {
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
    public Map<Integer, String> resolveDoubleBondPositions(LipidMapsParser.Db_positionsContext context) {
        Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
        if (context.db_position() != null) {
            return resolveDoubleBondPosition(context.db_position(), doubleBondPositions);
        } else {
            throw new ParseTreeVisitorException("Unhandled state in IsomericFattyAcid - double bond positions!");
        }
    }

    public ModificationsList resolveModification(ModificationContext context, ModificationsList mods) {
        Integer contextNumber = -1;
        String modText = "";
        if (context.number() != null) {
            contextNumber = HandlerUtils.asInt(context.number(), -1);
        }
        if (context.mod_text() != null) {
            modText = context.mod_text().getText();
            mods.add(Pair.of(contextNumber, modText));
        }
        if (context.modification() != null) {
            return resolveModificationList(context.modification(), mods);
        }
        return mods;
    }

    public ModificationsList resolveModificationList(List<ModificationContext> modifications, ModificationsList mods) {
        for (ModificationContext context : modifications) {
            resolveModification(context, mods);
        }
        return mods;
    }

    public ModificationsList resolveModifications(LipidMapsParser.ModificationContext modifications) {
        if (modifications != null) {
            ModificationsList mods = new ModificationsList();
            return resolveModificationList(Arrays.asList(modifications), mods);
        } else {
            throw new ParseTreeVisitorException("Unhandled state in FattyAcid Modifications!");
        }
    }
}

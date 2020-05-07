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
package de.isas.lipidomics.palinom.hmdb;

import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.palinom.HMDBParser;
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

    public LipidFaBondType getLipidLcbBondType(HeadGroup headGroup, HMDBParser.LcbContext lcbContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        return lfbt;
    }

    public Integer getNHydroxyl(HMDBParser.LcbContext lcbContext) {
        Integer hydroxyl = 0;
        if (lcbContext.lcb_core() != null) {
            HMDBParser.Lcb_coreContext coreCtx = lcbContext.lcb_core();
            if (coreCtx.hydroxyl() != null) {
                switch (coreCtx.hydroxyl().getText()) {
                    case "t":
                        hydroxyl = 3;
                        break;
                    case "d":
                        hydroxyl = 2;
                        break;
                    case "m":
                        hydroxyl = 1;
                        break;
                    default:
                        throw new ParseTreeVisitorException("Unsupported old hydroxyl prefix: " + coreCtx.hydroxyl().getText());
                }
                return hydroxyl;
            }
        }
        throw new ParseTreeVisitorException("Uninitialized lcb_core context!");
    }

    public LipidFaBondType getLipidFaBondType(HMDBParser.FaContext faContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        if (faContext.fa_core() != null && faContext.fa_core().ether() != null) {
            if ("O-".equals(faContext.fa_core().ether().getText().toUpperCase())) {
                lfbt = LipidFaBondType.ETHER_PLASMANYL;
            } else if ("P-".equals(faContext.fa_core().ether().getText())) {
                lfbt = LipidFaBondType.ETHER_PLASMENYL;
            } else if ("i-".equals(faContext.fa_core().ether().getText()) || "a-".equals(faContext.fa_core().ether().getText())) {
//                lfbt = LipidFaBondType.ETHER_UNSPECIFIED;
                throw new ParseTreeVisitorException("Unsupported FA prefix: " + faContext.fa_core().ether().getText() + ". Please contact the developers at https://lifs.isas.de/support for assistance.");
            } else {
                throw new ParseTreeVisitorException("Unknown ether context value: " + faContext.fa_core().ether());
            }
        }

        return lfbt;
    }

    public Map<Integer, String> resolveDoubleBondPosition(HMDBParser.Db_positionContext dbContext, Map<Integer, String> doubleBondPositions) {
        if (dbContext.db_single_position() != null) {
            doubleBondPositions.put(
                    Integer.parseInt(dbContext.db_single_position().db_position_number().getText()),
                    Optional.ofNullable(dbContext.db_single_position().cistrans()).map((t) -> {
                        return t.getText();
                    }).orElse(""));
        } else {
            for (HMDBParser.Db_positionContext dbSubContext : dbContext.db_position()) {
                resolveDoubleBondPosition(dbSubContext, doubleBondPositions);
            }
        }
        return doubleBondPositions;
    }

    /**
     * Resolve double bond positions from the given Db_positionsContext.
     *
     * @param lfbt the bond type between lipid head group and fatty acyl.
     * @param context the double bond context.
     * @return a map of position to double bond configuration mappings.
     */
    public Map<Integer, String> resolveDoubleBondPositions(LipidFaBondType lfbt, HMDBParser.Db_positionsContext context) {
        Map<Integer, String> doubleBondPositions = new TreeMap<>();
        if (context.db_position() != null) {
            if (lfbt == LipidFaBondType.ETHER_PLASMENYL) {
                doubleBondPositions.put(1, "Z"); // add implicit double bond for plasmenyls
            }
            return resolveDoubleBondPosition(context.db_position(), doubleBondPositions);
        } else {
            throw new ParseTreeVisitorException("Unhandled state in IsomericFattyAcid - double bond positions!");
        }
    }
}

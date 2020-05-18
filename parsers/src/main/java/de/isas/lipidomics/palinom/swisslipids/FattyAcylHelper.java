/*
 * Copyright 2020  nils.hoffmann.
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
package de.isas.lipidomics.palinom.swisslipids;

import de.isas.lipidomics.domain.Element;
import de.isas.lipidomics.domain.ElementTable;
import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.domain.LipidClass;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.ModificationsList;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.SwissLipidsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Helper class for FA and LCB handling.
 *
 * @author nils.hoffmann
 */
class FattyAcylHelper {

    public LipidFaBondType getLipidLcbBondType(HeadGroup headGroup, SwissLipidsParser.LcbContext lcbContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        return lfbt;
    }

    public Integer getNHydroxyl(SwissLipidsParser.LcbContext lcbContext) {
        Integer hydroxyl = 0;
        if (lcbContext.lcb_core() != null) {
            SwissLipidsParser.Lcb_coreContext coreCtx = lcbContext.lcb_core();
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

    public LipidFaBondType getLipidFaBondType(SwissLipidsParser.FaContext faContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        if (faContext.fa_core() != null && faContext.fa_core().ether() != null) {
            if ("O-".equals(faContext.fa_core().ether().getText())) {
                lfbt = LipidFaBondType.ETHER_PLASMANYL;
            } else if ("P-".equals(faContext.fa_core().ether().getText())) {
                lfbt = LipidFaBondType.ETHER_PLASMENYL;
            } else {
                throw new ParseTreeVisitorException("Unknown ether context value: " + faContext.fa_core().ether());
            }
        }

        return lfbt;
    }

    public Map<Integer, String> resolveDoubleBondPosition(SwissLipidsParser.Db_positionContext dbContext, Map<Integer, String> doubleBondPositions) {
        if (dbContext.db_single_position() != null) {
            doubleBondPositions.put(
                    Integer.parseInt(dbContext.db_single_position().db_position_number().getText()),
                    Optional.ofNullable(dbContext.db_single_position().cistrans()).map((t) -> {
                        return t.getText();
                    }).orElse(""));
        } else {
            for (SwissLipidsParser.Db_positionContext dbSubContext : dbContext.db_position()) {
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
    public Map<Integer, String> resolveDoubleBondPositions(LipidFaBondType lfbt, SwissLipidsParser.Db_positionsContext context) {
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

    /**
     * Returns the number of double bonds and the number of carbons that need to
     * be subtracted from the sterol ester head group to calculate the proper
     * sum formula and mass on species levels.
     *
     * @param headGroup the head group
     * @return a map with slots "doubleBondCorrection" and "carbonCorrection".
     */
    public Map<String, Integer> getSterolSpeciesCountCorrection(HeadGroup headGroup) {
        Integer doubleBondCorrection = 0;
        Integer carbonCorrection = 0;
        LipidClass lipidClass = headGroup.getLipidClass();
        switch (lipidClass) {
            case SE_27_1:
                doubleBondCorrection = 1;
                break;
            case SE_27_2:
            case SE_28_2:
            case SE_29_2:
            case SE_30_2:
                doubleBondCorrection = 2;
                break;
            case SE_28_3:
                doubleBondCorrection = 3;
                break;
        }
        switch (lipidClass) {
            case SE_27_1:
            case SE_27_2:
            case SE_28_2:
            case SE_29_2:
            case SE_30_2:
            case SE_28_3:
                ElementTable et = lipidClass.getElements();
                carbonCorrection = et.get(Element.ELEMENT_C);
                break;
        }
        Map<String, Integer> map = new HashMap<>();
        map.put("doubleBondCorrection", doubleBondCorrection);
        map.put("carbonCorrection", carbonCorrection);
        return map;
    }

    public ModificationsList resolveModification(SwissLipidsParser.Fa_lcb_suffixContext context, ModificationsList mods) {
        Integer contextNumber = -1;
        if (context.fa_lcb_suffix_core() != null) {
            if (context.fa_lcb_suffix_core().fa_lcb_suffix_number() != null) {
                contextNumber = HandlerUtils.asInt(context.fa_lcb_suffix_core().fa_lcb_suffix_number(), 1);
            }
            if (context.fa_lcb_suffix_core().fa_lcb_suffix_type() != null) {
                String modText = context.fa_lcb_suffix_core().fa_lcb_suffix_type().getText();
                mods.add(Pair.of(contextNumber, modText));
            } // insert recursive handling here, if required, see LipidMAPS FattyAcylHandler
        }
        return mods;
    }

    public ModificationsList resolveModificationList(List<SwissLipidsParser.Fa_lcb_suffixContext> modifications, ModificationsList mods) {
        for (SwissLipidsParser.Fa_lcb_suffixContext context : modifications) {
            resolveModification(context, mods);
        }
        return mods;
    }

    public ModificationsList resolveModifications(SwissLipidsParser.Fa_lcb_suffixContext modifications) {
        if (modifications != null) {
            ModificationsList mods = new ModificationsList();
            return resolveModificationList(Arrays.asList(modifications), mods);
        } else {
            throw new ParseTreeVisitorException("Unhandled state in FattyAcid Modifications!");
        }
    }
}

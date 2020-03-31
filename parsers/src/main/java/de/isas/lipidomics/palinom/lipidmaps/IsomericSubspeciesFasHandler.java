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

import de.isas.lipidomics.domain.IsomericFattyAcid;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.StructuralFattyAcid;
import static de.isas.lipidomics.palinom.HandlerUtils.asInt;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author nilshoffmann
 */
public class IsomericSubspeciesFasHandler {

    private final FattyAcylHelper faHelper;

    public IsomericSubspeciesFasHandler(FattyAcylHelper faHelper) {
        this.faHelper = faHelper;
    }

    public StructuralFattyAcid buildIsomericFa(LipidMapsParser.FaContext ctx, String faName, int position) {
        IsomericFattyAcid.IsomericFattyAcidBuilder fa = IsomericFattyAcid.isomericFattyAcidBuilder();
        String modifications = "";
        if (ctx.fa_mod() != null) {
            if (ctx.fa_mod().modification() != null) {
                modifications = ctx.fa_mod().modification().getText();
            }
        }
        if (ctx.fa_unmod() != null) {
            LipidFaBondType faBondType = faHelper.getLipidFaBondTypeUnmod(ctx);
            int plasmenylEtherDbBondCorrection = 0;
//            switch (faBondType) {
//                case ETHER_PLASMENYL:
//                    plasmenylEtherDbBondCorrection = 1;
//                default:
//                    plasmenylEtherDbBondCorrection = 0;
//            }
            fa.lipidFaBondType(faBondType);
            fa.nCarbon(asInt(ctx.fa_unmod().fa_pure().carbon(), 0));
            fa.nHydroxy(asInt(ctx.fa_unmod().fa_pure().hydroxyl(), 0));
            if (ctx.fa_unmod().fa_pure().db() != null) {
                if (ctx.fa_unmod().fa_pure().db().db_positions() != null) {
                    Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
                    LipidMapsParser.Db_positionContext dbPosCtx = ctx.fa_unmod().fa_pure().db().db_positions().db_position();
                    if (dbPosCtx.db_single_position() != null) {
                        Integer dbPosition = asInt(dbPosCtx.db_single_position().db_position_number(), -1);
                        String cisTrans = dbPosCtx.db_single_position().cistrans().getText();
                        doubleBondPositions.put(dbPosition, cisTrans);
                    } else if (dbPosCtx.db_position() != null) {
                        for (LipidMapsParser.Db_positionContext dbpos : dbPosCtx.db_position()) {
                            if (dbpos.db_single_position() != null) {
                                Integer dbPosition = asInt(dbpos.db_single_position().db_position_number(), -1);
                                String cisTrans = dbpos.db_single_position().cistrans().getText();
                                doubleBondPositions.put(dbPosition, cisTrans);
                            }
                        }
                    }
                    fa.doubleBondPositions(doubleBondPositions);
                } else {
                    fa.doubleBondPositions(Collections.emptyMap());
                }
            }
            return fa.name(faName).position(position).build();
        } else {
            throw new ParseTreeVisitorException("No FaContext!");
        }
    }
}

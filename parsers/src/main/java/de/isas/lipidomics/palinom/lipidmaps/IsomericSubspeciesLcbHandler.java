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

import de.isas.lipidomics.palinom.swisslipids.*;
import de.isas.lipidomics.domain.IsomericFattyAcid;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.StructuralFattyAcid;
import static de.isas.lipidomics.palinom.HandlerUtils.asInt;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author nilshoffmann
 */
public class IsomericSubspeciesLcbHandler {

    private final IsomericSubspeciesFasHandler isfh;
    private final FattyAcylHelper faHelper;

    public IsomericSubspeciesLcbHandler(IsomericSubspeciesFasHandler isfh, FattyAcylHelper faHelper) {
        this.isfh = isfh;
        this.faHelper = faHelper;
    }

    public StructuralFattyAcid buildIsomericLcb(LipidMapsParser.LcbContext ctx, String faName, int position) {
        IsomericFattyAcid.IsomericFattyAcidBuilder fa = IsomericFattyAcid.isomericFattyAcidBuilder();
        // FIXME handle these once they are defined
        String modifications = "";
        if (ctx.lcb_fa().lcb_fa_mod() != null) {
            if (ctx.lcb_fa().lcb_fa_mod().modification() != null) {
                modifications = ctx.lcb_fa().lcb_fa_mod().modification().getText();
            }
        }
        if (ctx.lcb_fa().lcb_fa_unmod() != null) {
            fa.nCarbon(asInt(ctx.lcb_fa().lcb_fa_unmod().carbon(), 0));
            Integer nHydroxy = faHelper.getHydroxyCount(ctx);
            fa.nHydroxy(nHydroxy);
            if (ctx.lcb_fa().lcb_fa_unmod().db() != null) {
                if (ctx.lcb_fa().lcb_fa_unmod().db().db_positions() != null) {
                    Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
                    LipidMapsParser.Db_positionContext dbPosCtx = ctx.lcb_fa().lcb_fa_unmod().db().db_positions().db_position();
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
            return fa.name(faName).position(position).lcb(true).lipidFaBondType(LipidFaBondType.ESTER).build();
        } else {
            throw new ParseTreeVisitorException("No LcbContext!");
        }
    }
}

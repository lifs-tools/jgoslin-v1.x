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

import de.isas.lipidomics.domain.IsomericFattyAcid;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.StructuralFattyAcid;
import de.isas.lipidomics.palinom.GoslinParser;
import static de.isas.lipidomics.palinom.HandlerUtils.asInt;
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
public class IsomericSubspeciesFasHandler {

    private final FattyAcylHelper faHelper;

    public IsomericSubspeciesFasHandler(FattyAcylHelper faHelper) {
        this.faHelper = faHelper;
    }

    public Optional<LipidSpecies> visitIsomericSubspeciesFas(String headGroup, List<GoslinParser.FaContext> faContexts) {
        List<StructuralFattyAcid> fas = new LinkedList<>();
        int nIsomericFas = 0;
        for (int i = 0; i < faContexts.size(); i++) {
            StructuralFattyAcid fa = buildIsomericFa(headGroup, faContexts.get(i), "FA" + (i + 1), i + 1);
            fas.add(fa);
            if (fa instanceof IsomericFattyAcid) {
                nIsomericFas++;
            }
        }
        if (nIsomericFas == fas.size()) {
            IsomericFattyAcid[] arrs = new IsomericFattyAcid[fas.size()];
            fas.stream().map((t) -> {
                return (IsomericFattyAcid) t;
            }).collect(Collectors.toList()).toArray(arrs);
            return Optional.of(new LipidIsomericSubspecies(headGroup, arrs));
        } else {
            StructuralFattyAcid[] arrs = new StructuralFattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));
        }
    }

    public IsomericFattyAcid buildIsomericFa(String headGroup, GoslinParser.FaContext ctx, String faName, int position) {
        IsomericFattyAcid.IsomericFattyAcidBuilder fa = IsomericFattyAcid.isomericFattyAcidBuilder();
        LipidFaBondType lfbt = faHelper.getLipidFaBondType(headGroup, ctx);
        if (ctx.fa_pure() != null) {
            fa.nCarbon(asInt(ctx.fa_pure().carbon(), 0));
            fa.nHydroxy(asInt(ctx.fa_pure().hydroxyl(), 0));
            if (ctx.fa_pure().db().db_positions() != null) {
                Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
                GoslinParser.Db_positionContext dbPosCtx = ctx.fa_pure().db().db_positions().db_position();
                if (dbPosCtx.db_single_position() != null) {
                    Integer dbPosition = asInt(dbPosCtx.db_single_position().db_position_number(), -1);
                    String cisTrans = dbPosCtx.db_single_position().cistrans().getText();
                    doubleBondPositions.put(dbPosition, cisTrans);
                } else if(dbPosCtx.db_position() != null) {
                    for (GoslinParser.Db_positionContext dbpos : dbPosCtx.db_position()) {
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
            fa.lipidFaBondType(lfbt);
            return fa.name(faName).position(position).build();
        } else {
            throw new ParseTreeVisitorException("Uninitialized FaContext!");
        }
    }
}

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
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.FattyAcidType;
import de.isas.lipidomics.palinom.HandlerUtils;
import static de.isas.lipidomics.palinom.HandlerUtils.asInt;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
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

    public Optional<LipidSpecies> visitIsomericSubspeciesFas(String headGroup, List<LipidMapsParser.FaContext> faContexts) {
        List<FattyAcid> fas = new LinkedList<>();
        int nIsomericFas = 0;
        for (int i = 0; i < faContexts.size(); i++) {
            FattyAcid fa = buildIsomericFa(faContexts.get(i), "FA" + (i + 1), i + 1);
            fas.add(fa);
            if (fa.getType() == FattyAcidType.ISOMERIC) {
                nIsomericFas++;
            }
        }
        if (nIsomericFas == fas.size()) {
            FattyAcid[] arrs = new FattyAcid[fas.size()];
            fas.stream().map((t) -> {
                return (FattyAcid) t;
            }).collect(Collectors.toList()).toArray(arrs);
            return Optional.of(new LipidIsomericSubspecies(headGroup, arrs));
        } else {
            FattyAcid[] arrs = new FattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));
        }
    }

    public FattyAcid buildIsomericFa(LipidMapsParser.FaContext ctx, String faName, int position) {
        FattyAcid.IsomericFattyAcidBuilder fa = FattyAcid.isomericFattyAcidBuilder();
        if (ctx.fa_mod() != null) {
            if (ctx.fa_mod().modification() != null) {
                fa.modifications(faHelper.resolveModifications(ctx.fa_mod().modification()));
            }
        }
        if (ctx.fa_unmod() != null) {
            LipidFaBondType faBondType = faHelper.getLipidFaBondType(ctx);
            fa.lipidFaBondType(faBondType);
            fa.nCarbon(asInt(ctx.fa_unmod().fa_pure().carbon(), 0));
            fa.nHydroxy(asInt(ctx.fa_unmod().fa_pure().hydroxyl(), 0));
            if (ctx.fa_unmod().fa_pure().db() != null) {
                if (ctx.fa_unmod().fa_pure().db().db_positions() != null) {
                    fa.doubleBondPositions(faHelper.resolveDoubleBondPositions(ctx.fa_unmod().fa_pure().db().db_positions()));
                } else {
                    Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
                    if (ctx.fa_unmod().fa_pure().db().db_count() != null) {
                        int doubleBonds = HandlerUtils.asInt(ctx.fa_unmod().fa_pure().db().db_count(), 0);
                        if (doubleBonds > 0) {
                            return FattyAcid.structuralFattyAcidBuilder().
                                    lipidFaBondType(faBondType).
                                    name(faName).
                                    lcb(true).
                                    nCarbon(HandlerUtils.asInt(ctx.fa_unmod().fa_pure().carbon(), 0)).
                                    nDoubleBonds(doubleBonds).
                                    build();
                        }
                    }
                    fa.doubleBondPositions(doubleBondPositions);
                }
            }
            return fa.name(faName).position(position).build();
        } else {
            throw new ParseTreeVisitorException("No FaContext!");
        }
    }
}
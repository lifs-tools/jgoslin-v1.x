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

import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.FattyAcidType;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.palinom.GoslinParser;
import static de.isas.lipidomics.palinom.HandlerUtils.asInt;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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

    public Optional<LipidSpecies> visitIsomericSubspeciesLcb(String headGroup, GoslinParser.LcbContext lcbContext, List<GoslinParser.FaContext> faContexts) {
        List<FattyAcid> fas = new LinkedList<>();
        FattyAcid lcbA = buildIsomericLcb(lcbContext, "LCB", 1);
        fas.add(lcbA);
        int nIsomericFas = 0;
        if (lcbA.getType() == FattyAcidType.ISOMERIC) {
            nIsomericFas++;
        }
        for (int i = 0; i < faContexts.size(); i++) {
            FattyAcid fa = isfh.buildIsomericFa(headGroup, faContexts.get(i), "FA" + (i + 1), i + 2);
            fas.add(fa);
            if (fa.getType() == FattyAcidType.ISOMERIC) {
                nIsomericFas++;
            }
        }
        if (nIsomericFas == fas.size()) {
            FattyAcid[] arrs = new FattyAcid[fas.size()];
            fas.stream().map((t) -> {
                return t;
            }).collect(Collectors.toList()).toArray(arrs);
            return Optional.of(new LipidIsomericSubspecies(headGroup, arrs));
        } else {
            FattyAcid[] arrs = new FattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));
        }
    }

    public FattyAcid buildIsomericLcb(GoslinParser.LcbContext ctx, String faName, int position) {
        if (ctx.lcb_pure() != null && ctx.heavy_lcb() != null) {
            throw new RuntimeException("Heavy label in lcb_pure context not implemented yet!");
        }
        GoslinParser.Lcb_pureContext pureCtx = ctx.lcb_pure();
        FattyAcid.IsomericFattyAcidBuilder fa = FattyAcid.isomericFattyAcidBuilder();
        fa.nCarbon(asInt(pureCtx.carbon(), 0));
        fa.nHydroxy(asInt(pureCtx.hydroxyl(), 0));
        if (pureCtx.db() != null) {
            if (ctx.lcb_pure().db().db_positions() != null) {
                fa.doubleBondPositions(faHelper.resolveDoubleBondPositions(ctx.lcb_pure().db().db_positions()));
            } else {
                fa.doubleBondPositions(Collections.emptyMap());
            }
        }
        fa.lipidFaBondType(LipidFaBondType.ESTER);
        return fa.name(faName).position(position).lcb(true).build();
    }

}
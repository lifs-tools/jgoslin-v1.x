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
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.StructuralFattyAcid;
import static de.isas.lipidomics.palinom.HandlerUtils.asInt;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author nilshoffmann
 */
public class StructuralSubspeciesLcbHandler {

    private final StructuralSubspeciesFasHandler ssfh;
    private final IsomericSubspeciesLcbHandler islh;
    private final FattyAcylHelper faHelper;

    public StructuralSubspeciesLcbHandler(StructuralSubspeciesFasHandler ssfh, IsomericSubspeciesLcbHandler islh, FattyAcylHelper faHelper) {
        this.ssfh = ssfh;
        this.islh = islh;
        this.faHelper = faHelper;
    }

    public Optional<LipidSpecies> visitStructuralSubspeciesLcb(String headGroup, LipidMapsParser.LcbContext lcbContext) {
        StructuralFattyAcid fa = buildStructuralLcb(lcbContext, "FA" + 1, 1);
        return Optional.of(new LipidStructuralSubspecies(headGroup, fa));
    }
    
    public Optional<LipidSpecies> visitStructuralSubspeciesLcb(String headGroup, LipidMapsParser.LcbContext lcbContext, List<LipidMapsParser.FaContext> faContexts) {
        List<StructuralFattyAcid> fas = new LinkedList<>();
        StructuralFattyAcid lcbA = buildStructuralLcb(lcbContext, "LCB", 1);
        fas.add(lcbA);
        int nIsomericFas = 0;
        if (lcbA instanceof IsomericFattyAcid) {
            nIsomericFas++;
        }
        for (int i = 0; i < faContexts.size(); i++) {
            StructuralFattyAcid fa = ssfh.buildStructuralFa(faContexts.get(i), "FA" + (i + 1), i + 2);
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

    public StructuralFattyAcid buildStructuralLcb(LipidMapsParser.LcbContext ctx, String faName, int position) {
        StructuralFattyAcid.StructuralFattyAcidBuilder fa = StructuralFattyAcid.structuralFattyAcidBuilder();
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
                int nDoubleBonds = asInt(ctx.lcb_fa().lcb_fa_unmod().db().db_count(), 0);
                fa.nDoubleBonds(nDoubleBonds);
                if (ctx.lcb_fa().lcb_fa_unmod().db().db_positions() != null || nDoubleBonds == 0) {
                    return islh.buildIsomericLcb(ctx, faName, position);
                }
            }
            return fa.name(faName).position(position).lcb(true).lipidFaBondType(LipidFaBondType.ESTER).build();
        } else {
            throw new ParseTreeVisitorException("No LcbContext!");
        }
    }

}

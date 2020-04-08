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
public class StructuralSubspeciesFasHandler {

    private final IsomericSubspeciesFasHandler isfh;
    private final FattyAcylHelper faHelper;
    
    public StructuralSubspeciesFasHandler(IsomericSubspeciesFasHandler isfh, FattyAcylHelper faHelper) {
        this.isfh = isfh;
        this.faHelper = faHelper;
    }
    
    public Optional<LipidSpecies> visitStructuralSubspeciesFas(String headGroup, List<LipidMapsParser.FaContext> faContexts) {
        List<StructuralFattyAcid> fas = new LinkedList<>();
        int nIsomericFas = 0;
        for (int i = 0; i < faContexts.size(); i++) {
            StructuralFattyAcid fa = buildStructuralFa(faContexts.get(i), "FA" + (i + 1), i + 1);
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

    public StructuralFattyAcid buildStructuralFa(LipidMapsParser.FaContext ctx, String faName, int position) {
        StructuralFattyAcid.StructuralFattyAcidBuilder fa = StructuralFattyAcid.structuralFattyAcidBuilder();
        String modifications = "";
        if (ctx.fa_mod() != null) {
            if (ctx.fa_mod().modification() != null) {
                modifications = ctx.fa_mod().modification().getText();
            }
        }
        if (ctx.fa_unmod() != null) {
            LipidFaBondType faBondType = faHelper.getLipidFaBondType(ctx);
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
                int nDoubleBonds = asInt(ctx.fa_unmod().fa_pure().db().db_count(), 0);
                fa.nDoubleBonds(plasmenylEtherDbBondCorrection + nDoubleBonds);
                if (ctx.fa_unmod().fa_pure().db().db_positions() != null || nDoubleBonds == 0) {
                    return isfh.buildIsomericFa(ctx, faName, position);
                }
            }
            return fa.name(faName).position(position).build();
        } else {
            throw new ParseTreeVisitorException("No FaContext!");
        }
    }
}

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

import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.FattyAcidType;
import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.HMDBParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handler for Structural FAs.
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

    public Optional<LipidSpecies> visitStructuralSubspeciesFas(HeadGroup headGroup, List<HMDBParser.FaContext> faContexts) {
        List<FattyAcid> fas = new LinkedList<>();
        int nIsomericFas = 0;
        for (int i = 0; i < faContexts.size(); i++) {
            FattyAcid fa = buildStructuralFa(headGroup, faContexts.get(i), "FA" + (i + 1), i + 1);
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

    public FattyAcid buildStructuralFa(HeadGroup headGroup, HMDBParser.FaContext ctx, String faName, int position) {
        FattyAcid.StructuralFattyAcidBuilder fa = FattyAcid.structuralFattyAcidBuilder();
        LipidFaBondType lfbt = faHelper.getLipidFaBondType(ctx);
        if (ctx.fa_core() != null) {
            fa.nCarbon(HandlerUtils.asInt(ctx.fa_core().carbon(), 0));
            if (ctx.fa_core().db() != null) {
                fa.nDoubleBonds(HandlerUtils.asInt(ctx.fa_core().db().db_count(), 0) + (lfbt == LipidFaBondType.ETHER_PLASMENYL ? 1 : 0));
                if (ctx.fa_core().db().db_positions() != null) {
                    return isfh.buildIsomericFa(headGroup, ctx, faName, position);
                }
            }
            fa.lipidFaBondType(lfbt);
            return fa.name(faName).position(position).build();
        } else if (ctx.fa_lcb_prefix() != null || ctx.fa_lcb_suffix() != null) { //handling of lcbs
            throw new RuntimeException("Support for lcbs is implemented in " + StructuralSubspeciesLcbHandler.class.getSimpleName() + "!");
        } else if (ctx.furan_fa() != null) {
            throw new ParseTreeVisitorException("Furan FAs are currently unsupported! Please contact the developers at https://lifs.isas.de/support for assistance.");
        } else {
            throw new ParseTreeVisitorException("Uninitialized FaContext!");
        }
    }
}

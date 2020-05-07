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

import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.GoslinParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Handler for Molecular FAs.
 *
 * @author nilshoffmann
 */
public class MolecularSubspeciesFasHandler {

    private final FattyAcylHelper faHelper;

    public MolecularSubspeciesFasHandler(FattyAcylHelper faHelper) {
        this.faHelper = faHelper;
    }

    public Optional<LipidSpecies> visitMolecularSubspeciesFas(HeadGroup headGroup, List<GoslinParser.FaContext> faContexts) {
        List<FattyAcid> fas = new LinkedList<>();
        for (int i = 0; i < faContexts.size(); i++) {
            FattyAcid fa = buildMolecularFa(headGroup, faContexts.get(i), "FA" + (i + 1));
            fas.add(fa);
        }
        FattyAcid[] arrs = new FattyAcid[fas.size()];
        fas.toArray(arrs);
        return Optional.of(new LipidMolecularSubspecies(headGroup, arrs));
    }

    public FattyAcid buildMolecularFa(HeadGroup headGroup, GoslinParser.FaContext ctx, String faName) {
        if (ctx.fa_pure() != null && ctx.heavy_fa() != null) {
            throw new RuntimeException("Heavy label in fa_pure context not implemented yet!");
        }
        FattyAcid.MolecularFattyAcidBuilder fa = FattyAcid.molecularFattyAcidBuilder();
        LipidFaBondType lfbt = faHelper.getLipidFaBondType(headGroup, ctx);
        if (ctx.fa_pure() != null) {
            fa.nCarbon(HandlerUtils.asInt(ctx.fa_pure().carbon(), 0));
            fa.nHydroxy(HandlerUtils.asInt(ctx.fa_pure().hydroxyl(), 0));
            if (ctx.fa_pure().db() != null) {
                fa.nDoubleBonds(HandlerUtils.asInt(ctx.fa_pure().db().db_count(), 0));
                if (ctx.fa_pure().db().db_positions() != null) {
                    throw new RuntimeException("Support for double bond positions is implemented in " + IsomericSubspeciesFasHandler.class.getSimpleName() + "!");
                }
            }
            fa.lipidFaBondType(lfbt);
            return fa.name(faName).build();

        } else {
            throw new ParseTreeVisitorException("Uninitialized FaContext!");
        }
    }
}

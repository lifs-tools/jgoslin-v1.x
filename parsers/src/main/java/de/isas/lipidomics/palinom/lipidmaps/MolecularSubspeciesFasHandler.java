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

import de.isas.lipidomics.domain.LipidCategory;
import de.isas.lipidomics.domain.LipidClass;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author nilshoffmann
 */
public class MolecularSubspeciesFasHandler {

    private final FattyAcylHelper faHelper;
    
    public MolecularSubspeciesFasHandler(FattyAcylHelper faHelper) {
        this.faHelper = faHelper;
    }
    
    public LipidSpecies handlePureFaContext(LipidMapsParser.Pure_faContext ctx) {
        
        if (ctx.fa_no_hg() != null && ctx.fa_no_hg().fa() != null) {
            FattyAcid fa = buildMolecularFa(ctx.fa_no_hg().fa(), "FA1");
            LipidSpeciesInfo lsi = new LipidSpeciesInfo(
                    LipidLevel.SPECIES,
                    fa.getNCarbon(),
                    fa.getNHydroxy(),
                    fa.getNDoubleBonds(),
                    LipidFaBondType.UNDEFINED
            );
            LipidSpecies ls = new LipidSpecies(
                    ctx.hg_fa().getText(),
                    LipidCategory.FA,
                    LipidClass.forHeadGroup(ctx.hg_fa().getText()),
                    Optional.of(lsi)
            );
            return ls;
        } else if (ctx.pure_fa_species() != null && ctx.hg_fa() != null) {
            LipidMapsParser.Pure_fa_speciesContext speciesContext = ctx.pure_fa_species();
            if (speciesContext != null) {
                FattyAcid fa = buildMolecularFa(speciesContext.fa(), "FA1");
                LipidSpeciesInfo lsi = new LipidSpeciesInfo(
                        LipidLevel.SPECIES,
                        fa.getNCarbon(),
                        fa.getNHydroxy(),
                        fa.getNDoubleBonds(),
                        LipidFaBondType.UNDEFINED
                );
                LipidSpecies ls = new LipidSpecies(
                        ctx.hg_fa().getText(),
                        LipidCategory.FA,
                        LipidClass.forHeadGroup(ctx.hg_fa().getText()),
                        Optional.of(lsi)
                );
                return ls;
            } else {
                throw new ParseTreeVisitorException("Unhandled pure FA species context: " + ctx.pure_fa_species());
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled pure FA: " + ctx.getText());
        }
    }
    
    public Optional<LipidSpecies> visitMolecularSubspeciesFas(String headGroup, List<LipidMapsParser.FaContext> faContexts) {
        List<FattyAcid> fas = new LinkedList<>();
        for (int i = 0; i < faContexts.size(); i++) {
            FattyAcid fa = buildMolecularFa(faContexts.get(i), "FA" + (i + 1));
            fas.add(fa);
        }
        FattyAcid[] arrs = new FattyAcid[fas.size()];
        fas.toArray(arrs);
        return Optional.of(new LipidMolecularSubspecies(headGroup, arrs));
    }
    
    public FattyAcid buildMolecularFa(LipidMapsParser.FaContext ctx, String faName) {
        FattyAcid.MolecularFattyAcidBuilder fa = FattyAcid.molecularFattyAcidBuilder();
        if (ctx.fa_unmod() != null) {
            LipidFaBondType faBondType = faHelper.getLipidFaBondType(ctx);
            int plasmenylEtherDbBondCorrection = 0;
            fa.lipidFaBondType(faBondType);
            fa.nCarbon(HandlerUtils.asInt(ctx.fa_unmod().fa_pure().carbon(), 0));
            fa.nHydroxy(HandlerUtils.asInt(ctx.fa_unmod().fa_pure().hydroxyl(), 0));
            if (ctx.fa_unmod().fa_pure().db() != null) {
                fa.nDoubleBonds(plasmenylEtherDbBondCorrection + HandlerUtils.asInt(ctx.fa_unmod().fa_pure().db().db_count(), 0));
                if (ctx.fa_unmod().fa_pure().db().db_positions() != null) {
                    throw new RuntimeException("Support for double bond positions not implemented yet!");
                }
            }
            return fa.name(faName).build();

        } else if (ctx.fa_mod() != null) {
            throw new RuntimeException("Support for modified FA handling not implemented!");
        } else {
            throw new ParseTreeVisitorException("No FaContext!");
        }
    }
}

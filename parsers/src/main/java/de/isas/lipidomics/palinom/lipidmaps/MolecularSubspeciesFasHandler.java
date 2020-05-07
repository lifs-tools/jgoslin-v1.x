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

import de.isas.lipidomics.domain.LipidClass;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.FattyAcidType;
import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.LipidMapsParser;
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
    private final StructuralSubspeciesFasHandler ssfh;

    public MolecularSubspeciesFasHandler(StructuralSubspeciesFasHandler ssfh, FattyAcylHelper faHelper) {
        this.ssfh = ssfh;
        this.faHelper = faHelper;
    }

    public LipidSpecies handlePureFaContext(LipidMapsParser.Pure_faContext ctx) {
        if (ctx.fa_no_hg() != null && ctx.fa_no_hg().fa() != null) {
            FattyAcid fa = buildMolecularFa(ctx.fa_no_hg().fa(), "FA1");
            switch (fa.getType()) {
                case ISOMERIC:
                    return new LipidIsomericSubspecies(
                            new HeadGroup(
                                    "FA"),
                            fa
                    );
                case STRUCTURAL:
                case MOLECULAR:
                    return new LipidStructuralSubspecies(
                            new HeadGroup(
                                    "FA"),
                            fa
                    );
            }
            LipidSpeciesInfo.LipidSpeciesInfoBuilder builder = LipidSpeciesInfo.lipidSpeciesInfoBuilder();
            LipidSpeciesInfo lsi = builder.level(LipidLevel.SPECIES).
                    nCarbon(fa.getNCarbon()).
                    nHydroxy(fa.getNHydroxy()).
                    nDoubleBonds(fa.getNDoubleBonds()).
                    lipidFaBondType(LipidFaBondType.UNDEFINED).
                    modifications(fa.getModifications()).
                    build();
            LipidSpecies ls = new LipidSpecies(
                    new HeadGroup(
                            "FA"
                    ),
                    Optional.of(lsi)
            );
            return ls;
        } else if (ctx.pure_fa_species() != null && ctx.hg_fa() != null) {
            LipidMapsParser.Pure_fa_speciesContext speciesContext = ctx.pure_fa_species();
            if (speciesContext != null) {
                FattyAcid fa = buildMolecularFa(speciesContext.fa(), "FA1");
                switch (fa.getType()) {
                    case ISOMERIC:
                        return new LipidIsomericSubspecies(
                                new HeadGroup(
                                        ctx.hg_fa().getText()),
                                fa
                        );
                    case STRUCTURAL:
                    case MOLECULAR:
                        return new LipidStructuralSubspecies(
                                new HeadGroup(
                                        ctx.hg_fa().getText()),
                                fa
                        );
                }

                LipidSpeciesInfo.LipidSpeciesInfoBuilder builder = LipidSpeciesInfo.lipidSpeciesInfoBuilder();
                LipidSpeciesInfo lsi = builder.level(LipidLevel.SPECIES).
                        nCarbon(fa.getNCarbon()).
                        nHydroxy(fa.getNHydroxy()).
                        nDoubleBonds(fa.getNDoubleBonds()).
                        lipidFaBondType(LipidFaBondType.UNDEFINED).
                        modifications(fa.getModifications()).
                        build();
                LipidSpecies ls = new LipidSpecies(
                        new HeadGroup(
                                ctx.hg_fa().getText()
                        ),
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

    public Optional<LipidSpecies> visitMolecularSubspeciesFas(HeadGroup headGroup, List<LipidMapsParser.FaContext> faContexts) {
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
        if (ctx.fa_mod() != null) {
            if (ctx.fa_mod().modification() != null) {
                fa.modifications(faHelper.resolveModifications(ctx.fa_mod().modification()));
            }
        }
        if (ctx.fa_unmod() != null) {
            LipidFaBondType faBondType = faHelper.getLipidFaBondType(ctx);
            int plasmenylEtherDbBondCorrection = 0;
            fa.lipidFaBondType(faBondType);
            fa.nCarbon(HandlerUtils.asInt(ctx.fa_unmod().fa_pure().carbon(), 0));
            fa.nHydroxy(HandlerUtils.asInt(ctx.fa_unmod().fa_pure().hydroxyl(), 0));
            if (ctx.fa_unmod().fa_pure().db() != null) {
                fa.nDoubleBonds(plasmenylEtherDbBondCorrection + HandlerUtils.asInt(ctx.fa_unmod().fa_pure().db().db_count(), 0) + (faBondType == LipidFaBondType.ETHER_PLASMENYL ? 1 : 0));
                if (ctx.fa_unmod().fa_pure().db().db_positions() != null) {
                    return ssfh.buildStructuralFa(ctx, faName, -1);
                }
            }
            return fa.name(faName).build();

        } else {
            throw new ParseTreeVisitorException("No FaContext!");
        }
    }
}

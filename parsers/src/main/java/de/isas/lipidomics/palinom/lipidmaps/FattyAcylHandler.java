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

import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.ModificationsList;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.SwissLipidsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * ParserRuleContextHandler for FattyAcyls.
 *
 * @author nilshoffmann
 */
public class FattyAcylHandler implements ParserRuleContextHandler<LipidMapsParser.Lipid_pureContext, LipidSpecies> {

    private final MolecularSubspeciesFasHandler msfah;
    private final StructuralSubspeciesFasHandler ssfah;
    private final StructuralSubspeciesLcbHandler sslah;
    private final FattyAcylHelper faHelper;

    public FattyAcylHandler(MolecularSubspeciesFasHandler msfah, StructuralSubspeciesFasHandler ssfah, StructuralSubspeciesLcbHandler sslah, FattyAcylHelper faHelper) {
        this.msfah = msfah;
        this.ssfah = ssfah;
        this.sslah = sslah;
        this.faHelper = faHelper;
    }

    @Override
    public LipidSpecies handle(LipidMapsParser.Lipid_pureContext ctx) {
        if (ctx.mediator() != null) {
            return LipidIsomericSubspecies.lipidIsomericSubspeciesBuilder().headGroup(ctx.mediator().getText()).fa(new FattyAcid[0]).build();
        } else if (ctx.pure_fa() != null) {
            return msfah.handlePureFaContext(ctx.pure_fa());
        }
        throw new ParseTreeVisitorException("Unhandled FA context: " + ctx.getText());
    }

    public Optional<LipidSpecies> visitSpeciesLcb(String headGroup, LipidMapsParser.LcbContext lcbContext) {
        Optional<LipidSpeciesInfo> lsi = getSpeciesInfo(lcbContext);
        if (lsi.isPresent()) {
            switch (lsi.get().getLevel()) {
                case SPECIES:
                    return Optional.of(new LipidSpecies(headGroup, lsi));
                case STRUCTURAL_SUBSPECIES:
                case ISOMERIC_SUBSPECIES:
                    return sslah.visitStructuralSubspeciesLcb(headGroup, lcbContext);
                default:
                    throw new ParseTreeVisitorException("Unexpected FA level " + lsi.get().getLevel());
            }
        }
        return Optional.of(new LipidSpecies(headGroup, lsi));
    }

    public Optional<LipidSpecies> visitSpeciesFas(String headGroup, LipidMapsParser.FaContext faContext) {
        Optional<LipidSpeciesInfo> lsi = getSpeciesInfo(headGroup, faContext);
        if (lsi.isPresent()) {
            switch (lsi.get().getLevel()) {
                case SPECIES:
                    return Optional.of(new LipidSpecies(headGroup, lsi));
                case STRUCTURAL_SUBSPECIES:
                case ISOMERIC_SUBSPECIES:
                    return ssfah.visitStructuralSubspeciesFas(headGroup, Arrays.asList(faContext));
                default:
                    throw new ParseTreeVisitorException("Unexpected FA level " + lsi.get().getLevel());
            }
        }
        return Optional.of(new LipidSpecies(headGroup, lsi));
    }

    public Optional<LipidSpecies> visitSubspeciesFas2(String headGroup, List<LipidMapsParser.Fa2Context> fa2Contexts) {
        List<FattyAcid> fas = new LinkedList<>();
        LipidLevel level = LipidLevel.UNDEFINED;
        for (int i = 0; i < fa2Contexts.size(); i++) {
            LipidMapsParser.Fa2Context fa2Ctx = fa2Contexts.get(i);
            if (fa2Ctx.fa2_sorted() != null) {
                level = LipidLevel.STRUCTURAL_SUBSPECIES;
                for (int j = 0; j < fa2Ctx.fa2_sorted().fa().size(); j++) {
                    FattyAcid fa = ssfah.buildStructuralFa(fa2Ctx.fa2_sorted().fa().get(j), "FA" + ((i + 1) + j), i + 1);
                    fas.add(fa);
                }
            } else if (fa2Ctx.fa2_unsorted() != null) {
                level = LipidLevel.MOLECULAR_SUBSPECIES;
                for (int j = 0; j < fa2Ctx.fa2_unsorted().fa().size(); j++) {
                    FattyAcid fa = msfah.buildMolecularFa(fa2Ctx.fa2_unsorted().fa().get(i), "FA" + ((i + 1) + j));
                    fas.add(fa);
                }
            }
        }
        switch (level) {
            case MOLECULAR_SUBSPECIES:
                FattyAcid[] marrs = new FattyAcid[fas.size()];
                fas.toArray(marrs);
                return Optional.of(new LipidMolecularSubspecies(headGroup, marrs));
            case STRUCTURAL_SUBSPECIES:
                FattyAcid[] sarrs = new FattyAcid[fas.size()];
                fas.toArray(sarrs);
                return Optional.of(new LipidStructuralSubspecies(headGroup, sarrs));
            case ISOMERIC_SUBSPECIES:
                FattyAcid[] ifa = new FattyAcid[fas.size()];
                fas.toArray(ifa);
                return Optional.of(new LipidIsomericSubspecies(headGroup, ifa));
            default:
                throw new ParseTreeVisitorException("Unhandled lipid level for CL: " + level);
        }
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(String headGroup, LipidMapsParser.FaContext faContext) {
        LipidSpeciesInfo.LipidSpeciesInfoBuilder lsi = LipidSpeciesInfo.lipidSpeciesInfoBuilder();
        if (faContext.fa_mod() != null) {
            lsi.modifications(faHelper.resolveModification(faContext.fa_mod().modification(), new ModificationsList()));
        }
        if (faContext.fa_unmod().fa_pure().db().db_positions() != null) {
            return Optional.of(LipidSpeciesInfo.lipidSubspeciesInfoBuilder().
                    level(LipidLevel.ISOMERIC_SUBSPECIES).
                    name("FA").
                    position(-1).
                    nCarbon(HandlerUtils.asInt(faContext.fa_unmod().fa_pure().carbon(), 0)).
                    nHydroxy(HandlerUtils.asInt(faContext.fa_unmod().fa_pure().hydroxyl(), 0)).
                    doubleBondPositions(faHelper.resolveDoubleBondPositions(faContext.fa_unmod().fa_pure().db().db_positions())).
                    lipidFaBondType(faHelper.getLipidFaBondType(faContext)).
                    build()
            );
        } else {
            if (faContext.fa_unmod() != null) {
                return Optional.of(
                        lsi.
                                position(-1).
                                name(LipidLevel.SPECIES.name()).
                                level(LipidLevel.SPECIES).
                                lipidFaBondType(faHelper.getLipidFaBondType(faContext)).
                                nCarbon(HandlerUtils.asInt(faContext.fa_unmod().fa_pure().carbon(), 0)).
                                nDoubleBonds(HandlerUtils.asInt(faContext.fa_unmod().fa_pure().db(), 0)).
                                nHydroxy(HandlerUtils.asInt(faContext.fa_unmod().fa_pure().hydroxyl(), 0)).build()
                );
            }
        }
        throw new ParseTreeVisitorException("Unknown fa context value: " + faContext.getText());
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(LipidMapsParser.LcbContext lcbContext) {
        LipidSpeciesInfo.LipidSpeciesInfoBuilder lsi = LipidSpeciesInfo.lipidSpeciesInfoBuilder();
        Integer hydroxyl = 0;
        if (lcbContext.hydroxyl_lcb() != null) {
            hydroxyl = faHelper.getHydroxyCount(lcbContext);
            lsi.nHydroxy(hydroxyl);
        }
        if (lcbContext.lcb_fa().lcb_fa_mod() != null) {
            lsi.modifications(faHelper.resolveModification(lcbContext.lcb_fa().lcb_fa_mod().modification(), new ModificationsList()));
        }
        if (lcbContext.lcb_fa().lcb_fa_unmod() != null) {
            return Optional.of(lsi.
                    position(-1).
                    name(LipidLevel.SPECIES.name()).
                    level(LipidLevel.SPECIES).
                    lipidFaBondType(LipidFaBondType.ESTER).
                    nCarbon(HandlerUtils.asInt(lcbContext.lcb_fa().lcb_fa_unmod().carbon(), 0)).
                    nDoubleBonds(HandlerUtils.asInt(lcbContext.lcb_fa().lcb_fa_unmod().db(), 0)).
                    build()
            );
        } else {
            throw new ParseTreeVisitorException("Unknown lcb fa context value: " + lcbContext.getText());
        }
    }

}

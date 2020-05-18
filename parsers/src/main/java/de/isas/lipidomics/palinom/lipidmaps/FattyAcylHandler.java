/*
 * Copyright 2020  nils.hoffmann.
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
import de.isas.lipidomics.domain.FattyAcidType;
import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.domain.ModificationsList;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ParserRuleContextHandler for FattyAcyls.
 *
 * @author  nils.hoffmann
 */
class FattyAcylHandler implements ParserRuleContextHandler<LipidMapsParser.Lipid_pureContext, LipidSpecies> {

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
            return LipidIsomericSubspecies.lipidIsomericSubspeciesBuilder().headGroup(new HeadGroup(ctx.mediator().getText())).fa(new FattyAcid[0]).build();
        } else if (ctx.pure_fa() != null) {
            return msfah.handlePureFaContext(ctx.pure_fa());
        }
        throw new ParseTreeVisitorException("Unhandled FA context: " + ctx.getText());
    }

    public Optional<LipidSpecies> visitSpeciesLcb(HeadGroup headGroup, LipidMapsParser.LcbContext lcbContext) {
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

    public Optional<LipidSpecies> visitSpeciesFas(HeadGroup headGroup, LipidMapsParser.FaContext faContext) {
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

    public Optional<LipidSpecies> visitSubspeciesFas2(HeadGroup headGroup, List<LipidMapsParser.Fa2Context> fa2Contexts) {
        List<FattyAcid> fas = new LinkedList<>();
        int nIsomericFas = 0;
        int nMolecularFas = 0;
        for (int i = 0; i < fa2Contexts.size(); i++) {
            LipidMapsParser.Fa2Context fa2Ctx = fa2Contexts.get(i);
            if (fa2Ctx.fa2_sorted() != null) {
                for (int j = 0; j < fa2Ctx.fa2_sorted().fa().size(); j++) {
                    FattyAcid fa = ssfah.buildStructuralFa(fa2Ctx.fa2_sorted().fa().get(j), "FA" + ((i * fa2Contexts.size() + 1) + j), (i * fa2Contexts.size()) + j);
                    if (fa.getType() == FattyAcidType.ISOMERIC) {
                        nIsomericFas++;
                    }
                    if (fa.getType() == FattyAcidType.MOLECULAR) {
                        nMolecularFas++;
                    }
                    fas.add(fa);
                }
            } else if (fa2Ctx.fa2_unsorted() != null) {
                for (int j = 0; j < fa2Ctx.fa2_unsorted().fa().size(); j++) {
                    FattyAcid fa = msfah.buildMolecularFa(fa2Ctx.fa2_unsorted().fa().get(i), "FA" + ((i * fa2Contexts.size() + 1) + j));
                    if (fa.getType() == FattyAcidType.ISOMERIC) {
                        nIsomericFas++;
                    }
                    if (fa.getType() == FattyAcidType.MOLECULAR) {
                        nMolecularFas++;
                    }
                    fas.add(fa);
                }
            }
        }
        if (nIsomericFas == fas.size()) {
            FattyAcid[] arrs = new FattyAcid[fas.size()];
            fas.stream().map((t) -> {
                return (FattyAcid) t;
            }).collect(Collectors.toList()).toArray(arrs);
            return Optional.of(new LipidIsomericSubspecies(headGroup, arrs));
        } else if (nMolecularFas < fas.size()) {
            FattyAcid[] arrs = new FattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));
        } else {
            FattyAcid[] arrs = new FattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidMolecularSubspecies(headGroup, arrs));
        }
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(HeadGroup headGroup, LipidMapsParser.FaContext faContext) {
        LipidSpeciesInfo.LipidSpeciesInfoBuilder lsi = LipidSpeciesInfo.lipidSpeciesInfoBuilder();
        int modificationHydroxyls = 0;
        ModificationsList modifications = new ModificationsList();
        if (faContext.fa_mod() != null) {
            modifications = faHelper.resolveModifications(faContext.fa_mod().modification());
            modificationHydroxyls += modifications.countFor("OH");
            lsi.modifications(modifications);
        }
        LipidFaBondType lfbt = faHelper.getLipidFaBondType(faContext);
        if (faContext.fa_unmod().fa_pure().db().db_positions() != null) {
            return Optional.of(LipidSpeciesInfo.lipidSubspeciesInfoBuilder().
                    level(LipidLevel.ISOMERIC_SUBSPECIES).
                    name("FA").
                    position(-1).
                    nCarbon(HandlerUtils.asInt(faContext.fa_unmod().fa_pure().carbon(), 0)).
                    nHydroxy(HandlerUtils.asInt(faContext.fa_unmod().fa_pure().hydroxyl(), 0) + modificationHydroxyls).
                    nDoubleBonds(HandlerUtils.asInt(faContext.fa_unmod().fa_pure().db(), 0) + (faHelper.getLipidFaBondType(faContext) == LipidFaBondType.ETHER_PLASMENYL ? 1 : 0)).
                    doubleBondPositions(faHelper.resolveDoubleBondPositions(lfbt, faContext.fa_unmod().fa_pure().db().db_positions())).
                    lipidFaBondType(lfbt).
                    modifications(modifications).
                    build()
            );
        } else {
            if (faContext.fa_unmod() != null) {
                return Optional.of(lsi.
                        position(-1).
                        name(LipidLevel.SPECIES.name()).
                        level(LipidLevel.SPECIES).
                        lipidFaBondType(faHelper.getLipidFaBondType(faContext)).
                        nCarbon(HandlerUtils.asInt(faContext.fa_unmod().fa_pure().carbon(), 0)).
                        nDoubleBonds(HandlerUtils.asInt(faContext.fa_unmod().fa_pure().db(), 0) + (faHelper.getLipidFaBondType(faContext) == LipidFaBondType.ETHER_PLASMENYL ? 1 : 0)).
                        nHydroxy(HandlerUtils.asInt(faContext.fa_unmod().fa_pure().hydroxyl(), 0) + modificationHydroxyls).build()
                );
            }
        }
        throw new ParseTreeVisitorException("Unknown fa context value: " + faContext.getText());
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(LipidMapsParser.LcbContext lcbContext) {
        LipidSpeciesInfo.LipidSpeciesInfoBuilder lsi = LipidSpeciesInfo.lipidSpeciesInfoBuilder();
        ModificationsList modifications = new ModificationsList();
        if (lcbContext.lcb_fa().lcb_fa_mod() != null) {
            modifications = faHelper.resolveModifications(lcbContext.lcb_fa().lcb_fa_mod().modification());
            lsi.modifications(modifications);
        }
        Integer hydroxyl = modifications.countFor("OH");
        if (lcbContext.hydroxyl_lcb() != null) {
            hydroxyl += faHelper.getHydroxyCount(lcbContext);
            lsi.nHydroxy(hydroxyl);
        }
        if (lcbContext.lcb_fa().lcb_fa_unmod() != null) {
            return Optional.of(lsi.
                    position(-1).
                    name(LipidLevel.SPECIES.name()).
                    level(LipidLevel.SPECIES).
                    lcb(true).
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

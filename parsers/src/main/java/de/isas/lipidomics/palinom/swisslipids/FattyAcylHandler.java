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
package de.isas.lipidomics.palinom.swisslipids;

import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.SwissLipidsParser;
import de.isas.lipidomics.palinom.SwissLipidsParser.FaContext;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ParserRuleContextHandler for FattyAcyls.
 *
 * @author nilshoffmann
 */
public class FattyAcylHandler implements ParserRuleContextHandler<SwissLipidsParser.Lipid_pureContext, LipidSpecies> {

    private final FattyAcylHelper faHelper = new FattyAcylHelper();
    private final StructuralSubspeciesFasHandler ssfh;
    private final StructuralSubspeciesLcbHandler sslh;

    public FattyAcylHandler(StructuralSubspeciesFasHandler ssfh, StructuralSubspeciesLcbHandler sslh) {
        this.ssfh = ssfh;
        this.sslh = sslh;
    }

    @Override
    public LipidSpecies handle(SwissLipidsParser.Lipid_pureContext ctx) {
        if (ctx.fatty_acid() != null) {
            if (ctx.fatty_acid().fa_hg() != null) {
                HeadGroup headGroup = new HeadGroup(ctx.fatty_acid().fa_hg().getText());
                FaContext faContext = null;
                if (ctx.fatty_acid().fa_fa() != null && ctx.fatty_acid().fa_fa().fa() != null) {
                    faContext = ctx.fatty_acid().fa_fa().fa();
                    return visitSpeciesFas(headGroup, faContext).orElse(LipidSpecies.NONE);
                } else {
                    throw new ParseTreeVisitorException("Context for FA fa was null!");
                }
            } else if (ctx.fatty_acid().mediator() != null) {
                // mediator fron positions, e.g 11,12-DiHETrE
                String mediatorPositions = "";
                if (ctx.fatty_acid().mediator().med_positions() != null) {
                    mediatorPositions = ctx.fatty_acid().mediator().med_positions().getText();
                }
                // E + Z positions
//                    if (ctx.fatty_acid().mediator().mediator_single() != null) {
//                        ctx.fatty_acid().mediator().mediator_single().db_positions();
//                    }
                HeadGroup mediatorsSingleContext = new HeadGroup(ctx.fatty_acid().mediator().mediator_single().getText());
                return LipidIsomericSubspecies.lipidIsomericSubspeciesBuilder().headGroup(mediatorsSingleContext).fa(new FattyAcid[0]).build();
            } else {
                throw new ParseTreeVisitorException("Context for FA head group was null!");
            }

        } else {
            throw new ParseTreeVisitorException("Context for FA was null!");
        }
    }

    public Optional<LipidSpecies> visitSpeciesLcb(HeadGroup headGroup, SwissLipidsParser.LcbContext lcbContext) {
        Optional<LipidSpeciesInfo> lsi = getSpeciesInfo(headGroup, lcbContext);
        if (lsi.isPresent()) {
            switch (lsi.get().getLevel()) {
                case SPECIES:
                    return Optional.of(new LipidSpecies(headGroup, lsi));
                case STRUCTURAL_SUBSPECIES:
                case ISOMERIC_SUBSPECIES:
                    return sslh.visitStructuralSubspeciesLcb(headGroup, lcbContext);
                default:
                    throw new ParseTreeVisitorException("Unexpected FA level " + lsi.get().getLevel());
            }
        }
        return Optional.of(new LipidSpecies(headGroup, lsi));
    }

    public Optional<LipidSpecies> visitSpeciesFas(HeadGroup headGroup, SwissLipidsParser.FaContext faContext) {
        Optional<LipidSpeciesInfo> lsi = getSpeciesInfo(headGroup, faContext);
        if (lsi.isPresent()) {
            switch (lsi.get().getLevel()) {
                case SPECIES:
                    return Optional.of(new LipidSpecies(headGroup, lsi));
                case STRUCTURAL_SUBSPECIES:
                case ISOMERIC_SUBSPECIES:
                    return ssfh.visitStructuralSubspeciesFas(headGroup, Arrays.asList(faContext));
                default:
                    throw new ParseTreeVisitorException("Unexpected FA level " + lsi.get().getLevel());
            }
        }
        return Optional.of(new LipidSpecies(headGroup, lsi));
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(HeadGroup headGroup, SwissLipidsParser.FaContext faContext) {
        LipidFaBondType lfbt = faHelper.getLipidFaBondType(faContext);
        int nHydroxyl = 0;
        if (faContext.fa_lcb_prefix() != null) {
            throw new ParseTreeVisitorException("Unsupported lcb prefix on fa: " + faContext.fa_lcb_prefix().getText());
        }
        if (faContext.fa_lcb_suffix() != null) {
            throw new ParseTreeVisitorException("Unsupported lcb suffix on fa: " + faContext.fa_lcb_suffix().getText());
        }
        if (faContext.fa_core().db().db_positions() != null) {
            return Optional.of(LipidSpeciesInfo.lipidSubspeciesInfoBuilder().
                    level(LipidLevel.ISOMERIC_SUBSPECIES).
                    name("FA").
                    position(-1).
                    nCarbon(HandlerUtils.asInt(faContext.fa_core().carbon(), 0)).
                    nHydroxy(nHydroxyl).
                    doubleBondPositions(faHelper.resolveDoubleBondPositions(lfbt, faContext.fa_core().db().db_positions())).
                    lipidFaBondType(lfbt).
                    build()
            );
        } else {
            return Optional.of(LipidSpeciesInfo.lipidSpeciesInfoBuilder().
                    level(LipidLevel.SPECIES).
                    name("FA").
                    position(-1).
                    nCarbon(HandlerUtils.asInt(faContext.fa_core().carbon(), 0)).
                    nHydroxy(nHydroxyl).
                    nDoubleBonds(HandlerUtils.asInt(faContext.fa_core().db(), 0) + (lfbt == LipidFaBondType.ETHER_PLASMENYL ? 1 : 0)).
                    lipidFaBondType(lfbt).
                    build()
            );
        }
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(HeadGroup headGroup, SwissLipidsParser.LcbContext lcbContext) {
        Integer nHydroxyl = 0;
        if (lcbContext.lcb_core() != null) {
            nHydroxyl = faHelper.getNHydroxyl(lcbContext);
            return Optional.of(LipidSpeciesInfo.lipidSpeciesInfoBuilder().
                    level(LipidLevel.SPECIES).
                    name("LCB").
                    lcb(true).
                    position(-1).
                    nCarbon(HandlerUtils.asInt(lcbContext.lcb_core().carbon(), 0)).
                    nHydroxy(nHydroxyl).
                    nDoubleBonds(HandlerUtils.asInt(lcbContext.lcb_core().db(), 0)).
                    lipidFaBondType(faHelper.getLipidLcbBondType(headGroup, lcbContext)).
                    build()
            );
        }
        throw new ParseTreeVisitorException("Uninitialized lcb_core context!");
    }

    public boolean isIsomericFa(List<SwissLipidsParser.FaContext> faContexts) {
        for (SwissLipidsParser.FaContext faContext : faContexts) {
            if (faContext.fa_core() != null) {
                SwissLipidsParser.Fa_coreContext coreCtx = faContext.fa_core();
                if (coreCtx.db() != null) {
                    int dbCount = coreCtx.db().db_count() != null ? HandlerUtils.asInt(coreCtx.db().db_count(), 0) : -1;
                    return dbCount == 0 || coreCtx.db().db_positions() != null;
                }
            }
        }
        return false;
    }

    public boolean isIsomericFa(SwissLipidsParser.FaContext faContext) {
        if (faContext.fa_core() != null) {
            SwissLipidsParser.Fa_coreContext coreCtx = faContext.fa_core();
            if (coreCtx.db() != null) {
                int dbCount = coreCtx.db().db_count() != null ? HandlerUtils.asInt(coreCtx.db().db_count(), 0) : -1;
                return dbCount == 0 || coreCtx.db().db_positions() != null;
            }
        }
        return false;
    }

    public boolean isIsomericLcb(SwissLipidsParser.LcbContext lcbContext) {
        if (lcbContext.lcb_core() != null) {
            SwissLipidsParser.Lcb_coreContext coreCtx = lcbContext.lcb_core();
            if (coreCtx.db() != null) {
                int dbCount = coreCtx.db().db_count() != null ? HandlerUtils.asInt(coreCtx.db().db_count(), 0) : -1;
                return dbCount == 0 || coreCtx.db().db_positions() != null;
            }
        }
        return false;
    }
}

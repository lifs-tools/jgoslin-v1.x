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
package de.isas.lipidomics.palinom.goslinfragments;

import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.palinom.GoslinFragmentsParser;
import de.isas.lipidomics.palinom.HandlerUtils;
import static de.isas.lipidomics.palinom.HandlerUtils.asInt;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.List;
import java.util.Optional;

/**
 * ParserRuleContextHandler for FattyAcyls.
 *
 * @author  nils.hoffmann
 */
class FattyAcylHandler implements ParserRuleContextHandler<GoslinFragmentsParser.Lipid_pureContext, LipidSpecies> {

    private final FattyAcylHelper helper = new FattyAcylHelper();

    @Override
    public LipidSpecies handle(GoslinFragmentsParser.Lipid_pureContext ctx) {
        if (ctx.mediatorc() != null) {
            if (ctx.mediatorc().mediator() != null) {
                // mediator fron positions, e.g 11,12-DiHETrE
//                String mediatorPositions = "";
                // E + Z positions
//                    if (ctx.fatty_acid().mediator().mediator_single() != null) {
//                        ctx.fatty_acid().mediator().mediator_single().db_positions();
//                    }
                HeadGroup mediatorsSingleContext = new HeadGroup(ctx.mediatorc().mediator().getText());
                return LipidIsomericSubspecies.lipidIsomericSubspeciesBuilder().headGroup(mediatorsSingleContext).fa(new FattyAcid[0]).build();
            } else {
                throw new ParseTreeVisitorException("Context for FA head group was null!");
            }

        } else {
            throw new ParseTreeVisitorException("Context for FA was null!");
        }
    }

    public Optional<LipidSpecies> visitSpeciesLcb(HeadGroup headGroup, GoslinFragmentsParser.LcbContext lcbContext) {
        return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(lcbContext)));
    }

    public Optional<LipidSpecies> visitSpeciesFas(HeadGroup headGroup, GoslinFragmentsParser.FaContext faContext) {
        return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(headGroup, faContext)));
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(HeadGroup headGroup, GoslinFragmentsParser.FaContext faContext) {
        //fa_pure, ether, heavy
        if (faContext.fa_pure() != null && faContext.heavy_fa() != null) {
            throw new RuntimeException("Heavy label in FA_pure context not implemented yet!");
        }

        LipidSpeciesInfo lsi = new LipidSpeciesInfo(
                LipidLevel.SPECIES,
                asInt(faContext.fa_pure().carbon(), 0),
                asInt(faContext.fa_pure().hydroxyl(), 0),
                asInt(faContext.fa_pure().db(), 0),
                helper.getLipidFaBondType(headGroup, faContext));
        LipidFaBondType consensusBondType = LipidFaBondType.getLipidFaBondType(headGroup, lsi);
        return Optional.of(new LipidSpeciesInfo(
                LipidLevel.SPECIES,
                asInt(faContext.fa_pure().carbon(), 0),
                asInt(faContext.fa_pure().hydroxyl(), 0),
                asInt(faContext.fa_pure().db(), 0),
                consensusBondType)
        );
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(GoslinFragmentsParser.LcbContext lcbContext) {
        Integer hydroxyl = 0;
        if (lcbContext.lcb_pure() != null && lcbContext.heavy_lcb() != null) {
            throw new RuntimeException("Heavy label in lcb_pure context not implemented yet!");
        }
        if (lcbContext.lcb_pure() != null) {
            GoslinFragmentsParser.Lcb_pureContext pureCtx = lcbContext.lcb_pure();
            if (pureCtx.old_hydroxyl() != null) {
                switch (pureCtx.old_hydroxyl().getText()) {
                    case "t":
                        hydroxyl = 3;
                        break;
                    case "d":
                        hydroxyl = 2;
                        break;
                    default:
                        throw new ParseTreeVisitorException("Unsupported old hydroxyl prefix: " + pureCtx.old_hydroxyl().getText());
                }
            } else if (pureCtx.hydroxyl() != null) {
                hydroxyl = asInt(pureCtx.hydroxyl(), 0);
            }
            return Optional.of(LipidSpeciesInfo.lipidSpeciesInfoBuilder().
                    level(LipidLevel.SPECIES).
                    name("LCB").
                    lcb(true).
                    nCarbon(asInt(pureCtx.carbon(), 0)).
                    nHydroxy(hydroxyl).
                    nDoubleBonds(asInt(pureCtx.db(), 0)).
                    lipidFaBondType(LipidFaBondType.ESTER).
                    build()
            );
        }
        throw new ParseTreeVisitorException("Uninitialized lcb_pure context!");
    }

    public boolean isIsomericFa(List<GoslinFragmentsParser.FaContext> faContexts) {
        for (GoslinFragmentsParser.FaContext faContext : faContexts) {
            if (faContext.fa_pure() != null) {
                GoslinFragmentsParser.Fa_pureContext coreCtx = faContext.fa_pure();
                if (coreCtx.db() != null) {
                    int dbCount = coreCtx.db().db_count() != null ? HandlerUtils.asInt(coreCtx.db().db_count(), 0) : -1;
                    return dbCount == 0 || coreCtx.db().db_positions() != null;
                }
            }
        }
        return false;
    }

    public boolean isIsomericFa(GoslinFragmentsParser.FaContext faContext) {
        if (faContext.fa_pure() != null) {
            GoslinFragmentsParser.Fa_pureContext coreCtx = faContext.fa_pure();
            if (coreCtx.db() != null) {
                int dbCount = coreCtx.db().db_count() != null ? HandlerUtils.asInt(coreCtx.db().db_count(), 0) : -1;
                return dbCount == 0 || coreCtx.db().db_positions() != null;
            }
        }
        return false;
    }

    public boolean isIsomericLcb(GoslinFragmentsParser.LcbContext lcbContext) {
        if (lcbContext.lcb_pure() != null) {
            GoslinFragmentsParser.Lcb_pureContext coreCtx = lcbContext.lcb_pure();
            if (coreCtx.db() != null) {
                int dbCount = coreCtx.db().db_count() != null ? HandlerUtils.asInt(coreCtx.db().db_count(), 0) : -1;
                return dbCount == 0 || coreCtx.db().db_positions() != null;
            }
        }
        return false;
    }
}

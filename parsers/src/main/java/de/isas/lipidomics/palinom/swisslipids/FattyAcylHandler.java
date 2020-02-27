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

import de.isas.lipidomics.domain.IsomericFattyAcid;
import de.isas.lipidomics.domain.LipidCategory;
import de.isas.lipidomics.domain.LipidClass;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.palinom.SwissLipidsParser;
import de.isas.lipidomics.palinom.SwissLipidsParser.FaContext;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Optional;
import java.util.function.Function;
import org.antlr.v4.runtime.tree.RuleNode;

/**
 *
 * @author nilshoffmann
 */
public class FattyAcylHandler implements ParserRuleContextHandler<SwissLipidsParser.Lipid_pureContext, LipidSpecies> {

    @Override
    public LipidSpecies handle(SwissLipidsParser.Lipid_pureContext ctx) {
        if (ctx.fatty_acid() != null) {
            if (ctx.fatty_acid().fa_hg() != null) {
                String headGroup = ctx.fatty_acid().fa_hg().getText();
                FaContext faContext = null;
                if (ctx.fatty_acid().fa_fa() != null && ctx.fatty_acid().fa_fa().fa() != null) {
                    faContext = ctx.fatty_acid().fa_fa().fa();
                    return visitSpeciesFas(headGroup, faContext).orElse(LipidSpecies.NONE);
                } else {
                    throw new ParseTreeVisitorException("Context for FA fa was null!");
                }
            } else if(ctx.fatty_acid().mediator() != null) {
                // mediator fron positions, e.g 11,12-DiHETrE
                    String mediatorPositions = "";
                    if(ctx.fatty_acid().mediator().med_positions() != null) {
                        mediatorPositions = ctx.fatty_acid().mediator().med_positions().getText();
                    }
                // E + Z positions
//                    if (ctx.fatty_acid().mediator().mediator_single() != null) {
//                        ctx.fatty_acid().mediator().mediator_single().db_positions();
//                    }
                    String mediatorsSingleContext = ctx.fatty_acid().mediator().mediator_single().getText();
                    Optional<LipidClass> lipidClass = LipidClass.forHeadGroup(mediatorsSingleContext);
                    return new LipidSpecies(ctx.fatty_acid().mediator().getText(),LipidCategory.FA, lipidClass, Optional.of(LipidSpeciesInfo.NONE));
            } else {
                throw new ParseTreeVisitorException("Context for FA head group was null!");
            }

        } else {
            throw new ParseTreeVisitorException("Context for FA was null!");
        }
    }

    public LipidFaBondType getLipidFaBondType(SwissLipidsParser.FaContext faContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        if (faContext.fa_core() != null && faContext.fa_core().ether() != null) {
            if ("O-".equals(faContext.fa_core().ether().getText())) {
                lfbt = LipidFaBondType.ETHER_PLASMANYL;
            } else if ("P-".equals(faContext.fa_core().ether().getText())) {
                lfbt = LipidFaBondType.ETHER_PLASMENYL;
            } else {
                throw new ParseTreeVisitorException("Unknown ether context value: " + faContext.fa_core().ether());
            }
        }
        return lfbt;
    }

    public <T extends RuleNode> Integer asInt(T context, Integer defaultValue) {
        return maybeMapOr(context, (t) -> {
            return Integer.parseInt(t.getText());
        }, defaultValue);
    }

    public static <T> Optional<T> maybe(T t) {
        return Optional.ofNullable(t);
    }

    public static <T, R> R maybeMapOr(T t, Function<? super T, R> mapper, R r) {
        return maybe(t).map(mapper).orElse(r);
    }

    public Optional<LipidSpecies> visitSpeciesLcb(String headGroup, SwissLipidsParser.LcbContext lcbContext) {
        return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(lcbContext)));
    }

    public Optional<LipidSpecies> visitSpeciesFas(String headGroup, SwissLipidsParser.FaContext faContext) {
        return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(faContext)));
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(SwissLipidsParser.FaContext faContext) {
        LipidFaBondType lfbt = getLipidFaBondType(faContext);
        int nHydroxyl = 0;
        if (faContext.fa_lcb_prefix() != null) {
            throw new ParseTreeVisitorException("Unsupported lcb prefix on fa: " + faContext.fa_lcb_prefix().getText());
        }
        if (faContext.fa_lcb_suffix() != null) {
            throw new ParseTreeVisitorException("Unsupported lcb suffix on fa: " + faContext.fa_lcb_suffix().getText());
        }
        return Optional.of(new LipidSpeciesInfo(
                LipidLevel.SPECIES,
                asInt(faContext.fa_core().carbon(), 0),
                nHydroxyl,
                asInt(faContext.fa_core().db(), 0),
                lfbt));
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(SwissLipidsParser.LcbContext lcbContext) {
        Integer hydroxyl = 0;
        if (lcbContext.lcb_core() != null) {
            SwissLipidsParser.Lcb_coreContext coreCtx = lcbContext.lcb_core();
            if (coreCtx.hydroxyl() != null) {
                switch (coreCtx.hydroxyl().getText()) {
                    case "t":
                        hydroxyl = 3;
                        break;
                    case "d":
                        hydroxyl = 2;
                        break;
                    case "m":
                        hydroxyl = 1;
                        break;
                    default:
                        throw new ParseTreeVisitorException("Unsupported old hydroxyl prefix: " + coreCtx.hydroxyl().getText());
                }
            }
            return Optional.of(new LipidSpeciesInfo(
                    LipidLevel.SPECIES,
                    asInt(coreCtx.carbon(), 0),
                    hydroxyl,
                    asInt(coreCtx.db(), 0), LipidFaBondType.ESTER));
        }
        throw new ParseTreeVisitorException("Uninitialized lcb_core context!");
    }
}

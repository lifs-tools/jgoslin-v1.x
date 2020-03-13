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

import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidCategory;
import de.isas.lipidomics.domain.LipidClass;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.palinom.SwissLipidsParser;
import de.isas.lipidomics.palinom.SwissLipidsParser.FaContext;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.List;
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
                    return new LipidSpecies(ctx.fatty_acid().mediator().getText(), LipidCategory.FA, lipidClass, Optional.of(LipidSpeciesInfo.NONE));
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
    
    public LipidFaBondType getLipidLcbBondType(String headGroup, SwissLipidsParser.LcbContext lcbContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
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
        return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(headGroup, lcbContext)));
    }

    public Optional<LipidSpecies> visitSpeciesFas(String headGroup, SwissLipidsParser.FaContext faContext) {
        return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(headGroup, faContext)));
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(String headGroup, SwissLipidsParser.FaContext faContext) {
        LipidFaBondType lfbt = getLipidFaBondType(faContext);
        int nHydroxyl = 0;
        if (faContext.fa_lcb_prefix() != null) {
            throw new ParseTreeVisitorException("Unsupported lcb prefix on fa: " + faContext.fa_lcb_prefix().getText());
        }
        if (faContext.fa_lcb_suffix() != null) {
            throw new ParseTreeVisitorException("Unsupported lcb suffix on fa: " + faContext.fa_lcb_suffix().getText());
        }
        return Optional.of(LipidSpeciesInfo.lipidSpeciesInfoBuilder().
                level(LipidLevel.SPECIES).
                name("FA").
                position(-1).
                nCarbon(asInt(faContext.fa_core().carbon(), 0)).
                nHydroxy(nHydroxyl).
                nDoubleBonds(asInt(faContext.fa_core().db(), 0)).
                lipidFaBondType(lfbt).
            build()
        );
    }
    
    public Integer getNHydroxyl(SwissLipidsParser.LcbContext lcbContext) {
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
                return hydroxyl;
            }
        }
        throw new ParseTreeVisitorException("Uninitialized lcb_core context!");
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(String headGroup, SwissLipidsParser.LcbContext lcbContext) {
        Integer nHydroxyl = 0;
        if (lcbContext.lcb_core() != null) {
            SwissLipidsParser.Lcb_coreContext coreCtx = lcbContext.lcb_core();
            if (coreCtx.hydroxyl() != null) {
                switch (coreCtx.hydroxyl().getText()) {
                    case "t":
                        nHydroxyl = 3;
                        break;
                    case "d":
                        nHydroxyl = 2;
                        break;
                    case "m":
                        nHydroxyl = 1;
                        break;
                    default:
                        throw new ParseTreeVisitorException("Unsupported old hydroxyl prefix: " + coreCtx.hydroxyl().getText());
                }
            }
            return Optional.of(LipidSpeciesInfo.lipidSpeciesInfoBuilder().
                    level(LipidLevel.SPECIES).
                    name("LCB").
                    position(-1).
                    nCarbon(asInt(coreCtx.carbon(), 0)).
                    nHydroxy(nHydroxyl).
                    nDoubleBonds(asInt(coreCtx.db(), 0)).
                    lipidFaBondType(getLipidLcbBondType(headGroup, lcbContext)).
                build()
            );
        }
        throw new ParseTreeVisitorException("Uninitialized lcb_core context!");
    }
    
    public boolean isIsomericFa(List<SwissLipidsParser.FaContext> faContexts) {
        for(SwissLipidsParser.FaContext faContext:faContexts) {
            if (faContext.fa_core()!= null) {
                SwissLipidsParser.Fa_coreContext coreCtx = faContext.fa_core();
                if(coreCtx.db()!=null) {
                    if(coreCtx.db().db_positions()!=null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isIsomericFa(SwissLipidsParser.FaContext faContext) {
        if (faContext.fa_core()!= null) {
            SwissLipidsParser.Fa_coreContext coreCtx = faContext.fa_core();
            if(coreCtx.db()!=null) {
                return coreCtx.db().db_positions()!=null;
            }
        }
        return false;
    }
    
    public boolean isIsomericLcb(SwissLipidsParser.LcbContext lcbContext) {
        if (lcbContext.lcb_core() != null) {
            SwissLipidsParser.Lcb_coreContext coreCtx = lcbContext.lcb_core();
            if(coreCtx.db()!=null) {
                return coreCtx.db().db_positions()!=null;
            }
        }
        return false;
    }
}

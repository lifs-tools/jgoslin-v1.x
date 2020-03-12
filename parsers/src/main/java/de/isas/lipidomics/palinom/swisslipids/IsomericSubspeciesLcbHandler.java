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
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.StructuralFattyAcid;
import de.isas.lipidomics.palinom.SwissLipidsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import static de.isas.lipidomics.palinom.goslin.GoslinFragmentsVisitorImpl.asInt;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author nilshoffmann
 */
public class IsomericSubspeciesLcbHandler {

    private final IsomericSubspeciesFasHandler isfh;
    private final FattyAcylHandler faHelperFunctions;
    
    public IsomericSubspeciesLcbHandler(IsomericSubspeciesFasHandler isfh, FattyAcylHandler faBondTypeResolver) {
        this.isfh = isfh;
        this.faHelperFunctions = faBondTypeResolver;
    }
    
    public Optional<LipidSpecies> visitIsomericSubspeciesLcb(String headGroup, SwissLipidsParser.LcbContext lcbContext, List<SwissLipidsParser.FaContext> faContexts) {
        List<StructuralFattyAcid> fas = new LinkedList<>();
        StructuralFattyAcid lcbA = buildIsomericLcb(lcbContext, "LCB", 1);
        fas.add(lcbA);
        for (int i = 0; i < faContexts.size(); i++) {
            IsomericFattyAcid fa = isfh.buildIsomericFa(faContexts.get(i), "FA" + (i + 1), i + 2);
            fas.add(fa);
        }
        IsomericFattyAcid[] arrs = new IsomericFattyAcid[fas.size()];
        fas.toArray(arrs);
        return Optional.of(new LipidIsomericSubspecies(headGroup, arrs));
    }

    public IsomericFattyAcid buildIsomericLcb(SwissLipidsParser.LcbContext ctx, String faName, int position) {
        IsomericFattyAcid.IsomericFattyAcidBuilder fa = IsomericFattyAcid.isomericFattyAcidBuilder();
        LipidFaBondType lfbt = faHelperFunctions.getLipidLcbBondType(ctx);
        if (ctx.lcb_core()!= null) {
            fa.nCarbon(faHelperFunctions.asInt(ctx.lcb_core().carbon(), 0));
            fa.nHydroxy(faHelperFunctions.getNHydroxyl(ctx));
            if (ctx.lcb_core().db() != null) {
                if (ctx.lcb_core().db().db_positions() != null) {
                    Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
                    if (ctx.lcb_core().db().db_positions().db_position().db_single_position() != null) {
                        SwissLipidsParser.Db_single_positionContext dbSingleCtx = ctx.lcb_core().db().db_positions().db_position().db_single_position();
                        doubleBondPositions.put(Integer.parseInt(dbSingleCtx.db_position_number().getText()), dbSingleCtx.cistrans().getText());
                    } else if (ctx.lcb_core().db().db_positions().db_position().db_position() != null) {
                        for(SwissLipidsParser.Db_positionContext dbCtx: ctx.lcb_core().db().db_positions().db_position().db_position()) {
                            SwissLipidsParser.Db_single_positionContext dbSingleCtx = dbCtx.db_single_position();
                            if(dbSingleCtx != null) {
                                doubleBondPositions.put(Integer.parseInt(dbSingleCtx.db_position_number().getText()), dbSingleCtx.cistrans().getText());
                            }
                        }
                    } else {
                         throw new ParseTreeVisitorException("Unhandled state in LCB IsomericFattyAcid!");
                    }
                    fa.doubleBondPositions(doubleBondPositions);
                } else { // handle cases like (0:0) but with at least one fa with isomeric subspecies level
                    Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
                    if(ctx.lcb_core().db().db_count() != null) {
                        int doubleBonds = faHelperFunctions.asInt(ctx.lcb_core().db().db_count(), 0);
                        if(doubleBonds != 0) {
                            throw new ParseTreeVisitorException("Unexpected number of double bonds: "+doubleBonds+" in LCB IsomericFattyAcid, with no positions defined!");
                        }
                    }
                    fa.doubleBondPositions(doubleBondPositions);
                }
            }
            fa.lipidFaBondType(lfbt);
            return fa.name(faName).position(position).build();
        } else {
            throw new ParseTreeVisitorException("Uninitialized FaContext!");
        }
    }
}

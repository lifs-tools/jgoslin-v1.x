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

import de.isas.lipidomics.domain.IsomericFattyAcid;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.StructuralFattyAcid;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author nilshoffmann
 */
public class IsomericSubspeciesLcbHandler {

    private final IsomericSubspeciesFasHandler isfh;
    private final FattyAcylHelper faHelper;

    public IsomericSubspeciesLcbHandler(IsomericSubspeciesFasHandler isfh, FattyAcylHelper faHelper) {
        this.isfh = isfh;
        this.faHelper = faHelper;
    }

    public Optional<LipidSpecies> visitIsomericSubspeciesLcb(String headGroup, LipidMapsParser.LcbContext lcbContext, List<LipidMapsParser.FaContext> faContexts) {
        List<StructuralFattyAcid> fas = new LinkedList<>();
        StructuralFattyAcid lcbA = buildIsomericLcb(headGroup, lcbContext, "LCB", 1);
        fas.add(lcbA);
        int nIsomericFas = 0;
        if (lcbA instanceof IsomericFattyAcid) {
            nIsomericFas++;
        }
        for (int i = 0; i < faContexts.size(); i++) {
            StructuralFattyAcid fa = isfh.buildIsomericFa(faContexts.get(i), "FA" + (i + 1), i + 2);
            fas.add(fa);
            if (fa instanceof IsomericFattyAcid) {
                nIsomericFas++;
            }
        }
        if (nIsomericFas == fas.size()) {
            IsomericFattyAcid[] arrs = new IsomericFattyAcid[fas.size()];
            fas.stream().map((t) -> {
                return (IsomericFattyAcid) t;
            }).collect(Collectors.toList()).toArray(arrs);
            return Optional.of(new LipidIsomericSubspecies(headGroup, arrs));
        } else {
            StructuralFattyAcid[] arrs = new StructuralFattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));
        }
    }

    public StructuralFattyAcid buildIsomericLcb(String headGroup, LipidMapsParser.LcbContext ctx, String faName, int position) {
        IsomericFattyAcid.IsomericFattyAcidBuilder fa = IsomericFattyAcid.isomericFattyAcidBuilder();
        LipidFaBondType lfbt = faHelper.getLipidLcbBondType(ctx);
        if (ctx.lcb_fa() != null) {
            if (ctx.lcb_fa().lcb_fa_unmod() != null) {
                LipidMapsParser.Lcb_fa_unmodContext factx = ctx.lcb_fa().lcb_fa_unmod();
                fa.nCarbon(HandlerUtils.asInt(factx.carbon(), 0));
                fa.nHydroxy(faHelper.getHydroxyCount(ctx));
                if (factx.db() != null) {
                    if (factx.db().db_positions() != null) {
                        Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
                        if (factx.db().db_positions().db_position().db_single_position() != null) {
                            LipidMapsParser.Db_single_positionContext dbSingleCtx = factx.db().db_positions().db_position().db_single_position();
                            doubleBondPositions.put(Integer.parseInt(dbSingleCtx.db_position_number().getText()), dbSingleCtx.cistrans().getText());
                        } else if (factx.db().db_positions().db_position().db_position() != null) {
                            for (LipidMapsParser.Db_positionContext dbCtx : factx.db().db_positions().db_position().db_position()) {
                                LipidMapsParser.Db_single_positionContext dbSingleCtx = dbCtx.db_single_position();
                                if (dbSingleCtx != null) {
                                    doubleBondPositions.put(Integer.parseInt(dbSingleCtx.db_position_number().getText()), dbSingleCtx.cistrans().getText());
                                }
                            }
                        } else {
                            throw new ParseTreeVisitorException("Unhandled state in LCB IsomericFattyAcid!");
                        }
                        fa.doubleBondPositions(doubleBondPositions);
                    } else { // handle cases like (0:0) but with at least one fa with isomeric subspecies level
                        Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
                        if (factx.db().db_count() != null) {
                            int doubleBonds = HandlerUtils.asInt(factx.db().db_count(), 0);
                            if (doubleBonds > 0) {
                                return StructuralFattyAcid.structuralFattyAcidBuilder().
                                        lipidFaBondType(lfbt).
                                        name(faName).
                                        lcb(true).
                                        nCarbon(HandlerUtils.asInt(factx.carbon(), 0)).
                                        nDoubleBonds(doubleBonds).
                                        build();
                            }
                        }
                        fa.doubleBondPositions(doubleBondPositions);
                    }
                }
                fa.lipidFaBondType(lfbt);
                return fa.name(faName).lcb(true).position(position).build();
            } else {
                throw new ParseTreeVisitorException("Uninitialized FaContext!");
            }
        } else if (ctx.lcb_fa().lcb_fa_mod() != null) {
            throw new ParseTreeVisitorException("Currently unsupported LCB Fa modified context!");
        } else {
            throw new ParseTreeVisitorException("Uninitialized LCB FaContext!");
        }
    }
}

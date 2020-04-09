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

import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.FattyAcidType;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.SwissLipidsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author nilshoffmann
 */
public class StructuralSubspeciesLcbHandler {

    private final StructuralSubspeciesFasHandler ssfh;
    private final IsomericSubspeciesLcbHandler islh;

    public StructuralSubspeciesLcbHandler(StructuralSubspeciesFasHandler ssfh, IsomericSubspeciesLcbHandler islh) {
        this.ssfh = ssfh;
        this.islh = islh;
    }

    public Optional<LipidSpecies> visitStructuralSubspeciesLcb(String headGroup, SwissLipidsParser.LcbContext lcbContext, List<SwissLipidsParser.FaContext> faContexts) {
        List<FattyAcid> fas = new LinkedList<>();
        FattyAcid lcbA = buildStructuralLcb(headGroup, lcbContext, "LCB", 1);
        fas.add(lcbA);
        int nIsomericFas = 0;
        if (lcbA.getType() == FattyAcidType.ISOMERIC) {
            nIsomericFas++;
        }
        for (int i = 0; i < faContexts.size(); i++) {
            FattyAcid fa = ssfh.buildStructuralFa(headGroup, faContexts.get(i), "FA" + (i + 1), i + 2);
            fas.add(fa);
            if (fa instanceof FattyAcid) {
                nIsomericFas++;
            }
        }
        if (nIsomericFas == fas.size()) {
            FattyAcid[] arrs = new FattyAcid[fas.size()];
            fas.stream().map((t) -> {
                return (FattyAcid) t;
            }).collect(Collectors.toList()).toArray(arrs);
            return Optional.of(new LipidIsomericSubspecies(headGroup, arrs));
        } else {
            FattyAcid[] arrs = new FattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));
        }
    }

    public Optional<LipidSpecies> visitStructuralSubspeciesLcb(String headGroup, SwissLipidsParser.LcbContext lcbContext) {
        FattyAcid fa = buildStructuralLcb(headGroup, lcbContext, "LCB", 1);
        return Optional.of(new LipidStructuralSubspecies(headGroup, fa));
    }

    public FattyAcid buildStructuralLcb(String headGroup, SwissLipidsParser.LcbContext ctx, String faName, int position) {
        SwissLipidsParser.Lcb_coreContext pureCtx = ctx.lcb_core();
        FattyAcid.StructuralFattyAcidBuilder fa = FattyAcid.structuralFattyAcidBuilder();
        fa.nCarbon(HandlerUtils.asInt(pureCtx.carbon(), 0));
        Integer hydroxyl = 0;
        if (pureCtx != null) {
            if (pureCtx.hydroxyl() != null) {
                switch (pureCtx.hydroxyl().getText()) {
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
                        throw new ParseTreeVisitorException("Unsupported old hydroxyl prefix: " + pureCtx.hydroxyl().getText());
                }
            }
        }
        fa.nHydroxy(hydroxyl);
        if (pureCtx.db() != null) {
            int nDoubleBonds = HandlerUtils.asInt(pureCtx.db().db_count(), 0);
            fa.nDoubleBonds(nDoubleBonds);
            if (pureCtx.db().db_positions() != null || nDoubleBonds == 0) {
                return islh.buildIsomericLcb(headGroup, ctx, faName, position);
            }
        }
        fa.lipidFaBondType(LipidFaBondType.ESTER);
        return fa.name(faName).position(position).lcb(true).build();
    }

}

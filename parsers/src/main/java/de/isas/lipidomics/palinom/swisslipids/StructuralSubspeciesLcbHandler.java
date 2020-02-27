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
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.StructuralFattyAcid;
import de.isas.lipidomics.palinom.SwissLipidsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author nilshoffmann
 */
public class StructuralSubspeciesLcbHandler {

    private final StructuralSubspeciesFasHandler ssfh;
    private final FattyAcylHandler faHelperFunctions;

    public StructuralSubspeciesLcbHandler(StructuralSubspeciesFasHandler ssfh, FattyAcylHandler faBondTypeResolver) {
        this.ssfh = ssfh;
        this.faHelperFunctions = faBondTypeResolver;
    }

    public Optional<LipidSpecies> visitStructuralSubspeciesLcb(String headGroup, SwissLipidsParser.LcbContext lcbContext, List<SwissLipidsParser.FaContext> faContexts) {
        List<StructuralFattyAcid> fas = new LinkedList<>();
        StructuralFattyAcid lcbA = buildStructuralLcb(lcbContext, "LCB", 1);
        fas.add(lcbA);
        for (int i = 0; i < faContexts.size(); i++) {
            StructuralFattyAcid fa = ssfh.buildStructuralFa(faContexts.get(i), "FA" + (i + 1), i + 2);
            fas.add(fa);
        }
        StructuralFattyAcid[] arrs = new StructuralFattyAcid[fas.size()];
        fas.toArray(arrs);
        return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));
    }

    public Optional<LipidSpecies> visitStructuralSubspeciesLcb(String headGroup, SwissLipidsParser.LcbContext lcbContext) {
        StructuralFattyAcid fa = buildStructuralLcb(lcbContext, "LCB", 1);
        return Optional.of(new LipidStructuralSubspecies(headGroup, fa));
    }

    public StructuralFattyAcid buildStructuralLcb(SwissLipidsParser.LcbContext ctx, String faName, int position) {
        SwissLipidsParser.Lcb_coreContext pureCtx = ctx.lcb_core();
        StructuralFattyAcid.StructuralFattyAcidBuilder fa = StructuralFattyAcid.structuralFaBuilder();
        fa.nCarbon(faHelperFunctions.asInt(pureCtx.carbon(), 0));
//        fa.nHydroxy(faHelperFunctions.asInt(pureCtx.hydroxyl(), 0));
        if (pureCtx.db() != null) {
            fa.nDoubleBonds(faHelperFunctions.asInt(pureCtx.db().db_count(), 0));
            if (pureCtx.db().db_positions() != null) {
                throw new RuntimeException("Support for double bond positions not implemented yet!");
            }
        }
        fa.lipidFaBondType(LipidFaBondType.ESTER);
        return fa.name(faName).position(position).lcb(true).build();
    }

//
//    public StructuralFattyAcid buildStructuralFa(SwissLipidsParser.FaContext ctx, String faName) {
//        StructuralFattyAcid.StructuralFattyAcidBuilder fa = StructuralFattyAcid.structuralFaBuilder();
//        LipidFaBondType lfbt = faHelperFunctions.getLipidFaBondType(ctx);
//        if (ctx.fa_core() != null) {
//            fa.nCarbon(faHelperFunctions.asInt(ctx.fa_core().carbon(), 0));
////            fa.nHydroxy(asInt(ctx.fa_core().hydroxyl(), 0));
//            if (ctx.fa_core().db() != null) {
//                fa.nDoubleBonds(faHelperFunctions.asInt(ctx.fa_core().db().db_count(), 0));
//                if (ctx.fa_core().db().db_positions() != null) {
//                    throw new RuntimeException("Support for double bond positions not implemented yet!");
//                }
//            }
//            fa.lipidFaBondType(lfbt);
//            return fa.name(faName).build();
//        } else if(ctx.fa_lcb_prefix() != null || ctx.fa_lcb_suffix() !=null) { //handling of lcbs
//            throw new ParseTreeVisitorException("LCBs currently not handled!");
//        } else {
//            throw new ParseTreeVisitorException("Uninitialized FaContext!");
//        }
//    }
}

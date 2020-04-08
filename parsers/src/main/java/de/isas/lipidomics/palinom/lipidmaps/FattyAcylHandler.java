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

import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.IsomericFattyAcid;
import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.MolecularFattyAcid;
import de.isas.lipidomics.domain.StructuralFattyAcid;
import de.isas.lipidomics.palinom.HandlerUtils;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author nilshoffmann
 */
public class FattyAcylHandler implements ParserRuleContextHandler<LipidMapsParser.Lipid_pureContext, LipidSpecies> {

    private final MolecularSubspeciesFasHandler msfah;
    private final StructuralSubspeciesFasHandler ssfah;
    private final FattyAcylHelper faHelper;

    public FattyAcylHandler(MolecularSubspeciesFasHandler msfah, StructuralSubspeciesFasHandler ssfah, FattyAcylHelper faHelper) {
        this.msfah = msfah;
        this.ssfah = ssfah;
        this.faHelper = faHelper;
    }

    @Override
    public LipidSpecies handle(LipidMapsParser.Lipid_pureContext ctx) {
        if (ctx.mediator() != null) {
            return LipidIsomericSubspecies.lipidIsomericSubspeciesBuilder().headGroup(ctx.mediator().getText()).fa(new IsomericFattyAcid[0]).build();
        } else if (ctx.pure_fa() != null) {
            return msfah.handlePureFaContext(ctx.pure_fa());
        }
        throw new ParseTreeVisitorException("Unhandled FA context: " + ctx.getText());
    }

    public Optional<LipidSpecies> visitSpeciesLcb(String headGroup, LipidMapsParser.LcbContext lcbContext) {
        return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(lcbContext)));
    }

    public Optional<LipidSpecies> visitSpeciesFas(String headGroup, LipidMapsParser.FaContext faContext) {
        return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(headGroup, faContext)));
    }

    public Optional<LipidSpecies> visitSubspeciesFas2(String headGroup, List<LipidMapsParser.Fa2Context> fa2Contexts) {
        List<FattyAcid> fas = new LinkedList<>();
        LipidLevel level = LipidLevel.UNDEFINED;
        for (int i = 0; i < fa2Contexts.size(); i++) {
            LipidMapsParser.Fa2Context fa2Ctx = fa2Contexts.get(i);
            if (fa2Ctx.fa2_sorted() != null) {
//                    if (level == LipidLevel.MOLECULAR_SUBSPECIES) {
//                        throw new ParseTreeVisitorException("CL second FAs group can not be on molecular level, first group was on structural level!");
//                    }
                level = LipidLevel.STRUCTURAL_SUBSPECIES;
                for (int j = 0; j < fa2Ctx.fa2_sorted().fa().size(); j++) {
                    StructuralFattyAcid fa = ssfah.buildStructuralFa(fa2Ctx.fa2_sorted().fa().get(j), "FA" + ((i + 1) + j), i + 1);
                    fas.add(fa);
                }
            } else if (fa2Ctx.fa2_unsorted() != null) {
//                    if (level == LipidLevel.STRUCTURAL_SUBSPECIES) {
//                        throw new ParseTreeVisitorException("CL second FAs group can not be on molecular level, first group was on structural level!");
//                    }
                level = LipidLevel.MOLECULAR_SUBSPECIES;
                for (int j = 0; j < fa2Ctx.fa2_unsorted().fa().size(); j++) {
                    MolecularFattyAcid fa = msfah.buildMolecularFa(fa2Ctx.fa2_unsorted().fa().get(i), "FA" + ((i + 1) + j));
                    fas.add(fa);
                }
            }
        }
        switch (level) {
            case MOLECULAR_SUBSPECIES:
                MolecularFattyAcid[] marrs = new MolecularFattyAcid[fas.size()];
                fas.toArray(marrs);
                return Optional.of(new LipidMolecularSubspecies(headGroup, marrs));
            case STRUCTURAL_SUBSPECIES:
                StructuralFattyAcid[] sarrs = new StructuralFattyAcid[fas.size()];
                fas.toArray(sarrs);
                return Optional.of(new LipidStructuralSubspecies(headGroup, sarrs));
            case ISOMERIC_SUBSPECIES:
                IsomericFattyAcid[] ifa = new IsomericFattyAcid[fas.size()];
                fas.toArray(ifa);
                return Optional.of(new LipidIsomericSubspecies(headGroup, ifa));
            default:
                throw new ParseTreeVisitorException("Unhandled lipid level for CL: " + level);
        }
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(String headGroup, LipidMapsParser.FaContext faContext) {
        if (faContext.fa_unmod() != null) {
            LipidFaBondType faBondType = faHelper.getLipidFaBondType(faContext);
            return Optional.of(new LipidSpeciesInfo(
                    LipidLevel.SPECIES,
                    HandlerUtils.asInt(faContext.fa_unmod().fa_pure().carbon(), 0),
                    HandlerUtils.asInt(faContext.fa_unmod().fa_pure().hydroxyl(), 0),
                    HandlerUtils.asInt(faContext.fa_unmod().fa_pure().db(), 0),
                    faBondType));
        } else if (faContext.fa_mod() != null) {
            throw new RuntimeException("Modified FA handling not implemented yet for " + faContext.getText());
        }
        throw new ParseTreeVisitorException("Unknown fa context value: " + faContext.getText());
    }

    public Optional<LipidSpeciesInfo> getSpeciesInfo(LipidMapsParser.LcbContext lcbContext) {
        Integer hydroxyl = 0;
        if (lcbContext.hydroxyl_lcb() != null) {
            hydroxyl = faHelper.getHydroxyCount(lcbContext);
        }
        String modification = "";
        if (lcbContext.lcb_fa().lcb_fa_mod() != null) {
            modification = lcbContext.lcb_fa().lcb_fa_mod().modification().getText();
        }
        if (lcbContext.lcb_fa().lcb_fa_unmod() != null) {
            return Optional.of(new LipidSpeciesInfo(
                    LipidLevel.SPECIES,
                    HandlerUtils.asInt(lcbContext.lcb_fa().lcb_fa_unmod().carbon(), 0),
                    hydroxyl,
                    HandlerUtils.asInt(lcbContext.lcb_fa().lcb_fa_unmod().db(), 0),
                    LipidFaBondType.ESTER));
        } else {
            throw new ParseTreeVisitorException("Unknown lcb fa context value: " + lcbContext.getText());
        }
    }

}

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

import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.SwissLipidsParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.SwissLipidsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author nilshoffmann
 */
public class SterolHandler implements ParserRuleContextHandler<Lipid_pureContext, LipidSpecies> {

    private final MolecularSubspeciesFasHandler msfh;
    private final StructuralSubspeciesFasHandler ssfh;
    
    public SterolHandler(MolecularSubspeciesFasHandler msfh, StructuralSubspeciesFasHandler ssfh) {
        this.msfh = msfh;
        this.ssfh = ssfh;
    }
    
    @Override
    public LipidSpecies handle(Lipid_pureContext t) {
        return handleSterol(t).orElse(LipidSpecies.NONE);
    }

    private Optional<LipidSpecies> handleSterol(SwissLipidsParser.Lipid_pureContext ctx) throws RuntimeException {
        if (ctx.st().st_species() != null) {
            return Optional.of(handleStSpecies(ctx.st().st_species()).orElse(LipidSpecies.NONE));
        } else if (ctx.st().st_sub1() != null) {
            return Optional.of(handleStFa1(ctx.st().st_sub1()).orElse(LipidSpecies.NONE));
        } else if (ctx.st().st_sub2() != null) {
            return Optional.of(handleStFa2(ctx.st().st_sub2()).orElse(LipidSpecies.NONE));
        } else {
            throw new ParseTreeVisitorException("Unhandled sterol lipid: " + ctx.st().getText());
        }
    }

    private Optional<LipidSpecies> handleStSpecies(SwissLipidsParser.St_speciesContext che) {
        String headGroup = che.st_species_hg().getText();
        if (che.st_species_fa() != null) {
            if (che.st_species_fa().fa_species() != null && che.st_species_fa().fa_species().fa() != null) {
                return ssfh.visitStructuralSubspeciesFas(headGroup, Arrays.asList(che.st_species_fa().fa_species().fa()));
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in sterol species fa!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in sterol species!");
        }
    }

    private Optional<LipidSpecies> handleStFa1(SwissLipidsParser.St_sub1Context che) {
        String headGroup = che.st_sub1_hg().getText();
        if (che.st_sub1_fa() != null) {
            return ssfh.visitStructuralSubspeciesFas(headGroup, Arrays.asList(che.st_sub1_fa().fa()));
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in sterol fa1!");
        }
    }

    private Optional<LipidSpecies> handleStFa2(SwissLipidsParser.St_sub2Context che) {
        String headGroup = che.st_sub2_hg().getText();
        if (che.st_sub2_fa() != null) {
            if (che.st_sub2_fa().fa2().fa2_unsorted() != null) {
                return msfh.visitMolecularSubspeciesFas(headGroup, che.st_sub2_fa().fa2().fa2_unsorted().fa());
            } else if (che.st_sub2_fa().fa2().fa2_sorted() != null) {
                return ssfh.visitStructuralSubspeciesFas(headGroup, che.st_sub2_fa().fa2().fa2_sorted().fa());
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in sterol fa2 FAs!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in sterol fa2!");
        }
    }
}

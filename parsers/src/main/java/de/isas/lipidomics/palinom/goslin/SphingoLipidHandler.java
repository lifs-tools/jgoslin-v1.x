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
package de.isas.lipidomics.palinom.goslin;

import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.GoslinParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.GoslinParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author nilshoffmann
 */
public class SphingoLipidHandler implements ParserRuleContextHandler<Lipid_pureContext, LipidSpecies> {

    private final StructuralSubspeciesLcbHandler sslh;
    private final FattyAcylHandler fah;

    public SphingoLipidHandler(StructuralSubspeciesLcbHandler sslh, FattyAcylHandler fah) {
        this.sslh = sslh;
        this.fah = fah;
    }

    @Override
    public LipidSpecies handle(Lipid_pureContext t) {
        return handleSphingolipid(t).orElse(LipidSpecies.NONE);
    }

    private Optional<LipidSpecies> handleSphingolipid(GoslinParser.Lipid_pureContext ctx) throws RuntimeException {
        if (ctx.sl().dsl() != null) {
            return handleDsl(ctx.sl().dsl());
        } else if (ctx.sl().lsl() != null) {
            return handleLsl(ctx.sl().lsl());
        } else {
            throw new RuntimeException("Unhandled sphingolipid: " + ctx.sl().getText());
        }
    }

    private Optional<LipidSpecies> handleDsl(GoslinParser.DslContext dsl) {
        String headGroup = dsl.hg_dslc().getText();
        if (dsl.sl_species() != null) { //species level
            //process species level
            return fah.visitSpeciesLcb(headGroup, dsl.sl_species().lcb());
        } else if (dsl.sl_subspecies() != null) {
            //process subspecies
            if (dsl.sl_subspecies().sorted_fa_separator() != null) {
                //sorted => StructuralSubspecies
                return sslh.visitStructuralSubspeciesLcb(headGroup, dsl.sl_subspecies().lcb(), Arrays.asList(dsl.sl_subspecies().fa()));
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in DSL!");
        }
        return Optional.empty();
    }

    private Optional<LipidSpecies> handleLsl(GoslinParser.LslContext lsl) {
        String headGroup = lsl.hg_lslc().getText();
        if (lsl.lcb() != null) { //species / subspecies level
            //process structural sub species level
            return sslh.visitStructuralSubspeciesLcb(headGroup, lsl.lcb());
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in LSL!");
        }
    }

}

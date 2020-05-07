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

import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.LipidMapsParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Handler implementation for Sterollipids.
 *
 * @author nilshoffmann
 */
public class SterolLipidHandler implements ParserRuleContextHandler<Lipid_pureContext, LipidSpecies> {

    private final StructuralSubspeciesFasHandler ssfh;

    public SterolLipidHandler(StructuralSubspeciesFasHandler ssfh) {
        this.ssfh = ssfh;
    }

    @Override
    public LipidSpecies handle(Lipid_pureContext t) {
        return handleSterol(t).orElse(LipidSpecies.NONE);
    }

    private Optional<LipidSpecies> handleSterol(LipidMapsParser.Lipid_pureContext ctx) throws RuntimeException {
        if (ctx.sterol().chc() != null) {
            return Optional.of(handleSt(ctx));
        } else if (ctx.sterol().chec() != null) {
            return Optional.of(handleSte(ctx.sterol().chec()).orElse(LipidSpecies.NONE));
        } else {
            throw new ParseTreeVisitorException("Unhandled sterol lipid: " + ctx.sterol().getText());
        }
    }

    private LipidSpecies handleSt(Lipid_pureContext ctx) {
        if (ctx.sterol() != null && ctx.sterol().chc().ch() != null) {
            return new LipidIsomericSubspecies(new HeadGroup(ctx.sterol().chc().ch().getText()));
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in Sterol!");
        }
    }

    private Optional<LipidSpecies> handleSte(LipidMapsParser.ChecContext che) {
        if (che.che_fa().fa() != null) {
            return ssfh.visitStructuralSubspeciesFas(new HeadGroup(che.che_fa().hg_che().getText()), Arrays.asList(che.che_fa().fa()));
        } else if (che.che() != null) {
            return ssfh.visitStructuralSubspeciesFas(new HeadGroup(che.che().hg_che().getText()), Arrays.asList(che.che().fa()));
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in ChE!");
        }
    }

}

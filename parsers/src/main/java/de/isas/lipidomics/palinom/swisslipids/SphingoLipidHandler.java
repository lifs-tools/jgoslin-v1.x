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
import java.util.List;
import java.util.Optional;

/**
 *
 * @author nilshoffmann
 */
public class SphingoLipidHandler implements ParserRuleContextHandler<Lipid_pureContext, LipidSpecies> {

    private final StructuralSubspeciesLcbHandler sslh;
    private final FattyAcylHandler fhf;

    public SphingoLipidHandler(StructuralSubspeciesLcbHandler sslh, FattyAcylHandler fhf) {
        this.sslh = sslh;
        this.fhf = fhf;
    }

    @Override
    public LipidSpecies handle(Lipid_pureContext t) {
        return handleSphingolipid(t).orElse(LipidSpecies.NONE);
    }

    private Optional<LipidSpecies> handleSphingolipid(SwissLipidsParser.Lipid_pureContext ctx) throws RuntimeException {
        if (ctx.sl() != null) {
            return handleSphingoLcb(ctx.sl());
        } else {
            throw new ParseTreeVisitorException("Context for SL was null!");
        }
    }

    private Optional<LipidSpecies> handleSphingoLcb(SwissLipidsParser.SlContext slc) {
        String headGroup = slc.sl_hg().getText();
        if (slc.sl_lcb() != null && slc.sl_lcb().sl_lcb_species() != null) { //species level
            return fhf.visitSpeciesLcb(headGroup, slc.sl_lcb().sl_lcb_species().lcb());
        } else if (slc.sl_lcb() != null && slc.sl_lcb().sl_lcb_subspecies() != null) { // subspecies level
            SwissLipidsParser.Sl_lcb_subspeciesContext slsc = slc.sl_lcb().sl_lcb_subspecies();
            List<SwissLipidsParser.FaContext> faContexts = Arrays.asList(slsc.fa());
            return sslh.visitStructuralSubspeciesLcb(headGroup, slsc.lcb(), faContexts);
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in SL!");
        }
    }

}

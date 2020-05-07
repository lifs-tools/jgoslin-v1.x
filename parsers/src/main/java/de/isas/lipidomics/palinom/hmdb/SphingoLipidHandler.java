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
package de.isas.lipidomics.palinom.hmdb;

import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.HMDBParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.HMDBParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Handler implementation for Sphingolipids.
 *
 * @author nilshoffmann
 */
public class SphingoLipidHandler implements ParserRuleContextHandler<Lipid_pureContext, LipidSpecies> {

    private final StructuralSubspeciesLcbHandler sslh;
    private final IsomericSubspeciesLcbHandler isfh;
    private final FattyAcylHandler fhf;

    public SphingoLipidHandler(StructuralSubspeciesLcbHandler sslh, IsomericSubspeciesLcbHandler isfh, FattyAcylHandler fhf) {
        this.sslh = sslh;
        this.isfh = isfh;
        this.fhf = fhf;
    }

    @Override
    public LipidSpecies handle(Lipid_pureContext t) {
        return handleSphingolipid(t).orElse(LipidSpecies.NONE);
    }

    private Optional<LipidSpecies> handleSphingolipid(HMDBParser.Lipid_pureContext ctx) throws RuntimeException {
        if (ctx.lipid_class().sl() != null) {
            return handleSphingoLcb(ctx.lipid_class().sl());
        } else {
            throw new ParseTreeVisitorException("Context for SL was null!");
        }
    }

    private Optional<LipidSpecies> handleSphingoLcb(HMDBParser.SlContext slc) {
        HeadGroup headGroup = new HeadGroup(slc.sl_hg().getText());
        if (slc.sl_lcb() != null && slc.sl_lcb().sl_lcb_species() != null) { //species level
            return fhf.visitSpeciesLcb(headGroup, slc.sl_lcb().sl_lcb_species().lcb());
        } else if (slc.sl_lcb() != null && slc.sl_lcb().sl_lcb_subspecies() != null) { // subspecies level
            HMDBParser.Sl_lcb_subspeciesContext slsc = slc.sl_lcb().sl_lcb_subspecies();
            List<HMDBParser.FaContext> faContexts = Arrays.asList(slsc.fa());
            if(fhf.isIsomericFa(slsc.fa())) {
                return isfh.visitIsomericSubspeciesLcb(headGroup, slsc.lcb(), faContexts);
            } else {
                return sslh.visitStructuralSubspeciesLcb(headGroup, slsc.lcb(), faContexts);
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in SL!");
        }
    }

}

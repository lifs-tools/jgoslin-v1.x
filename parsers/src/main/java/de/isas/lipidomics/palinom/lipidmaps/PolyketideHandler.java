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

import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.LipidMapsParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Handler for Polyketides.
 *
 * @author nilshoffmann
 */
public class PolyketideHandler implements ParserRuleContextHandler<Lipid_pureContext, LipidSpecies> {

    private final MolecularSubspeciesFasHandler msfh;
    private final StructuralSubspeciesFasHandler ssfh;
    private final FattyAcylHandler fhf;

    public PolyketideHandler(MolecularSubspeciesFasHandler msfh, StructuralSubspeciesFasHandler ssfh, FattyAcylHandler fhf) {
        this.msfh = msfh;
        this.ssfh = ssfh;
        this.fhf = fhf;
    }

    @Override
    public LipidSpecies handle(Lipid_pureContext t) {
        return handlePolyketide(t).orElse(LipidSpecies.NONE);
    }

    private Optional<LipidSpecies> handlePolyketide(LipidMapsParser.Lipid_pureContext ctx) throws RuntimeException {
        if (ctx.pk() != null) {
            return handlePk(ctx.pk());
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in GL!");
        }
    }

    private Optional<LipidSpecies> handlePk(LipidMapsParser.PkContext pk) {
        String headGroup = pk.pk_hg().getText();
        if (pk.pk_fa() != null) {
            //process subspecies
            if (pk.pk_fa().fa() != null) {
                //sorted => StructuralSubspecies
                return ssfh.visitStructuralSubspeciesFas(headGroup, Arrays.asList(pk.pk_fa().fa()));
            } else {
                throw new ParseTreeVisitorException("Empty FA context in PK!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in PK!");
        }
    }
}

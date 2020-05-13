/*
 * Copyright 2020  nils.hoffmann.
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
import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.LipidMapsParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Optional;

/**
 * Handler implementation for Glycerolipids.
 * @author  nils.hoffmann
 */
class GlyceroLipidHandler implements ParserRuleContextHandler<Lipid_pureContext, LipidSpecies> {

    private final MolecularSubspeciesFasHandler msfh;
    private final StructuralSubspeciesFasHandler ssfh;
    private final FattyAcylHandler fhf;

    public GlyceroLipidHandler(MolecularSubspeciesFasHandler msfh, StructuralSubspeciesFasHandler ssfh, FattyAcylHandler fhf) {
        this.msfh = msfh;
        this.ssfh = ssfh;
        this.fhf = fhf;
    }

    @Override
    public LipidSpecies handle(Lipid_pureContext t) {
        return handleGlycerolipid(t).orElse(LipidSpecies.NONE);
    }

    private Optional<LipidSpecies> handleGlycerolipid(LipidMapsParser.Lipid_pureContext ctx) throws RuntimeException {
        if (ctx.gl().sgl() != null) {
            return handleSgl(ctx.gl().sgl());
        } else if (ctx.gl().tgl() != null) {
            return handleTgl(ctx.gl().tgl());
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in GL!");
        }
    }

    private Optional<LipidSpecies> handleTgl(LipidMapsParser.TglContext tgl) {
        HeadGroup headGroup = new HeadGroup(tgl.hg_glc().getText());
        if (tgl.tgl_species() != null) {
            return fhf.visitSpeciesFas(headGroup, tgl.tgl_species().fa());
        } else if (tgl.tgl_subspecies() != null) {
            //process subspecies
            if (tgl.tgl_subspecies().fa3().fa3_sorted() != null) {
                //sorted => StructuralSubspecies
                return ssfh.visitStructuralSubspeciesFas(headGroup, tgl.tgl_subspecies().fa3().fa3_sorted().fa());
            } else if (tgl.tgl_subspecies().fa3().fa3_unsorted() != null) {
                //unsorted => MolecularSubspecies
                return msfh.visitMolecularSubspeciesFas(headGroup, tgl.tgl_subspecies().fa3().fa3_unsorted().fa());
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in TGL!");
        }
        return Optional.empty();
    }

    private Optional<LipidSpecies> handleSgl(LipidMapsParser.SglContext sgl) {
        HeadGroup headGroup = new HeadGroup(sgl.hg_sglc().getText());
        if (sgl.sgl_species() != null) { //species level
            //process species level
            return fhf.visitSpeciesFas(headGroup, sgl.sgl_species().fa());
        } else if (sgl.sgl_subspecies() != null) {
            //process subspecies
            if (sgl.sgl_subspecies().fa2().fa2_sorted() != null) {
                //sorted => StructuralSubspecies
                return ssfh.visitStructuralSubspeciesFas(headGroup, sgl.sgl_subspecies().fa2().fa2_sorted().fa());
            } else if (sgl.sgl_subspecies().fa2().fa2_unsorted() != null) {
                //unsorted => MolecularSubspecies
                return msfh.visitMolecularSubspeciesFas(headGroup, sgl.sgl_subspecies().fa2().fa2_unsorted().fa());
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in SGL!");
        }
        return Optional.empty();
    }
}

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

import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.GoslinParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.GoslinParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler implementation for Glycerolipids.
 * @author nilshoffmann
 */
@Slf4j
public class GlyceroLipidHandler implements ParserRuleContextHandler<Lipid_pureContext, LipidSpecies> {

    private final MolecularSubspeciesFasHandler msfh;
    private final StructuralSubspeciesFasHandler ssfh;
    private final FattyAcylHandler fah;

    public GlyceroLipidHandler(MolecularSubspeciesFasHandler msfh, StructuralSubspeciesFasHandler ssfh, FattyAcylHandler fah) {
        this.msfh = msfh;
        this.ssfh = ssfh;
        this.fah = fah;
    }

    @Override
    public LipidSpecies handle(Lipid_pureContext t) {
        return handleGlycerolipid(t).orElse(LipidSpecies.NONE);
    }

    private Optional<LipidSpecies> handleGlycerolipid(Lipid_pureContext ctx) throws RuntimeException {
        //glycerophospholipids
        //cardiolipin
        if (ctx.gl().dgl() != null) {
            return handleDgl(ctx.gl().dgl());
        } else if (ctx.gl().mgl() != null) {
            return handleMgl(ctx.gl().mgl());
        } else if (ctx.gl().sgl() != null) {
            return handleSgl(ctx.gl().sgl());
        } else if (ctx.gl().tgl() != null) {
            return handleTgl(ctx.gl().tgl());
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in GL!");
        }
    }

    private Optional<LipidSpecies> handleTgl(GoslinParser.TglContext tgl) {
        HeadGroup headGroup = new HeadGroup(tgl.hg_tgl_full().getText());
        if (tgl.gl_species() != null) { //species level
            //process species level
            return fah.visitSpeciesFas(headGroup, tgl.gl_species().fa());
        } else if (tgl.tgl_subspecies() != null) {
            //process subspecies
            if (tgl.tgl_subspecies().fa3().fa3_sorted() != null) {
                //sorted => StructuralSubspecies
                log.info("Building structural subspecies");
                return ssfh.visitStructuralSubspeciesFas(headGroup, tgl.tgl_subspecies().fa3().fa3_sorted().fa());
            } else if (tgl.tgl_subspecies().fa3().fa3_unsorted() != null) {
                //unsorted => MolecularSubspecies
                log.info("Building molecular subspecies");
                return msfh.visitMolecularSubspeciesFas(headGroup, tgl.tgl_subspecies().fa3().fa3_unsorted().fa());
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in TGL!");
        }
        return Optional.empty();
    }

    private Optional<LipidSpecies> handleSgl(GoslinParser.SglContext sgl) {
        HeadGroup headGroup = new HeadGroup(sgl.hg_sgl_full().getText());
        if (sgl.gl_species() != null) { //species level
            //process species level
            return fah.visitSpeciesFas(headGroup, sgl.gl_species().fa());
        } else if (sgl.dgl_subspecies() != null) {
            //process subspecies
            if (sgl.dgl_subspecies().fa2().fa2_sorted() != null) {
                //sorted => StructuralSubspecies
                return ssfh.visitStructuralSubspeciesFas(headGroup, sgl.dgl_subspecies().fa2().fa2_sorted().fa());
            } else if (sgl.dgl_subspecies().fa2().fa2_unsorted() != null) {
                //unsorted => MolecularSubspecies
                return msfh.visitMolecularSubspeciesFas(headGroup, sgl.dgl_subspecies().fa2().fa2_unsorted().fa());
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in SGL!");
        }
        return Optional.empty();
    }

    private Optional<LipidSpecies> handleMgl(GoslinParser.MglContext mgl) {
        HeadGroup headGroup = new HeadGroup(mgl.hg_mgl_full().getText());
        if (mgl.fa() != null) {
            return ssfh.visitStructuralSubspeciesFas(headGroup, Arrays.asList(mgl.fa()));
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in MGL!");
        }
    }

    private Optional<LipidSpecies> handleDgl(GoslinParser.DglContext dgl) {
        HeadGroup headGroup = new HeadGroup(dgl.hg_dgl_full().getText());
        if (dgl.gl_species() != null) { //species level
            //process species level
            return fah.visitSpeciesFas(headGroup, dgl.gl_species().fa());
        } else if (dgl.dgl_subspecies() != null) {
            //process subspecies
            if (dgl.dgl_subspecies().fa2().fa2_sorted() != null) {
                //sorted => StructuralSubspecies
                return ssfh.visitStructuralSubspeciesFas(headGroup, dgl.dgl_subspecies().fa2().fa2_sorted().fa());
            } else if (dgl.dgl_subspecies().fa2().fa2_unsorted() != null) {
                //unsorted => MolecularSubspecies
                return msfh.visitMolecularSubspeciesFas(headGroup, dgl.dgl_subspecies().fa2().fa2_unsorted().fa());
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in DGL!");
        }
        return Optional.empty();
    }
}

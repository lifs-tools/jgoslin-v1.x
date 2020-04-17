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
package de.isas.lipidomics.palinom.goslinfragments;

import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.GoslinFragmentsParser;
import de.isas.lipidomics.palinom.GoslinFragmentsParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Handler implementation for Glycero-phospholipids.
 *
 * @author nilshoffmann
 */
public class GlycerophosphoLipidHandler implements ParserRuleContextHandler<Lipid_pureContext, LipidSpecies> {

    private final MolecularSubspeciesFasHandler msfh;
    private final StructuralSubspeciesFasHandler ssfh;
    private final FattyAcylHandler fah;

    public GlycerophosphoLipidHandler(MolecularSubspeciesFasHandler msfh, StructuralSubspeciesFasHandler ssfh, FattyAcylHandler fah) {
        this.msfh = msfh;
        this.ssfh = ssfh;
        this.fah = fah;
    }

    @Override
    public LipidSpecies handle(Lipid_pureContext t) {
        return handleGlyceroPhospholipid(t).orElse(LipidSpecies.NONE);
    }

    private Optional<LipidSpecies> handleGlyceroPhospholipid(Lipid_pureContext ctx) throws RuntimeException {
        //glycerophospholipids
        //cardiolipin
        if (ctx.pl().cl() != null) {
            return handleCl(ctx.pl().cl());
        } else if (ctx.pl().dpl() != null) {
            return handleDpl(ctx.pl().dpl());
        } else if (ctx.pl().lpl() != null) {
            return handleLpl(ctx.pl().lpl());
        } else if (ctx.pl().mlcl() != null) {
            return handleMlcl(ctx.pl().mlcl());
        } else if (ctx.pl().pl_o() != null) {
            return handlePlo(ctx.pl().pl_o());
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in PL!");
        }
    }

    private Optional<LipidSpecies> handlePlo(GoslinFragmentsParser.Pl_oContext ploc) {
        if (ploc.dpl_o() != null) {
            String headGroup = ploc.dpl_o().hg_pl_oc().getText();
            if (ploc.dpl_o().pl_species() != null) {
                //process species
                return fah.visitSpeciesFas(headGroup, ploc.dpl_o().pl_species().fa());
            } else if (ploc.dpl_o().pl_subspecies() != null) {
                //process subspecies
                if (ploc.dpl_o().pl_subspecies().fa2().fa2_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return ssfh.visitStructuralSubspeciesFas(headGroup, ploc.dpl_o().pl_subspecies().fa2().fa2_sorted().fa());
                } else if (ploc.dpl_o().pl_subspecies().fa2().fa2_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    return msfh.visitMolecularSubspeciesFas(headGroup, ploc.dpl_o().pl_subspecies().fa2().fa2_unsorted().fa());
                }
            }
        } else if (ploc.lpl_o() != null) {
            String headGroup = ploc.lpl_o().hg_lpl_oc().getText();
            return ssfh.visitStructuralSubspeciesFas(headGroup, Arrays.asList(ploc.lpl_o().fa()));
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in PL O!");
        }
        return Optional.empty();
    }

    private Optional<LipidSpecies> handleCl(GoslinFragmentsParser.ClContext cl) {
        String headGroup = cl.hg_clc().getText();
        if (cl.pl_species() != null) { //species level
            //process species level
            return fah.visitSpeciesFas(headGroup, cl.pl_species().fa());
        } else if (cl.cl_subspecies() != null) {
            //process subspecies
            if (cl.cl_subspecies().fa4().fa4_sorted() != null) {
                //sorted => StructuralSubspecies
                return ssfh.visitStructuralSubspeciesFas(headGroup, cl.cl_subspecies().fa4().fa4_sorted().fa());
            } else if (cl.cl_subspecies().fa4().fa4_unsorted() != null) {
                //unsorted => MolecularSubspecies
                return msfh.visitMolecularSubspeciesFas(headGroup, cl.cl_subspecies().fa4().fa4_unsorted().fa());
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in CL!");
        }
        return Optional.empty();
    }

    private Optional<LipidSpecies> handleMlcl(GoslinFragmentsParser.MlclContext mlcl) {
        String headGroup = mlcl.hg_mlclc().getText();
        if (mlcl.pl_species() != null) { //species level
            //process species level
            return fah.visitSpeciesFas(headGroup, mlcl.pl_species().fa());
        } else if (mlcl.mlcl_subspecies() != null) {
            //process subspecies
            if (mlcl.mlcl_subspecies().fa3().fa3_sorted() != null) {
                //sorted => StructuralSubspecies
                return ssfh.visitStructuralSubspeciesFas(headGroup, mlcl.mlcl_subspecies().fa3().fa3_sorted().fa());
            } else if (mlcl.mlcl_subspecies().fa3().fa3_unsorted() != null) {
                //unsorted => MolecularSubspecies
                return msfh.visitMolecularSubspeciesFas(headGroup, mlcl.mlcl_subspecies().fa3().fa3_unsorted().fa());
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in CL!");
        }
        return Optional.empty();
    }

    private Optional<LipidSpecies> handleDpl(GoslinFragmentsParser.DplContext dpl) {
        String headGroup = dpl.hg_plc().getText();
        if (dpl.pl_species() != null) { //species level
            //process species level
            return fah.visitSpeciesFas(headGroup, dpl.pl_species().fa());
        } else if (dpl.pl_subspecies() != null) {
            //process subspecies
            if (dpl.pl_subspecies().fa2().fa2_sorted() != null) {
                //sorted => StructuralSubspecies
                return ssfh.visitStructuralSubspeciesFas(headGroup, dpl.pl_subspecies().fa2().fa2_sorted().fa());
            } else if (dpl.pl_subspecies().fa2().fa2_unsorted() != null) {
                //unsorted => MolecularSubspecies
                return msfh.visitMolecularSubspeciesFas(headGroup, dpl.pl_subspecies().fa2().fa2_unsorted().fa());
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in PL!");
        }
        return Optional.empty();
    }

    private Optional<LipidSpecies> handleLpl(GoslinFragmentsParser.LplContext lpl) {
        String headGroup = lpl.hg_lplc().getText();
        //lyso PL has one FA, Species=MolecularSubSpecies=StructuralSubSpecies
        if (lpl.fa() != null) {
            return ssfh.visitStructuralSubspeciesFas(headGroup, Arrays.asList(lpl.fa()));
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in PL!");
        }
    }

}

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
 * Handler implementation for Glycero-phospholipids.
 *
 * @author nilshoffmann
 */
public class GlycerophosphoLipidHandler implements ParserRuleContextHandler<Lipid_pureContext, LipidSpecies> {

    private final MolecularSubspeciesFasHandler msfh;
    private final StructuralSubspeciesFasHandler ssfh;
    private final FattyAcylHandler fhf;

    public GlycerophosphoLipidHandler(MolecularSubspeciesFasHandler msfh, StructuralSubspeciesFasHandler ssfh, FattyAcylHandler fhf) {
        this.msfh = msfh;
        this.ssfh = ssfh;
        this.fhf = fhf;
    }

    @Override
    public LipidSpecies handle(Lipid_pureContext t) {
        return handleGlyceroPhospholipid(t).orElse(LipidSpecies.NONE);
    }

    private Optional<LipidSpecies> handleGlyceroPhospholipid(LipidMapsParser.Lipid_pureContext ctx) throws RuntimeException {
        //glycerophospholipids
        //cardiolipin
        if (ctx.pl().cl() != null) {
            return handleCl(ctx.pl().cl());
        } else if (ctx.pl().dpl() != null) {
            return handleDpl(ctx.pl().dpl());
        } else if (ctx.pl().lpl() != null) {
            return handleLpl(ctx.pl().lpl());
        } else if (ctx.pl().threepl() != null) {
            return handleThreePl(ctx.pl().threepl());
        } else if (ctx.pl().fourpl() != null) {
            return handleFourPl(ctx.pl().fourpl());
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in PL!");
        }
    }

    private Optional<LipidSpecies> handleCl(LipidMapsParser.ClContext cl) {
        String headGroup = cl.hg_clc().getText();
        if (cl.cl_species() != null) { //species level
            //process species level
            return fhf.visitSpeciesFas(headGroup, cl.cl_species().fa());
        } else if (cl.cl_subspecies() != null) {
            //process subspecies
            if (cl.cl_subspecies().fa2() != null) {
                //sorted => StructuralSubspecies
                return fhf.visitSubspeciesFas2(headGroup, cl.cl_subspecies().fa2());
            } else {
                throw new ParseTreeVisitorException("CL had not fatty acids defined!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in CL!");
        }
    }

    private Optional<LipidSpecies> handleDpl(LipidMapsParser.DplContext dpl) {
        LipidMapsParser.Hg_ddplContext hg_ddplcontext = dpl.hg_ddpl();
        if (hg_ddplcontext != null) {
            String headGroup = hg_ddplcontext.hg_dplc().getText();
            if(hg_ddplcontext.pip_position() != null) {
                headGroup += hg_ddplcontext.pip_position().getText();
            }
            if (dpl.dpl_species() != null) { //species level
                //process species level
                return fhf.visitSpeciesFas(headGroup, dpl.dpl_species().fa());
            } else if (dpl.dpl_subspecies() != null) {
                //process subspecies
                if (dpl.dpl_subspecies().fa2().fa2_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return ssfh.visitStructuralSubspeciesFas(headGroup, dpl.dpl_subspecies().fa2().fa2_sorted().fa());
                } else if (dpl.dpl_subspecies().fa2().fa2_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    return msfh.visitMolecularSubspeciesFas(headGroup, dpl.dpl_subspecies().fa2().fa2_unsorted().fa());
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in PL!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in PL!");
        }
        return Optional.empty();
    }

    private Optional<LipidSpecies> handleLpl(LipidMapsParser.LplContext lpl) {
        String headGroup = lpl.hg_lplc().getText();
        //lyso PL has one FA, Species=MolecularSubSpecies=StructuralSubSpecies
        if (lpl.fa_lpl() != null) {
            if (lpl.fa_lpl().fa() != null) {
                return ssfh.visitStructuralSubspeciesFas(headGroup, Arrays.asList(lpl.fa_lpl().fa()));
            } else if (lpl.fa_lpl().fa2() != null) {
                LipidMapsParser.Fa2Context fa2ctx = lpl.fa_lpl().fa2();
                if (fa2ctx.fa2_sorted() != null) {
                    return ssfh.visitStructuralSubspeciesFas(headGroup, fa2ctx.fa2_sorted().fa());
                } else if (fa2ctx.fa2_unsorted() != null) {
                    throw new ParseTreeVisitorException("Lyso PL FAs are defined on structural subspecies level, provided FAs were defined on molecular subspecies level!");
                }
            }
            throw new ParseTreeVisitorException("Unhandled FA context state in Lyso PL!");
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in Lyso PL!");
        }
    }
    
    private Optional<LipidSpecies> handleThreePl(LipidMapsParser.ThreeplContext tpl) {
        LipidMapsParser.Hg_threeplcContext context = tpl.hg_threeplc();
        if (context != null) {
            String headGroup = context.hg_threepl().getText();
            if (tpl.species_fa() != null) { //species level
                //process species level
                return fhf.visitSpeciesFas(headGroup, tpl.species_fa().fa());
            } else if (tpl.fa3() != null) {
                //process subspecies
                if (tpl.fa3().fa3_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return ssfh.visitStructuralSubspeciesFas(headGroup, tpl.fa3().fa3_sorted().fa());
                } else if (tpl.fa3().fa3_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    return msfh.visitMolecularSubspeciesFas(headGroup, tpl.fa3().fa3_unsorted().fa());
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in three PL!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in three PL!");
        }
        return Optional.empty();
    }
    
    private Optional<LipidSpecies> handleFourPl(LipidMapsParser.FourplContext fpl) {
        LipidMapsParser.Hg_fourplcContext context = fpl.hg_fourplc();
        if (context != null) {
            String headGroup = context.hg_fourpl().getText();
            if (fpl.fa4() != null) { //species level
                //process species level
                return fhf.visitSpeciesFas(headGroup, fpl.species_fa().fa());
            } else if (fpl.fa4() != null) {
                //process subspecies
                if (fpl.fa4().fa4_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return ssfh.visitStructuralSubspeciesFas(headGroup, fpl.fa4().fa4_sorted().fa());
                } else if (fpl.fa4().fa4_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    return msfh.visitMolecularSubspeciesFas(headGroup, fpl.fa4().fa4_unsorted().fa());
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in four PL!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in four PL!");
        }
        return Optional.empty();
    }
}

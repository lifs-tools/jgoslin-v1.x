/*
 * Copyright 2019 nils.hoffmann.
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

import de.isas.lipidomics.domain.Adduct;
import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.Fragment;
import de.isas.lipidomics.domain.IsomericFattyAcid;
import de.isas.lipidomics.domain.IsomericFattyAcid.IsomericFattyAcidBuilder;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidCategory;
import static de.isas.lipidomics.domain.LipidCategory.GL;
import static de.isas.lipidomics.domain.LipidCategory.ST;
import de.isas.lipidomics.domain.LipidClass;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.MolecularFattyAcid;
import de.isas.lipidomics.domain.MolecularFattyAcid.MolecularFattyAcidBuilder;
import de.isas.lipidomics.domain.StructuralFattyAcid;
import de.isas.lipidomics.domain.StructuralFattyAcid.StructuralFattyAcidBuilder;
import de.isas.lipidomics.palinom.LipidMapsBaseVisitor;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.LipidMapsParser.Fa2Context;
import de.isas.lipidomics.palinom.LipidMapsParser.Hg_ddplContext;
import de.isas.lipidomics.palinom.LipidMapsParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import de.isas.lipidomics.palinom.swisslipids.SwissLipidsVisitorParser;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.RuleNode;

/**
 * Overriding implementation of {@link LipidMapsBaseVisitor}. Creates
 * {@link LipidAdduct} instances from the provided context.
 *
 * @see SwissLipidsVisitorParser
 * @author nils.hoffmann
 */
@Slf4j
class LipidMapsVisitorImpl extends LipidMapsBaseVisitor<LipidAdduct> {

    /**
     *
     * @throws ParseTreeVisitorException for structural or state-related issues
     * while trying to process a parsing context.
     * @throws RuntimeException
     * @param ctx
     * @return
     */
    @Override
    public LipidAdduct visitLipid(LipidMapsParser.LipidContext ctx) {
        LipidMapsParser.Lipid_ruleContext lipid = ctx.lipid_rule();
        if (lipid.isotope() != null) {
            throw new ParseTreeVisitorException("Support for isotopes in LipidMaps names not implemented yet!");
        }
        if (lipid.lipid_mono() != null && lipid.lipid_mono().isoform() != null) {
            throw new ParseTreeVisitorException("Support for isoforms in LipidMaps names not implemented yet!");
        }
        Optional<Lipid_pureContext> categoryContext = Optional.ofNullable(lipid.lipid_mono().lipid_pure());

        LipidAdduct la = new LipidAdduct(categoryContext.map((cc) -> {
            return new LipidVisitor().visitLipid_pure(cc);
        }).orElse(LipidSpecies.NONE), Adduct.NONE, "", new Fragment(""));
        return la;
    }

    private static class LipidVisitor extends LipidMapsBaseVisitor<LipidSpecies> {

        public LipidSpecies handlePureFaContext(LipidMapsParser.Pure_faContext ctx) {
            if (ctx.fa_no_hg() != null && ctx.fa_no_hg().fa() != null) {
                MolecularFattyAcid fa = buildMolecularFa(ctx.fa_no_hg().fa(), "FA1");
                LipidSpeciesInfo lsi = new LipidSpeciesInfo(
                        LipidLevel.SPECIES,
                        fa.getNCarbon(),
                        fa.getNHydroxy(),
                        fa.getNDoubleBonds(),
                        LipidFaBondType.UNDEFINED
                );
                LipidSpecies ls = new LipidSpecies(
                        ctx.hg_fa().getText(),
                        LipidCategory.FA,
                        LipidClass.forHeadGroup(ctx.hg_fa().getText()),
                        Optional.of(lsi)
                );
                return ls;
            } else if (ctx.pure_fa_species() != null && ctx.hg_fa() != null) {
                LipidMapsParser.Pure_fa_speciesContext speciesContext = ctx.pure_fa_species();
                if (speciesContext != null) {
                    MolecularFattyAcid fa = buildMolecularFa(speciesContext.fa(), "FA1");
                    LipidSpeciesInfo lsi = new LipidSpeciesInfo(
                            LipidLevel.SPECIES,
                            fa.getNCarbon(),
                            fa.getNHydroxy(),
                            fa.getNDoubleBonds(),
                            LipidFaBondType.UNDEFINED
                    );
                    LipidSpecies ls = new LipidSpecies(
                            ctx.hg_fa().getText(),
                            LipidCategory.FA,
                            LipidClass.forHeadGroup(ctx.hg_fa().getText()),
                            Optional.of(lsi)
                    );
                    return ls;
                } else {
                    throw new ParseTreeVisitorException("Unhandled pure FA species context: " + ctx.pure_fa_species());
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled pure FA: " + ctx.getText());
            }
        }

        @Override
        public LipidSpecies visitLipid_pure(LipidMapsParser.Lipid_pureContext ctx) {
            LipidSpecies lipid = null;
            BitSet bs = new BitSet(5);
            bs.set(LipidCategory.ST.ordinal(), ctx.cholesterol() != null);
            bs.set(LipidCategory.GL.ordinal(), ctx.gl() != null);
            bs.set(LipidCategory.FA.ordinal(), ctx.mediator() != null || ctx.pure_fa() != null);
            bs.set(LipidCategory.GP.ordinal(), ctx.pl() != null);
            bs.set(LipidCategory.SP.ordinal(), ctx.sl() != null);
            LipidCategory contextCategory = LipidCategory.UNDEFINED;
            switch (bs.cardinality()) {
                case 0:
                    throw new ParseTreeVisitorException("Parsing context did not contain content for any lipid category. Must contain exactly one of " + Arrays.toString(LipidCategory.values()));
                case 1:
                    contextCategory = LipidCategory.values()[bs.nextSetBit(0)];
                    break;
                default:
                    throw new ParseTreeVisitorException("Parsing context contained content for more than one lipid category. Must contain exactly one of " + Arrays.toString(LipidCategory.values()));
            }
            switch (contextCategory) {
                case ST:
                    if (ctx.cholesterol().chc() != null) {
                        lipid = handleCh(ctx);
                        break;
                    } else if (ctx.cholesterol().chec() != null) {
                        lipid = handleChe(ctx.cholesterol().chec()).orElse(LipidSpecies.NONE);
                        break;
                    } else {
                        throw new ParseTreeVisitorException("Unhandled sterol lipid: " + ctx.cholesterol().getText());
                    }
                case GL:
                    lipid = handleGlycerolipid(ctx).orElse(LipidSpecies.NONE);
                    break;
                case FA:
                    if (ctx.mediator() != null) {
                        lipid = new LipidIsomericSubspecies(ctx.mediator().getText());
                    } else if (ctx.pure_fa() != null) {
                        lipid = handlePureFaContext(ctx.pure_fa());
                    }
                    break;
                case GP:
                    lipid = handleGlyceroPhospholipid(ctx).orElse(LipidSpecies.NONE);
                    break;
                case SP:
                    if (ctx.sl().dsl() != null) {
                        lipid = handleDsl(ctx.sl().dsl()).orElse(LipidSpecies.NONE);
                    } else if (ctx.sl().lsl() != null) {
                        lipid = handleLsl(ctx.sl().lsl()).orElse(LipidSpecies.NONE);
                    } else {
                        throw new RuntimeException("Unhandled sphingolipid: " + ctx.sl().getText());
                    }
                    break;
                default:
                    throw new ParseTreeVisitorException("Unhandled contextCategory: " + contextCategory);
            }
            return lipid;
        }

        private LipidSpecies handleCh(Lipid_pureContext ctx) {
            if (ctx.cholesterol() != null && ctx.cholesterol().chc().ch() != null) {
                return new LipidIsomericSubspecies(ctx.cholesterol().chc().ch().getText());
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in Cholesterol!");
            }
        }

        private Optional<LipidSpecies> handleGlycerolipid(Lipid_pureContext ctx) throws RuntimeException {
            if (ctx.gl().sgl() != null) {
                return handleSgl(ctx.gl().sgl());
            } else if (ctx.gl().tgl() != null) {
                return handleTgl(ctx.gl().tgl());
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in GL!");
            }
        }

        private Optional<LipidSpecies> handleDsl(LipidMapsParser.DslContext dsl) {
            String headGroup = dsl.hg_dslc().getText();
            if (dsl.dsl_species() != null) { //species level
                //process species level
                return visitSpeciesLcb(headGroup, dsl.dsl_species().lcb());
            } else if (dsl.dsl_subspecies() != null) {
                //process subspecies
                if (dsl.dsl_subspecies().lcb_fa_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return visitStructuralSubspeciesLcb(headGroup, dsl.dsl_subspecies().lcb_fa_sorted().lcb(), Arrays.asList(dsl.dsl_subspecies().lcb_fa_sorted().fa()));
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in DSL!");
            }
            return Optional.empty();
        }

        private Optional<LipidSpecies> handleLsl(LipidMapsParser.LslContext lsl) {
            String headGroup = lsl.hg_lslc().getText();
            if (lsl.lcb() != null) { //species / subspecies level
                //process structural sub species level
                return visitStructuralSubspeciesLcb(headGroup, lsl.lcb());
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in LSL!");
            }
        }

        private Optional<LipidSpecies> handleTgl(LipidMapsParser.TglContext tgl) {
            String headGroup = tgl.hg_glc().getText();
            if (tgl.tgl_species() != null) {
                return visitSpeciesFas(headGroup, tgl.tgl_species().fa());
            } else if (tgl.tgl_subspecies() != null) {
                //process subspecies
                if (tgl.tgl_subspecies().fa3().fa3_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return visitStructuralSubspeciesFas(headGroup, tgl.tgl_subspecies().fa3().fa3_sorted().fa());
                } else if (tgl.tgl_subspecies().fa3().fa3_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    return visitMolecularSubspeciesFas(headGroup, tgl.tgl_subspecies().fa3().fa3_unsorted().fa());
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in TGL!");
            }
            return Optional.empty();
        }

        private Optional<LipidSpecies> handleSgl(LipidMapsParser.SglContext sgl) {
            String headGroup = sgl.hg_sglc().getText();
            if (sgl.sgl_species() != null) { //species level
                //process species level
                return visitSpeciesFas(headGroup, sgl.sgl_species().fa());
            } else if (sgl.sgl_subspecies() != null) {
                //process subspecies
                if (sgl.sgl_subspecies().fa2().fa2_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return visitStructuralSubspeciesFas(headGroup, sgl.sgl_subspecies().fa2().fa2_sorted().fa());
                } else if (sgl.sgl_subspecies().fa2().fa2_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    return visitMolecularSubspeciesFas(headGroup, sgl.sgl_subspecies().fa2().fa2_unsorted().fa());
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in SGL!");
            }
            return Optional.empty();
        }

        private Optional<LipidSpecies> handleChe(LipidMapsParser.ChecContext che) {
            if (che.che_fa().fa() != null) {
                return visitStructuralSubspeciesFas(che.che_fa().hg_che().getText(), Arrays.asList(che.che_fa().fa()));
            } else if (che.che() != null) {
                return visitStructuralSubspeciesFas(che.che().hg_che().getText(), Arrays.asList(che.che().fa()));
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in ChE!");
            }
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
            } else if (ctx.pl().fourpl() != null) {
                throw new RuntimeException("Support for PAT16 / PAT18 not implemented yet!");
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in PL!");
            }
        }

        private Optional<LipidSpecies> handleCl(LipidMapsParser.ClContext cl) {
            String headGroup = cl.hg_clc().getText();
            if (cl.cl_species() != null) { //species level
                //process species level
                return visitSpeciesFas(headGroup, cl.cl_species().fa());
            } else if (cl.cl_subspecies() != null) {
                //process subspecies
                if (cl.cl_subspecies().fa2() != null) {
                    //sorted => StructuralSubspecies
                    return visitSubspeciesFas2(headGroup, cl.cl_subspecies().fa2());
                } else {
                    throw new ParseTreeVisitorException("CL had not fatty acids defined!");
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in CL!");
            }
        }

        private Optional<LipidSpecies> handleDpl(LipidMapsParser.DplContext dpl) {
            Hg_ddplContext hg_ddplcontext = dpl.hg_ddpl();
            if (hg_ddplcontext != null) {
                String headGroup = hg_ddplcontext.hg_dplc().getText();
                // TODO implement pip handling
                //hg_ddplcontext.pip_position().pip_pos().
                if (dpl.dpl_species() != null) { //species level
                    //process species level
                    return visitSpeciesFas(headGroup, dpl.dpl_species().fa());
                } else if (dpl.dpl_subspecies() != null) {
                    //process subspecies
                    if (dpl.dpl_subspecies().fa2().fa2_sorted() != null) {
                        //sorted => StructuralSubspecies
                        return visitStructuralSubspeciesFas(headGroup, dpl.dpl_subspecies().fa2().fa2_sorted().fa());
                    } else if (dpl.dpl_subspecies().fa2().fa2_unsorted() != null) {
                        //unsorted => MolecularSubspecies
                        return visitMolecularSubspeciesFas(headGroup, dpl.dpl_subspecies().fa2().fa2_unsorted().fa());
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
                    return visitStructuralSubspeciesFas(headGroup, Arrays.asList(lpl.fa_lpl().fa()));
                } else if (lpl.fa_lpl().fa2() != null) {
                    Fa2Context fa2ctx = lpl.fa_lpl().fa2();
                    if (fa2ctx.fa2_sorted() != null) {
                        return visitStructuralSubspeciesFas(headGroup, fa2ctx.fa2_sorted().fa());
                    } else if (fa2ctx.fa2_unsorted() != null) {
                        throw new ParseTreeVisitorException("Lyso PL FAs are defined on structural subspecies level, provided FAs were defined on molecular subspecies level!");
                    }
                }
                throw new ParseTreeVisitorException("Unhandled FA context state in Lyso PL!");
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in Lyso PL!");
            }
        }

        private Optional<LipidSpecies> visitSpeciesLcb(String headGroup, LipidMapsParser.LcbContext lcbContext) {
            return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(lcbContext)));
        }

        private Optional<LipidSpecies> visitSpeciesFas(String headGroup, LipidMapsParser.FaContext faContext) {
            return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(headGroup, faContext)));
        }

        private Optional<LipidSpecies> visitMolecularSubspeciesFas(String headGroup, List<LipidMapsParser.FaContext> faContexts) {
            List<MolecularFattyAcid> fas = new LinkedList<>();
            for (int i = 0; i < faContexts.size(); i++) {
                MolecularFattyAcid fa = buildMolecularFa(faContexts.get(i), "FA" + (i + 1));
                fas.add(fa);
            }
            MolecularFattyAcid[] arrs = new MolecularFattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidMolecularSubspecies(headGroup, arrs));
        }

        private Optional<LipidSpecies> visitStructuralSubspeciesLcb(String headGroup, LipidMapsParser.LcbContext lcbContext, List<LipidMapsParser.FaContext> faContexts) {
            List<StructuralFattyAcid> fas = new LinkedList<>();
            StructuralFattyAcid lcbA = buildStructuralLcb(lcbContext, "LCB", 1);
            fas.add(lcbA);
            int nIsomericFas = 0;
            if (lcbA instanceof IsomericFattyAcid) {
                nIsomericFas++;
            }
            for (int i = 0; i < faContexts.size(); i++) {
                StructuralFattyAcid fa = buildStructuralFa(faContexts.get(i), "FA" + (i + 1), i + 2);
                fas.add(fa);
                if (fa instanceof IsomericFattyAcid) {
                    nIsomericFas++;
                }
            }
            if (nIsomericFas == fas.size()) {
                IsomericFattyAcid[] arrs = new IsomericFattyAcid[fas.size()];
                fas.stream().map((t) -> {
                    return (IsomericFattyAcid) t;
                }).collect(Collectors.toList()).toArray(arrs);
                return Optional.of(new LipidIsomericSubspecies(headGroup, arrs));
            } else {
                StructuralFattyAcid[] arrs = new StructuralFattyAcid[fas.size()];
                fas.toArray(arrs);
                return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));
            }
        }

        private Optional<LipidSpecies> visitStructuralSubspeciesFas(String headGroup, List<LipidMapsParser.FaContext> faContexts) {
            List<StructuralFattyAcid> fas = new LinkedList<>();
            int nIsomericFas = 0;
            for (int i = 0; i < faContexts.size(); i++) {
                StructuralFattyAcid fa = buildStructuralFa(faContexts.get(i), "FA" + (i + 1), i + 1);
                fas.add(fa);
                if (fa instanceof IsomericFattyAcid) {
                    nIsomericFas++;
                }
            }
            if (nIsomericFas == fas.size()) {
                IsomericFattyAcid[] arrs = new IsomericFattyAcid[fas.size()];
                fas.stream().map((t) -> {
                    return (IsomericFattyAcid) t;
                }).collect(Collectors.toList()).toArray(arrs);
                return Optional.of(new LipidIsomericSubspecies(headGroup, arrs));
            } else {
                StructuralFattyAcid[] arrs = new StructuralFattyAcid[fas.size()];
                fas.toArray(arrs);
                return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));
            }
        }

        private Optional<LipidSpecies> visitSubspeciesFas2(String headGroup, List<LipidMapsParser.Fa2Context> fa2Contexts) {
            List<FattyAcid> fas = new LinkedList<>();
            LipidLevel level = LipidLevel.UNDEFINED;
            for (int i = 0; i < fa2Contexts.size(); i++) {
                Fa2Context fa2Ctx = fa2Contexts.get(i);
                if (fa2Ctx.fa2_sorted() != null) {
//                    if (level == LipidLevel.MOLECULAR_SUBSPECIES) {
//                        throw new ParseTreeVisitorException("CL second FAs group can not be on molecular level, first group was on structural level!");
//                    }
                    level = LipidLevel.STRUCTURAL_SUBSPECIES;
                    for (int j = 0; j < fa2Ctx.fa2_sorted().fa().size(); j++) {
                        StructuralFattyAcid fa = buildStructuralFa(fa2Ctx.fa2_sorted().fa().get(j), "FA" + ((i + 1) + j), i + 1);
                        fas.add(fa);
                    }
                } else if (fa2Ctx.fa2_unsorted() != null) {
//                    if (level == LipidLevel.STRUCTURAL_SUBSPECIES) {
//                        throw new ParseTreeVisitorException("CL second FAs group can not be on molecular level, first group was on structural level!");
//                    }
                    level = LipidLevel.MOLECULAR_SUBSPECIES;
                    for (int j = 0; j < fa2Ctx.fa2_unsorted().fa().size(); j++) {
                        MolecularFattyAcid fa = buildMolecularFa(fa2Ctx.fa2_unsorted().fa().get(i), "FA" + ((i + 1) + j));
                        fas.add(fa);
                    }
                }
            }
            switch (level) {
                case MOLECULAR_SUBSPECIES:
                    MolecularFattyAcid[] marrs = new MolecularFattyAcid[fas.size()];
                    fas.toArray(marrs);
                    return Optional.of(new LipidMolecularSubspecies(headGroup, marrs));
                case STRUCTURAL_SUBSPECIES:
                    StructuralFattyAcid[] sarrs = new StructuralFattyAcid[fas.size()];
                    fas.toArray(sarrs);
                    return Optional.of(new LipidStructuralSubspecies(headGroup, sarrs));
                case ISOMERIC_SUBSPECIES:
                    IsomericFattyAcid[] ifa = new IsomericFattyAcid[fas.size()];
                    fas.toArray(ifa);
                    return Optional.of(new LipidIsomericSubspecies(headGroup, ifa));
                default:
                    throw new ParseTreeVisitorException("Unhandled lipid level for CL: " + level);
            }
        }

        private Optional<LipidSpecies> visitStructuralSubspeciesLcb(String headGroup, LipidMapsParser.LcbContext lcbContext) {
            StructuralFattyAcid fa = buildStructuralLcb(lcbContext, "FA" + 1, 1);
            return Optional.of(new LipidStructuralSubspecies(headGroup, fa));
        }

        private Optional<LipidSpeciesInfo> getSpeciesInfo(String headGroup, LipidMapsParser.FaContext faContext) {
//                       if (faContext.fa_pure() != null && faContext.heavy_fa() != null) {
//                throw new RuntimeException("Heavy label in FA_pure context not implemented yet!");
//            }
//
//            LipidSpeciesInfo lsi = new LipidSpeciesInfo(
//                    LipidLevel.SPECIES,
//                    asInt(faContext.fa_pure().carbon(), 0),
//                    asInt(faContext.fa_pure().hydroxyl(), 0),
//                    asInt(faContext.fa_pure().db(), 0),
//                    getLipidFaBondType(faContext));
//            LipidFaBondType consensusBondType = LipidFaBondType.getLipidFaBondType(headGroup, lsi);
//            return Optional.of(new LipidSpeciesInfo(
//                    LipidLevel.SPECIES,
//                    asInt(faContext.fa_pure().carbon(), 0),
//                    asInt(faContext.fa_pure().hydroxyl(), 0),
//                    asInt(faContext.fa_pure().db(), 0),
//                    consensusBondType)
//            );
            if (faContext.fa_unmod() != null) {
                LipidFaBondType faBondType = getLipidFaBondTypeUnmod(faContext);
                int plasmenylEtherDbBondCorrection = 0;
//                switch (faBondType) {
//                    case ETHER_PLASMENYL:
//                        plasmenylEtherDbBondCorrection = 1;
//                    default:
//                        plasmenylEtherDbBondCorrection = 0;
//                }
                return Optional.of(new LipidSpeciesInfo(
                        LipidLevel.SPECIES,
                        asInt(faContext.fa_unmod().fa_pure().carbon(), 0),
                        asInt(faContext.fa_unmod().fa_pure().hydroxyl(), 0),
                        plasmenylEtherDbBondCorrection + asInt(faContext.fa_unmod().fa_pure().db(), 0),
                        faBondType));
            } else if (faContext.fa_mod() != null) {
                throw new RuntimeException("Modified FA handling not implemented yet for " + faContext.getText());
            }
            throw new ParseTreeVisitorException("Unknown fa context value: " + faContext.getText());
        }

        private Optional<LipidSpeciesInfo> getSpeciesInfo(LipidMapsParser.LcbContext lcbContext) {
            Integer hydroxyl = 0;
            if (lcbContext.hydroxyl_lcb() != null) {
                hydroxyl = getHydroxyCount(lcbContext);
            }
            String modification = "";
            if (lcbContext.lcb_fa().lcb_fa_mod() != null) {
                modification = lcbContext.lcb_fa().lcb_fa_mod().modification().getText();
            }
            if (lcbContext.lcb_fa().lcb_fa_unmod() != null) {
                return Optional.of(new LipidSpeciesInfo(
                        LipidLevel.SPECIES,
                        asInt(lcbContext.lcb_fa().lcb_fa_unmod().carbon(), 0),
                        hydroxyl,
                        asInt(lcbContext.lcb_fa().lcb_fa_unmod().db(), 0),
                        LipidFaBondType.ESTER));
            } else {
                throw new ParseTreeVisitorException("Unknown lcb fa context value: " + lcbContext.getText());
            }
        }
    }

    public static LipidFaBondType getLipidFaBondTypeUnmod(LipidMapsParser.FaContext faContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        if (faContext.fa_unmod().ether() != null) {
            if (null == faContext.fa_unmod().ether().getText()) {
                throw new ParseTreeVisitorException("Undefined ether context value!");
            } else {
                switch (faContext.fa_unmod().ether().getText()) {
                    case "O-":
                        lfbt = LipidFaBondType.ETHER_PLASMANYL;
                        break;
                    case "P-":
                        lfbt = LipidFaBondType.ETHER_PLASMENYL;
                        break;
                    default:
                        throw new ParseTreeVisitorException("Unknown ether context value: " + faContext.fa_unmod().ether());
                }
            }
        }
        return lfbt;
    }

    public static MolecularFattyAcid buildMolecularFa(LipidMapsParser.FaContext ctx, String faName) {
        MolecularFattyAcidBuilder fa = MolecularFattyAcid.molecularFattyAcidBuilder();
        if (ctx.fa_unmod() != null) {
            LipidFaBondType faBondType = getLipidFaBondTypeUnmod(ctx);
            int plasmenylEtherDbBondCorrection = 0;
//            switch (faBondType) {
//                case ETHER_PLASMENYL:
//                    plasmenylEtherDbBondCorrection = 1;
//                default:
//                    plasmenylEtherDbBondCorrection = 0;
//            }
            fa.lipidFaBondType(faBondType);
            fa.nCarbon(asInt(ctx.fa_unmod().fa_pure().carbon(), 0));
            fa.nHydroxy(asInt(ctx.fa_unmod().fa_pure().hydroxyl(), 0));
            if (ctx.fa_unmod().fa_pure().db() != null) {
                fa.nDoubleBonds(plasmenylEtherDbBondCorrection + asInt(ctx.fa_unmod().fa_pure().db().db_count(), 0));
                if (ctx.fa_unmod().fa_pure().db().db_positions() != null) {
                    throw new RuntimeException("Support for double bond positions not implemented yet!");
                }
            }
            return fa.name(faName).build();

        } else if (ctx.fa_mod() != null) {
            throw new RuntimeException("Support for modified FA handling not implemented!");
        } else {
            throw new ParseTreeVisitorException("No FaContext!");
        }
    }

    public static <T extends RuleNode> Integer asInt(T context, Integer defaultValue) {
        return maybeMapOr(context, (t) -> {
            return Integer.parseInt(t.getText());
        }, defaultValue);
    }

    public static <T> Optional<T> maybe(T t) {
        return Optional.ofNullable(t);
    }

    public static <T, R> R maybeMapOr(T t, Function<? super T, R> mapper, R r) {
        return maybe(t).map(mapper).orElse(r);
    }

    public static StructuralFattyAcid buildIsomericLcb(LipidMapsParser.LcbContext ctx, String faName, int position) {
        IsomericFattyAcidBuilder fa = IsomericFattyAcid.isomericFattyAcidBuilder();
        // FIXME handle these once they are defined
        String modifications = "";
        if (ctx.lcb_fa().lcb_fa_mod() != null) {
            if (ctx.lcb_fa().lcb_fa_mod().modification() != null) {
                modifications = ctx.lcb_fa().lcb_fa_mod().modification().getText();
            }
        }
        if (ctx.lcb_fa().lcb_fa_unmod() != null) {
            fa.nCarbon(asInt(ctx.lcb_fa().lcb_fa_unmod().carbon(), 0));
            Integer nHydroxy = getHydroxyCount(ctx);
            fa.nHydroxy(nHydroxy);
            if (ctx.lcb_fa().lcb_fa_unmod().db() != null) {
                if (ctx.lcb_fa().lcb_fa_unmod().db().db_positions() != null) {
                    Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
                    LipidMapsParser.Db_positionContext dbPosCtx = ctx.lcb_fa().lcb_fa_unmod().db().db_positions().db_position();
                    if (dbPosCtx.db_single_position() != null) {
                        Integer dbPosition = asInt(dbPosCtx.db_single_position().db_position_number(), -1);
                        String cisTrans = dbPosCtx.db_single_position().cistrans().getText();
                        doubleBondPositions.put(dbPosition, cisTrans);
                    } else if (dbPosCtx.db_position() != null) {
                        for (LipidMapsParser.Db_positionContext dbpos : dbPosCtx.db_position()) {
                            if (dbpos.db_single_position() != null) {
                                Integer dbPosition = asInt(dbpos.db_single_position().db_position_number(), -1);
                                String cisTrans = dbpos.db_single_position().cistrans().getText();
                                doubleBondPositions.put(dbPosition, cisTrans);
                            }
                        }
                    }
                    fa.doubleBondPositions(doubleBondPositions);
                } else {
                    fa.doubleBondPositions(Collections.emptyMap());
                }
            }
            return fa.name(faName).position(position).lcb(true).lipidFaBondType(LipidFaBondType.ESTER).build();
        } else {
            throw new ParseTreeVisitorException("No LcbContext!");
        }
    }

    public static StructuralFattyAcid buildStructuralLcb(LipidMapsParser.LcbContext ctx, String faName, int position) {
        StructuralFattyAcidBuilder fa = StructuralFattyAcid.structuralFattyAcidBuilder();
        // FIXME handle these once they are defined
        String modifications = "";
        if (ctx.lcb_fa().lcb_fa_mod() != null) {
            if (ctx.lcb_fa().lcb_fa_mod().modification() != null) {
                modifications = ctx.lcb_fa().lcb_fa_mod().modification().getText();
            }
        }
        if (ctx.lcb_fa().lcb_fa_unmod() != null) {
            fa.nCarbon(asInt(ctx.lcb_fa().lcb_fa_unmod().carbon(), 0));
            Integer nHydroxy = getHydroxyCount(ctx);
            fa.nHydroxy(nHydroxy);
            if (ctx.lcb_fa().lcb_fa_unmod().db() != null) {
                int nDoubleBonds = asInt(ctx.lcb_fa().lcb_fa_unmod().db().db_count(), 0);
                fa.nDoubleBonds(nDoubleBonds);
                if (ctx.lcb_fa().lcb_fa_unmod().db().db_positions() != null || nDoubleBonds == 0) {
                    return buildIsomericLcb(ctx, faName, position);
                }
            }
            return fa.name(faName).position(position).lcb(true).lipidFaBondType(LipidFaBondType.ESTER).build();
        } else {
            throw new ParseTreeVisitorException("No LcbContext!");
        }
    }

    private static Integer getHydroxyCount(LipidMapsParser.LcbContext ctx) {
        Integer nHydroxy = Optional.ofNullable(ctx.hydroxyl_lcb()).map((t) -> {
            String hydroxy = t.getText();
            switch (hydroxy) {
                case "m":
                    return 1;
                case "d":
                    return 2;
                case "t":
                    return 3;
            }
            return Integer.valueOf(0);
        }).orElse(0);
        return nHydroxy;
    }

    public static StructuralFattyAcid buildIsomericFa(LipidMapsParser.FaContext ctx, String faName, int position) {
        IsomericFattyAcidBuilder fa = IsomericFattyAcid.isomericFattyAcidBuilder();
        String modifications = "";
        if (ctx.fa_mod() != null) {
            if (ctx.fa_mod().modification() != null) {
                modifications = ctx.fa_mod().modification().getText();
            }
        }
        if (ctx.fa_unmod() != null) {
            LipidFaBondType faBondType = getLipidFaBondTypeUnmod(ctx);
            int plasmenylEtherDbBondCorrection = 0;
//            switch (faBondType) {
//                case ETHER_PLASMENYL:
//                    plasmenylEtherDbBondCorrection = 1;
//                default:
//                    plasmenylEtherDbBondCorrection = 0;
//            }
            fa.lipidFaBondType(faBondType);
            fa.nCarbon(asInt(ctx.fa_unmod().fa_pure().carbon(), 0));
            fa.nHydroxy(asInt(ctx.fa_unmod().fa_pure().hydroxyl(), 0));
            if (ctx.fa_unmod().fa_pure().db() != null) {
                if (ctx.fa_unmod().fa_pure().db().db_positions() != null) {
                    Map<Integer, String> doubleBondPositions = new LinkedHashMap<>();
                    LipidMapsParser.Db_positionContext dbPosCtx = ctx.fa_unmod().fa_pure().db().db_positions().db_position();
                    if (dbPosCtx.db_single_position() != null) {
                        Integer dbPosition = asInt(dbPosCtx.db_single_position().db_position_number(), -1);
                        String cisTrans = dbPosCtx.db_single_position().cistrans().getText();
                        doubleBondPositions.put(dbPosition, cisTrans);
                    } else if (dbPosCtx.db_position() != null) {
                        for (LipidMapsParser.Db_positionContext dbpos : dbPosCtx.db_position()) {
                            if (dbpos.db_single_position() != null) {
                                Integer dbPosition = asInt(dbpos.db_single_position().db_position_number(), -1);
                                String cisTrans = dbpos.db_single_position().cistrans().getText();
                                doubleBondPositions.put(dbPosition, cisTrans);
                            }
                        }
                    }
                    fa.doubleBondPositions(doubleBondPositions);
                } else {
                    fa.doubleBondPositions(Collections.emptyMap());
                }
            }
            return fa.name(faName).position(position).build();
        } else {
            throw new ParseTreeVisitorException("No FaContext!");
        }
    }

    public static StructuralFattyAcid buildStructuralFa(LipidMapsParser.FaContext ctx, String faName, int position) {
        StructuralFattyAcidBuilder fa = StructuralFattyAcid.structuralFattyAcidBuilder();
        String modifications = "";
        if (ctx.fa_mod() != null) {
            if (ctx.fa_mod().modification() != null) {
                modifications = ctx.fa_mod().modification().getText();
            }
        }
        if (ctx.fa_unmod() != null) {
            LipidFaBondType faBondType = getLipidFaBondTypeUnmod(ctx);
            int plasmenylEtherDbBondCorrection = 0;
//            switch (faBondType) {
//                case ETHER_PLASMENYL:
//                    plasmenylEtherDbBondCorrection = 1;
//                default:
//                    plasmenylEtherDbBondCorrection = 0;
//            }
            fa.lipidFaBondType(faBondType);
            fa.nCarbon(asInt(ctx.fa_unmod().fa_pure().carbon(), 0));
            fa.nHydroxy(asInt(ctx.fa_unmod().fa_pure().hydroxyl(), 0));
            if (ctx.fa_unmod().fa_pure().db() != null) {
                int nDoubleBonds = asInt(ctx.fa_unmod().fa_pure().db().db_count(), 0);
                fa.nDoubleBonds(plasmenylEtherDbBondCorrection + nDoubleBonds);
                if (ctx.fa_unmod().fa_pure().db().db_positions() != null || nDoubleBonds == 0) {
                    return buildIsomericFa(ctx, faName, position);
                }
            }
            return fa.name(faName).position(position).build();
        } else {
            throw new ParseTreeVisitorException("No FaContext!");
        }
    }
}

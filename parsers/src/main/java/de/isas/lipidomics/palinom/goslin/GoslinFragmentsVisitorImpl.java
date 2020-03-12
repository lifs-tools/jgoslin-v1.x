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

import de.isas.lipidomics.domain.Adduct;
import de.isas.lipidomics.domain.Fragment;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidCategory;
import de.isas.lipidomics.domain.LipidClass;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.MolecularFattyAcid;
import de.isas.lipidomics.domain.StructuralFattyAcid;
import de.isas.lipidomics.palinom.GoslinFragmentsBaseVisitor;
import de.isas.lipidomics.palinom.GoslinFragmentsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.RuleNode;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
public class GoslinFragmentsVisitorImpl extends GoslinFragmentsBaseVisitor<LipidAdduct> {

    @Override
    public LipidAdduct visitLipid(GoslinFragmentsParser.LipidContext ctx) {
        GoslinFragmentsParser.Lipid_eofContext lipid = ctx.lipid_eof();
        Optional<GoslinFragmentsParser.Fragment_nameContext> fragmentContext = Optional.ofNullable(lipid.fragment_name());
        Optional<GoslinFragmentsParser.Lipid_pureContext> lipidContext = Optional.ofNullable(lipid.just_lipid().lipid_pure());
        Optional<GoslinFragmentsParser.Adduct_infoContext> adductTermContext = Optional.ofNullable(lipid.just_lipid().adduct_info());
        LipidAdduct la = new LipidAdduct(lipidContext.map((cc) -> {
            return new GoslinFragmentsVisitorImpl.LipidVisitor().visitLipid_pure(cc);
        }).orElse(LipidSpecies.NONE), adductTermContext.map((t) -> {
            return new GoslinFragmentsVisitorImpl.AdductVisitor().visitAdduct_info(t);
        }).orElse(Adduct.NONE), "", fragmentContext.map((t) -> {
            return new Fragment(t.frag_char().getText());
        }).orElse(Fragment.NONE));
        return la;
    }

    private static class LipidVisitor extends GoslinFragmentsBaseVisitor<LipidSpecies> {

        @Override
        public LipidSpecies visitLipid_pure(GoslinFragmentsParser.Lipid_pureContext ctx) {
            LipidSpecies lipid = null;
            BitSet bs = new BitSet(5);
            bs.set(LipidCategory.ST.ordinal(), ctx.cholesterol() != null);
            bs.set(LipidCategory.GL.ordinal(), ctx.gl() != null);
            bs.set(LipidCategory.FA.ordinal(), ctx.mediatorc() != null);
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
                        LipidSpeciesInfo lsi = new LipidSpeciesInfo(LipidLevel.SPECIES, 0, 0, 0, LipidFaBondType.UNDEFINED);
                        lipid = new LipidSpecies(ctx.cholesterol().chc().ch().getText(), LipidCategory.ST, Optional.of(LipidClass.CH), Optional.of(lsi));
                        break;
                    } else if (ctx.cholesterol().che() != null) {
                        lipid = handleChe(ctx.cholesterol().che()).orElse(LipidSpecies.NONE);
                        break;
                    } else {
                        throw new ParseTreeVisitorException("Unhandled sterol lipid: " + ctx.cholesterol().getText());
                    }
                case GL:
                    lipid = handleGlycerolipid(ctx).orElse(LipidSpecies.NONE);
                    break;
                case FA:
                    //FIXME convert to FA handler
                    lipid = new LipidSpecies(ctx.mediatorc().getText(), LipidCategory.FA,
                            LipidClass.forHeadGroup(ctx.mediatorc().getText()),
                            Optional.empty()
                    );
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

        private Optional<LipidSpecies> handleGlycerolipid(GoslinFragmentsParser.Lipid_pureContext ctx) throws RuntimeException {
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

        private Optional<LipidSpecies> handleDsl(GoslinFragmentsParser.DslContext dsl) {
            String headGroup = dsl.hg_dslc().getText();
            if (dsl.sl_species() != null) { //species level
                //process species level
                return visitSpeciesLcb(headGroup, dsl.sl_species().lcb());
            } else if (dsl.sl_subspecies() != null) {
                //process subspecies
                if (dsl.sl_subspecies().sorted_fa_separator() != null) {
                    //sorted => StructuralSubspecies
                    return visitStructuralSubspeciesLcb(headGroup, dsl.sl_subspecies().lcb(), Arrays.asList(dsl.sl_subspecies().fa()));
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in DSL!");
            }
            return Optional.empty();
        }

        private Optional<LipidSpecies> handleLsl(GoslinFragmentsParser.LslContext lsl) {
            String headGroup = lsl.hg_lslc().getText();
            if (lsl.lcb() != null) { //species / subspecies level
                //process structural sub species level
                return visitStructuralSubspeciesLcb(headGroup, lsl.lcb());
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in LSL!");
            }
        }

        private static boolean hasElements(List<?> l) {
            return (l == null ? false : !l.isEmpty());
        }

        private Optional<LipidSpecies> handleTgl(GoslinFragmentsParser.TglContext tgl) {
            String headGroup = tgl.hg_tgl_full().getText();
            if (tgl.gl_species() != null) { //species level
                //process species level
                return visitSpeciesFas(headGroup, tgl.gl_species().fa());
            } else if (tgl.tgl_subspecies() != null) {
                //process subspecies
                if (tgl.tgl_subspecies().fa3().fa3_sorted() != null) {
                    //sorted => StructuralSubspecies
                    log.info("Building structural subspecies");
                    return visitStructuralSubspeciesFas(headGroup, tgl.tgl_subspecies().fa3().fa3_sorted().fa());
                } else if (tgl.tgl_subspecies().fa3().fa3_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    log.info("Building molecular subspecies");
                    return visitMolecularSubspeciesFas(headGroup, tgl.tgl_subspecies().fa3().fa3_unsorted().fa());
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in TGL!");
            }
            return Optional.empty();
        }

        private Optional<LipidSpecies> handleSgl(GoslinFragmentsParser.SglContext sgl) {
            String headGroup = sgl.hg_sgl_full().getText();
            if (sgl.gl_species() != null) { //species level
                //process species level
                return visitSpeciesFas(headGroup, sgl.gl_species().fa());
            } else if (sgl.dgl_subspecies() != null) {
                //process subspecies
                if (sgl.dgl_subspecies().fa2().fa2_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return visitStructuralSubspeciesFas(headGroup, sgl.dgl_subspecies().fa2().fa2_sorted().fa());
                } else if (sgl.dgl_subspecies().fa2().fa2_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    return visitMolecularSubspeciesFas(headGroup, sgl.dgl_subspecies().fa2().fa2_unsorted().fa());
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in SGL!");
            }
            return Optional.empty();
        }

        private Optional<LipidSpecies> handleChe(GoslinFragmentsParser.CheContext che) {
            String headGroup = che.hg_chec().getText();
            if (che.fa() != null) {
                return visitStructuralSubspeciesFas(headGroup, Arrays.asList(che.fa()));
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in ChE!");
            }
        }

        private Optional<LipidSpecies> handleMgl(GoslinFragmentsParser.MglContext mgl) {
            String headGroup = mgl.hg_mgl_full().getText();
            if (mgl.fa() != null) {
                return visitStructuralSubspeciesFas(headGroup, Arrays.asList(mgl.fa()));
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in MGL!");
            }
        }

        private Optional<LipidSpecies> handleDgl(GoslinFragmentsParser.DglContext dgl) {
            String headGroup = dgl.hg_dgl_full().getText();
            if (dgl.gl_species() != null) { //species level
                //process species level
                return visitSpeciesFas(headGroup, dgl.gl_species().fa());
            } else if (dgl.dgl_subspecies() != null) {
                //process subspecies
                if (dgl.dgl_subspecies().fa2().fa2_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return visitStructuralSubspeciesFas(headGroup, dgl.dgl_subspecies().fa2().fa2_sorted().fa());
                } else if (dgl.dgl_subspecies().fa2().fa2_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    return visitMolecularSubspeciesFas(headGroup, dgl.dgl_subspecies().fa2().fa2_unsorted().fa());
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in DGL!");
            }
            return Optional.empty();
        }

        private Optional<LipidSpecies> handleGlyceroPhospholipid(GoslinFragmentsParser.Lipid_pureContext ctx) throws RuntimeException {
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
                    return visitSpeciesFas(headGroup, ploc.dpl_o().pl_species().fa());
                } else if (ploc.dpl_o().pl_subspecies() != null) {
                    //process subspecies
                    if (ploc.dpl_o().pl_subspecies().fa2().fa2_sorted() != null) {
                        //sorted => StructuralSubspecies
                        return visitStructuralSubspeciesFas(headGroup, ploc.dpl_o().pl_subspecies().fa2().fa2_sorted().fa());
                    } else if (ploc.dpl_o().pl_subspecies().fa2().fa2_unsorted() != null) {
                        //unsorted => MolecularSubspecies
                        return visitMolecularSubspeciesFas(headGroup, ploc.dpl_o().pl_subspecies().fa2().fa2_unsorted().fa());
                    }
                }
            } else if (ploc.lpl_o() != null) {
                String headGroup = ploc.lpl_o().hg_lpl_oc().getText();
                return visitStructuralSubspeciesFas(headGroup, Arrays.asList(ploc.lpl_o().fa()));
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in PL O!");
            }
            return Optional.empty();
        }

        private Optional<LipidSpecies> handleCl(GoslinFragmentsParser.ClContext cl) {
            String headGroup = cl.hg_clc().getText();
            if (cl.pl_species() != null) { //species level
                //process species level
                return visitSpeciesFas(headGroup, cl.pl_species().fa());
            } else if (cl.cl_subspecies() != null) {
                //process subspecies
                if (cl.cl_subspecies().fa4().fa4_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return visitStructuralSubspeciesFas(headGroup, cl.cl_subspecies().fa4().fa4_sorted().fa());
                } else if (cl.cl_subspecies().fa4().fa4_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    return visitMolecularSubspeciesFas(headGroup, cl.cl_subspecies().fa4().fa4_unsorted().fa());
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
                return visitSpeciesFas(headGroup, mlcl.pl_species().fa());
            } else if (mlcl.mlcl_subspecies() != null) {
                //process subspecies
                if (mlcl.mlcl_subspecies().fa3().fa3_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return visitStructuralSubspeciesFas(headGroup, mlcl.mlcl_subspecies().fa3().fa3_sorted().fa());
                } else if (mlcl.mlcl_subspecies().fa3().fa3_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    return visitMolecularSubspeciesFas(headGroup, mlcl.mlcl_subspecies().fa3().fa3_unsorted().fa());
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
                return visitSpeciesFas(headGroup, dpl.pl_species().fa());
            } else if (dpl.pl_subspecies() != null) {
                //process subspecies
                if (dpl.pl_subspecies().fa2().fa2_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return visitStructuralSubspeciesFas(headGroup, dpl.pl_subspecies().fa2().fa2_sorted().fa());
                } else if (dpl.pl_subspecies().fa2().fa2_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    return visitMolecularSubspeciesFas(headGroup, dpl.pl_subspecies().fa2().fa2_unsorted().fa());
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
                return visitStructuralSubspeciesFas(headGroup, Arrays.asList(lpl.fa()));
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in PL!");
            }
        }

        private Optional<LipidSpecies> visitSpeciesLcb(String headGroup, GoslinFragmentsParser.LcbContext lcbContext) {
            return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(lcbContext)));
        }

        private Optional<LipidSpecies> visitSpeciesFas(String headGroup, GoslinFragmentsParser.FaContext faContext) {
            return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(faContext)));
        }

        private Optional<LipidSpecies> visitMolecularSubspeciesFas(String headGroup, List<GoslinFragmentsParser.FaContext> faContexts) {
            List<MolecularFattyAcid> fas = new LinkedList<>();
            for (int i = 0; i < faContexts.size(); i++) {
                MolecularFattyAcid fa = buildMolecularFa(faContexts.get(i), "FA" + (i + 1));
                fas.add(fa);
            }
            MolecularFattyAcid[] arrs = new MolecularFattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidMolecularSubspecies(headGroup, arrs));
        }

        private Optional<LipidSpecies> visitStructuralSubspeciesLcb(String headGroup, GoslinFragmentsParser.LcbContext lcbContext, List<GoslinFragmentsParser.FaContext> faContexts) {
            List<StructuralFattyAcid> fas = new LinkedList<>();
            StructuralFattyAcid lcbA = buildStructuralLcb(lcbContext, "LCB", 1);
            fas.add(lcbA);
            for (int i = 0; i < faContexts.size(); i++) {
                StructuralFattyAcid fa = buildStructuralFa(faContexts.get(i), "FA" + (i + 1), i + 2);
                fas.add(fa);
            }
            StructuralFattyAcid[] arrs = new StructuralFattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));
        }

        private Optional<LipidSpecies> visitStructuralSubspeciesFas(String headGroup, List<GoslinFragmentsParser.FaContext> faContexts) {
            List<StructuralFattyAcid> fas = new LinkedList<>();
            for (int i = 0; i < faContexts.size(); i++) {
                StructuralFattyAcid fa = buildStructuralFa(faContexts.get(i), "FA" + (i + 1), i + 1);
                fas.add(fa);
            }
            StructuralFattyAcid[] arrs = new StructuralFattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));
        }

        private Optional<LipidSpecies> visitStructuralSubspeciesLcb(String headGroup, GoslinFragmentsParser.LcbContext lcbContext) {
            StructuralFattyAcid fa = buildStructuralLcb(lcbContext, "LCB", 1);
            return Optional.of(new LipidStructuralSubspecies(headGroup, fa));
        }

        private Optional<LipidSpeciesInfo> getSpeciesInfo(GoslinFragmentsParser.FaContext faContext) {
            //fa_pure, ether, heavy
            if (faContext.fa_pure() != null && faContext.heavy_fa() != null) {
                throw new RuntimeException("Heavy label in FA_pure context not implemented yet!");
            }
            LipidFaBondType lfbt = getLipidFaBondType(faContext);
            return Optional.of(new LipidSpeciesInfo(
                    LipidLevel.SPECIES,
                    asInt(faContext.fa_pure().carbon(), 0),
                    asInt(faContext.fa_pure().hydroxyl(), 0),
                    asInt(faContext.fa_pure().db(), 0),
                    lfbt));
        }

        private Optional<LipidSpeciesInfo> getSpeciesInfo(GoslinFragmentsParser.LcbContext lcbContext) {
            Integer hydroxyl = 0;
            if (lcbContext.lcb_pure() != null && lcbContext.heavy_lcb() != null) {
                throw new RuntimeException("Heavy label in lcb_pure context not implemented yet!");
            }
            if (lcbContext.lcb_pure() != null) {
                GoslinFragmentsParser.Lcb_pureContext pureCtx = lcbContext.lcb_pure();
                if (pureCtx.old_hydroxyl() != null) {
                    switch (pureCtx.old_hydroxyl().getText()) {
                        case "t":
                            hydroxyl = 3;
                            break;
                        case "d":
                            hydroxyl = 2;
                            break;
                        default:
                            throw new ParseTreeVisitorException("Unsupported old hydroxyl prefix: " + pureCtx.old_hydroxyl().getText());
                    }
                } else if (pureCtx.hydroxyl() != null) {
                    hydroxyl = asInt(pureCtx.hydroxyl(), 0);
                }
                return Optional.of(new LipidSpeciesInfo(
                        LipidLevel.SPECIES,
                        asInt(pureCtx.carbon(), 0),
                        hydroxyl,
                        asInt(pureCtx.db(), 0), LipidFaBondType.ESTER));
            }
            throw new ParseTreeVisitorException("Uninitialized lcb_pure context!");
        }
    }

    private static class AdductVisitor extends GoslinFragmentsBaseVisitor<Adduct> {

        @Override
        public Adduct visitAdduct_info(GoslinFragmentsParser.Adduct_infoContext ctx) {
            String chargeSign = ctx.charge_sign().getText();
            Integer chargeSignValue = 0;
            switch (chargeSign) {
                case "+":
                    chargeSignValue = 1;
                    break;
                case "-":
                    chargeSignValue = -1;
                    break;
                default:
                    chargeSignValue = 0;
            }
            String adductText = ctx.adduct().getText();
            Adduct adduct = new Adduct("", adductText, Integer.parseInt(ctx.charge().getText()), chargeSignValue);
            return adduct;
        }
    }

    private static LipidFaBondType getLipidFaBondType(GoslinFragmentsParser.FaContext faContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        if (faContext.ether() != null) {
            if ("a".equals(faContext.ether().getText())) {
                lfbt = LipidFaBondType.ETHER_PLASMANYL;
            } else if ("p".equals(faContext.ether().getText())) {
                lfbt = LipidFaBondType.ETHER_PLASMENYL;
            } else {
                throw new ParseTreeVisitorException("Unknown ether context value: " + faContext.ether());
            }
        }
        return lfbt;
    }

    public static MolecularFattyAcid buildMolecularFa(GoslinFragmentsParser.FaContext ctx, String faName) {
        MolecularFattyAcid.MolecularFattyAcidBuilder fa = MolecularFattyAcid.molecularFattyAcidBuilder();
        LipidFaBondType lfbt = getLipidFaBondType(ctx);
        if (ctx.fa_pure() != null) {
            fa.nCarbon(asInt(ctx.fa_pure().carbon(), 0));
            fa.nHydroxy(asInt(ctx.fa_pure().hydroxyl(), 0));
            if (ctx.fa_pure().db() != null) {
                fa.nDoubleBonds(asInt(ctx.fa_pure().db().db_count(), 0));
                if (ctx.fa_pure().db().db_positions() != null) {
                    throw new RuntimeException("Support for double bond positions not implemented yet!");
                }
            }
            fa.lipidFaBondType(lfbt);
            return fa.name(faName).build();

        } else {
            throw new ParseTreeVisitorException("Uninitialized FaContext!");
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

    public static StructuralFattyAcid buildStructuralLcb(GoslinFragmentsParser.LcbContext ctx, String faName, int position) {
        if (ctx.lcb_pure() != null && ctx.heavy_lcb() != null) {
            throw new RuntimeException("Heavy label in lcb_pure context not implemented yet!");
        }
        GoslinFragmentsParser.Lcb_pureContext pureCtx = ctx.lcb_pure();
        StructuralFattyAcid.StructuralFattyAcidBuilder fa = StructuralFattyAcid.structuralFattyAcidBuilder();
        fa.nCarbon(asInt(pureCtx.carbon(), 0));
        fa.nHydroxy(asInt(pureCtx.hydroxyl(), 0));
        if (pureCtx.db() != null) {
            fa.nDoubleBonds(asInt(pureCtx.db().db_count(), 0));
            if (pureCtx.db().db_positions() != null) {
                throw new RuntimeException("Support for double bond positions not implemented yet!");
            }
        }
        fa.lipidFaBondType(LipidFaBondType.ESTER);
        return fa.name(faName).position(position).lcb(true).build();
    }

    public static StructuralFattyAcid buildStructuralFa(GoslinFragmentsParser.FaContext ctx, String faName, int position) {
        StructuralFattyAcid.StructuralFattyAcidBuilder fa = StructuralFattyAcid.structuralFattyAcidBuilder();
        LipidFaBondType lfbt = getLipidFaBondType(ctx);
        if (ctx.fa_pure() != null) {
            fa.nCarbon(asInt(ctx.fa_pure().carbon(), 0));
            fa.nHydroxy(asInt(ctx.fa_pure().hydroxyl(), 0));
            if (ctx.fa_pure().db() != null) {
                fa.nDoubleBonds(asInt(ctx.fa_pure().db().db_count(), 0));
                if (ctx.fa_pure().db().db_positions() != null) {
                    throw new RuntimeException("Support for double bond positions not implemented yet!");
                }
            }
            fa.lipidFaBondType(lfbt);
            return fa.name(faName).position(position).build();

        } else {
            throw new ParseTreeVisitorException("Uninitialized FaContext!");
        }
    }

}

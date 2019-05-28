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
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.domain.Adduct;
import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidCategory;
import static de.isas.lipidomics.domain.LipidCategory.GL;
import static de.isas.lipidomics.domain.LipidCategory.ST;
import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.domain.MolecularFattyAcid;
import de.isas.lipidomics.domain.MolecularFattyAcid.MolecularFattyAcidBuilder;
import de.isas.lipidomics.domain.StructuralFattyAcid;
import de.isas.lipidomics.domain.StructuralFattyAcid.StructuralFattyAcidBuilder;
import de.isas.lipidomics.palinom.LipidMapsParser.Fa2Context;
import de.isas.lipidomics.palinom.LipidMapsParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.exceptions.PalinomVisitorException;
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
class LipidMapsVisitorImpl extends LipidMapsBaseVisitor<LipidAdduct> {

    /**
     *
     * @throws PalinomVisitorException for structural or state-related issues
     * while trying to process a parsing context.
     * @throws RuntimeException
     * @param ctx
     * @return
     */
    @Override
    public LipidAdduct visitLipid(LipidMapsParser.LipidContext ctx) {
        LipidMapsParser.Lipid_ruleContext lipid = ctx.lipid_rule();
        if (lipid.isotope() != null) {
            throw new PalinomVisitorException("Support for isotopes in LipidMaps names not implemented yet!");
        }
        if (lipid.lipid_mono() != null && lipid.lipid_mono().isoform() != null) {
            throw new PalinomVisitorException("Support for isoforms in LipidMaps names not implemented yet!");
        }
        Optional<Lipid_pureContext> categoryContext = Optional.ofNullable(lipid.lipid_mono().lipid_pure());

        LipidAdduct la = new LipidAdduct(categoryContext.map((cc) -> {
            return new LipidVisitor().visitLipid_pure(cc);
        }).orElse(LipidSpecies.NONE), Adduct.NONE);
        return la;
    }

    private static class LipidVisitor extends LipidMapsBaseVisitor<LipidSpecies> {

        @Override
        public LipidSpecies visitLipid_pure(LipidMapsParser.Lipid_pureContext ctx) {
            LipidSpecies lipid = null;
            BitSet bs = new BitSet(5);
            bs.set(LipidCategory.ST.ordinal(), ctx.cholesterol() != null);
            bs.set(LipidCategory.GL.ordinal(), ctx.gl() != null);
            bs.set(LipidCategory.FA.ordinal(), ctx.mediator() != null);
            bs.set(LipidCategory.GP.ordinal(), ctx.pl() != null);
            bs.set(LipidCategory.SP.ordinal(), ctx.sl() != null);
            LipidCategory contextCategory = LipidCategory.UNDEFINED;
            switch (bs.cardinality()) {
                case 0:
                    throw new PalinomVisitorException("Parsing context did not contain content for any lipid category. Must contain exactly one of " + Arrays.toString(LipidCategory.values()));
                case 1:
                    contextCategory = LipidCategory.values()[bs.nextSetBit(0)];
                    break;
                default:
                    throw new PalinomVisitorException("Parsing context contained content for more than one lipid category. Must contain exactly one of " + Arrays.toString(LipidCategory.values()));
            }
            switch (contextCategory) {
                case ST:
                    if (ctx.cholesterol().chc() != null) {
                        lipid = new LipidSpecies(ctx.cholesterol().chc().ch().getText());
                        break;
                    } else if (ctx.cholesterol().chec() != null) {
                        lipid = handleChe(ctx.cholesterol().chec()).orElse(LipidSpecies.NONE);
                        break;
                    } else {
                        throw new PalinomVisitorException("Unhandled sterol lipid: " + ctx.cholesterol().getText());
                    }
                case GL:
                    lipid = handleGlycerolipid(ctx).orElse(LipidSpecies.NONE);
                    break;
                case FA:
                    lipid = new LipidSpecies(ctx.mediator().getText(), LipidCategory.FA, Optional.empty(), Optional.empty());
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
                    throw new PalinomVisitorException("Unhandled contextCategory: " + contextCategory);
            }
            return lipid;
        }

        private Optional<LipidSpecies> handleGlycerolipid(Lipid_pureContext ctx) throws RuntimeException {
            if (ctx.gl().sgl() != null) {
                return handleSgl(ctx.gl().sgl());
            } else if (ctx.gl().tgl() != null) {
                return handleTgl(ctx.gl().tgl());
            } else {
                throw new PalinomVisitorException("Unhandled context state in GL!");
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
                throw new PalinomVisitorException("Unhandled context state in DSL!");
            }
            return Optional.empty();
        }

        private Optional<LipidSpecies> handleLsl(LipidMapsParser.LslContext lsl) {
            String headGroup = lsl.hg_lslc().getText();
            if (lsl.lcb() != null) { //species / subspecies level
                //process structural sub species level
                return visitStructuralSubspeciesLcb(headGroup, lsl.lcb());
            } else {
                throw new PalinomVisitorException("Unhandled context state in LSL!");
            }
        }

        private Optional<LipidSpecies> handleTgl(LipidMapsParser.TglContext tgl) {
            String headGroup = tgl.hg_glc().getText();
            if (tgl.tgl_subspecies() != null) {
                //process subspecies
                if (tgl.tgl_subspecies().fa3().fa3_sorted() != null) {
                    //sorted => StructuralSubspecies
                    return visitStructuralSubspeciesFas(headGroup, tgl.tgl_subspecies().fa3().fa3_sorted().fa());
                } else if (tgl.tgl_subspecies().fa3().fa3_unsorted() != null) {
                    //unsorted => MolecularSubspecies
                    return visitMolecularSubspeciesFas(headGroup, tgl.tgl_subspecies().fa3().fa3_unsorted().fa());
                }
            } else {
                throw new PalinomVisitorException("Unhandled context state in SGL!");
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
                throw new PalinomVisitorException("Unhandled context state in SGL!");
            }
            return Optional.empty();
        }

        private Optional<LipidSpecies> handleChe(LipidMapsParser.ChecContext che) {
            String headGroup = che.che().hg_che().getText();
            if (che.che_fa().fa() != null) {
                return visitStructuralSubspeciesFas(headGroup, Arrays.asList(che.che_fa().fa()));
            } else {
                throw new PalinomVisitorException("Unhandled context state in ChE!");
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
                throw new PalinomVisitorException("Unhandled context state in PL!");
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
                    throw new PalinomVisitorException("CL had not fatty acids defined!");
                }
            } else {
                throw new PalinomVisitorException("Unhandled context state in CL!");
            }
        }

        private Optional<LipidSpecies> handleDpl(LipidMapsParser.DplContext dpl) {
            String headGroup = dpl.hg_pl().getText();
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
                throw new PalinomVisitorException("Unhandled context state in PL!");
            }
            return Optional.empty();
        }

        private Optional<LipidSpecies> handleLpl(LipidMapsParser.LplContext lpl) {
            String headGroup = lpl.hg_lplc().getText();
            //lyso PL has one FA, Species=MolecularSubSpecies=StructuralSubSpecies
            if (lpl.fa() != null) {
                return visitStructuralSubspeciesFas(headGroup, Arrays.asList(lpl.fa()));
            } else {
                throw new PalinomVisitorException("Unhandled context state in PL!");
            }
        }

        private Optional<LipidSpecies> visitSpeciesLcb(String headGroup, LipidMapsParser.LcbContext lcbContext) {
            return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(lcbContext)));
        }

        private Optional<LipidSpecies> visitSpeciesFas(String headGroup, LipidMapsParser.FaContext faContext) {
            return Optional.of(new LipidSpecies(headGroup, getSpeciesInfo(faContext)));
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
            for (int i = 0; i < faContexts.size(); i++) {
                StructuralFattyAcid fa = buildStructuralFa(faContexts.get(i), "FA" + (i + 2), i + 2);
                fas.add(fa);
            }
            StructuralFattyAcid[] arrs = new StructuralFattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));

        }

        private Optional<LipidSpecies> visitStructuralSubspeciesFas(String headGroup, List<LipidMapsParser.FaContext> faContexts) {
            List<StructuralFattyAcid> fas = new LinkedList<>();
            for (int i = 0; i < faContexts.size(); i++) {
                StructuralFattyAcid fa = buildStructuralFa(faContexts.get(i), "FA" + (i + 1), i + 1);
                fas.add(fa);
            }
            StructuralFattyAcid[] arrs = new StructuralFattyAcid[fas.size()];
            fas.toArray(arrs);
            return Optional.of(new LipidStructuralSubspecies(headGroup, arrs));
        }

        private Optional<LipidSpecies> visitSubspeciesFas2(String headGroup, List<LipidMapsParser.Fa2Context> fa2Contexts) {
            List<FattyAcid> fas = new LinkedList<>();
            LipidLevel level = LipidLevel.UNDEFINED;
            for (int i = 0; i < fa2Contexts.size(); i++) {
                Fa2Context fa2Ctx = fa2Contexts.get(i);
                if (fa2Ctx.fa2_sorted() != null) {
                    if (level == LipidLevel.MOLECULAR_SUBSPECIES) {
                        throw new PalinomVisitorException("CL second FAs group can not be on molecular level, first group was on structural level!");
                    }
                    level = LipidLevel.STRUCTURAL_SUBSPECIES;
                    for (int j = 0; j < fa2Ctx.fa2_sorted().fa().size(); j++) {
                        StructuralFattyAcid fa = buildStructuralFa(fa2Ctx.fa2_sorted().fa().get(j), "FA" + ((i + 1) + j), i + 1);
                        fas.add(fa);
                    }
                } else if (fa2Ctx.fa2_unsorted() != null) {
                    if (level == LipidLevel.STRUCTURAL_SUBSPECIES) {
                        throw new PalinomVisitorException("CL second FAs group can not be on molecular level, first group was on structural level!");
                    }
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
                    return Optional.of(new LipidMolecularSubspecies(headGroup, sarrs));
                default:
                    throw new PalinomVisitorException("Unhandled lipid level for CL: " + level);
            }
        }

        private Optional<LipidSpecies> visitStructuralSubspeciesLcb(String headGroup, LipidMapsParser.LcbContext lcbContext) {
            StructuralFattyAcid fa = buildStructuralLcb(lcbContext, "FA" + 1, 1);
            return Optional.of(new LipidStructuralSubspecies(headGroup, fa));
        }

        private Optional<LipidSpeciesInfo> getSpeciesInfo(LipidMapsParser.FaContext faContext) {
            if (faContext.fa_unmod() != null) {
                LipidFaBondType faBondType = getLipidFaBondTypeUnmod(faContext);
                int plasmenylEtherDbBondCorrection = 0;
                switch (faBondType) {
                    case ETHER_PLASMENYL:
                        plasmenylEtherDbBondCorrection = 1;
                    default:
                        plasmenylEtherDbBondCorrection = 0;
                }
                return Optional.of(new LipidSpeciesInfo(
                        LipidLevel.SPECIES,
                        asInt(faContext.fa_unmod().fa_pure().carbon(), 0),
                        asInt(faContext.fa_unmod().fa_pure().hydroxyl(), 0),
                        plasmenylEtherDbBondCorrection + asInt(faContext.fa_unmod().fa_pure().db(), 0),
                        faBondType));
            } else if (faContext.fa_mod() != null) {
                throw new RuntimeException("Modified FA handling not implemented yet for " + faContext.getText());
            }
            throw new PalinomVisitorException("Unknown fa context value: " + faContext.getText());
        }

        private Optional<LipidSpeciesInfo> getSpeciesInfo(LipidMapsParser.LcbContext lcbContext) {
            Integer hydroxyl = 0;
            if (lcbContext.hydroxyl_lcb() != null) {
                hydroxyl = asInt(lcbContext.hydroxyl_lcb(), 0);
            }
            return Optional.of(new LipidSpeciesInfo(
                    LipidLevel.SPECIES,
                    asInt(lcbContext.carbon(), 0),
                    hydroxyl,
                    asInt(lcbContext.db(), 0),
                    LipidFaBondType.ESTER));
        }
    }

    public static LipidFaBondType getLipidFaBondTypeUnmod(LipidMapsParser.FaContext faContext) throws PalinomVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        if (faContext.fa_unmod().ether() != null) {
            if (null == faContext.fa_unmod().ether().getText()) {
                throw new PalinomVisitorException("Undefined ether context value!");
            } else {
                switch (faContext.fa_unmod().ether().getText()) {
                    case "O-":
                        lfbt = LipidFaBondType.ETHER_PLASMANYL;
                        break;
                    case "P-":
                        lfbt = LipidFaBondType.ETHER_PLASMENYL;
                        break;
                    default:
                        throw new PalinomVisitorException("Unknown ether context value: " + faContext.fa_unmod().ether());
                }
            }
        }
        return lfbt;
    }

    public static MolecularFattyAcid buildMolecularFa(LipidMapsParser.FaContext ctx, String faName) {
        MolecularFattyAcidBuilder fa = MolecularFattyAcid.molecularFaBuilder();
        if (ctx.fa_unmod() != null) {
            LipidFaBondType faBondType = getLipidFaBondTypeUnmod(ctx);
            int plasmenylEtherDbBondCorrection = 0;
            switch (faBondType) {
                case ETHER_PLASMENYL:
                    plasmenylEtherDbBondCorrection = 1;
                default:
                    plasmenylEtherDbBondCorrection = 0;
            }
            fa.lipidFaBondType(faBondType);
            fa.nCarbon(asInt(ctx.fa_unmod().fa_pure().carbon(), 0));
            fa.nHydroxy(asInt(ctx.fa_unmod().fa_pure().hydroxyl(), 0));
            if (ctx.fa_unmod().fa_pure().db() != null) {
                fa.nDoubleBonds(plasmenylEtherDbBondCorrection + asInt(ctx.fa_unmod().fa_pure().db().db_count(), 0));
                if (ctx.fa_unmod().fa_pure().db().db_position() != null) {
                    throw new RuntimeException("Support for double bond positions not implemented yet!");
                }
            }
            return fa.name(faName).build();

        } else if (ctx.fa_mod() != null) {
            throw new RuntimeException("Support for modified FA handling not implemented!");
        } else {
            throw new PalinomVisitorException("No FaContext!");
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

    public static StructuralFattyAcid buildStructuralLcb(LipidMapsParser.LcbContext ctx, String faName, int position) {
        StructuralFattyAcidBuilder fa = StructuralFattyAcid.structuralFaBuilder();
        fa.nCarbon(asInt(ctx.carbon(), 0));
        fa.nHydroxy(asInt(ctx.hydroxyl_lcb(), 0));
        if (ctx.db() != null) {
            fa.nDoubleBonds(asInt(ctx.db().db_count(), 0));
            if (ctx.db().db_position() != null) {
                throw new RuntimeException("Support for double bond positions not implemented yet!");
            }
        }
        return fa.name(faName).position(position).lcb(true).build();
    }

    public static StructuralFattyAcid buildStructuralFa(LipidMapsParser.FaContext ctx, String faName, int position) {
        StructuralFattyAcidBuilder fa = StructuralFattyAcid.structuralFaBuilder();
        if (ctx.fa_unmod() != null) {
            LipidFaBondType faBondType = getLipidFaBondTypeUnmod(ctx);
            int plasmenylEtherDbBondCorrection = 0;
            switch (faBondType) {
                case ETHER_PLASMENYL:
                    plasmenylEtherDbBondCorrection = 1;
                default:
                    plasmenylEtherDbBondCorrection = 0;
            }
            fa.lipidFaBondType(faBondType);
            fa.nCarbon(asInt(ctx.fa_unmod().fa_pure().carbon(), 0));
            fa.nHydroxy(asInt(ctx.fa_unmod().fa_pure().hydroxyl(), 0));
            if (ctx.fa_unmod().fa_pure().db() != null) {
                fa.nDoubleBonds(plasmenylEtherDbBondCorrection + asInt(ctx.fa_unmod().fa_pure().db().db_count(), 0));
                if (ctx.fa_unmod().fa_pure().db().db_position() != null) {
                    throw new RuntimeException("Support for double bond positions not implemented yet!");
                }
            }
            return fa.name(faName).position(position).build();

        } else if (ctx.fa_mod() != null) {
            throw new RuntimeException("Support for modified FA handling not implemented!");
        } else {
            throw new PalinomVisitorException("No FaContext!");
        }
    }
}

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
import de.isas.lipidomics.domain.Lipid;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidCategory;
import de.isas.lipidomics.palinom.LipidNamesParser.AdductInfoContext;
import de.isas.lipidomics.palinom.LipidNamesParser.Lipid_pureContext;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
class LipidNamesVisitorImpl extends LipidNamesBaseVisitor<LipidAdduct> {

    @Override
    public LipidAdduct visitLipid(LipidNamesParser.LipidContext ctx) {

//        CategoryContext categoryContext = ctx.category().;
        Optional<Lipid_pureContext> categoryContext = Optional.ofNullable(ctx.lipid_pure());
        Optional<AdductInfoContext> adductTermContext = Optional.ofNullable(ctx.adductInfo());

        LipidAdduct la = new LipidAdduct(categoryContext.map((cc) -> {
            return new LipidVisitor().visitLipid_pure(cc);
        }).orElse(Lipid.NONE), adductTermContext.map((t) -> {
            return new AdductVisitor().visitAdductInfo(t);
        }).orElse(Adduct.NONE));
        return la;
    }

    private static class LipidVisitor extends LipidNamesBaseVisitor<Lipid> {

        @Override
        public Lipid visitLipid_pure(LipidNamesParser.Lipid_pureContext ctx) {
            Lipid lipid = null;
            BitSet bs = new BitSet(5);
            bs.set(LipidCategory.ST.ordinal(), ctx.cholesterol() != null);
            bs.set(LipidCategory.GL.ordinal(), ctx.gl() != null);
            bs.set(LipidCategory.FA.ordinal(), ctx.mediatorc() != null);
            bs.set(LipidCategory.GP.ordinal(), ctx.pl() != null);
            bs.set(LipidCategory.SP.ordinal(), ctx.sl() != null);
            LipidCategory contextCategory = LipidCategory.UNDEFINED;
            switch (bs.cardinality()) {
                case 0:
                    throw new IllegalStateException("Parsing context did not contain content for any lipid category. Must contain exactly one of " + Arrays.toString(LipidCategory.values()));
                case 1:
                    contextCategory = LipidCategory.values()[bs.nextSetBit(0)];
                    break;
                default:
                    throw new IllegalStateException("Parsing context contained content for more than one lipid category. Must contain exactly one of " + Arrays.toString(LipidCategory.values()));
            }
            switch (contextCategory) {
                case ST:
                    if (ctx.cholesterol().chc()!= null) {
                        lipid = new Lipid(ctx.cholesterol().chc().ch().getText());
                        break;
                    } else if (ctx.cholesterol().che() != null) {
//                            lipid.setHeadGroup(ctx.cholesterol().che().().getText());
                        throw new RuntimeException("CHe not implemented yet!");
                    } else {
                        throw new RuntimeException("Unhandled sterol lipid: " + ctx.cholesterol().getText());
                    }
                case GL:
//                    ctx.gl();
                    throw new RuntimeException("GL not implemented yet!");
                case FA:
//                    ctx.mediator();
                    lipid = new Lipid(ctx.mediatorc().getText());
                    break;
                case GP:
                    lipid = handleGlyceroPhospholipid(ctx).orElse(Lipid.NONE);
                    break;
                case SP:
                    if (ctx.sl().dsl() != null) {
                        lipid = new Lipid(ctx.sl().dsl().hg_dslc().getText());
                        visitFas(Arrays.asList(ctx.sl().dsl().fa()), lipid);
                    } else if (ctx.sl().lsl() != null) {
                        throw new RuntimeException("LSL handling not implemented yet in SL!");
//                        lipid.setLipidClass("LSL");
//                        lipid.setHeadGroup(ctx.sl().lsl().hg_lsl().getText());
//                        visitFas(Arrays.asList(ctx.sl().lsl().lcb().fa()), lipid);
                    } else {
                        throw new RuntimeException("Unhandled sphingolipid: " + ctx.sl().getText());
                    }
                    break;
                default:
                    log.warn("Unhandled contextCategory: {}", contextCategory);
            }
//            lipid.setLipidCategory(contextCategory.name());
//            lipid.setHeadGroup(headGroup);
//            lipid.setFa(fa);
            return lipid;
        }

        private Optional<Lipid> handleGlyceroPhospholipid(Lipid_pureContext ctx) throws RuntimeException {
            //glycerophospholipids
            //cardiolipin
            if (ctx.pl().cl() != null) {
//                lipid.setLipidClass("CL");
                Lipid lipid = new Lipid(ctx.pl().cl().hg_clc().getText());
                visitFas(ctx.pl().cl().fa(), lipid);
                return Optional.of(lipid);
            } else if (ctx.pl().dpl() != null) {
                Lipid lipid = new Lipid(ctx.pl().dpl().hg_plc().getText());
                visitFas(ctx.pl().dpl().fa(), lipid);
                return Optional.of(lipid);
            } else if (ctx.pl().lpl() != null) {
                Lipid lipid = new Lipid(ctx.pl().lpl().hg_lplc().getText());
                visitFas(Arrays.asList(ctx.pl().lpl().fa()), lipid);
                return Optional.of(lipid);
            } else if (ctx.pl().mlcl() != null) {
                Lipid lipid = new Lipid(ctx.pl().mlcl().hg_mlclc().getText());
                visitFas(ctx.pl().mlcl().fa(), lipid);
                return Optional.of(lipid);
            } else if (ctx.pl().pl_o() != null) {
                throw new RuntimeException("PL_o handling not implemented yet in PL!");
            } else {
                throw new RuntimeException("Unhandled context state in PL!");
            }
        }

        private void visitFas(List<LipidNamesParser.FaContext> faContexts, Lipid lipid) {
            for (int i = 0; i < faContexts.size(); i++) {
                FaVisitor faVisitor = new FaVisitor();
                FattyAcid fa = faVisitor.visit(faContexts.get(i));
                fa.setName("FA" + (i + 1));
                lipid.getFa().put(fa.getName(), fa);
            }
        }
    }

//    private static class PlVisitor extends PaLiNomBaseVisitor<Object> {
//        
//    }
    private static class AdductVisitor extends LipidNamesBaseVisitor<Adduct> {

        @Override
        public Adduct visitAdductInfo(LipidNamesParser.AdductInfoContext ctx) {
            Adduct adduct = new Adduct(ctx.adduct().getText(), ctx.adduct().getText(), Integer.parseInt(ctx.charge().getText()), Integer.parseInt(ctx.charge_sign().getText()));
            return adduct;
        }
    }

    private static class FaVisitor extends LipidNamesBaseVisitor<FattyAcid> {

        @Override
        public FattyAcid visitFa(LipidNamesParser.FaContext ctx) {
            if (ctx.ether() != null) {
                throw new RuntimeException("Not implemented yet!");
            } else if (ctx.fa_pure() != null) {
                //hydroxyl case
                FattyAcid fa = new FattyAcid();
                fa.setNCarbon(Integer.parseInt(ctx.fa_pure().carbon().getText()));

                if (ctx.fa_pure().db() != null) {
                    //double bonds without positions
                    if (ctx.fa_pure().db().db_count() != null) {
                        fa.addDoubleBonds(Integer.parseInt(ctx.fa_pure().db().db_count().getText()));
                    } else if (ctx.fa_pure().db().db_position() != null) {
                        throw new RuntimeException("Support for double bond positions not implemented yet!");
                    } else {
                        throw new RuntimeException("Unsupported double bond specification!");
                    }
                }
                //base case
                if (ctx.fa_pure().hydroxyl() != null) {
                    fa.setNHydroxy(Integer.parseInt(ctx.fa_pure().hydroxyl().getText()));
                }
                return fa;

            } else {
                throw new RuntimeException("Uninitialized FaContext!");
            }
        }

    }

//    private static class MediatorVisitor extends PaLiNomBaseVisitor<Object> 
}

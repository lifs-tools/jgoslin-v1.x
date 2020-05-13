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
package de.isas.lipidomics.palinom.goslinfragments;

import de.isas.lipidomics.domain.Adduct;
import de.isas.lipidomics.domain.Fragment;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidCategory;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.GoslinFragmentsBaseVisitor;
import de.isas.lipidomics.palinom.GoslinFragmentsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Base visitor implementation for the GoslinFragments grammar.
 *
 * Overriding implementation of {@link GoslinFragmentsBaseVisitor}. Creates
 * {@link LipidAdduct} instances from the provided context.
 *
 * @author nils.hoffmann
 */
@Slf4j
public class GoslinFragmentsVisitorImpl extends GoslinFragmentsBaseVisitor<LipidAdduct> {

    /**
     * Produces a LipidAdduct given the LipidContext.
     *
     * @throws ParseTreeVisitorException for structural or state-related issues
     * while trying to process a parsing context.
     * @throws RuntimeException
     * @param ctx
     * @return a LipidAdduct.
     */
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
        }).orElse(Adduct.NONE), fragmentContext.map((t) -> {
            return new Fragment(t.frag_char().getText());
        }).orElse(Fragment.NONE));
        return la;
    }

    private static class LipidVisitor extends GoslinFragmentsBaseVisitor<LipidSpecies> {

        @Override
        public LipidSpecies visitLipid_pure(GoslinFragmentsParser.Lipid_pureContext ctx) {
            LipidSpecies lipid = null;
            BitSet bs = new BitSet(5);
            bs.set(LipidCategory.ST.ordinal(), ctx.sterol() != null);
            bs.set(LipidCategory.GL.ordinal(), ctx.gl() != null);
            bs.set(LipidCategory.FA.ordinal(), ctx.mediatorc() != null);
            bs.set(LipidCategory.GP.ordinal(), ctx.pl() != null);
            bs.set(LipidCategory.SP.ordinal(), ctx.sl() != null);
            final FattyAcylHelper faHelper = new FattyAcylHelper();
            final MolecularSubspeciesFasHandler msfh = new MolecularSubspeciesFasHandler(faHelper);
            final IsomericSubspeciesFasHandler isfh = new IsomericSubspeciesFasHandler(faHelper);
            final StructuralSubspeciesFasHandler ssfh = new StructuralSubspeciesFasHandler(isfh, faHelper);
            final IsomericSubspeciesLcbHandler islh = new IsomericSubspeciesLcbHandler(isfh, faHelper);
            final StructuralSubspeciesLcbHandler sslh = new StructuralSubspeciesLcbHandler(ssfh, islh);
            final FattyAcylHandler faHandler = new FattyAcylHandler();
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
                    lipid = new SterolLipidHandler(
                            ssfh
                    ).handle(ctx);
                    break;
                case GL:
                    lipid = new GlyceroLipidHandler(
                            msfh,
                            ssfh,
                            faHandler
                    ).handle(ctx);
                    break;
                case FA:
                    lipid = new FattyAcylHandler().handle(ctx);
                    break;
                case GP:
                    lipid = new GlycerophosphoLipidHandler(
                            msfh,
                            ssfh,
                            faHandler
                    ).handle(ctx);
                    break;
                case SP:
                    lipid = new SphingoLipidHandler(
                            sslh,
                            faHandler
                    ).handle(ctx);
                    break;
                default:
                    throw new ParseTreeVisitorException("Unhandled contextCategory: " + contextCategory);
            }
            return lipid;
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
}

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
package de.isas.lipidomics.palinom.hmdb;

import de.isas.lipidomics.palinom.swisslipids.*;
import de.isas.lipidomics.domain.Adduct;
import de.isas.lipidomics.domain.Fragment;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidCategory;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.HMDBBaseVisitor;
import de.isas.lipidomics.palinom.SwissLipidsBaseVisitor;
import de.isas.lipidomics.palinom.HMDBParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Optional;

/**
 * Base visitor implementation for the GoslinFragments grammar.
 * 
 * Overriding implementation of {@link HMDBBaseVisitor}. Creates
 * {@link LipidAdduct} instances from the provided context.
 *
 * @see HmdbVisitorParser
 * @author nils.hoffmann
 */
public class HmdbVisitorImpl extends HMDBBaseVisitor<LipidAdduct> {

    /**
     * Produces a LipidAdduct given the LipidContext.
     * @throws ParseTreeVisitorException for structural or state-related issues
     * while trying to process a parsing context.
     * @throws RuntimeException
     * @param ctx
     * @return a LipidAdduct.
     */
    @Override
    public LipidAdduct visitLipid(HMDBParser.LipidContext ctx) {
        Optional<HMDBParser.Lipid_pureContext> categoryContext = Optional.ofNullable(ctx.lipid_pure());

        LipidAdduct la = new LipidAdduct(categoryContext.map((cc) -> {
            return new LipidVisitor().visitLipid_pure(cc);
        }).orElse(LipidSpecies.NONE), Adduct.NONE, "", new Fragment(""));
        return la;
    }

    private static class LipidVisitor extends HMDBBaseVisitor<LipidSpecies> {

        @Override
        public LipidSpecies visitLipid_pure(HMDBParser.Lipid_pureContext ctx) {
            LipidSpecies lipid = null;
            BitSet bs = new BitSet(5);
            bs.set(LipidCategory.ST.ordinal(), ctx.lipid_class().st() != null);
            bs.set(LipidCategory.GL.ordinal(), ctx.lipid_class().gl() != null);
            bs.set(LipidCategory.FA.ordinal(), ctx.lipid_class().fatty_acid() != null);
            bs.set(LipidCategory.GP.ordinal(), ctx.lipid_class().pl() != null);
            bs.set(LipidCategory.SP.ordinal(), ctx.lipid_class().sl() != null);
            final FattyAcylHelper faHelper = new FattyAcylHelper();
            final MolecularSubspeciesFasHandler msfh = new MolecularSubspeciesFasHandler(faHelper);
            final IsomericSubspeciesFasHandler isfh = new IsomericSubspeciesFasHandler(faHelper);
            final StructuralSubspeciesFasHandler ssfh = new StructuralSubspeciesFasHandler(isfh, faHelper);
            final IsomericSubspeciesLcbHandler islh = new IsomericSubspeciesLcbHandler(isfh, faHelper);
            final StructuralSubspeciesLcbHandler sslh = new StructuralSubspeciesLcbHandler(ssfh, islh, faHelper);
            final FattyAcylHandler faHandler = new FattyAcylHandler();
            String lipidSuffix = "";
            if (ctx.lipid_suffix() != null) {
                lipidSuffix = ctx.lipid_suffix().getText();
                throw new ParseTreeVisitorException("The lipid suffix '" + lipidSuffix + "' is currently unsupported. Please contact the developers at https://lifs.isas.de/support for assistance.");
            }
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
                            msfh,
                            ssfh,
                            isfh,
                            faHandler
                    ).handle(ctx);
                    break;
                case GL:
                    lipid = new GlyceroLipidHandler(
                            msfh,
                            ssfh,
                            isfh,
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
                            isfh,
                            faHandler
                    ).handle(ctx);
                    break;
                case SP:
                    lipid = new SphingoLipidHandler(
                            sslh,
                            islh,
                            faHandler
                    ).handle(ctx);
                    break;
                default:
                    throw new ParseTreeVisitorException("Unhandled contextCategory: " + contextCategory);
            }
            return lipid;
        }
    }
}

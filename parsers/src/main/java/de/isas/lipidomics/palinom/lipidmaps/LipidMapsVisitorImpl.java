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
import de.isas.lipidomics.domain.Fragment;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidCategory;
import static de.isas.lipidomics.domain.LipidCategory.GL;
import static de.isas.lipidomics.domain.LipidCategory.ST;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.LipidMapsBaseVisitor;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.LipidMapsParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Overriding implementation of {@link LipidMapsBaseVisitor}. Creates
 * {@link LipidAdduct} instances from the provided context.
 *
 * @see LipidMapsVisitorParser
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
            log.warn("Support for isotopes in LipidMaps names not implemented yet!");
        }
        if (lipid.lipid_mono() != null && lipid.lipid_mono().isoform() != null) {
            log.warn("Support for isoforms in LipidMaps names not implemented yet!");
        }
        Optional<Lipid_pureContext> categoryContext = Optional.ofNullable(lipid.lipid_mono().lipid_pure());

        LipidAdduct la = new LipidAdduct(categoryContext.map((cc) -> {
            return new LipidVisitor().visitLipid_pure(cc);
        }).orElse(LipidSpecies.NONE), Adduct.NONE, "", new Fragment(""));
        return la;
    }

    private static class LipidVisitor extends LipidMapsBaseVisitor<LipidSpecies> {

        @Override
        public LipidSpecies visitLipid_pure(LipidMapsParser.Lipid_pureContext ctx) {
            LipidSpecies lipid = null;
            BitSet bs = new BitSet(6);
            bs.set(LipidCategory.ST.ordinal(), ctx.sterol() != null);
            bs.set(LipidCategory.GL.ordinal(), ctx.gl() != null);
            bs.set(LipidCategory.FA.ordinal(), ctx.mediator() != null || ctx.pure_fa() != null);
            bs.set(LipidCategory.GP.ordinal(), ctx.pl() != null);
            bs.set(LipidCategory.SP.ordinal(), ctx.sl() != null);
            bs.set(LipidCategory.PK.ordinal(), ctx.pk() != null);
            final FattyAcylHelper faHelper = new FattyAcylHelper();
            final MolecularSubspeciesFasHandler msfh = new MolecularSubspeciesFasHandler(faHelper);
            final IsomericSubspeciesFasHandler isfh = new IsomericSubspeciesFasHandler(faHelper);
            final StructuralSubspeciesFasHandler ssfh = new StructuralSubspeciesFasHandler(isfh, faHelper);
            final IsomericSubspeciesLcbHandler islh = new IsomericSubspeciesLcbHandler(isfh, faHelper);
            final StructuralSubspeciesLcbHandler sslh = new StructuralSubspeciesLcbHandler(ssfh, islh, faHelper);
            final FattyAcylHandler faHandler = new FattyAcylHandler(msfh, ssfh, faHelper);
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
                    lipid = new SterolLipidHandler(ssfh).handle(ctx);
                    break;
                case GL:
                    lipid = new GlyceroLipidHandler(msfh, ssfh, faHandler).handle(ctx);
                    break;
                case FA:
                    lipid = faHandler.handle(ctx);
                    break;
                case GP:
                    lipid = new GlycerophosphoLipidHandler(msfh, ssfh, faHandler).handle(ctx);
                    break;
                case SP:
                    lipid = new SphingoLipidHandler(sslh, faHandler).handle(ctx);
                    break;
                case PK:
                    lipid = new PolyketideHandler(msfh, ssfh, faHandler).handle(ctx);
                    break;
                default:
                    throw new ParseTreeVisitorException("Unhandled contextCategory: " + contextCategory);
            }
            return lipid;
        }
    }
}

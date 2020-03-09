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
package de.isas.lipidomics.palinom.swisslipids;

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
import de.isas.lipidomics.palinom.GoslinBaseVisitor;
import de.isas.lipidomics.palinom.SwissLipidsBaseVisitor;
import de.isas.lipidomics.palinom.SwissLipidsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.antlr.v4.runtime.tree.RuleNode;

/**
 *
 * @author nils.hoffmann
 */
public class SwissLipidsVisitorImpl extends SwissLipidsBaseVisitor<LipidAdduct> {

    /**
     *
     * @throws ParseTreeVisitorException for structural or state-related issues
     * while trying to process a parsing context.
     * @throws RuntimeException
     * @param ctx
     * @return
     */
    @Override
    public LipidAdduct visitLipid(SwissLipidsParser.LipidContext ctx) {
        Optional<SwissLipidsParser.Lipid_pureContext> categoryContext = Optional.ofNullable(ctx.lipid_pure());

        LipidAdduct la = new LipidAdduct(categoryContext.map((cc) -> {
            return new LipidVisitor().visitLipid_pure(cc);
        }).orElse(LipidSpecies.NONE), Adduct.NONE, "", new Fragment(""));
        return la;
    }

    private static class LipidVisitor extends SwissLipidsBaseVisitor<LipidSpecies> {

        @Override
        public LipidSpecies visitLipid_pure(SwissLipidsParser.Lipid_pureContext ctx) {
            LipidSpecies lipid = null;
            BitSet bs = new BitSet(5);
            bs.set(LipidCategory.ST.ordinal(), ctx.st() != null);
            bs.set(LipidCategory.GL.ordinal(), ctx.gl() != null);
            bs.set(LipidCategory.FA.ordinal(), ctx.fatty_acid() != null);
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
                    lipid = new SterolLipidHandler(
                            new MolecularSubspeciesFasHandler(new FattyAcylHandler()),
                            new StructuralSubspeciesFasHandler(new FattyAcylHandler())
                    ).handle(ctx);
                    break;
                case GL:
                    lipid = new GlyceroLipidHandler(
                            new MolecularSubspeciesFasHandler(new FattyAcylHandler()),
                            new StructuralSubspeciesFasHandler(new FattyAcylHandler()),
                            new FattyAcylHandler()
                    ).handle(ctx);
                    break;
                case FA:
                    lipid = new FattyAcylHandler().handle(ctx);
                    break;
                case GP:
                    lipid = new GlycerophosphoLipidHandler(
                            new MolecularSubspeciesFasHandler(new FattyAcylHandler()),
                            new StructuralSubspeciesFasHandler(new FattyAcylHandler()),
                            new FattyAcylHandler()
                    ).handle(ctx);
                    break;
                case SP:
                    lipid = new SphingoLipidHandler(
                            new StructuralSubspeciesLcbHandler(
                                    new StructuralSubspeciesFasHandler(new FattyAcylHandler()), new FattyAcylHandler()),
                            new FattyAcylHandler()
                    ).handle(ctx);
                    break;
                default:
                    throw new ParseTreeVisitorException("Unhandled contextCategory: " + contextCategory);
            }
            return lipid;
        }
    }
}

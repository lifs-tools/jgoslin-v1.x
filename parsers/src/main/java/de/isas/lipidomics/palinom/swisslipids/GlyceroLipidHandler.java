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
package de.isas.lipidomics.palinom.swisslipids;

import de.isas.lipidomics.domain.HeadGroup;
import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.SwissLipidsParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.SwissLipidsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Optional;

/**
 * Handler implementation for Glycerolipids.
 *
 * @author  nils.hoffmann
 */
class GlyceroLipidHandler implements ParserRuleContextHandler<Lipid_pureContext, LipidSpecies> {

    private final MolecularSubspeciesFasHandler msfh;
    private final StructuralSubspeciesFasHandler ssfh;
    private final IsomericSubspeciesFasHandler isfh;
    private final FattyAcylHandler fhf;

    public GlyceroLipidHandler(MolecularSubspeciesFasHandler msfh, StructuralSubspeciesFasHandler ssfh, IsomericSubspeciesFasHandler isfh, FattyAcylHandler fhf) {
        this.msfh = msfh;
        this.ssfh = ssfh;
        this.isfh = isfh;
        this.fhf = fhf;
    }

    @Override
    public LipidSpecies handle(Lipid_pureContext t) {
        return handleGlycerolipid(t).orElse(LipidSpecies.NONE);
    }

    private Optional<LipidSpecies> handleGlycerolipid(SwissLipidsParser.Lipid_pureContext ctx) throws RuntimeException {
        if (ctx.gl() != null) {
            //glycerophospholipids
            //cardiolipin
            if (ctx.gl().gl_regular() != null) {
                return handleGlRegular(ctx.gl().gl_regular());
            } else if (ctx.gl().gl_mono() != null) {
                return handleGlMono(ctx.gl().gl_mono());
            } else if (ctx.gl().gl_molecular() != null) {
                return handleGlMolecular(ctx.gl().gl_molecular());
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in GL!");
            }
        } else {
            throw new ParseTreeVisitorException("Context for GL was null!");
        }
    }

    private Optional<LipidSpecies> handleGlRegular(SwissLipidsParser.Gl_regularContext dsl) {
        HeadGroup headGroup = new HeadGroup(dsl.gl_hg().getText());
        if (dsl.gl_fa().fa_species() != null) { //species level
            //process single fa
            return fhf.visitSpeciesFas(headGroup, dsl.gl_fa().fa_species().fa());
        } else if (dsl.gl_fa().fa3() != null) {
            //process triple fa
            if (dsl.gl_fa().fa3().fa3_unsorted() != null) {
                return msfh.visitMolecularSubspeciesFas(headGroup, dsl.gl_fa().fa3().fa3_unsorted().fa());
            } else if (dsl.gl_fa().fa3().fa3_sorted() != null) {
                if (dsl.gl_fa().fa3().fa3_sorted().fa() != null) {
                    if (fhf.isIsomericFa(dsl.gl_fa().fa3().fa3_sorted().fa())) {
                        return isfh.visitIsomericSubspeciesFas(headGroup, dsl.gl_fa().fa3().fa3_sorted().fa());
                    } else {
                        return ssfh.visitStructuralSubspeciesFas(headGroup, dsl.gl_fa().fa3().fa3_sorted().fa());
                    }
                } else {
                    throw new ParseTreeVisitorException("Unhandled context state in GL regular FA3 sorted");
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in GL regular FA3!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in GL regular!");
        }
    }

    private Optional<LipidSpecies> handleGlMono(SwissLipidsParser.Gl_monoContext lsl) {
        HeadGroup headGroup = new HeadGroup(lsl.gl_mono_hg().getText());
        if (lsl.gl_mono_fa() != null) {
            if (lsl.gl_mono_fa().fa_species() != null) {
                return fhf.visitSpeciesFas(headGroup, lsl.gl_mono_fa().fa_species().fa());
            } else if (lsl.gl_mono_fa().fa2() != null) {
                if (lsl.gl_mono_fa().fa2().fa2_unsorted() != null) {
                    return msfh.visitMolecularSubspeciesFas(headGroup, lsl.gl_mono_fa().fa2().fa2_unsorted().fa());
                } else if (lsl.gl_mono_fa().fa2().fa2_sorted() != null) {
                    if (lsl.gl_mono_fa().fa2().fa2_sorted().fa() != null) {
                        if (fhf.isIsomericFa(lsl.gl_mono_fa().fa2().fa2_sorted().fa())) {
                            return isfh.visitIsomericSubspeciesFas(headGroup, lsl.gl_mono_fa().fa2().fa2_sorted().fa());
                        } else {
                            return ssfh.visitStructuralSubspeciesFas(headGroup, lsl.gl_mono_fa().fa2().fa2_sorted().fa());
                        }
                    } else {
                        throw new ParseTreeVisitorException("Unhandled context state in Gl mono FA2 sorted!");
                    }
                } else {
                    throw new ParseTreeVisitorException("Unhandled context state in Gl mono FA2!");
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in Gl mono FA!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in Gl mono!");
        }
    }

    private Optional<LipidSpecies> handleGlMolecular(SwissLipidsParser.Gl_molecularContext lsl) {
        HeadGroup headGroup = new HeadGroup(lsl.gl_molecular_hg().getText());
        if (lsl.gl_molecular_fa() != null) {
            if (lsl.gl_molecular_fa().fa2() != null) {
                if (lsl.gl_molecular_fa().fa2().fa2_unsorted() != null) {
                    return msfh.visitMolecularSubspeciesFas(headGroup, lsl.gl_molecular_fa().fa2().fa2_unsorted().fa());
                } else if (lsl.gl_molecular_fa().fa2().fa2_sorted() != null) {
                    if (lsl.gl_molecular_fa().fa2().fa2_sorted().fa() != null) {
                        if (fhf.isIsomericFa(lsl.gl_molecular_fa().fa2().fa2_sorted().fa())) {
                            return isfh.visitIsomericSubspeciesFas(headGroup, lsl.gl_molecular_fa().fa2().fa2_sorted().fa());
                        } else {
                            return ssfh.visitStructuralSubspeciesFas(headGroup, lsl.gl_molecular_fa().fa2().fa2_sorted().fa());
                        }
                    } else {
                        throw new ParseTreeVisitorException("Unhandled context state in GL regular FA2 sorted!");
                    }
                } else {
                    throw new ParseTreeVisitorException("Unhandled context state in GL regular FA2!");
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in Gl mono FA!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in Gl mono!");
        }
    }
}

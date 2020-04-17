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

import de.isas.lipidomics.palinom.ParserRuleContextHandler;
import de.isas.lipidomics.domain.LipidSpecies;
import de.isas.lipidomics.palinom.HMDBParser.Lipid_pureContext;
import de.isas.lipidomics.palinom.HMDBParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Optional;

/**
 * Handler implementation for Glycero-phospholipids.
 *
 * @author nilshoffmann
 */
public class GlycerophosphoLipidHandler implements ParserRuleContextHandler<Lipid_pureContext, LipidSpecies> {

    private final MolecularSubspeciesFasHandler msfh;
    private final StructuralSubspeciesFasHandler ssfh;
    private final IsomericSubspeciesFasHandler isfh;
    private final FattyAcylHandler fhf;

    public GlycerophosphoLipidHandler(MolecularSubspeciesFasHandler msfh, StructuralSubspeciesFasHandler ssfh, IsomericSubspeciesFasHandler isfh, FattyAcylHandler fhf) {
        this.msfh = msfh;
        this.ssfh = ssfh;
        this.isfh = isfh;
        this.fhf = fhf;
    }

    @Override
    public LipidSpecies handle(Lipid_pureContext t) {
        return handleGlycerophosphoLipid(t).orElse(LipidSpecies.NONE);
    }

    private Optional<LipidSpecies> handleGlycerophosphoLipid(HMDBParser.Lipid_pureContext ctx) throws RuntimeException {
        if (ctx.lipid_class().pl() != null) {
            //glycerophospholipids
            //cardiolipin
            if (ctx.lipid_class().pl().pl_regular() != null) {
                return handleGpRegular(ctx.lipid_class().pl().pl_regular());
            } else if (ctx.lipid_class().pl().pl_three() != null) {
                return handleGpThree(ctx.lipid_class().pl().pl_three());
            } else if (ctx.lipid_class().pl().pl_four() != null) {
                return handleGpFour(ctx.lipid_class().pl().pl_four());
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in GP!");
            }
        } else {
            throw new ParseTreeVisitorException("Context for GP was null!");
        }
    }

    private Optional<LipidSpecies> handleGpRegular(HMDBParser.Pl_regularContext dsl) {
        String headGroup = dsl.pl_hg().getText();
        if (dsl.pl_fa().fa_species() != null) { //species level
            //process single fa
            return fhf.visitSpeciesFas(headGroup, dsl.pl_fa().fa_species().fa());
        } else if (dsl.pl_fa().fa2() != null) {
            //process double fa
            if (dsl.pl_fa().fa2().fa2_unsorted() != null) {
                return msfh.visitMolecularSubspeciesFas(headGroup, dsl.pl_fa().fa2().fa2_unsorted().fa());
            } else if (dsl.pl_fa().fa2().fa2_sorted() != null) {
                if (dsl.pl_fa().fa2().fa2_sorted().fa() != null) {
                    if (fhf.isIsomericFa(dsl.pl_fa().fa2().fa2_sorted().fa())) {
                        return isfh.visitIsomericSubspeciesFas(headGroup, dsl.pl_fa().fa2().fa2_sorted().fa());
                    } else {
                        return ssfh.visitStructuralSubspeciesFas(headGroup, dsl.pl_fa().fa2().fa2_sorted().fa());
                    }
                } else {
                    throw new ParseTreeVisitorException("Unhandled context state in GP regular FA2 sorted!");
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in GP regular FA2!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in GP regular!");
        }
    }

    private Optional<LipidSpecies> handleGpThree(HMDBParser.Pl_threeContext lsl) {
        String headGroup = lsl.pl_three_hg().getText();
        if (lsl.pl_three_fa() != null) {
            if (lsl.pl_three_fa().fa_species() != null) {
                return fhf.visitSpeciesFas(headGroup, lsl.pl_three_fa().fa_species().fa());
            } else if (lsl.pl_three_fa().fa3() != null) {
                if (lsl.pl_three_fa().fa3().fa3_unsorted() != null) {
                    return msfh.visitMolecularSubspeciesFas(headGroup, lsl.pl_three_fa().fa3().fa3_unsorted().fa());
                } else if (lsl.pl_three_fa().fa3().fa3_sorted() != null) {
                    if (lsl.pl_three_fa().fa3().fa3_sorted().fa() != null) {
                        if (fhf.isIsomericFa(lsl.pl_three_fa().fa3().fa3_sorted().fa())) {
                            return isfh.visitIsomericSubspeciesFas(headGroup, lsl.pl_three_fa().fa3().fa3_sorted().fa());
                        } else {
                            return ssfh.visitStructuralSubspeciesFas(headGroup, lsl.pl_three_fa().fa3().fa3_sorted().fa());
                        }
                    } else {
                        throw new ParseTreeVisitorException("Unhandled context state in GP three FA3 sorted !");
                    }
                } else {
                    throw new ParseTreeVisitorException("Unhandled context state in GP three FA3!");
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in GP three species!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in GP three!");
        }
    }

    private Optional<LipidSpecies> handleGpFour(HMDBParser.Pl_fourContext lsl) {
        String headGroup = lsl.pl_four_hg().getText();
        if (lsl.pl_four_fa() != null) {
            if (lsl.pl_four_fa().fa_species() != null) {
                return fhf.visitSpeciesFas(headGroup, lsl.pl_four_fa().fa_species().fa());
            } else if (lsl.pl_four_fa().fa2() != null) {
                if (lsl.pl_four_fa().fa2().fa2_unsorted() != null) {
                    return msfh.visitMolecularSubspeciesFas(headGroup, lsl.pl_four_fa().fa2().fa2_unsorted().fa());
                } else if (lsl.pl_four_fa().fa2().fa2_sorted() != null) {
                    if (lsl.pl_four_fa().fa2().fa2_sorted().fa() != null) {
                        if (fhf.isIsomericFa(lsl.pl_four_fa().fa2().fa2_sorted().fa())) {
                            return isfh.visitIsomericSubspeciesFas(headGroup, lsl.pl_four_fa().fa2().fa2_sorted().fa());
                        } else {
                            return ssfh.visitStructuralSubspeciesFas(headGroup, lsl.pl_four_fa().fa2().fa2_sorted().fa());
                        }
                    } else {
                        throw new ParseTreeVisitorException("Unhandled context state in GP four FA2 sorted!");
                    }
                } else {
                    throw new ParseTreeVisitorException("Unhandled context state in GP four FA2!");
                }
            } else if (lsl.pl_four_fa().fa4() != null) {
                if (lsl.pl_four_fa().fa4().fa4_unsorted() != null) {
                    return msfh.visitMolecularSubspeciesFas(headGroup, lsl.pl_four_fa().fa4().fa4_unsorted().fa());
                } else if (lsl.pl_four_fa().fa4().fa4_sorted() != null) {
                    if (lsl.pl_four_fa().fa4().fa4_sorted().fa() != null) {
                        if (fhf.isIsomericFa(lsl.pl_four_fa().fa4().fa4_sorted().fa())) {
                            return isfh.visitIsomericSubspeciesFas(headGroup, lsl.pl_four_fa().fa4().fa4_sorted().fa());
                        } else {
                            return ssfh.visitStructuralSubspeciesFas(headGroup, lsl.pl_four_fa().fa4().fa4_sorted().fa());
                        }
                    } else {
                        throw new ParseTreeVisitorException("Unhandled context state in GP four FA4 sorted!");
                    }
                } else {
                    throw new ParseTreeVisitorException("Unhandled context state in GP four FA4!");
                }
            } else {
                throw new ParseTreeVisitorException("Unhandled context state in GP four species!");
            }
        } else {
            throw new ParseTreeVisitorException("Unhandled context state in GP four!");
        }
    }
}

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
package de.isas.lipidomics.domain;

import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author nils.hoffmann
 */
public enum LipidClass {
    
    /** Fatty acyls [FA]
      * Fatty acids and conjugates [FA01] */
    FA(LipidCategory.FA, "FA","Fatty acids and conjugates [FA01]"),
    /** Glycerolipids [GL] 
      * Monoradylglycerols [GL01] */
    MG(LipidCategory.GL, "MG","Monoradylglycerols [GL01]"),
    /** Diradylglycerols [GL02] */
    DG(LipidCategory.GL, "DG", "Diradylglycerols [GL02]"),
    /** Triradylglycerols [GL03] */
    TG(LipidCategory.GL, "TG", "Triradylglycerols [GL03]"),
    /* TODO: there are some newer categories in LipidMaps, like Glycosylmono/di-radylglycerols, SQMG and SQDG */
    /** Glycerophospholipids [GP] 
     *  
     */
    BMP(LipidCategory.GP, "BMP", "Monoacylglycerophosphomonoradylglycerols [GP0410]"),
    CL(LipidCategory.GP, "CL", "Glycerophosphoglycerophosphoglycerols [GP12]"),
    PA(LipidCategory.GP, "PA", "Glycerophosphates [GP10]"),
    LPA(LipidCategory.GP, "LPA", "Glycerophosphates [GP10]"),
    PC(LipidCategory.GP, "PC", "Glycerophosphocholines [GP01]"),
    LPC(LipidCategory.GP, "LPC", "Glycerophosphocholines [GP01]"),
    PE(LipidCategory.GP, "PE", "Glycerophosphoethanolamines [GP02]"),
    LPE(LipidCategory.GP, "LPE", "Glycerophosphoethanolamines [GP02]"),
    PG(LipidCategory.GP, "PG", "Glycerophosphoglycerols [GP04]"),
    LPG(LipidCategory.GP, "LPG", "Glycerophosphoglycerols [GP04]"),
    PGP(LipidCategory.GP, "PGP", "Glycerophosphoglycerophosphates [GP05]"),
    PI(LipidCategory.GP, "PI", "Glycerophosphoinositols [GP06]"),
    LPI(LipidCategory.GP, "LPI", "Glycerophosphoinositols [GP06]"),
    PIP(LipidCategory.GP, "PIP", "Glycerophosphoinositol monophosphates [GP07]"),
    PIP_3p(LipidCategory.GP, "PIP[3']", "Glycerophosphoinositol monophosphates [GP07]"),
    PIP_4p(LipidCategory.GP, "PIP[4']", "Glycerophosphoinositol monophosphates [GP07]"),
    PIP_5p(LipidCategory.GP, "PIP[5']", "Glycerophosphoinositol monophosphates [GP07]"),
    PIP2(LipidCategory.GP, "PIP2", "Glycerophosphoinositol bisphosphates [GP08]"),
    PIP2_3p_4p(LipidCategory.GP, "PIP2[3',4']", "Glycerophosphoinositol bisphosphates [GP08]"),
    PIP2_3p_5p(LipidCategory.GP, "PIP2[3',5']", "Glycerophosphoinositol bisphosphates [GP08]"),
    PIP3(LipidCategory.GP, "PIP3", "Glycerophosphoinositol trisphosphates [GP09]"),
    PS(LipidCategory.GP, "PS", "Glycerophosphoserines [GP03]"),
    /**Sphingolipids */
    CER(LipidCategory.SP, "Cer", "Ceramides [SP02]"),
    C1P(LipidCategory.SP, "C1P", "Ceramide-1-phosphates [SP0205]"),
    SPH(LipidCategory.SP, "SPH", "Sphingoid bases [SP01]"),
    S1P(LipidCategory.SP, "S1P", "Sphingoid bases [SP01]"),
    SM(LipidCategory.SP, "SM", "Phosphosphingolipids [SP03]"),
    HEXCER(LipidCategory.SP, "HexCer", "Neutral glycosphingolipids [SP05]"),
    GLCCER(LipidCategory.SP, "GlcCer", "Neutral glycosphingolipids [SP05]"),
    GALCER(LipidCategory.SP, "GalCer", "Neutral glycosphingolipids [SP05]"),
    HEX2CER(LipidCategory.SP, "Hex2Cer", "Neutral glycosphingolipids [SP05]"),
    LACCER(LipidCategory.SP, "LacCer", "Neutral glycosphingolipids [SP05]"),
    /** Sterol lipids */
    ST(LipidCategory.ST, "ST", "Sterols [ST01]"),
    SE(LipidCategory.ST, "SE", "Steryl esters [ST0102]"),
    FC(LipidCategory.ST, "FC", "Cholesterol [LMST01010001]"),
    CH(LipidCategory.ST, "Ch", "Cholesterol [LMST01010001]"),
    CHE(LipidCategory.ST, "ChE", "Cholesteryl esters [ST0102]"),
    CE(LipidCategory.ST, "CE", "Cholesteryl esters [ST0102]")
    ;
    
    private final LipidCategory category;
    private final String abbreviation;
    private final String lipidMapsClassName;

    private LipidClass(LipidCategory category, String abbreviation, String lipidMapsClassName) {
        this.category = category;
        this.abbreviation = abbreviation;
        this.lipidMapsClassName = lipidMapsClassName;
    }

    public LipidCategory getCategory() {
        return this.category;
    }
    
    public String getAbbreviation() {
        return this.abbreviation;
    }
    
    public String getLipidMapsClassName() {
        return this.lipidMapsClassName;
    }
    
    public String getLysoAbbreviation(LipidClass lipidClass) {
        if(lipidClass.getCategory()==LipidCategory.GP) {
            return "L"+lipidClass.getAbbreviation();
        }
        throw new UnsupportedOperationException("Lipid category must be "+LipidCategory.GP+" for lyso-classes!");
    }
    
    public static Optional<LipidClass> forHeadGroup(String headGroup) {
        return Arrays.asList(values()).stream().filter((lipidClass) -> {
            return lipidClass.getAbbreviation().equals(headGroup);
        }).findFirst();
    }

}

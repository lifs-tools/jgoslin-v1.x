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

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Base class for lipid names parsed using the different grammars. This can
 * contain a lipid, an adduct, a sum formula and a fragment.
 *
 * @author nils.hoffmann
 * @see LipidSpecies
 * @see Adduct
 * @see Fragment
 */
@AllArgsConstructor
@Data
public class LipidAdduct {

    private LipidSpecies lipid;
    private Adduct adduct;
    private String sumFormula;
    private Fragment fragment;

    public String getLipidString(LipidLevel level) {
        StringBuilder sb = new StringBuilder();
        if (lipid != null) {
            sb.append(lipid.getLipidString(level));
        } else {
            return "";
        }

        if (adduct != null) {
            sb.append(adduct.getLipidString());
        }
        return sb.toString();
    }

    public String getNormalizedLipidString(LipidLevel level) {
        StringBuilder sb = new StringBuilder();
        if (lipid != null) {
            sb.append(lipid.getLipidString(level, true));
        } else {
            return "";
        }

        if (adduct != null) {
            sb.append(adduct.getLipidString());
        }
        return sb.toString();
    }

    public String getClassName() {
        if (lipid != null) {
            return lipid.getLipidClass().orElse(LipidClass.UNDEFINED).getAbbreviation();
        } else {
            return "";
        }
    }

    public String getLipidFragmentString(LipidLevel level) {
        StringBuilder sb = new StringBuilder();
        if (lipid != null) {
            sb.append(getLipidString(level));
        } else {
            return "";
        }
        if (adduct != null) {
            sb.append(adduct.getLipidString());
        }
        if (fragment != null) {
            sb.append(" - ").append(fragment.getLipidString());
        }
        return sb.toString();
    }

    public String getNormalizedLipidFragmentString(LipidLevel level) {
        StringBuilder sb = new StringBuilder();
        if (lipid != null) {
            sb.append(getNormalizedLipidString(level));
        } else {
            return "";
        }
        if (adduct != null) {
            sb.append(adduct.getLipidString());
        }
        if (fragment != null) {
            sb.append(" - ").append(fragment.getLipidString());
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        if(lipid!=null) {
            return getLipidString(lipid.getInfo().orElse(LipidSpeciesInfo.NONE).getLevel());
        }
        return "";
    }

}

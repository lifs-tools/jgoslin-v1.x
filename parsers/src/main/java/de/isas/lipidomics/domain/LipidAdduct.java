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

import static de.isas.lipidomics.domain.LipidLevel.ISOMERIC_SUBSPECIES;
import static de.isas.lipidomics.domain.LipidLevel.MOLECULAR_SUBSPECIES;
import static de.isas.lipidomics.domain.LipidLevel.SPECIES;
import static de.isas.lipidomics.domain.LipidLevel.STRUCTURAL_SUBSPECIES;
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
    private Fragment fragment;

    /**
     * Calculates the mass based on the elements of this lipid adduct.
     *
     * @return the mass (in Dalton).
     */
    public Double getMass() {
        ElementTable elements = getElements();
        int charge = 0;
        double mass = 0;

        if (adduct != null) {
            charge = adduct.getCharge();
        }

        mass = elements.getMass();

        if (charge != 0) {
            mass = (mass - charge * Element.ELECTRON_REST_MASS) / Math.abs(charge);
        }

        return mass;
    }

    /**
     * Returns the elemental composition table.
     *
     * @return the elemental composition table.
     */
    public ElementTable getElements() {
        ElementTable elements = new ElementTable();
        if (lipid != null) {
            elements.add(lipid.getElements());
        }
        if (adduct != null) {
            if (lipid != null) {
                LipidSpeciesInfo info = lipid.getInfo();
                //only add elements on species level or below
                //higher levels will not return a mass 
                switch (info.getLevel()) {
                    case ISOMERIC_SUBSPECIES:
                    case MOLECULAR_SUBSPECIES:
                    case STRUCTURAL_SUBSPECIES:
                    case SPECIES:
                        elements.add(adduct.getElements());
                        break;
                }
            }
        }
        return elements;
    }

    /**
     * Returns the sum formula.
     *
     * @return the sum formula.
     */
    public String getSumFormula() {
        return getElements().getSumFormula();
    }

    /**
     * Returns the non-normalized (original head group) lipid name for the
     * native level of this lipid.
     *
     * @return the non-normalized lipid name with adduct.
     */
    public String getLipidString() {
        if (lipid != null) {
            return getLipidString(lipid.getInfo().getLevel());
        }
        return "";
    }

    /**
     * Returns the non-normalized (original head group) lipid name for the given
     * level.
     *
     * @param level the lipid level to generate the name on.
     * @return the non-normalized lipid name with adduct.
     */
    public String getLipidString(LipidLevel level) {
        StringBuilder sb = new StringBuilder();
        if (lipid != null) {
            sb.append(lipid.getLipidString(level));
        } else {
            return "";
        }

        if (adduct != null && !adduct.getLipidString().isEmpty()) {
            sb.append(adduct.getLipidString());
        }
        return sb.toString();
    }

    /**
     * Returns the normalized (class name as head group) lipid name for the
     * native level of this lipid.
     *
     * @return the normalized lipid name with adduct.
     */
    public String getNormalizedLipidString() {
        if (lipid != null) {
            return getNormalizedLipidString(lipid.getInfo().getLevel());
        }
        return "";
    }

    /**
     * Returns the normalized (class name as head group) lipid name for the
     * given level.
     *
     * @param level the lipid level to generate the name on.
     * @return the normalized lipid name with adduct.
     */
    public String getNormalizedLipidString(LipidLevel level) {
        StringBuilder sb = new StringBuilder();
        if (lipid != null) {
            sb.append(lipid.getLipidString(level, true));
        } else {
            return "";
        }

        if (adduct != null && !adduct.getLipidString().isEmpty()) {
            sb.append(adduct.getLipidString());
        }
        return sb.toString();
    }

    /**
     * Returns the class name for this lipid adduct.
     *
     * @return the class name.
     */
    public String getClassName() {
        if (lipid != null) {
            return lipid.getLipidClass().getAbbreviation();
        } else {
            return "";
        }
    }

    /**
     * Returns the non-normalized lipid adduct name (original head group) with
     * fragment, if available, for the given level.
     *
     * @param level the lipid level to generate the name on.
     * @return the non-normalized lipid name with adduct and fragment.
     */
    public String getLipidFragmentString(LipidLevel level) {
        StringBuilder sb = new StringBuilder();
        if (lipid != null) {
            sb.append(getLipidString(level));
        } else {
            return "";
        }
        if (adduct != null && !adduct.getLipidString().isEmpty()) {
            sb.append(adduct.getLipidString());
        }
        if (fragment != null && !fragment.getLipidString().isEmpty()) {
            sb.append(" - ").append(fragment.getLipidString());
        }
        return sb.toString();
    }

    /**
     * Returns the normalized lipid adduct name (class name as head group) with
     * fragment for the given level.
     *
     * @param level the lipid level to generate the name on.
     * @return the normalized lipid name with adduct and fragment.
     */
    public String getNormalizedLipidFragmentString(LipidLevel level) {
        StringBuilder sb = new StringBuilder();
        if (lipid != null) {
            sb.append(getNormalizedLipidString(level));
        } else {
            return "";
        }
        if (adduct != null && !adduct.getLipidString().isEmpty()) {
            sb.append(adduct.getLipidString());
        }
        if (fragment != null && !fragment.getLipidString().isEmpty()) {
            sb.append(" - ").append(fragment.getLipidString());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        if (lipid != null) {
            return getLipidFragmentString(lipid.getInfo().getLevel());
        }
        return "";
    }

}

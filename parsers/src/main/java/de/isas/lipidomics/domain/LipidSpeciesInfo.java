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

import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class summarizes the FA parts of a lipid, independent of its head group.
 * Thus, it accounts the total number of carbon atoms, double bonds, the number
 * of hydroxylations, the overall FA-headgroup bond type, e.g. PLASMANYL /
 * PLASMENYL, if any of a lipid's FA chains has such a bond type, or ESTER or
 * UNDEFINED for other cases.
 *
 * @author nils.hoffmann
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LipidSpeciesInfo extends FattyAcid {

    private static final class None extends LipidSpeciesInfo {

        private None() {
            super(LipidLevel.UNDEFINED, "NONE", -1, 0, 0, 0, LipidFaBondType.UNDEFINED, false, ModificationsList.NONE);
        }
    }

    /**
     * Use this class to indicate that no lipid species info is available.
     */
    public static final LipidSpeciesInfo NONE = new LipidSpeciesInfo.None();

    private final LipidLevel level;

    /**
     * Create a new LipidSpeciesInfo on the provided level.
     *
     * @param level the level of the species info.
     * @param name the name, e.g. FA1 for the first FA.
     * @param position the sn position. -1 if undefined or unknown.
     * @param nCarbon the number of carbons in this FA.
     * @param nHydroxy the number of hydroxyls on this FA.
     * @param nDoubleBonds the number of double bonds in this FA.
     * @param lipidFaBondType the bond type, e.g. ESTER.
     * @param lcb true if this is a long-chain base, e.g. in a Ceramide.
     * @param modifications optional modifications for this FA.
     * @see LipidFaBondType
     */
    @Builder(builderMethodName = "lipidSpeciesInfoBuilder")
    public LipidSpeciesInfo(LipidLevel level, String name, int position, int nCarbon, int nHydroxy, int nDoubleBonds, LipidFaBondType lipidFaBondType, boolean lcb, ModificationsList modifications) {
        super(name, position, nCarbon, nHydroxy, nDoubleBonds, lipidFaBondType, lcb, modifications);
        this.level = level;
    }

    /**
     * Create a new LipidSpeciesInfo on the provided level.
     *
     * @param level the level of the species info.
     * @param name the name, e.g. FA1 for the first FA.
     * @param position the sn position. -1 if undefined or unknown.
     * @param nCarbon the number of carbons in this FA.
     * @param nHydroxy the number of hydroxyls on this FA.
     * @param lipidFaBondType the bond type, e.g. ESTER.
     * @param lcb true if this is a long-chain base, e.g. in a Ceramide.
     * @param modifications optional modifications for this FA.
     * @param nDoubleBonds the number of double bonds in this FA.
     * @param doubleBondPositions double bond positions in this FA.
     * @see LipidFaBondType
     */
    @Builder(builderMethodName = "lipidSubspeciesInfoBuilder", builderClassName = "LipidSubspeciesInfoBuilder")
    public LipidSpeciesInfo(LipidLevel level, String name, int position, int nCarbon, int nHydroxy, LipidFaBondType lipidFaBondType, boolean lcb, ModificationsList modifications, int nDoubleBonds, Map<Integer, String> doubleBondPositions) {
        super(name, position, nCarbon, nHydroxy, lipidFaBondType, lcb, modifications, nDoubleBonds, doubleBondPositions);
        this.level = level;
    }

    /**
     * Create a new LipidSpeciesInfo on the provided level. The name is inferred
     * from the level.
     *
     * @param level the level of the species info.
     * @param nCarbon the number of carbons in this FA.
     * @param nHydroxy the number of hydroxyls on this FA.
     * @param lipidFaBondType the bond type, e.g. ESTER.
     */
    public LipidSpeciesInfo(LipidLevel level, int nCarbon, int nHydroxy, int nDoubleBonds, LipidFaBondType lipidFaBondType) {
        this(level, level.name(), -1, nCarbon, nHydroxy, nDoubleBonds, lipidFaBondType, false, ModificationsList.NONE);
    }

    @Override
    public String buildSubstructureName(LipidLevel level) {
        final StringBuilder sb = new StringBuilder();
        sb.
                append(getName()).
                append(" ").
                append(getNCarbon()).
                append(":").
                append(getNDoubleBonds());
        if (getNHydroxy() > 0) {
            sb.append(";").append(getNHydroxy());
        }
        if (!getModifications().isEmpty()) {
            sb.append("(");
            sb.append(getModifications().stream().map((t) -> {
                return (t.getLeft() == -1 ? "" : t.getLeft()) + "" + t.getRight();
            }).collect(Collectors.joining(",")));
            sb.append(")");
        }
        return sb.toString();
    }

    /**
     * Returns the element table for this species info adjusted for oxygen and
     * hydrogen.
     *
     * @param numFa the number of fatty acyls to account for.
     * @return the element table.
     */
    public ElementTable getElements(int numFa) {
        ElementTable elements = super.getElements();
        if (numFa > 0) {
            elements.incrementBy(Element.ELEMENT_O, numFa - 1);
            elements.decrementBy(Element.ELEMENT_H, numFa - 1);
        }
        return elements;
    }
}

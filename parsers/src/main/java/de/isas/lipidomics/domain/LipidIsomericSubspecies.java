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

import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An isomeric subspecies. Child of LipidStructuralSubspecies. Individual FAs,
 * their sn positions, and the double bond positions are known.
 *
 * Example: PE(P-18:0/22:6(4Z,7Z,10Z,13Z,16Z,19Z))
 *
 * @author nils.hoffmann
 * @see LipidStructuralSubspecies
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LipidIsomericSubspecies extends LipidStructuralSubspecies {

    /**
     * Creates a new LipidIsomericSubspecies.
     *
     * @param headGroup the head group for this lipid.
     * @param fa the FattyAcids attached to this lipid.
     */
    @Builder(builderMethodName = "lipidIsomericSubspeciesBuilder")
    public LipidIsomericSubspecies(String headGroup, FattyAcid... fa) {
        super(headGroup);
        int nCarbon = 0;
        int nHydroxyl = 0;
        int nDoubleBonds = 0;
        for (FattyAcid fas : fa) {
            if (super.fa.containsKey(fas.getName())) {
                throw new ConstraintViolationException(
                        "FA names must be unique! FA with name " + fas.getName() + " was already added!");
            } else {
                super.fa.put(fas.getName(), fas);
                nCarbon += fas.getNCarbon();
                nHydroxyl += fas.getNHydroxy();
                nDoubleBonds += fas.getNDoubleBonds();
            }
        }
        super.info = Optional.of(
                LipidSpeciesInfo.lipidSpeciesInfoBuilder().
                        level(LipidLevel.ISOMERIC_SUBSPECIES).
                        name(headGroup).
                        position(-1).
                        nCarbon(nCarbon).
                        nHydroxy(nHydroxyl).
                        nDoubleBonds(nDoubleBonds).
                        lipidFaBondType(LipidFaBondType.getLipidFaBondType(headGroup, fa)).
                        build()
        );
    }

    private String buildLipidIsomericSubstructureName(LipidLevel level, String headGroup) {
        String faStrings = getFa().values().stream().map((fa) -> {
            return fa.buildSubstructureName(level);
        }).collect(Collectors.joining("/"));
        return headGroup + (faStrings.isEmpty() ? "" : getHeadGroupSuffix()) + faStrings;
    }

    @Override
    public String getLipidString(LipidLevel level, boolean normalizeHeadGroup) {
        String headGroup = normalizeHeadGroup ? getNormalizedHeadGroup() : getHeadGroup();
        switch (level) {
            case ISOMERIC_SUBSPECIES:
                return buildLipidIsomericSubstructureName(level, headGroup);
            case STRUCTURAL_SUBSPECIES:
            case MOLECULAR_SUBSPECIES:
            case CATEGORY:
            case CLASS:
            case SPECIES:
                return super.getLipidString(level, normalizeHeadGroup);
            default:
                LipidLevel thisLevel = getInfo().orElse(LipidSpeciesInfo.NONE).getLevel();
                throw new ConstraintViolationException(getClass().getSimpleName() + " can not create a string for lipid with level " + thisLevel + " for level " + level + ": target level is more specific than this lipid's level!");
        }
    }

    @Override
    public String getLipidString(LipidLevel level) {
        return this.getLipidString(level, false);
    }

    @Override
    public String getNormalizedLipidString() {
        return getLipidString(getInfo().orElse(LipidSpeciesInfo.NONE).getLevel(), true);
    }

    @Override
    public String toString() {
        return getLipidString();
    }
}

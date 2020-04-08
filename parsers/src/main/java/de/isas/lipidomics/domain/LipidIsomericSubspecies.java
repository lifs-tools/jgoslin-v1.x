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
 * Example: PE(P-18:0/22:6(4Z,7Z,10Z,13Z,16Z,19Z))
 *
 * @author nils.hoffmann
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LipidIsomericSubspecies extends LipidStructuralSubspecies {

    /**
     *
     * @param headGroup
     * @param fa
     */
    @Builder(builderMethodName = "lipidIsomericSubspeciesBuilder")
    public LipidIsomericSubspecies(String headGroup, IsomericFattyAcid... fa) {
        super(headGroup);
        int nCarbon = 0;
        int nHydroxyl = 0;
        int nDoubleBonds = 0;
        for (IsomericFattyAcid fas : fa) {
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

    private String buildLipidIsomericSubstructureName(LipidLevel level) {
        String faStrings = getFa().values().stream().map((fa) -> {
            return fa.buildSubstructureName(level);
        }).collect(Collectors.joining("/"));
        return getHeadGroup() + (faStrings.isEmpty() ? "" : getHeadGroupSuffix()) + faStrings;
    }

    @Override
    public String getLipidString(LipidLevel level) {
        switch (level) {
            case ISOMERIC_SUBSPECIES:
                return buildLipidIsomericSubstructureName(level);
            case STRUCTURAL_SUBSPECIES:
            case MOLECULAR_SUBSPECIES:
            case CATEGORY:
            case CLASS:
            case SPECIES:
                return super.getLipidString(level);
            default:
                LipidLevel thisLevel = getInfo().orElse(LipidSpeciesInfo.NONE).getLevel();
                throw new ConstraintViolationException(getClass().getSimpleName() + " can not create a string for lipid with level " + thisLevel + " for level " + level + ": target level is more specific than this lipid's level!");
        }
    }

    @Override
    public String toString() {
        return getLipidString();
    }
}

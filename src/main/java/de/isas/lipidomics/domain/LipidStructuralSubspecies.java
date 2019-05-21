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
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author nils.hoffmann
 */
@Data
public class LipidStructuralSubspecies extends LipidMolecularSubspecies {

    private final String lipidSpeciesString;

    public LipidStructuralSubspecies(String headGroup, StructuralFattyAcid... fa) {
        super(headGroup);
        int nCarbon = 0;
        int nHydroxyl = 0;
        int nDoubleBonds = 0;
        LipidFaBondType lipidFaBondType = LipidFaBondType.ESTER;
        for (StructuralFattyAcid fas : fa) {
            if (super.fa.containsKey(fas.getName())) {
                throw new ConstraintViolationException(
                        "FA names must be unique! FA with name " + fas.getName() + " was already added!");
            } else {
                super.fa.put(fas.getName(), fas);
                nCarbon += fas.getNCarbon();
                nHydroxyl += fas.getNHydroxy();
                nDoubleBonds += fas.getNDoubleBonds();
                if (lipidFaBondType == LipidFaBondType.ESTER && (fas.getLipidFaBondType() == LipidFaBondType.ETHER_PLASMANYL || fas.getLipidFaBondType() == LipidFaBondType.ETHER_PLASMENYL)) {
                    lipidFaBondType = fas.getLipidFaBondType();
                } else if (lipidFaBondType != LipidFaBondType.ESTER && (fas.getLipidFaBondType() == LipidFaBondType.ETHER_PLASMANYL || fas.getLipidFaBondType() == LipidFaBondType.ETHER_PLASMENYL)) {
                    throw new ConstraintViolationException("Only one FA can define an ether bond to the head group! Tried to add " + fas.getLipidFaBondType() + " over existing " + lipidFaBondType);
                }
            }
        }
        super.info = Optional.of(new LipidSpeciesInfo(LipidLevel.STRUCTURAL_SUBSPECIES, nCarbon, nHydroxyl, nDoubleBonds, lipidFaBondType));
        this.lipidSpeciesString = super.buildLipidSubspeciesName("/");
    }

    @Override
    public String getLipidString(LipidLevel level) {
        switch (level) {
            case STRUCTURAL_SUBSPECIES:
                return lipidSpeciesString;
            case MOLECULAR_SUBSPECIES:
            case CATEGORY:
            case CLASS:
            case SPECIES:
                return super.getLipidString(level);
            default:
                throw new RuntimeException(getClass().getSimpleName() + " does not know how to create a lipid string for level " + level);
        }
    }
}

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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Example: Phosphatidylinositol (8:0-8:0) or PI(8:0-8:0)
 * @author nils.hoffmann
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class LipidMolecularSubspecies extends LipidSpecies {

    protected final Map<String, FattyAcid> fa = new LinkedHashMap<>();

    @Builder
    public LipidMolecularSubspecies(String headGroup, MolecularFattyAcid... fa) {
        super(headGroup);
        int nCarbon = 0;
        int nHydroxyl = 0;
        int nDoubleBonds = 0;
        LipidFaBondType lipidFaBondType = LipidFaBondType.ESTER;
        for (MolecularFattyAcid fas : fa) {
            if (fas.getPosition() != -1) {
                throw new ConstraintViolationException("MolecularFattyAcid " + fas.getName() + " must have position set to -1! Was: " + fas.getPosition());
            }
            if (this.fa.containsKey(fas.getName())) {
                throw new ConstraintViolationException(
                        "FA names must be unique! FA with name " + fas.getName() + " was already added!");
            } else {
                this.fa.put(fas.getName(), fas);
                nCarbon += fas.getNCarbon();
                nHydroxyl += fas.getNHydroxy();
                nDoubleBonds += fas.getNDoubleBonds();
                if (lipidFaBondType == LipidFaBondType.ESTER && (fas.getLipidFaBondType() == LipidFaBondType.ETHER_PLASMANYL || fas.getLipidFaBondType() == LipidFaBondType.ETHER_PLASMENYL)) {
                    lipidFaBondType = fas.getLipidFaBondType();
//                    nDoubleBonds += lipidFaBondType.doubleBondCorrection();
//                    log.debug("Correcting double bond count to {} due to ether bond.", nDoubleBonds);
                } else if (lipidFaBondType != LipidFaBondType.ESTER && (fas.getLipidFaBondType() == LipidFaBondType.ETHER_PLASMANYL || fas.getLipidFaBondType() == LipidFaBondType.ETHER_PLASMENYL)) {
                    throw new ConstraintViolationException("Only one FA can define an ether bond to the head group! Tried to add " + fas.getLipidFaBondType() + " over existing " + lipidFaBondType);
                }
            }
        }
        super.info = Optional.of(LipidSpeciesInfo.lipidSpeciesInfoBuilder().
            level(LipidLevel.MOLECULAR_SUBSPECIES).
            name(headGroup).
            position(-1).
            nCarbon(nCarbon).
            nHydroxy(nHydroxyl).
            nDoubleBonds(nDoubleBonds).
            lipidFaBondType(lipidFaBondType).
            build()
        );
    }

    @Override
    public Map<String, FattyAcid> getFa() {
        return Collections.unmodifiableMap(fa);
    }

    protected String buildLipidSubspeciesName(String faSeparator) {
        String faStrings = getFa().values().stream().map((fa) -> {
            return fa.buildSubstructureName();
        }).collect(Collectors.joining(faSeparator));
        String hgToFaSep = " ";
        if (isEsterLipid()) {
            hgToFaSep = "-";
        }
        return getHeadGroup() + (faStrings.isEmpty() ? "" : hgToFaSep) + faStrings;
    }

    @Override
    public String getLipidString(LipidLevel level) {
        switch (level) {
            case MOLECULAR_SUBSPECIES:
                return buildLipidSubspeciesName("_");
            case CATEGORY:
            case CLASS:
            case SPECIES:
                return super.getLipidString(level);
            default:
                throw new RuntimeException(getClass().getSimpleName() + " does not know how to create a lipid string for level " + level);
        }
    }

    @Override
    public String toString() {
        return getLipidString(super.info.orElse(LipidSpeciesInfo.NONE).getLevel());
    }

}

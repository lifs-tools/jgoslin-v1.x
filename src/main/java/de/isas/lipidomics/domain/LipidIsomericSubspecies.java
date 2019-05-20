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
public class LipidIsomericSubspecies extends LipidStructuralSubspecies {
    
    private final String lipidSpeciesString;

    public LipidIsomericSubspecies(String headGroup, IsomericFattyAcid... fa) {
        super(headGroup);
        int nCarbon = 0;
        int nHydroxyl = 0;
        int nDoubleBonds = 0;
        boolean ether = false;
        for (IsomericFattyAcid fas : fa) {
            if (getFa().containsKey(fas.getName())) {
                throw new ConstraintViolationException(
                        "FA names must be unique! FA with name " + fas.getName() + " was already added!");
            } else {
                getFa().put(fas.getName(), fas);
                nCarbon += fas.getNCarbon();
                nHydroxyl += fas.getNHydroxy();
                nDoubleBonds += fas.getNDoubleBonds();
                ether = ether || fas.isEther();
            }
        }
        super.info = Optional.of(new LipidSpeciesInfo(LipidLevel.STRUCTURAL_SUBSPECIES, nCarbon, nHydroxyl, nDoubleBonds, ether));
        this.lipidSpeciesString = buildLipidStructuralSubspeciesName();
    }

    private String buildLipidStructuralSubspeciesName() {
        List<String> faStrings = new LinkedList<>();
        for (String faKey : getFa().
                keySet()) {
            IsomericFattyAcid fattyAcid = (IsomericFattyAcid) getFa().
                    get(faKey);
            int nDB = 0;
            int nHydroxy = 0;
            int nCarbon = 0;
            nDB += fattyAcid.getNDoubleBonds();
            String dbPos = "";
            List<String> dbPositions = new LinkedList<>();
            for (Integer key : fattyAcid.getDoubleBondPositions().keySet()) {
                dbPositions.add(key + fattyAcid.getDoubleBondPositions().get(key));
            }
            if (!fattyAcid.getDoubleBondPositions().isEmpty()) {
                dbPos = "(" + dbPositions.stream().collect(Collectors.joining(",")) + ")";
            }
            nCarbon += fattyAcid.getNCarbon();
            nHydroxy += fattyAcid.getNHydroxy();
            faStrings.add(nCarbon + ":" + nDB + dbPos + (nHydroxy > 0 ? ";" + nHydroxy : ""));
        }
        return getHeadGroup() + " " + faStrings.stream().collect(Collectors.joining("/"));
    }

    @Override
    public String getLipidString(LipidLevel level) {
        switch(level) {
            case ISOMERIC_SUBSPECIES:
                return lipidSpeciesString;
            case STRUCTURAL_SUBSPECIES:
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

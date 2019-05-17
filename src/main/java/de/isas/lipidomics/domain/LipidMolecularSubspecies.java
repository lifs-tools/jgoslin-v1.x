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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;

/**
 *
 * @author nils.hoffmann
 */
@Data
public class LipidMolecularSubspecies extends LipidSpecies {

    private final Map<String, FattyAcid> fa = new LinkedHashMap<>();
    private final Optional<LipidSpeciesInfo> info;
    private final String lipidSpeciesString;

    public LipidMolecularSubspecies(String headGroup, MolecularFattyAcid... fa) {
        super(headGroup);
        int nCarbon = 0;
        int nHydroxyl = 0;
        int nDoubleBonds = 0;
        boolean ether = false;
        for (MolecularFattyAcid fas : fa) {
            if (fas.getPosition() != -1) {
                throw new IllegalArgumentException("MolecularFattyAcid " + fas.getName() + " must have position set to -1! Was: " + fas.getPosition());
            }
            if (this.fa.containsKey(fas.getName())) {
                throw new IllegalArgumentException(
                        "FA names must be unique! FA with name " + fas.getName() + " was already added!");
            } else {
                this.fa.put(fas.getName(), fas);
                nCarbon += fas.getNCarbon();
                nHydroxyl += fas.getNHydroxy();
                nDoubleBonds += fas.getNDoubleBonds();
                ether = ether || fas.isEther();
            }
        }
        info = Optional.of(new LipidSpeciesInfo(LipidLevel.MOLECULAR_SUBSPECIES, nCarbon, nHydroxyl, nDoubleBonds, ether));
        this.lipidSpeciesString = buildLipidMolecularSubspeciesName();
    }
    
    public Map<String, FattyAcid> getFa() {
        return Collections.unmodifiableMap(fa);
    }

    private String buildLipidMolecularSubspeciesName() {
        List<String> faStrings = new LinkedList<>();
        for (String faKey : getFa().
                keySet()) {
            FattyAcid fattyAcid = getFa().
                    get(faKey);
            int nDB = 0;
            int nHydroxy = 0;
            int nCarbon = 0;
            nDB += fattyAcid.getNDoubleBonds();
            nCarbon += fattyAcid.getNCarbon();
            nHydroxy += fattyAcid.getNHydroxy();
            faStrings.add(nCarbon+":"+nDB+(nHydroxy > 0 ? ";" + nHydroxy : ""));
        }
        return getHeadGroup() + " " + faStrings.stream().collect(Collectors.joining("/"));
    }

    @Override
    public Optional<LipidSpeciesInfo> getInfo() {
        return info;
    }

    @Override
    public String getLipidString() {
        return lipidSpeciesString;
    }

}

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
import lombok.Data;

/**
 *
 * @author nils.hoffmann
 */
@Data
public class LipidMolecularSubspecies extends LipidSpecies {

    protected final Map<String, FattyAcid> fa = new LinkedHashMap<>();
    private final String lipidSpeciesString;

    public LipidMolecularSubspecies(String headGroup, MolecularFattyAcid... fa) {
        super(headGroup);
        int nCarbon = 0;
        int nHydroxyl = 0;
        int nDoubleBonds = 0;
        boolean ether = false;
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
                ether = ether || fas.isEther();
            }
        }
        super.info = Optional.of(new LipidSpeciesInfo(LipidLevel.MOLECULAR_SUBSPECIES, nCarbon, nHydroxyl, nDoubleBonds, ether));
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
            String etherType = "";
            switch (fattyAcid.getEtherFaType()) {
                case ETHER_PLASMANYL:
                    etherType = "a";
                    break;
                case ETHER_PLASMENYL:
                    etherType = "p";
                    break;
                case ESTER:
                case UNDEFINED:
                default:
                    etherType = "";
            }
            faStrings.add(nCarbon + ":" + nDB + (nHydroxy > 0 ? ";" + nHydroxy : "") + etherType);
        }
        return getHeadGroup() + " " + faStrings.stream().collect(Collectors.joining("/"));
    }

    @Override
    public String getLipidString(LipidLevel level) {
        switch(level) {
            case MOLECULAR_SUBSPECIES:
                return lipidSpeciesString;
            case CATEGORY:
            case CLASS:
            case SPECIES:
                return super.getLipidString(level);
            default:
                throw new RuntimeException(getClass().getSimpleName() + " does not know how to create a lipid string for level " + level);
        }
    }

}

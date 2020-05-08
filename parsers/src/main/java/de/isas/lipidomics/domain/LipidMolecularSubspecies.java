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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * A molecular subspecies. Child of LipidSpecies. Individual FAs are known, but
 * neither their sn positions nor double bond positions.
 *
 * Example: Phosphatidylinositol (8:0-8:0) or PI(8:0-8:0)
 *
 * @author nils.hoffmann
 * @see LipidSpecies
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class LipidMolecularSubspecies extends LipidSpecies {

    protected final Map<String, FattyAcid> fa = new LinkedHashMap<>();

    @Builder
    public LipidMolecularSubspecies(HeadGroup headGroup, FattyAcid... fa) {
        super(headGroup);
        int nCarbon = 0;
        int nHydroxyl = 0;
        int nDoubleBonds = 0;
        ModificationsList mods = new ModificationsList();
        for (FattyAcid fas : fa) {
//            if (fas.getPosition() != -1) {
//                throw new ConstraintViolationException("MolecularFattyAcid " + fas.getName() + " must have position set to -1! Was: " + fas.getPosition());
//            }
            if (this.fa.containsKey(fas.getName())) {
                throw new ConstraintViolationException(
                        "FA names must be unique! FA with name " + fas.getName() + " was already added!");
            } else {
                this.fa.put(fas.getName(), fas);
                nCarbon += fas.getNCarbon();
                nHydroxyl += fas.getNHydroxy();
                nDoubleBonds += fas.getNDoubleBonds();
                mods.addAll(fas.getModifications());
            }

        }
        super.info = Optional.of(LipidSpeciesInfo.lipidSpeciesInfoBuilder().
                level(LipidLevel.MOLECULAR_SUBSPECIES).
                name(headGroup.getName()).
                position(-1).
                nCarbon(nCarbon).
                nHydroxy(nHydroxyl).
                nDoubleBonds(nDoubleBonds).
                lipidFaBondType(LipidFaBondType.getLipidFaBondType(headGroup, fa)).
                modifications(mods).
                build()
        );
    }

    @Override
    public Map<String, FattyAcid> getFa() {
        return Collections.unmodifiableMap(fa);
    }

    protected String getHeadGroupSuffix() {
        String hgToFaSep = " ";
        if (isEtherLipid()) {
            hgToFaSep = " O-";
        }
        return hgToFaSep;
    }

    protected StringBuilder buildSubspeciesHeadGroupString(String headGroup, boolean normalizeHeadGroup) {
        StringBuilder lipidString = new StringBuilder();
        lipidString.append(getHeadGroup().getLipidClass().map((lclass) -> {
            switch (lclass) {
                case SE:
                case SE_27_1:
                case SE_27_2:
                case SE_28_2:
                case SE_28_3:
                case SE_29_2:
                case SE_30_2:
                    if (!getFa().isEmpty()) {
                        return (normalizeHeadGroup ? headGroup : getNormalizedHeadGroup()) + getHeadGroupSuffix().trim() + "/";
                    }
            }
            return headGroup + (getFa().isEmpty() ? "" : getHeadGroupSuffix());
        }).orElse(headGroup + (getFa().isEmpty() ? "" : getHeadGroupSuffix())));
        return lipidString;
    }

    protected String buildLipidSubspeciesName(LipidLevel level, String faSeparator, String headGroup, boolean isNormalized) {
        StringBuilder sb = new StringBuilder();
        String faStrings = getFa().values().stream().map((fa) -> {
            return fa.buildSubstructureName(level);
        }).collect(Collectors.joining(faSeparator));
        return sb.append(buildSubspeciesHeadGroupString(headGroup, isNormalized)
        ).append(faStrings).toString();
    }

    @Override
    public String getLipidString(LipidLevel level) {
        return this.getLipidString(level, false);
    }

    @Override
    public String getLipidString(LipidLevel level, boolean normalizeHeadGroup) {
        String headGroup = normalizeHeadGroup ? getNormalizedHeadGroup() : getHeadGroup().getName();
        switch (level) {
            case MOLECULAR_SUBSPECIES:
                return buildLipidSubspeciesName(level, "-", headGroup, normalizeHeadGroup);
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
    public String getNormalizedLipidString() {
        return getLipidString(getInfo().orElse(LipidSpeciesInfo.NONE).getLevel(), true);
    }

    @Override
    public boolean validate() {
        return true;
    }

    @Override
    public String toString() {
        return getLipidString();
    }

}

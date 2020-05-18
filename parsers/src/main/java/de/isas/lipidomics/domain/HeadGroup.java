/*
 * Copyright 2020  nils.hoffmann.
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

import java.util.Optional;
import lombok.Data;

/**
 * This class represents functional head groups of lipids. This is where the
 * association to {@link LipidClass} and {@link LipidCategory} is maintained.
 *
 * @author nils.hoffmann
 */
@Data
public class HeadGroup {

    private final String name;
    private final String rawName;
    private final LipidClass lipidClass;
    private final LipidCategory lipidCategory;

    /**
     * Creates a new head group from the given head group name. Lipid class and
     * category will be looked up from {@link LipidClass#forHeadGroup(java.lang.String)
     * }.
     *
     * @param rawName the lipid head group string.
     */
    public HeadGroup(String rawName) {
        this.rawName = rawName;
        this.name = rawName.trim().replaceAll(" O", "");
        this.lipidClass = LipidClass.forHeadGroup(this.name);
        this.lipidCategory = this.lipidClass.getCategory();
    }

    /**
     * Creates a new head group from the given head group name and optionally a
     * lipid class. The lipid class also determines the category.
     *
     * @param rawName the lipid head gruop string.
     * @param lipidClass the lipid class.
     */
    public HeadGroup(String rawName, Optional<LipidClass> lipidClass) {
        this.rawName = rawName;
        this.name = rawName.trim().replaceAll(" O", "");
        this.lipidClass = lipidClass.orElse(LipidClass.UNDEFINED);
        this.lipidCategory = this.lipidClass.getCategory();
    }

    /**
     * Returns a lipid string representation for the head group of this lipid.
     * This method normalizes the original head group name to the class specific
     * primary alias, if the level and class are known. E.g. TG is normalized to
     * TAG.
     *
     * @return the normalized lipid head group.
     */
    public String getNormalizedName() {
        if (lipidClass == LipidClass.UNDEFINED) {
            return name;
        }
        return lipidClass.getSynonyms().get(0);
    }

    @Override
    public String toString() {
        return this.name;
    }
}

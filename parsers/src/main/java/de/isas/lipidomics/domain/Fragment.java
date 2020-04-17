/*
 * Copyright 2019 nilshoffmann.
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

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A generic lipid fragment. There is currently no further specialization in
 * fragment handling available, since the nomenclature is still not standardized
 * in a way that would allow for generic detection of fragment-specific
 * features.
 *
 * @author nils.hoffmann
 */
@AllArgsConstructor
@Data
public class Fragment {
    private static final class None extends Fragment {

        private None() {
            super("");
        }
    }

    public static final Fragment NONE = new None();

    private String name;
    
    public String getLipidString() {
        return name;
    }
}

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

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Base class for lipid names parsed using the different grammars. This can
 * contain a lipid, an adduct, a sum formula and a fragment.
 *
 * @author nils.hoffmann
 * @see LipidSpecies
 * @see Adduct
 * @see Fragment
 */
@AllArgsConstructor
@Data
public class LipidAdduct {

    private LipidSpecies lipid;
    private Adduct adduct;
    private String sumFormula;
    private Fragment fragment;

}

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

/**
 * The lipid fatty acyl bond types define how the FA is connected to the head
 * group or which variant of a particular bond type it is.
 *
 * @author nils.hoffmann
 */
public enum LipidFaBondType {
    UNDEFINED, ESTER, ETHER_PLASMANYL, ETHER_PLASMENYL, ETHER_UNSPECIFIED;

    public String suffix() {
        switch (this) {
            case ETHER_UNSPECIFIED:
                return "";
            case ETHER_PLASMANYL:
                return "a";
            case ETHER_PLASMENYL:
                return "p";
            case ESTER:
            default:
                return "";
        }
    }

}

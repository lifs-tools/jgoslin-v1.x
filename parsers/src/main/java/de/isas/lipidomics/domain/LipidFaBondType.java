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

    public static LipidFaBondType getLipidFaBondType(HeadGroup headGroup, FattyAcid... fa) {
        LipidFaBondType speciesFaBondType = LipidFaBondType.UNDEFINED;
        if (headGroup.getRawName().endsWith(" O") || headGroup.getRawName().endsWith("-O")) {
            speciesFaBondType = LipidFaBondType.ETHER_UNSPECIFIED;
        }
        LipidFaBondType mostSpecificFaBondType = LipidFaBondType.UNDEFINED;
        for (FattyAcid fas : fa) {
            LipidFaBondType faBondType = fas.getLipidFaBondType();
            switch (mostSpecificFaBondType) {
                case UNDEFINED:
                    switch (faBondType) {
                        case ETHER_UNSPECIFIED:
                        case ETHER_PLASMANYL:
                        case ETHER_PLASMENYL:
                        case ESTER:
                            mostSpecificFaBondType = faBondType;
                            break;
                        default:
                            throw new ConstraintViolationException("Did not except " + faBondType + " in context of FA: " + fas);
                    }
                    break;
                case ESTER:
                case ETHER_UNSPECIFIED:
                    switch (faBondType) {
                        case ETHER_PLASMANYL:
                        case ETHER_PLASMENYL:
                            mostSpecificFaBondType = faBondType;
                            break;
                        case UNDEFINED:
                            throw new ConstraintViolationException("Did not except " + faBondType + " in context of FA: " + fas);
                    }
                    break;
                case ETHER_PLASMANYL:
                case ETHER_PLASMENYL:
                    switch (faBondType) {
                        case ETHER_PLASMANYL:
                        case ETHER_PLASMENYL:
                            throw new ConstraintViolationException("Only one FA can define an ether bond to the head group! Tried to add " + fas.getLipidFaBondType() + " over existing " + mostSpecificFaBondType + " for " + headGroup + " with  bond type " + speciesFaBondType + " and FA: " + fas);
                        case ESTER:
                            //leave as is, ETHER overrules ESTER
                            break;
                        default:
                            throw new ConstraintViolationException("Did not except " + faBondType + " in context of FA: " + fas);
                    }
                    break;
                default:
                    throw new ConstraintViolationException("Unhandled case for" + faBondType + " in context of FA: " + fas);
            }
        }
        switch(speciesFaBondType) {
            case UNDEFINED:
                return mostSpecificFaBondType;
            case ETHER_UNSPECIFIED:
                switch(mostSpecificFaBondType) {
                    case ETHER_PLASMANYL:
                    case ETHER_PLASMENYL:
                    case ETHER_UNSPECIFIED:
                        return mostSpecificFaBondType;
                }
            default:
                return speciesFaBondType;
        }
    }

}

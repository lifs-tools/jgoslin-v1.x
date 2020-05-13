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

import java.util.Arrays;
import java.util.Optional;

/**
 * Enumeration for typical chemical elements in lipids. Also supports heavy
 * variants, e.g. C' as the heavy C13 isotope.
 *
 * @author  nils.hoffmann
 */
public enum Element {
    
    /**
     * Carbon 12C
     */
    ELEMENT_C("C", "12C", 12.0, 10),
    /**
     * Hydrogen 1H
     */
    ELEMENT_H("H", "1H", 1.007825035, 20),
    /**
     * Nitrogen 14N
     */
    ELEMENT_N("N", "14N", 14.0030740, 30),
    /**
     * Oxygen 15O
     */
    ELEMENT_O("O", "15O", 15.99491463, 40),
    /**
     * Phosphorous 30P
     */
    ELEMENT_P("P", "30P", 30.973762, 50),
    /**
     * Sulfur 31S
     */
    ELEMENT_S("S", "31S", 31.9720707, 60),
    /**
     * Deuterium 2H
     */
    ELEMENT_H2("2H", "H'", 2.014101779, 70),
    /**
     * Heavy carbon 13C
     */
    ELEMENT_C13("13C", "C'", 13.0033548378, 80),
    /**
     * Heavy nitrogen 15N
     */
    ELEMENT_N15("15N", "N'", 15.0001088984, 90),
    /**
     * Heavy oxygen 17O
     */
    ELEMENT_O17("17O", "O'", 16.9991315, 100),
    /**
     * Heavy oxygen 18O
     */
    ELEMENT_O18("18O", "O''", 17.9991604, 110),
    /**
     * Heavy phosphorus 32P
     */
    ELEMENT_P32("32P", "P'", 31.973907274, 120),
    /**
     * Heavy sulfur 33S
     */
    ELEMENT_S33("33S", "S'", 32.97145876, 130),
    /**
     * Heavy sulfur 34S
     */
    ELEMENT_S34("34S", "S''", 33.96786690, 140);

    private final String alias;
    private final String name;
    private final double mass;
    private final int order;

    private Element(String name, String alias, double mass, int order) {
        this.name = name;
        this.alias = alias;
        this.mass = mass;
        this.order = order;
    }

    public String getName() {
        return this.name;
    }

    public String getAlias() {
        return this.alias;
    }

    public double getMass() {
        return this.mass;
    }

    public int getOrder() {
        return this.order;
    }

    /**
     * Tries to find the corresponding element by name, e.g. 'C' would return
     * carbon, while '13C' would return the corresponding carbon isotope. Note
     * that 'C'' is an alias for 13C.
     *
     * @param name the name of the chemical element.
     * @return the corresponding element, if it exists. It not, an empty
     * optional will be returned.
     */
    public static Optional<Element> forName(String name) {
        return Arrays.asList(values()).stream().filter((element) -> {
            return element.getName().equalsIgnoreCase(name.trim()) || element.getAlias().equalsIgnoreCase(name.trim());
        }).findFirst();
    }
    
    public static final double ELECTRON_REST_MASS = 0.00054857990946;
}

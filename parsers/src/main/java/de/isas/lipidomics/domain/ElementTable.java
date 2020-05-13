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

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.palinom.sumformula.SumFormulaVisitorParser;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Accounting table for chemical element frequency. This is used to calculate
 * sum formulas and total masses for a given chemical element distribution, e.g.
 * in a lipid.
 *
 * @author  nils.hoffmann
 */
public final class ElementTable extends EnumMap<Element, Integer> {

    public ElementTable(Map<Element, ? extends Integer> m) {
        super(m);
    }

    /**
     * Creates an empty element table.
     */
    public ElementTable() {
        super(Element.class);
    }

    /**
     * Creates the element table from the provided sum formula. If an empty
     * string is passed in this will create an empty table.
     *
     * @param sumFormula the sum formula to parse.
     * @throws ParsingException if the sum formula does not conform with the
     * SumFormula grammar.
     */
    public ElementTable(String sumFormula) throws ParsingException {
        this();
        if (!sumFormula.isEmpty()) {
            SumFormulaVisitorParser parser = new SumFormulaVisitorParser();
            add(parser.parse(sumFormula));
        }
    }

    /**
     * Adds the element counts of the provided table to this one.
     *
     * @param other the table to add to this one.
     * @return a new element table.
     */
    public ElementTable add(ElementTable other) {
        other.entrySet().stream().filter((entry) -> {
            return entry.getValue() != null;
        }).forEach((entry) -> {
            incrementBy(entry.getKey(), entry.getValue());
        });
        return this;
    }

    /**
     * Increment the count of the provided element by one.
     *
     * @param element the element.
     */
    public void increment(Element element) {
        merge(element, 1, Integer::sum);
    }

    /**
     * Increment the count of the provided element by the given number.
     *
     * @param element the element.
     * @param increment the increment for the element.
     */
    public void incrementBy(Element element, Integer increment) {
        merge(element, increment, Integer::sum);
    }

    /**
     * Decrements the current count for element by one.
     *
     * @param element the element to decrement the counts for.
     */
    public void decrement(Element element) {
        incrementBy(element, -1);
    }

    /**
     * Decrement the count of the provided element by the given number.
     *
     * @param element the element.
     * @param decrement the decrement for the element.
     */
    public void decrementBy(Element element, Integer decrement) {
        merge(element, -decrement, Integer::sum);
    }

    /**
     * Negates the count stored in the table. E.g. '5' will become '-5', '-5'
     * would become '5'.
     *
     * @param element the element count to negate.
     */
    public void negate(Element element) {
        Integer count = getOrDefault(element, 0);
        put(element, -1 * count);
    }

    /**
     * Subtracts all element counts in the provided element table from this
     * table.
     *
     * @param elementTable the element table to subtract from this.
     * @return this element table.
     */
    public ElementTable subtract(ElementTable elementTable) {
        elementTable.entrySet().stream().filter((entry) -> {
            return entry.getValue() != null;
        }).forEach((entry) -> {
            decrementBy(entry.getKey(), entry.getValue());
        });
        return this;
    }

    /**
     * Returns the sum formula for all elements in this table.
     *
     * @return the sum formula. Returns an empty string if the table is empty.
     */
    public String getSumFormula() {
        return entrySet().stream().filter((entry) -> {
            return entry.getValue() != null && entry.getValue() > 0;
        }).map((entry) -> {
            return entry.getKey().getName() + ((entry.getValue() > 1) ? entry.getValue() : "");
        }).collect(Collectors.joining());
    }

    /**
     * Returns the individual total mass for the provided element.
     *
     * @param element the element to calculate the total mass for.
     * @return the total mass for the given element, or 0.
     */
    public Double getMass(Element element) {
        Integer count = getOrDefault(element, 0);
        if (count > 0) {
            return count.doubleValue() * element.getMass();
        }
        return 0.0d;
    }

    /**
     * Returns the total summed mass per number of elements.
     *
     * @return the total summed mass for this element table. Returns 0 if the
     * table is empty.
     */
    public Double getMass() {
        return keySet().stream().map((key) -> {
            return getMass(key);
        }).reduce(0.0d, Double::sum);
    }

    /**
     * Returns an copy of all mappings in this table.
     *
     * @return the element table copy.
     */
    public ElementTable copy() {
        return new ElementTable(this);
    }

}

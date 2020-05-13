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
package de.isas.lipidomics.palinom.sumformula;

import de.isas.lipidomics.domain.Element;
import de.isas.lipidomics.domain.ElementTable;
import de.isas.lipidomics.palinom.SumFormulaBaseVisitor;
import de.isas.lipidomics.palinom.SumFormulaParser;
import java.util.Optional;

/**
 *
 * Base visitor implementation for the SumFormula grammar.
 *
 * Overriding implementation of {@link SumFormulaBaseVisitor}. Creates
 * {@link ElementTable} instances from the provided context.
 *
 * @see SumFormulaVisitorParser
 * @author nils.hoffmann
 */
public class SumFormulaVisitorImpl extends SumFormulaBaseVisitor<ElementTable> {

    @Override
    public ElementTable visitMolecule(SumFormulaParser.MoleculeContext ctx) {
        if (ctx.molecule_rule() != null) {
            SumFormulaParser.Molecule_groupContext moleculeGroup = ctx.molecule_rule().molecule_group();
            return visitMoleculeGroup(moleculeGroup, new ElementTable());
        }
        return new ElementTable();
    }

    private ElementTable visitMoleculeGroup(SumFormulaParser.Molecule_groupContext moleculeGroup, ElementTable table) {
        if (moleculeGroup.element_group() != null) {
            visitElementGroup(moleculeGroup.element_group(), table);
        } else if (moleculeGroup.single_element() != null) {
            visitSingleElement(moleculeGroup.single_element(), table);
        } else if (moleculeGroup.molecule_group() != null) {
            moleculeGroup.molecule_group().stream().forEach((moleculeGroupCtx) -> {
                visitMoleculeGroup(moleculeGroupCtx, table);
            });
        }
        return table;
    }

    private ElementTable visitSingleElement(SumFormulaParser.Single_elementContext singleElement, ElementTable table) {
        Optional<Element> element = Element.forName(singleElement.element().getText());
        if (element.isPresent()) {
            table.increment(element.get());
        }
        return table;
    }

    private ElementTable visitElementGroup(SumFormulaParser.Element_groupContext elementGroup, ElementTable table) {
        Integer count = 0;
        if (elementGroup.count() == null) {
            count = 1;
        } else {
            count = Integer.parseInt(elementGroup.count().getText());
        }
        Optional<Element> element = Element.forName(elementGroup.element().getText());
        if (element.isPresent()) {
            table.incrementBy(element.get(), count);
        }
        return table;
    }
}

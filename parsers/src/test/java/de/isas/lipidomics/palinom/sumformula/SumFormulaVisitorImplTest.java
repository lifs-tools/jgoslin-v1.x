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
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 *
 * @author  nils.hoffmann
 */
public class SumFormulaVisitorImplTest {
    
    @Test
    public void testSumFormulaParsing() throws ParsingException {
        SumFormulaVisitorParser sfp = new SumFormulaVisitorParser();
        ElementTable elementTable = sfp.parse("C41H83N2O6P");
        assertEquals(41, elementTable.get(Element.ELEMENT_C));
        assertEquals(83, elementTable.get(Element.ELEMENT_H));
        assertEquals(2, elementTable.get(Element.ELEMENT_N));
        assertEquals(6, elementTable.get(Element.ELEMENT_O));
        assertEquals(1, elementTable.get(Element.ELEMENT_P));
    }
}

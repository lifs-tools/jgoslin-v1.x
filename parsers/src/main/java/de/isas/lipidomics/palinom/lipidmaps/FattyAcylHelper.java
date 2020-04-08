/*
 * Copyright 2020 nilshoffmann.
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
package de.isas.lipidomics.palinom.lipidmaps;

import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.palinom.LipidMapsParser;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Optional;

/**
 *
 * @author nilshoffmann
 */
public class FattyAcylHelper {

    public LipidFaBondType getLipidLcbBondType(LipidMapsParser.LcbContext lcbContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        return lfbt;
    }

    public LipidFaBondType getLipidFaBondType(LipidMapsParser.FaContext faContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        if (faContext.fa_unmod().ether() != null) {
            if (null == faContext.fa_unmod().ether().getText()) {
                throw new ParseTreeVisitorException("Undefined ether context value!");
            } else {
                switch (faContext.fa_unmod().ether().getText()) {
                    case "O-":
                        lfbt = LipidFaBondType.ETHER_PLASMANYL;
                        break;
                    case "P-":
                        lfbt = LipidFaBondType.ETHER_PLASMENYL;
                        break;
                    default:
                        throw new ParseTreeVisitorException("Unknown ether context value: " + faContext.fa_unmod().ether());
                }
            }
        }
        return lfbt;
    }

    public Integer getHydroxyCount(LipidMapsParser.LcbContext ctx) {
        Integer nHydroxy = Optional.ofNullable(ctx.hydroxyl_lcb()).map((t) -> {
            String hydroxy = t.getText();
            switch (hydroxy) {
                case "m":
                    return 1;
                case "d":
                    return 2;
                case "t":
                    return 3;
            }
            return Integer.valueOf(0);
        }).orElse(0);
        return nHydroxy;
    }
}

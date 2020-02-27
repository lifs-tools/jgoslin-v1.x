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
package de.isas.lipidomics.palinom.swisslipids;

import de.isas.lipidomics.domain.LipidFaBondType;
import de.isas.lipidomics.palinom.exceptions.ParseTreeVisitorException;
import java.util.Optional;
import java.util.function.Function;
import org.antlr.v4.runtime.tree.RuleNode;

/**
 *
 * @author nilshoffmann
 */
public class FaHelperFunctions {

    public LipidFaBondType getLipidFaBondType(SwissLipidsParser.FaContext faContext) throws ParseTreeVisitorException {
        LipidFaBondType lfbt = LipidFaBondType.ESTER;
        if (faContext.fa_core() != null && faContext.fa_core().ether() != null) {
            if ("O-".equals(faContext.fa_core().ether().getText())) {
                lfbt = LipidFaBondType.ETHER_PLASMANYL;
            } else if ("P-".equals(faContext.fa_core().ether().getText())) {
                lfbt = LipidFaBondType.ETHER_PLASMENYL;
            } else {
                throw new ParseTreeVisitorException("Unknown ether context value: " + faContext.fa_core().ether());
            }
        }
        return lfbt;
    }
    
    public <T extends RuleNode> Integer asInt(T context, Integer defaultValue) {
        return maybeMapOr(context, (t) -> {
            return Integer.parseInt(t.getText());
        }, defaultValue);
    }
    
    public static <T> Optional<T> maybe(T t) {
        return Optional.ofNullable(t);
    }

    public static <T, R> R maybeMapOr(T t, Function<? super T, R> mapper, R r) {
        return maybe(t).map(mapper).orElse(r);
    }
}

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
package de.isas.lipidomics.palinom;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Generic base class for context handler implementations for the different
 * grammar parsers.
 *
 * @author nilshoffmann
 * @param <T> the generic {@link ParserRuleContext}
 * @param <U> the type to return after handling.
 */
public interface ParserRuleContextHandler<T extends ParserRuleContext, U> {

    U handle(T t);
}

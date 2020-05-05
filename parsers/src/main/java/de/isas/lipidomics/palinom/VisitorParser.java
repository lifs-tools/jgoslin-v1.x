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

import de.isas.lipidomics.palinom.exceptions.ParsingException;

/**
 * Base interface for grammar-specific parser implementations based on the
 * ANTLRv4 generated parsers.
 *
 * @author nilshoffmann
 * @param <T> the type of the visitor parser
 */
public interface VisitorParser<T> {

    T parse(String lipidString, SyntaxErrorListener listener) throws ParsingException;

    default T parse(String lipidString) throws ParsingException {
        return parse(lipidString, new SyntaxErrorListener());
    }

}

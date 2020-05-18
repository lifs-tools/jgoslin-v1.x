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
package de.isas.lipidomics.palinom;

import java.util.Optional;
import java.util.function.Function;
import org.antlr.v4.runtime.tree.RuleNode;

/**
 * Common functions for ANTLRv4's RuleNode handling.
 *
 * @author nils.hoffmann
 */
public class HandlerUtils {

    /**
     * Parse the provided context's text value as an Integer or use default
     * value.
     *
     * @param <T> the type of the context.
     * @param context the context.
     * @param defaultValue the default value.
     * @return the context's text value as an integer, or the default value.
     */
    public static <T extends RuleNode> Integer asInt(T context, Integer defaultValue) {
        return maybeMapOr(context, (t) -> {
            return Integer.parseInt(t.getText());
        }, defaultValue);
    }

    /**
     * Wrap t as an optional of nullable.
     *
     * @param <T> the type of t.
     * @param t the argument.
     * @return an optional.
     */
    public static <T> Optional<T> maybe(T t) {
        return Optional.ofNullable(t);
    }

    /**
     * Wrap t as an optional and return r if t is null or empty.
     *
     * @param <T> the type of t.
     * @param <R> the type of the return value r.
     * @param t the argument to wrap.
     * @param mapper the mapping function to get from t to r, if t is not null.
     * @param r the default return value.
     * @return the mapped value for t of type R, or the default value r.
     */
    public static <T, R> R maybeMapOr(T t, Function<? super T, R> mapper, R r) {
        return maybe(t).map(mapper).orElse(r);
    }

}

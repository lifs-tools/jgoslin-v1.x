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

import java.util.Optional;
import java.util.function.Function;
import org.antlr.v4.runtime.tree.RuleNode;

/**
 *
 * @author nilshoffmann
 */
public class HandlerUtils {

    public static <T extends RuleNode> Optional<T> asCtx(T context, Class<? extends T> tclass) {
        if (context == null) {
            return Optional.empty();
        }
        if (tclass.isAssignableFrom(context.getClass())) {
            return Optional.of(tclass.cast(context));
        }
        return Optional.empty();
    }

    public static <T extends RuleNode> Integer asInt(T context, Integer defaultValue) {
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

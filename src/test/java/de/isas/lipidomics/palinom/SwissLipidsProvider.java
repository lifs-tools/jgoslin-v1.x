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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 *
 * @author nilshoffmann
 */
public class SwissLipidsProvider {

    public static Object[] provideCurrentSwissLipidsNames() throws IOException {
        URL u = SwissLipidsProvider.class.getClassLoader().getResource("de/isas/lipidomics/palinom/swisslipids-names-Feb-10-2020.tsv");
        AtomicInteger maxLines = new AtomicInteger(122215);
        AtomicInteger line = new AtomicInteger(0);
        try (InputStreamReader ir = new InputStreamReader(u.openStream())) {
            try (BufferedReader br = new BufferedReader(ir)) {
                List<String> result = br.lines().limit(maxLines.get()).map((t) -> {
                    System.out.println("Line "+line.get());
                    if(line.get()>0) {
                        String[] lineElements = t.toString().split("\t");
                        line.incrementAndGet();
                        return t.isEmpty()||lineElements.length<=3?"":lineElements[3].replaceAll("\\("," ").replaceAll("\\)", "");
                    }
                    line.incrementAndGet();
                    return "";
                }).collect(Collectors.toList());
                return result.toArray();
            }
        }
    }

}

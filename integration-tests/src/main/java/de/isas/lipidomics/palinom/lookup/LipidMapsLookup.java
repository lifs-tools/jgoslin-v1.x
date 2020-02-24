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
package de.isas.lipidomics.palinom.lookup;

import de.isas.lipidomics.domain.ExternalDatabaseReference;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.palinom.GoslinVisitorParser;
import de.isas.lipidomics.palinom.LipidMapsVisitorParser;
import de.isas.lipidomics.palinom.exceptions.PalinomVisitorException;
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author nilshoffmann
 */
@Slf4j
public class LipidMapsLookup {

    public static void main(String[] args) throws IOException {
        URL lipidMapsTable = LipidMapsLookup.class.getClassLoader().getResource("lipidmaps-names-Feb-10-2020.tsv");
        try (InputStream is = lipidMapsTable.openStream()) {
            try (InputStreamReader isr = new InputStreamReader(is, "UTF-8")) {
                try (BufferedReader br = new BufferedReader(isr)) {
                    File outputFile = new File("lipidmaps-normalized.tsv");
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
                        // SLID	LEVEL	NAME	ABBREVIATION	SYNONYMS1	SYNONYMS2	SYNONYMS3	SYNONYMS4	SYNONYMS5
                        // LM_ID	NAME	SYSTEMATIC_NAME	ABBREVIATION	CATEGORY	MAIN_CLASS	SUB_CLASS
                        bw.write("databaseUrl\tdatabaseElementId\tlipidLevel\tnativeAbbreviation\tnativeName\tnormalizedName");
                        bw.newLine();
                        br.lines().skip(1).map((lipidMapsEntry) -> {
                            String[] elements = lipidMapsEntry.split("\t", -1);
                            System.out.println(Arrays.deepToString(elements));
                            return new ExternalDatabaseReference(
                                    "https://www.lipidmaps.org/data/LMSDRecord.php?LMID=",
                                    elements[0],
                                    String.join(" | ", elements[4], elements[5], elements[6]),
                                    elements[3],
                                    elements[1],
                                    parseAbbreviation(elements[3])
                            );
                        }).forEach((edr) -> {
                            StringBuilder sb = new StringBuilder();
                            sb.
                                    append(edr.getDatabaseUrl()).append("\t").
                                    append(edr.getDatabaseElementId()).append("\t").
                                    append(edr.getLipidLevel()).append("\t").
                                    append(edr.getNativeName()).append("\t").
                                    append(edr.getNativeAbbreviation()).append("\t").
                                    append(edr.getNormalizedName()).append("\t");
                            try {
                                bw.write(sb.toString());
                                bw.newLine();
                            } catch (IOException ex) {
                                log.error("Exception while writing external database reference " + edr + " to file " + outputFile, ex);
                            }
                        });
                    }
                }
            }
        }
    }

    public static String parseAbbreviation(String abbreviation) {
        LipidMapsVisitorParser parser = new LipidMapsVisitorParser();
        String result;
        try {
            LipidAdduct la = parser.parse(abbreviation);
            result = la.getLipid().getLipidString(la.getLipid().getInfo().get().getLevel());
        } catch (ParsingException ex) {
            log.error("Exception while parsing " + abbreviation, ex);
            result = "N.D.";
        } catch (PalinomVisitorException pve) {
            log.error("Exception in LipidMapsVisitorParser " + abbreviation, pve);
            result = "N.I.";
        }
        return result;
    }
}

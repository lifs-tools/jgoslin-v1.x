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
package de.isas.lipidomics.palinom.cli;

import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.LipidAdduct;
import de.isas.lipidomics.domain.LipidClass;
import de.isas.lipidomics.domain.LipidIsomericSubspecies;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.palinom.SyntaxErrorListener;
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import de.isas.lipidomics.palinom.goslin.GoslinVisitorParser;
import de.isas.lipidomics.palinom.lipidmaps.LipidMapsVisitorParser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author nils.hoffmann
 */
@Slf4j
public class CmdLineParser {

    public static final String LIPIDMAPS_CLASS_REGEXP = ".+\\[([A-Z0-9]+)\\]";

    private static String getAppInfo() throws IOException {
        Properties p = new Properties();
        p.load(CmdLineParser.class.getResourceAsStream(
                "/application.properties"));
        StringBuilder sb = new StringBuilder();
        String buildDate = p.getProperty("app.build.date", "no build date");
        if (!"no build date".equals(buildDate)) {
            Instant instant = Instant.ofEpochMilli(Long.parseLong(buildDate));
            buildDate = instant.toString();
        }
        /*
         *Property keys are in src/main/resources/application.properties
         */
        sb.append("Running ").
                append(p.getProperty("app.name", "undefined app")).
                append("\n\r").
                append(" version: '").
                append(p.getProperty("app.version", "unknown version")).
                append("'").
                append("\n\r").
                append(" build-date: '").
                append(buildDate).
                append("'").
                append("\n\r").
                append(" scm-location: '").
                append(p.getProperty("scm.location", "no scm location")).
                append("'").
                append("\n\r").
                append(" commit: '").
                append(p.getProperty("scm.commit.id", "no commit id")).
                append("'").
                append("\n\r").
                append(" branch: '").
                append(p.getProperty("scm.branch", "no branch")).
                append("'").
                append("\n\r");
        return sb.toString();
    }

    /**
     * <p>
     * Runs the command line parser for jgoslin, including validation.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     * @throws java.lang.Exception if any.
     */
    @SuppressWarnings("static-access")
    public static void main(String[] args) throws Exception {
        CommandLineParser parser = new PosixParser();
        Options options = new Options();
        String helpOpt = addHelpOption(options);
        String versionOpt = addVersionOption(options);
        String lipidNameOpt = addLipidNameInputOption(options);
        String lipidFileOpt = addLipidFileInputOption(options);
        String outputToFileOpt = addOutputToFileOption(options);

        CommandLine line = parser.parse(options, args);
        if (line.getOptions().length == 0 || line.hasOption(helpOpt)) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("jgoslin-cli", options);
        } else if (line.hasOption(versionOpt)) {
            log.info(getAppInfo());
        } else {
            boolean toFile = false;
            if (line.hasOption(outputToFileOpt)) {
                toFile = true;
            }
            Stream<String> lipidNames = Stream.empty();
            if (line.hasOption(lipidNameOpt)) {
                lipidNames = Stream.of(line.getOptionValues(lipidNameOpt));
            } else if (line.hasOption(lipidFileOpt)) {
                lipidNames = Files.lines(new File(lipidFileOpt).toPath());
            }
            if(toFile) {
                log.info("Saving output to 'goslin-out.tsv'.");
                boolean successful = writeToFile(new File("goslin-out.tsv"), parseNames(lipidNames));
                if(!successful) {
                    System.exit(1);
                }
            } else {
                log.info("Echoing output to stdout.");
                boolean successful = writeToStdOut(parseNames(lipidNames));
                if(!successful) {
                    System.exit(1);
                }
            }
        }
    }

    @Data
    private static class ValidationResult {

        public static enum Grammar {
            GOSLIN, GOSLIN_FRAGMENTS, LIPIDMAPS, SWISSLIPIDS
        };

        private String lipidName;

        private Grammar grammar;
        
        private LipidLevel level;

        private List<String> messages = Collections.emptyList();

        private LipidAdduct lipidAdduct;

        private LipidSpeciesInfo lipidSpeciesInfo;

        private String goslinName;

        private String lipidMapsCategory;

        private String lipidMapsClass;

        private Map<String, FattyAcid> fattyAcids = Collections.emptyMap();

    }

    protected static boolean writeToStdOut(List<Pair<String, List<ValidationResult>>> results) {
        
        try(StringWriter sw = new StringWriter()) {
            writeToWriter(new BufferedWriter(sw), results);
            sw.flush();
            sw.close();
            System.out.println(sw.toString());
            return true;
        } catch (IOException ex) {
            log.error("Caught exception while trying to write validation results string!", ex);
            return false;
        }
    }
    
    protected static boolean writeToFile(File f, List<Pair<String, List<ValidationResult>>> results) {
        
        try (BufferedWriter bw = Files.newBufferedWriter(f.toPath())) {
            writeToWriter(bw, results);
            return true;
        } catch (IOException ex) {
            log.error("Caught exception while trying to write validation results to file " + f, ex);
            return false;
        }
    }
    
    protected static void writeToWriter(BufferedWriter bw, List<Pair<String, List<ValidationResult>>> results) {
        String header = "ORIGINAL_NAME\tNORMALIZED_NAME\tGRAMMAR\tLEVEL\tLM_CATEGORY\tLM_CLASS\tFAS\tMESSAGES";
        try {
            bw.write(header);
            bw.newLine();
            //LIPIDNAME\tGRAMMAR\tLEVEL\tCATEGORY\tCLASS\tFAS\tMESSAGES
            results.stream().forEach((pair) -> {
                Pair<String, List<ValidationResult>> resultPair = pair;
                resultPair.getValue().stream().forEach((validationResult) -> {
                    StringBuilder rowBuilder = new StringBuilder();
                    rowBuilder.append(validationResult.getLipidName()).append("\t");
                    rowBuilder.append(validationResult.getGoslinName()).append("\t");
                    rowBuilder.append(validationResult.getGrammar()).append("\t");
                    rowBuilder.append(validationResult.getLevel()).append("\t");
                    rowBuilder.append(validationResult.getLipidMapsCategory()).append("\t");
                    rowBuilder.append(validationResult.getLipidMapsClass()).append("\t");
                    rowBuilder.append("").append("\t");
                    rowBuilder.append(validationResult.getMessages().stream().collect(Collectors.joining("\\|")));
                    try {
                        bw.write(rowBuilder.toString());
                        bw.newLine();
                    } catch (IOException ex) {
                        log.error("Caught exception while trying to write validation results to buffered writer.", ex);
                    }
                });
            });
        } catch (IOException ex) {
            log.error("Caught exception while trying to write validation results to buffered writer.", ex);
        }
    }

    protected static List<Pair<String, List<ValidationResult>>> parseNames(Stream<String> lipidNames) {
        return lipidNames.map((t) -> {
            return parseName(t);
        }).collect(Collectors.toList());
    }

    protected static Pair<String, List<ValidationResult>> parseName(String lipidName) {
        List<ValidationResult> results = new ArrayList<>();
        GoslinVisitorParser parser = new GoslinVisitorParser();
        SyntaxErrorListener listener = new SyntaxErrorListener();
        ValidationResult goslinResult = new ValidationResult();
        try {
            LipidAdduct la = parser.parse(lipidName, listener);
            goslinResult.setLipidName(lipidName);
            goslinResult.setLipidAdduct(la);
            goslinResult.setGrammar(ValidationResult.Grammar.GOSLIN);
            goslinResult.setMessages(toStringMessages(listener));
            goslinResult.setLipidMapsCategory(la.getLipid().getLipidCategory().name());
            goslinResult.setLipidMapsClass(getLipidMapsClassAbbreviation(la));
            goslinResult.setLipidSpeciesInfo(la.getLipid().getInfo().orElse(LipidSpeciesInfo.NONE));
            try {
                String normalizedName = la.getLipid().getLipidString();
                goslinResult.setGoslinName(normalizedName);
            } catch (RuntimeException re) {
                log.warn("Parsing error for {}!", lipidName);
            }
            extractFas(la, goslinResult);
        } catch (ParsingException ex) {
            goslinResult.setLipidName(lipidName);
            goslinResult.setMessages(toStringMessages(listener));
            log.warn("Caught exception while parsing " + lipidName + " with Goslin grammar: ", ex);

        }
        results.add(goslinResult);
        SyntaxErrorListener lmListener = new SyntaxErrorListener();
        ValidationResult lmResult = new ValidationResult();
        try {
            LipidMapsVisitorParser lmParser = new LipidMapsVisitorParser();
            LipidAdduct lma = lmParser.parse(lipidName, lmListener);
            lmResult.setLipidName(lipidName);
            lmResult.setLipidAdduct(lma);
            lmResult.setGrammar(ValidationResult.Grammar.LIPIDMAPS);
            lmResult.setLevel(lma.getLipid().getInfo().orElse(LipidSpeciesInfo.NONE).getLevel());
            lmResult.setMessages(toStringMessages(lmListener));
            lmResult.setLipidMapsCategory(lma.getLipid().getLipidCategory().name());
            lmResult.setLipidMapsClass(getLipidMapsClassAbbreviation(lma));
            lmResult.setLipidSpeciesInfo(lma.getLipid().getInfo().orElse(LipidSpeciesInfo.NONE));
            try {
                String normalizedName = lma.getLipid().getLipidString();
                lmResult.setGoslinName(normalizedName);
            } catch (RuntimeException re) {
                log.warn("Parsing error for {}!", lipidName);
            }
            extractFas(lma, lmResult);
        } catch (ParsingException ex1) {
            log.warn("Caught exception while parsing " + lipidName + " with LipidMaps grammar: ", ex1);
//            ValidationResult result = new ValidationResult();
            lmResult.setLipidName(lipidName);
            lmResult.setMessages(toStringMessages(lmListener));
        }
        results.add(lmResult);
        return Pair.of(lipidName, results);
    }

    private static void extractFas(LipidAdduct la, ValidationResult result) {
        switch (la.getLipid().getInfo().orElse(LipidSpeciesInfo.NONE).getLevel()) {
            case MOLECULAR_SUBSPECIES:
                LipidMolecularSubspecies lms = (LipidMolecularSubspecies) la.getLipid();
                result.setFattyAcids(lms.getFa());
                break;
            case STRUCTURAL_SUBSPECIES:
                LipidStructuralSubspecies lss = (LipidStructuralSubspecies) la.getLipid();
                result.setFattyAcids(lss.getFa());
                break;
            case ISOMERIC_SUBSPECIES:
                LipidIsomericSubspecies lis = (LipidIsomericSubspecies) la.getLipid();
                result.setFattyAcids(lis.getFa());
                break;
            default:
        }
    }

    private static List<String> toStringMessages(SyntaxErrorListener listener) {
        return listener.getSyntaxErrors().stream().map((syntaxError) -> {
            return syntaxError.getMessage();
        }).collect(Collectors.toList());
    }

    private static String getLipidMapsClassAbbreviation(LipidAdduct la) {
        String lipidMapsClass = la.getLipid().getLipidClass().orElse(LipidClass.UNDEFINED).getLipidMapsClassName();
        Pattern lmcRegexp = Pattern.compile(LIPIDMAPS_CLASS_REGEXP);
        Matcher lmcMatcher = lmcRegexp.matcher(lipidMapsClass);
        if (lmcMatcher.matches() && lmcMatcher.groupCount() == 1) {
            lipidMapsClass = lmcMatcher.group(1);
        } else {
            lipidMapsClass = null;
        }
        return lipidMapsClass;
    }

    protected static String addLipidFileInputOption(Options options) {
        String versionOpt = "file";
        options.addOption("f", versionOpt, true, "Input a file name to read from for lipid name for parsing. Each lipid name must be on a separate line.");
        return versionOpt;
    }

    protected static String addLipidNameInputOption(Options options) {
        String versionOpt = "name";
        options.addOption("n", versionOpt, true, "Input a lipid name for parsing.");
        return versionOpt;
    }

    protected static String addVersionOption(Options options) {
        String versionOpt = "version";
        options.addOption("v", versionOpt, false, "Print version information.");
        return versionOpt;
    }

    protected static String addHelpOption(Options options) {
        String helpOpt = "help";
        options.addOption("h", helpOpt, false, "Print help message.");
        return helpOpt;
    }
    
    protected static String addOutputToFileOption(Options options) {
        String outputToFileOpt = "outputFile";
        options.addOption("o", outputToFileOpt, false, "Write output to file 'goslin-out.tsv' instead of to std out.");
        return outputToFileOpt;
    }
}

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
package de.isas.lipidomics.generator;

import com.opencsv.CSVReader;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import de.isas.lipidomics.domain.LipidCategory;
import de.isas.lipidomics.domain.LipidClass;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.lang.model.element.Modifier;
import lombok.Value;

/**
 *
 * @author nilshoffmann
 */
public class LipidClassGenerator {

    public static void main(String... args) {
        LipidClassGenerator gne = new LipidClassGenerator();
        try {
            System.out.println(gne.getEnumFromTable(gne.getEnumEntries()));
        } catch (IOException ex) {
            Logger.getLogger(LipidClassGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Value
    public static class LipidClassEntry {

        private final String lipidName;
        private final String lipidCategory;
        private final String lipidDescription;
        private final Integer maxNumFa;
        private final String allowedNumFa;
        private final List<String> synonyms;
    }

    public Stream<LipidClassEntry> getEnumEntries() throws IOException {
        try (Reader reader = Files.newBufferedReader(Paths.get(ClassLoader.getSystemResource("lipid-list.csv").toURI()))) {;
            CSVReader csvReader = new CSVReader(reader);
            csvReader.skip(1);
            Iterator<String[]> iter = csvReader.iterator();
            List<LipidClassEntry> entries = new ArrayList<>();
            while (iter.hasNext()) {
                String[] s = iter.next();
                int len = s.length;
                List<String> synonyms = new ArrayList<>();
                for (int i = 5; i < len; i++) {
                    if (!s[i].trim().isEmpty()) {
                        synonyms.add(s[i]);
                    }
                }
                LipidClassEntry entry = new LipidClassEntry(
                        s[0],
                        s[1],
                        s[2],
                        Integer.parseInt(s[3]),
                        s[4],
                        synonyms
                );
                System.out.println("Entry: " + entry);
                entries.add(entry);
            }
            return entries.stream();
        } catch (URISyntaxException ex) {
            Logger.getLogger(LipidClassGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Stream.empty();
    }

    public String sanitizeToEnumConstant(String lipidName, String lipidCategory) {
        return lipidName.replaceAll("[/\\-\\s(),.\\[\\]:;]", "_").replaceAll("'", "p").replaceAll("^([_0-9]+)", lipidCategory + "_$1").replaceAll("[_]+", "_").toUpperCase();
    }

    public String getEnumFromTable(Stream<LipidClassEntry> stream) {
        final Builder lipidClassBuilder = TypeSpec.enumBuilder("LipidClass").addJavadoc(
                "Enumeration of lipid classes. The shorthand names / abbreviations are used to\n"
                + "look up the lipid class association of a lipid head group. We try to map each\n"
                + "abbreviation and synonyms thereof to LipidMAPS main class. However, not all\n"
                + "described head groups are categorized in LipidMAPS, or only occur in other\n"
                + "databases, so they do not have such an association at the moment.\n"
                + "\n"
                + "Example: Category=Glyerophospholipids -> Class=Glycerophosphoinositols (PI)\n"
                + "\n"
                + "@author nils.hoffmann"
        ).
                addModifiers(Modifier.PUBLIC);
        lipidClassBuilder.addField(LipidCategory.class, "category", Modifier.PRIVATE, Modifier.FINAL);
        lipidClassBuilder.addField(String.class, "lipidMapsClassName", Modifier.PRIVATE, Modifier.FINAL);
        ClassName synonymsClass = ClassName.get("java.lang", "String");
        ClassName integersClass = ClassName.get("java.lang", "Integer");
        ClassName list = ClassName.get("java.util", "List");
        lipidClassBuilder.addField(String.class, "allowedNumFaStr", Modifier.PRIVATE, Modifier.FINAL);
        TypeName listOfIntegers = ParameterizedTypeName.get(list, integersClass);
        lipidClassBuilder.addField(listOfIntegers, "allowedNumFa", Modifier.PRIVATE, Modifier.FINAL);
        lipidClassBuilder.addField(Integer.class, "maxNumFa", Modifier.PRIVATE, Modifier.FINAL);
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfSynonyms = ParameterizedTypeName.get(list, synonymsClass);
        lipidClassBuilder.addField(listOfSynonyms, "synonyms", Modifier.PRIVATE, Modifier.FINAL);
        TypeName optionalLipidClass = ParameterizedTypeName.get(Optional.class, LipidClass.class);

//            private LipidClass(LipidCategory category, String lipidMapsClassName, String... synonyms) {
//        this.category = category;
//        this.lipidMapsClassName = lipidMapsClassName;
//        if (synonyms.length == 0) {
//            throw new IllegalArgumentException("Must supply at least one synonym!");
//        }
//        this.synonyms = Arrays.asList(synonyms);
//    }
        lipidClassBuilder.addMethod(
                MethodSpec.constructorBuilder().
                        addModifiers(Modifier.PRIVATE).
                        addParameter(LipidCategory.class, "category").
                        addStatement("this.$N = $N", "category", "category").
                        addParameter(String.class, "lipidMapsClassName").
                        addStatement("this.$N = $N", "lipidMapsClassName", "lipidMapsClassName").
                        addParameter(Integer.class, "maxNumFa").
                        addStatement("this.$N = $N", "maxNumFa", "maxNumFa").
                        addParameter(String.class, "allowedNumFaStr").
                        addStatement("this.$N = $N", "allowedNumFaStr", "allowedNumFaStr").
                        addParameter(String[].class, "synonyms").
                        addStatement(
                                CodeBlock.of(
                                        "if ($N.length == 0) {\n"
                                        + " throw new IllegalArgumentException(\"Must supply at least one synonym!\");\n"
                                        + "}", "synonyms")
                        ).
                        addStatement(
                                CodeBlock.of(
                                        "this.$N = Arrays.asList(allowedNumFaStr.split(\"\\\\|\")).stream().map((t) -> {\n"
                                        + "   return Integer.parseInt(t);\n"
                                        + "}).collect(java.util.stream.Collectors.toList())", "allowedNumFa")
                        ).
                        addStatement("this.$N = Arrays.asList($N)", "synonyms", "synonyms").
                        varargs(true).
                        build()
        );

        lipidClassBuilder.addMethod(
                MethodSpec.methodBuilder("getCategory").addModifiers(Modifier.PUBLIC).returns(LipidCategory.class).addCode("return this.$N;", "category").build()
        );
        lipidClassBuilder.addMethod(
                MethodSpec.methodBuilder("getAbbreviation").addModifiers(Modifier.PUBLIC).returns(String.class).addCode("return this.$N.get(0);", "synonyms").build()
        );
        lipidClassBuilder.addMethod(
                MethodSpec.methodBuilder("getLipidMapsClassName").addModifiers(Modifier.PUBLIC).returns(String.class).addCode("return this.$N;", "lipidMapsClassName").build()
        );
        lipidClassBuilder.addMethod(
                MethodSpec.methodBuilder("getMaxNumFa").addModifiers(Modifier.PUBLIC).returns(Integer.class).addCode("return this.$N;", "maxNumFa").build()
        );
        lipidClassBuilder.addMethod(
                MethodSpec.methodBuilder("getAllowedNumFa").addModifiers(Modifier.PUBLIC).returns(listOfIntegers).addCode("return this.$N;", "allowedNumFa").build()
        );
        lipidClassBuilder.addMethod(
                MethodSpec.methodBuilder("getSynonyms").addModifiers(Modifier.PUBLIC).returns(listOfSynonyms).addCode("return this.$N;", "synonyms").build()
        );

        lipidClassBuilder.addMethod(
                MethodSpec.methodBuilder("matchesAbbreviation").
                        addModifiers(Modifier.PUBLIC).
                        addParameter(String.class, "headGroup").
                        addStatement(
                                CodeBlock.of(
                                        "return this.synonyms.stream().anyMatch((synonym) -> {\n"
                                        + "return synonym.equals($N);\n"
                                        + "})", "headGroup")).
                        returns(boolean.class).
                        build()
        );

        lipidClassBuilder.addMethod(
                MethodSpec.methodBuilder("getLysoAbbreviation").
                        addModifiers(Modifier.PUBLIC).
                        addParameter(LipidClass.class, "lipidClass").
                        addStatement(
                                CodeBlock.of(
                                        "if ($N.getCategory() == LipidCategory.GP) { "
                                        + "return \"L\" + $N.getAbbreviation();\n"
                                        + "}\n"
                                        + "throw new ConstraintViolationException(\"Lipid category must be \" + LipidCategory.GP + \" for lyso-classes!\")", "lipidClass", "lipidClass")).
                        returns(String.class).
                        build()
        );

        lipidClassBuilder.addMethod(
                MethodSpec.methodBuilder("forHeadGroup").
                        addModifiers(Modifier.PUBLIC, Modifier.STATIC).
                        addParameter(String.class, "headGroup").
                        addStatement(
                                CodeBlock.of(
                                        "return Arrays.asList(values()).stream().filter((lipidClass) -> {\n"
                                        + "    return lipidClass.matchesAbbreviation($N.trim());\n"
                                        + "}).findFirst()", "headGroup")).
                        returns(optionalLipidClass).
                        build()
        );
        //UNDEFINED(LipidCategory.UNDEFINED, "UNDEFINED", "Undefined lipid class"),
//        helloWorld.addEnumConstant("UNDEFINED", TypeSpec.anonymousClassBuilder("$N, $S, $S", "LipidCategory.UNDEFINED", "UNDEFINED", "Undefined lipid class").build());

        stream.forEach((lipidClassEntry) -> {
            String sanitizedName = sanitizeToEnumConstant(lipidClassEntry.getLipidName(), lipidClassEntry.getLipidCategory());
            LipidCategory category = LipidCategory.valueOf(lipidClassEntry.getLipidCategory());
            List<String> template = new ArrayList<>();
            template.addAll(
                    Arrays.asList(
                            "LipidCategory." + category.name(),
                            "\"" + lipidClassEntry.lipidDescription + "\"",
                            lipidClassEntry.maxNumFa + "",
                            "\"" + lipidClassEntry.allowedNumFa + "\"",
                            "\"" + lipidClassEntry.lipidName + "\""
                    )
            );
            template.addAll(lipidClassEntry.getSynonyms().stream().map((t) -> {
                return "\"" + t + "\"";
            }).collect(Collectors.toList()));
            CodeBlock cb = CodeBlock.of(String.join(", ", template));
            lipidClassBuilder.addEnumConstant(sanitizedName, TypeSpec.anonymousClassBuilder(cb).build());
        });

        return JavaFile.builder("de.isas.lipidomics.domain", lipidClassBuilder.build()).addFileComment(
                " Copyright 2019 nils.hoffmann.\n"
                + "\n"
                + " Licensed under the Apache License, Version 2.0 (the \"License\");\n"
                + " you may not use this file except in compliance with the License.\n"
                + " You may obtain a copy of the License at\n"
                + "\n"
                + "      http://www.apache.org/licenses/LICENSE-2.0\n"
                + "\n"
                + " Unless required by applicable law or agreed to in writing, software\n"
                + " distributed under the License is distributed on an \"AS IS\" BASIS,\n"
                + " WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
                + " See the License for the specific language governing permissions and\n"
                + " limitations under the License."
        ).build().toString();
    }
}

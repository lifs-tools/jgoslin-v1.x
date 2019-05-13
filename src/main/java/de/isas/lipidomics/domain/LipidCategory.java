/*
 * 
 */
package de.isas.lipidomics.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author nilshoffmann
 */
public enum LipidCategory {

    UNDEFINED("Undefined"),
    GL("Glycerolipid"),
    GP("Glycerophospholipid"),
    PL("Phospholipid"),
    SL("Sphingolipid"),
    ST("Sterollipid"),
    MEDIATOR("Mediator");

    private final String fullName;

    private LipidCategory(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public static LipidCategory forFullName(String fullName) {
        List<LipidCategory> matches = Arrays.asList(LipidCategory.values()).stream().filter((t) -> {
            return t.getFullName().equalsIgnoreCase(fullName);
        }).collect(Collectors.toList());
        if (matches.isEmpty()) {
            return LipidCategory.UNDEFINED;
        } else if (matches.size() > 1) {
            throw new RuntimeException("Query string " + fullName + " found more than once in enum values! Please check enum definition: fullName is compared case insensitive!");
        }
        return matches.get(0);
    }
}

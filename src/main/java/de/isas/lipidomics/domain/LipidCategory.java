/*
 * 
 */
package de.isas.lipidomics.domain;

/**
 *
 * @author nilshoffmann
 */
public enum LipidCategory {

    GL("Glycerolipid"),
    PL("Phospholipid"),
    SL("Sphingolipid"),
    CHOL("Cholesterol"),
    MEDIATOR("Mediator");

    private final String fullName;

    private LipidCategory(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return this.fullName;
    }
}

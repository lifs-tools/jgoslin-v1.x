/*
 * 
 */
package de.isas.lipidomics.domain;

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * An adduct, generally, consists of a sum formula part, an adduct string, the
 * charge and the charge sign. An example for a valid adduct is : [M+H]1+.
 *
 * @author nils.hoffmann
 */
@AllArgsConstructor
@Data
public class Adduct {

    private static final class None extends Adduct {

        private None() {
            super("", "", 0, 0);
        }
    }

    public static final Adduct NONE = new None();

    private String sumFormula;
    private String adductString;
    private Integer charge;
    private Integer chargeSign;

    public Adduct() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setChargeSign(Integer sign) {
        if (sign != -1 || sign != 0 || sign != 1) {
            throw new IllegalArgumentException("Sign can only be -1, 0, or 1");
        }
    }

    public String getLipidString() {
        if (adductString == null || adductString.isEmpty()) {
            return "";
        }
        if (charge == 0) {
            return "[M]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[M").append(sumFormula).append(adductString).append("]").append(charge).append((chargeSign > 0) ? "+" : "-");
        return sb.toString();
    }

    public ElementTable getElements() {
        ElementTable elements = new ElementTable();
        String adductName = Optional.ofNullable(adductString).map((t) -> {
            return t.length() > 1 ? t.substring(1) : "";
        }).orElse("");
        try {
            elements.accumulate(new ElementTable(adductName));
        } catch (ParsingException ex) {
            return elements;
        }
        if (adductString.length() > 0 && adductString.startsWith("-")) {
            elements.keySet().stream().forEach(elements::decrement);
        }
        return elements;
    }

    public int getCharge() {
        return charge * chargeSign;
    }

}

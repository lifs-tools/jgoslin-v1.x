/*
 * 
 */
package de.isas.lipidomics.domain;

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import java.util.Optional;
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

    private final String sumFormula;
    private final String adductString;
    private final Integer positiveElementaryCharge;
    private final Integer chargeSign;

    public Adduct() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getLipidString() {
        if (adductString == null || adductString.isEmpty()) {
            return "";
        }
        if (positiveElementaryCharge == 0) {
            return "[M]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[M").append(sumFormula).append(adductString).append("]").append(positiveElementaryCharge).append((chargeSign > 0) ? "+" : ((chargeSign < 0) ? "-" : ""));
        return sb.toString();
    }

    public ElementTable getElements() {
        ElementTable elements = new ElementTable();
        String adductName = Optional.ofNullable(adductString).map((t) -> {
            return t.length() > 1 ? t.substring(1) : "";
        }).orElse("");
        try {
            elements.add(new ElementTable(adductName));
        } catch (ParsingException ex) {
            return elements;
        }
        if (adductString.length() > 0 && adductString.startsWith("-")) {
            elements.keySet().stream().forEach((t) -> {
                elements.negate(t);
            });
        }
        return elements;
    }

    /**
     * Returns the positive elementary charge times the charge sign.
     *
     * @return the net charge.
     */
    public int getCharge() {
        return positiveElementaryCharge * chargeSign;
    }

}

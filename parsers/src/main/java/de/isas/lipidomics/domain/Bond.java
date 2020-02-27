/*
 * 
 */
package de.isas.lipidomics.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Defines a bond by its type and position.
 *
 * @author nils.hoffmann
 */
@AllArgsConstructor
@Data
public class Bond {

    public final static int NO_POSITION = -1;

    /**
     * The bond type, typical bond types in lipids are represented here.
     */
    public static enum TYPE {
        UNDETERMINED, SINGLE, DOUBLE, TRIPLE, IONIC
    };

    private TYPE type;
    private int position;
}

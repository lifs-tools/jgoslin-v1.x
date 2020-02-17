/*
 * 
 */
package de.isas.lipidomics.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author nilshoffmann
 */
@AllArgsConstructor
@Data
public class Bond {
    public final static int NO_POSITION = -1;
    public static enum TYPE {UNDETERMINED, SINGLE, DOUBLE, TRIPLE, IONIC};
    
    private TYPE type;
    private int position;
}

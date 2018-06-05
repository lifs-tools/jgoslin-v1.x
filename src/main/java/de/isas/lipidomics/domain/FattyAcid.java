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
public class FattyAcid {

    private String name;
    private int nCarbon;
    private int nDoubleBond;
    private int nHydroxy;

    public FattyAcid() {
        
    }

}

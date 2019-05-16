/*
 * 
 */
package de.isas.lipidomics.domain;

import lombok.Data;

/**
 *
 * @author nilshoffmann
 */
@Data
public abstract class FattyAcid {

    private final String name;
    private final int position;
    private final int nCarbon;
    private final int nHydroxy;
    
    public FattyAcid(String name, int position, int nCarbon, int nHydroxy) {
        this.name = name;
        if(nCarbon<1) {
            throw new IllegalArgumentException("FattyAcid must have at least 1 carbon!");
        }
        this.position = position;
        if(position < -1) {
            throw new IllegalArgumentException("FattyAcid position must be greater or equal to -1 (undefined) or greater or equal to 0 (0 = first position)!");
        }
        this.nCarbon = nCarbon;
        if(nHydroxy<0) {
            throw new IllegalArgumentException("FattyAcid must have at least 0 hydroxy groups!");
        }
        this.nHydroxy = nHydroxy;
    }

    public abstract int getNDoubleBonds();

}

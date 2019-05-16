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
public class MolecularFattyAcid extends FattyAcid {

    private final int nDoubleBonds;

    public MolecularFattyAcid(String name, int nCarbon, int nHydroxy, int nDoubleBonds) {
        this(name, -1, nCarbon, nHydroxy, nDoubleBonds);
    }
    
    protected MolecularFattyAcid(String name, int position, int nCarbon, int nHydroxy, int nDoubleBonds) {
        super(name, position, nCarbon, nHydroxy);
        if (nDoubleBonds < 0) {
            throw new IllegalArgumentException("MolecularFattyAcid must have at least 0 double bonds!");
        }
        this.nDoubleBonds = nDoubleBonds;
    }

}

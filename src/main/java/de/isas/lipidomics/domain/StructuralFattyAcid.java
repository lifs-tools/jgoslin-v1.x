/*
 * 
 */
package de.isas.lipidomics.domain;

import lombok.Data;

/**
 * A structural fatty acid has a determined position.
 * @author nilshoffmann
 */
@Data
public class StructuralFattyAcid extends MolecularFattyAcid {

    public StructuralFattyAcid(String name, int position, int nCarbon, int nHydroxy, int nDoubleBonds) {
        super(name, position, nCarbon, nHydroxy, nDoubleBonds);
    }
}

/*
 * 
 */
package de.isas.lipidomics.domain;

import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author nilshoffmann
 */
@Data
public class MolecularFattyAcid extends FattyAcid {

    private final int nDoubleBonds;

    @Builder(builderMethodName = "molecularFaBuilder")
    public MolecularFattyAcid(String name, int nCarbon, int nHydroxy, int nDoubleBonds, boolean ether) {
        this(name, -1, nCarbon, nHydroxy, nDoubleBonds, ether);
    }
    
    protected MolecularFattyAcid(String name, int position, int nCarbon, int nHydroxy, int nDoubleBonds, boolean ether) {
        super(name, position, nCarbon, nHydroxy, ether);
        if (nDoubleBonds < 0) {
            throw new ConstraintViolationException("MolecularFattyAcid must have at least 0 double bonds!");
        }
        this.nDoubleBonds = nDoubleBonds;
    }

}

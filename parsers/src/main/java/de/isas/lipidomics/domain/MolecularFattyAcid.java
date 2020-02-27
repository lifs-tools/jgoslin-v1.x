/*
 * 
 */
package de.isas.lipidomics.domain;

import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import lombok.Builder;
import lombok.Data;

/**
 * Part of a {@link LipidMolecularSubspecies}. This FA does not have a defined
 * SN position with regard to its head group.
 *
 * @author nils.hoffmann
 */
@Data
public class MolecularFattyAcid extends FattyAcid {

    private final int nDoubleBonds;

    @Builder(builderMethodName = "molecularFaBuilder")
    public MolecularFattyAcid(String name, int nCarbon, int nHydroxy, int nDoubleBonds, LipidFaBondType lipidFaBondType, boolean lcb) {
        this(name, -1, nCarbon, nHydroxy, nDoubleBonds, lipidFaBondType, lcb);
    }

    protected MolecularFattyAcid(String name, int position, int nCarbon, int nHydroxy, int nDoubleBonds, LipidFaBondType lipidFaBondType, boolean lcb) {
        super(name, position, nCarbon, nHydroxy, lipidFaBondType, lcb);
        if (nDoubleBonds < 0) {
            throw new ConstraintViolationException("MolecularFattyAcid must have at least 0 double bonds!");
        }
        this.nDoubleBonds = nDoubleBonds;
    }

}

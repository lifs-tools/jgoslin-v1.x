/*
 * 
 */
package de.isas.lipidomics.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Part of a {@link LipidStructuralSubspecies}. This FA has a defined SN
 * position with regard to its head group.
 *
 * @author nils.hoffmann
 */
@Data
public class StructuralFattyAcid extends MolecularFattyAcid {

    @Builder(builderMethodName = "structuralFaBuilder")
    public StructuralFattyAcid(String name, int position, int nCarbon, int nHydroxy, int nDoubleBonds, LipidFaBondType lipidFaBondType, boolean lcb) {
        super(name, position, nCarbon, nHydroxy, nDoubleBonds, lipidFaBondType, lcb);
    }
}

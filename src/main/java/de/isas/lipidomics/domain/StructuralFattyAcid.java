/*
 * 
 */
package de.isas.lipidomics.domain;

import lombok.Builder;
import lombok.Data;

/**
 * A structural fatty acid has a determined position.
 * @author nilshoffmann
 */
@Data
public class StructuralFattyAcid extends MolecularFattyAcid {

    @Builder(builderMethodName = "structuralFaBuilder")
    public StructuralFattyAcid(String name, int position, int nCarbon, int nHydroxy, int nDoubleBonds, LipidFaBondType lipidFaBondType, boolean lcb) {
        super(name, position, nCarbon, nHydroxy, nDoubleBonds, lipidFaBondType, lcb);
    }
}

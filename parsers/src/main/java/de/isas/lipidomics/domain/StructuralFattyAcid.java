/*
 * 
 */
package de.isas.lipidomics.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Part of a {@link LipidStructuralSubspecies}. This FA has a defined SN
 * position with regard to its head group.
 *
 * @author nils.hoffmann
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class StructuralFattyAcid extends MolecularFattyAcid {

    @Builder(builderMethodName = "structuralFattyAcidBuilder")
    public StructuralFattyAcid(String name, int position, int nCarbon, int nHydroxy, int nDoubleBonds, LipidFaBondType lipidFaBondType, boolean lcb, ModificationsList modifications) {
        super(name, position, nCarbon, nHydroxy, nDoubleBonds, lipidFaBondType, lcb, modifications);
    }

    @Override
    public String buildSubstructureName(LipidLevel level) {
        return super.buildSubstructureName(level);
    }
}

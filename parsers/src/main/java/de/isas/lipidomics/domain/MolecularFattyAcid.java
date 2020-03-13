/*
 * 
 */
package de.isas.lipidomics.domain;

import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Part of a {@link LipidMolecularSubspecies}. This FA does not have a defined
 * SN position with regard to its head group.
 *
 * @author nils.hoffmann
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MolecularFattyAcid extends FattyAcid {

    @Builder(builderMethodName = "molecularFattyAcidBuilder")
    public MolecularFattyAcid(String name, int nCarbon, int nHydroxy, int nDoubleBonds, LipidFaBondType lipidFaBondType, boolean lcb, ModificationsList modifications) {
        this(name, -1, nCarbon, nHydroxy, nDoubleBonds, lipidFaBondType, lcb, modifications);
    }

    protected MolecularFattyAcid(String name, int position, int nCarbon, int nHydroxy, int nDoubleBonds, LipidFaBondType lipidFaBondType, boolean lcb, ModificationsList modifications) {
        super(name, position, nCarbon, nHydroxy, nDoubleBonds, lipidFaBondType, lcb, modifications);
        if (nDoubleBonds < 0) {
            throw new ConstraintViolationException("MolecularFattyAcid must have at least 0 double bonds!");
        }
    }
    
    @Override
    public String buildSubstructureName(LipidLevel level) {
        StringBuilder sb = new StringBuilder();
        int nDB = 0;
        int nHydroxy = 0;
        int nCarbon = 0;
        nDB += getNDoubleBonds();
        nCarbon += getNCarbon();
        nHydroxy += getNHydroxy();
        sb.
            append(nCarbon).
            append(":").
            append(nDB).
            append((nHydroxy > 0 ? ";" + nHydroxy : "")).
            append(getLipidFaBondType().suffix());
        return sb.toString();
    }

}

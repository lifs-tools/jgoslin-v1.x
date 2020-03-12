/*
 * 
 */
package de.isas.lipidomics.domain;

import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import java.util.Optional;
import lombok.Data;

/**
 *
 * @author nils.hoffmann
 */
@Data
public abstract class FattyAcid {

    private final String name;
    private final int position;
    private final int nCarbon;
    private final int nHydroxy;
    private final int nDoubleBonds;
    private final LipidFaBondType lipidFaBondType;
    private final boolean lcb;
    private final ModificationsList modifications;

    /**
     * 
     * @param name
     * @param position
     * @param nCarbon
     * @param nHydroxy
     * @param nDoubleBonds
     * @param lipidFaBondType
     * @param lcb
     * @param modifications 
     */
    public FattyAcid(String name, int position, int nCarbon, int nHydroxy, int nDoubleBonds, LipidFaBondType lipidFaBondType, boolean lcb, ModificationsList modifications) {
        this.name = name;
        if (nCarbon < 0) {
            throw new ConstraintViolationException("FattyAcid must have at least 0 carbons!");
        }
        this.position = position;
        if (position < -1) {
            throw new ConstraintViolationException("FattyAcid position must be greater or equal to -1 (undefined) or greater or equal to 0 (0 = first position)!");
        }
        this.nCarbon = nCarbon;
        if (nHydroxy < 0) {
            throw new ConstraintViolationException("FattyAcid must have at least 0 hydroxy groups!");
        }
        this.nHydroxy = nHydroxy;
        this.nDoubleBonds = nDoubleBonds;
        this.lipidFaBondType = Optional.ofNullable(lipidFaBondType).orElse(LipidFaBondType.UNDEFINED);
        this.lcb = lcb;
        this.modifications = modifications==null?ModificationsList.NONE:modifications;
    }
    
    /**
     * Build the name of this substructure.
     * @return the name of this substructure.
     */
    public abstract String buildSubstructureName();

}

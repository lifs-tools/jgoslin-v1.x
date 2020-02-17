/*
 * 
 */
package de.isas.lipidomics.domain;

import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import java.util.Optional;
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
    private final LipidFaBondType lipidFaBondType;
    private final boolean lcb;

    public FattyAcid(String name, int position, int nCarbon, int nHydroxy, LipidFaBondType lipidFaBondType, boolean lcb) {
        this.name = name;
        if (nCarbon < 2) {
            throw new ConstraintViolationException("FattyAcid must have at least 2 carbons!");
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
        this.lipidFaBondType = Optional.ofNullable(lipidFaBondType).orElse(LipidFaBondType.UNDEFINED);
        this.lcb = lcb;
    }

    public abstract int getNDoubleBonds();

}

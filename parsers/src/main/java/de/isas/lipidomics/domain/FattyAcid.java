/*
 * 
 */
package de.isas.lipidomics.domain;

import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;

/**
 *
 * @author nils.hoffmann
 */
@Data
public class FattyAcid {

    private final FattyAcidType type;
    private final String name;
    private final int position;
    private final int nCarbon;
    private final int nHydroxy;
    private final int nDoubleBonds;
    private final LipidFaBondType lipidFaBondType;
    private final boolean lcb;
    private final ModificationsList modifications;
    private final Map<Integer, String> doubleBondPositions;

    /**
     *
     * @param name
     * @param position
     * @param nCarbon
     * @param nHydroxy
     * @param lipidFaBondType
     * @param lcb
     * @param modifications
     * @param doubleBondPositions
     */
    @Builder(builderMethodName = "isomericFattyAcidBuilder", builderClassName = "IsomericFattyAcidBuilder")
    public FattyAcid(String name, int position, int nCarbon, int nHydroxy, LipidFaBondType lipidFaBondType, boolean lcb, ModificationsList modifications, Map<Integer, String> doubleBondPositions) {
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
        this.lipidFaBondType = Optional.ofNullable(lipidFaBondType).orElse(LipidFaBondType.UNDEFINED);
        this.lcb = lcb;
        this.modifications = modifications == null ? ModificationsList.NONE : modifications;
        if (doubleBondPositions == null) {
            this.doubleBondPositions = Collections.emptyMap();
            this.nDoubleBonds = 0;
        } else {
            this.doubleBondPositions = new TreeMap<>();
            this.doubleBondPositions.putAll(doubleBondPositions);
            this.nDoubleBonds = this.doubleBondPositions.size();
        }
        this.type = FattyAcidType.ISOMERIC;
    }

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
    @Builder(builderMethodName = "structuralFattyAcidBuilder", builderClassName = "StructuralFattyAcidBuilder")
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
        if (nDoubleBonds < 0) {
            throw new ConstraintViolationException("FattyAcid must have at least 0 double bonds!");
        }
        this.nDoubleBonds = nDoubleBonds;
        this.lipidFaBondType = Optional.ofNullable(lipidFaBondType).orElse(LipidFaBondType.UNDEFINED);
        this.lcb = lcb;
        this.modifications = modifications == null ? ModificationsList.NONE : modifications;
        this.doubleBondPositions = Collections.emptyMap();
        this.type = FattyAcidType.STRUCTURAL;
    }

    /**
     *
     * @param name
     * @param nCarbon
     * @param nHydroxy
     * @param nDoubleBonds
     * @param lipidFaBondType
     * @param lcb
     * @param modifications
     */
    @Builder(builderMethodName = "molecularFattyAcidBuilder", builderClassName = "MolecularFattyAcidBuilder")
    public FattyAcid(String name, int nCarbon, int nHydroxy, int nDoubleBonds, LipidFaBondType lipidFaBondType, boolean lcb, ModificationsList modifications) {
        this.name = name;
        if (nCarbon < 0) {
            throw new ConstraintViolationException("FattyAcid must have at least 0 carbons!");
        }
        this.position = -1;
        if (position < -1) {
            throw new ConstraintViolationException("FattyAcid position must be greater or equal to -1 (undefined) or greater or equal to 0 (0 = first position)!");
        }
        this.nCarbon = nCarbon;
        if (nHydroxy < 0) {
            throw new ConstraintViolationException("FattyAcid must have at least 0 hydroxy groups!");
        }
        this.nHydroxy = nHydroxy;
        if (nDoubleBonds < 0) {
            throw new ConstraintViolationException("FattyAcid must have at least 0 double bonds!");
        }
        this.nDoubleBonds = nDoubleBonds;
        this.lipidFaBondType = Optional.ofNullable(lipidFaBondType).orElse(LipidFaBondType.UNDEFINED);
        this.lcb = lcb;
        this.modifications = modifications == null ? ModificationsList.NONE : modifications;
        this.doubleBondPositions = Collections.emptyMap();
        this.type = FattyAcidType.MOLECULAR;
    }

    /**
     * Build the name of this substructure.
     *
     * @param the structural lipid level to return this substructure's name on.
     * @return the name of this substructure.
     */
    public String buildSubstructureName(LipidLevel level) {
        StringBuilder sb = new StringBuilder();
        int nDB = 0;
        int nHydroxy = 0;
        int nCarbon = 0;
        nDB += getNDoubleBonds();
        StringBuilder dbPos = new StringBuilder();
        List<String> dbPositions = new LinkedList<>();
        for (Integer key : getDoubleBondPositions().keySet()) {
            dbPositions.add(key + getDoubleBondPositions().get(key));
        }
        if (!getDoubleBondPositions().isEmpty()) {
            dbPos.
                    append("(").
                    append(dbPositions.stream().collect(Collectors.joining(","))).
                    append(")");
        }

        nCarbon += getNCarbon();
        nHydroxy += getNHydroxy();
        sb.
                append(nCarbon).
                append(":").
                append(nDB).
                append(dbPos.toString()).
                append(nHydroxy > 0 ? ";" + nHydroxy : "").
                append(getLipidFaBondType().suffix());
        return sb.toString();
    }

}

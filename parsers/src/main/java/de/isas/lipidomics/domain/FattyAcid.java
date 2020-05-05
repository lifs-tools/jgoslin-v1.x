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
 * A fatty acid with a specific type. This object defines the name, position,
 * number of carbon atoms, hydroxyls and double bonds, as well as the bond type
 * to the head group. A FattyAcid can carry optional modifications and can
 * report double bond positions.
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
     * Create a new isomeric level FattyAcid.
     *
     * @param name the name, e.g. FA1 for the first FA.
     * @param position the sn position. -1 if undefined or unknown.
     * @param nCarbon the number of carbons in this FA.
     * @param nHydroxy the number of hydroxyls on this FA.
     * @param lipidFaBondType the bond type, e.g. ESTER.
     * @param lcb true if this is a long-chain base, e.g. in a Ceramide.
     * @param modifications optional modifications for this FA.
     * @param doubleBondPositions double bond positions in this FA.
     * @see LipidFaBondType
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
     * Create a new structural level FattyAcid.
     *
     * @param name the name, e.g. FA1 for the first FA.
     * @param position the sn position. -1 if undefined or unknown.
     * @param nCarbon the number of carbons in this FA.
     * @param nHydroxy the number of hydroxyls on this FA.
     * @param lipidFaBondType the bond type, e.g. ESTER.
     * @param lcb true if this is a long-chain base, e.g. in a Ceramide.
     * @param modifications optional modifications for this FA.
     * @see LipidFaBondType
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
     * Create a new molecular level FattyAcid.
     *
     * @param name the name, e.g. FA1 for the first FA.
     * @param nCarbon the number of carbons in this FA.
     * @param nHydroxy the number of hydroxyls on this FA.
     * @param lipidFaBondType the bond type, e.g. ESTER.
     * @param lcb true if this is a long-chain base, e.g. in a Ceramide.
     * @param modifications optional modifications for this FA.
     * @see LipidFaBondType
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
     * @param level the structural lipid level to return this substructure's
     * name on.
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
        if (!getModifications().isEmpty()) {
            sb.append("(");
            sb.append(getModifications().stream().map((t) -> {
                return (t.getLeft() == -1 ? "" : t.getLeft()) + "" + t.getRight();
            }).collect(Collectors.joining(",")));
            sb.append(")");
        }
        return sb.toString();
    }

    public ElementTable getElements() {
        ElementTable table = new ElementTable();
        if (!lcb) {
            if (nCarbon > 0 || nDoubleBonds > 0) {
                table.incrementBy(Element.ELEMENT_C, nCarbon);// C
                switch (lipidFaBondType) {
                    case ESTER:
                        table.incrementBy(Element.ELEMENT_H, 2 * nCarbon - 1 - 2 * nDoubleBonds); // H
                        table.incrementBy(Element.ELEMENT_O, 1 + nHydroxy); // O
                        break;
                    case ETHER_PLASMENYL:
                        table.incrementBy(Element.ELEMENT_H, 2 * nCarbon - 1 - 2 * nDoubleBonds + 2); // H
                        table.incrementBy(Element.ELEMENT_O, nHydroxy); // O
                        break;
                    case ETHER_PLASMANYL:
                        table.incrementBy(Element.ELEMENT_H, (nCarbon + 1) * 2 - 1 - 2 * nDoubleBonds); // H
                        table.incrementBy(Element.ELEMENT_O, nHydroxy); // O
                        break;
                    default:
                        throw new ConstraintViolationException("Mass cannot be computed for fatty acyl chain with bond type: " + lipidFaBondType);
                }
            }
        } else {
            // long chain base
            table.incrementBy(Element.ELEMENT_C, nCarbon); // C
            table.incrementBy(Element.ELEMENT_H, 2 * (nCarbon - nDoubleBonds) + 1); // H
            table.incrementBy(Element.ELEMENT_O, nHydroxy); // O
            table.incrementBy(Element.ELEMENT_N, 1); // N
        }
        return table;
    }

}

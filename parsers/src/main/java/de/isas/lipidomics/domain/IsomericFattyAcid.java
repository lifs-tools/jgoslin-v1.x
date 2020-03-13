/*
 * 
 */
package de.isas.lipidomics.domain;

import static de.isas.lipidomics.domain.LipidLevel.ISOMERIC_SUBSPECIES;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Part of a {@link LipidIsomericSubspecies}. An isomeric fatty acid has a
 * determined position, defined number of carbons, hydroxylations, bond type to
 * the head group, and its double bond positions defined.
 *
 * @author nils.hoffmann
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class IsomericFattyAcid extends StructuralFattyAcid {

    private final Map<Integer, String> doubleBondPositions = new TreeMap<>();

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
    @Builder(builderMethodName = "isomericFattyAcidBuilder")
    public IsomericFattyAcid(String name, int position, int nCarbon, int nHydroxy, LipidFaBondType lipidFaBondType, boolean lcb, ModificationsList modifications, Map<Integer, String> doubleBondPositions) {
        this(name, position, nCarbon, nHydroxy, doubleBondPositions == null ? 0 : doubleBondPositions.size(), lipidFaBondType, lcb, modifications);
        this.doubleBondPositions.putAll(doubleBondPositions);
    }

    protected IsomericFattyAcid(String name, int position, int nCarbon, int nHydroxy, int nDoubleBonds, LipidFaBondType lipidFaBondType, boolean lcb, ModificationsList modifications) {
        super(name, position, nCarbon, nHydroxy, nDoubleBonds, lipidFaBondType, lcb, modifications);
    }

    @Override
    public String buildSubstructureName(LipidLevel level) {
        StringBuilder sb = new StringBuilder();
        int nDB = 0;
        int nHydroxy = 0;
        int nCarbon = 0;
        nDB += getNDoubleBonds();
        StringBuilder dbPos = new StringBuilder();
        if (level == ISOMERIC_SUBSPECIES) {
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

/*
 * 
 */
package de.isas.lipidomics.domain;

import java.util.Map;
import java.util.TreeMap;
import lombok.Builder;
import lombok.Data;

/**
 * Part of a {@link LipidIsomericSubspecies}.
 * An isomeric fatty acid has a determined position, defined number of carbons, hydroxylations,
 * bond type to the head group, and its double bond positions defined.
 * @author nils.hoffmann
 */
@Data
public class IsomericFattyAcid extends StructuralFattyAcid {

    private final Map<Integer, String> doubleBondPositions = new TreeMap<>();
    
    @Builder(builderMethodName = "isomericFaBuilder")
    public IsomericFattyAcid(String name, int position, int nCarbon, int nHydroxy, LipidFaBondType lipidFaBondType, boolean lcb, Map<Integer, String> doubleBondPositions) {
        super(name, position, nCarbon, nHydroxy, doubleBondPositions.size(), lipidFaBondType, lcb);
        this.doubleBondPositions.putAll(doubleBondPositions);
    }
}

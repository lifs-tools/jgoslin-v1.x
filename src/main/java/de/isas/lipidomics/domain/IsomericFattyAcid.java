/*
 * 
 */
package de.isas.lipidomics.domain;

import java.util.Map;
import java.util.TreeMap;
import lombok.Data;

/**
 * A structural fatty acid has a determined position.
 * @author nilshoffmann
 */
@Data
public class IsomericFattyAcid extends StructuralFattyAcid {

    private final Map<Integer, String> doubleBondPositions = new TreeMap<Integer, String>();
    
    public IsomericFattyAcid(String name, int position, int nCarbon, int nHydroxy, LipidFaBondType lipidFaBondType, boolean lcb, Map<Integer, String> doubleBondPositions) {
        super(name, position, nCarbon, nHydroxy, doubleBondPositions.size(), lipidFaBondType, lcb);
        this.doubleBondPositions.putAll(doubleBondPositions);
    }
}

/*
 * 
 */
package de.isas.lipidomics.domain;

import java.util.LinkedList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author nilshoffmann
 */
@AllArgsConstructor
@Data
public class FattyAcid {

    private String name;
    private int nCarbon;
    private int nHydroxy;
    private List<Integer> doubleBondLocations = new LinkedList<>();

    public FattyAcid() {
    }

    public FattyAcid(String name, int nCarbon, int nDoubleBonds, int nHydroxy) {
        this.name = name;
        if(nCarbon<1) {
            throw new IllegalArgumentException("FattyAcid must have at least 1 carbon!");
        }
        this.nCarbon = nCarbon;
        if(nHydroxy<0) {
            throw new IllegalArgumentException("FattyAcid must have at least 0 hydroxy groups!");
        }
        this.nHydroxy = nHydroxy;
        if(nDoubleBonds<0) {
            throw new IllegalArgumentException("FattyAcid must have at least 0 double bonds!");
        }
        for (int i = 0; i < nDoubleBonds; i++) {
            this.doubleBondLocations.add(-1);
        }
    }

    public void addDoubleBonds(Integer nDoubleBonds) {
        for (int i = 0; i < nDoubleBonds; i++) {
            this.doubleBondLocations.add(-1);
        }
    }

    public void addDoubleBond() {
        this.doubleBondLocations.add(-1);
    }

    public void addDoubleBondWithLocation(Integer dbBondPosition) {
        this.doubleBondLocations.add(dbBondPosition);
    }

    public int getNDoubleBonds() {
        return this.doubleBondLocations.size();
    }

}

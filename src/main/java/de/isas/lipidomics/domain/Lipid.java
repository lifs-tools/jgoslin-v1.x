/*
 * 
 */
package de.isas.lipidomics.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Data;

/**
 *
 * @author nilshoffmann
 */
@Data
public class Lipid {

    private static final class None extends Lipid {

        private None() {
            super("");
        }
    }

    public static final Lipid NONE = new None();

    private final LipidCategory lipidCategory;
    private final Optional<LipidClass> lipidClass;
    private final String headGroup;
    private Map<String, FattyAcid> fa = new HashMap<>();

    public Lipid(String headGroup, LipidClass lipidClass, LipidCategory category) {
        this.headGroup = headGroup;
        this.lipidClass = Optional.of(lipidClass);
        this.lipidCategory = category;
    }
    
    public Lipid(String headGroup) {
        this.headGroup = headGroup;
        this.lipidClass = LipidClass.forHeadGroup(headGroup);
        this.lipidCategory = this.lipidClass.map((lipidClass) -> {
            return lipidClass.getCategory();
        }).orElse(LipidCategory.UNDEFINED);
    }

    public Lipid(String headGroup, FattyAcid... fa) {
        this(headGroup);
        for (FattyAcid fas : fa) {
            if (this.fa.containsKey(fas.getName())) {
                throw new IllegalArgumentException(
                        "FA names must be unique! FA with name " + fas.getName() + " was already added!");
            } else {
                this.fa.put(fas.getName(), fas);
            }
        }
    }

    public String getLipidSpecies() {
        int nDB = 0;
        int nHydroxy = 0;
        int nCarbon = 0;
        for (String faKey : fa.
                keySet()) {
            FattyAcid fattyAcid = fa.
                    get(faKey);
            nDB += fattyAcid.getNDoubleBonds();
            nCarbon += fattyAcid.getNCarbon();
            nHydroxy += fattyAcid.getNHydroxy();
        }
        return headGroup + " " + nCarbon + ":" + nDB + (nHydroxy > 0 ? ";" + nHydroxy : "");
    }

}

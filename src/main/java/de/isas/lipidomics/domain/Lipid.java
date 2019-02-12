/*
 * 
 */
package de.isas.lipidomics.domain;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author nilshoffmann
 */
@AllArgsConstructor
@Data
public class Lipid {

    private String category;
    private String headGroup;
    private Map<String, FattyAcid> fa = new HashMap<>();

    public Lipid() {

    }

    public Lipid(String category, String headGroup) {
        this.category = category;
        this.headGroup = headGroup;
    }

    public Lipid(String category, String headGroup, FattyAcid... fa) {
        this(category, headGroup);
        for (FattyAcid fas : fa) {
            if (this.fa.containsKey(fas.getName())) {
                throw new IllegalArgumentException(
                    "FA names must be unique! FA with name " + fas.getName() + " was already added!");
            } else {
                this.fa.put(fas.getName(), fas);
            }
        }
    }

}

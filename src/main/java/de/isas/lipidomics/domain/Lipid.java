/*
 * 
 */
package de.isas.lipidomics.domain;

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
    private Map<String, FattyAcid> fa;

    public Lipid() {
        
    }
    
    public Lipid(String category, String headGroup) {
        this.category = category;
        this.headGroup = headGroup;
    }


}

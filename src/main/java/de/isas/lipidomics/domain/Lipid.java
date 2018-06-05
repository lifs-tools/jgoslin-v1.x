/*
 * 
 */
package de.isas.lipidomics.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author nilshoffmann
 */
@AllArgsConstructor
@Data
public class Lipid {

    private String headGroup;
    private FattyAcid fa1;
    private FattyAcid fa2;

    public Lipid() {
        
    }
    


}

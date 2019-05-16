/*
 * 
 */
package de.isas.lipidomics.domain;

/**
 *
 * @author nilshoffmann
 */
public enum LipidLevel {
    /* Undefined / non-inferable lipid level */
    UNDEFINED,
    /* Mediators, Glycerolipids, Glycerophospholipids, Sphingolipids, Steroids, Prenols */
    CATEGORY, 
    /*
     Glyerophospholipids -> Glycerophosphoinositols (PI)
    */
    CLASS, 
    /*
    Phosphatidylinositol (16:0) or PI(16:0)
    */
    SPECIES, 
    /*
     Phosphatidylinositol (8:0-8:0) or PI(8:0-8:0)
    */
    MOLECULAR_SUBSPECIES,
    /*
     Phosphatidylinositol (8:0/8:0) or PI(8:0/8:0)
    */
    STRUCTURAL_SUBSPECIES, 
    /*
     1,2-dioctanoyl-sn-glycero-3-phospho-1D-myo-inositol
    
     PE(P-18:0/22:6(4Z,7Z,10Z,13Z,16Z,19Z))
     Phosphatidylethanolamine (P-18:0/22:6(4Z,7Z,10Z,13Z,16Z,19Z))
    */
    ISOMERIC_SUBSPECIES;
}

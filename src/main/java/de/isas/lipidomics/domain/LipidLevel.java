/*
 * 
 */
package de.isas.lipidomics.domain;

/**
 *
 * @author nilshoffmann
 */
public enum LipidLevel {
    /* Mediators, Glycerolipids, Glycerophospholipids, Sphingolipids, Steroids, Prenols */
    CATEGORY, 
    /*
     Sphingolipids -> Ceramides, Glyerophospholipids -> Glycerophosphoinositols (PI)
    */
    CLASS, 
    /*
    Ceramide (d14:0), Phosphatidylinositol (16:0) PI(16:0)
    */
    SPECIES, 
    /*
     Phosphatidylinositol (8:0-8:0) PI(8:0-8:0)
    */
    MOLECULAR_SUBSPECIES,
    /*
     Phosphatidylinositol (8:0/8:0) PI(8:0/8:0)
    */
    STRUCTURAL_SUBSPECIES, 
    /*
     1,2-dioctanoyl-sn-glycero-3-phospho-1D-myo-inositol
    */
    ISOMER;
}

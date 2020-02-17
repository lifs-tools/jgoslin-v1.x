/*
 * 
 */
package de.isas.lipidomics.domain;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 *
 * @author nilshoffmann
 */
@Data
public class LipidSpecies {

    private static final class None extends LipidSpecies {

        private None() {
            super("", Optional.of(LipidSpeciesInfo.NONE));
        }
    }

    public static final LipidSpecies NONE = new None();

    private final LipidCategory lipidCategory;
    private final Optional<LipidClass> lipidClass;
    private final String headGroup;
    @Setter(AccessLevel.NONE)
    protected Optional<LipidSpeciesInfo> info;

    public LipidSpecies(String headGroup) {
        this(headGroup, Optional.of(LipidSpeciesInfo.NONE));
    }

    public LipidSpecies(String headGroup, LipidCategory lipidCategory, Optional<LipidClass> lipidClass, Optional<LipidSpeciesInfo> lipidSpeciesInfo) {
        this.headGroup = headGroup.trim();
        this.lipidCategory = lipidCategory;
        this.lipidClass = lipidClass;
        this.info = lipidSpeciesInfo;
    }

    public LipidSpecies(String headGroup, Optional<LipidSpeciesInfo> lipidSpeciesInfo) {
        this.headGroup = headGroup.trim();
        this.lipidClass = LipidClass.forHeadGroup(this.headGroup);
        this.lipidCategory = this.lipidClass.map((lipidClass) -> {
            return lipidClass.getCategory();
        }).orElse(LipidCategory.UNDEFINED);
        this.info = lipidSpeciesInfo;
    }

    public Optional<LipidSpeciesInfo> getInfo() {
        return this.info;
    }

    public String getLipidString(LipidLevel level) {
        switch (level) {
            case CATEGORY:
                return this.lipidCategory.name();
            case CLASS:
                return this.lipidClass.orElse(LipidClass.UNDEFINED).name();
            case SPECIES:
                StringBuilder lipidString = new StringBuilder();
                lipidString.append(headGroup);
                if (this.info.isPresent() && this.info.get().getNCarbon() > 0) {
                    int nCarbon = info.get().getNCarbon();
                    String hgToFaSep = " ";
                    switch(headGroup) {
                        case "PC O":
                        case "LPC O":
                        case "PE O":
                        case "LPE O":
                            hgToFaSep = " O-";
                            break;
                    }
                    if(this.info.get().getLipidFaBondType()==LipidFaBondType.ETHER_PLASMANYL || this.info.get().getLipidFaBondType()==LipidFaBondType.ETHER_PLASMENYL) {
                        hgToFaSep = " O-";
                    }
                    lipidString.append(hgToFaSep).append(nCarbon);
                    int nDB = info.get().getNDoubleBonds();
                    lipidString.append(":").append(nDB);
                    int nHydroxy = info.get().getNHydroxy();
                    lipidString.append(nHydroxy > 0 ? ";" + nHydroxy : "");
                    lipidString.append(info.get().getLipidFaBondType().suffix());
                }
                return lipidString.toString();
            default:
                throw new RuntimeException(getClass().getSimpleName() + " does not know how to create a lipid string for level " + level);
        }

    }

}

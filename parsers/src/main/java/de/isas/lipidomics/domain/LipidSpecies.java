/*
 * 
 */
package de.isas.lipidomics.domain;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * A lipid species is the factual root of the object hierarchy. Lipid category
 * and class are used as taxonomic roots of a lipid species. Partial structural
 * knowledge, apart from the head group, is first encoded in the lipid species.
 *
 * A typical lipid species is
 * <a href="https://www.swisslipids.org/#/entity/SLM:000056493" target="_blank" title="PC 32:0">PC
 * 32:0</a>, where the head group is defined as PC (Glycerophosphocholines),
 * with fatty acyl chains of unknown individual composition, but known total
 * composition (32 carbon atoms, zero double bonds, no hydroxylations).
 *
 * @author nils.hoffmann
 * @see LipidCategory
 * @see LipidClass
 * @see LipidMolecularSubspecies
 * @see LipidStructuralSubspecies
 * @see LipidIsomericSubspecies
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

    /**
     * Create a lipid species using the provided head group and a lipid species
     * info {@link LipidSpeciesInfo#NONE}.
     *
     * @param headGroup the lipid species head group.
     */
    public LipidSpecies(String headGroup) {
        this(headGroup, Optional.of(LipidSpeciesInfo.NONE));
    }

    /**
     * Create a lipid species from head group, lipid category, an optional lipid
     * class and optional lipid species info.
     *
     * @param headGroup the lipid species head group.
     * @param lipidCategory the lipid category.
     * @param lipidClass the lipid class.
     * @param lipidSpeciesInfo the lipid species info.
     */
    public LipidSpecies(String headGroup, LipidCategory lipidCategory, Optional<LipidClass> lipidClass, Optional<LipidSpeciesInfo> lipidSpeciesInfo) {
        this.headGroup = headGroup.trim();
        this.lipidCategory = lipidCategory;
        this.lipidClass = lipidClass;
        this.info = lipidSpeciesInfo;
    }

    /**
     * Create a lipid species from a head group and an optional
     * {@link LipidSpeciesInfo}. This constructor will infer the lipid class
     * from the head group automatically. It then uses the lipid class to
     * retrieve the category of this lipid automatically, or sets the category
     * to {@link LipidCategory#UNDEFINED}. The lipid species info, which
     * contains details about the total no. of carbons in FA chains, no. of
     * double bonds etc., is used as provided.
     *
     * @param headGroup the lipid species head group.
     * @param lipidSpeciesInfo the lipid species info object.
     */
    public LipidSpecies(String headGroup, Optional<LipidSpeciesInfo> lipidSpeciesInfo) {
        this.headGroup = headGroup.trim();
        this.lipidClass = LipidClass.forHeadGroup(this.headGroup);
        this.lipidCategory = this.lipidClass.map((lipidClass) -> {
            return lipidClass.getCategory();
        }).orElse(LipidCategory.UNDEFINED);
        this.info = lipidSpeciesInfo;
    }

    /**
     * Returns the {@link LipidSpeciesInfo} for this lipid.
     *
     * @return the lipid species info.
     */
    public Optional<LipidSpeciesInfo> getInfo() {
        return this.info;
    }

    /**
     * Returns true, if the head group ends with ' O' or if the lipid fa bond
     * type is either {@link LipidFaBondType#ETHER_PLASMANYL} or
     * {@link LipidFaBondType#ETHER_PLASMENYL}.
     *
     * @return whether this is an 'ester' lipid, e.g. a Plasmanyl or Plasmenyl
     * species.
     */
    public boolean isEsterLipid() {
        return getHeadGroup().endsWith(" O") || this.info.get().getLipidFaBondType() == LipidFaBondType.ETHER_PLASMANYL || this.info.get().getLipidFaBondType() == LipidFaBondType.ETHER_PLASMENYL;
        //return this.info.get().getLipidFaBondType() == LipidFaBondType.ETHER_PLASMANYL || this.info.get().getLipidFaBondType() == LipidFaBondType.ETHER_PLASMENYL;
    }

    /**
     * Returns a lipid string representation for the given {@link LipidLevel},
     * e.g. Category, Species, etc. Please note that this method is overridden
     * by specific implementations for molecular, structural and isomeric
     * subspecies levels.
     *
     * @param level the lipid level to report the name of this lipid on.
     * @return the lipid name.
     */
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
                    if (isEsterLipid()) {
                        hgToFaSep = "-";
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

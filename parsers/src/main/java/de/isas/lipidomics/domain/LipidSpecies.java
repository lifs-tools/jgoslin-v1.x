/*
 * 
 */
package de.isas.lipidomics.domain;

import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * A lipid species is the factual root of the object hierarchy. Lipid category
 * and class are used as taxonomic roots of a lipid species. Partial structural
 * knowledge, apart from the head group, is first encoded in the lipid species.
 *
 * A typical lipid species is
 * PC 32:0 (SwissLipids SLM:000056493), where the head group is defined as PC (Glycerophosphocholines),
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
        this.headGroup = headGroup.trim().replaceAll(" O", "");
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
        this.headGroup = headGroup.trim().replaceAll(" O", "");
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
     * type is either {@link LipidFaBondType#ETHER_UNSPECIFIED},
     * {@link LipidFaBondType#ETHER_PLASMANYL} or
     * {@link LipidFaBondType#ETHER_PLASMENYL}.
     *
     * @return whether this is an 'ether' lipid, e.g. a unspecified ether
     * species, a Plasmanyl or Plasmenyl species.
     */
    public boolean isEtherLipid() {
        return this.info.get().getLipidFaBondType() == LipidFaBondType.ETHER_PLASMANYL
                || this.info.get().getLipidFaBondType() == LipidFaBondType.ETHER_PLASMENYL
                || this.info.get().getLipidFaBondType() == LipidFaBondType.ETHER_UNSPECIFIED
                || getFa().values().stream().anyMatch((t) -> {
                    return t.getLipidFaBondType() == LipidFaBondType.ETHER_UNSPECIFIED
                            || t.getLipidFaBondType() == LipidFaBondType.ETHER_PLASMANYL
                            || t.getLipidFaBondType() == LipidFaBondType.ETHER_PLASMENYL;
                });
    }

    /**
     * Returns a lipid string representation for the {@link LipidLevel}, e.g.
     * Category, Species, etc, as returned by {@link #getInfo()}.
     *
     * Will return the head group name if the level is
     * {@link LipidSpeciesInfo#NONE}.
     *
     * @return the lipid name for the native level.
     */
    public String getLipidString() {
        return getLipidString(getInfo().orElse(LipidSpeciesInfo.NONE).getLevel());
    }

    /**
     * Returns a lipid string representation for the given {@link LipidLevel},
     * e.g. Category, Species, etc. Please note that this method is overridden
     * by specific implementations for molecular, structural and isomeric
     * subspecies levels. This method does not normalize the head group.
     *
     * @param level the lipid level to report the name of this lipid on.
     * @return the lipid name.
     */
    public String getLipidString(LipidLevel level) {
        return this.buildLipidString(level, headGroup);
    }

    /**
     * Returns a lipid string representation for the given {@link LipidLevel},
     * e.g. Category, Species, etc. Please note that this method is overridden
     * by specific implementations for molecular, structural and isomeric
     * subspecies levels. This method normalizes the head group to the primary
     * class-specific synonym. E.g. TG would be normalized to TAG.
     *
     * @param level the lipid level to report the name of this lipid on.
     * @param normalizeHeadGroup if true, use class specific synonym for
     * headGroup, if false, use head group as parsed.
     * @return the lipid name.
     */
    public String getLipidString(LipidLevel level, boolean normalizeHeadGroup) {
        return this.buildLipidString(level, normalizeHeadGroup ? getNormalizedHeadGroup() : headGroup);
    }

    protected String buildLipidString(LipidLevel level, String headGroup) throws ConstraintViolationException {
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
                    if (isEtherLipid()) {
                        hgToFaSep = " O-";
                    }
                    lipidString.append(hgToFaSep).append(nCarbon);
                    int nDB = info.get().getNDoubleBonds();
                    lipidString.append(":").append(nDB);
                    int nHydroxy = info.get().getNHydroxy();
                    lipidString.append(nHydroxy > 0 ? ";" + nHydroxy : "");
                    lipidString.append(info.get().getLipidFaBondType().suffix());
                    if (!info.get().getModifications().isEmpty()) {
                        lipidString.append("(");
                        lipidString.append(info.get().getModifications().stream().map((t) -> {
                            return (t.getLeft() == -1 ? "" : t.getLeft()) + "" + t.getRight();
                        }).collect(Collectors.joining(",")));
                        lipidString.append(")");
                    }
                }
                return lipidString.toString();
            case UNDEFINED:
                return this.getHeadGroup();
            default:
                LipidLevel thisLevel = getInfo().orElse(LipidSpeciesInfo.NONE).getLevel();
                throw new ConstraintViolationException(getClass().getSimpleName() + " can not create a string for lipid with level " + thisLevel + " for level " + level + ": target level is more specific than this lipid's level!");
        }
    }

    /**
     * Returns a lipid string representation for the head group of this lipid.
     * This method normalizes the original head group name to the class specific
     * primary alias, if the level and class are known. E.g. TG is normalized to
     * TAG.
     *
     * @return the normalized lipid head group.
     */
    public String getNormalizedHeadGroup() {
        if (lipidClass.isPresent()) {
            return lipidClass.get().getSynonyms().get(0);
        }
        return headGroup;
    }

    /**
     * Returns a lipid string representation for the native {@link LipidLevel},
     * e.g. Category, Species, etc, as returned by {@link #getInfo()} of this
     * lipid. This method normalizes the head group to the primary
     * class-specific synonym. E.g. TG would be normalized to TAG.
     *
     * @return the normalized lipid name.
     */
    public String getNormalizedLipidString() {
        return getLipidString(getInfo().orElse(LipidSpeciesInfo.NONE).getLevel(), true);
    }

    /**
     * Validate this lipid against the class-specific available FA types and
     * slots.
     *
     * @return true if this lipid's FA types and their number match the class
     * definition, false otherwise.
     */
    public boolean validate() {
        return true;
    }

    /**
     * Returns the fatty acyls registered for this lipid.
     *
     * @return the fatty acyls.
     */
    public Map<String, FattyAcid> getFa() {
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return getLipidString(info.orElse(LipidSpeciesInfo.NONE).getLevel());
    }

}

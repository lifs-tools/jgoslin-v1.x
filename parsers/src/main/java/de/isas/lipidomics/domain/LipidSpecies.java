/*
 * 
 */
package de.isas.lipidomics.domain;

import static de.isas.lipidomics.domain.Element.ELEMENT_C;
import static de.isas.lipidomics.domain.Element.ELEMENT_H;
import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * A lipid species is the factual root of the object hierarchy. Lipid category
 * and class are used as taxonomic roots of a lipid species. Partial structural
 * knowledge, apart from the head group, is first encoded in the lipid species.
 *
 * A typical lipid species is PC 32:0 (SwissLipids SLM:000056493), where the
 * head group is defined as PC (Glycerophosphocholines), with fatty acyl chains
 * of unknown individual composition, but known total composition (32 carbon
 * atoms, zero double bonds, no hydroxylations).
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
            super(new HeadGroup(""), Optional.of(LipidSpeciesInfo.NONE));
        }
    }

    public static final LipidSpecies NONE = new None();
    private final HeadGroup headGroup;
    @Setter(AccessLevel.NONE)
    protected Optional<LipidSpeciesInfo> info;

    /**
     * Create a lipid species using the provided head group and a lipid species
     * info {@link LipidSpeciesInfo#NONE}.
     *
     * @param headGroup the lipid species head group.
     */
    public LipidSpecies(HeadGroup headGroup) {
        this(headGroup, Optional.of(LipidSpeciesInfo.NONE));
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
    public LipidSpecies(HeadGroup headGroup, Optional<LipidSpeciesInfo> lipidSpeciesInfo) {
        this.headGroup = headGroup;
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
        return this.info.orElse(LipidSpeciesInfo.NONE).getLipidFaBondType() == LipidFaBondType.ETHER_PLASMANYL
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
        return this.buildLipidString(level, headGroup.getName(), false);
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
        return this.buildLipidString(level, normalizeHeadGroup ? getNormalizedHeadGroup() : headGroup.getName(), normalizeHeadGroup);
    }

    protected StringBuilder buildSpeciesHeadGroupString(String headGroup, boolean normalizeHeadGroup) {
        StringBuilder lipidString = new StringBuilder();
        lipidString.append(this.headGroup.getLipidClass().map((lclass) -> {
            switch (lclass) {
                case SE:
                case SE_27_1:
                case SE_27_2:
                case SE_28_2:
                case SE_28_3:
                case SE_29_2:
                case SE_30_2:
                    return getNormalizedHeadGroup() + "/"; // use this for disambiguation to avoid SE 16:1 to be similar to SE 43:2 because of expansion to SE 27:1/16:1
            }
            return headGroup + " ";
        }).orElse(headGroup + " "));
        return lipidString;
    }

    protected String buildLipidString(LipidLevel level, String headGroup, boolean isNormalized) throws ConstraintViolationException {
        switch (level) {
            case CATEGORY:
                return this.headGroup.getLipidCategory().name();
            case CLASS:
                return this.headGroup.getLipidClass().orElse(LipidClass.UNDEFINED).name();
            case SPECIES:
                StringBuilder lipidString = new StringBuilder();
                lipidString.append(buildSpeciesHeadGroupString(headGroup, isNormalized));
                LipidSpeciesInfo info = this.info.orElse(LipidSpeciesInfo.NONE);
                if (info.getNCarbon() > 0) {
                    int nCarbon = info.getNCarbon();
                    String hgToFaSep = "";
                    if (isEtherLipid()) {
                        hgToFaSep = "O-";
                    }
                    lipidString.append(hgToFaSep).append(nCarbon);
                    int nDB = info.getNDoubleBonds();
                    lipidString.append(":").append(nDB);
                    int nHydroxy = info.getNHydroxy();
                    lipidString.append(nHydroxy > 0 ? ";" + nHydroxy : "");
                    lipidString.append(info.getLipidFaBondType().suffix());
                    if (!info.getModifications().isEmpty()) {
                        lipidString.append("(");
                        lipidString.append(info.getModifications().stream().map((t) -> {
                            return (t.getLeft() == -1 ? "" : t.getLeft()) + "" + t.getRight();
                        }).collect(Collectors.joining(",")));
                        lipidString.append(")");
                    }
                }
                return lipidString.toString().trim();
            case UNDEFINED:
                return this.headGroup.getName();
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
        return headGroup.getNormalizedName();
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

    /**
     * Returns the element count table for this lipid.
     *
     * @return the element count table.
     */
    public ElementTable getElements() {
        ElementTable elements = new ElementTable();
        if (info.isPresent()) {
            switch (info.get().getLevel()) {
                case CATEGORY:
                case CLASS:
                case UNDEFINED:
                    return elements;
            }
        }

        headGroup.getLipidClass().ifPresent((lclass) -> {
            elements.add(lclass.getElements());
        });

        info.ifPresent((t) -> {
            switch (t.getLevel()) {
                case MOLECULAR_SUBSPECIES:
                case STRUCTURAL_SUBSPECIES:
                case ISOMERIC_SUBSPECIES:
                    int nTrueFa = 0;
                    for (FattyAcid fa : getFa().values()) {
                        ElementTable faElements = fa.getElements();
                        if (fa.getNCarbon() != 0 || fa.getNDoubleBonds() != 0) {
                            nTrueFa += 1;
                        }
                        elements.add(faElements);
                    }
                    if (headGroup.getLipidClass().isPresent()) {
                        if (headGroup.getLipidClass().get().getMaxNumFa() < nTrueFa) {
                            throw new ConstraintViolationException("Inconsistency in number of fatty acyl chains for lipid '" + headGroup.getName() + "'. Expected at most: " + headGroup.getLipidClass().get().getMaxNumFa() + "; received: " + nTrueFa);
                        }
                        elements.incrementBy(Element.ELEMENT_H, headGroup.getLipidClass().get().getMaxNumFa() - nTrueFa); // adding hydrogens for absent fatty acyl chains
                    }
                    break;
                case SPECIES:
                    int maxNumFa = 0;
                    if (headGroup.getLipidClass().isPresent()) {
                        LipidClass lclass = headGroup.getLipidClass().get();
                        maxNumFa = lclass.getMaxNumFa();
                    }

                    if (info.isPresent()) {
                        int maxPossNumFa = headGroup.getLipidClass().get().getAllowedNumFa().stream().max(Integer::compareTo).orElse(0);
                        ElementTable faElements = info.get().getElements(maxPossNumFa);
                        elements.add(faElements);
                        elements.incrementBy(ELEMENT_H, maxNumFa - maxPossNumFa); // adding hydrogens for absent fatty acyl chains
                    }
                    break;
                default:
                    break;
            }
        });

        return elements;
    }

    public Optional<LipidClass> getLipidClass() {
        return headGroup.getLipidClass();
    }

    public LipidCategory getLipidCategory() {
        return headGroup.getLipidCategory();
    }

    @Override
    public String toString() {
        return getLipidString(info.orElse(LipidSpeciesInfo.NONE).getLevel());
    }

}

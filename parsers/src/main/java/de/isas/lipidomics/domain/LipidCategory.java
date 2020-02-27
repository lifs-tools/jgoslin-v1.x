/*
 * 
 */
package de.isas.lipidomics.domain;

import de.isas.lipidomics.palinom.exceptions.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The lipid category nomenclature follows the shorthand notation of
 * <pre>Liebisch, G., Vizcaíno,
 * J.A., Köfeler, H., Trötzmüller, M., Griffiths, W.J., Schmitz, G., Spener, F.,
 * and Wakelam, M.J.O. (2013). Shorthand notation for lipid structures derived
 * from mass spectrometry. J. Lipid Res. 54, 1523–1530.</pre>
 *
 * We use the associations to either LipidMAPS or SwissLipids (Saccharolipids),
 * where appropriate.
 * 
 * Example: Category=Glyerophospholipids (GP)
 *
 * @author nils.hoffmann
 */
public enum LipidCategory {

    UNDEFINED("Undefined lipid category"),
    /* SLM:000117142 Glycerolipids */
    GL("Glycerolipid"),
    /* SLM:000001193 Glycerophospholipids */
    GP("Glycerophospholipid"),
    /* SLM:000000525 Sphingolipids */
    SP("Sphingolipid"),
    /* SLM:000500463 Steroids and derivatives */
    ST("Sterollipid"),
    /* SLM:000390054 Fatty acyls and derivatives */
    FA("Fattyacyls"),
    /* Swiss lipids*/
    SL("Saccharolipids");

    private final String fullName;

    private LipidCategory(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public static LipidCategory forFullName(String fullName) {
        List<LipidCategory> matches = Arrays.asList(LipidCategory.values()).stream().filter((t) -> {
            return t.getFullName().equalsIgnoreCase(fullName);
        }).collect(Collectors.toList());
        if (matches.isEmpty()) {
            return LipidCategory.UNDEFINED;
        } else if (matches.size() > 1) {
            throw new ConstraintViolationException("Query string " + fullName + " found more than once in enum values! Please check enum definition: fullName is compared case insensitive!");
        }
        return matches.get(0);
    }
}

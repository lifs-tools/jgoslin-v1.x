/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.domain.MolecularFattyAcid;
import de.isas.lipidomics.domain.LipidSpecies;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;
import de.isas.lipidomics.domain.LipidCategory;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidSpeciesInfo;
import org.junit.Assert;

/**
 *
 * @author nilshoffmann
 */
public class Sphingolipids2FAStepDefs {

    private String lipidname;
    private LipidSpecies lipid;

    @Given("the lipid molecular sub species name is {string}")
    public void the_lipid_molecular_sub_species_name_is(String string) {
        this.lipidname = string;
    }

    @When("I parse {string}")
    public void i_parse(String string) throws ParsingException {
        PaLiNomVisitorParser parser = new PaLiNomVisitorParser();
        this.lipid = parser.parse(string).getLipid();
    }

    @Then(
        "I should get a lipid of category {string} and species {string} {string} headgroup, FA{int} with {int} carbon atoms, {int} double bonds and {int} hydroxy groups, as well as FA{int} with {int} carbon atoms, {int} double bonds and {int} hydroxy groups.")
    public void i_should_get_a_lipid_of_category_species_headgroup_FA_with_carbon_atoms_double_bonds_and_hydroxy_groups_as_well_as_FA_with_carbon_atoms_double_bonds_and_hydroxy_groups(
        String category,
        String species,
        String headgroup,
        Integer fa1,
        Integer fa1_c,
        Integer fa1_db,
        Integer fa1_hydroxy,
        Integer fa2,
        Integer fa2_c,
        Integer fa2_db,
        Integer fa2_hydroxy) {

        LipidMolecularSubspecies referenceLipid = new LipidMolecularSubspecies(headgroup,
            new MolecularFattyAcid("FA" + fa1, fa1_c, fa1_hydroxy, fa1_db),
            new MolecularFattyAcid("FA" + fa2, fa2_c, fa2_hydroxy, fa2_db));
        Assert.assertEquals(referenceLipid, lipid);
        Assert.assertEquals(LipidCategory.valueOf(category), lipid.getLipidCategory());
        Assert.assertEquals(species, lipid.getLipidString());
    }

    protected String toString(LipidMolecularSubspecies referenceLipid) {
        return referenceLipid.getHeadGroup() + " " + referenceLipid.getFa().
            get("FA1").
            getNCarbon() + referenceLipid.getFa().
                get("FA2").
                getNCarbon() + ":" + referenceLipid.getFa().
                get("FA1").
                getNDoubleBonds() + referenceLipid.getFa().
                get("FA2").
                getNDoubleBonds() + ";" + referenceLipid.getFa().
                get("FA1").
                getNHydroxy() + referenceLipid.getFa().
                get("FA2").
                getNHydroxy();
    }
}

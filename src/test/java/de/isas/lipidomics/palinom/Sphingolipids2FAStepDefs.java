/*
 * 
 */
package de.isas.lipidomics.palinom;

import cucumber.api.PendingException;
import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.Lipid;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;
import de.isas.lipidomics.domain.LipidCategory;
import org.junit.Assert;

/**
 *
 * @author nilshoffmann
 */
public class Sphingolipids2FAStepDefs {

    private String lipidname;
    private Lipid lipid;

    @Given("the lipid sub species name is {string}")
    public void the_lipid_sub_species_name_is(String string) {
        this.lipidname = string;
    }

    @When("I parse {string}")
    public void i_parse(String string) throws ParsingException {
        GoslinVisitorParser parser = new GoslinVisitorParser();
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

        Lipid referenceLipid = new Lipid(headgroup,
            new FattyAcid("FA" + fa1, fa1_c, fa1_db, fa1_hydroxy),
            new FattyAcid("FA" + fa2, fa2_c, fa2_db, fa2_hydroxy));
        Assert.assertEquals(referenceLipid, lipid);
        Assert.assertEquals(LipidCategory.valueOf(category), lipid.getLipidCategory());
        Assert.assertEquals(species, lipid.getLipidSpecies());
    }

    protected String toString(Lipid referenceLipid) {
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

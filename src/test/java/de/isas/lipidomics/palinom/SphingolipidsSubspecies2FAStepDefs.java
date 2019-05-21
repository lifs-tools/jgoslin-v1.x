/*
 * 
 */
package de.isas.lipidomics.palinom;

import de.isas.lipidomics.palinom.exceptions.ParsingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;
import de.isas.lipidomics.domain.FattyAcid;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;

/**
 *
 * @author nilshoffmann
 */
@Slf4j
public class SphingolipidsSubspecies2FAStepDefs {

    private String lipidname;
    private LipidMolecularSubspecies lipid;

    @Given("the lipid molecular sub species name is {string}")
    public void the_lipid_molecular_sub_species_name_is(String string) {
        this.lipidname = string;
    }

    @When("I parse {string}")
    public void i_parse(String string) {
        PaLiNomVisitorParser parser = new PaLiNomVisitorParser();
        try {
            this.lipid = (LipidMolecularSubspecies) parser.parse(string).getLipid();
        } catch (ParsingException pe) {
            log.error("Caught parsing exception: ", pe);
        }
    }

    @Then("I should get a lipid of category {string} and species {string} {string} headgroup")
    public void i_should_get_a_lipid_of_category_and_species_headgroup(String category,
                        String species,
                        String headgroup) {
        Assert.assertEquals(category, this.lipid.getLipidCategory().name());
        Assert.assertEquals(species, this.lipid.getLipidString(LipidLevel.SPECIES));
        Assert.assertEquals(headgroup, this.lipid.getHeadGroup());
    }

    @Then("the first fatty acid at position {int} with {int} carbon atoms, {int} double bonds and {int} hydroxy groups")
        public void the_first_fatty_acid_FA_with_carbon_atoms_double_bonds_and_hydroxy_groups(Integer position, Integer nCarbon, Integer nDoubleBonds, Integer nHydroxy) {
        FattyAcid fa1 = this.lipid.getFa().get("FA1");
        Assert.assertEquals(position.intValue(), fa1.getPosition());
        Assert.assertEquals(nCarbon.intValue(), fa1.getNCarbon());
        Assert.assertEquals(nDoubleBonds.intValue(), fa1.getNDoubleBonds());
        Assert.assertEquals(nHydroxy.intValue(), fa1.getNHydroxy());
    }

    @Then("the second fatty acid at position {int} with {int} carbon atoms, {int} double bonds and {int} hydroxy groups.")
        public void the_second_fatty_acid_FA_with_carbon_atoms_double_bonds_and_hydroxy_groups(Integer position, Integer nCarbon, Integer nDoubleBonds, Integer nHydroxy) {
        FattyAcid fa2 = this.lipid.getFa().get("FA2");
        Assert.assertEquals(position.intValue(), fa2.getPosition());
        Assert.assertEquals(nCarbon.intValue(), fa2.getNCarbon());
        Assert.assertEquals(nDoubleBonds.intValue(), fa2.getNDoubleBonds());
        Assert.assertEquals(nHydroxy.intValue(), fa2.getNHydroxy());
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

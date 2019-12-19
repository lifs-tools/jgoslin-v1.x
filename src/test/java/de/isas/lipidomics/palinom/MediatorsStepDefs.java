/*
 * 
 */
package de.isas.lipidomics.palinom;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.isas.lipidomics.domain.LipidLevel;
import de.isas.lipidomics.domain.LipidMolecularSubspecies;
import de.isas.lipidomics.domain.LipidStructuralSubspecies;
import de.isas.lipidomics.palinom.exceptions.ParsingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;

/**
 *
 * @author nilshoffmann
 */
@Slf4j
public class MediatorsStepDefs {

    private String lipidname;
    private LipidStructuralSubspecies lipid;

    @Given("the lipid structural sub species name is {string}")
    public void the_lipid_structural_sub_species_name_is(String string) {
        this.lipidname = string;
    }
    
    @When("I parse {string}")
    public void i_parse(String string) {
        GoslinVisitorParser parser = new GoslinVisitorParser();
        try {
            this.lipid = (LipidStructuralSubspecies) parser.parse(string).getLipid();
        } catch (ParsingException pe) {
            log.error("Caught parsing exception: ", pe);
        }
    }
    

    @Then("I should get a lipid of category {string} and species {string} {string} headgroup.")
    public void i_should_get_a_lipid_of_category_and_species_headgroup(String category, String species, String headgroup) {
        Assert.assertEquals(category, this.lipid.getLipidCategory().name());
        Assert.assertEquals(species, this.lipid.getLipidString(LipidLevel.SPECIES));
        Assert.assertEquals(headgroup, this.lipid.getHeadGroup());
    }
}

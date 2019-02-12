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
import org.junit.Assert;

/**
 *
 * @author nilshoffmann
 */
public class SphingolipidsStepDefs {

    private String lipidname;
    private Lipid lipid;

    @Given("the lipid sub species name is {string}")
    public void the_lipid_sub_species_name_is(String string) {
        this.lipidname = string;
    }

    @When("I parse {string}")
    public void i_parse(String string) {
        PaLiNomLexer lexer = new PaLiNomLexer(CharStreams.fromString(
            string));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PaLiNomParser parser = new PaLiNomParser(tokens);
        PaLiNomParser.LipidIdentifierContext context = parser.
            lipidIdentifier();
        PaLiNomVisitor visitor = new PaLiNomVisitor();
        this.lipid = visitor.visit(context);
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

        Lipid referenceLipid = new Lipid(category, headgroup,
            new FattyAcid("FA" + fa1, fa1_c, fa1_db, fa1_hydroxy),
            new FattyAcid("FA" + fa2, fa2_c, fa2_db, fa2_hydroxy));
        Assert.assertEquals(referenceLipid, lipid);
        Assert.assertEquals(category, lipid.getCategory());
        Assert.assertEquals(species, toSpeciesString(lipid));
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

    protected String toSpeciesString(Lipid referenceLipid) {
        int nDB = 0;
        int nHydroxy = 0;
        int nCarbon = 0;
        for (String faKey : referenceLipid.getFa().
            keySet()) {
            FattyAcid fa = referenceLipid.getFa().
                get(faKey);
            nDB += fa.getNDoubleBonds();
            nCarbon += fa.getNCarbon();
            nHydroxy += fa.getNHydroxy();
        }
        return referenceLipid.getHeadGroup() + " " + nCarbon + ":" + nDB + (nHydroxy > 0 ? ";" + nHydroxy : "");
    }
}

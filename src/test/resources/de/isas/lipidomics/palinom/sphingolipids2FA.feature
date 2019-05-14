Feature: Is this a valid Sphingolipid name?
  Everybody wants a valid Sphingolipid name

  Scenario Outline: SP Sphingolipid with two fatty acid chains, zero or more double bonds, and zero or more hydroxy groups
    Given the lipid sub species name is <lipid_sub_species>
    When I parse <lipid_sub_species>
    Then I should get a lipid of category <lipid_category> and species <lipid_species> <headgroup> headgroup, FA1 with <fa1_n_carb> carbon atoms, <fa1_n_db> double bonds and <fa1_n_hydroxy> hydroxy groups, as well as FA2 with <fa2_n_carb> carbon atoms, <fa2_n_db> double bonds and <fa2_n_hydroxy> hydroxy groups.

    Examples:
      | lipid_sub_species  | lipid_category |  lipid_species | headgroup | fa1_n_carb | fa1_n_db | fa1_n_hydroxy | fa2_n_carb | fa2_n_db | fa2_n_hydroxy |
      | "PE 18:3;1-16:2"   | "GP"           |    "PE 34:5;1" |      "PE" |         18 |        3 |             1 |         16 |        2 |             0 |
      | "PE 18:2;1-16:2"   | "GP"           |    "PE 34:4;1" |      "PE" |         18 |        2 |             1 |         16 |        2 |             0 |
      | "PE 18:2-16:2"     | "GP"           |    "PE 34:4"   |      "PE" |         18 |        2 |             0 |         16 |        2 |             0 |
      | "PC 16:2-16:1"     | "GP"           |    "PC 32:3"   |      "PC" |         16 |        2 |             0 |         16 |        1 |             0 |

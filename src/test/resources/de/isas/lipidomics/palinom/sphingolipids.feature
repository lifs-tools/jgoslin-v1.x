Feature: Is this a valid Sphingolipid name?
  Everybody wants a valid Sphingolipid name

  Scenario Outline: PL Sphingolipid with two fatty acid chains, zero or more double bonds, and zero or more hydroxy groups
    Given the lipid sub species name is <lipid_sub_species>
    When I parse <lipid_sub_species>
    Then I should get a lipid of species <lipid_species> <headgroup> headgroup, FA1 with <fa1_n_carb> carbon atoms, <fa1_n_db> double bonds and <fa1_n_hydroxy> hydroxy groups, as well as FA2 with <fa2_n_carb> carbon atoms, <fa2_n_db> double bonds and <fa2_n_hydroxy> hydroxy groups.

    Examples:
      | lipid_sub_species  |  lipid_species | headgroup | fa1_n_carb | fa1_n_db | fa1_n_hydroxy | fa2_n_carb | fa2_n_db | fa2_n_hydroxy |
      | "PE 18:3;1_16:2"   |    "PE 34:5;1" |      "PE" |         18 |        3 |             1 |         16 |        2 |             0 |
      | "PE 18:2;1_16:2"   |    "PE 34:4;1" |      "PE" |         18 |        2 |             1 |         16 |        2 |             0 |
      | "PE 18:2_16:2"     |    "PE 34:4;0" |      "PE" |         18 |        2 |             0 |         16 |        2 |             0 |
      | "PC 16:2_16:1"     |    "PC 32:3;0" |      "PC" |         16 |        2 |             0 |         16 |        1 |             0 |
